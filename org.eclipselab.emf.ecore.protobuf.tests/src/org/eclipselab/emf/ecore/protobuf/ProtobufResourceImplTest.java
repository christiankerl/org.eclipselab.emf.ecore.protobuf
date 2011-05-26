package org.eclipselab.emf.ecore.protobuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
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
 */
public class ProtobufResourceImplTest {

	@Test
	public void test() throws Exception {
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

		resource.getContents().add(lib);
		
		ByteArrayOutputStream dataOutput = new ByteArrayOutputStream(1024);
		
		long start = System.nanoTime();
		resource.save(dataOutput, null);
		System.out.println((System.nanoTime() - start) / 10.0e5);
		
		resource.unload();
		
		ByteArrayInputStream dataInput = new ByteArrayInputStream(dataOutput.toByteArray());
		
		start = System.nanoTime();
		resource.load(dataInput, null);
		System.out.println((System.nanoTime() - start) / 10.0e5);
		
		lib = (Library) resource.getContents().get(0);
		
		System.out.println(lib);
		System.out.println(lib.getAuthors());
		System.out.println(lib.getBooks());
		System.out.println(lib.getBooks().get(0).getAuthor());
	}
}
