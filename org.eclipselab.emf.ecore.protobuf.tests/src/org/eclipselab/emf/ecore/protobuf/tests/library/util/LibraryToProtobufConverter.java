package org.eclipselab.emf.ecore.protobuf.tests.library.util;

import com.google.protobuf.*;
import com.google.protobuf.Descriptors.*;
import org.eclipse.emf.ecore.*;
import org.eclipselab.emf.ecore.protobuf.conversion.*;
import org.eclipselab.emf.ecore.protobuf.tests.library.*; 

public class LibraryToProtobufConverter extends ToProtoBufMessageConverter<Library, LibraryProtos.Library> implements Converter.WithRegistry
{
  private ToProtoBufMessageConverter<Book, LibraryProtos.Book> bookConverter;
  private ToProtoBufMessageConverter<Author, LibraryProtos.Author> authorConverter;

  @Override
  @SuppressWarnings("unchecked")
  public void setRegistry(ConverterRegistry registry)
  {
    bookConverter = (ToProtoBufMessageConverter<Book, LibraryProtos.Book>) registry.find(LibraryPackage.Literals.BOOK);
    authorConverter = (ToProtoBufMessageConverter<Author, LibraryProtos.Author>) registry.find(LibraryPackage.Literals.AUTHOR);
  }

  @Override
  public void setObjectPool(EObjectPool pool)
  {
    super.setObjectPool(pool);
  	bookConverter.setObjectPool(pool);
  	authorConverter.setObjectPool(pool);
  }
  
  @Override
  public boolean supports(EClass sourceType, Descriptor targetType)
  {
    return LibraryProtos.Library.getDescriptor() == targetType && LibraryPackage.Literals.LIBRARY == sourceType;
  }

  @Override
  public LibraryProtos.Library convert(final EClass sourceType, final Library source, final Descriptor targetType)
  {
    final LibraryProtos.Library.Builder result = LibraryProtos.Library.newBuilder();
    result.setId(pool.getId(source));
    
	if(source.eIsSet(LibraryPackage.Literals.LIBRARY__NAME))
	{	
	  result.setName(source.getName());
	}
	if(source.eIsSet(LibraryPackage.Literals.LIBRARY__AUTHORS))
	{	
	  final int numAuthors = source.getAuthors().size();
	  
	  for(int idxAuthors = 0; idxAuthors < numAuthors; idxAuthors++)
	  {
	    final Author curAuthor = source.getAuthors().get(idxAuthors);
	  
	    if(curAuthor.eClass() == LibraryPackage.Literals.AUTHOR)
	    {
	      result.addAuthorsBuilder().setExtension(LibraryProtos.Author.authorAuthor,
	        authorConverter.convert(LibraryPackage.Literals.AUTHOR, curAuthor, LibraryProtos.Author.getDescriptor())
	      );
	    }
	    else
	    {
	      throw new UnsupportedOperationException();
	    }
	  }
	}
	if(source.eIsSet(LibraryPackage.Literals.LIBRARY__BOOKS))
	{	
	  final int numBooks = source.getBooks().size();
	  
	  for(int idxBooks = 0; idxBooks < numBooks; idxBooks++)
	  {
	    final Book curBook = source.getBooks().get(idxBooks);
	  
	    if(curBook.eClass() == LibraryPackage.Literals.BOOK)
	    {
	      result.addBooksBuilder().setExtension(LibraryProtos.Book.bookBook,
	        bookConverter.convert(LibraryPackage.Literals.BOOK, curBook, LibraryProtos.Book.getDescriptor())
	      );
	    }
	    else
	    {
	      throw new UnsupportedOperationException();
	    }
	  }
	}
    
    return result.build();
  }

  @Override
  protected Descriptor getTargetType(EClass sourceType)
  {
    return LibraryProtos.Library.getDescriptor();
  }
}

