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
import org.eclipselab.emf.ecore.protobuf.conversion.Converter;
import org.eclipselab.emf.ecore.protobuf.conversion.Converter.MappingContext;
import org.eclipselab.emf.ecore.protobuf.conversion.ConverterRegistry;
import org.eclipselab.emf.ecore.protobuf.conversion.FromProtoBufMessageConverter;
import org.eclipselab.emf.ecore.protobuf.conversion.ToProtoBufMessageConverter;
import org.eclipselab.emf.ecore.protobuf.internal.EObjectPool;
import org.eclipselab.emf.ecore.protobuf.internal.conversion.DynamicFromProtoBufMessageConverter;
import org.eclipselab.emf.ecore.protobuf.internal.conversion.DynamicToProtoBufMessageConverter;
import org.eclipselab.emf.ecore.protobuf.mapping.DefaultNamingStrategy;
import org.eclipselab.emf.ecore.protobuf.mapping.EPackageMappingCache;
import org.eclipselab.emf.ecore.protobuf.mapping.MapperRegistry;
import org.eclipselab.emf.ecore.protobuf.mapping.MappingException;
import org.eclipselab.emf.ecore.protobuf.mapping.NamingStrategy;
import org.eclipselab.emf.ecore.protobuf.util.EPackageDependencyAnalyzer;
import org.eclipselab.emf.ecore.protobuf.util.EcoreUtil2;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;


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
  
  private class ProtobufPackageDumper implements MappingContext<EClass, Descriptors.Descriptor>
  {
    private EcoreProtos.EResourceProto.Builder resource;
    private Map<EPackage, PbPackageEntry> packages;
    
    private Set<EPackage> packagesBeingAdded;
    
    public ProtobufPackageDumper(EcoreProtos.EResourceProto.Builder resource)
    {
      this.resource = resource;
      this.packages = new HashMap<EPackage, PbPackageEntry>();
      this.packagesBeingAdded = new HashSet<EPackage>();
    }
    
    public PbPackageEntry get(EPackage ePackage)
    {
      if(!packages.containsKey(ePackage))
      {
        add(ePackage);
      }
      
      return packages.get(ePackage);
    }

    @Override
    public Descriptors.Descriptor lookup(EClass sourceType)
    {
      return lookup(get(EcoreUtil2.getRootPackage(sourceType)), sourceType);
    }
    
    public Descriptors.Descriptor lookup(PbPackageEntry pbPackageEntry, EClass sourceType)
    {
      return pbPackageEntry.getPbPackage().findMessageTypeByName(naming.getMessage(sourceType));
    } 
       
    private void add(EPackage ePackage)
    {
      final EPackageDependencyAnalyzer dependencyAnalyzer = EPackageDependencyAnalyzer.get(ePackage);
      
      if(packagesBeingAdded.contains(ePackage))
      {
        throw MappingException.causeDependencyCycle();
      }
      
      packagesBeingAdded.add(ePackage);
      
      final List<EPackage> eDependencies = dependencyAnalyzer.getDependencies();
      final List<Integer> pbDependencyIndices = new ArrayList<Integer>(eDependencies.size());
      
      final EPackageMappingCache cache = EPackageMappingCache.get(ePackage);
      Descriptors.FileDescriptor pbPackage;
      
      if(cache != null)
      {
        pbPackage = cache.getDescriptor();

        for(int idx = 0; idx < eDependencies.size(); idx++)
        {
          PbPackageEntry entry = get(eDependencies.get(idx));
          pbDependencyIndices.add(entry.getIndex());
        }
      }
      else
      {
        final DescriptorProtos.FileDescriptorSet.Builder files = DescriptorProtos.FileDescriptorSet.newBuilder();
        
        mappers
          .find(ePackage)
          .map(ePackage, files);
        
        final DescriptorProtos.FileDescriptorProto.Builder pbPackageProto = files.getFileBuilder(0);
        
        final Descriptors.FileDescriptor[] pbDependencies = new Descriptors.FileDescriptor[eDependencies.size()];

        for(int idx = 0; idx < eDependencies.size(); idx++)
        {
          PbPackageEntry entry = get(eDependencies.get(idx));
          pbDependencyIndices.add(entry.getIndex());
          pbDependencies[idx] = entry.getPbPackage();
          
          pbPackageProto.addDependency(entry.getPbPackage().getName());
        }
        
        try
        {
          pbPackage = Descriptors.FileDescriptor.buildFrom(pbPackageProto.build(), pbDependencies);
        }
        catch (DescriptorValidationException e)
        {
          throw new MappingException(e);
        }
        
        EPackageMappingCache.create(ePackage, pbPackage);
      }

      resource.addEpackageBuilder()
        .setUri(ePackage.getNsURI())
        .addAllDependency(pbDependencyIndices)
        .setDefinition(pbPackage.toProto());
      
      packages.put(ePackage, new PbPackageEntry(resource.getEpackageCount() - 1, pbPackage));
      
      packagesBeingAdded.remove(ePackage);
    }
  }
  
  private class ProtobufPackageLoader implements Converter.MappingContext<Descriptors.Descriptor, EClass>
  {
    private Descriptors.FileDescriptor[] pbPackages;
    private ExtensionRegistry pbExtensionRegistry;
    private Map<Descriptors.FileDescriptor, EPackage> ePackageLookup;
    
    public ProtobufPackageLoader(EcoreProtos.EResourceProto resource)
    {
      initialize(resource);
    }
    
    public Descriptors.Descriptor get(int pbPackageIndex, int pbMessageIndex)
    {
      return pbPackages[pbPackageIndex].getMessageTypes().get(pbMessageIndex);
    }
    
    
    public ExtensionRegistry getExtentionRegistry()
    {
      return pbExtensionRegistry;
    }

    @Override
    public EClass lookup(Descriptors.Descriptor sourceType)
    {
      return (EClass)EcoreUtil2.getClassifierFromPackageHierarchy(ePackageLookup.get(sourceType.getFile()), sourceType.getName());
    }
    
    private void initialize(EcoreProtos.EResourceProto resource)
    {
      pbExtensionRegistry = ExtensionRegistry.newInstance();
      pbPackages = new Descriptors.FileDescriptor[resource.getEpackageCount()];
      ePackageLookup = new HashMap<Descriptors.FileDescriptor, EPackage>(resource.getEpackageCount());
      
      for(int idx = 0; idx < pbPackages.length; idx++)
      {
        loadPackage(resource, idx);
      }
    }

    private Descriptors.FileDescriptor loadPackage(EcoreProtos.EResourceProto resource, int pbPackageIdx)
    {
      if(pbPackages[pbPackageIdx] == null)
      {
        EcoreProtos.EPackageProto pbPackageProto = resource.getEpackage(pbPackageIdx);
        EPackage ePackage = getResourceSet().getPackageRegistry().getEPackage(pbPackageProto.getUri());
        Descriptors.FileDescriptor pbPackage;
        EPackageMappingCache cache = EPackageMappingCache.get(ePackage);
        
        if(cache != null)
        {
          pbPackage = cache.getDescriptor();
        }
        else
        {
          // TODO: no cache, but we should try to do some "fast lookup" in mapper registry
          
          Descriptors.FileDescriptor[] pbDependencies = new Descriptors.FileDescriptor[pbPackageProto.getDependencyCount()];
          
          for(int idx = 0; idx < pbDependencies.length; idx++)
          {
            pbDependencies[idx] = loadPackage(resource, idx); 
          }
          
          try
          {
            pbPackage = Descriptors.FileDescriptor.buildFrom(pbPackageProto.getDefinition(), pbDependencies);
          }
          catch (DescriptorValidationException e)
          {
            throw new MappingException(e);
          }
          
          EPackageMappingCache.create(ePackage, pbPackage);
        }
        
        ePackageLookup.put(pbPackage, ePackage);
        pbPackages[pbPackageIdx] = pbPackage;
        
        mappers.find(ePackage).registerExtensions(pbPackage, pbExtensionRegistry);
      }
      
      return pbPackages[pbPackageIdx]; 
    }
  }
  
  public static final String OPTION_CONVERTER_REGISTRY = "converterRegistry";
  public static final String OPTION_MAPPER_REGISTRY = "mapperRegistry";
  
  private final NamingStrategy naming = new DefaultNamingStrategy();
  private ConverterRegistry defaultConverters = new ConverterRegistry();
  private MapperRegistry mappers;

  public ProtobufResourceImpl()
  {
    super();
    defaultConverters.register(new DynamicToProtoBufMessageConverter(naming));
    defaultConverters.register(new DynamicFromProtoBufMessageConverter(naming));
  }

  public ProtobufResourceImpl(URI uri)
  {
    super(uri);
    defaultConverters.register(new DynamicToProtoBufMessageConverter(naming));
    defaultConverters.register(new DynamicFromProtoBufMessageConverter(naming));
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
    if(options != null && options.containsKey(OPTION_MAPPER_REGISTRY))
    {
      mappers = (MapperRegistry)options.get(OPTION_MAPPER_REGISTRY);
    }
    else
    {
      mappers = new MapperRegistry(naming);
    }
    
    final ConverterRegistry converters = getConverterRegistry(options);
    final EcoreProtos.EResourceProto.Builder resource = EcoreProtos.EResourceProto.newBuilder();
    final ProtobufPackageDumper ePackages = new ProtobufPackageDumper(resource);
    final EObjectPool pool = new EObjectPool();
    
    for (EObject eObject : getContents())
    {
      EClass eClass = eObject.eClass();
      EPackage ePackage = EcoreUtil2.getRootPackage(eClass);

      PbPackageEntry pbPackageEntry = ePackages.get(ePackage);
      Descriptors.Descriptor pbClass = ePackages.lookup(pbPackageEntry, eClass);

      // strange cast but who cares
      @SuppressWarnings("unchecked")
      final ToProtoBufMessageConverter<EObject, Message> converter = ((ToProtoBufMessageConverter<EObject, Message>) converters.find(eClass));
      converter.setMappingContext(ePackages);
      converter.setObjectPool(pool);
      
      final Message data = converter.convert(eClass, eObject, pbClass);
      
      resource
        .addEobjectBuilder()
        .setEpackageIndex(pbPackageEntry.getIndex())
        .setEclassIndex(pbClass.getIndex())
        .setData(data.toByteString());
    }

    resource.build().writeTo(outputStream);
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
    if(options != null && options.containsKey(OPTION_MAPPER_REGISTRY))
    {
      mappers = (MapperRegistry)options.get(OPTION_MAPPER_REGISTRY);
    }
    else
    {
      mappers = new MapperRegistry(naming);
    }
    
    final ConverterRegistry converters = getConverterRegistry(options);
    final EcoreProtos.EResourceProto resource = EcoreProtos.EResourceProto.parseFrom(inputStream);
    final ProtobufPackageLoader pbPackages = new ProtobufPackageLoader(resource);
    final EObjectPool pool = new EObjectPool();
    
    for (EcoreProtos.EObjectProto pbObject : resource.getEobjectList())
    {
      final Descriptors.Descriptor pbClass = pbPackages.get(pbObject.getEpackageIndex(), pbObject.getEclassIndex());
      final FromProtoBufMessageConverter<? extends Message, ? extends EObject> converter = converters.find(pbClass);
      converter.setObjectPool(pool);
      converter.setMappingContext(pbPackages);
      
      getContents().add(
        converter.loadAndConvert(pbClass, pbPackages.getExtentionRegistry(), pbObject.getData())
      );
    }
  }
  
  private ConverterRegistry getConverterRegistry(Map<?, ?> options)
  {
    return options != null && options.containsKey(OPTION_CONVERTER_REGISTRY) ? (ConverterRegistry)options.get(OPTION_CONVERTER_REGISTRY) : defaultConverters;
  }
}
