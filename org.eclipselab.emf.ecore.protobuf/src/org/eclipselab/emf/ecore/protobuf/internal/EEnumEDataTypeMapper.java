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

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipselab.emf.ecore.protobuf.EDataTypeMapper;
import org.eclipselab.emf.ecore.protobuf.TypeMappingResult;


/**
 * 
 * 
 * @author Christian Kerl
 */
public class EEnumEDataTypeMapper implements EDataTypeMapper
{

  /* (non-Javadoc)
   * @see org.eclipselab.emf.ecore.protobuf.EDataTypeMapper#supports(org.eclipse.emf.ecore.EDataType)
   */
  @Override
  public boolean supports(EDataType type)
  {
    return type instanceof EEnum;
  }

  /* (non-Javadoc)
   * @see org.eclipselab.emf.ecore.protobuf.EDataTypeMapper#map(org.eclipse.emf.ecore.EDataType)
   */
  @Override
  public TypeMappingResult map(EDataType type)
  {
    return TypeMappingResult.forEnumType(type.getName());
  }
}
