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

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FileDescriptor;

public class EPackageMappingCache extends AdapterImpl
{

  public static EPackageMappingCache get(EPackage ePackage)
  {
    return (EPackageMappingCache) EcoreUtil.getExistingAdapter(ePackage, EPackageMappingCache.class);
  }

  public static EPackageMappingCache create(EPackage ePackage, Descriptors.FileDescriptor pbPackage)
  {
    EPackageMappingCache result = new EPackageMappingCache(pbPackage);
    ePackage.eAdapters().add(result);
    
    return result;
  }
  
  private Descriptors.FileDescriptor pbPackage;
  
  public EPackageMappingCache(FileDescriptor pbPackage)
  {
    this.pbPackage = pbPackage;
  }
  
  @Override
  public boolean isAdapterForType(Object type)
  {
    return EPackageMappingCache.class == type;
  }
  
  public Descriptors.FileDescriptor getDescriptor()
  {
    return pbPackage;
  }
}
