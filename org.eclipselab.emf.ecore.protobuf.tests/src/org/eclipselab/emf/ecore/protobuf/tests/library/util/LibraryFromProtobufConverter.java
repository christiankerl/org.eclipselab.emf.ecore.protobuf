package org.eclipselab.emf.ecore.protobuf.tests.library.util;

import com.google.protobuf.*;
import com.google.protobuf.Descriptors.*;
import org.eclipse.emf.ecore.*;
import org.eclipselab.emf.ecore.protobuf.conversion.*;
import org.eclipselab.emf.ecore.protobuf.tests.library.*; 

public class LibraryFromProtobufConverter extends FromProtoBufMessageConverter<LibraryProtos.Library, Library> implements Converter.WithRegistry
{
  private FromProtoBufMessageConverter<LibraryProtos.Book, Book> bookConverter;
  private FromProtoBufMessageConverter<LibraryProtos.Author, Author> authorConverter;

  @Override
  @SuppressWarnings("unchecked")
  public void setRegistry(ConverterRegistry registry)
  {
    bookConverter = (FromProtoBufMessageConverter<LibraryProtos.Book, Book>) registry.find(LibraryProtos.Book.getDescriptor(), LibraryPackage.Literals.BOOK);
    authorConverter = (FromProtoBufMessageConverter<LibraryProtos.Author, Author>) registry.find(LibraryProtos.Author.getDescriptor(), LibraryPackage.Literals.AUTHOR);
  }

  @Override
  public void setObjectPool(EObjectPool pool)
  {
    super.setObjectPool(pool);
  	bookConverter.setObjectPool(pool);
  	authorConverter.setObjectPool(pool);
  }
      
  @Override
  public boolean supports(Descriptor sourceType, EClass targetType)
  {
    return LibraryProtos.Library.getDescriptor() == sourceType && LibraryPackage.Literals.LIBRARY == targetType;
  }

  @Override
  public Library convert(final Descriptor sourceType, final LibraryProtos.Library source, final EClass targetType)
  {
	final Library result = (Library) pool.getObject(LibraryPackage.Literals.LIBRARY, source.getId());
    
	if(source.hasName())
    {
      result.setName(source.getName());
    }
	if(source.getAuthorsCount() > 0)
    {
      final int numAuthors = source.getAuthorsCount();
      
      for(int idxAuthors = 0; idxAuthors < numAuthors; idxAuthors++)
      {
        final LibraryProtos.Author.Ref curAuthorRef = source.getAuthors(idxAuthors);
      
        if(curAuthorRef.hasExtension(LibraryProtos.Author.authorAuthor))
        {
          final LibraryProtos.Author curAuthor = curAuthorRef.getExtension(LibraryProtos.Author.authorAuthor);
      
          result.getAuthors().add(
            authorConverter.convert(curAuthor, LibraryPackage.Literals.AUTHOR)
          );
        }
        else
        {
          throw new UnsupportedOperationException();
        }
      }
    }
	if(source.getBooksCount() > 0)
    {
      final int numBooks = source.getBooksCount();
      
      for(int idxBooks = 0; idxBooks < numBooks; idxBooks++)
      {
        final LibraryProtos.Book.Ref curBookRef = source.getBooks(idxBooks);
      
        if(curBookRef.hasExtension(LibraryProtos.Book.bookBook))
        {
          final LibraryProtos.Book curBook = curBookRef.getExtension(LibraryProtos.Book.bookBook);
      
          result.getBooks().add(
            bookConverter.convert(curBook, LibraryPackage.Literals.BOOK)
          );
        }
        else
        {
          throw new UnsupportedOperationException();
        }
      }
    }
    
    return result;
  }

  @Override
  protected EClass getTargetType(Descriptor sourceType)
  {
    return LibraryPackage.Literals.LIBRARY;
  }

  @Override
  public LibraryProtos.Library doLoad(Descriptor sourceType, ExtensionRegistry extensions, ByteString data) throws InvalidProtocolBufferException
  {
    return LibraryProtos.Library.parseFrom(data, extensions);
  }
}

