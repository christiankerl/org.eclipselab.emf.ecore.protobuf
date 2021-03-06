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
package org.eclipselab.emf.ecore.protobuf.mapping;

import org.eclipse.emf.ecore.EClassifier;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;

/**
 * BasicEClassifierMapperImpl provides a base class for an {@link EClassifierMapper} implementation for 
 * an {@link EClassifier} subclass. Subclasses have to implement {@link EClassifierMapper#supports(EClassifier)}
 * so that if it returns true casting the given {@link EClassifier} to SourceType is safe.
 * 
 * @param <SourceType> The subclass of {@link EClassifier} this {@link EClassifierMapper} supports
 * 
 * @author Christian Kerl
 */
public abstract class BasicEClassifierMapperImpl<SourceType extends EClassifier> implements EClassifierMapper
{
  @Override
  @SuppressWarnings("unchecked")
  public void map(EClassifier source, FileDescriptorProto.Builder context)
  {
    doMap((SourceType)source, context);
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public void mapReference(EClassifier source, FieldDescriptorProto.Builder context)
  {
    doMapReference((SourceType)source, context);
  }
  
  protected abstract void doMap(SourceType eClassifier, FileDescriptorProto.Builder context);
  
  protected abstract void doMapReference(SourceType eClassifier, FieldDescriptorProto.Builder context);
  
}