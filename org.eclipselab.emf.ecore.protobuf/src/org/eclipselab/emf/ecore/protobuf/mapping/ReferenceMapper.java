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
 * ReferenceMapper maps a reference to SourceType to its representation in the ContextType.
 * 
 * @author Christian Kerl
 */
public interface ReferenceMapper<SourceType, ContextType> extends MapperRegistry.Element<SourceType> 
{    
  void mapReference(SourceType source, ContextType context);
}