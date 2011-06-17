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
package org.eclipselab.emf.ecore.protobuf.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;

/**
 * 
 * 
 * @author Christian Kerl
 */
public class EcoreUtil2
{
  public static EPackage getRootPackage(EClassifier eClassifier) 
  {
    return getRootPackage(eClassifier.getEPackage());
  }

  public static EPackage getRootPackage(EPackage ePackage)
  {
    EPackage eRootPackage = ePackage;
    
    while(eRootPackage.getESuperPackage() != null)
    {
      eRootPackage = eRootPackage.getESuperPackage();
    }
    
    return eRootPackage;
  }
  
  public static List<EClassifier> getAllClassifiersFromPackageHierarchy(EPackage eRootPackage)
  {
    List<EClassifier> result = new ArrayList<EClassifier>();
    Queue<EPackage> ePackages = new ArrayDeque<EPackage>();
    ePackages.add(eRootPackage);
    
    while(!ePackages.isEmpty())
    {
      EPackage ePackage = ePackages.poll();
      ePackages.addAll(ePackage.getESubpackages());
      
      result.addAll(ePackage.getEClassifiers());
    }
    
    return result;
  }
}
