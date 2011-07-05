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

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;

public class ProtobufUtil
{

  public static Object getFirstFieldValue(Message pbMessage)
  {
    if(pbMessage.getAllFields().isEmpty())
    {
      throw new IllegalArgumentException("The DynamicMessage is empty!");
    }
    
    return pbMessage.getAllFields().entrySet().iterator().next().getValue();
  }
  
  public static Descriptors.Descriptor getMessageTypeByName(Descriptors.Descriptor pbMessage, String fullyQualifiedName)
  {
    return getMessageTypeByName(pbMessage.getFile(), fullyQualifiedName);
  }
  
  public static Descriptors.Descriptor getMessageTypeByName(Descriptors.FileDescriptor pbPackage, String fullyQualifiedName)
  {
    String[] elements = fullyQualifiedName.split("\\.");
    
    if(!pbPackage.getPackage().equals(elements[1]))
    {
      for(Descriptors.FileDescriptor pbDependency : pbPackage.getDependencies())
      {
        if(pbDependency.getPackage().equals(elements[1]))
        {
          pbPackage = pbDependency;
          break;
        }
      }
    }
    
    return pbPackage.findMessageTypeByName(elements[2]);
  }
}
