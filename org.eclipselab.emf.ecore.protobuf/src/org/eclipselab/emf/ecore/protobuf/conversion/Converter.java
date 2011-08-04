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
  
  public static interface MappingContext<SourceType, TargetType>
  {
    TargetType lookup(SourceType sourceType);
  }
  
  public static interface WithMappingContext<Source, SourceType, Target, TargetType> extends Converter<Source, SourceType, Target, TargetType>
  {
    void setMappingContext(MappingContext<SourceType, TargetType> context);
    
    MappingContext<SourceType, TargetType> getMappingContext();
  }
  
  public static interface WithRegistry
  {
    void setRegistry(ConverterRegistry registry);
  }
}
