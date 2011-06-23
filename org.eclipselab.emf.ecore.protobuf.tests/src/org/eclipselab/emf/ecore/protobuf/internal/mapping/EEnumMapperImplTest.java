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

import static org.junit.Assert.*;

import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipselab.emf.ecore.protobuf.mapping.DefaultNamingStrategy;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryPackage;
import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.DescriptorProtos;

/**
 * UnitTest for {@link EEnumMapperImpl}.
 * 
 * @author Christian Kerl
 */
public class EEnumMapperImplTest
{
  private EEnumMapperImpl mapper;
  private EEnum eEnum;
  private DescriptorProtos.FileDescriptorProto.Builder pbPackage;

  @Before
  public void setup()
  {
    mapper = new EEnumMapperImpl(new DefaultNamingStrategy());
    pbPackage = DescriptorProtos.FileDescriptorProto.newBuilder();
    eEnum = LibraryPackage.Literals.RATING;
  }
  
  @Test
  public void itShouldSupportAnyEEnum()
  {
    assertTrue(mapper.supports(eEnum));
    assertFalse(mapper.supports(EcorePackage.Literals.EINT));
  }
  
  @Test
  public void itShouldMapEEnumToEnumDescriptorProto()
  {
    mapper.map(eEnum, pbPackage);
    
    assertEquals(1, pbPackage.getEnumTypeCount());
    
    DescriptorProtos.EnumDescriptorProto pbEnum = pbPackage.getEnumType(0);
    
    assertEquals(eEnum.getName(), pbEnum.getName());
    assertEquals(eEnum.getELiterals().size(), pbEnum.getValueCount());
    
    for(int idx = 0; idx < eEnum.getELiterals().size(); idx++)
    {
      assertEquals(eEnum.getELiterals().get(idx).getName(), pbEnum.getValue(idx).getName());
      assertEquals(eEnum.getELiterals().get(idx).getValue(), pbEnum.getValue(idx).getNumber());
    }
  }
  
  @Test
  public void itShouldUseQualifiedNameInReference()
  {
    pbPackage.setName("libraryext");
    DescriptorProtos.FieldDescriptorProto.Builder pbField = pbPackage.addMessageTypeBuilder()
      .setName("MyMessage")
      .addFieldBuilder()
        .setName("my_enum_field");
    
    mapper.mapReference(eEnum, pbField);
    
    assertEquals(DescriptorProtos.FieldDescriptorProto.Type.TYPE_ENUM, pbField.getType());
    assertEquals(".library.Rating", pbField.getTypeName());
  }
  
}
