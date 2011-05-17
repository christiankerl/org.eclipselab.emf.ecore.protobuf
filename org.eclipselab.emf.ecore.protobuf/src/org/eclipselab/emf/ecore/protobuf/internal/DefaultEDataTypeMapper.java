package org.eclipselab.emf.ecore.protobuf.internal;

import org.eclipse.emf.ecore.EDataType;
import org.eclipselab.emf.ecore.protobuf.EDataTypeMapper;

import com.google.protobuf.DescriptorProtos;

/**
 * Default {@link EDataTypeMapper} implementation providing a fallback mapping to string. 
 * 
 * @author Christian Kerl
 */
public class DefaultEDataTypeMapper implements EDataTypeMapper {

	/* (non-Javadoc)
	 * @see org.eclipselab.emf.ecore.protobuf.EDataTypeMapper#supports(org.eclipse.emf.ecore.EDataType)
	 */
	@Override
	public boolean supports(EDataType type) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipselab.emf.ecore.protobuf.EDataTypeMapper#map(org.eclipse.emf.ecore.EDataType)
	 */
	@Override
	public DescriptorProtos.FieldDescriptorProto.Type map(EDataType type) {
		return DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING;
	}
}
