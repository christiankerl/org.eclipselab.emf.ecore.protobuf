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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class EPackageDependencyAnalyzer extends AdapterImpl
{
  public static EPackageDependencyAnalyzer get(EPackage ePackage)
  {
    EPackageDependencyAnalyzer analyzer = (EPackageDependencyAnalyzer)EcoreUtil.getExistingAdapter(ePackage, EPackageDependencyAnalyzer.class);
    
    if(analyzer == null)
    {
      analyzer = new EPackageDependencyAnalyzer();
      
      ePackage.eAdapters().add(analyzer);
    }
    
    return analyzer;
  }
  
  private List<EPackage> dependencies;

  private EPackageDependencyAnalyzer()
  {
  }
  
  @Override
  public boolean isAdapterForType(Object type)
  {
    return EPackageDependencyAnalyzer.class.equals(type);
  }
  
  public EPackage getEPackage()
  {
    return (EPackage)getTarget();
  }

  public List<EPackage> getDependencies()
  {
    if(dependencies == null)
    {
      dependencies = new ArrayList<EPackage>(getReferencedRootPackages(getEPackage()));
    }
    
    return dependencies;
  }
  
  private static Set<EPackage> getReferencedRootPackages(EPackage ePackage)
  {
    Set<EPackage> ePackages = new HashSet<EPackage>();
    List<EClassifier> eClassifiers = EcoreUtil2.getAllClassifiersFromPackageHierarchy(ePackage);
    
    for(EClassifier eClassifier : eClassifiers)
    {
      if(eClassifier instanceof EClass)
      {
        EClass eClass = (EClass)eClassifier;
        
        for(EClass eSuperClass : eClass.getEAllSuperTypes())
        {
          ePackages.add(EcoreUtil2.getRootPackage(eSuperClass));
        }
        
        for(EReference eReference : eClass.getEAllReferences())
        {
          ePackages.add(EcoreUtil2.getRootPackage(eReference.getEReferenceType()));
        }
      }
    }
    
    ePackages.remove(ePackage);
    
    return ePackages;
  }
}