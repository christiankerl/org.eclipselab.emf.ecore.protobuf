/**
 * <copyright>
 *
 * Copyright (c) 2011 Christian Kerl
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Christian Kerl - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipselab.emf.ecore.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipselab.emf.ecore.protobuf.converter.ConverterRegistry;
import org.eclipselab.emf.ecore.protobuf.converter.DynamicFromProtoBufMessageConverter;
import org.eclipselab.emf.ecore.protobuf.converter.DynamicToProtoBufMessageConverter;
import org.eclipselab.emf.ecore.protobuf.mapping.DefaultNamingStrategy;
import org.eclipselab.emf.ecore.protobuf.mapping.MapperRegistry;
import org.eclipselab.emf.ecore.protobuf.mapping.MappingException;
import org.eclipselab.emf.ecore.protobuf.mapping.NamingStrategy;
import org.eclipselab.emf.ecore.protobuf.util.EPackageDependencyAnalyzer;
import org.eclipselab.emf.ecore.protobuf.util.EcoreUtil2;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.ExtensionRegistry;


/**
 * A {@link Resource} implementation providing serialization in Google's Protocol Buffer format.
 * 
 * @author Christian Kerl
 */
public class ProtobufResourceImpl extends ResourceImpl
{
  private static class PbPackageEntry
  {
    private final int index;
    private final Descriptors.FileDescriptor pbPackage;
    
    public int getIndex()
    {
      return index;
    }
    
    public Descriptors.FileDescriptor getPbPackage()
    {
      return pbPackage;
    }

    public PbPackageEntry(int index, Descriptors.FileDescriptor pbPackage)
    {
      super();
      this.index = index;
      this.pbPackage = pbPackage;
    }
  }
  
  // TODO: maybe this is not the best name
  private static class PbPackageExporter
  {
    private MapperRegistry mappers;
    private EcoreProtos.EResourceProto.Builder resource;
    private Map<EPackage, PbPackageEntry> packages;
    
    private Set<EPackage> packagesCurrentlyLoading;
    
    public PbPackageExporter(MapperRegistry mappers, EcoreProtos.EResourceProto.Builder resource)
    {
      this.mappers = mappers;
      this.resource = resource;
      this.packages = new HashMap<EPackage, PbPackageEntry>();
      this.packagesCurrentlyLoading = new HashSet<EPackage>();
    }
    
    public PbPackageEntry get(EPackage ePackage)
    {
      if(!packages.containsKey(ePackage))
      {
        add(ePackage);
      }
      
      return packages.get(ePackage);
    }

    private void add(EPackage ePackage)
    {
      EPackageDependencyAnalyzer dependencyAnalyzer = EPackageDependencyAnalyzer.get(ePackage);
      
      if(packagesCurrentlyLoading.contains(ePackage))
      {
        throw MappingException.causeDependencyCycle();
      }
      
      packagesCurrentlyLoading.add(ePackage);
      
      List<EPackage> eDependencies = dependencyAnalyzer.getDependencies();
      
      DescriptorProtos.FileDescriptorSet.Builder files = DescriptorProtos.FileDescriptorSet.newBuilder();
      
      mappers
        .find(ePackage)
        .map(ePackage, files);
      
      DescriptorProtos.FileDescriptorProto.Builder pbPackage = files.getFileBuilder(0);
      
      Descriptors.FileDescriptor[] pbDependencies = new Descriptors.FileDescriptor[eDependencies.size()];
      List<Integer> pbDependencyIndices = new ArrayList<Integer>(eDependencies.size());
      
      for(int idx = 0; idx < eDependencies.size(); idx++)
      {
        PbPackageEntry entry = get(eDependencies.get(idx));
        pbDependencyIndices.add(entry.getIndex());
        pbDependencies[idx] = entry.getPbPackage();
        
        pbPackage.addDependency(entry.getPbPackage().getName());
      }
      
      resource
        .addEpackageBuilder()
        .setUri(ePackage.getNsURI())
        .addAllDependency(pbDependencyIndices)
        .setDefinition(pbPackage);
      
      try
      {
        packages.put(ePackage, new PbPackageEntry(resource.getEpackageCount() - 1, Descriptors.FileDescriptor.buildFrom(pbPackage.build(), pbDependencies)));
      }
      catch (DescriptorValidationException e)
      {
        throw new MappingException(e);
      }
      
      packagesCurrentlyLoading.remove(ePackage);
    }
  }
  
  private static class PbPackageImporter
  {
    private Descriptors.FileDescriptor[] pbPackages;
    private ExtensionRegistry pbExtensionRegistry;
    
    public PbPackageImporter(EcoreProtos.EResourceProto resource)
    {
      initialize(resource);
    }

    public Descriptors.FileDescriptor get(int index)
    {
      return pbPackages[index];
    }
    
    public ExtensionRegistry getExtentionRegistry()
    {
      return pbExtensionRegistry;
    }
    
    private void initialize(EcoreProtos.EResourceProto resource)
    {
      pbExtensionRegistry = ExtensionRegistry.newInstance();
      pbPackages = new Descriptors.FileDescriptor[resource.getEpackageCount()];
      
      for(int idx = 0; idx < pbPackages.length; idx++)
      {
        loadPackage(resource, idx);
      }
    }

    private Descriptors.FileDescriptor loadPackage(EcoreProtos.EResourceProto resource, int pbPackageIdx)
    {
      if(pbPackages[pbPackageIdx] == null)
      {
        EcoreProtos.EPackageProto pbPackage = resource.getEpackage(pbPackageIdx);
        
        Descriptors.FileDescriptor[] pbDependencies = new Descriptors.FileDescriptor[pbPackage.getDependencyCount()];
        
        for(int idx = 0; idx < pbDependencies.length; idx++)
        {
          pbDependencies[idx] = loadPackage(resource, idx); 
        }
        
        try
        {
          pbPackages[pbPackageIdx] = Descriptors.FileDescriptor.buildFrom(pbPackage.getDefinition(), pbDependencies);
        }
        catch (DescriptorValidationException e)
        {
          throw new MappingException(e);
        }
        
        for(Descriptors.Descriptor pbMessage : pbPackages[pbPackageIdx].getMessageTypes())
        {
          for(Descriptors.FieldDescriptor pbExtension : pbMessage.getExtensions())
          {
            pbExtensionRegistry.add(pbExtension, DynamicMessage.getDefaultInstance(pbMessage));
          }
        }
      }
      
      return pbPackages[pbPackageIdx]; 
    }
  }
  
  private final NamingStrategy naming = new DefaultNamingStrategy();
  private final ConverterRegistry converters = new ConverterRegistry();
  private final MapperRegistry mappers = new MapperRegistry(naming);

  public ProtobufResourceImpl()
  {
    super();
  }

  public ProtobufResourceImpl(URI uri)
  {
    super(uri);
  }

  @Override
  protected void doSave(OutputStream outputStream, Map< ? , ? > options) throws IOException
  {
    try
    {
      internalDoSave(outputStream, options);
    }
    catch(MappingException e)
    {
      throw new IOWrappedException(e);
    }
  }
  
  private void internalDoSave(OutputStream outputStream, Map< ? , ? > options) throws IOException
  {
    EcoreProtos.EResourceProto.Builder resource = EcoreProtos.EResourceProto.newBuilder();
    PbPackageExporter ePackages = new PbPackageExporter(mappers, resource);
    
    DynamicToProtoBufMessageConverter toProtoBuf = new DynamicToProtoBufMessageConverter(new EObjectPool(), converters, naming);

    for (EObject eObject : getContents())
    {
      EClass eClass = eObject.eClass();
      EPackage ePackage = EcoreUtil2.getRootPackage(eClass);

      PbPackageEntry pbPackageEntry = ePackages.get(ePackage);
      
      Descriptors.FileDescriptor pbPackage = pbPackageEntry.getPbPackage();
      Descriptors.Descriptor pbClass = pbPackage.findMessageTypeByName(eClass.getName());

      DynamicMessage data = toProtoBuf.convert(eObject, pbClass);
      
      resource
        .addEobjectBuilder()
        .setEpackageIndex(pbPackageEntry.getIndex())
        .setEclassIndex(pbClass.getIndex())
        .setData(data.toByteString());
    }

    outputStream.write(resource.build().toByteArray());
  }

  @Override
  protected void doLoad(InputStream inputStream, Map< ? , ? > options) throws IOException
  {
    try
    {
      internalDoLoad(inputStream, options);
    }
    catch(MappingException e)
    {
      throw new IOWrappedException(e);
    }
  }
  
  private void internalDoLoad(InputStream inputStream, Map< ? , ? > options) throws IOException
  {
    EcoreProtos.EResourceProto resource = EcoreProtos.EResourceProto.parseFrom(inputStream);
    PbPackageImporter pbPackages = new PbPackageImporter(resource);
    
    DynamicFromProtoBufMessageConverter fromProtoBuf = new DynamicFromProtoBufMessageConverter(new EObjectPool(), converters, naming);
    
    for (EcoreProtos.EObjectProto pbObject : resource.getEobjectList())
    {
      EcoreProtos.EPackageProto pbEpackage = resource.getEpackage(pbObject.getEpackageIndex());
      EPackage ePackage = getResourceSet().getPackageRegistry().getEPackage(pbEpackage.getUri());

      Descriptors.FileDescriptor pbPackage = pbPackages.get(pbObject.getEpackageIndex());
            
      Descriptors.Descriptor pbClass = pbPackage.getMessageTypes().get(pbObject.getEclassIndex());
      DynamicMessage pbData = DynamicMessage.parseFrom(pbClass, pbObject.getData(), pbPackages.getExtentionRegistry());
      
      EClass eClass = (EClass)ePackage.getEClassifier(pbClass.getName());

      getContents().add(fromProtoBuf.convert(pbData, eClass));
    }
  }
}
