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
package org.eclipselab.emf.ecore.protobuf.internal.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipselab.emf.ecore.protobuf.mapping.EPackageMapper;

import com.google.protobuf.DescriptorProtos;

public class CachingEPackageMapperImpl implements EPackageMapper
{
  private EPackageMapper delegate;
  private Map<EPackage, List<DescriptorProtos.FileDescriptorProto>> cache;
  
  public CachingEPackageMapperImpl(EPackageMapper delegate) 
  {
    this.delegate = delegate;
    this.cache = new HashMap<EPackage, List<DescriptorProtos.FileDescriptorProto>>();
  }
  
  @Override
  public boolean supports(EPackage type)
  {
    return delegate.supports(type);
  }

  @Override
  public void map(EPackage source, DescriptorProtos.FileDescriptorSet.Builder context)
  {
    if(!cache.containsKey(source)) {
      delegate.map(source, context);
      
      cache.put(source, context.getFileList());
    } else {
      context.addAllFile(cache.get(source));
    }
  }


}
