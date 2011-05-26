package org.eclipselab.emf.ecore.protobuf;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Builder;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;

/**
 * 
 * 
 * @author Christian Kerl
 */
public class TypeMappingResult {
	public static TypeMappingResult forDataType(Type type) {
		return new TypeMappingResult(null, type);
	}
	
	public static TypeMappingResult forEnumType(String name) {
		return new TypeMappingResult(name, Type.TYPE_ENUM);
	}
	
	public static TypeMappingResult forClassType(String name) {
		return new TypeMappingResult(name, Type.TYPE_MESSAGE);
	}
	
	private final String protobufTypeName;
	private final Type protobufType;
		
	public TypeMappingResult(String protobufTypeName, Type protobufType) {
		super();
		this.protobufTypeName = protobufTypeName;
		this.protobufType = protobufType;
	}
	
	public Builder apply(Builder field) {
		if(protobufTypeName != null) {
			field.setTypeName(protobufTypeName);
		}
		if(protobufType != null) {
			field.setType(protobufType);
		}
		return field;
	}
}
