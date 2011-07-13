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

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipselab.emf.ecore.protobuf.mapping.EClassifierMapper;
import org.eclipselab.emf.ecore.protobuf.mapping.EPackageMapper;
import org.eclipselab.emf.ecore.protobuf.mapping.MapperRegistry;
import org.eclipselab.emf.ecore.protobuf.util.EcoreUtil2;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.ExtensionRegistry;

/**
 * EPackageMapperImpl provides the default {@link EPackageMapper} implementation
 * using a {@link MapperRegistry} to find {@link EClassifierMapper}s for the
 * {@link EPackage#getEClassifiers()}.
 * 
 * @author Christian Kerl
 */
public class EPackageMapperImpl implements EPackageMapper
{
  private final MapperRegistry registry;
  
  public EPackageMapperImpl(MapperRegistry registry)
  {
    super();
    this.registry = registry;
  }

  @Override
  public boolean supports(EPackage ePackage)
  {
    return true;
  }

  @Override
  public void map(EPackage ePackage, FileDescriptorSet.Builder pbPackages)
  {
    FileDescriptorProto.Builder pbPackage = pbPackages.addFileBuilder();
    pbPackage.setPackage(ePackage.getName());
    
    for(EClassifier eClassifier : EcoreUtil2.getAllClassifiersFromPackageHierarchy(ePackage))
    {
      registry
        .find(eClassifier)
        .map(eClassifier, pbPackage);
    }
  }

  @Override
  public void registerExtensions(Descriptors.FileDescriptor pbPackage, ExtensionRegistry extensionRegistry)
  {
    for(Descriptors.Descriptor pbMessage : pbPackage.getMessageTypes())
    {
      for(Descriptors.FieldDescriptor pbExtension : pbMessage.getExtensions())
      {
        extensionRegistry.add(pbExtension, DynamicMessage.getDefaultInstance(pbMessage));
      }
    }
  }
}
