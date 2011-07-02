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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipselab.emf.ecore.protobuf.mapping.DefaultNamingStrategy;
import org.eclipselab.emf.ecore.protobuf.mapping.MapperRegistry;
import org.eclipselab.emf.ecore.protobuf.mapping.NamingStrategy;
import org.eclipselab.emf.ecore.protobuf.tests.EPackageBuilder;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryPackage;
import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.DescriptorProtos;

/**
 * UnitTest for {@link EClassMapperImpl}.
 * 
 * @author Christian Kerl
 */
public class EClassMapperImplTest
{
  private EClassMapperImpl mapper;

  @Before
  public void setup()
  {
    NamingStrategy naming = new DefaultNamingStrategy();
    mapper = new EClassMapperImpl(new MapperRegistry(naming), naming);
  }
  
  @Test
  public void itShouldSupportAnyEClass()
  {
    assertTrue(mapper.supports(LibraryPackage.Literals.AUTHOR));
    assertTrue(mapper.supports(LibraryPackage.Literals.BOOK));
    assertTrue(mapper.supports(EcorePackage.Literals.ECLASS));
    
    assertFalse(mapper.supports(LibraryPackage.Literals.RATING));
    assertFalse(mapper.supports(EcorePackage.Literals.EINT));
  }
  
  @Test 
  public void itShouldCreateMessageWithRefMessageForAnyEClass()
  {
    EPackage pkg = EPackageBuilder.create("pkg")
      .withClass("MyAbstractClass").beingAbstract().end()
      .withClass("MyInterfaceClass").beingInterface().end()
      .withClass("MyClass").end()
    .get();
    
    DescriptorProtos.FileDescriptorProto.Builder pbPackage = DescriptorProtos.FileDescriptorProto.newBuilder();
    
    for(EClassifier eClassifier : pkg.getEClassifiers())
    {
      assertTrue(mapper.supports(eClassifier));
      
      mapper.map(eClassifier, pbPackage);
      
      assertEquals(1, pbPackage.getMessageTypeCount());
      
      DescriptorProtos.DescriptorProto pbMessage = pbPackage.getMessageType(0);
      
      assertEquals(eClassifier.getName(), pbMessage.getName());
      assertEquals(1, pbMessage.getNestedTypeCount());
      
      DescriptorProtos.DescriptorProto pbRefMessage = pbMessage.getNestedType(0);
      
      assertEquals("Ref", pbRefMessage.getName());
      assertEquals(1, pbRefMessage.getExtensionRangeCount());
      
      pbPackage.clearMessageType();
    }
  }
  
  @Test
  public void itShouldCreateMessageWithIdAndRefMessageExtensionForNonAbstractEClass()
  {
    EPackage pkg = EPackageBuilder.create("pkg")
      .withClass("MyClass").end()
    .get();
    
    DescriptorProtos.FileDescriptorProto.Builder pbPackage = DescriptorProtos.FileDescriptorProto.newBuilder();
    
    mapper.map(pkg.getEClassifier("MyClass"), pbPackage);
    
    assertEquals(1, pbPackage.getMessageTypeCount());
    
    DescriptorProtos.DescriptorProto pbMessage = pbPackage.getMessageType(0);
    
    assertEquals(1, pbMessage.getFieldCount());
    
    DescriptorProtos.FieldDescriptorProto pbField = pbMessage.getField(0);
    
    assertEquals("__id", pbField.getName());
    assertEquals(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32, pbField.getType());
    assertEquals(DescriptorProtos.FieldDescriptorProto.Label.LABEL_REQUIRED, pbField.getLabel());
    
    assertEquals(1, pbMessage.getExtensionCount());
    
    DescriptorProtos.FieldDescriptorProto pbExtension = pbMessage.getExtension(0);
    
    assertEquals(".pkg.MyClass.Ref", pbExtension.getExtendee());
    assertEquals("myClass_myClass", pbExtension.getName());
    assertEquals(DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE, pbExtension.getType());
    assertEquals("MyClass", pbExtension.getTypeName());
    assertEquals(DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL, pbExtension.getLabel());
  }
  
  @Test
  public void itShouldCreateRefMessageExtensionsForSuperClasses()
  {
    EPackage pkg = EPackageBuilder.create("pkg")
      .withClass("MyRootClass").beingAbstract().end()
      .withClass("MyIntermediateClass").beingAbstract().subclassing("MyRootClass").end()
      .withClass("MyClass").subclassing("MyIntermediateClass").end()
    .get();
    
    List<String> expectedExtendees = new ArrayList<String>(Arrays.asList(".pkg.MyRootClass.Ref", ".pkg.MyIntermediateClass.Ref", ".pkg.MyClass.Ref"));
    
    DescriptorProtos.FileDescriptorProto.Builder pbPackage = DescriptorProtos.FileDescriptorProto.newBuilder();
    
    mapper.map(pkg.getEClassifier("MyClass"), pbPackage);
    
    assertEquals(1, pbPackage.getMessageTypeCount());
    
    DescriptorProtos.DescriptorProto pbMessage = pbPackage.getMessageType(0);
    
    assertEquals(3, pbMessage.getExtensionCount());
    
    for(DescriptorProtos.FieldDescriptorProto pbExtension : pbMessage.getExtensionList())
    {
      assertTrue(expectedExtendees.remove(pbExtension.getExtendee()));
    }
  }
  
  @Test
  public void itShouldMapEStructuralFeaturesToFieldDescriptorProtos()
  {
    EPackage pkg = EPackageBuilder.create("pkg")
      .withClass("MyChildClass")
      .end()
      .withClass("MyClass")
        .having("myInt").maybe().one().ofType(EcorePackage.Literals.EINT).end()
        .having("myFloat").one().ofType(EcorePackage.Literals.EFLOAT).end()
        .having("myStringList").many().ofType(EcorePackage.Literals.ESTRING).end()
        .having("myChildren").containing().many().ofType("MyChildClass").end()
        .having("mySelectedChildren").referencing().many().ofType("MyChildClass").end()
      .end()
    .get();
  
    DescriptorProtos.FileDescriptorProto.Builder pbPackage = DescriptorProtos.FileDescriptorProto.newBuilder();
  
    mapper.map(pkg.getEClassifier("MyClass"), pbPackage);
  
    assertEquals(1, pbPackage.getMessageTypeCount());
  
    DescriptorProtos.DescriptorProto pbMessage = pbPackage.getMessageType(0);
    
    assertEquals(6, pbMessage.getFieldCount());
    // TODO
  }
  
  @Test
  public void itShouldUseQualifiedRefMessageNameInReferences()
  {
    EPackage pkg = EPackageBuilder.create("pkg")
      .withClass("MyClass").end()
    .get();
    
    DescriptorProtos.FileDescriptorProto.Builder pbPackage = DescriptorProtos.FileDescriptorProto.newBuilder()
      .setName("pkgext");
    
    DescriptorProtos.FieldDescriptorProto.Builder pbField = pbPackage.addMessageTypeBuilder()
      .setName("MyMessage")
      .addFieldBuilder()
        .setName("my_class_field");
    
    mapper.mapReference(pkg.getEClassifier("MyClass"), pbField);
    
    assertEquals(DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE, pbField.getType());
    assertEquals(".pkg.MyClass.Ref", pbField.getTypeName());
  }
}
