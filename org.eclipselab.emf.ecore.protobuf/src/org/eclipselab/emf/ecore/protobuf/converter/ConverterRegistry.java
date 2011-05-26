package org.eclipselab.emf.ecore.protobuf.converter;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EDataType;

import com.google.protobuf.Descriptors;

public class ConverterRegistry {

	private List<Converter.ToProtoBufScalarConverter> toProtoBufScalarConverters = Arrays.asList(
			new EnumConverter.ToProtoBuf(),
			new EcoreScalarConverter.ToProtoBuf(),
			new DefaultScalarConverter.ToProtoBuf()
	);

	private List<Converter.FromProtoBufScalarConverter> fromProtoBufScalarConverters = Arrays.asList(
			new EnumConverter.FromProtoBuf(),
			new EcoreScalarConverter.FromProtoBuf(),
			new DefaultScalarConverter.FromProtoBuf()
	);
	
	public Converter.ToProtoBufScalarConverter find(EDataType sourceType, Descriptors.FieldDescriptor targetType) {
		for(Converter.ToProtoBufScalarConverter converter : toProtoBufScalarConverters) {
			if(converter.supports(sourceType, targetType)) {
				return converter;
			}
		}
		
		throw new IllegalArgumentException();
	}
	
	public Converter.FromProtoBufScalarConverter find(Descriptors.FieldDescriptor sourceType, EDataType targetType) {
		for(Converter.FromProtoBufScalarConverter converter : fromProtoBufScalarConverters) {
			if(converter.supports(sourceType, targetType)) {
				return converter;
			}
		}
		
		throw new IllegalArgumentException();
	}
}
