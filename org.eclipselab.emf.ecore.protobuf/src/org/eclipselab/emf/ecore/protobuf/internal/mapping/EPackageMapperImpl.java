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

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipselab.emf.ecore.protobuf.mapping.EClassifierMapper;
import org.eclipselab.emf.ecore.protobuf.mapping.EPackageMapper;
import org.eclipselab.emf.ecore.protobuf.mapping.MapperRegistry;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;

/**
 * EPackageMapperImpl provides the default {@link EPackageMapper} implementation
 * using a {@link MapperRegistry} to find {@link EClassifierMapper}s for the
 * {@link EPackage#getEClassifiers()}.
 * 
 * @author Christian Kerl
 */
public class EPackageMapperImpl implements EPackageMapper
{
  private final MapperRegistry registry;
  
  public EPackageMapperImpl(MapperRegistry registry)
  {
    super();
    this.registry = registry;
  }

  @Override
  public boolean supports(EPackage ePackage)
  {
    return true;
  }

  @Override
  public void map(EPackage ePackage, FileDescriptorSet.Builder pbPackages)
  {
    FileDescriptorProto.Builder pbPackage = pbPackages.addFileBuilder();
    pbPackage.setPackage(ePackage.getName());
    
    for(EClassifier eClassifier : ePackage.getEClassifiers())
    {
      registry
        .find(eClassifier)
        .map(eClassifier, pbPackage);
    }
  }
  
}
