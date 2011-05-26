package org.eclipselab.emf.ecore.protobuf.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipselab.emf.ecore.protobuf.EObjectPool;
import org.eclipselab.emf.ecore.protobuf.internal.EPackageMapper;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;

/**
 * DynamicToProtoBufMessageConverter provides a {@link Converter.ToProtoBufMessageConverter} implementation
 * based on ProtoBuf's {@link DynamicMessage}.
 * 
 * @author Christian Kerl
 */
public class DynamicToProtoBufMessageConverter extends Converter.ToProtoBufMessageConverter<EObject, DynamicMessage> {
	private final class ObjectConversion {
		private final EClass sourceType; 
		private final EObject source; 
		private final Descriptor targetType;
		private final DynamicMessage.Builder target;
		
		public ObjectConversion(EClass sourceType, EObject source, Descriptor targetType, DynamicMessage.Builder target) {
			super();
			this.sourceType = sourceType;
			this.source = source;
			this.targetType = targetType;
			this.target = target;
		}
		
		public DynamicMessage run() {
			createInternalIdField();
			createAttributeFields();
			createReferenceFields();
			
			return target.build();
		}
		
		private void createInternalIdField() {
			set(field(EPackageMapper.INTERNAL_ID_FIELD), pool.getId(source));
		}

		private void createAttributeFields() {
			for(EAttribute attr : sourceType.getEAllAttributes()) {
				if(!source.eIsSet(attr)) continue;
				
				EDataType attrType = attr.getEAttributeType();
				FieldDescriptor field = field(attr.getName());
				
				Converter<Object, EDataType, Object, FieldDescriptor> converter = registry.find(attrType, field);
				
				Object attrRawValue = source.eGet(attr);
				Object fieldValue;
				
				if(attr.isMany()) {
					@SuppressWarnings("unchecked")
					Collection<Object> attrValues = (Collection<Object>) attrRawValue;
					List<Object> fieldValues = new ArrayList<Object>();
					
					for(Object attrValue : attrValues) {
						fieldValues.add(converter.convert(attrType, attrValue, field));
					}
					
					fieldValue = fieldValues;
				} else {
					fieldValue = converter.convert(attrType, attrRawValue, field);
				}
				
				set(field, fieldValue);
			}
		}
		
		private void createReferenceFields() {
			for(EReference ref : sourceType.getEAllReferences()) {
				if(!source.eIsSet(ref)) continue;

				FieldDescriptor field = field(ref.getName());
				
				Object refRawValue = source.eGet(ref);
				Object fieldValue;
				
				if(ref.isMany()) {
					@SuppressWarnings("unchecked")
					Collection<EObject> refValues = (Collection<EObject>) refRawValue;
					List<DynamicMessage> fieldValues = new ArrayList<DynamicMessage>();
					
					for(EObject refValue : refValues) {
						fieldValues.add(createReference(refValue.eClass(), refValue, field.getMessageType(), ref.isContainment()));
					}
					
					fieldValue = fieldValues;
				} else {
					EObject refValue = (EObject) refRawValue;
					
					fieldValue = createReference(refValue.eClass(), refValue, field.getMessageType(), ref.isContainment());
				}
				
				set(field, fieldValue);
			}
		}
		
		private DynamicMessage createReference(EClass refSourceType, EObject refSource, Descriptors.Descriptor refTargetType, boolean containment) {
			DynamicMessage.Builder refTarget = DynamicMessage.newBuilder(refTargetType);
			
			refTarget.setField(
					refTargetType.findFieldByName("sub_type"), 
					refTargetType.getEnumTypes().get(0).findValueByName(refSourceType.getName())
			);
			
			if(containment) {
				FieldDescriptor refTargetField = refTargetType.findFieldByName(refSourceType.getName().toLowerCase());
				refTarget.setField(refTargetField, convert(refSource, refTargetField.getMessageType()));
			} else {
				refTarget.setField(refTargetType.findFieldByName(EPackageMapper.INTERNAL_ID_FIELD), pool.getId(refSource));
			}
			
			return refTarget.build();
		}
		
		private void set(FieldDescriptor field, Object value) {
			target.setField(field, value);
		}
		
		private FieldDescriptor field(String fieldName) {
			return targetType.findFieldByName(fieldName);
		}
	}
	
	private final EObjectPool pool;
	private final ConverterRegistry registry;
	
	public DynamicToProtoBufMessageConverter(EObjectPool pool, ConverterRegistry registry) {
		super();
		this.pool = pool;
		this.registry = registry;
	}

	/* (non-Javadoc)
	 * @see org.eclipselab.emf.ecore.protobuf.converter.Converter#supports(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean supports(EClass sourceType, Descriptor targetType) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipselab.emf.ecore.protobuf.converter.Converter#convert(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public DynamicMessage convert(EClass sourceType, EObject source, Descriptor targetType) {
		return new ObjectConversion(sourceType, source, targetType, DynamicMessage.newBuilder(targetType)).run();
	}
}
