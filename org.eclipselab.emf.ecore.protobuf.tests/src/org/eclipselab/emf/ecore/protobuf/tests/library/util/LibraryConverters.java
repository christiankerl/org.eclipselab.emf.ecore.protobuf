package org.eclipselab.emf.ecore.protobuf.tests.library.util;

import org.eclipselab.emf.ecore.protobuf.conversion.ConverterRegistry;

public class LibraryConverters
{
  public static final void register(ConverterRegistry converterRegistry)
  {
    converterRegistry.register(
      new LibraryToProtobufConverter()
,      new BookToProtobufConverter()
,      new AuthorToProtobufConverter()
    );

    converterRegistry.register(
      new LibraryFromProtobufConverter()
,      new BookFromProtobufConverter()
,      new AuthorFromProtobufConverter()
    );
  }
}

