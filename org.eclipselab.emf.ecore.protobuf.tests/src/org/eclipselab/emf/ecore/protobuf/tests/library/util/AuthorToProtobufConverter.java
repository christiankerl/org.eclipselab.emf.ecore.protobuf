package org.eclipselab.emf.ecore.protobuf.tests.library.util;

import com.google.protobuf.Descriptors.*;
import org.eclipse.emf.ecore.*;
import org.eclipselab.emf.ecore.protobuf.conversion.*;
import org.eclipselab.emf.ecore.protobuf.tests.library.*; 

public class AuthorToProtobufConverter extends ToProtoBufMessageConverter<Author, LibraryProtos.Author> implements Converter.WithRegistry
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
  public boolean supports(EClass sourceType, Descriptor targetType)
  {
    return LibraryProtos.Author.getDescriptor() == targetType && LibraryPackage.Literals.AUTHOR == sourceType;
  }

  @Override
  public LibraryProtos.Author convert(final EClass sourceType, final Author source, final Descriptor targetType)
  {
    final LibraryProtos.Author.Builder result = LibraryProtos.Author.newBuilder();
    result.setId(pool.getId(source));
    
    if(source.eIsSet(LibraryPackage.Literals.AUTHOR__NAME))
    {
      result.setName(source.getName());
    }
    
    return result.build();
  }

  @Override
  protected Descriptor getTargetType(EClass sourceType)
  {
    return LibraryProtos.Author.getDescriptor();
  }
}

