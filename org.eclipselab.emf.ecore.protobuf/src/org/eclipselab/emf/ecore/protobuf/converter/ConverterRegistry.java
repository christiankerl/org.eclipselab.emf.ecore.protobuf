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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipselab.emf.ecore.protobuf.internal.conversion.DefaultScalarConverter;
import org.eclipselab.emf.ecore.protobuf.internal.conversion.EcoreScalarConverter;
import org.eclipselab.emf.ecore.protobuf.internal.conversion.EnumConverter;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;


public class ConverterRegistry
{

  private List<ToProtoBufScalarConverter> toProtoBufScalarConverters = Arrays.asList(
    new EnumConverter.ToProtoBuf(),
    new EcoreScalarConverter.ToProtoBuf(),
    new DefaultScalarConverter.ToProtoBuf());

  private List<FromProtoBufScalarConverter> fromProtoBufScalarConverters = Arrays.asList(
    new EnumConverter.FromProtoBuf(),
    new EcoreScalarConverter.FromProtoBuf(),
    new DefaultScalarConverter.FromProtoBuf());
  
  private List<ToProtoBufMessageConverter< ? extends EObject, ? extends Message>> toProtobufMessageConverters = new ArrayList<ToProtoBufMessageConverter< ? extends EObject, ? extends Message>>();
  
  private List<FromProtoBufMessageConverter<? extends Message, ? extends EObject>> fromProtobufMessageConverters = new ArrayList<FromProtoBufMessageConverter<? extends Message, ? extends EObject>>();
  
  public void register(FromProtoBufMessageConverter<? extends Message, ? extends EObject> fromProtobufMessageConverter)
  {
    fromProtobufMessageConverters.add(0, fromProtobufMessageConverter);
    
    if(fromProtobufMessageConverter instanceof Converter.WithRegistry)
    {
      ((Converter.WithRegistry) fromProtobufMessageConverter).setRegistry(this);
    }
  }

  public void register(ToProtoBufMessageConverter<? extends EObject, ? extends Message> toProtobufMessageConverter)
  {
    toProtobufMessageConverters.add(0, toProtobufMessageConverter);
    
    if(toProtobufMessageConverter instanceof Converter.WithRegistry)
    {
      ((Converter.WithRegistry) toProtobufMessageConverter).setRegistry(this);
    }
  }
  
  public ToProtoBufScalarConverter find(EDataType sourceType, Descriptors.FieldDescriptor targetType)
  {
    for (ToProtoBufScalarConverter converter : toProtoBufScalarConverters)
    {
      if (converter.supports(sourceType, targetType))
      {
        return converter;
      }
    }

    throw new IllegalArgumentException();
  }

  public FromProtoBufScalarConverter find(Descriptors.FieldDescriptor sourceType, EDataType targetType)
  {
    for (FromProtoBufScalarConverter converter : fromProtoBufScalarConverters)
    {
      if (converter.supports(sourceType, targetType))
      {
        return converter;
      }
    }

    throw new IllegalArgumentException();
  }
  
  public FromProtoBufMessageConverter<? extends Message, ? extends EObject> find(Descriptors.Descriptor sourceType)
  {
    for(FromProtoBufMessageConverter<? extends Message, ? extends EObject> converter : fromProtobufMessageConverters)
    {
      if(converter.supports(sourceType))
      {
        return converter;
      }
    }
    
    throw new IllegalArgumentException();
  }
  
  public FromProtoBufMessageConverter<? extends Message, ? extends EObject> find(Descriptors.Descriptor sourceType, EClass targetType)
  {
    for(FromProtoBufMessageConverter<? extends Message, ? extends EObject> converter : fromProtobufMessageConverters)
    {
      if(converter.supports(sourceType, targetType))
      {
        return converter;
      }
    }
    
    throw new IllegalArgumentException();
  }
  
  public ToProtoBufMessageConverter<? extends EObject, ? extends Message> find(EClass sourceType)
  {
    for(ToProtoBufMessageConverter<? extends EObject, ? extends Message> converter : toProtobufMessageConverters)
    {
      if(converter.supports(sourceType))
      {
        return converter;
      }
    }
    
    throw new IllegalArgumentException();
  }
}
