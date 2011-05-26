package org.eclipselab.emf.ecore.protobuf.internal;

import java.util.Date;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipselab.emf.ecore.protobuf.EDataTypeConverter;

import com.google.protobuf.ByteString;

public class EcoreEDataTypeConverter implements EDataTypeConverter {
		
	@Override
	public boolean supports(EDataType type) {
		return EcorePackage.eINSTANCE == type.getEPackage();
	}

	@Override
	public Object convertFromProtoBuf(EDataType ecoreType, Object protobufValue) {
		switch(ecoreType.getClassifierID()) {
		case EcorePackage.EBYTE_ARRAY:
			return((ByteString) protobufValue).toByteArray();
		case EcorePackage.EDATE:
			return new Date((Long) protobufValue);
		default:
			return protobufValue;
		}
	}

	@Override
	public Object convertToProtoBuf(EDataType ecoreType, Object ecoreValue) {
		switch(ecoreType.getClassifierID()) {
		case EcorePackage.EBYTE_ARRAY:
			return ByteString.copyFrom((byte[]) ecoreValue);
		case EcorePackage.EDATE:
			return ((Date) ecoreValue).getTime();
		default:
			return ecoreValue;
		}
	}
}
