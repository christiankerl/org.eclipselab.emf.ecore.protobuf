package org.eclipselab.emf.ecore.protobuf.tests.library.util;

import org.eclipse.emf.ecore.EPackage;
import org.eclipselab.emf.ecore.protobuf.mapping.EPackageMapper;
import org.eclipselab.emf.ecore.protobuf.mapping.EPackageMappingCache;
import org.eclipselab.emf.ecore.protobuf.mapping.MapperRegistry;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryPackage;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryProtos;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.ExtensionRegistry;

public class LibraryPackageMapper implements EPackageMapper
{
  public static void register(MapperRegistry registry)
  {
    registry.register(new LibraryPackageMapper());
  }
  
  public LibraryPackageMapper()
  {
    if(EPackageMappingCache.get(LibraryPackage.eINSTANCE) == null)
    {
      EPackageMappingCache.create(LibraryPackage.eINSTANCE, LibraryProtos.getDescriptor());
    }
  }
  
  @Override
  public boolean supports(EPackage type)
  {
    return type == LibraryPackage.eINSTANCE;
  }

  @Override
  public void map(EPackage source, DescriptorProtos.FileDescriptorSet.Builder context)
  {    
    context.addFile(LibraryProtos.getDescriptor().toProto());
  }

  @Override
  public void registerExtensions(FileDescriptor pbPackage, ExtensionRegistry extensionRegistry)
  {
    LibraryProtos.registerAllExtensions(extensionRegistry);
  }
}
