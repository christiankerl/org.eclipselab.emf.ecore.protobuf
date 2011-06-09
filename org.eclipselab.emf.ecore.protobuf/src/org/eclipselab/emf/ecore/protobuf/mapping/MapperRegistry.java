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
package org.eclipselab.emf.ecore.protobuf.mapping;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipselab.emf.ecore.protobuf.internal.mapping.DefaultEDataTypeMapperImpl;
import org.eclipselab.emf.ecore.protobuf.internal.mapping.EClassMapperImpl;
import org.eclipselab.emf.ecore.protobuf.internal.mapping.EEnumMapperImpl;
import org.eclipselab.emf.ecore.protobuf.internal.mapping.EPackageMapperImpl;
import org.eclipselab.emf.ecore.protobuf.internal.mapping.EcoreEDataTypeMapperImpl;

public class MapperRegistry 
{
  public static interface Element<Type>
  {
    boolean supports(Type type);
  }

  private final EPackageMapper ePackageMapper = new EPackageMapperImpl(this);  
  private final List<EClassifierMapper> eClassifierMappers = new ArrayList<EClassifierMapper>();
  
  public MapperRegistry(NamingStrategy naming)
  {
    eClassifierMappers.add(new EClassMapperImpl(this, naming));
    eClassifierMappers.add(new EEnumMapperImpl(naming));
    eClassifierMappers.add(new EcoreEDataTypeMapperImpl());
    eClassifierMappers.add(new DefaultEDataTypeMapperImpl());
  }
  
  public EPackageMapper find(EPackage ePackage)
  {
    return ePackageMapper;
  }
  
  public EClassifierMapper find(EClassifier eClassifier)
  {
    for(EClassifierMapper mapper : eClassifierMappers)
    {
      if(mapper.supports(eClassifier))
      {
        return mapper;
      }
    }

    throw new IllegalArgumentException();
  }
  
  
}