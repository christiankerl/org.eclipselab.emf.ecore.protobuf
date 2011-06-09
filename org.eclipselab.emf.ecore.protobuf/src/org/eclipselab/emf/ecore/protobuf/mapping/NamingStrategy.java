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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;


/**
 * NamingStrategy provides methods to determine the name of
 * certain ProtoBuf fields and messages.
 * 
 * @author Christian Kerl
 */
public interface NamingStrategy
{
  String getInternalIdField();

  String getSubTypeField();

  String getSubTypeEnum();

  String getMessage(EClass eClass);

  String getEnum(EEnum eEnum);

  boolean isRefMessage(String name);

  String getRefMessage();
  
  String getRefMessage(EClass eClass);

  String getQualifiedMessage(EClass eClass);
  String getQualifiedRefMessage(EClass eClass);
  
  String getRefMessageField(EClass eClass);
  String getRefMessageExtensionField(EClass eSuperClass, EClass eClass);
}
