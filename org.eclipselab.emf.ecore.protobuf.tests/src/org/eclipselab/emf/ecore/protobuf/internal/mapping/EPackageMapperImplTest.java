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

import org.eclipse.emf.ecore.EPackage;
import org.eclipselab.emf.ecore.protobuf.mapping.DefaultNamingStrategy;
import org.eclipselab.emf.ecore.protobuf.mapping.MapperRegistry;
import org.eclipselab.emf.ecore.protobuf.tests.EPackageBuilder;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryPackage;
import org.eclipselab.emf.ecore.protobuf.util.DescriptorDebugStringBuilder;
import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.TextFormat;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.DescriptorValidationException;

public class EPackageMapperImplTest
{
  private EPackageMapperImpl mapper;
  private FileDescriptorSet.Builder files;
  private final DescriptorDebugStringBuilder protoStringBuilder = new DescriptorDebugStringBuilder();

  @Before
  public void setup()
  {
    mapper = new EPackageMapperImpl(new MapperRegistry(new DefaultNamingStrategy()));
    files = FileDescriptorSet.newBuilder();
  }
  
  @Test
  public void test() throws Exception
  {
    mapper.map(LibraryPackage.eINSTANCE, files);

    debug(files.getFile(0));
  }

  @Test
  public void testClassHierarchyMapping() throws Exception
  {
    EPackage pkg = EPackageBuilder.create("example").withClass("BaseA").beingAbstract().end().withClass("BaseB").subclassing("BaseA").end().withClass(
      "BaseC").subclassing("BaseB").beingAbstract().end().withClass("Impl1").subclassing("BaseC").end().withClass("Impl2").subclassing(
      "BaseC").end().withClass("Container").having("baseAObjects").containing().many().ofType("BaseA").end().having("baseCObjects").containing().many().ofType(
      "BaseC").end().end().get();

    mapper.map(pkg, files);

    Descriptors.FileDescriptor descriptors = Descriptors.FileDescriptor.buildFrom(files.getFile(0), new Descriptors.FileDescriptor[0]);

    Descriptors.Descriptor descriptor = descriptors.findMessageTypeByName("BaseC").findNestedTypeByName("Ref");
    Descriptors.FieldDescriptor field = descriptors.findMessageTypeByName("Impl2").findFieldByName("baseC_impl2");
    
    DynamicMessage.Builder b = DynamicMessage.newBuilder(descriptor);
    b.setField(field, DynamicMessage.newBuilder(field.getMessageType()).setField(field.getMessageType().findFieldByName("__id"), 1).build());
    
    debug(files.getFile(0));
  }
  
  private void debug(DescriptorProtos.FileDescriptorProto proto) throws DescriptorValidationException
  {
    System.out.println(TextFormat.printToString(proto));
    
    Descriptors.FileDescriptor result = Descriptors.FileDescriptor.buildFrom(proto, new Descriptors.FileDescriptor[0]);

    System.out.println(protoStringBuilder.build(result));
  }
}
