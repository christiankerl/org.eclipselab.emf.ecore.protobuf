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

/**
 * @author Christian Kerl
 */
public interface ProtobufCompiler
{
  /**
   * @return True, if protoc is available, false otherwise
   */
  boolean isAvailable();
  
  void invoke(String workingDirectory, String protoFile, String outputDirectory);
}
