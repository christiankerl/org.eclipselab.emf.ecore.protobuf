package org.eclipselab.emf.ecore.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipselab.emf.ecore.protobuf.converter.ConverterRegistry;
import org.eclipselab.emf.ecore.protobuf.converter.DynamicFromProtoBufMessageConverter;
import org.eclipselab.emf.ecore.protobuf.converter.DynamicToProtoBufMessageConverter;
import org.eclipselab.emf.ecore.protobuf.internal.EPackageMapper;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.DynamicMessage;

/**
 * A {@link Resource} implementation providing serialization in Google's Protocol Buffer format.
 */
public class ProtobufResourceImpl extends ResourceImpl {	
	private static final Descriptors.FileDescriptor[] NO_DEPENDENCIES = new Descriptors.FileDescriptor[0];
	
	private EPackageMapper ePackageMapper = new EPackageMapper();
	
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
				
		DynamicToProtoBufMessageConverter toProtoBuf = new DynamicToProtoBufMessageConverter(new EObjectPool(), new ConverterRegistry());
		
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
				.setData(toProtoBuf.convert(eObject, pbClass).toByteString());
		}
		
		outputStream.write(resource.build().toByteArray());
	}
	
	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		EcoreProtos.EResourceProto resource = EcoreProtos.EResourceProto.parseFrom(inputStream);
		
		DynamicFromProtoBufMessageConverter converter = new DynamicFromProtoBufMessageConverter(new EObjectPool(), new ConverterRegistry());
		
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
			
			getContents().add(converter.convert(pbData, eClass));
		}
	}
}
