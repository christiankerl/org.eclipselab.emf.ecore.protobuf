package org.eclipselab.emf.ecore.protobuf.internal;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipselab.emf.ecore.protobuf.EDataTypeConverter;

public class DefaultEDataTypeConverter implements EDataTypeConverter {

	@Override
	public boolean supports(EDataType type) {
		return true;
	}

	@Override
	public Object convertFromProtoBuf(EDataType ecoreType, Object protobufValue) {
		return EcoreUtil.createFromString(ecoreType, (String) protobufValue);
	}

	@Override
	public Object convertToProtoBuf(EDataType ecoreType, Object ecoreValue) {
		return EcoreUtil.convertToString(ecoreType, ecoreValue);
	}
}
