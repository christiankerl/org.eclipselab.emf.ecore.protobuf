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
 * EClassifierMapper  is a {@link Mapper}, which maps an {@link EClassifier} to its representation
 * as child of a given {@link FileDescriptorProto}. It also is a {@link ReferenceMapper}, which maps a
 * reference to an {@link EClassifier} into a {@link FieldDescriptorProto}.
 * 
 * @author Christian Kerl
 */
public interface EClassifierMapper extends 
  Mapper<EClassifier, FileDescriptorProto.Builder>, 
  ReferenceMapper<EClassifier, FieldDescriptorProto.Builder>
{  
}