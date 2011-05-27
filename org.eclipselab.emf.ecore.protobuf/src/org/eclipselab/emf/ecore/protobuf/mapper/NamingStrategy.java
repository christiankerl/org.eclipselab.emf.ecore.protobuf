package org.eclipselab.emf.ecore.protobuf.mapper;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;

/**
 * NamingStrategy provides methods to determine the name of
 * certain ProtoBuf fields and messages.
 * 
 * @author Christian Kerl
 */
public interface NamingStrategy {
	
	String getInternalIdField();
	
	String getSubTypeField();
	
	String getSubTypeEnum();
	
	String getMessage(EClass eClass);
	
	String getEnum(EEnum eEnum);
	
	boolean isRefMessage(String name);
	
	String getRefMessage(EClass eClass);
	
	String getRefMessageField(EClass eClass);
}
