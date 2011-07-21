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
package org.eclipselab.emf.ecore.protobuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipselab.emf.ecore.protobuf.conversion.ConverterRegistry;
import org.eclipselab.emf.ecore.protobuf.mapping.DefaultNamingStrategy;
import org.eclipselab.emf.ecore.protobuf.mapping.MapperRegistry;
import org.eclipselab.emf.ecore.protobuf.tests.library.Author;
import org.eclipselab.emf.ecore.protobuf.tests.library.Book;
import org.eclipselab.emf.ecore.protobuf.tests.library.Library;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryFactory;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryPackage;
import org.eclipselab.emf.ecore.protobuf.tests.library.util.LibraryConverters;
import org.eclipselab.emf.ecore.protobuf.tests.library.util.LibraryPackageMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

@BenchmarkOptions(warmupRounds = 150, benchmarkRounds = 150)
public class ProtobufResourceBenchmark extends AbstractBenchmark
{
  /**
   * Run several rounds inside one junit-benchmarks round to get visible per round measurements.
   */
  private static final int INTERNAL_ROUNDS = 1;
  
  private static EObject model;
  private static ByteArrayOutputStream out;
  private static Map<Object, Object> staticOptions;
  private static byte[] binaryData;
  private static byte[] xmiData;
  private static byte[] protobufData;
  private static ResourceSetImpl resources;
  
  private static EObject createModel() {
    Library lib = LibraryFactory.eINSTANCE.createLibrary();
    
    lib.setName("Big Library");
    
    int authors = 15000;
    int books = 50000;
    
    for(int idx = 0; idx < authors; idx++) {
      Author author = LibraryFactory.eINSTANCE.createAuthor();
      author.setName("Mr No" + idx);
      
      lib.getAuthors().add(author);
    }
    
    Random rnd = new Random();
    
    for(int idx = 0; idx < books; idx++) {
      Book book =  LibraryFactory.eINSTANCE.createBook();
      book.setName("Book No" + idx);
      book.setAuthor(lib.getAuthors().get(rnd.nextInt(authors)));
      
      lib.getBooks().add(book);
    }
    
    return lib;
  }

  private static byte[] serialize(Resource resource, Map<?, ?> options, EObject model) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    resource.getContents().add(model);
    resource.save(out, options);
    
    return out.toByteArray();
  }
  
  @BeforeClass
  public static void setup() throws Exception
  {
    model = createModel();
    out = new ByteArrayOutputStream();
    binaryData = serialize(new BinaryResourceImpl(), null, model);
    xmiData = serialize(new XMIResourceImpl(), null, model);
    protobufData = serialize(new ProtobufResourceImpl(), null, model);
    
    // clear dynamic cache
    LibraryPackage.eINSTANCE.eAdapters().clear();
    
    final ConverterRegistry converters = new ConverterRegistry();
    LibraryConverters.register(converters);
    final MapperRegistry mappers = new MapperRegistry(new DefaultNamingStrategy());
    LibraryPackageMapper.register(mappers);
    
    staticOptions = new HashMap<Object, Object>();
    staticOptions.put(ProtobufResourceImpl.OPTION_CONVERTER_REGISTRY, converters);
    staticOptions.put(ProtobufResourceImpl.OPTION_MAPPER_REGISTRY, mappers);

    resources = new ResourceSetImpl();
    resources.getPackageRegistry().put(LibraryPackage.eNS_URI, LibraryPackage.eINSTANCE);
  }
  
  @Test
  public void testBinaryWritePerformance() throws Exception
  {
    for(int idx = 0; idx < INTERNAL_ROUNDS; idx++)
      save(new BinaryResourceImpl(), null);
  }

  @Test
  public void testXmiWritePerformance() throws Exception
  {
    for(int idx = 0; idx < INTERNAL_ROUNDS; idx++)
      save(new XMIResourceImpl(), null);
  }
  
  @Test
  public void testStaticProtobufWritePerformance() throws Exception
  {
    for(int idx = 0; idx < INTERNAL_ROUNDS; idx++)
      save(new ProtobufResourceImpl(), staticOptions);
  }
  
  @Test
  public void testDynamicProtobufWritePerformance() throws Exception
  {
    for(int idx = 0; idx < INTERNAL_ROUNDS; idx++)
      save(new ProtobufResourceImpl(), null);
  }

  @Test
  public void testBinaryReadPerformance() throws Exception
  {
    for(int idx = 0; idx < INTERNAL_ROUNDS; idx++)
      load(new BinaryResourceImpl(), null, binaryData);
  }

  @Test
  public void testXmiReadPerformance() throws Exception
  {
    for(int idx = 0; idx < INTERNAL_ROUNDS; idx++)
      load(new XMIResourceImpl(), null, xmiData);
  }
    
  @Test
  public void testStaticProtobufReadPerformance() throws Exception
  {
    for(int idx = 0; idx < INTERNAL_ROUNDS; idx++)
      load(new ProtobufResourceImpl(), staticOptions, protobufData);
  }
  
  @Test
  public void testDynamicProtobufReadPerformance() throws Exception
  {
    for(int idx = 0; idx < INTERNAL_ROUNDS; idx++)
      load(new ProtobufResourceImpl(), null, protobufData);
  }
  
  @AfterClass
  public static void printDataSizeComparision()
  {
    System.out.println(String.format("ProtobufResourceImpl: %s bytes", protobufData.length));
    System.out.println(String.format("BinaryResourceImpl: %s bytes", binaryData.length));
    System.out.println(String.format("XMIResourceImpl: %s bytes", xmiData.length));
  }
  
  private final void save(final Resource resource, final Map<?, ?> options) throws IOException
  {
    resource.getContents().add(model);
    resource.save(out, options);
    out.reset();
  }
  
  private final void load(final ResourceImpl resource, final Map<?, ?> options, byte[] data) throws IOException
  {
    resource.basicSetResourceSet(resources, null);
    resource.load(new ByteArrayInputStream(data), options);
  }
}
