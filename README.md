# lib-linked-data-marc4ld
Copyright (C) 2024 The Open Library Foundation

This software is distributed under the terms of the Apache License, Version 2.0.
See the file "[LICENSE](LICENSE)" for more information.

This software uses a copyleft (LGPL-2.1-or-later) licensed software library: [marc4j](https://github.com/marc4j/marc4j)

## Purpose
Lib-linked-data-marc4ld is a Java library designed for converting MARC records to Linked Data Graphs and vice versa.
## Compiling
```bash
mvn clean install
```
## Using the library
### Convert MARC Bibliographic record to Linked Data Graph
```java
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.marc2ld.bib.MarcBib2ldMapper;
import org.springframework.stereotype.Service;

@Service
public class YourService {

  private final MarcBib2ldMapper mapper;

  public YourService(MarcBib2ldMapper mapper) {
    this.mapper = mapper;
  }

  public void yourMethod(String marcJson) {
    Resource resource = mapper.fromMarcJson(marcJson);
    // ...
    // ...
  }
}
```
### Convert MARC Authority record to Linked Data Graph
```java
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.marc2ld.authority.MarcAuthority2ldMapper;
import org.springframework.stereotype.Service;

@Service
public class YourService {

  private final MarcAuthority2ldMapper mapper;

  public YourService(MarcAuthority2ldMapper mapper) {
    this.mapper = mapper;
  }

  public void yourMethod(String marcJson) {
    Resource resource = mapper.fromMarcJson(marcJson);
    // ...
    // ...
  }
}
```
### Convert Linked Data Graph to MARC record

```java
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.Ld2MarcMapper;
import org.springframework.stereotype.Service;

@Service
public class YourService {
  private final Ld2MarcMapper mapper;

  public YourService(Ld2MarcMapper mapper) {
    this.mapper = mapper;
  }

  public void yourMethod(Resource resource) {
    String marcJson = mapper.toMarcJson(resource);
    // ...
    // ...
  }
}
```
## Dependencies
- [lib-linked-data-dictionary](https://github.com/folio-org/lib-linked-data-dictionary)
- [lib-linked-data-fingerprint](https://github.com/folio-org/lib-linked-data-fingerprint)
## Download and configuration
The built artifacts for this module are available. See [configuration](https://dev.folio.org/download/artifacts/) for repository access.

## Developer Documentation

For detailed configuration and developer instructions, see the [Developer Guide](DEVELOPER_GUIDE.md).
