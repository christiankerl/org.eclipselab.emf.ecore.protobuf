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
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipselab.emf.ecore.protobuf.mapping.BasicEClassifierMapperImpl;
import org.eclipselab.emf.ecore.protobuf.mapping.NamingStrategy;

import com.google.protobuf.DescriptorProtos.EnumDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;

public class EEnumMapperImpl extends BasicEClassifierMapperImpl<EEnum>
{
  private final NamingStrategy naming;

  public EEnumMapperImpl(NamingStrategy naming)
  {
    super();
    this.naming = naming;
  }

  @Override
  public boolean supports(EClassifier eClassifier)
  {
    return eClassifier instanceof EEnum;
  }

  @Override
  protected void doMap(EEnum eEnum, FileDescriptorProto.Builder pbPackage)
  {
    EnumDescriptorProto.Builder pbEnum = pbPackage.addEnumTypeBuilder();  
    pbEnum.setName(naming.getEnum(eEnum));

    for (EEnumLiteral literal : eEnum.getELiterals())
    {
      pbEnum.addValueBuilder()
        .setName(literal.getName())
        .setNumber(literal.getValue());
    }
  }

  @Override
  protected void doMapReference(EEnum eEnum, FieldDescriptorProto.Builder pbField)
  {
    pbField.setType(FieldDescriptorProto.Type.TYPE_ENUM);
    pbField.setTypeName(naming.getEnum(eEnum));
  }
}
