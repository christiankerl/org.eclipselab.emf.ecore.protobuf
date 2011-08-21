package org.eclipselab.emf.ecore.protobuf.tests.library.util;

import com.google.protobuf.*;
import com.google.protobuf.Descriptors.*;
import org.eclipse.emf.ecore.*;
import org.eclipselab.emf.ecore.protobuf.conversion.*;
import org.eclipselab.emf.ecore.protobuf.tests.library.*; 

public class AuthorFromProtobufConverter extends FromProtoBufMessageConverter<LibraryProtos.Author, Author> implements Converter.WithRegistry
{

  @Override
  @SuppressWarnings("unchecked")
  public void setRegistry(ConverterRegistry registry)
  {
  }

  @Override
  public void setObjectPool(EObjectPool pool)
  {
    super.setObjectPool(pool);
  }
      
  @Override
  public boolean supports(Descriptor sourceType, EClass targetType)
  {
    return LibraryProtos.Author.getDescriptor() == sourceType && LibraryPackage.Literals.AUTHOR == targetType;
  }

  @Override
  public Author convert(final Descriptor sourceType, final LibraryProtos.Author source, final EClass targetType)
  {
    final Author result = (Author) pool.getObject(LibraryPackage.Literals.AUTHOR, source.getId());
    
    if(source.hasName())
    {
      result.setName((java.lang.String) source.getName());
    }
    
    return result;
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

