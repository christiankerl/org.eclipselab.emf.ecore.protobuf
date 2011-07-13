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
package org.eclipselab.emf.ecore.protobuf.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;


/**
 * 
 * @author Christian Kerl
 */
public final class EObjectPool
{
  private final Map<EObject, Integer> objectToIdMap = new HashMap<EObject, Integer>();
  private final Map<Integer, EObject> idToObjectMap = new HashMap<Integer, EObject>();

  public final Integer getId(final EObject eObject)
  {
    Integer id = objectToIdMap.get(eObject);
    
    if(id == null)
    {
      id = objectToIdMap.size();
      objectToIdMap.put(eObject, id);
    }
    
    return id;
  }

  public final EObject getObject(EClass eClass, Integer id)
  {
    EObject eObject = idToObjectMap.get(id);
    
    if (eObject == null)
    {
      eObject = EcoreUtil.create(eClass);
      idToObjectMap.put(id, eObject);
    }

    return eObject;
  }
}
