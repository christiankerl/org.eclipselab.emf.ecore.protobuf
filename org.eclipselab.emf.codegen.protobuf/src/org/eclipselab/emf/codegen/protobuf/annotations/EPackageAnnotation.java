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
package org.eclipselab.emf.codegen.protobuf.annotations;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;

public class EPackageAnnotation
{
  public static final String SOURCE_ID = "org.eclipselab.emf.codegen.protobuf";
  
  public static final String GENERATE_ENTRY_ID = "generate";
  
  public static EPackageAnnotation get(EPackage ePackage)
  {    
    return new EPackageAnnotation(ePackage.getEAnnotation(SOURCE_ID));
  }
  
  public static EPackageAnnotation create(EPackage ePackage, boolean generate)
  {
    EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    annotation.setSource(SOURCE_ID);
    annotation.getDetails().put(GENERATE_ENTRY_ID, Boolean.toString(generate));
    
    ePackage.getEAnnotations().add(annotation);
    
    return get(ePackage);
  }
  
  private boolean generate = false;
  private boolean exists = false;
  
  public EPackageAnnotation(EAnnotation eAnnotation)
  {
    if(eAnnotation != null)
    {
      exists = true;
      generate = Boolean.parseBoolean((eAnnotation.getDetails().get(GENERATE_ENTRY_ID)));
    }
  }
  
  public boolean generate()
  {
    return generate;
  }
  
  public boolean exists()
  {
    return exists;
  }
}
