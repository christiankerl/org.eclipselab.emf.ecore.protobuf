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
import org.eclipse.emf.ecore.EObject;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

/**
 * A FromProtoBufMessageConverter converts from a ProtoBuf {@link Message} to
 * an Ecore {@link EObject}.
 * 
 * @author Christian Kerl
 */
public abstract class FromProtoBufMessageConverter<SourceType extends Message, TargetType extends EObject>
  implements
    Converter.WithMappingContext<SourceType, Descriptors.Descriptor, TargetType, EClass>
{
  private Converter.MappingContext<Descriptor, EClass> mappingContext;

  @Override
  public void setMappingContext(Converter.MappingContext<Descriptors.Descriptor, EClass> context)
  {
    mappingContext = context;
  }
  
  @Override
  public Converter.MappingContext<Descriptors.Descriptor, EClass> getMappingContext()
  {
    return mappingContext;
  }
  
  public boolean supports(Descriptors.Descriptor sourceType)
  {
    return supports(sourceType, getTargetType(sourceType));
  }
  
  protected abstract EClass getTargetType(Descriptors.Descriptor sourceType);
  
  public TargetType convert(SourceType source)
  {
    return convert(source, getTargetType(source.getDescriptorForType()));
  }
  
  public TargetType convert(SourceType source, EClass targetType)
  {
    return convert(source.getDescriptorForType(), source, targetType);
  }
  
  public final SourceType load(Descriptors.Descriptor sourceType, ExtensionRegistry extensions, ByteString data)
  {
    try
    {
      return doLoad(sourceType, extensions, data);
    }
    catch (InvalidProtocolBufferException e)
    {
      throw new ConversionException(e);
    }
  }
  
  protected abstract SourceType doLoad(Descriptors.Descriptor sourceType, ExtensionRegistry extensions, ByteString data) throws InvalidProtocolBufferException;
  
  public TargetType loadAndConvert(Descriptors.Descriptor sourceType, ExtensionRegistry extensions, ByteString data)
  {
    return convert(load(sourceType, extensions, data));
  }
}