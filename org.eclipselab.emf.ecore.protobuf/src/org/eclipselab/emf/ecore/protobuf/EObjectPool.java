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
package org.eclipselab.emf.ecore.protobuf;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;


/**
 * 
 * @author Christian Kerl
 */
public class EObjectPool
{
  private Integer lastId = 1;
  private Map<EObject, Integer> objectToIdMap = new HashMap<EObject, Integer>();
  private Map<Integer, EObject> idToObjectMap = new HashMap<Integer, EObject>();

  public Integer getId(EObject eObject)
  {
    if (!objectToIdMap.containsKey(eObject))
    {
      objectToIdMap.put(eObject, lastId++);
    }

    return objectToIdMap.get(eObject);
  }

  public EObject getObject(EClass eClass, Integer id)
  {
    if (!idToObjectMap.containsKey(id))
    {
      idToObjectMap.put(id, EcoreUtil.create(eClass));
    }

    return idToObjectMap.get(id);
  }
}
