package org.eclipselab.emf.ecore.protobuf;

import java.util.Arrays;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;

/**
 * DebugStringBuilder provides an implementation of the C++ ProtoBuf *Descriptor::DebugString() methods.
 * 
 * @author Christian Kerl
 */
public class DescriptorDebugStringBuilder {

	public String build(FileDescriptor pbFile) {
		StringBuilder content = new StringBuilder();
		
		// TODO dependencies
		
		if(!pbFile.getPackage().isEmpty()) {
			content.append(String.format("package %s;\n\n", pbFile.getPackage()));
		}
		
		// TODO options
		
		for(EnumDescriptor pbEnum : pbFile.getEnumTypes()) {
			build(pbEnum, 0, content);
			content.append("\n");
		}
		
		for(Descriptor pbMessage : pbFile.getMessageTypes()) {
			content.append(String.format("message %s", pbMessage.getName()));
			build(pbMessage, 0, content);
			content.append("\n");
		}
		
		// TODO services
		// TODO extensions
		
		return content.toString();
	}

	private void build(Descriptor pbMessage, int depth, StringBuilder content) {
		String prefix = prefix(depth);
		++depth;
		
		content.append(" {\n");
		
		// TODO options
		
		for(Descriptor pbNestedMessage : pbMessage.getNestedTypes()) {
			content.append(String.format("%s message %s", prefix, pbNestedMessage.getName()));
			build(pbNestedMessage, depth, content);
		}

		for(EnumDescriptor pbEnum : pbMessage.getEnumTypes()) {
			build(pbEnum, depth, content);
		}
		
		for(FieldDescriptor pbField : pbMessage.getFields()) {
			build(pbField, depth, content);
		}
		
		// TODO extension ranges
		// TODO extensions

		content.append(String.format("%s}\n", prefix));
	}

	private void build(FieldDescriptor pbField, int depth, StringBuilder content) {
		String prefix = prefix(depth);
		String fieldType;
		
		switch(pbField.getType()) {
		case MESSAGE:
			fieldType = "." + pbField.getMessageType().getFullName();
			break;
		case ENUM:
			fieldType = "." + pbField.getEnumType().getFullName();
			break;
		default:
			fieldType = pbField.getType().name().toLowerCase();
			break;
		}
		
		String label = "";
		
		if(pbField.isRepeated()) label = "repeated";
		if(pbField.isRequired()) label = "required";
		if(pbField.isOptional()) label = "optional";
		
		content.append(String.format("%s%s %s %s = %s;", prefix, label, fieldType, pbField.getName(), pbField.getNumber()));
		
		// TODO default value
		// TODO options
		
		content.append("\n");
	}

	private void build(EnumDescriptor pbEnum, int depth, StringBuilder content) {		
		String prefix = prefix(depth);
		++depth;
		
		content.append(String.format("%senum %s {\n", prefix, pbEnum.getName()));		
		for(EnumValueDescriptor pbEnumValue : pbEnum.getValues()) {
			build(pbEnumValue, depth, content);
		}		
		content.append(String.format("%s}\n", prefix));
	}
	
	private void build(EnumValueDescriptor pbEnumValue, int depth, StringBuilder content) {
		String prefix = prefix(depth);
		content.append(String.format("%s%s = %s", prefix, pbEnumValue.getName(), pbEnumValue.getNumber()));
		
		// TODO options
		
		content.append(String.format(";\n"));
	}

	private String prefix(int depth) {
		depth *= 2;
		
		char[] result = new char[depth];
		
		Arrays.fill(result, ' ');
		
		return new String(result);
	}
	
}
