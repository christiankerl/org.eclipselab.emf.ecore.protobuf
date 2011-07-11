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
package org.eclipselab.emf.ecore.protobuf.conversion;

import org.eclipse.emf.ecore.EDataType;

import com.google.protobuf.Descriptors;

/**
 * A ToProtoBufScalarConverter converts a scalar value from its Ecore representation
 * to its ProtoBuf representation.
 * 
 * @author Christian Kerl
 */
public abstract class ToProtoBufScalarConverter implements Converter<Object, EDataType, Object, Descriptors.FieldDescriptor>
{

}