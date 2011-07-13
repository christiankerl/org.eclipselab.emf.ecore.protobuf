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
package org.eclipselab.emf.ecore.protobuf.internal.conversion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipselab.emf.ecore.protobuf.conversion.Converter;
import org.eclipselab.emf.ecore.protobuf.conversion.ConverterRegistry;
import org.eclipselab.emf.ecore.protobuf.conversion.ToProtoBufMessageConverter;
import org.eclipselab.emf.ecore.protobuf.mapping.NamingStrategy;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;


/**
 * DynamicToProtoBufMessageConverter provides a {@link ToProtoBufMessageConverter} implementation
 * based on ProtoBuf's {@link DynamicMessage}.
 * 
 * @author Christian Kerl
 */
public class DynamicToProtoBufMessageConverter extends ToProtoBufMessageConverter<EObject, DynamicMessage> implements Converter.WithRegistry
{
  private final class ObjectConversion
  {
    private final EClass sourceType;
    private final EObject source;
    private final Descriptor targetType;
    private final DynamicMessage.Builder target;

    public ObjectConversion(EClass sourceType, EObject source, Descriptor targetType, DynamicMessage.Builder target)
    {
      super();
      this.sourceType = sourceType;
      this.source = source;
      this.targetType = targetType;
      this.target = target;
    }

    public DynamicMessage run()
    {
      createInternalIdField();
      createAttributeFields();
      createReferenceFields();

      return target.build();
    }

    private void createInternalIdField()
    {
      set(field(naming.getInternalIdField()), pool.getId(source));
    }

    private void createAttributeFields()
    {
      for (EAttribute attr : sourceType.getEAllAttributes())
      {
        if (!source.eIsSet(attr))
          continue;

        EDataType attrType = attr.getEAttributeType();
        FieldDescriptor field = field(attr.getName());

        Converter<Object, EDataType, Object, FieldDescriptor> converter = registry.find(attrType, field);

        Object attrRawValue = source.eGet(attr);
        Object fieldValue;

        if (attr.isMany())
        {
          @SuppressWarnings("unchecked")
          List<Object> attrValues = (List<Object>)attrRawValue;
          List<Object> fieldValues = new ArrayList<Object>();

          int size = attrValues.size();
          
          for(int idx = 0; idx < size; idx++)
          {            
            fieldValues.add(converter.convert(attrType, attrValues.get(idx), field));
          }

          fieldValue = fieldValues;
        }
        else
        {
          fieldValue = converter.convert(attrType, attrRawValue, field);
        }

        set(field, fieldValue);
      }
    }

    private void createReferenceFields()
    {
      List<EReference> refs = sourceType.getEAllReferences();
      EReference ref;
      
      int refSize = refs.size();
      
      for(int refIdx = 0; refIdx < refSize; refIdx++)
      {
        ref = refs.get(refIdx);
        
        if (!source.eIsSet(ref))
          continue;

        FieldDescriptor field = field(ref.getName());

        Object refRawValue = source.eGet(ref);
        Object fieldValue;

        if (ref.isMany())
        {
          EObject refValue;
          @SuppressWarnings("unchecked")
          List<EObject> refValues = (List<EObject>)refRawValue;
          List<DynamicMessage> fieldValues = new ArrayList<DynamicMessage>();
          
          int size = refValues.size();
          
          for(int refValueIdx = 0; refValueIdx < size; refValueIdx++)
          {
            refValue = refValues.get(refValueIdx);
            fieldValues.add(createReference(ref.getEReferenceType(), refValue.eClass(), refValue, field.getMessageType(), ref.isContainment()));
          }

          fieldValue = fieldValues;
        }
        else
        {
          EObject refValue = (EObject)refRawValue;

          fieldValue = createReference(ref.getEReferenceType(), refValue.eClass(), refValue, field.getMessageType(), ref.isContainment());
        }

        set(field, fieldValue);
      }
    }

    /**
     * @param refType The type of the {@link EReference} ({@link EReference#getEReferenceType()})
     * @param refSourceType The type of the actual object stored in the reference
     * @param refSource The actual object stored in the reference
     * @param refTargetType The *.Ref ProtoBuf message type
     * @param containment Whether the {@link EReference} is a containment ({@link EReference#isContainment()})
     * @return
     */
    private DynamicMessage createReference(
      EClass refType,
      EClass refSourceType,
      EObject refSource,
      Descriptors.Descriptor refTargetType,
      boolean containment)
    {      
      DynamicMessage.Builder refTarget = DynamicMessage.newBuilder(refTargetType);
      // lookup message defining extension for 'refTargetType'
      Descriptors.Descriptor refTargetExtension = getMappingContext().lookup(refSourceType);
      // lookup extension field
      FieldDescriptor refTargetField = refTargetExtension.findFieldByName(naming.getRefMessageExtensionField(refType, refSourceType));
      
      DynamicMessage refTargetFieldValue;
      
      if (containment)
      {
        refTargetFieldValue = convert(refSource, refTargetField.getMessageType());
      }
      else
      {
        refTargetFieldValue = DynamicMessage.newBuilder(refTargetExtension)
          .setField(refTargetExtension.findFieldByName(naming.getInternalIdField()), pool.getId(refSource))
          .build();
      }
      
      refTarget.setField(refTargetField, refTargetFieldValue);
      
      return refTarget.build();
    }

    private void set(FieldDescriptor field, Object value)
    {
      target.setField(field, value);
    }

    private FieldDescriptor field(String fieldName)
    {
      return targetType.findFieldByName(fieldName);
    }
  }

  private ConverterRegistry registry;
  private final NamingStrategy naming;

  public DynamicToProtoBufMessageConverter(NamingStrategy naming)
  {
    super();
    this.naming = naming;
  }

  @Override
  public void setRegistry(ConverterRegistry registry)
  {
    this.registry = registry;
  }
  
  @Override
  protected Descriptor getTargetType(EClass sourceType)
  {
    return getMappingContext().lookup(sourceType);
  }
  
  /* (non-Javadoc)
   * @see org.eclipselab.emf.ecore.protobuf.converter.Converter.ToProtoBufMessageConverter#supports(org.eclipse.emf.ecore.EClass)
   */
  @Override
  public boolean supports(EClass sourceType)
  {
    return true;
  }
  
  /* (non-Javadoc)
   * @see org.eclipselab.emf.ecore.protobuf.converter.Converter#supports(java.lang.Object, java.lang.Object)
   */
  @Override
  public boolean supports(EClass sourceType, Descriptor targetType)
  {
    return true;
  }

  /* (non-Javadoc)
   * @see org.eclipselab.emf.ecore.protobuf.converter.Converter#convert(java.lang.Object, java.lang.Object, java.lang.Object)
   */
  @Override
  public DynamicMessage convert(EClass sourceType, EObject source, Descriptor targetType)
  {
    return new ObjectConversion(sourceType, source, targetType, DynamicMessage.newBuilder(targetType)).run();
  }
}
