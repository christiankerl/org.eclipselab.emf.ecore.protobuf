# Eclipse EMF ProtoBuf Integration

[![Build Status](https://secure.travis-ci.org/christiankerl/org.eclipselab.emf.ecore.protobuf.png)](http://travis-ci.org/christiankerl/org.eclipselab.emf.ecore.protobuf)

This project integrates Google Protocol Buffers as serialization layer into Eclipse EMF. The initial implementation was created during Google Summer of Code 2011.

Features:

 * EMF Resource implementation to save any EMF model in ProtoBuf format
 * Conversion of any EMF model to ProtoBuf format
 * Generation of ProtoBuf's .proto file from an Ecore model
 * Generation of model specific, optimized converters to convert Ecore objects faster from and to ProtoBuf objects 

## Install from P2 Repository Archive

 * Download the latest version from the `Downloads` section
 * Open Eclipse
 * Open `Help` -> `Install New Software...`
 * Open `Add...`
 * Open `Archive...`
 * Select the downloaded archive
 * Close the dialog with `OK`
 * Select the features you want to install and follow the installation wizard
 
## Install from Source

### Prerequisites

 * Git
 * Apache Maven 3.0.3+
 * Google protoc 2.4.1+

### Build

This instruction assumes `git`, `mvn` and `protoc` are in your `$PATH`! 

    $ git clone git://github.com/christiankerl/org.eclipselab.emf.ecore.protobuf.git org.eclipselab.emf.ecore.protobuf
    $ cd ./org.eclipselab.emf.ecore.protobuf/org.eclipselab.emf.protobuf.releng/
    $ mvn clean install

Now proceed with the instructions from `Install from P2 Repository Archive`. The "downloaded archive" is `org.eclipselab.emf.ecore.protobuf/org.eclipselab.emf.protobuf.releng.repository/target/org.eclipselab.emf.protobuf.releng.repository.zip`