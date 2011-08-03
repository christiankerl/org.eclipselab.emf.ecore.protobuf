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
package org.eclipselab.emf.codegen.protobuf;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipselab.emf.codegen.protobuf.annotations.EPackageAnnotation;
import org.eclipselab.emf.codegen.protobuf.internal.ProtobufGeneratorPlugin;
import org.eclipselab.emf.codegen.protobuf.template.GenerateProtobufUtilities;
import org.eclipselab.emf.ecore.protobuf.mapping.DefaultNamingStrategy;
import org.eclipselab.emf.ecore.protobuf.mapping.MapperRegistry;
import org.eclipselab.emf.ecore.protobuf.util.DescriptorDebugStringBuilder;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.DescriptorValidationException;

public class ProtobufPackageGeneratorAdapter extends GenBaseGeneratorAdapter
{
  private ProtobufCompiler protobufCompiler;
  
  public ProtobufPackageGeneratorAdapter(GeneratorAdapterFactory generatorAdapterFactory)
  {
    this(generatorAdapterFactory, ProtobufGeneratorPlugin.getProtobufCompiler());
  }
  
  public ProtobufPackageGeneratorAdapter(GeneratorAdapterFactory generatorAdapterFactory, ProtobufCompiler protobufCompiler)
  {
    super(generatorAdapterFactory);
    setProtobufCompiler(protobufCompiler);
  }

  public void setProtobufCompiler(ProtobufCompiler protobufCompiler)
  {
    this.protobufCompiler = protobufCompiler;
  }
  
  @Override
  public boolean canGenerate(Object object, Object projectType)
  {
    return MODEL_PROJECT_TYPE.equals(projectType) ? super.canGenerate(object, projectType) : false;
  }
  
  @Override
  protected Diagnostic generateModel(Object object, Monitor monitor)
  {
    GenModel genModel = (GenModel) object;
    
    for(GenPackage genPackage : genModel.getGenPackages())
    {
      EPackage ePackage = genPackage.getEcorePackage();
      
      if(EPackageAnnotation.get(ePackage).generate())
      {
        DescriptorProtos.FileDescriptorSet.Builder files = DescriptorProtos.FileDescriptorSet.newBuilder();
        MapperRegistry mappers = new MapperRegistry(new DefaultNamingStrategy());
        
        mappers
          .find(ePackage)
          .map(ePackage, files);
        
        DescriptorProtos.FileDescriptorProto.Builder file = files.getFileBuilder(0);
        file.getOptionsBuilder()
          .setJavaPackage(genPackage.getReflectionPackageName())
          .setJavaOuterClassname(genPackage.getPrefix() + "Protos");

        Descriptors.FileDescriptor file2;
        
        try
        {
          file2 = Descriptors.FileDescriptor.buildFrom(file.build(), new Descriptors.FileDescriptor[0]);
        }
        catch (DescriptorValidationException e)
        {
          return BasicDiagnostic.toDiagnostic(e);
        }
        
        URI srcDirectory = toURI(genModel.getModelDirectory());
        URI targetDirectory = srcDirectory.appendSegments(genPackage.getReflectionPackageName().split("\\."));
        URI targetFile = targetDirectory.appendSegment(file.getPackage() + ".proto");
        
        ensureContainerExists(targetDirectory, createMonitor(monitor, 1));

        try
        {
          Writer writer = new OutputStreamWriter(createOutputStream(targetFile));
          writer.write(new DescriptorDebugStringBuilder().build(file2));
          writer.close();
        }
        catch (Exception e)
        {
          return BasicDiagnostic.toDiagnostic(e);
        }
        
        srcDirectory = srcDirectory.appendSegment("");
        String workingDirectory = toAbsoluteFilesystemPath(srcDirectory) + File.separator;
        String protoFile = toPlatformResourceURI(targetFile).deresolve(toPlatformResourceURI(srcDirectory)).toFileString();
        
        if(protobufCompiler.isAvailable())
        {
          try 
          {
            protobufCompiler.invoke(workingDirectory, protoFile, workingDirectory);
          } 
          catch(Throwable t) 
          {
            return BasicDiagnostic.toDiagnostic(t);
          }
        }
        
        URI utilDirectory = toURI(genModel.getModelDirectory()).appendSegments(genPackage.getUtilitiesPackageName().split("\\."));
        
        try
        {
          GenerateProtobufUtilities generateProtobufUtilities = new GenerateProtobufUtilities(genPackage, new File(toAbsoluteFilesystemPath(utilDirectory)), Collections.emptyList());
          generateProtobufUtilities.generate(monitor);
        }
        catch (IOException e)
        {
          return BasicDiagnostic.toDiagnostic(e);          
        }
      }
    }
    
    return Diagnostic.OK_INSTANCE;
  }
  
  private String toAbsoluteFilesystemPath(URI platformPath)
  {
    return EcorePlugin.getWorkspaceRoot().getFolder(new Path(platformPath.toFileString())).getLocation().toOSString();
  }
}
