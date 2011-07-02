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
package org.eclipselab.emf.ecore.protobuf.tests;

import java.util.Arrays;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;


/**
 * EPackageBuilder provides utility methods to easily create {@link EPackage} programmatically.
 * 
 * @author Christian Kerl
 */
public class EPackageBuilder
{
  public static class EStructuralFeatureBuilder
  {

    private EClassBuilder parent;
    private EStructuralFeature feature;

    public EStructuralFeatureBuilder(EClassBuilder eClassBuilder, String name)
    {
      parent = eClassBuilder;

      feature = parent.parent.factory.createEAttribute();
      feature.setName(name);
      feature.setLowerBound(1);
      feature.setUpperBound(1);
    }

    public EStructuralFeatureBuilder referencing()
    {
      EReference reference = parent.parent.factory.createEReference();
      reference.setName(feature.getName());
      reference.setLowerBound(feature.getLowerBound());
      reference.setUpperBound(feature.getUpperBound());
      reference.setContainment(false);

      feature = reference;

      return this;
    }

    public EStructuralFeatureBuilder containing()
    {
      EReference reference = parent.parent.factory.createEReference();
      reference.setName(feature.getName());
      reference.setLowerBound(feature.getLowerBound());
      reference.setUpperBound(feature.getUpperBound());
      reference.setContainment(true);

      feature = reference;

      return this;
    }

    public EStructuralFeatureBuilder ofType(String eClassName)
    {
      feature.setEType(parent.parent.epackage.getEClassifier(eClassName));

      return this;
    }

    public EStructuralFeatureBuilder ofType(EDataType eDataType)
    {
      feature.setEType(eDataType);

      return this;
    }

    public EStructuralFeatureBuilder many()
    {
      feature.setUpperBound(-1);

      return this;
    }

    public EStructuralFeatureBuilder one()
    {
      feature.setUpperBound(1);

      return this;
    }

    public EStructuralFeatureBuilder maybe()
    {
      feature.setLowerBound(0);
      
      return this;
    }
    
    public EClassBuilder end()
    {
      parent.eclass.getEStructuralFeatures().add(feature);

      return parent;
    }
  }

  public static class EClassBuilder
  {
    private EPackageBuilder parent;
    private EClass eclass;

    public EClassBuilder(EPackageBuilder ePackageBuilder, String eClassName)
    {
      parent = ePackageBuilder;

      eclass = parent.factory.createEClass();
      eclass.setName(eClassName);
    }

    public EClassBuilder subclassing(String... parentEClasses)
    {
      for (String parentEClass : Arrays.asList(parentEClasses))
      {
        eclass.getESuperTypes().add((EClass)parent.epackage.getEClassifier(parentEClass));
      }

      return this;
    }

    public EClassBuilder beingInterface()
    {
      eclass.setInterface(true);

      return beingAbstract();
    }

    public EClassBuilder beingAbstract()
    {
      eclass.setAbstract(true);

      return this;
    }

    public EStructuralFeatureBuilder having(String name)
    {
      return new EStructuralFeatureBuilder(this, name);
    }

    public EPackageBuilder end()
    {
      parent.epackage.getEClassifiers().add(eclass);

      return parent;
    }
  }

  public static EPackageBuilder create(String pkg)
  {
    return new EPackageBuilder(pkg);
  }

  private EcoreFactory factory = EcoreFactory.eINSTANCE;
  private EPackage epackage;

  public EPackageBuilder(String pkgName)
  {
    epackage = factory.createEPackage();
    epackage.setName(pkgName);
    epackage.setNsPrefix(pkgName);
    epackage.setNsURI(String.format("http://eclipselab.org/%s/", pkgName));
  }

  public EClassBuilder withClass(String eClassName)
  {
    return new EClassBuilder(this, eClassName);
  }

  public EPackage get()
  {
    return epackage;
  }
}
