package org.eclipselab.emf.ecore.protobuf.util;

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
	
	private StringBuilder content;
	private int depth;
	
	public String build(FileDescriptor pbFile) {
		content = new StringBuilder();
		depth = 0;
		
		// TODO dependencies
		
		if(!pbFile.getPackage().isEmpty()) {
			append("package %s;", pbFile.getPackage()).nl().nl();
		}
		
		// TODO options
		
		for(EnumDescriptor pbEnum : pbFile.getEnumTypes()) {
			build(pbEnum).nl();
		}
		
		for(Descriptor pbMessage : pbFile.getMessageTypes()) {
			append("message %s", pbMessage.getName())
				.build(pbMessage)
			.nl();
		}
		
		// TODO services
		// TODO extensions
		
		return content.toString();
	}

	private DescriptorDebugStringBuilder build(Descriptor pbMessage) {
		append(" {").nl();
		
		// TODO options
		
		depth++;
		
		for(Descriptor pbNestedMessage : pbMessage.getNestedTypes()) {
			prefix().append("message %s", pbNestedMessage.getName())
				.build(pbNestedMessage);
		}

		for(EnumDescriptor pbEnum : pbMessage.getEnumTypes()) {
			build(pbEnum);
		}
		
		for(FieldDescriptor pbField : pbMessage.getFields()) {
			build(pbField);
		}
		
		// TODO extension ranges
		// TODO extensions

		depth--;
		
		prefix().append("}").nl();
		
		return this;
	}

	private void build(FieldDescriptor pbField) {
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
		
		prefix().append("%s %s %s = %s;", label, fieldType, pbField.getName(), pbField.getNumber());
		
		// TODO default value
		// TODO options
		
		nl();
	}

	private DescriptorDebugStringBuilder build(EnumDescriptor pbEnum) {
		prefix().append("enum %s {", pbEnum.getName()).nl();
		
		depth++;
		
		for(EnumValueDescriptor pbEnumValue : pbEnum.getValues()) {
			build(pbEnumValue);
		}
		
		depth--;
		
		prefix().append("}").nl();
		
		return this;
	}
	
	private void build(EnumValueDescriptor pbEnumValue) {
		prefix().append("%s = %s", pbEnumValue.getName(), pbEnumValue.getNumber());
		
		// TODO options
		
		append(";").nl();
	}
	
	private DescriptorDebugStringBuilder append(String str) {
		content.append(str);
		
		return this;
	}
	
	private DescriptorDebugStringBuilder append(String format, Object... args) {
		return append(String.format(format, args));
	}
	
	private DescriptorDebugStringBuilder nl() {
		return append("\n");
	}
	
	private DescriptorDebugStringBuilder prefix() {
		char[] result = new char[depth * 2];
		Arrays.fill(result, ' ');
		
		content.append(result);
		
		return this;
	}
}