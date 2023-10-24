package org.folio.marc2ld.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.NON_SORT_NUM;
import static org.folio.ld.dictionary.PropertyDictionary.NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.SUBTITLE;
import static org.folio.ld.dictionary.PropertyDictionary.VARIANT_TYPE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PARALLEL_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.VARIANT_TITLE;
import static org.folio.marc2ld.mapper.test.TestUtil.loadResourceAsString;

import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.marc2ld.configuration.ObjectMapperConfig;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.configuration.property.YamlPropertySourceFactory;
import org.folio.marc2ld.model.Resource;
import org.folio.marc2ld.model.ResourceEdge;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@AutoConfigureMockMvc
@EnableConfigurationProperties
@SpringBootTest(classes = {Marc2BibframeMapperImpl.class, Marc2BibframeRules.class, ObjectMapperConfig.class,
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
    assertThat(result.getDoc()).isNull();
    assertThat(result.getOutgoingEdges()).hasSize(3);
    var edgeIterator = result.getOutgoingEdges().iterator();
    validateTitle(edgeIterator.next(), result);
    validateVariantTitle(edgeIterator.next(), result);
    validateParallelTitle(edgeIterator.next(), result);
  }

  private void validateTitle(ResourceEdge edge1, Resource result) {
    assertThat(edge1.getId()).isNotNull();
    assertThat(edge1.getId().getSourceHash()).isEqualTo(result.getResourceHash());
    assertThat(edge1.getId().getTargetHash()).isEqualTo(edge1.getTarget().getResourceHash());
    assertThat(edge1.getId().getPredicateHash()).isEqualTo(edge1.getPredicate().getHash());
    assertThat(edge1.getPredicate().getHash()).isEqualTo(PredicateDictionary.TITLE.getHash());
    assertThat(edge1.getPredicate().getUri()).isEqualTo(PredicateDictionary.TITLE.getUri());
    assertThat(edge1.getTarget().getResourceHash()).isNotNull();
    assertThat(edge1.getTarget().getLabel()).isEqualTo("Instance MainTitle");
    assertThat(edge1.getTarget().getTypes()).containsExactly(TITLE);
    assertThat(edge1.getTarget().getOutgoingEdges()).isEmpty();
    assertThat(edge1.getTarget().getDoc()).hasSize(5);
    assertThat(edge1.getTarget().getDoc().has(MAIN_TITLE.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(MAIN_TITLE.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(MAIN_TITLE.getValue()).get(0).asText()).isEqualTo("Instance MainTitle");
    assertThat(edge1.getTarget().getDoc().has(SUBTITLE.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(SUBTITLE.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(SUBTITLE.getValue()).get(0).asText()).isEqualTo("Instance SubTitle");
    assertThat(edge1.getTarget().getDoc().has(PART_NUMBER.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(PART_NUMBER.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(PART_NUMBER.getValue()).get(0).asText()).isEqualTo("8");
    assertThat(edge1.getTarget().getDoc().has(PART_NAME.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(PART_NAME.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(PART_NAME.getValue()).get(0).asText()).isEqualTo("Instance PartName");
    assertThat(edge1.getTarget().getDoc().has(NON_SORT_NUM.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(NON_SORT_NUM.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(NON_SORT_NUM.getValue()).get(0).asText()).isEqualTo("7");
  }

  private void validateVariantTitle(ResourceEdge edge1, Resource result) {
    assertThat(edge1.getId()).isNotNull();
    assertThat(edge1.getId().getSourceHash()).isEqualTo(result.getResourceHash());
    assertThat(edge1.getId().getTargetHash()).isEqualTo(edge1.getTarget().getResourceHash());
    assertThat(edge1.getId().getPredicateHash()).isEqualTo(edge1.getPredicate().getHash());
    assertThat(edge1.getPredicate().getHash()).isEqualTo(PredicateDictionary.TITLE.getHash());
    assertThat(edge1.getPredicate().getUri()).isEqualTo(PredicateDictionary.TITLE.getUri());
    assertThat(edge1.getTarget().getResourceHash()).isNotNull();
    assertThat(edge1.getTarget().getLabel()).isEqualTo("Variant-MainTitle");
    assertThat(edge1.getTarget().getTypes()).containsExactly(VARIANT_TITLE);
    assertThat(edge1.getTarget().getOutgoingEdges()).isEmpty();
    assertThat(edge1.getTarget().getDoc()).hasSize(7);
    assertThat(edge1.getTarget().getDoc().has(MAIN_TITLE.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(MAIN_TITLE.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(MAIN_TITLE.getValue()).get(0).asText()).isEqualTo("Variant-MainTitle");
    assertThat(edge1.getTarget().getDoc().has(SUBTITLE.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(SUBTITLE.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(SUBTITLE.getValue()).get(0).asText()).isEqualTo("Variant-SubTitle");
    assertThat(edge1.getTarget().getDoc().has(PART_NUMBER.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(PART_NUMBER.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(PART_NUMBER.getValue()).get(0).asText()).isEqualTo("5");
    assertThat(edge1.getTarget().getDoc().has(PART_NAME.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(PART_NAME.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(PART_NAME.getValue()).get(0).asText()).isEqualTo("Variant-PartName");
    assertThat(edge1.getTarget().getDoc().has(DATE.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(DATE.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(DATE.getValue()).get(0).asText()).isEqualTo("2023-02-02");
    assertThat(edge1.getTarget().getDoc().has(NOTE.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(NOTE.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(NOTE.getValue()).get(0).asText()).isEqualTo("Variant-Note");
    assertThat(edge1.getTarget().getDoc().has(VARIANT_TYPE.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(VARIANT_TYPE.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(VARIANT_TYPE.getValue()).get(0).asText()).isEqualTo("9");
  }

  private void validateParallelTitle(ResourceEdge edge1, Resource result) {
    assertThat(edge1.getId()).isNotNull();
    assertThat(edge1.getId().getSourceHash()).isEqualTo(result.getResourceHash());
    assertThat(edge1.getId().getTargetHash()).isEqualTo(edge1.getTarget().getResourceHash());
    assertThat(edge1.getId().getPredicateHash()).isEqualTo(edge1.getPredicate().getHash());
    assertThat(edge1.getPredicate().getHash()).isEqualTo(PredicateDictionary.TITLE.getHash());
    assertThat(edge1.getPredicate().getUri()).isEqualTo(PredicateDictionary.TITLE.getUri());
    assertThat(edge1.getTarget().getResourceHash()).isNotNull();
    assertThat(edge1.getTarget().getLabel()).isEqualTo("Parallel-MainTitle");
    assertThat(edge1.getTarget().getTypes()).containsExactly(PARALLEL_TITLE);
    assertThat(edge1.getTarget().getOutgoingEdges()).isEmpty();
    assertThat(edge1.getTarget().getDoc()).hasSize(6);
    assertThat(edge1.getTarget().getDoc().has(MAIN_TITLE.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(MAIN_TITLE.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(MAIN_TITLE.getValue()).get(0).asText()).isEqualTo("Parallel-MainTitle");
    assertThat(edge1.getTarget().getDoc().has(SUBTITLE.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(SUBTITLE.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(SUBTITLE.getValue()).get(0).asText()).isEqualTo("Parallel-SubTitle");
    assertThat(edge1.getTarget().getDoc().has(PART_NUMBER.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(PART_NUMBER.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(PART_NUMBER.getValue()).get(0).asText()).isEqualTo("6");
    assertThat(edge1.getTarget().getDoc().has(PART_NAME.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(PART_NAME.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(PART_NAME.getValue()).get(0).asText()).isEqualTo("Parallel-PartName");
    assertThat(edge1.getTarget().getDoc().has(DATE.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(DATE.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(DATE.getValue()).get(0).asText()).isEqualTo("2023-01-01");
    assertThat(edge1.getTarget().getDoc().has(NOTE.getValue())).isTrue();
    assertThat(edge1.getTarget().getDoc().get(NOTE.getValue())).hasSize(1);
    assertThat(edge1.getTarget().getDoc().get(NOTE.getValue()).get(0).asText()).isEqualTo("Parallel-Note");
  }
}
