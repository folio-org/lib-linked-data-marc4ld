# Developer Guide: Configuring MARC to Graph Conversion and Vice Versa

This guide provides instructions for developers on how to configure the conversion between MARC records and Linked Data
graphs (MARC to graph and graph to MARC).

## Table of Contents
1. [Introduction](#1-introduction)
2. [Configuration File Locations](#2-configuration-file-locations)
3. [Configuration File contents](#3-configuration-file-contents)
4. [Java-based Mappings for MARC to Graph Conversion](#4-java-based-mappings-for-marc-to-graph-conversion)
5. [Java-based Mappings for Graph to MARC Conversion](#5-java-based-mappings-for-graph-to-marc-conversion)

---

## 1. Introduction
This document is intended for developers who need to understand, modify, or extend the configuration for
MARC <-> Linked Data conversion.
- Bidirectional conversion is supported for MARC Bibliographic records (MARC to Graph
and Graph to MARC).
- For MARC Authority records, only MARC to Graph conversion is supported; Graph to MARC conversion is
not available.

## 2. Configuration File Locations
The configuration files are located at:
```
src/main/resources/marc4ld.yml
src/main/resources/marc4ldAuthority.yml
```
- **marc4ld.yml**: Defines the rules for MARC bibliographic records.
- **marc4ldAuthority.yml**: Defines the rules for MARC authority records.

## 3. Configuration File contents

The overall structure of the configuration file is as follows:

```
MARC_tag_1:
  configurations for MARC tag 1
MARC_tag_2:
  configurations for MARC tag 2
...
```

Each top-level key in the YAML file is a MARC field tag (e.g., '100', '245', '650'), and its value is a set of
configuration options that define how that field is mapped to or from Linked Data.

**Example:**

```yaml
100:
  types: [PERSON]
  predicate: CREATOR
  parent: WORK
  subfields:
    a: NAME
    d: DATE
245:
  types: [TITLE]
  parent: INSTANCE
  predicate: TITLE
  subfields:
    a: MAIN_TITLE
    b: SUBTITLE
```

Below are properties for configuring the conversion rules for each MARC tag:

## types
**Description:** Specifies the types (classes) of the resource to be created in the Linked Data graph.
**Usage:** List one or more types from the [ResourceTypeDictionary](https://github.com/folio-org/lib-linked-data-dictionary/blob/master/src/main/java/org/folio/ld/dictionary/ResourceTypeDictionary.java)
(e.g., `INSTANCE`, `WORK`, `PERSON`, `ORGANIZATION`, `TOPIC`).
**Example:**
```yaml
types:
  - PERSON
  - CONCEPT
```

## parent
**Description:** Defines the parent resource from which an outgoing connection will be established to the new resource.
**Usage:** Reference to the parent node, typically by Type of the parent.
Use values from the ResourceTypeDictionary (e.g., `INSTANCE`).

**Example:**
```yaml
parent: INSTANCE
```

## predicate
**Description:** Specifies the type of connection (predicate/property) from the parent resource to the current resource.
**Usage:** Use values from the [PredicateDictionary](https://github.com/folio-org/lib-linked-data-dictionary/blob/master/src/main/java/org/folio/ld/dictionary/PredicateDictionary.java) (e.g., `FOCUS`, `SUB_FOCUS`).
**Example:**
```yaml
predicate: FOCUS
```

## subfields
**Description:** Maps MARC subfields to one or more properties of the new resource in the Linked Data graph.
**Usage:** Use property names from the [PropertyDictionary](https://github.com/folio-org/lib-linked-data-dictionary/blob/master/src/main/java/org/folio/ld/dictionary/PropertyDictionary.java). If a MARC subfield should be mapped to multiple graph
properties, specify them as a comma-separated list. Only the first property will be used for mapping back to MARC during
graph-to-MARC conversion.

**Example:**
```yaml
subfields:
  a: NAME, LABEL
  b: DATE
  c: PLACE
```
In this example, subfield 'a' is mapped to both `NAME` and `LABEL` properties. When converting from graph to MARC,
only `NAME` will be used for subfield 'a'.

## concat
**Description:** Specifies the character to use when concatenating values from multiple MARC subfields that are mapped
to the same resource property.
**Usage:** Place the `concat` property at the same level as `subfields`. When multiple subfields are mapped to the same
property, their values will be joined using the specified character.
**Example:**
```yaml
subfields:
  a: MAIN_TITLE
  d: MAIN_TITLE
concat: ' '
```
In this example, if both subfields 'a' and 'd' are present, their values will be concatenated with a space and stored
in the `MAIN_TITLE` property of the graph resource (e.g., `MAIN_TITLE: "valueA valueD"`).

## ind1 and ind2
**Description:** Specify the resource properties that correspond to the MARC indicators 1 and 2 (ind1 and ind2) for the
field.
**Usage:** Use property names from the [PropertyDictionary](https://github.com/folio-org/lib-linked-data-dictionary/blob/master/src/main/java/org/folio/ld/dictionary/PropertyDictionary.java) to map the indicator values to resource properties.
**Example:**
```yaml
ind2: NON_SORT_NUM
```
In this example, the value of ind2 in the MARC record will be mapped to the `NON_SORT_NUM` property in the graph.

## controlFields
**Description:** Allows extracting a substring from a MARC control field and assigning it to a resource property.
**Usage:** Specify the control field tag (e.g., '008') as the key. For each property, provide the property name and the
substring range as `start,end` (0-based, end exclusive). Assignment will happen only if the extracted value is not blank.
**Example:**
```yaml
controlFields:
  '008':
    CODE: 22,23
```
In this example, substring 22-23 from control field 008 will be extracted and assigned to the `CODE` property of the
new resource, only if the value is not blank.

## constants
**Description:** Allows setting constant values in the resource or constructing values using other resource properties.
**Usage:** Specify the property name and the value to assign. You can use placeholders (e.g., `%CODE`) to substitute
the value of another property.
**Example:**
```yaml
constants:
  LINK: http://id.loc.gov/vocabulary/languages/%CODE
```
In this example, the `LINK` property will be set to `http://id.loc.gov/vocabulary/languages/` followed by the value of
the `CODE` property in the resource.

## includeMarcKey
**Description:** If set to `true`, the source MARC record will be included in the generated resource's document in
JSON format. The property used for capturing the MARC record is `http://bibfra.me/vocab/bflc/marcKey`.

**Usage:**
  ```yaml
  includeMarcKey: true
  ```
When enabled, the original MARC record is embedded in the graph resource for reference.

## append
**Description:** If set to `true`, the specified properties will be added to an existing resource (if one exists)
with the same type(s) under the parent, rather than creating a new resource. If no such resource exists, a new resource
will be created.

**Usage:**
  ```yaml
  append: true
  ```
- **Example:**
  ```yaml
  - types: ANNOTATION
    parent: INSTANCE
    predicate: ADMIN_METADATA
    append: true
    controlFields:
      '001':
        CONTROL_NUMBER: 0,99
  ```
In this example, the `CONTROL_NUMBER` property will be added to the existing `ANNOTATION` resource (if present) under
the `INSTANCE` parent. If no such resource exists, a new `ANNOTATION` resource will be created.

## marc2ldCondition

**Description:** Controls the conditions under which a MARC record is converted to a Linked Data resource. Multiple
conditions can be combined, and more advanced conditions may be supported in the future.

**Sub-properties:**
  - `skip`: If set to `true`, the MARC to Linked Data mapping for this field will be skipped.
  - `leader`: Allows conditional mapping based on the MARC leader field.
    - `substring`: Specify the start and end index (0-based, end exclusive) to extract a substring from the leader.
    - `isAny`: Comma-separated values; if the extracted substring matches any of these, mapping is performed.
  - `controlFields`: Allows conditional mapping based on the value of a specific MARC control field. This is a list of conditions, each with:
    - `tag`: The MARC control field tag to check (e.g., '008', '006').
    - `substring`: Specify the start and end index (0-based, end exclusive) to extract a substring from the control field value.
    - `isAny`: Comma-separated values; if the extracted substring matches any of these, mapping is performed.
    - `isBlank`: If true, mapping is performed only if the substring is blank.
  - `ind1` / `ind2`: Specify a character value for ind1 and/or ind2. Mapping will only occur if the corresponding indicator in the MARC record matches the configured character. To specify a "not equal to" condition, prefix the character with `!` (e.g., `ind1: '!1'` means mapping occurs only if ind1 is NOT '1').
  - `fieldsAllOf`: Specifies a set of subfield presence/absence conditions that must all be satisfied for mapping to occur. Each subfield key is mapped to either `presented` (the subfield must be present) or `not_presented` (the subfield must be absent).
  - `fieldsAnyOf`: Specifies a set of subfield presence/absence conditions where mapping will occur if any one of the conditions is satisfied. Each subfield key is mapped to either `presented` (the subfield must be present) or `not_presented` (the subfield must be absent).

**Examples:**

  *Skip mapping for a field:*
  ```yaml
  marc2ldCondition:
    skip: true
  ```

  *Leader-based condition:*
  ```yaml
  marc2ldCondition:
    leader:
      substring: 7,8
      isAny: a,b
  ```
  This maps only if the substring from position 7 to 8 in the MARC leader is 'a' or 'b'.

  *Control field value condition:*
  ```yaml
  marc2ldCondition:
    controlFields:
      - tag: '008'
        substring: 35,37
        isAny: ab,cd
      - tag: '006'
        substring: 0,1
        isBlank: true
  ```
  This maps only if the substring from position 35 to 37 in control field 008 is 'ab' or 'cd', or the substring from position 0 to 1 in control field 006 is blank.

  *Indicator-based condition (equal and not equal):*
  ```yaml
  marc2ldCondition:
    ind1: '1'    # Only if ind1 is '1'
    ind2: '!2'  # Only if ind2 is NOT '2'
  ```
  This maps only if ind1 is '1' and ind2 is NOT '2' in the MARC record.

  *Fields presence/absence condition (all must be met):*
  ```yaml
  marc2ldCondition:
    fieldsAllOf:
      m: presented
      z: not_presented
  ```
  This maps only if subfield "m" is present and subfield "z" is not present.

  *Fields presence/absence condition (any can be met):*
  ```yaml
  marc2ldCondition:
    fieldsAnyOf:
      m: presented
      z: not_presented
  ```
  This maps if either subfield "m" is present or subfield "z" is not present.

  *Combining multiple conditions:*
  ```yaml
  marc2ldCondition:
    skip: false
    leader:
      substring: 7,8
      isAny: a,b
    controlFields:
      - tag: '008'
        substring: 35,37
        isAny: ab,cd
    ind1: '!1'
    ind2: ' '
  ```
  In this example, mapping is performed only if all specified conditions are satisfied, including indicator values (ind1 is NOT '1', ind2 is a blank space).

  *Note:* As the configuration evolves, more complex or additional conditions may be supported. Always refer to the latest documentation or codebase for new options.

## ld2marcCondition
- **Description:** Controls the conditions under which a Linked Data resource is converted back to a MARC field. Supports simple and complex rules.
- **Sub-properties:**
  - `skip`: If set to `true`, the graph resource will not be converted back to MARC.
  - `workType`: The resource will be converted to MARC only if the work associated with the instance is of the specified type. **Note:** `workType` is applicable only for bibliographic record transformation and not for authority records.
  - Complex edge-based rules: You can specify conditions based on the edges (relationships) of the current resource using `anyMatch` or `allMatch`.
    - `anyMatch`: MARC will be generated if any of the listed conditions are met.
    - `allMatch`: MARC will be generated only if all listed conditions are met.
    - Each condition can use:
      - `present`: Checks if a specific edge exists (`true`) or does not exist (`false`).
      - `properties`: Further restricts the condition to edges with specific property values.
- **Examples:**
  Simple usage:
  ```yaml
  ld2marcCondition:
    skip: true
    workType: CONTINUING_RESOURCES
  ```
  Complex edge-based rule:
  ```yaml
  ld2marcCondition:
    STATUS:
      anyMatch:
        - present: false
        - present: true
          properties:
            LINK: http://id.loc.gov/vocabulary/mstatus/current
  ```
  In this example, MARC will be generated if:
  - The resource does not have a STATUS edge, OR
  - The resource has a STATUS edge and the connected resource has a LINK property with value `http://id.loc.gov/vocabulary/mstatus/current`.

  If `allMatch` is used instead of `anyMatch`, then all listed conditions must be satisfied for MARC to be generated.

### include

The `include` property allows you to refer to shared rules defined in the `bibSharedRules` section (for `marc4ld.yml`) or the `sharedAuthorityRules` section (for `marc4ldAuthority.yml`).

- Shared rules are reusable mapping fragments that can be defined once and referenced in multiple places using the `include` property.
- In `marc4ld.yml`, shared rules are defined under the top-level `bibSharedRules:` section.
- In `marc4ldAuthority.yml`, shared rules are defined under the top-level `sharedAuthorityRules:` section.
- To use a shared rule, specify `include: rule_name` in your field or mapping configuration. The referenced rule will be merged into the current mapping context.

**Example (Authority):**
```yaml
100:
  - include: 100_700_mappings
700:
  - include: 100_700_mappings

sharedAuthorityRules:
  100_700_mappings:
    types: PERSON
    parent: WORK
    subfields:
      a: NAME
      d: DATE
```

**Example (Bibliographic):**
```yaml

010:
  - include: current_instance_identifier
020:
  - include: current_instance_identifier

bibSharedRules:
  current_instance_identifier:
    types: IDENTIFIER
    parent: INSTANCE
    predicate: MAP
    subfields:
      a: NAME
```

This mechanism helps avoid duplication and keeps your configuration DRY (Don't Repeat Yourself).

## 4. Java-based Mappings for MARC to Graph Conversion

For complex mapping requirements that cannot be fully expressed in the configuration files, the system provides a way
to implement custom mapping logic using Java interfaces. There are two main extension points:

### 4.1 AdditionalMapper
**Purpose:** Allows you to provide additional mapping logic that is executed after the standard configuration-based
mapping has been applied for a MARC field.

**Typical Use Cases:**
  - Enriching the mapped data with extra triples or properties
  - Overriding or supplementing the default mapping for specific tags or conditions
  - Handling complex mapping rules not representable in configuration files

**How to Use:**
  - Implement the `AdditionalMapper` interface:
    ```java
    public interface AdditionalMapper {
      List<String> getTags();
      boolean canMap(Marc4LdRules.FieldRule fieldRule);
      void map(MarcData marcData, Resource mappedSofar);
    }
    ```
  - The `Resource mappedSofar` parameter in the `map` method represents the resource as it has been mapped so far based on the configuration. In your custom mapper, you can add, modify, or delete properties and edges on this resource.
  - Register your implementation as a Spring bean so it is discovered and executed by the system.

### 4.2 CustomMapper
**Purpose:** Allows you to provide a complete mapping from scratch, bypassing the configuration-based mapping for a record.

**Typical Use Cases:**
  - Implementing highly specialized or non-standard mappings
  - Mapping records that require logic not possible with configuration or AdditionalMapper

**How to Use:**
Implement the `CustomMapper` interface:

```java
  public interface CustomMapper {
    boolean isApplicable(org.marc4j.marc.Record marcRecord);
    void map(org.marc4j.marc.Record marcRecord, Resource instance);
  }
```

- The `Resource instance` parameter in the `map` method is the root Instance resource. In your custom mapper, you can add additional connections (edges) and properties to this Instance resource to represent complex relationships or data not handled by configuration.
- Register your implementation as a Spring bean. The system will invoke your mapper for applicable records.

**Note:**
- `AdditionalMapper` is executed after the config-based mapping for a field, and can modify or enrich the mapped resource.
- `CustomMapper` is executed independently and is responsible for the entire mapping of a record if applicable.
- Both interfaces allow you to extend the system for advanced mapping needs.

See the `org.folio.marc4ld.service.marc2ld.mapper` package for examples and further details.

## 5. Java-based Mappings for Graph to MARC Conversion

For complex or advanced scenarios in graph-to-MARC (Linked Data to MARC) conversion, you can implement custom mapping
logic using the following interfaces:

### 5.1 CustomDataFieldsMapper
**Purpose:** Handles complex or non-configurable conversions from Linked Data edges to MARC data fields. These mappers
operate independently of configuration-driven logic.

**How to Use:**
Implement the `CustomDataFieldsMapper` interface:
```java
  public interface CustomDataFieldsMapper extends Predicate<ResourceEdge>, Function<ResourceEdge, DataField> {
    default Optional<DataField> map(ResourceEdge resourceEdge) {
      return Optional.of(resourceEdge)
        .filter(this)
        .map(this);
    }
  }
```

Register your implementation as a Spring bean. The system will invoke your mapper for applicable edges.

### 5.2 CustomControlFieldsMapper
**Purpose:** Handles complex or non-configurable conversions from Linked Data resources to MARC control fields. These
mappers operate independently of configuration-driven logic.

**How to Use:**
Implement the `CustomControlFieldsMapper` interface:

  ```java
  public interface CustomControlFieldsMapper {
    void map(Resource resource, ControlFieldsBuilder controlFieldsBuilder);
  }
  ```
Register your implementation as a Spring bean. The system will invoke your mapper for applicable resources.

### 5.3 AdditionalDataFieldsMapper
**Purpose:** Provides additional mapping logic that is executed after the standard configuration-based mapping for a
Linked Data edge to a MARC data field. Useful for enriching, overriding, or supplementing the default mapping.

**How to Use:**
Implement the `AdditionalDataFieldsMapper` interface:
  ```java
  public interface AdditionalDataFieldsMapper extends Predicate<ResourceEdge>, BiFunction<ResourceEdge, DataField, DataField> {
    default DataField map(ResourceEdge resourceEdge, DataField mappedSoFar) {
      if (this.test(resourceEdge)) {
        return this.apply(resourceEdge, mappedSoFar);
      }
      return mappedSoFar;
    }
  }
  ```
Register your implementation as a Spring bean. The system will invoke your mapper after config-based mapping.

**Note:**
- `CustomDataFieldsMapper` and `CustomControlFieldsMapper` are responsible for providing custom fields from scratch, independent of configuration.
- `AdditionalDataFieldsMapper` is executed after config-based mapping and can modify or enrich the mapped data field.
- These interfaces allow you to extend the system for advanced graph-to-MARC mapping needs.

See the `org.folio.marc4ld.service.ld2marc.mapper` package for examples and further details.
