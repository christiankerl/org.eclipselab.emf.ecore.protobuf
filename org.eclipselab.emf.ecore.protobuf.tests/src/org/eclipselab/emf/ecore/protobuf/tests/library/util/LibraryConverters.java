package org.eclipselab.emf.ecore.protobuf.tests.library.util;

import org.eclipselab.emf.ecore.protobuf.conversion.ConverterRegistry;

public class LibraryConverters
{
  public static final void register(ConverterRegistry converterRegistry)
  {    
    converterRegistry.register(new AuthorToProtobufConverter());
    converterRegistry.register(new AuthorFromProtobufConverter());
    
    converterRegistry.register(new BookToProtobufConverter());
    converterRegistry.register(new BookFromProtobufConverter());
    
    converterRegistry.register(new LibraryToProtobufConverter());
    converterRegistry.register(new LibraryFromProtobufConverter()); 
  }
}

