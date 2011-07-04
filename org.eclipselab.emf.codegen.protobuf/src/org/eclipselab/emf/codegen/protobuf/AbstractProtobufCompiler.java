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

import java.io.File;
import java.io.IOException;

public abstract class AbstractProtobufCompiler implements ProtobufCompiler
{ 
  public abstract String getExecutable();
  
  @Override
  public boolean isAvailable()
  {
    return new File(getExecutable()).exists();
  }

  @Override
  public void invoke(String workingDirectory, String protoFile, String outputDirectory)
  {
    File fWorkingDirectory = new File(workingDirectory);
    
    ProcessBuilder builder = new ProcessBuilder();
    builder.directory(fWorkingDirectory);
    builder.command(getExecutable(), "--java_out", outputDirectory, protoFile);
    
    try
    {
      Process process = builder.start();
      
      process.waitFor();
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
    catch (InterruptedException e)
    {
      throw new RuntimeException(e);
    }
  }
}
