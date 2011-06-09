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
package org.eclipselab.emf.ecore.protobuf.internal.mapping;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipselab.emf.ecore.protobuf.mapping.BasicEClassifierMapperImpl;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;

public class DefaultEDataTypeMapperImpl extends BasicEClassifierMapperImpl<EDataType>
{
  @Override
  public boolean supports(EClassifier eClassifier)
  {
    return eClassifier instanceof EDataType;
  }

  @Override
  protected void doMap(EDataType eDataType, FileDescriptorProto.Builder pbPackage)
  {
  }

  @Override
  protected void doMapReference(EDataType eDataType, FieldDescriptorProto.Builder pbField)
  {
    pbField.setType(FieldDescriptorProto.Type.TYPE_STRING);
  }
}
