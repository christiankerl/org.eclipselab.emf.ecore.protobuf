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

import java.util.Date;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipselab.emf.ecore.protobuf.conversion.FromProtoBufScalarConverter;
import org.eclipselab.emf.ecore.protobuf.conversion.ToProtoBufScalarConverter;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.FieldDescriptor;


/**
 * EcoreScalarConverter provides {@link FromProtoBufScalarConverter} and
 * {@link ToProtoBufScalarConverter} implementations for {@link EDataType}s
 * defined in the {@link EcorePackage}.
 * 
 * @author Christian Kerl
 */
public class EcoreScalarConverter
{

  public static class FromProtoBuf extends FromProtoBufScalarConverter
  {

    @Override
    public boolean supports(FieldDescriptor sourceType, EDataType targetType)
    {
      return targetType.getEPackage() == EcorePackage.eINSTANCE;
    }

    @Override
    public Object convert(FieldDescriptor sourceType, Object source, EDataType targetType)
    {
      switch (targetType.getClassifierID())
      {
        case EcorePackage.EBYTE_ARRAY:
          return ((ByteString)source).toByteArray();
        case EcorePackage.EDATE:
          return new Date((Long)source);
        default:
          return source;
      }
    }
  }

  public static class ToProtoBuf extends ToProtoBufScalarConverter
  {

    @Override
    public boolean supports(EDataType sourceType, FieldDescriptor targetType)
    {
      return sourceType.getEPackage() == EcorePackage.eINSTANCE;
    }

    @Override
    public Object convert(EDataType sourceType, Object source, FieldDescriptor targetType)
    {
      switch (sourceType.getClassifierID())
      {
        case EcorePackage.EBYTE_ARRAY:
          return ByteString.copyFrom((byte[])source);
        case EcorePackage.EDATE:
          return ((Date)source).getTime();
        default:
          return source;
      }
    }
  }
}
