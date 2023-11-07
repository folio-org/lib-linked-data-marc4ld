package org.folio.marc2ld.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.PE_PUBLICATION;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.EDITION_STATEMENT;
import static org.folio.ld.dictionary.PropertyDictionary.LCNAF_ID;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.NON_SORT_NUM;
import static org.folio.ld.dictionary.PropertyDictionary.NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.QUALIFIER;
import static org.folio.ld.dictionary.PropertyDictionary.SIMPLE_PLACE;
import static org.folio.ld.dictionary.PropertyDictionary.SUBTITLE;
import static org.folio.ld.dictionary.PropertyDictionary.VARIANT_TYPE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_ISBN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PARALLEL_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PROVIDER_EVENT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.VARIANT_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc2ld.mapper.test.TestUtil.loadResourceAsString;

import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.marc2ld.configuration.ObjectMapperBackupConfig;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.configuration.property.YamlPropertySourceFactory;
import org.folio.marc2ld.model.ResourceEdge;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@AutoConfigureMockMvc
@EnableConfigurationProperties
@SpringBootTest(classes = {Marc2BibframeMapperImpl.class, Marc2BibframeRules.class, ObjectMapperBackupConfig.class,
  YamlPropertySourceFactory.class})
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
  void map_shouldReturnCorrectlyMappedResource() {
    // given
    var marc = loadResourceAsString("full_marc_sample.jsonl");

    // when
    var result = marc2BibframeMapper.map(marc);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getResourceHash()).isNotNull();
    assertThat(result.getLabel()).isEqualTo("Instance MainTitle");
    assertThat(result.getTypes()).containsExactly(INSTANCE);
    assertThat(result.getDoc()).hasSize(1);
    assertThat(result.getDoc().has(EDITION_STATEMENT.getValue())).isTrue();
    assertThat(result.getDoc().get(EDITION_STATEMENT.getValue())).hasSize(1);
    assertThat(result.getDoc().get(EDITION_STATEMENT.getValue()).get(0).asText())
      .isEqualTo("Edition Statement Edition statement2");
    var edgeIterator = result.getOutgoingEdges().iterator();
    validateLccn(edgeIterator.next(), result.getResourceHash());
    validateIsbn(edgeIterator.next(), result.getResourceHash());
    validateWork(edgeIterator.next(), result.getResourceHash());
    validateTitle(edgeIterator.next(), result.getResourceHash());
    validateTitle2(edgeIterator.next(), result.getResourceHash());
    validateTitle3(edgeIterator.next(), result.getResourceHash());
    validateVariantTitle(edgeIterator.next(), result.getResourceHash());
    validateParallelTitle(edgeIterator.next(), result.getResourceHash());
    validateProviderEvent(edgeIterator.next(), result.getResourceHash(), PE_PUBLICATION, "Publication262");
    validateProviderEvent(edgeIterator.next(), result.getResourceHash(), PE_PUBLICATION, "Publication");
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateLccn(ResourceEdge edge, Long parentHash) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(MAP.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(MAP.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo("2019493854");
    assertThat(edge.getTarget().getTypes()).containsExactly(ID_LCCN, IDENTIFIER);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NAME.getValue()).get(0).asText()).isEqualTo("2019493854");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateIsbn(ResourceEdge edge, Long parentHash) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(MAP.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(MAP.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo("9780143789963");
    assertThat(edge.getTarget().getTypes()).containsExactly(ID_ISBN, IDENTIFIER);
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    assertThat(edge.getTarget().getDoc().has(NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NAME.getValue()).get(0).asText()).isEqualTo("9780143789963");
    assertThat(edge.getTarget().getDoc().has(QUALIFIER.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(QUALIFIER.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(QUALIFIER.getValue()).get(0).asText()).isEqualTo("(paperback)");
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
    assertThat(edge.getTarget().getLabel()).isEqualTo("");
    assertThat(edge.getTarget().getTypes()).containsExactly(WORK);
    assertThat(edge.getTarget().getDoc()).isNull();
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
    assertThat(edge.getTarget().getLabel()).isEmpty();
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
}
