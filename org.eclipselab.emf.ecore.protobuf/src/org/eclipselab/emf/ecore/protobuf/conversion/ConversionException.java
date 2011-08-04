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
package org.eclipselab.emf.ecore.protobuf.conversion;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;

public class ConversionException extends RuntimeException
{
  private static final long serialVersionUID = 3123192576333858453L;

  public ConversionException(Throwable cause)
  {
    super(cause);
  }

  public ConversionException(String message)
  {
    super(message);
  }

  public static ConversionException causeMissingConverter(EClass sourceType, Descriptors.Descriptor targetType)
  {
    return causeMissingConverter(sourceType.getName(), targetType.getFullName());
  }

  public static ConversionException causeMissingConverter(Descriptors.Descriptor sourceType, EClass targetType)
  {
    return causeMissingConverter(sourceType.getFullName(), targetType.getName());
  }

  public static ConversionException causeMissingConverter(EDataType sourceType, FieldDescriptor targetType)
  {
    return causeMissingConverter(sourceType.getName(), targetType.getType().name());
  }

  public static ConversionException causeMissingConverter(FieldDescriptor sourceType, EDataType targetType)
  {
    return causeMissingConverter(sourceType.getType().name(), targetType.getName());
  }
  
  private static ConversionException causeMissingConverter(String from, String to)
  {
    return new ConversionException(String.format("Missing converter from '%s' to '%s'!", from, to));
  }
  
  public static ConversionException causeMissingConverter(EClassifier eClassifier)
  {
    return causeMissingConverter(eClassifier.getName());
  }

  public static ConversionException causeMissingConverter(Descriptor sourceType)
  {
    return causeMissingConverter(sourceType.getFullName());
  }

  private static ConversionException causeMissingConverter(String name)
  {
    return new ConversionException(String.format("Missing converter for '%s'!", name));
  }

  public static ConversionException causeUnexpectedField(String expected, String actual)
  {
    return new ConversionException(String.format("Expected field '%s', but got field '%s'!", expected, actual));
  }
}