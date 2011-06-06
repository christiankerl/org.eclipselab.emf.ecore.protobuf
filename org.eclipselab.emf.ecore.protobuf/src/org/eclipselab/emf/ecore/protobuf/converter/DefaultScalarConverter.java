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

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;


/**
 * DefaultScalarConverter provides {@link Converter.FromProtoBufScalarConverter} and
 * {@link Converter.ToProtoBufScalarConverter} implementations for arbitrary
 * {@link EDataType} definitions based on {@link EcoreUtil#createFromString(EDataType, String)} and
 * {@link EcoreUtil#convertToString(EDataType, Object)}. 
 * 
 * @author Christian Kerl
 */
public class DefaultScalarConverter
{

  public static class FromProtoBuf extends Converter.FromProtoBufScalarConverter
  {

    @Override
    public boolean supports(FieldDescriptor sourceType, EDataType targetType)
    {
      return sourceType.getType() == Type.STRING;
    }

    @Override
    public Object convert(FieldDescriptor sourceType, Object source, EDataType targetType)
    {
      return EcoreUtil.createFromString(targetType, (String)source);
    }
  }

  public static class ToProtoBuf extends Converter.ToProtoBufScalarConverter
  {

    @Override
    public boolean supports(EDataType sourceType, FieldDescriptor targetType)
    {
      return targetType.getType() == Type.STRING;
    }

    @Override
    public Object convert(EDataType sourceType, Object source, FieldDescriptor targetType)
    {
      return EcoreUtil.convertToString(sourceType, source);
    }
  }
}
