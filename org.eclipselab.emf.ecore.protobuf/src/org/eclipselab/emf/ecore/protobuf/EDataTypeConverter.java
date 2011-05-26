package org.eclipselab.emf.ecore.protobuf;

import org.eclipse.emf.ecore.EDataType;


public interface EDataTypeConverter {
	
	boolean supports(EDataType type);
	
	Object convertFromProtoBuf(EDataType ecoreType, Object protobufValue);
	
	Object convertToProtoBuf(EDataType ecoreType, Object ecoreValue);
}
