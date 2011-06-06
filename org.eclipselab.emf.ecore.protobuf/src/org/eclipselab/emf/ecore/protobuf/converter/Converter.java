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

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.Descriptors.Descriptor;


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

  /**
   * A FromProtoBufMessageConverter converts from a ProtoBuf {@link Message} to
   * an Ecore {@link EObject}.
   * 
   * @author Christian Kerl
   */
  abstract class FromProtoBufMessageConverter<SourceType extends Message, TargetType extends EObject>
    implements
      Converter<SourceType, Descriptors.Descriptor, TargetType, EClass>
  {
    public EObject convert(SourceType source, EClass targetType)
    {
      return convert(source.getDescriptorForType(), source, targetType);
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
      Converter<SourceType, EClass, TargetType, Descriptors.Descriptor>
  {
    public TargetType convert(SourceType source, Descriptor targetType)
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
