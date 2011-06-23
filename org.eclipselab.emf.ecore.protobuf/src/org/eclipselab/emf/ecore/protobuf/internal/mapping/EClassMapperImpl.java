/**
 * <copyright>
 *
 * Copyright (c) 2011 Christian Kerl
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Christian Kerl - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipselab.emf.ecore.protobuf.internal.mapping;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipselab.emf.ecore.protobuf.mapping.BasicEClassifierMapperImpl;
import org.eclipselab.emf.ecore.protobuf.mapping.EClassifierMapper;
import org.eclipselab.emf.ecore.protobuf.mapping.MapperRegistry;
import org.eclipselab.emf.ecore.protobuf.mapping.NamingStrategy;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;

/**
 * EClassMapperImpl provides an {@link EClassifierMapper} implementation for
 * mapping {@link EClass} to {@link DescriptorProto}.
 * 
 * @author Christian Kerl
 */
public class EClassMapperImpl extends BasicEClassifierMapperImpl<EClass>
{
  private static class MappingData extends AdapterImpl
  {
    public static final int EXTENSION_START = 1;
    
    public static MappingData get(EClass eClass)
    {
      MappingData data = (MappingData)EcoreUtil.getExistingAdapter(eClass, MappingData.class);
      
      if(data == null)
      {
        data = new MappingData();
        
        eClass.eAdapters().add(data);
      }
      
      return data;
    }

    private int ext = EXTENSION_START;
    
    @Override
    public boolean isAdapterForType(Object type)
    {
      return MappingData.class.equals(type);
    }
    
    public int getNextRefMessageExtensionNumber()
    {
      return ext++;
    }
  }
  
  private final MapperRegistry registry;
  private final NamingStrategy naming;
  
  public EClassMapperImpl(MapperRegistry registry, NamingStrategy naming)
  {
    super();
    this.registry = registry;
    this.naming = naming;
  }

  @Override
  public boolean supports(EClassifier eClassifier)
  {
    return eClassifier instanceof EClass;
  }

  @Override
  protected void doMap(EClass eClass, FileDescriptorProto.Builder pbPackage)
  {
    DescriptorProto.Builder pbMessage = pbPackage.addMessageTypeBuilder();
    pbMessage.setName(naming.getMessage(eClass));
    
    createRefMessage(pbMessage);
    
    if(!eClass.isAbstract())
    {
      createRefMessageExtensions(eClass, pbMessage);
      createInternalIdField(pbMessage);
      createFields(eClass, pbMessage);
    }
  }

  @Override
  protected void doMapReference(EClass eClass, FieldDescriptorProto.Builder pbField)
  {
    pbField.setType(FieldDescriptorProto.Type.TYPE_MESSAGE);
    pbField.setTypeName(naming.getQualifiedRefMessage(eClass));
  }
  
  private void createRefMessage(DescriptorProto.Builder pbMessage)
  {
    DescriptorProto.Builder pbRefMessage = pbMessage.addNestedTypeBuilder()
      .setName(naming.getRefMessage());
    
    pbRefMessage.addExtensionRangeBuilder()
        .setStart(MappingData.EXTENSION_START)
        .setEnd(Integer.MAX_VALUE);
  }
  
  private void createRefMessageExtensions(EClass eClass, DescriptorProto.Builder pbMessage)
  {
    createRefMessageExtension(eClass, eClass, pbMessage);
    
    for(EClass eSuperClass : eClass.getEAllSuperTypes())
    {
      createRefMessageExtension(eSuperClass, eClass, pbMessage);
    }
  }
  
  private void createRefMessageExtension(EClass eSuperClass, EClass eClass, DescriptorProto.Builder pbMessage)
  {
    pbMessage.addExtensionBuilder()
      .setExtendee(naming.getQualifiedRefMessage(eSuperClass))
      .setLabel(FieldDescriptorProto.Label.LABEL_OPTIONAL)
      .setType(FieldDescriptorProto.Type.TYPE_MESSAGE)
      .setTypeName(pbMessage.getName())
      .setName(naming.getRefMessageExtensionField(eSuperClass, eClass))
      .setNumber(MappingData.get(eSuperClass).getNextRefMessageExtensionNumber());
  }
  
  private void createInternalIdField(DescriptorProto.Builder pbMessage)
  {
    pbMessage.addFieldBuilder()
      .setLabel(FieldDescriptorProto.Label.LABEL_REQUIRED)
      .setType(FieldDescriptorProto.Type.TYPE_INT32)
      .setName(naming.getInternalIdField())
      .setNumber(pbMessage.getFieldCount());
  }
  
  private void createFields(EClass eClass, DescriptorProto.Builder pbMessage)
  {
    for(EStructuralFeature eFeature : eClass.getEAllStructuralFeatures())
    {
      if(shouldIgnoreFeature(eFeature)) continue;
      
      FieldDescriptorProto.Builder pbField = pbMessage.addFieldBuilder()
        .setLabel(getFieldLabel(eFeature))
        .setName(eFeature.getName())
        .setNumber(pbMessage.getFieldCount());
      
      registry
        .find(eFeature.getEType())
        .mapReference(eFeature.getEType(), pbField);
    }
  }
  
  private boolean shouldIgnoreFeature(EStructuralFeature feature)
  {
    return feature.isTransient();
  }

  private FieldDescriptorProto.Label getFieldLabel(EStructuralFeature feature)
  {
    if (feature.isMany())
    {
      return FieldDescriptorProto.Label.LABEL_REPEATED;
    }
    else if (feature.isRequired())
    {
      return FieldDescriptorProto.Label.LABEL_REQUIRED;
    }
    else
    {
      return FieldDescriptorProto.Label.LABEL_OPTIONAL;
    }
  }
}
