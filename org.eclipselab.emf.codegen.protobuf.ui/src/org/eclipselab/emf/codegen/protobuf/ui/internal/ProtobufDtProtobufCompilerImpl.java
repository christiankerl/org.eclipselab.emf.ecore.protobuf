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
package org.eclipselab.emf.codegen.protobuf.ui.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipselab.emf.codegen.protobuf.ProtobufCompiler;

public class ProtobufDtProtobufCompilerImpl implements ProtobufCompiler
{
  @Override
  public boolean isAvailable()
  {
    return true;
  }

  @Override
  public void invoke(String workingDirectory, String protoFile, String outputDirectory)
  {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    IFile file = workspace.getRoot().getFileForLocation(new Path(workingDirectory).append(protoFile));
    
    if(file != null)
    {
      // seems we don't need to do anything
    }
  }

}
