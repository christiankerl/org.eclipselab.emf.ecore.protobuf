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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipselab.emf.ecore.protobuf.internal.EObjectPool;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;


/**
 * A Converter converts a source object of the runtime type Source, which is described by an instance of SourceType,
 * to a target object of the runtime type Target, which is described by an instance of TargetType.
 * 
 * @author Christian Kerl
 */
public interface Converter<Source, SourceType, Target, TargetType>
{

  /**
   * Returns true, if this converter can convert objects of the given source type to
   * the given target type, false otherwise.
   * 
   * @param sourceType
   * @param targetType
   * 
   * @return
   */
  boolean supports(SourceType sourceType, TargetType targetType);

  /**
   * Converts a source object, which's type is described by the given source type, to
   * a target instance of the given target type. 
   * 
   * @param sourceType
   * @param source
   * @param targetType
   * 
   * @return
   */
  Target convert(SourceType sourceType, Source source, TargetType targetType);

  // source type of reference
  // source type of actual value
  // source actual value
  // target type of actual value
  // target value holding the reference
  
  // void convertReference(Message targetRef, EClass sourceRefType, EObject sourceRefValue);
  
  public interface MappingContext<SourceType, TargetType>
  {
    TargetType lookup(SourceType sourceType);
  }
  
  public interface ConverterWithMappingContext<Source, SourceType, Target, TargetType> extends Converter<Source, SourceType, Target, TargetType>
  {
    void setMappingContext(MappingContext<SourceType, TargetType> context);
    
    MappingContext<SourceType, TargetType> getMappingContext();
  }
  
  public interface WithRegistry
  {
    void setRegistry(ConverterRegistry registry);
  }

  public interface ConversionContext
  {
    EObjectPool pool();
    
    MappingContext<?, ?> mapping();
    
    ConverterRegistry converters();
  }
  
  /**
   * A FromProtoBufMessageConverter converts from a ProtoBuf {@link Message} to
   * an Ecore {@link EObject}.
   * 
   * @author Christian Kerl
   */
  abstract class FromProtoBufMessageConverter<SourceType extends Message, TargetType extends EObject>
    implements
      ConverterWithMappingContext<SourceType, Descriptors.Descriptor, TargetType, EClass>
  {
    private MappingContext<Descriptor, EClass> mappingContext;

    @Override
    public void setMappingContext(MappingContext<Descriptors.Descriptor, EClass> context)
    {
      mappingContext = context;
    }
    
    @Override
    public MappingContext<Descriptors.Descriptor, EClass> getMappingContext()
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

  public static class ConversionException extends RuntimeException
  {
    private static final long serialVersionUID = 3123192576333858453L;

    public ConversionException(Throwable cause)
    {
      super(cause);
    }    
  }
  
  /**
   * A ToProtoBufMessageConverter converts from an Ecore {@link EObject} to 
   * a ProtoBuf {@link Message}.
   * 
   * @author Christian Kerl
   */
  abstract class ToProtoBufMessageConverter<SourceType extends EObject, TargetType extends Message>
    implements
      ConverterWithMappingContext<SourceType, EClass, TargetType, Descriptors.Descriptor>
  {
    private MappingContext<EClass, Descriptors.Descriptor> mappingContext;
    
    protected EObjectPool pool;

    @Override
    public void setMappingContext(MappingContext<EClass, Descriptors.Descriptor> context)
    {
      mappingContext = context;
    }
    
    @Override
    public MappingContext<EClass, Descriptors.Descriptor> getMappingContext()
    {
      return mappingContext;
    }
    
    public void setObjectPool(EObjectPool pool)
    {
      this.pool = pool;
    }
    
    public boolean supports(EClass sourceType)
    {
      return supports(sourceType, getTargetType(sourceType));
    }
    
    protected abstract Descriptors.Descriptor getTargetType(EClass sourceType);
    
    public final TargetType convert(SourceType source)
    {
      return convert(source, getMappingContext().lookup(source.eClass()));
    }
    
    public final TargetType convert(SourceType source, Descriptors.Descriptor targetType)
    {
      return convert(source.eClass(), source, targetType);
    }
  }

  /**
   * A FromProtoBufScalarConverter converts a scalar value from its ProtoBuf representation
   * to its Ecore representation.
   * 
   * @author Christian Kerl
   */
  abstract class FromProtoBufScalarConverter implements Converter<Object, Descriptors.FieldDescriptor, Object, EDataType>
  {

  }

  /**
   * A ToProtoBufScalarConverter converts a scalar value from its Ecore representation
   * to its ProtoBuf representation.
   * 
   * @author Christian Kerl
   */
  abstract class ToProtoBufScalarConverter implements Converter<Object, EDataType, Object, Descriptors.FieldDescriptor>
  {

  }
}
