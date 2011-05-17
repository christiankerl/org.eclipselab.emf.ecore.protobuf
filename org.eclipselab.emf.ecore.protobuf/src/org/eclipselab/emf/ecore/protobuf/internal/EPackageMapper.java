package org.eclipselab.emf.ecore.protobuf.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipselab.emf.ecore.protobuf.EDataTypeMapper;

import com.google.protobuf.DescriptorProtos;

/**
 * EPackageMapper maps an {@link EPackage Ecore package} to a {@link DescriptorProtos.FileDescriptorProto ProtoBuf package} definition.
 * 
 * @author Christian Kerl
 */
public class EPackageMapper {
	private static final String REF_CLASS_SUFFIX = "Ref";
	private static final String REF_CLASS_TYPE_ENUM = "SubType";
	private static final String REF_CLASS_TYPE_FIELD = "sub_type";

	private EDataTypeMapper eDataTypeMapper = new EcoreEDataTypeMapper();
	
	private DescriptorProtos.FileDescriptorProto.Builder pbPackage;
	
	private Map<EClass, DescriptorProtos.DescriptorProto.Builder> refClasses = new HashMap<EClass, DescriptorProtos.DescriptorProto.Builder>();
	private Set<String> usedRefClasses = new HashSet<String>();
	
	public DescriptorProtos.FileDescriptorProto map(EPackage ePackage) {
		refClasses.clear();
		usedRefClasses.clear();
		
		pbPackage = DescriptorProtos.FileDescriptorProto.newBuilder();
		pbPackage.setPackage(ePackage.getName());
		
		for(EClassifier classifier : ePackage.getEClassifiers()) {
			if(classifier instanceof EClass) {
				EClass eClass = (EClass) classifier;
				
				getRefClass(eClass);
				
				if(eClass.isAbstract()) {
					continue;
				}
				
				mapClass(eClass, pbPackage.addMessageTypeBuilder());
			}
			
			if(classifier instanceof EEnum) {
				mapEnum((EEnum) classifier, pbPackage.addEnumTypeBuilder());
			}
		}
		
		removeUnusedRefClasses();
		
		return pbPackage.build();
	}

	private String getRefClassName(EClass eClass) {
		String name = eClass.getName() + REF_CLASS_SUFFIX;
		
		usedRefClasses.add(name);
		
		return name;
	}

	private DescriptorProtos.DescriptorProto.Builder getRefClass(EClass eClass) {
		if(!refClasses.containsKey(eClass)) {
			DescriptorProtos.DescriptorProto.Builder pbRefClass = pbPackage.addMessageTypeBuilder();
			pbRefClass.setName(eClass.getName() + REF_CLASS_SUFFIX);
			pbRefClass.addEnumTypeBuilder()
				.setName(REF_CLASS_TYPE_ENUM);
			
			pbRefClass.addFieldBuilder()
				.setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_REQUIRED)
				.setTypeName(REF_CLASS_TYPE_ENUM)
				.setName(REF_CLASS_TYPE_FIELD)
				.setNumber(1);
			
			if(!eClass.isAbstract()) {
				addClassToRefClass(pbRefClass, eClass);
				
				for(EClass eSuperClass : eClass.getEAllSuperTypes()) {
					addClassToRefClass(getRefClass(eSuperClass), eClass);
				}
			}
			
			refClasses.put(eClass, pbRefClass);
		}
		
		return refClasses.get(eClass);
	}

	private void addClassToRefClass(DescriptorProtos.DescriptorProto.Builder pbRefClass, EClass eClass) {
		DescriptorProtos.EnumDescriptorProto.Builder pbRefTypeEnum = pbRefClass.getEnumTypeBuilder(0);
		
		pbRefTypeEnum.addValueBuilder()
			.setName(eClass.getName())
			.setNumber(pbRefTypeEnum.getValueCount());
		
		pbRefClass.addFieldBuilder()
			.setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL)
			.setTypeName(eClass.getName())
			.setName(eClass.getName().toLowerCase())
			.setNumber(pbRefClass.getFieldCount());
	}
	
	private void removeUnusedRefClasses() {
		for(int idx = pbPackage.getMessageTypeCount() - 1; idx >= 0; idx--) {
			DescriptorProtos.DescriptorProtoOrBuilder pbClass = pbPackage.getMessageTypeOrBuilder(idx);
			
			if(pbClass.getName().endsWith(REF_CLASS_SUFFIX) && !usedRefClasses.contains(pbClass.getName())) {
				pbPackage.removeMessageType(idx);
			}
		}
	}
	
	private void mapClass(EClass eClass, DescriptorProtos.DescriptorProto.Builder pbClass) {
		pbClass.setName(eClass.getName());
		
		int fieldNumber = 1;
		
		for(EAttribute attribute : eClass.getEAllAttributes()) {
			if(shouldIgnoreFeature(attribute)) {
				continue;
			}
			
			pbClass.addFieldBuilder()
				.setLabel(getFieldLabel(attribute))
				.setType(eDataTypeMapper.map(attribute.getEAttributeType()))
				.setName(attribute.getName())
				.setNumber(fieldNumber++);
		}
		
		for(EReference reference : eClass.getEAllContainments()) {
			if(shouldIgnoreFeature(reference)) {
				continue;
			}
			
			pbClass.addFieldBuilder()
				.setLabel(getFieldLabel(reference))
				.setTypeName(getRefClassName(reference.getEReferenceType()))
				.setName(reference.getName())
				.setNumber(fieldNumber++);
		}
	}

	private void mapEnum(EEnum eEnum, DescriptorProtos.EnumDescriptorProto.Builder pbEnum) {
		pbEnum.setName(eEnum.getName());
		
		for(EEnumLiteral literal : eEnum.getELiterals()) {
			pbEnum.addValueBuilder()
				.setName(literal.getName())
				.setNumber(literal.getValue());
		}
	}
	
	private boolean shouldIgnoreFeature(EStructuralFeature feature) {
		// TODO: is this right?
		return feature.isTransient() || feature.isVolatile() || feature.isDerived();
	}
	
	private DescriptorProtos.FieldDescriptorProto.Label getFieldLabel(EStructuralFeature feature) {
		if(feature.isMany()) {
			return DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
		} else if(feature.isRequired()) {
			return DescriptorProtos.FieldDescriptorProto.Label.LABEL_REQUIRED;
		} else {
			return DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL;
		}
	}
}
