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
package org.eclipselab.emf.codegen.protobuf;

import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory;
import org.eclipse.emf.common.notify.Adapter;

/**
 * @author Christian Kerl
 */
public class ProtobufGeneratorAdapterFactory extends GenModelGeneratorAdapterFactory
{  
  private ProtobufPackageGeneratorAdapter genModelAdapter;

  @Override
  public Adapter createGenPackageAdapter() { return null; }
  
  @Override
  public Adapter createGenClassAdapter() { return null; }
  
  @Override
  public Adapter createGenEnumAdapter() { return null; }
  
  @Override
  public Adapter createGenModelAdapter()
  {
    if(genModelAdapter == null) {
      genModelAdapter = new ProtobufPackageGeneratorAdapter(this);
    }
    
    return genModelAdapter;
  }
}
