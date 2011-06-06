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
package org.eclipselab.emf.ecore.protobuf.converter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipselab.emf.ecore.protobuf.EObjectPool;
import org.eclipselab.emf.ecore.protobuf.mapper.NamingStrategy;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;


/**
 * DynamicFromProtoBufMessageConverter provides a {@link FromProtoBufMessageConverter} implementation
 * based on ProtoBuf's {@link DynamicMessage}.
 * 
 * @author Christian Kerl
 */
public class DynamicFromProtoBufMessageConverter extends Converter.FromProtoBufMessageConverter<DynamicMessage, EObject>
{
  private final class ObjectConversion
  {
    private final DynamicMessage source;
    private final EClass targetType;
    private EObject target;

    public ObjectConversion(DynamicMessage source, EClass targetType)
    {
      super();
      this.source = source;
      this.targetType = targetType;
    }

    public EObject run()
    {
      Iterator<Map.Entry<FieldDescriptor, Object>> it = source.getAllFields().entrySet().iterator();

      Map.Entry<FieldDescriptor, Object> firstFieldAndValue = it.next();

      if (!firstFieldAndValue.getKey().getName().equals(naming.getInternalIdField()))
      {
        throw new IllegalArgumentException();
      }

      target = pool.getObject(targetType, (Integer)firstFieldAndValue.getValue());

      while (it.hasNext())
      {
        Map.Entry<Descriptors.FieldDescriptor, Object> fieldAndValue = it.next();
        EStructuralFeature feature = feature(fieldAndValue.getKey());

        if (feature instanceof EAttribute)
        {
          createAttribute((EAttribute)feature, fieldAndValue.getKey(), fieldAndValue.getValue());
        }
        else
        {
          createReference((EReference)feature, fieldAndValue.getKey(), fieldAndValue.getValue());
        }
      }

      return target;
    }

    @SuppressWarnings("unchecked")
    private void createAttribute(EAttribute attr, FieldDescriptor field, Object fieldRawValue)
    {
      EDataType attrType = attr.getEAttributeType();

      Converter<Object, FieldDescriptor, Object, EDataType> converter = registry.find(field, attrType);

      if (attr.isMany())
      {
        Collection<Object> fieldValues = (Collection<Object>)fieldRawValue;
        List<Object> attrValues = (List<Object>)target.eGet(attr);

        for (Object fieldValue : fieldValues)
        {
          attrValues.add(converter.convert(field, fieldValue, attrType));
        }
      }
      else
      {
        target.eSet(attr, converter.convert(field, fieldRawValue, attrType));
      }
    }

    @SuppressWarnings("unchecked")
    private void createReference(EReference ref, FieldDescriptor field, Object fieldRawValue)
    {
      if (ref.isMany())
      {
        Collection<DynamicMessage> fieldValues = (Collection<DynamicMessage>)fieldRawValue;
        List<EObject> refValues = (List<EObject>)target.eGet(ref);

        for (DynamicMessage fieldValue : fieldValues)
        {
          refValues.add(resolveReference(fieldValue.getDescriptorForType(), fieldValue));
        }
      }
      else
      {
        DynamicMessage fieldValue = (DynamicMessage)fieldRawValue;

        target.eSet(ref, resolveReference(fieldValue.getDescriptorForType(), fieldValue));
      }
    }

    private EObject resolveReference(Descriptor refSourceType, DynamicMessage refSource)
    {
      String refTargetTypeName = ((Descriptors.EnumValueDescriptor)refSource.getField(refSourceType.findFieldByName(naming.getSubTypeField()))).getName();

      EClass refTargetType = (EClass)target.eClass().getEPackage().getEClassifier(refTargetTypeName);
      EObject refTarget;

      FieldDescriptor idField = refSourceType.findFieldByName(naming.getInternalIdField());

      if (refSource.hasField(idField))
      {
        refTarget = pool.getObject(refTargetType, (Integer)refSource.getField(idField));
      }
      else
      {
        refTarget = convert(
          (DynamicMessage)refSource.getField(refSourceType.findFieldByName(naming.getRefMessageField(refTargetType))),
          refTargetType);
      }

      return refTarget;
    }

    private EStructuralFeature feature(FieldDescriptor field)
    {
      return targetType.getEStructuralFeature(field.getName());
    }
  }

  private final EObjectPool pool;
  private final ConverterRegistry registry;
  private final NamingStrategy naming;

  public DynamicFromProtoBufMessageConverter(EObjectPool pool, ConverterRegistry registry, NamingStrategy naming)
  {
    super();
    this.pool = pool;
    this.registry = registry;
    this.naming = naming;
  }

  /* (non-Javadoc)
   * @see org.eclipselab.emf.ecore.protobuf.converter.Converter#supports(java.lang.Object, java.lang.Object)
   */
  @Override
  public boolean supports(Descriptor sourceType, EClass targetType)
  {
    return true;
  }

  /* (non-Javadoc)
   * @see org.eclipselab.emf.ecore.protobuf.converter.Converter#convert(java.lang.Object, java.lang.Object, java.lang.Object)
   */
  @Override
  public EObject convert(Descriptor sourceType, DynamicMessage source, EClass targetType)
  {
    return new ObjectConversion(source, targetType).run();
  }
}
