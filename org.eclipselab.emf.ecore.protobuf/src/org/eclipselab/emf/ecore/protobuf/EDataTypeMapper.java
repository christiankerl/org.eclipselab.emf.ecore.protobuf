package org.eclipselab.emf.ecore.protobuf;

import org.eclipse.emf.ecore.EDataType;

import com.google.protobuf.DescriptorProtos;

/**
 * EDataTypeMapper provides a mapping from a {@link EDataType primitive Ecore type} to a {@link DescriptorProtos.FieldDescriptorProto.Type primitive ProtoBuf type}.
 * 
 * @author Christian Kerl
 */
public interface EDataTypeMapper {
	
	/**
	 * Returns true, if this mapper can provide a mapping for the given {@link EDataType}, false otherwise. 
	 * 
	 * @param type
	 * 
	 * @return
	 */
	boolean supports(EDataType type);
	
	/**
	 * Returns the primitive ProtoBuf type for the given primitive Ecore type.
	 * This method should be called only after {@link #supports(EDataType)} returned true.
	 * 
	 * @param type
	 * 
	 * @return
	 */
	DescriptorProtos.FieldDescriptorProto.Type map(EDataType type);
}
