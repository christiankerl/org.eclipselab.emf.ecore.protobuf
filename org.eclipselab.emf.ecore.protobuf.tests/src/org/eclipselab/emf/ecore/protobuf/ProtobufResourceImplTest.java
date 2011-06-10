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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipselab.emf.ecore.protobuf.tests.library.Author;
import org.eclipselab.emf.ecore.protobuf.tests.library.Book;
import org.eclipselab.emf.ecore.protobuf.tests.library.Library;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryFactory;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryPackage;
import org.eclipselab.emf.ecore.protobuf.tests.library.Rating;
import org.eclipselab.emf.ecore.protobuf.tests.library.util.LibraryResourceFactoryImpl;
import org.junit.Test;


/**
 * UnitTest for {@link ProtobufResourceImpl}.
 * 
 * @author Christian Kerl
 */
public class ProtobufResourceImplTest
{

  @Test
  public void test() throws Exception
  {
    ResourceSet resources = new ResourceSetImpl();
    resources.getPackageRegistry().put(LibraryPackage.eNS_URI, LibraryPackage.eINSTANCE);
    resources.getResourceFactoryRegistry().getExtensionToFactoryMap().put("library", new LibraryResourceFactoryImpl());

    Resource resource = resources.createResource(URI.createFileURI("./fixture.library"));

    LibraryFactory factory = LibraryFactory.eINSTANCE;

    Author author = factory.createAuthor();
    author.setName("Me");

    Book book = factory.createBook();
    book.setName("Tales of EMF and ProtoBuf");
    book.setAuthor(author);
    book.setRating(Rating.MEDIUM);

    Library lib = factory.createLibrary();
    lib.setName("MyVirtualLibrary");
    lib.getAuthors().add(author);
    lib.getBooks().add(book);

    resource.getContents().add(EcoreUtil.copy(lib));
    
    ByteArrayOutputStream dataOutput = new ByteArrayOutputStream(1024);

    long start = System.nanoTime();
    resource.save(dataOutput, null);
    System.out.println((System.nanoTime() - start) / 10.0e5);

    resource.unload();

    ByteArrayInputStream dataInput = new ByteArrayInputStream(dataOutput.toByteArray());

    start = System.nanoTime();
    resource.load(dataInput, null);
    System.out.println((System.nanoTime() - start) / 10.0e5);

    Library loadedLib = (Library) resource.getContents().get(0);
    
    assertTrue(EcoreUtil.equals(lib, loadedLib));
  }
}
