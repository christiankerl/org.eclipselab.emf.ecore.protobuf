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

/**
 * Mapper maps some SourceType to another representation, which is a child of some ContextType.
 * This allows to map the SourceType to more than one object in the target representation.
 * 
 * @author Christian Kerl
 */
public interface Mapper<SourceType, ContextType> extends MapperRegistry.Element<SourceType> 
{    
  void map(SourceType source, ContextType context);
}