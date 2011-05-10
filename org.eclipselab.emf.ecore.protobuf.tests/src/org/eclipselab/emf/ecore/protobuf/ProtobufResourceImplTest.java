package org.eclipselab.emf.ecore.protobuf;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipselab.emf.ecore.protobuf.tests.library.Author;
import org.eclipselab.emf.ecore.protobuf.tests.library.Book;
import org.eclipselab.emf.ecore.protobuf.tests.library.Library;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryFactory;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryPackage;
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
		
		Library lib = factory.createLibrary();
		lib.setName("MyVirtualLibrary");
		// lib.getAuthors().add(author);
		// lib.getBooks().add(book);

		resource.getContents().add(lib);
		resource.save(null);
		
		resource.unload();
		
		resource.load(null);
		
		resource.getContents();
	}
}
