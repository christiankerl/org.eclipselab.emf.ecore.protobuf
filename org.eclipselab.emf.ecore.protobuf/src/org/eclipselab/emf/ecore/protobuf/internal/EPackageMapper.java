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
import org.eclipselab.emf.ecore.protobuf.mapper.NamingStrategy;

import com.google.protobuf.DescriptorProtos;

/**
 * EPackageMapper maps an {@link EPackage Ecore package} to a {@link DescriptorProtos.FileDescriptorProto ProtoBuf package} definition.
 * 
 * @author Christian Kerl
 */
public class EPackageMapper {
	private EDataTypeMapper eDataTypeMapper = CompositeEDataTypeMapper.create(
			new EcoreEDataTypeMapper(), 
			new EEnumEDataTypeMapper(), 
			new DefaultEDataTypeMapper()
	);
	
	private DescriptorProtos.FileDescriptorProto.Builder pbPackage;
	
	private Map<EClass, DescriptorProtos.DescriptorProto.Builder> refClasses = new HashMap<EClass, DescriptorProtos.DescriptorProto.Builder>();
	private Set<String> usedRefClasses = new HashSet<String>();

	private NamingStrategy naming;
	
	public EPackageMapper(NamingStrategy naming) {
		this.naming = naming;
	}
	
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
		String name = naming.getRefMessage(eClass);
		
		usedRefClasses.add(name);
		
		return name;
	}

	private DescriptorProtos.DescriptorProto.Builder getRefClass(EClass eClass) {
		if(!refClasses.containsKey(eClass)) {
			DescriptorProtos.DescriptorProto.Builder pbRefClass = pbPackage.addMessageTypeBuilder();
			pbRefClass.setName(naming.getRefMessage(eClass));
			pbRefClass.addEnumTypeBuilder()
				.setName(naming.getSubTypeEnum());
			
			pbRefClass.addFieldBuilder()
				.setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_REQUIRED)
				.setTypeName(naming.getSubTypeEnum())
				.setName(naming.getSubTypeField())
				.setNumber(1);
			
			pbRefClass.addFieldBuilder()
				.setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL)
				.setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32)
				.setName(naming.getInternalIdField())
				.setNumber(2);
			
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
			.setName(naming.getMessage(eClass))
			.setNumber(pbRefTypeEnum.getValueCount());
		
		pbRefClass.addFieldBuilder()
			.setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL)
			.setTypeName(naming.getMessage(eClass))
			.setName(naming.getRefMessageField(eClass))
			.setNumber(pbRefClass.getFieldCount());
	}
	
	private void removeUnusedRefClasses() {
		for(int idx = pbPackage.getMessageTypeCount() - 1; idx >= 0; idx--) {
			DescriptorProtos.DescriptorProtoOrBuilder pbClass = pbPackage.getMessageTypeOrBuilder(idx);
			
			if(naming.isRefMessage(pbClass.getName()) && !usedRefClasses.contains(pbClass.getName())) {
				pbPackage.removeMessageType(idx);
			}
		}
	}
	
	private void mapClass(EClass eClass, DescriptorProtos.DescriptorProto.Builder pbClass) {
		pbClass.setName(naming.getMessage(eClass));
		
		int fieldNumber = 1;
		
		pbClass.addFieldBuilder()
			.setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_REQUIRED)
			.setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32)
			.setName(naming.getInternalIdField())
			.setNumber(fieldNumber++);
		
		for(EAttribute attribute : eClass.getEAllAttributes()) {
			if(shouldIgnoreFeature(attribute)) {
				continue;
			}
			
			if(!eDataTypeMapper.supports(attribute.getEAttributeType())) {
				// log warning
				continue;
			}
			
			DescriptorProtos.FieldDescriptorProto.Builder pbField = pbClass.addFieldBuilder()
				.setLabel(getFieldLabel(attribute))
				.setName(attribute.getName())
				.setNumber(fieldNumber++);
			
			eDataTypeMapper
				.map(attribute.getEAttributeType())
				.apply(pbField);
		}
		
		for(EReference reference : eClass.getEAllReferences()) {
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
		pbEnum.setName(naming.getEnum(eEnum));
		
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
