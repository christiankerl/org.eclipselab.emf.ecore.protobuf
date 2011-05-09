package org.eclipselab.emf.ecore.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

/**
 * A {@link Resource} implementation providing serialization in Google's Protocol Buffer format.
 */
public class ProtobufResourceImpl extends ResourceImpl {
	
	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		// iterate over content
		// if new epackage map epackage to protobuf description
	}
	
	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		
	}
}
