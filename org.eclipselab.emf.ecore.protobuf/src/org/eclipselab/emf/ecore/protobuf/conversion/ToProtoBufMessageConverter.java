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
import org.eclipselab.emf.ecore.protobuf.internal.EObjectPool;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;

/**
 * A ToProtoBufMessageConverter converts from an Ecore {@link EObject} to 
 * a ProtoBuf {@link Message}.
 * 
 * @author Christian Kerl
 */
public abstract class ToProtoBufMessageConverter<SourceType extends EObject, TargetType extends Message>
  implements
    Converter.WithMappingContext<SourceType, EClass, TargetType, Descriptors.Descriptor>
{
  private Converter.MappingContext<EClass, Descriptors.Descriptor> mappingContext;
  
  protected EObjectPool pool;

  @Override
  public void setMappingContext(Converter.MappingContext<EClass, Descriptors.Descriptor> context)
  {
    mappingContext = context;
  }
  
  @Override
  public Converter.MappingContext<EClass, Descriptors.Descriptor> getMappingContext()
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