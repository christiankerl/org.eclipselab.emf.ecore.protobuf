/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipselab.emf.ecore.protobuf.tests.library.util;

import org.eclipse.emf.common.util.URI;
import org.eclipselab.emf.ecore.protobuf.ProtobufResourceImpl;
import org.eclipselab.emf.ecore.protobuf.conversion.ConverterRegistry;
import org.eclipselab.emf.ecore.protobuf.mapping.MapperRegistry;


/**
 * <!-- begin-user-doc -->
 * The <b>Resource </b> associated with the package.
 * <!-- end-user-doc -->
 * @see org.eclipselab.emf.ecore.protobuf.tests.library.util.LibraryResourceFactoryImpl
 * @generated NOT
 */
public class LibraryResourceImpl extends ProtobufResourceImpl
{
  /**
   * Creates an instance of the resource.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param uri the URI of the new resource.
   * @generated
   */
  public LibraryResourceImpl(URI uri)
  {
    super(uri);
  }
  
  @Override
  protected void registerMappers(MapperRegistry mapperRegistry)
  {
    super.registerMappers(mapperRegistry);
    
    LibraryPackageMapper.register(mapperRegistry);
  }
  
  @Override
  protected void registerConverters(ConverterRegistry converterRegistry)
  {
    super.registerConverters(converterRegistry);
    
    LibraryConverters.register(converterRegistry);
  }

} //LibraryResourceImpl
