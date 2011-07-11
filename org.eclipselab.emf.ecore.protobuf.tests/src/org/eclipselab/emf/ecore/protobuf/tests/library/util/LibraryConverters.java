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
package org.eclipselab.emf.ecore.protobuf.tests.library.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipselab.emf.ecore.protobuf.converter.Converter;
import org.eclipselab.emf.ecore.protobuf.converter.ConverterRegistry;
import org.eclipselab.emf.ecore.protobuf.converter.FromProtoBufMessageConverter;
import org.eclipselab.emf.ecore.protobuf.internal.EObjectPool;
import org.eclipselab.emf.ecore.protobuf.tests.library.Author;
import org.eclipselab.emf.ecore.protobuf.tests.library.Book;
import org.eclipselab.emf.ecore.protobuf.tests.library.Library;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryPackage;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryProtos;
import org.eclipselab.emf.ecore.protobuf.tests.library.Rating;
import org.eclipselab.emf.ecore.protobuf.util.ProtobufUtil;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public class LibraryConverters
{
  public static void register(ConverterRegistry registry)
  {
    registry.register(new AuthorFromProtobufConverter());
    registry.register(new BookFromProtobufConverter());
    registry.register(new LibraryFromProtobufConverter());

    registry.register(new AuthorToProtobufConverter());
    registry.register(new BookToProtobufConverter());
    registry.register(new LibraryToProtobufConverter());
  }
  
  public static class LibraryFromProtobufConverter extends FromProtoBufMessageConverter<LibraryProtos.Library, Library, Source, SourceType, Target, TargetType> implements Converter.WithRegistry
  {
    private EObjectPool pool;
    
    private FromProtoBufMessageConverter<LibraryProtos.Author, Author, Source, SourceType, Target, TargetType> authorConverter;
    private FromProtoBufMessageConverter<LibraryProtos.Book, Book, Source, SourceType, Target, TargetType> bookConverter;

    @Override
    @SuppressWarnings("unchecked")
    public void setRegistry(ConverterRegistry registry)
    {
      authorConverter = (FromProtoBufMessageConverter<LibraryProtos.Author, Author, Source, SourceType, Target, TargetType>)registry.find(LibraryProtos.Author.getDescriptor(), LibraryPackage.Literals.AUTHOR);
      bookConverter =  (FromProtoBufMessageConverter<LibraryProtos.Book, Book, Source, SourceType, Target, TargetType>)registry.find(LibraryProtos.Book.getDescriptor(), LibraryPackage.Literals.BOOK);
    }
        
    @Override
    public boolean supports(Descriptor sourceType, EClass targetType)
    {
      return LibraryProtos.Library.getDescriptor() == sourceType && LibraryPackage.Literals.LIBRARY == targetType;
    }

    @Override
    public Library convert(Descriptor sourceType, LibraryProtos.Library source, EClass targetType)
    {
      Library library = (Library) pool.getObject(LibraryPackage.Literals.LIBRARY, source.getId());
      
      if(source.hasName()) {
        library.setName(source.getName());
      }
      
      int nAuthors = source.getAuthorsCount();
      
      for(int idxAuthor = 0; idxAuthor < nAuthors; idxAuthor++)
      {
        LibraryProtos.Author.Ref authorRef = source.getAuthors(idxAuthor);
        
        if(authorRef.hasExtension(LibraryProtos.Author.authorAuthor))
        {
          LibraryProtos.Author author = authorRef.getExtension(LibraryProtos.Author.authorAuthor);
          
          library.getAuthors().add((Author) authorConverter.convert(author, LibraryPackage.Literals.AUTHOR));
        }
        else
        {
          // more
        }
      }
      
      int nBooks = source.getBooksCount();
      
      for(int idxBook = 0; idxBook < nBooks; idxBook++)
      {
        LibraryProtos.Book.Ref bookRef = source.getBooks(idxBook);
        
        if(bookRef.hasExtension(LibraryProtos.Book.bookBook))
        {
          LibraryProtos.Book book = bookRef.getExtension(LibraryProtos.Book.bookBook);
          
          library.getBooks().add((Book) bookConverter.convert(book, LibraryPackage.Literals.BOOK));
        }
        // add more branches
        else
        {
          // more 
        }
      }
      
      return library;
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
  
  public static class LibraryToProtobufConverter extends Converter.ToProtoBufMessageConverter<Library, LibraryProtos.Library> implements Converter.WithRegistry
  {
    private Converter.ToProtoBufMessageConverter<Author, LibraryProtos.Author> authorConverter;
    private Converter.ToProtoBufMessageConverter<Book, LibraryProtos.Book> bookConverter;

    @Override
    @SuppressWarnings("unchecked")
    public void setRegistry(ConverterRegistry registry)
    {
      authorConverter = (Converter.ToProtoBufMessageConverter<Author, LibraryProtos.Author>)registry.find(LibraryPackage.Literals.AUTHOR);
      bookConverter = (Converter.ToProtoBufMessageConverter<Book, LibraryProtos.Book>)registry.find(LibraryPackage.Literals.BOOK);
    }

    @Override
    public void setObjectPool(EObjectPool pool)
    {
      super.setObjectPool(pool);
      
      authorConverter.setObjectPool(pool);
      bookConverter.setObjectPool(pool);
    }
    
    @Override
    public boolean supports(EClass sourceType, Descriptor targetType)
    {
      return LibraryProtos.Library.getDescriptor() == targetType && LibraryPackage.Literals.LIBRARY == sourceType;
    }

    @Override
    public LibraryProtos.Library convert(final EClass sourceType, final Library source, final Descriptor targetType)
    {
      final LibraryProtos.Library.Builder library = LibraryProtos.Library.newBuilder();
      library.setId(pool.getId(source));
      
      if(source.eIsSet(LibraryPackage.Literals.LIBRARY__NAME))
      {
        library.setName(source.getName());
      }
      
      final int nAuthors = source.getAuthors().size();
      
      for(int idxAuthors = 0; idxAuthors < nAuthors; idxAuthors++)
      {
        final Author author = source.getAuthors().get(idxAuthors);
        
        if(author.eClass() == LibraryPackage.Literals.AUTHOR)
        {
          library.addAuthorsBuilder().setExtension(LibraryProtos.Author.authorAuthor, authorConverter.convert(LibraryPackage.Literals.AUTHOR, author, LibraryProtos.Author.getDescriptor()));
        }
        else
        {
          // more
        }
      }
      
      final int nBooks = source.getBooks().size();
      
      for(int idxBooks = 0; idxBooks < nBooks; idxBooks++)
      {
        final Book book = source.getBooks().get(idxBooks);
        
        if(book.eClass() == LibraryPackage.Literals.BOOK)
        {
          library.addBooksBuilder().setExtension(LibraryProtos.Book.bookBook, bookConverter.convert(LibraryPackage.Literals.BOOK, book, LibraryProtos.Book.getDescriptor()));
        }
        // add more branches
        else
        {
          // more
        }
      }
      
      return library.build();
    }

    @Override
    protected Descriptor getTargetType(EClass sourceType)
    {
      return LibraryProtos.Library.getDescriptor();
    }
  }
  
  public static class AuthorFromProtobufConverter extends FromProtoBufMessageConverter<LibraryProtos.Author, Author, Source, SourceType, Target, TargetType>
  {
    private EObjectPool pool;

    @Override
    public boolean supports(Descriptor sourceType, EClass targetType)
    {
      return LibraryProtos.Author.getDescriptor() == sourceType && LibraryPackage.Literals.AUTHOR == targetType;
    }

    @Override
    public Author convert(Descriptor sourceType, LibraryProtos.Author source, EClass targetType)
    {
      Author author = (Author) pool.getObject(LibraryPackage.Literals.AUTHOR, source.getId());
      
      if(source.hasName())
      {
        author.setName(source.getName());
      }
      
      return author;
    }

    @Override
    protected EClass getTargetType(Descriptor sourceType)
    {
      return LibraryPackage.Literals.AUTHOR;
    }

    @Override
    public LibraryProtos.Author doLoad(Descriptor sourceType, ExtensionRegistry extensions, ByteString data) throws InvalidProtocolBufferException
    {
      return LibraryProtos.Author.parseFrom(data, extensions);
    }
  }
  
  public static class AuthorToProtobufConverter extends Converter.ToProtoBufMessageConverter<Author, LibraryProtos.Author>
  {    
    @Override
    public boolean supports(EClass sourceType, Descriptor targetType)
    {
      return LibraryProtos.Author.getDescriptor() == targetType && LibraryPackage.Literals.AUTHOR == sourceType;
    }

    @Override
    public final LibraryProtos.Author convert(final EClass sourceType, final Author source, final Descriptor targetType)
    {
      final LibraryProtos.Author.Builder author = LibraryProtos.Author.newBuilder();
      author.setId(pool.getId(source));
      
      if(source.eIsSet(LibraryPackage.Literals.AUTHOR__NAME))
      {
        author.setName(source.getName());
      }
      
      return author.build();
    }

    @Override
    protected Descriptor getTargetType(EClass sourceType)
    {
      return LibraryProtos.Author.getDescriptor();
    }
  }
  
  public static class BookFromProtobufConverter extends FromProtoBufMessageConverter<LibraryProtos.Book, Book, Source, SourceType, Target, TargetType> implements Converter.WithRegistry
  {
    private EObjectPool pool;

    @Override
    public void setRegistry(ConverterRegistry registry)
    {
    }
    
    @Override
    public boolean supports(Descriptor sourceType, EClass targetType)
    {
      return LibraryProtos.Book.getDescriptor() == sourceType && LibraryPackage.Literals.BOOK == targetType;
    }
    
    @Override
    public Book convert(Descriptor sourceType, LibraryProtos.Book source, EClass targetType)
    {
      Book book = (Book) pool.getObject(LibraryPackage.Literals.BOOK, source.getId());
      
      if(source.hasName())
      {
        book.setName(source.getName());
      }
      if(source.hasRating())
      {
        switch(source.getRating())
        {
          case GOOD:
            book.setRating(Rating.GOOD);
            break;
          case MEDIUM:
            book.setRating(Rating.MEDIUM);
            break;
          case BAD:
            book.setRating(Rating.BAD);
            break;
          case NO_RATING:
            book.setRating(Rating.NO_RATING);
            break;
        }
      }
      if(source.hasAuthor())
      {
        EClass eClass;
        Integer id;
        
        if(source.getAuthor().hasExtension(LibraryProtos.Author.authorAuthor))
        {
          eClass = LibraryPackage.Literals.AUTHOR;
          id = source.getAuthor().getExtension(LibraryProtos.Author.authorAuthor).getId();
        }
        // add more branches for all known extensions (defined in this and all referenced packages)
        else
        {
          // handle possible extensions
          Message pbMessage = (Message) ProtobufUtil.getFirstFieldValue(source.getAuthor());
          Descriptors.Descriptor pbClass = pbMessage.getDescriptorForType();        
          
          eClass = getMappingContext().lookup(pbClass);
          id = (Integer) ProtobufUtil.getFirstFieldValue(pbMessage);
        }
        book.setAuthor((Author) pool.getObject(eClass, id));
      }
      
      return book;
    }

    @Override
    protected EClass getTargetType(Descriptor sourceType)
    {
      return LibraryPackage.Literals.BOOK;
    }

    @Override
    protected LibraryProtos.Book doLoad(Descriptor sourceType, ExtensionRegistry extensions, ByteString data) throws InvalidProtocolBufferException
    {
      return LibraryProtos.Book.parseFrom(data, extensions);
    }
  }
  
  public static class BookToProtobufConverter extends Converter.ToProtoBufMessageConverter<Book, LibraryProtos.Book>
  {    
    @Override
    public boolean supports(EClass sourceType, Descriptor targetType)
    {
      return LibraryProtos.Book.getDescriptor() == targetType && LibraryPackage.Literals.BOOK == sourceType;
    }

    @Override
    public final LibraryProtos.Book convert(final EClass sourceType, final Book source, final Descriptor targetType)
    {
      final LibraryProtos.Book.Builder book = LibraryProtos.Book.newBuilder();
      book.setId(pool.getId(source));
      
      if(source.eIsSet(LibraryPackage.Literals.BOOK__NAME))
      {
        book.setName(source.getName());
      }
      if(source.eIsSet(LibraryPackage.Literals.BOOK__RATING))
      {
        switch(source.getRating())
        {
          case GOOD:
            book.setRating(LibraryProtos.Rating.GOOD);
            break;
          case MEDIUM:
            book.setRating(LibraryProtos.Rating.MEDIUM);
            break;
          case BAD:
            book.setRating(LibraryProtos.Rating.BAD);
            break;
          case NO_RATING:
            book.setRating(LibraryProtos.Rating.NO_RATING);
            break;
        }
      }
      if(source.eIsSet(LibraryPackage.Literals.BOOK__AUTHOR))
      {
        final Author author = source.getAuthor();
        
        if(author.eClass() == LibraryPackage.Literals.AUTHOR)
        {
          final LibraryProtos.Author.Builder authorRef = LibraryProtos.Author.newBuilder();
          authorRef.setId(pool.getId(author));
          
          book.getAuthorBuilder().setExtension(LibraryProtos.Author.authorAuthor, authorRef.build());
        }
        // add more branches
        else
        {
          // more
          // getMappingContext().lookup(author.eClass());
        }
      }
      
      return book.build();
    }

    @Override
    protected Descriptor getTargetType(EClass sourceType)
    {
      return LibraryProtos.Book.getDescriptor();
    }
  }
}
