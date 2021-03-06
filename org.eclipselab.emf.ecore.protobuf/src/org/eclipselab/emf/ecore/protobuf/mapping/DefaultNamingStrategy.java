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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipselab.emf.ecore.protobuf.util.EcoreUtil2;


/**
 * DefaultNamingStrategy provides a standard implementation of {@link NamingStrategy}.
 * 
 * @author Christian Kerl
 */
public class DefaultNamingStrategy implements NamingStrategy
{

  @Override
  public String getInternalIdField()
  {
    return "__id";
  }

  @Override
  public String getMessage(EClass eClass)
  {
    return eClass.getName();
  }

  @Override
  public String getEnum(EEnum eEnum)
  {
    return eEnum.getName();
  }

  @Override
  public String getRefMessage(EClass eClass)
  {
    return getMessage(eClass) + ".Ref";
  }

  @Override
  public String getRefMessageField(EClass eClass)
  {
    String msg = getMessage(eClass);

    return msg.substring(0, 1).toLowerCase() + msg.substring(1);
  }

  @Override
  public boolean isRefMessage(String name)
  {
    return name.endsWith("Ref");
  }

  @Override
  public String getRefMessage()
  {
    return "Ref";
  }

  @Override
  public String getQualifiedMessage(EClass eClass)
  {
    String pbPackage = EcoreUtil2.getRootPackage(eClass).getName();
    String pbMessage = getMessage(eClass);
  
  return String.format(".%s.%s", pbPackage, pbMessage);
  }

  @Override
  public String getQualifiedRefMessage(EClass eClass)
  {
    String pbPackage = EcoreUtil2.getRootPackage(eClass).getName();
    String pbMessage = getRefMessage(eClass);
    
    return String.format(".%s.%s", pbPackage, pbMessage);
  }

  private Map<EClass, Map<EClass, String>> cache = new HashMap<EClass, Map<EClass,String>>();
  
  @Override
  public String getRefMessageExtensionField(EClass eSuperClass, EClass eClass)
  {
    if(!cache.containsKey(eSuperClass)) {
      cache.put(eSuperClass, new HashMap<EClass, String>());
    }
    
    Map<EClass, String> subCache = cache.get(eSuperClass);
    
    if(!subCache.containsKey(eClass)) {
      subCache.put(eClass, String.format("%s_%s", getRefMessageField(eSuperClass), getRefMessageField(eClass)));
    }
    
    return subCache.get(eClass);
  }

  @Override
  public String getQualifiedEnum(EEnum eEnum)
  {
    String pbPackage = EcoreUtil2.getRootPackage(eEnum).getName();
    String pbEnum = getEnum(eEnum);
  
    return String.format(".%s.%s", pbPackage, pbEnum);
  }
}
