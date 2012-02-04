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

import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.ui.IWorkbench;
import org.eclipselab.emf.codegen.protobuf.ProtobufCompiler;
import org.eclipselab.emf.codegen.protobuf.ui.preferences.ProtobufGeneratorPreferencePage;
import org.osgi.service.component.ComponentContext;

public class DelegatingProtobufCompilerImpl implements ProtobufCompiler
{
  private static final String PROTOBUF_DT_PREFERENCE_PAGE_ID = "com.google.eclipse.protobuf.Protobuf";
  
  private ProtobufCompiler delegate;
  private IWorkbench workbench;
  
  public void setWorkbench(IWorkbench workbench)
  {
    this.workbench = workbench;
  }
  
  public void activate(ComponentContext context)
  {
    IPreferenceNode protobufDtPreference = workbench.getPreferenceManager().find(PROTOBUF_DT_PREFERENCE_PAGE_ID);
    
    // is protobuf-dt installed?
    if(protobufDtPreference != null)
    {
      // remove our custom protoc config page
      workbench.getPreferenceManager().remove(ProtobufGeneratorPreferencePage.PAGE_ID);
      
      delegate = new ProtobufDtProtobufCompilerImpl();
    }
    else
    {
      delegate = new SimpleProtobufCompilerImpl();
    }
  }
  
  @Override
  public boolean isAvailable()
  {
    return delegate.isAvailable();
  }

  @Override
  public void invoke(String workingDirectory, String protoFile, String outputDirectory)
  {
    delegate.invoke(workingDirectory, protoFile, outputDirectory);
  }
}
