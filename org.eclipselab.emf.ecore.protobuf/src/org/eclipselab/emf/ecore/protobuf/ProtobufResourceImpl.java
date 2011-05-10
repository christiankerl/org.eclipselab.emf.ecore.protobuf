package org.eclipselab.emf.ecore.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.DynamicMessage;

/**
 * A {@link Resource} implementation providing serialization in Google's Protocol Buffer format.
 */
public class ProtobufResourceImpl extends ResourceImpl {
	
	public ProtobufResourceImpl() {
		super();
	}

	public ProtobufResourceImpl(URI uri) {
		super(uri);
	}

	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		EcoreProtos.EResourceProto.Builder resource = EcoreProtos.EResourceProto.newBuilder();
		
		Map<EPackage, EcoreProtos.EPackageProto.Builder> packages = new HashMap<EPackage, EcoreProtos.EPackageProto.Builder>();
		Map<EClass, EcoreProtos.EClassProto.Builder> classes = new HashMap<EClass, EcoreProtos.EClassProto.Builder>();
		
		DescriptorProtos.FileDescriptorProto.Builder protos = DescriptorProtos.FileDescriptorProto.newBuilder();
		int id = 1;
		
		Descriptors.FileDescriptor file = null;
		
		for(EObject eObject : getContents()) {
			EClass eClass = eObject.eClass();
			
			if(!packages.containsKey(eClass.getEPackage())) {
				EcoreProtos.EPackageProto.Builder b = resource.addEPackageBuilder();
				b.setUri(eClass.getEPackage().getNsURI());
				
				packages.put(eClass.getEPackage(), b);
			}
			
			if(!classes.containsKey(eClass)) {
				EcoreProtos.EClassProto.Builder b = packages.get(eClass.getEPackage()).addEClassBuilder();
				DescriptorProtos.DescriptorProto.Builder db = protos.addMessageTypeBuilder();
				
				int number = 1;
				
				db.setName(eClass.getName());
				
				for(EAttribute attribute : eClass.getEAllAttributes()) {
					db.addFieldBuilder()
						.setName(attribute.getName())
						.setNumber(number++)
						.setType(Type.TYPE_STRING);
				}
				
				b.setId(id++);
				b.setDefinition(db);
				
				classes.put(eClass, b);
				
				try {
					file = Descriptors.FileDescriptor.buildFrom(protos.build(), new Descriptors.FileDescriptor[0]);
				} catch (DescriptorValidationException e) {
					throw new RuntimeException(e);
				}
			}
			
			EcoreProtos.EClassProto.Builder c = classes.get(eClass);
			
			EcoreProtos.EObjectProto.Builder b = resource.addEObjectBuilder();
			b.setEClassId(c.getId());
						
			Descriptors.Descriptor dc = file.findMessageTypeByName(eClass.getName());
			
			DynamicMessage.Builder m = DynamicMessage.newBuilder(dc);
			
			for(EAttribute attribute : eClass.getEAllAttributes()) {
				if(eObject.eIsSet(attribute)) {
					m.setField(dc.findFieldByName(attribute.getName()), eObject.eGet(attribute));
				}
			}
			
			b.setData(m.build().toByteString());
		}
		
		outputStream.write(resource.build().toByteArray());
	}
		
	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		EcoreProtos.EResourceProto resource = EcoreProtos.EResourceProto.parseFrom(inputStream);
		
		Map<Integer, EClass> classes = new HashMap<Integer, EClass>();
		DescriptorProtos.FileDescriptorProto.Builder protos = DescriptorProtos.FileDescriptorProto.newBuilder();
		
		for(EcoreProtos.EPackageProto ePackage : resource.getEPackageList()) {
			EPackage p = getResourceSet().getPackageRegistry().getEPackage(ePackage.getUri());
			
			for(EcoreProtos.EClassProto eClass : ePackage.getEClassList()) {
				classes.put(eClass.getId(), (EClass) p.getEClassifier(eClass.getDefinition().getName()));
				protos.addMessageType(eClass.getDefinition());
			}
		}
		Descriptors.FileDescriptor file = null;
		
		try {
			file = Descriptors.FileDescriptor.buildFrom(protos.build(), new Descriptors.FileDescriptor[0]);
		} catch (DescriptorValidationException e) {
			throw new RuntimeException(e);
		}
		
		for(EcoreProtos.EObjectProto eObject : resource.getEObjectList()) {
			int id = eObject.getEClassId();
			
			EClass eClass = classes.get(id);
			Descriptors.Descriptor descriptor = file.findMessageTypeByName(eClass.getName());
			
			DynamicMessage m = DynamicMessage.parseFrom(descriptor, eObject.getData());
			
			EObject obj = eClass.getEPackage().getEFactoryInstance().create(eClass);
			
			for(Map.Entry<Descriptors.FieldDescriptor, Object> field : m.getAllFields().entrySet()) {
				obj.eSet(eClass.getEStructuralFeature(field.getKey().getName()), field.getValue());
			}
			
			getContents().add(obj);
		}
	}
}
