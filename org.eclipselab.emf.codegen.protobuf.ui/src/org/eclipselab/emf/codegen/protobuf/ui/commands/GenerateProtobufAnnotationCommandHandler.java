/**
 * <copyright>
 *
 * Copyright (c) 2012 Christian Kerl
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
package org.eclipselab.emf.codegen.protobuf.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipselab.emf.codegen.protobuf.annotations.EPackageAnnotation;

public class GenerateProtobufAnnotationCommandHandler extends AbstractHandler
{
  private EPackage getEPackageFromContext(Object evaluationContext)
  {
    EPackage result = null;

    if(evaluationContext instanceof EvaluationContext)
    {
      EvaluationContext ctx = (EvaluationContext)evaluationContext;
      Object defaultVariable = ctx.getDefaultVariable();
      
      if(defaultVariable instanceof List)
      {
        List<?> elements = (List<?>) defaultVariable;
        
        if(elements.size() == 1 && elements.get(0) instanceof EPackage)
        {
          result = (EPackage) elements.get(0);
        }
      }
    }
    
    return result;
  }
  
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException
  { 
    EPackage ePackage = getEPackageFromContext(event.getApplicationContext());
    EditingDomain domain = AdapterFactoryEditingDomain.getEditingDomainFor(ePackage);
    
    Command createAnnotationCommand = AddCommand.create(domain, ePackage, EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS, EPackageAnnotation.create(true));
    domain.getCommandStack().execute(createAnnotationCommand);
    
    return null;
  }
  
  @Override
  public void setEnabled(Object evaluationContext)
  {
    EPackage ePackage = getEPackageFromContext(evaluationContext);
    
    setBaseEnabled(ePackage != null && !EPackageAnnotation.get(ePackage).exists());
  }
}
