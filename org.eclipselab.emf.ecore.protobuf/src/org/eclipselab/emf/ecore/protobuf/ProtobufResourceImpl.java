package org.eclipselab.emf.ecore.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipselab.emf.ecore.protobuf.internal.EPackageMapper;
import org.eclipselab.emf.ecore.protobuf.internal.EcoreEDataTypeConverter;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.DynamicMessage;

/**
 * A {@link Resource} implementation providing serialization in Google's Protocol Buffer format.
 */
public class ProtobufResourceImpl extends ResourceImpl {	
	private static final Descriptors.FileDescriptor[] NO_DEPENDENCIES = new Descriptors.FileDescriptor[0];
	
	private EDataTypeConverter converter = new EcoreEDataTypeConverter();
	
	private EPackageMapper ePackageMapper = new EPackageMapper();
	
	private int lastEObjectId = 1;
	private Map<EObject, Integer> eObjectIds = new HashMap<EObject, Integer>();
	
	private Integer getEObjectId(EObject eObject) {
		if(!eObjectIds.containsKey(eObject)) {
			eObjectIds.put(eObject, lastEObjectId++);
		}
		
		return eObjectIds.get(eObject);
	}
	
	private Map<Integer, EObject> eObjects = new HashMap<Integer, EObject>();
	
	public ProtobufResourceImpl() {
		super();
	}

	public ProtobufResourceImpl(URI uri) {
		super(uri);
	}

	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		EcoreProtos.EResourceProto.Builder resource = EcoreProtos.EResourceProto.newBuilder();
		
		Map<EPackage, Descriptors.FileDescriptor> ePackages = new HashMap<EPackage, Descriptors.FileDescriptor>();
				
		for(EObject eObject : getContents()) {
			EClass eClass = eObject.eClass();
			
			if(!ePackages.containsKey(eClass.getEPackage())) {
				DescriptorProtos.FileDescriptorProto pbPackage = ePackageMapper.map(eClass.getEPackage());
				
				resource.addEpackageBuilder()
					.setUri(eClass.getEPackage().getNsURI())
					.setDefinition(pbPackage);
				
				try {
					ePackages.put(eClass.getEPackage(), Descriptors.FileDescriptor.buildFrom(pbPackage, NO_DEPENDENCIES));
				} catch (DescriptorValidationException e) {
					throw new IOWrappedException(e);
				}
			}
			
			Descriptors.FileDescriptor pbPackage = ePackages.get(eClass.getEPackage());
			Descriptors.Descriptor pbClass = pbPackage.findMessageTypeByName(eClass.getName());
			
			resource.addEobjectBuilder()
				.setEpackageIndex(0)
				.setEclassIndex(pbClass.getIndex())
				.setData(convertToProtoBuf(eObject, pbClass).toByteString());
		}
		
		outputStream.write(resource.build().toByteArray());
	}
	
	private DynamicMessage convertToProtoBuf(EObject eObject, Descriptors.Descriptor pbClass) {
		EClass eClass = eObject.eClass();
		
		DynamicMessage.Builder pbData = DynamicMessage.newBuilder(pbClass);
		Descriptors.FieldDescriptor pbIdField = pbClass.findFieldByName(EPackageMapper.INTERNAL_ID_FIELD);
		
		pbData.setField(pbIdField, getEObjectId(eObject));
		
		for(EAttribute attribute : eClass.getEAllAttributes()) {
			if(!eObject.eIsSet(attribute)) continue;
			
			Descriptors.FieldDescriptor field = pbClass.findFieldByName(attribute.getName());
			
			if(attribute.isMany()) {
				List<Object> pbPrimitiveData = new ArrayList<Object>();
				for(Object primitiveData : (Collection<Object>) eObject.eGet(attribute)) {
					pbPrimitiveData.add(converter.convertToProtoBuf(attribute.getEAttributeType(), primitiveData));
				}
				pbData.setField(field, pbPrimitiveData);
			} else {
				pbData.setField(
					field, 
					converter.convertToProtoBuf(attribute.getEAttributeType(), eObject.eGet(attribute))
				);
			}
		}
		
		for(EReference reference : eClass.getEAllReferences()) {
			if(!eObject.eIsSet(reference)) continue;
			
			Descriptors.FieldDescriptor field = pbClass.findFieldByName(reference.getName());
			
			if(reference.isMany()) {
				List<DynamicMessage> pbContainedObjects = new ArrayList<DynamicMessage>();
				for(EObject eContainedObject : (Collection<EObject>) eObject.eGet(reference)) {
					pbContainedObjects.add(convertToProtoBufRef(eContainedObject, field.getMessageType(), reference.isContainment()));
				}
				pbData.setField(field, pbContainedObjects);
			} else {
				pbData.setField(
					field, 
					convertToProtoBufRef((EObject) eObject.eGet(reference), field.getMessageType(), reference.isContainment())
				);
			}
		}
		
		return pbData.build();
	}
	
	private DynamicMessage convertToProtoBufRef(EObject eObject, Descriptors.Descriptor pbRefClass, boolean containment) {
		String eClassName = eObject.eClass().getName();
		Descriptors.EnumDescriptor subTypeEnum = pbRefClass.getEnumTypes().get(0);
		
		DynamicMessage.Builder pbData = DynamicMessage.newBuilder(pbRefClass);
		pbData.setField(pbRefClass.findFieldByName("sub_type"), subTypeEnum.findValueByName(eClassName));
		
		if(containment) {
			pbData.setField(pbRefClass.findFieldByName(eClassName.toLowerCase()), convertToProtoBuf(eObject, pbRefClass.getFile().findMessageTypeByName(eClassName)));
		} else {
			pbData.setField(pbRefClass.findFieldByName(EPackageMapper.INTERNAL_ID_FIELD), getEObjectId(eObject));
		}
		
		return pbData.build();
	}
	
	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		EcoreProtos.EResourceProto resource = EcoreProtos.EResourceProto.parseFrom(inputStream);
		
		for(EcoreProtos.EObjectProto pbObject : resource.getEobjectList()) {
			EcoreProtos.EPackageProto pbEpackage = resource.getEpackage(pbObject.getEpackageIndex());
			EPackage ePackage = getResourceSet().getPackageRegistry().getEPackage(pbEpackage.getUri());
			
			Descriptors.FileDescriptor pbPackage;
			
			try {
				pbPackage = Descriptors.FileDescriptor.buildFrom(pbEpackage.getDefinition(), NO_DEPENDENCIES);
			} catch (DescriptorValidationException e) {
				throw new IOWrappedException(e);
			}
			
			Descriptors.Descriptor pbClass = pbPackage.getMessageTypes().get(pbObject.getEclassIndex());
			DynamicMessage pbData = DynamicMessage.parseFrom(pbClass, pbObject.getData());
			EClass eClass = (EClass) ePackage.getEClassifier(pbClass.getName());
			
			getContents().add(convertFromProtoBuf(pbData, eClass));
		}
	}
	
	private EObject convertFromProtoBuf(DynamicMessage pbData, EClass eClass) {
		EObject eObject = null;
		
		Iterator<Map.Entry<Descriptors.FieldDescriptor, Object>> it = pbData.getAllFields().entrySet().iterator();
		
		Map.Entry<Descriptors.FieldDescriptor, Object> firstField = it.next();
		
		if(!firstField.getKey().getName().equals(EPackageMapper.INTERNAL_ID_FIELD)) {
			throw new IllegalArgumentException();
		}
		
		Integer eObjectId = (Integer) firstField.getValue();
		
		if(!eObjects.containsKey(eObjectId)) {
			eObject = EcoreUtil.create(eClass);
			eObjects.put(eObjectId, eObject);
		} else {
			eObject = eObjects.get(eObjectId);
		}
		
		while(it.hasNext()) {
			Map.Entry<Descriptors.FieldDescriptor, Object> field = it.next();
			EStructuralFeature eFeature = eClass.getEStructuralFeature(field.getKey().getName());
			
			if(eFeature instanceof EAttribute) {
				EAttribute attribute = (EAttribute) eFeature;
				
				if(attribute.isMany()) {
					List<Object> pbAttributeContents = (List<Object>) field.getValue();
					List<Object> eAttributeContents = (List<Object>) eObject.eGet(attribute);
					
					for(Object pbAttributeContent : pbAttributeContents) {
						eAttributeContents.add(converter.convertFromProtoBuf(attribute.getEAttributeType(), pbAttributeContent));
					}
				} else {
					eObject.eSet(attribute, converter.convertFromProtoBuf(attribute.getEAttributeType(), field.getValue()));
				}
			} else {
				EReference reference = (EReference) eFeature;
				
				if(reference.isMany()) {
					List<DynamicMessage> pbRefObjects = (List<DynamicMessage>) field.getValue();
					List<EObject> eContainedObjects = (List<EObject>) eObject.eGet(reference);
					
					for(DynamicMessage pbRefObject : pbRefObjects) {
						eContainedObjects.add(convertFromProtoBufRef(pbRefObject, reference.getEReferenceType()));
					}
				} else {
					eObject.eSet(reference, convertFromProtoBufRef((DynamicMessage) field.getValue(), reference.getEReferenceType()));
				}
			}
		}
		
		return eObject;
	}

	private EObject convertFromProtoBufRef(DynamicMessage pbRefData, EClass eRefClass) {
		Descriptors.Descriptor pbRefClass = pbRefData.getDescriptorForType();
		
		String eClassName = ((Descriptors.EnumValueDescriptor) pbRefData.getField(pbRefClass.findFieldByName("sub_type"))).getName();
		EClass eClass = (EClass) eRefClass.getEPackage().getEClassifier(eClassName);
		
		EObject result = null;
		
		Descriptors.FieldDescriptor idField = pbRefClass.findFieldByName(EPackageMapper.INTERNAL_ID_FIELD);
		
		if(pbRefData.hasField(idField)) {
			Integer eObjectId = (Integer) pbRefData.getField(idField);
			
			if(eObjects.containsKey(eObjectId)) {
				result = eObjects.get(eObjectId);
			} else {
				result = EcoreUtil.create(eClass);
				eObjects.put(eObjectId, result);
			}
		} else {
			DynamicMessage pbData = (DynamicMessage) pbRefData.getField(pbRefClass.findFieldByName(eClassName.toLowerCase()));
			
			result = convertFromProtoBuf(pbData, eClass);
		}
		
		return result;
	}
}
