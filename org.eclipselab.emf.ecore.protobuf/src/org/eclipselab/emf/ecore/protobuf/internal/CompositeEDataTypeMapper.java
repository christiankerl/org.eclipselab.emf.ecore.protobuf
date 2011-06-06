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
package org.eclipselab.emf.ecore.protobuf.internal;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EDataType;
import org.eclipselab.emf.ecore.protobuf.EDataTypeMapper;
import org.eclipselab.emf.ecore.protobuf.TypeMappingResult;


/**
 * @author Christian Kerl
 */
public class CompositeEDataTypeMapper implements EDataTypeMapper
{

  public static CompositeEDataTypeMapper create(EDataTypeMapper... mappers)
  {
    return new CompositeEDataTypeMapper(Arrays.asList(mappers));
  }

  private final List<EDataTypeMapper> mappers;
  private EDataTypeMapper currentMapper;

  public CompositeEDataTypeMapper(List<EDataTypeMapper> mappers)
  {
    super();
    this.mappers = mappers;
  }

  /* (non-Javadoc)
   * @see org.eclipselab.emf.ecore.protobuf.EDataTypeMapper#supports(org.eclipse.emf.ecore.EDataType)
   */
  @Override
  public boolean supports(EDataType type)
  {
    currentMapper = null;

    for (EDataTypeMapper mapper : mappers)
    {
      if (mapper.supports(type))
      {
        currentMapper = mapper;

        return true;
      }
    }

    return false;
  }

  /* (non-Javadoc)
   * @see org.eclipselab.emf.ecore.protobuf.EDataTypeMapper#map(org.eclipse.emf.ecore.EDataType)
   */
  @Override
  public TypeMappingResult map(EDataType type)
  {
    if (currentMapper == null && !supports(type))
    {
      throw new IllegalArgumentException();
    }

    return currentMapper.map(type);
  }

}
