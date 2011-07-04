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
package org.eclipselab.emf.codegen.protobuf.internal;

import org.eclipse.core.runtime.Plugin;
import org.eclipselab.emf.codegen.protobuf.ProtobufCompiler;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class ProtobufGeneratorPlugin extends Plugin
{
  public static ProtobufCompiler getProtobufCompiler()
  {
    ProtobufCompiler result;
    
    if(instance != null && !instance.compiler.isEmpty())
    {
      result = instance.compiler.getService();
    }
    else
    {
      result = new DefaultProtobufCompiler();
    }
    
    return result;
  }
  
  private static ProtobufGeneratorPlugin instance;
  private ServiceTracker<ProtobufCompiler, ProtobufCompiler> compiler;
    
  @Override
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    instance = this;
    compiler = new ServiceTracker<ProtobufCompiler, ProtobufCompiler>(context, ProtobufCompiler.class, null);
    compiler.open();
  }
  
  @Override
  public void stop(BundleContext context) throws Exception
  {
    compiler.close();
    instance = null;
    super.stop(context);
  }
}
