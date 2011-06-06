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
package org.eclipselab.emf.ecore.protobuf.internal;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipselab.emf.ecore.protobuf.EDataTypeMapper;
import org.eclipselab.emf.ecore.protobuf.TypeMappingResult;

import com.google.protobuf.DescriptorProtos;


/**
 * {@link EDataTypeMapper} implementation for {@link EDataType}s defined in {@link EcorePackage}.
 * 
 * @author Christian Kerl
 */
public class EcoreEDataTypeMapper implements EDataTypeMapper
{

  /* (non-Javadoc)
   * @see org.eclipselab.emf.ecore.protobuf.EDataTypeMapper#supports(org.eclipse.emf.ecore.EDataType)
   */
  public boolean supports(EDataType type)
  {
    return EcorePackage.eINSTANCE == type.getEPackage();
  }

  /* (non-Javadoc)
   * @see org.eclipselab.emf.ecore.protobuf.EDataTypeMapper#map(org.eclipse.emf.ecore.EDataType)
   */
  public TypeMappingResult map(EDataType type)
  {
    switch (type.getClassifierID())
    {
      case EcorePackage.EBOOLEAN:
      case EcorePackage.EBOOLEAN_OBJECT:
        return TypeMappingResult.forDataType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL);
      case EcorePackage.ECHAR:
      case EcorePackage.ECHARACTER_OBJECT:
      case EcorePackage.EBYTE:
      case EcorePackage.EBYTE_OBJECT:
      case EcorePackage.ESHORT:
      case EcorePackage.ESHORT_OBJECT:
      case EcorePackage.EINT:
      case EcorePackage.EINTEGER_OBJECT:
        return TypeMappingResult.forDataType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT32);
      case EcorePackage.ELONG:
      case EcorePackage.ELONG_OBJECT:
        return TypeMappingResult.forDataType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT64);
      case EcorePackage.EFLOAT:
      case EcorePackage.EFLOAT_OBJECT:
        return TypeMappingResult.forDataType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT);
      case EcorePackage.EDOUBLE:
      case EcorePackage.EDOUBLE_OBJECT:
        return TypeMappingResult.forDataType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE);
      case EcorePackage.EBYTE_ARRAY:
        return TypeMappingResult.forDataType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BYTES);
      case EcorePackage.ESTRING:
        return TypeMappingResult.forDataType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING);
      case EcorePackage.EDATE:
        return TypeMappingResult.forDataType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64);
      default:
        throw new IllegalArgumentException();
    }
  }
}
