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
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipselab.emf.ecore.protobuf.mapping.BasicEClassifierMapperImpl;
import org.eclipselab.emf.ecore.protobuf.mapping.EClassifierMapper;
import org.eclipselab.emf.ecore.protobuf.mapping.MappingException;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto.Builder;

/**
 * EcoreEDataTypeMapperImpl provides an {@link EClassifierMapper} implementation for
 * mapping {@link EDataType}s defined in {@link EcorePackage} to their {@link FieldDescriptorProto.Type}
 * representation.
 * 
 * @author Christian Kerl
 */
public class EcoreEDataTypeMapperImpl extends BasicEClassifierMapperImpl<EDataType>
{
  @Override
  public boolean supports(EClassifier eClassifier)
  {
    return eClassifier instanceof EDataType && EcorePackage.eINSTANCE == eClassifier.getEPackage();
  }

  @Override
  protected void doMap(EDataType eDataType, Builder context)
  {
  }

  @Override
  protected void doMapReference(EDataType eDataType, FieldDescriptorProto.Builder pbField)
  {
    switch (eDataType.getClassifierID())
    {
      case EcorePackage.EBOOLEAN:
      case EcorePackage.EBOOLEAN_OBJECT:
        pbField.setType(FieldDescriptorProto.Type.TYPE_BOOL);
        break;
      case EcorePackage.ECHAR:
      case EcorePackage.ECHARACTER_OBJECT:
      case EcorePackage.EBYTE:
      case EcorePackage.EBYTE_OBJECT:
      case EcorePackage.ESHORT:
      case EcorePackage.ESHORT_OBJECT:
      case EcorePackage.EINT:
      case EcorePackage.EINTEGER_OBJECT:
        pbField.setType(FieldDescriptorProto.Type.TYPE_SINT32);
        break;
      case EcorePackage.ELONG:
      case EcorePackage.ELONG_OBJECT:
        pbField.setType(FieldDescriptorProto.Type.TYPE_SINT64);
        break;
      case EcorePackage.EFLOAT:
      case EcorePackage.EFLOAT_OBJECT:
        pbField.setType(FieldDescriptorProto.Type.TYPE_FLOAT);
        break;
      case EcorePackage.EDOUBLE:
      case EcorePackage.EDOUBLE_OBJECT:
        pbField.setType(FieldDescriptorProto.Type.TYPE_DOUBLE);
        break;
      case EcorePackage.EBYTE_ARRAY:
        pbField.setType(FieldDescriptorProto.Type.TYPE_BYTES);
        break;
      case EcorePackage.ESTRING:
        pbField.setType(FieldDescriptorProto.Type.TYPE_STRING);
        break;
      case EcorePackage.EDATE:
        pbField.setType(FieldDescriptorProto.Type.TYPE_INT64);
        break;
      default:
        throw MappingException.causeMissingMapper(eDataType);
    }    
  }
}
