package org.eclipselab.emf.ecore.protobuf.converter;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;

import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;

/**
 * EnumConverter provides {@link Converter.FromProtoBufScalarConverter} and
 * {@link Converter.ToProtoBufScalarConverter} implementations for arbitrary
 * enums. 
 * 
 * @author Christian Kerl
 */
public class EnumConverter {
	
	public static class FromProtoBuf extends Converter.FromProtoBufScalarConverter {
		@Override
		public boolean supports(FieldDescriptor sourceType, EDataType targetType) {
			return sourceType.getType() == Type.ENUM && targetType instanceof EEnum;
		}
	
		@Override
		public Object convert(FieldDescriptor sourceType, Object source, EDataType targetType) {
			return doConvert((EnumValueDescriptor) source, (EEnum) targetType);
		}
	
		private Object doConvert(EnumValueDescriptor source, EEnum targetType) {
			return targetType.getEEnumLiteral(source.getName()).getInstance();
		}
	}
	
	public static class ToProtoBuf extends Converter.ToProtoBufScalarConverter {
		@Override
		public boolean supports(EDataType sourceType, FieldDescriptor targetType) {
			return sourceType instanceof EEnum && targetType.getType() == Type.ENUM;
		}

		@Override
		public Object convert(EDataType sourceType, Object source, FieldDescriptor targetType) {
			return doConvert((Enumerator) source, targetType.getEnumType());
		}
		
		private Object doConvert(Enumerator source, EnumDescriptor targetType) {
			return targetType.findValueByName(source.getName());
		}
	}
}
