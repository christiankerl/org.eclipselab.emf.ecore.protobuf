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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Callable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipselab.emf.ecore.protobuf.tests.library.Author;
import org.eclipselab.emf.ecore.protobuf.tests.library.Book;
import org.eclipselab.emf.ecore.protobuf.tests.library.Library;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryFactory;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryPackage;
import org.junit.Test;

import bb.util.Benchmark;


public class ProtobufResourcePerformanceTest
{
  private EObject createModel() {
    Library lib = LibraryFactory.eINSTANCE.createLibrary();
    
    lib.setName("Big Library");
    
    int authors = 50;
    int books = 1000;
    
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
  
  @Test
  public void testWritePerformanceWithBinary() throws Throwable {
    final Resource resource = new BinaryResourceImpl();
    final EObject model = createModel();
    resource.getContents().add(model);
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    benchmark(new Callable<Object>()
      {
        @Override
        public Object call() throws Exception
        {
          out.reset();
          resource.save(new BufferedOutputStream(out), Collections.emptyMap());
          
          return null;
        }
      });
    
    System.out.println("Size: " + out.size() + " bytes");
  }
  
  @Test
  public void testWritePerformanceWithProtobuf() throws Throwable {
    final Resource resource = new ProtobufResourceImpl();
    final EObject model = createModel();
    resource.getContents().add(model);
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    benchmark(new Callable<Object>()
      {
        @Override
        public Object call() throws Exception
        {
          out.reset();
          resource.save(new BufferedOutputStream(out), Collections.emptyMap());
          
          return null;
        }
      });

    System.out.println("Size: " + out.size() + " bytes");
  }
  
  @Test
  public void testReadPerformanceWithBinary() throws Throwable {
    final EObject model = createModel();
    final byte[] data = serialize(new BinaryResourceImpl(), model);
    final ResourceSet resources = new ResourceSetImpl();
    resources.getPackageRegistry().put(LibraryPackage.eNS_URI, LibraryPackage.eINSTANCE);
    
    benchmark(new Callable<Object>()
      {
        @Override
        public Object call() throws Exception
        {
          BinaryResourceImpl resource = new BinaryResourceImpl();
          resource.basicSetResourceSet(resources, null);
          resource.load(new ByteArrayInputStream(data), Collections.emptyMap());
          
          return null;
        }
      });
  }
  
  @Test
  public void testReadPerformanceWithProtobuf() throws Throwable {
    final EObject model = createModel();
    final byte[] data = serialize(new ProtobufResourceImpl(), model);
    final ResourceSet resources = new ResourceSetImpl();
    resources.getPackageRegistry().put(LibraryPackage.eNS_URI, LibraryPackage.eINSTANCE);
    
    benchmark(new Callable<Object>()
      {
        @Override
        public Object call() throws Exception
        {
          ProtobufResourceImpl resource = new ProtobufResourceImpl();
          resource.basicSetResourceSet(resources, null);
          resource.load(new ByteArrayInputStream(data), Collections.emptyMap());
          
          return null;
        }
      });
  }

  private byte[] serialize(Resource resource, EObject model) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    resource.getContents().add(model);
    resource.save(out, null);
    
    return out.toByteArray();
  }
  
  private void benchmark(Callable<Object> callable) throws Throwable {
    
    Benchmark.Params p = new Benchmark.Params(true);
    p.setConsoleFeedback(false);
    
    Benchmark benchmark = new Benchmark(callable, p);
    
    System.out.println(benchmark.toStringFull());
  }
}
