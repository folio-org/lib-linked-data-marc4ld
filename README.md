# lib-linked-data-marc4ld
© 2024 EBSCO Information Services.

This software is distributed under the terms of the Apache License, Version 2.0.
See the file "[LICENSE](LICENSE)" for more information.

## Introduction
lib-linked-data-marc4ld is a Java Spring library for converting MARC Bibliographic records to Linked Data Graph and vice-versa.

## Usage

### Convert MARC Bibliographic record to Linked Data Graph

```java
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.marc2ld.bib.MarcBib2ldMapper;
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

### Convert Linked Data Graph to MARC Bibliographic record

```java
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.Bibframe2MarcMapper;
import org.springframework.stereotype.Service;

@Service
public class YourService {
  private final Bibframe2MarcMapper mapper;

  public YourService(Bibframe2MarcMapper mapper) {
    this.mapper = mapper;
  }

  public void yourMethod(Resource resource) {
    String marcJson = mapper.toMarcJson(resource);
    // ...
    // ...
  }
}

```

### Dependencies
- [lib-linked-data-fingerprint](https://github.com/folio-org/lib-linked-data-fingerprint)
- [lib-linked-data-dictionary](https://github.com/folio-org/lib-linked-data-dictionary)
