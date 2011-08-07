package org.eclipselab.emf.ecore.protobuf.tests.library.util;

import com.google.protobuf.Descriptors.*;
import org.eclipse.emf.ecore.*;
import org.eclipselab.emf.ecore.protobuf.conversion.*;
import org.eclipselab.emf.ecore.protobuf.tests.library.*; 

public class BookToProtobufConverter extends ToProtoBufMessageConverter<Book, LibraryProtos.Book> implements Converter.WithRegistry
{
  private ToProtoBufMessageConverter<Author, LibraryProtos.Author> authorConverter;

  @Override
  @SuppressWarnings("unchecked")
  public void setRegistry(ConverterRegistry registry)
  {
    authorConverter = (ToProtoBufMessageConverter<Author, LibraryProtos.Author>) registry.find(LibraryPackage.Literals.AUTHOR);
  }

  @Override
  public void setObjectPool(EObjectPool pool)
  {
    super.setObjectPool(pool);
      authorConverter.setObjectPool(pool);
  }
  
  @Override
  public boolean supports(EClass sourceType, Descriptor targetType)
  {
    return LibraryProtos.Book.getDescriptor() == targetType && LibraryPackage.Literals.BOOK == sourceType;
  }

  @Override
  public LibraryProtos.Book convert(final EClass sourceType, final Book source, final Descriptor targetType)
  {
    final LibraryProtos.Book.Builder result = LibraryProtos.Book.newBuilder();
    result.setId(pool.getId(source));
    
    if(source.eIsSet(LibraryPackage.Literals.BOOK__NAME))
    {
      result.setName(source.getName());
    }
    if(source.eIsSet(LibraryPackage.Literals.BOOK__AUTHOR))
    {
      final Author curAuthor = source.getAuthor();
      
      if(curAuthor.eClass() == LibraryPackage.Literals.AUTHOR)
      {
        result.getAuthorBuilder().setExtension(LibraryProtos.Author.authorAuthor,
          LibraryProtos.Author.newBuilder().setId(pool.getId(curAuthor)).build()
        );
      }
      else
      {
        throw new UnsupportedOperationException();
      }
    }
    if(source.eIsSet(LibraryPackage.Literals.BOOK__RATING))
    {
      switch(source.getRating())
      {
        case NO_RATING:
          result.setRating(LibraryProtos.Rating.NO_RATING);
          break;
        case GOOD:
          result.setRating(LibraryProtos.Rating.GOOD);
          break;
        case MEDIUM:
          result.setRating(LibraryProtos.Rating.MEDIUM);
          break;
        case BAD:
          result.setRating(LibraryProtos.Rating.BAD);
          break;
      }
    }
    
    return result.build();
  }

  @Override
  protected Descriptor getTargetType(EClass sourceType)
  {
    return LibraryProtos.Book.getDescriptor();
  }
}

