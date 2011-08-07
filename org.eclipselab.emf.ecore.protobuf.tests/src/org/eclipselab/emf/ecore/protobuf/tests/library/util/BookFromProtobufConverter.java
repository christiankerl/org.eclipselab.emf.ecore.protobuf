package org.eclipselab.emf.ecore.protobuf.tests.library.util;

import com.google.protobuf.*;
import com.google.protobuf.Descriptors.*;
import org.eclipse.emf.ecore.*;
import org.eclipselab.emf.ecore.protobuf.conversion.*;
import org.eclipselab.emf.ecore.protobuf.tests.library.*; 

public class BookFromProtobufConverter extends FromProtoBufMessageConverter<LibraryProtos.Book, Book> implements Converter.WithRegistry
{
  private FromProtoBufMessageConverter<LibraryProtos.Author, Author> authorConverter;

  @Override
  @SuppressWarnings("unchecked")
  public void setRegistry(ConverterRegistry registry)
  {
    authorConverter = (FromProtoBufMessageConverter<LibraryProtos.Author, Author>) registry.find(LibraryProtos.Author.getDescriptor(), LibraryPackage.Literals.AUTHOR);
  }

  @Override
  public void setObjectPool(EObjectPool pool)
  {
    super.setObjectPool(pool);
      authorConverter.setObjectPool(pool);
  }
      
  @Override
  public boolean supports(Descriptor sourceType, EClass targetType)
  {
    return LibraryProtos.Book.getDescriptor() == sourceType && LibraryPackage.Literals.BOOK == targetType;
  }

  @Override
  public Book convert(final Descriptor sourceType, final LibraryProtos.Book source, final EClass targetType)
  {
    final Book result = (Book) pool.getObject(LibraryPackage.Literals.BOOK, source.getId());
    
    if(source.hasName())
    {
      result.setName(source.getName());
    }
    if(source.hasAuthor())
    {
      final LibraryProtos.Author.Ref curAuthorRef = source.getAuthor();
      
      if(curAuthorRef.hasExtension(LibraryProtos.Author.authorAuthor))
      {
        final LibraryProtos.Author curAuthor = curAuthorRef.getExtension(LibraryProtos.Author.authorAuthor);
      
        result.setAuthor(
          (Author) pool.getObject(LibraryPackage.Literals.AUTHOR, curAuthor.getId())
        );
      }
      else
      {
        throw new UnsupportedOperationException();
      }
    }
    if(source.hasRating())
    {
      switch(source.getRating())
      {
        case NO_RATING:
          result.setRating(Rating.NO_RATING);
          break;
        case GOOD:
          result.setRating(Rating.GOOD);
          break;
        case MEDIUM:
          result.setRating(Rating.MEDIUM);
          break;
        case BAD:
          result.setRating(Rating.BAD);
          break;
      }
    }
    
    return result;
  }

  @Override
  protected EClass getTargetType(Descriptor sourceType)
  {
    return LibraryPackage.Literals.BOOK;
  }

  @Override
  public LibraryProtos.Book doLoad(Descriptor sourceType, ExtensionRegistry extensions, ByteString data) throws InvalidProtocolBufferException
  {
    return LibraryProtos.Book.parseFrom(data, extensions);
  }
}

