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

import org.eclipse.emf.ecore.EClassifier;

/**
 * @author Christian Kerl
 */
public class MappingException extends RuntimeException
{
  private static final long serialVersionUID = 2265007636343206637L;

  public static MappingException causeDependencyCycle()
  {
    return new MappingException("Dependency cycle detected!");
  }

  public static MappingException causeMissingMapper(EClassifier eClassifier)
  {
    return new MappingException(String.format("Missing mapper for '%s'!", eClassifier.getName()));
  } 
  
  public MappingException(String message)
  {
    super(message);
  }

  public MappingException(Throwable cause)
  {
    super(cause);
  }

  public MappingException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
