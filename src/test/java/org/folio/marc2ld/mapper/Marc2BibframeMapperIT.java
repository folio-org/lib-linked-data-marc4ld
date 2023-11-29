package org.folio.marc2ld.mapper;


import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ACCESS_LOCATION;
import static org.folio.ld.dictionary.PredicateDictionary.CARRIER;
import static org.folio.ld.dictionary.PredicateDictionary.COPYRIGHT;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.MEDIA;
import static org.folio.ld.dictionary.PredicateDictionary.PE_DISTRIBUTION;
import static org.folio.ld.dictionary.PredicateDictionary.PE_MANUFACTURE;
import static org.folio.ld.dictionary.PredicateDictionary.PE_PRODUCTION;
import static org.folio.ld.dictionary.PredicateDictionary.PE_PUBLICATION;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.DIMENSIONS;
import static org.folio.ld.dictionary.PropertyDictionary.EAN_VALUE;
import static org.folio.ld.dictionary.PropertyDictionary.EDITION_STATEMENT;
import static org.folio.ld.dictionary.PropertyDictionary.EXTENT;
import static org.folio.ld.dictionary.PropertyDictionary.ISSUANCE;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LCNAF_ID;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.LOCAL_ID_VALUE;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.NON_SORT_NUM;
import static org.folio.ld.dictionary.PropertyDictionary.NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.PROJECTED_PROVISION_DATE;
import static org.folio.ld.dictionary.PropertyDictionary.QUALIFIER;
import static org.folio.ld.dictionary.PropertyDictionary.RESPONSIBILITY_STATEMENT;
import static org.folio.ld.dictionary.PropertyDictionary.SIMPLE_PLACE;
import static org.folio.ld.dictionary.PropertyDictionary.SUBTITLE;
import static org.folio.ld.dictionary.PropertyDictionary.TERM;
import static org.folio.ld.dictionary.PropertyDictionary.VARIANT_TYPE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ANNOTATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.COPYRIGHT_EVENT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_EAN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_ISBN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LOCAL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_UNKNOWN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PARALLEL_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PROVIDER_EVENT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.VARIANT_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc2ld.mapper.test.TestUtil.loadResourceAsString;

import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc2ld.configuration.ObjectMapperBackupConfig;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.configuration.property.YamlPropertySourceFactory;
import org.folio.marc2ld.mapper.condition.ConditionCheckerImpl;
import org.folio.marc2ld.mapper.field.FieldMapperImpl;
import org.folio.marc2ld.mapper.field.property.PropertyMapperImpl;
import org.folio.marc2ld.model.Resource;
import org.folio.marc2ld.model.ResourceEdge;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@AutoConfigureMockMvc
@EnableConfigurationProperties
@SpringBootTest(classes = {Marc2BibframeMapperImpl.class, ConditionCheckerImpl.class, FieldMapperImpl.class,
  PropertyMapperImpl.class, Marc2BibframeRules.class, ObjectMapperBackupConfig.class, YamlPropertySourceFactory.class})
class Marc2BibframeMapperIT {

  @Autowired
  private Marc2BibframeMapperImpl marc2BibframeMapper;

  @Test
  void map_shouldReturnNull_ifGivenMarcIsNull() {
    // given
    String marc = null;

    // when
    var result = marc2BibframeMapper.map(marc);

    // then
    assertThat(result).isNull();
  }

  @Test
  void map_shouldReturnCorrectlyMappedEmptyResource() {
    // given
    var marc = loadResourceAsString("empty_marc.jsonl");

    // when
    var result = marc2BibframeMapper.map(marc);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getResourceHash()).isNotNull();
    assertThat(result.getLabel()).isNotEmpty();
    assertThat(result.getDoc()).isEmpty();
    assertThat(result.getInventoryId()).isNull();
    assertThat(result.getSrsId()).isNull();
    assertThat(result.getTypes()).containsExactly(INSTANCE);
    assertThat(result.getOutgoingEdges()).isEmpty();
  }

  @Test
  void map_shouldReturnCorrectlyMappedResourceWithAppendableFieldsOnly() {
    // given
    var marc = loadResourceAsString("marc_appendable_only.jsonl");

    // when
    var resource = marc2BibframeMapper.map(marc);

    // then
    assertThat(resource).isNotNull();
    assertThat(resource.getResourceHash()).isNotNull();
    assertThat(resource.getLabel()).isNotEmpty();
    assertThat(resource.getDoc()).hasSize(1);
    assertThat(resource.getDoc().has(EDITION_STATEMENT.getValue())).isTrue();
    assertThat(resource.getDoc().get(EDITION_STATEMENT.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(EDITION_STATEMENT.getValue()).get(0).asText())
      .isEqualTo("Edition Statement Edition statement2");
    assertThat(resource.getInventoryId()).isNull();
    assertThat(resource.getSrsId()).isNull();
    assertThat(resource.getTypes()).containsExactly(INSTANCE);
    assertThat(resource.getOutgoingEdges()).hasSize(1);
    var workEdge = resource.getOutgoingEdges().iterator().next();
    assertThat(workEdge.getSource()).isEqualTo(resource);
    assertThat(workEdge.getPredicate()).isEqualTo(INSTANTIATES);
    assertThat(workEdge.getTarget().getResourceHash()).isNotNull();
    assertThat(workEdge.getTarget().getLabel()).isNotNull();
    assertThat(workEdge.getTarget().getTypes()).containsExactly(WORK);
    assertThat(workEdge.getTarget().getInventoryId()).isNull();
    assertThat(workEdge.getTarget().getSrsId()).isNull();
    assertThat(workEdge.getTarget().getDoc()).hasSize(1);
    assertThat(workEdge.getTarget().getDoc().has(RESPONSIBILITY_STATEMENT.getValue())).isTrue();
    assertThat(workEdge.getTarget().getDoc().get(RESPONSIBILITY_STATEMENT.getValue())).hasSize(1);
    assertThat(workEdge.getTarget().getDoc().get(RESPONSIBILITY_STATEMENT.getValue()).get(0).asText())
      .isEqualTo("Statement Of Responsibility");
    assertThat(workEdge.getTarget().getOutgoingEdges()).isEmpty();
  }

  @Test
  void map_shouldReturnCorrectlyMappedResource() {
    // given
    var marc = loadResourceAsString("full_marc.jsonl");

    // when
    var result = marc2BibframeMapper.map(marc);

    // then
    assertThat(result).isNotNull();
    validateInstance(result);
    var edgeIterator = result.getOutgoingEdges().iterator();
    validateLccn(edgeIterator.next(), result.getResourceHash(), "2019493854", "current");
    validateLccn(edgeIterator.next(), result.getResourceHash(), "88888888", "canceled or invalid");
    validateLocalId(edgeIterator.next(), result.getResourceHash(), "19861509", "current");
    validateLocalId(edgeIterator.next(), result.getResourceHash(), "09151986", "canceled or invalid");
    validateIsbn(edgeIterator.next(), result.getResourceHash(), "9780143789963", "current");
    validateIsbn(edgeIterator.next(), result.getResourceHash(), "9999999", "canceled or invalid");
    validateEan(edgeIterator.next(), result.getResourceHash(), "111222", "current");
    validateEan(edgeIterator.next(), result.getResourceHash(), "333", "canceled or invalid");
    validateOtherId(edgeIterator.next(), result.getResourceHash(), "20232023", "current");
    validateOtherId(edgeIterator.next(), result.getResourceHash(), "231123", "canceled or invalid");
    validateWork(edgeIterator.next(), result.getResourceHash());
    validateTitle(edgeIterator.next(), result.getResourceHash());
    validateTitle2(edgeIterator.next(), result.getResourceHash());
    validateTitle3(edgeIterator.next(), result.getResourceHash());
    validateVariantTitle(edgeIterator.next(), result.getResourceHash());
    validateParallelTitle(edgeIterator.next(), result.getResourceHash());
    validateProviderEvent(edgeIterator.next(), result.getResourceHash(), PE_MANUFACTURE, "Manufacture261");
    validateProviderEvent(edgeIterator.next(), result.getResourceHash(), PE_PUBLICATION, "Publication262");
    validateProviderEvent(edgeIterator.next(), result.getResourceHash(), PE_PRODUCTION, "Production");
    validateProviderEvent(edgeIterator.next(), result.getResourceHash(), PE_PUBLICATION, "Publication");
    validateProviderEvent(edgeIterator.next(), result.getResourceHash(), PE_DISTRIBUTION, "Distribution");
    validateProviderEvent(edgeIterator.next(), result.getResourceHash(), PE_MANUFACTURE, "Manufacture");
    validateCopyrightDate(edgeIterator.next(), result.getResourceHash());
    validateCategory(edgeIterator.next(), result.getResourceHash(), MEDIA);
    validateCategory(edgeIterator.next(), result.getResourceHash(), CARRIER);
    validateAccessLocation(edgeIterator.next(), result.getResourceHash());
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateInstance(Resource resource) {
    assertThat(resource.getResourceHash()).isNotNull();
    assertThat(resource.getLabel()).isEqualTo("Instance MainTitle");
    assertThat(resource.getDoc()).hasSize(5);
    assertThat(resource.getDoc().has(EDITION_STATEMENT.getValue())).isTrue();
    assertThat(resource.getDoc().get(EDITION_STATEMENT.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(EDITION_STATEMENT.getValue()).get(0).asText())
      .isEqualTo("Edition Statement Edition statement2");
    assertThat(resource.getDoc().has(EXTENT.getValue())).isTrue();
    assertThat(resource.getDoc().get(EXTENT.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(EXTENT.getValue()).get(0).asText()).isEqualTo("extent");
    assertThat(resource.getDoc().has(DIMENSIONS.getValue())).isTrue();
    assertThat(resource.getDoc().get(DIMENSIONS.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(DIMENSIONS.getValue()).get(0).asText()).isEqualTo("dimensions");
    assertThat(resource.getDoc().has(ISSUANCE.getValue())).isTrue();
    assertThat(resource.getDoc().get(ISSUANCE.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(ISSUANCE.getValue()).get(0).asText()).isEqualTo("issuance");
    assertThat(resource.getDoc().has(PROJECTED_PROVISION_DATE.getValue())).isTrue();
    assertThat(resource.getDoc().get(PROJECTED_PROVISION_DATE.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(PROJECTED_PROVISION_DATE.getValue()).get(0).asText()).isEqualTo(
      "projectedProvisionDate");
    assertThat(resource.getInventoryId()).hasToString("2165ef4b-001f-46b3-a60e-52bcdeb3d5a1");
    assertThat(resource.getSrsId()).hasToString("43d58061-decf-4d74-9747-0e1c368e861b");
    assertThat(resource.getTypes()).containsExactly(INSTANCE);
  }

  @Test
  void twoMappedResources_shouldContainWorkWithDifferentIds() {
    // given
    var marc1 = loadResourceAsString("full_marc.jsonl");
    var marc2 = loadResourceAsString("short_marc.jsonl");

    // when
    var result1 = marc2BibframeMapper.map(marc1);
    var result2 = marc2BibframeMapper.map(marc2);

    // then
    var work1opt = result1.getOutgoingEdges().stream().filter(re -> INSTANTIATES.equals(re.getPredicate())).findFirst();
    var work2opt = result2.getOutgoingEdges().stream().filter(re -> INSTANTIATES.equals(re.getPredicate())).findFirst();
    assertThat(work1opt).isPresent();
    assertThat(work2opt).isPresent();
    assertThat(work1opt.get().getTarget().getResourceHash()).isNotEqualTo(work2opt.get().getTarget().getResourceHash());
  }

  private void validateLccn(ResourceEdge edge, Long parentHash, String number, String status) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(MAP.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(MAP.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo(number);
    assertThat(edge.getTarget().getTypes()).containsExactly(ID_LCCN, IDENTIFIER);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NAME.getValue()).get(0).asText()).isEqualTo(number);
    var edgeIterator = edge.getTarget().getOutgoingEdges().iterator();
    validateIdStatus(edgeIterator.next(), edge.getTarget().getResourceHash(), status);
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateLocalId(ResourceEdge edge, Long parentHash, String number, String status) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(MAP.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(MAP.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo(number);
    assertThat(edge.getTarget().getTypes()).containsExactly(ID_LOCAL, IDENTIFIER);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(LOCAL_ID_VALUE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(LOCAL_ID_VALUE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(LOCAL_ID_VALUE.getValue()).get(0).asText()).isEqualTo(number);
    var edgeIterator = edge.getTarget().getOutgoingEdges().iterator();
    validateIdStatus(edgeIterator.next(), edge.getTarget().getResourceHash(), status);
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateIsbn(ResourceEdge edge, Long parentHash, String number, String status) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(MAP.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(MAP.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo(number);
    assertThat(edge.getTarget().getTypes()).containsExactly(ID_ISBN, IDENTIFIER);
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    assertThat(edge.getTarget().getDoc().has(NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NAME.getValue()).get(0).asText()).isEqualTo(number);
    assertThat(edge.getTarget().getDoc().has(QUALIFIER.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(QUALIFIER.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(QUALIFIER.getValue()).get(0).asText()).isEqualTo("(paperback)");
    var edgeIterator = edge.getTarget().getOutgoingEdges().iterator();
    validateIdStatus(edgeIterator.next(), edge.getTarget().getResourceHash(), status);
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateEan(ResourceEdge edge, Long parentHash, String number, String status) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(MAP.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(MAP.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo(number);
    assertThat(edge.getTarget().getTypes()).containsExactly(ID_EAN, IDENTIFIER);
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    assertThat(edge.getTarget().getDoc().has(EAN_VALUE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(EAN_VALUE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(EAN_VALUE.getValue()).get(0).asText()).isEqualTo(number);
    assertThat(edge.getTarget().getDoc().has(QUALIFIER.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(QUALIFIER.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(QUALIFIER.getValue()).get(0).asText()).isEqualTo("eanIdQal");
    var edgeIterator = edge.getTarget().getOutgoingEdges().iterator();
    validateIdStatus(edgeIterator.next(), edge.getTarget().getResourceHash(), status);
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateOtherId(ResourceEdge edge, Long parentHash, String number, String status) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(MAP.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(MAP.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo(number);
    assertThat(edge.getTarget().getTypes()).containsExactly(ID_UNKNOWN, IDENTIFIER);
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    assertThat(edge.getTarget().getDoc().has(NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NAME.getValue()).get(0).asText()).isEqualTo(number);
    assertThat(edge.getTarget().getDoc().has(QUALIFIER.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(QUALIFIER.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(QUALIFIER.getValue()).get(0).asText()).isEqualTo("otherIdQal");
    var edgeIterator = edge.getTarget().getOutgoingEdges().iterator();
    validateIdStatus(edgeIterator.next(), edge.getTarget().getResourceHash(), status);
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateIdStatus(ResourceEdge edge, Long parentHash, String value) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(STATUS.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(STATUS.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo(value);
    assertThat(edge.getTarget().getTypes()).containsExactly(ResourceTypeDictionary.STATUS);
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    assertThat(edge.getTarget().getDoc().has(LABEL.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(LABEL.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(LABEL.getValue()).get(0).asText()).isEqualTo(value);
    assertThat(edge.getTarget().getDoc().has(LINK.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(LINK.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(LINK.getValue()).get(0).asText())
      .isEqualTo("http://id.loc.gov/vocabulary/mstatus/" + (value.equals("canceled or invalid") ? "cancinv" : value));
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateWork(ResourceEdge edge, Long parentHash) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(INSTANTIATES.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(INSTANTIATES.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isNotEmpty();
    assertThat(edge.getTarget().getTypes()).containsExactly(WORK);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(RESPONSIBILITY_STATEMENT.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(RESPONSIBILITY_STATEMENT.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(RESPONSIBILITY_STATEMENT.getValue()).get(0).asText()).isEqualTo(
      "Statement Of Responsibility");
    assertThat(edge.getTarget().getOutgoingEdges()).isNotEmpty();
    var edgeIterator = edge.getTarget().getOutgoingEdges().iterator();
    validatePerson(edgeIterator.next(), edge.getTarget().getResourceHash());
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validatePerson(ResourceEdge edge, Long parentHash) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(PredicateDictionary.CREATOR.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(PredicateDictionary.CREATOR.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo("Author name");
    assertThat(edge.getTarget().getTypes()).containsExactly(PERSON);
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    assertThat(edge.getTarget().getDoc().has(NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NAME.getValue()).get(0).asText()).isEqualTo("Author name");
    assertThat(edge.getTarget().getDoc().has(LCNAF_ID.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(LCNAF_ID.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(LCNAF_ID.getValue()).get(0).asText()).isEqualTo("LCNAF id");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateTitle(ResourceEdge edge, Long parentHash) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(PredicateDictionary.TITLE.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(PredicateDictionary.TITLE.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo("Instance MainTitle");
    assertThat(edge.getTarget().getTypes()).containsExactly(TITLE);
    assertThat(edge.getTarget().getDoc()).hasSize(5);
    assertThat(edge.getTarget().getDoc().has(MAIN_TITLE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(MAIN_TITLE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(MAIN_TITLE.getValue()).get(0).asText()).isEqualTo("Instance MainTitle");
    assertThat(edge.getTarget().getDoc().has(SUBTITLE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(SUBTITLE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(SUBTITLE.getValue()).get(0).asText()).isEqualTo("Instance SubTitle");
    assertThat(edge.getTarget().getDoc().has(PART_NUMBER.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(PART_NUMBER.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(PART_NUMBER.getValue()).get(0).asText()).isEqualTo("8");
    assertThat(edge.getTarget().getDoc().has(PART_NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(PART_NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(PART_NAME.getValue()).get(0).asText()).isEqualTo("Instance PartName");
    assertThat(edge.getTarget().getDoc().has(NON_SORT_NUM.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NON_SORT_NUM.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NON_SORT_NUM.getValue()).get(0).asText()).isEqualTo("7");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateTitle2(ResourceEdge edge, Long parentHash) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(PredicateDictionary.TITLE.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(PredicateDictionary.TITLE.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo("Instance Title empty");
    assertThat(edge.getTarget().getTypes()).containsExactly(TITLE);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(MAIN_TITLE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(MAIN_TITLE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(MAIN_TITLE.getValue()).get(0).asText()).isEqualTo("Instance Title empty");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateTitle3(ResourceEdge edge, Long parentHash) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(PredicateDictionary.TITLE.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(PredicateDictionary.TITLE.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isNotEmpty();
    assertThat(edge.getTarget().getTypes()).containsExactly(TITLE);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(SUBTITLE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(SUBTITLE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(SUBTITLE.getValue()).get(0).asText())
      .isEqualTo("Instance Title empty label");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateVariantTitle(ResourceEdge edge, Long parentHash) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(PredicateDictionary.TITLE.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(PredicateDictionary.TITLE.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo("Variant-MainTitle");
    assertThat(edge.getTarget().getTypes()).containsExactly(VARIANT_TITLE);
    assertThat(edge.getTarget().getDoc()).hasSize(7);
    assertThat(edge.getTarget().getDoc().has(MAIN_TITLE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(MAIN_TITLE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(MAIN_TITLE.getValue()).get(0).asText()).isEqualTo("Variant-MainTitle");
    assertThat(edge.getTarget().getDoc().has(SUBTITLE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(SUBTITLE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(SUBTITLE.getValue()).get(0).asText()).isEqualTo("Variant-SubTitle");
    assertThat(edge.getTarget().getDoc().has(PART_NUMBER.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(PART_NUMBER.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(PART_NUMBER.getValue()).get(0).asText()).isEqualTo("5");
    assertThat(edge.getTarget().getDoc().has(PART_NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(PART_NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(PART_NAME.getValue()).get(0).asText()).isEqualTo("Variant-PartName");
    assertThat(edge.getTarget().getDoc().has(DATE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(DATE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(DATE.getValue()).get(0).asText()).isEqualTo("2023-02-02");
    assertThat(edge.getTarget().getDoc().has(NOTE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NOTE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NOTE.getValue()).get(0).asText()).isEqualTo("Variant-Note");
    assertThat(edge.getTarget().getDoc().has(VARIANT_TYPE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(VARIANT_TYPE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(VARIANT_TYPE.getValue()).get(0).asText()).isEqualTo("9");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateParallelTitle(ResourceEdge edge, Long parentHash) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(PredicateDictionary.TITLE.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(PredicateDictionary.TITLE.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo("Parallel-MainTitle");
    assertThat(edge.getTarget().getTypes()).containsExactly(PARALLEL_TITLE);
    assertThat(edge.getTarget().getDoc()).hasSize(6);
    assertThat(edge.getTarget().getDoc().has(MAIN_TITLE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(MAIN_TITLE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(MAIN_TITLE.getValue()).get(0).asText()).isEqualTo("Parallel-MainTitle");
    assertThat(edge.getTarget().getDoc().has(SUBTITLE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(SUBTITLE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(SUBTITLE.getValue()).get(0).asText()).isEqualTo("Parallel-SubTitle");
    assertThat(edge.getTarget().getDoc().has(PART_NUMBER.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(PART_NUMBER.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(PART_NUMBER.getValue()).get(0).asText()).isEqualTo("6");
    assertThat(edge.getTarget().getDoc().has(PART_NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(PART_NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(PART_NAME.getValue()).get(0).asText()).isEqualTo("Parallel-PartName");
    assertThat(edge.getTarget().getDoc().has(DATE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(DATE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(DATE.getValue()).get(0).asText()).isEqualTo("2023-01-01");
    assertThat(edge.getTarget().getDoc().has(NOTE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NOTE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NOTE.getValue()).get(0).asText()).isEqualTo("Parallel-Note");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateProviderEvent(ResourceEdge edge, Long parentHash, PredicateDictionary expectedPredicate,
                                     String expectedPrefix) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(expectedPredicate.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(expectedPredicate.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo(expectedPrefix + " Name");
    assertThat(edge.getTarget().getTypes()).containsExactly(PROVIDER_EVENT);
    assertThat(edge.getTarget().getDoc()).hasSize(3);
    assertThat(edge.getTarget().getDoc().has(SIMPLE_PLACE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(SIMPLE_PLACE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(SIMPLE_PLACE.getValue()).get(0).asText())
      .isEqualTo(expectedPrefix + " Place");
    assertThat(edge.getTarget().getDoc().has(DATE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(DATE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(DATE.getValue()).get(0).asText()).isEqualTo(expectedPrefix + " Date");
    assertThat(edge.getTarget().getDoc().has(NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NAME.getValue()).get(0).asText()).isEqualTo(expectedPrefix + " Name");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateCopyrightDate(ResourceEdge edge, Long parentHash) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(COPYRIGHT.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(COPYRIGHT.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo("©2018");
    assertThat(edge.getTarget().getTypes()).containsExactly(COPYRIGHT_EVENT);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(DATE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(DATE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(DATE.getValue()).get(0).asText()).isEqualTo("©2018");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateCategory(ResourceEdge edge, Long parentHash, PredicateDictionary predicate) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(predicate.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(predicate.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo(predicate.name() + " term");
    assertThat(edge.getTarget().getTypes()).containsExactly(CATEGORY);
    assertThat(edge.getTarget().getDoc()).hasSize(3);
    assertThat(edge.getTarget().getDoc().has(CODE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(CODE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(CODE.getValue()).get(0).asText()).isEqualTo(predicate.name() + " code");
    assertThat(edge.getTarget().getDoc().has(LINK.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(LINK.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(LINK.getValue()).get(0).asText()).isEqualTo(
      "http://id.loc.gov/vocabulary/" + predicate.name().toLowerCase() + "Types/" + predicate.name() + " code");
    assertThat(edge.getTarget().getDoc().has(TERM.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(TERM.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(TERM.getValue()).get(0).asText()).isEqualTo(predicate.name() + " term");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateAccessLocation(ResourceEdge edge, Long parentHash) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(ACCESS_LOCATION.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(ACCESS_LOCATION.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo("access location URI");
    assertThat(edge.getTarget().getTypes()).containsExactly(ANNOTATION);
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    assertThat(edge.getTarget().getDoc().has(LINK.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(LINK.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(LINK.getValue()).get(0).asText()).isEqualTo("access location URI");
    assertThat(edge.getTarget().getDoc().has(NOTE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NOTE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NOTE.getValue()).get(0).asText()).isEqualTo("access location note");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

}
