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
import org.eclipselab.emf.ecore.protobuf.conversion.ConversionException;
import org.eclipselab.emf.ecore.protobuf.conversion.Converter;
import org.eclipselab.emf.ecore.protobuf.conversion.Converter.MappingContext;
import org.eclipselab.emf.ecore.protobuf.conversion.ConverterRegistry;
import org.eclipselab.emf.ecore.protobuf.conversion.EObjectPool;
import org.eclipselab.emf.ecore.protobuf.conversion.FromProtoBufMessageConverter;
import org.eclipselab.emf.ecore.protobuf.conversion.ToProtoBufMessageConverter;
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
  
  private static final class ProtobufPackageDumper implements MappingContext<EClass, Descriptors.Descriptor>
  {
    private final EcoreProtos.EResourceProto.Builder resource;
    private final NamingStrategy naming;
    private final MapperRegistry mappers;
    private final Map<EPackage, PbPackageEntry> packages;
    
    private final Set<EPackage> packagesBeingAdded;
    
    public ProtobufPackageDumper(EcoreProtos.EResourceProto.Builder resource, NamingStrategy naming, MapperRegistry mappers)
    {
      this.resource = resource;
      this.naming = naming;
      this.mappers = mappers;
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
  
  private static final class ProtobufPackageLoader implements Converter.MappingContext<Descriptors.Descriptor, EClass>
  {
    private final EPackage.Registry ePackageRegistry;
    private final MapperRegistry mappers;
    
    private final Descriptors.FileDescriptor[] pbPackages;
    private final ExtensionRegistry pbExtensionRegistry;
    private final Map<Descriptors.FileDescriptor, EPackage> ePackageLookup;
    
    public ProtobufPackageLoader(EcoreProtos.EResourceProto resource, EPackage.Registry ePackageRegistry, MapperRegistry mappers)
    {
      this.ePackageRegistry = ePackageRegistry;
      this.mappers = mappers;

      pbExtensionRegistry = ExtensionRegistry.newInstance();
      pbPackages = new Descriptors.FileDescriptor[resource.getEpackageCount()];
      ePackageLookup = new HashMap<Descriptors.FileDescriptor, EPackage>(resource.getEpackageCount());
      
      for(int idx = 0; idx < pbPackages.length; idx++)
      {
        loadPackage(resource, idx);
      }
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
    
    private Descriptors.FileDescriptor loadPackage(EcoreProtos.EResourceProto resource, int pbPackageIdx)
    {
      if(pbPackages[pbPackageIdx] == null)
      {
        EcoreProtos.EPackageProto pbPackageProto = resource.getEpackage(pbPackageIdx);
        EPackage ePackage = ePackageRegistry.getEPackage(pbPackageProto.getUri());
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
  
  protected final NamingStrategy naming;
  private final ConverterRegistry defaultConverters;
  private final MapperRegistry defaultMappers;
  
  public ProtobufResourceImpl()
  {
    this(new DefaultNamingStrategy(), null);
  }
  
  public ProtobufResourceImpl(URI uri)
  {
    this(new DefaultNamingStrategy(), uri);
  }

  public ProtobufResourceImpl(NamingStrategy namingStrategy)
  {
    this(namingStrategy, null);
  }
  
  public ProtobufResourceImpl(NamingStrategy namingStrategy, URI uri)
  {
    super(uri);
    
    naming = namingStrategy;
    defaultMappers = new MapperRegistry(naming);
    defaultConverters = new ConverterRegistry();
    
    registerMappers(defaultMappers);
    registerConverters(defaultConverters);
  }
  
  protected void registerConverters(final ConverterRegistry converterRegistry)
  {
    converterRegistry.register(new DynamicToProtoBufMessageConverter(naming));
    converterRegistry.register(new DynamicFromProtoBufMessageConverter(naming));
  }
  
  protected void registerMappers(final MapperRegistry mapperRegistry)
  {
    
  }
  
  @Override
  protected void doSave(OutputStream outputStream, Map< ? , ? > options) throws IOException
  {
    try
    {
      internalDoSave(outputStream, options);
    }
    catch(ConversionException e)
    {
      throw new IOWrappedException(e);
    }
    catch(MappingException e)
    {
      throw new IOWrappedException(e);
    }
  }
    
  private void internalDoSave(OutputStream outputStream, Map< ? , ? > options) throws IOException
  {
    final ConverterRegistry converters = getConverterRegistry(options);
    final MapperRegistry mappers = getMapperRegistry(options);
    final EcoreProtos.EResourceProto.Builder resource = EcoreProtos.EResourceProto.newBuilder();
    final ProtobufPackageDumper ePackages = new ProtobufPackageDumper(resource, naming, mappers);
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
    catch(ConversionException e)
    {
      throw new IOWrappedException(e);
    }
    catch(MappingException e)
    {
      throw new IOWrappedException(e);
    }
  }
  
  private void internalDoLoad(InputStream inputStream, Map< ? , ? > options) throws IOException
  {
    final ConverterRegistry converters = getConverterRegistry(options);
    final MapperRegistry mappers = getMapperRegistry(options);
    final EcoreProtos.EResourceProto resource = EcoreProtos.EResourceProto.parseFrom(inputStream);
    final ProtobufPackageLoader pbPackages = new ProtobufPackageLoader(resource, getResourceSet().getPackageRegistry(), mappers);
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
  
  private MapperRegistry getMapperRegistry(Map<?, ?> options)
  {
    MapperRegistry result = options != null ? (MapperRegistry) options.get(OPTION_MAPPER_REGISTRY) : null;
    
    return result != null ? result : defaultMappers;
  }
  
  private ConverterRegistry getConverterRegistry(Map<?, ?> options)
  {
    ConverterRegistry result = options != null ? (ConverterRegistry) options.get(OPTION_CONVERTER_REGISTRY) : null;
    
    return result != null ? result : defaultConverters;
  }
}
