package org.eclipselab.emf.ecore.protobuf.internal;

import org.eclipse.emf.ecore.EPackage;
import org.eclipselab.emf.ecore.protobuf.mapper.DefaultNamingStrategy;
import org.eclipselab.emf.ecore.protobuf.tests.EPackageBuilder;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryPackage;
import org.eclipselab.emf.ecore.protobuf.util.DescriptorDebugStringBuilder;
import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.DescriptorValidationException;

/**
 * UnitTest for {@link EPackageMapper}.
 * 
 * @author Christian Kerl
 */
public class EPackageMapperTest {

	private EPackageMapper mapper;
	private final DescriptorDebugStringBuilder protoStringBuilder = new DescriptorDebugStringBuilder();
	
	@Before
	public void setup() {
		mapper = new EPackageMapper(new DefaultNamingStrategy());
	}
	
	@Test
	public void test() throws Exception {		
		DescriptorProtos.FileDescriptorProto result = mapper.map(LibraryPackage.eINSTANCE);
		
		debug(result);
	}
	
	@Test
	public void testClassHierarchyMapping() throws Exception {
		EPackage pkg = EPackageBuilder.create("example")
			.withClass("BaseA")
				.beingAbstract()
			.end()
			.withClass("BaseB").subclassing("BaseA")
			.end()
			.withClass("BaseC").subclassing("BaseB")
				.beingAbstract()
			.end()
			.withClass("Impl1").subclassing("BaseC")
			.end()
			.withClass("Impl2").subclassing("BaseC")
			.end()
			.withClass("Container")
				.having("baseAObjects").containing().many().ofType("BaseA").end()
				.having("baseCObjects").containing().many().ofType("BaseC").end()
			.end()
		.get();
		
		DescriptorProtos.FileDescriptorProto result = mapper.map(pkg);
		
		debug(result);
	}
	
	private void debug(DescriptorProtos.FileDescriptorProto proto) throws DescriptorValidationException {
		Descriptors.FileDescriptor result = Descriptors.FileDescriptor.buildFrom(proto, new Descriptors.FileDescriptor[0]);
		
		System.out.println(protoStringBuilder.build(result));
	}
}
