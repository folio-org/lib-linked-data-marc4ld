package org.folio.marc4ld.mapper;

import static java.util.Map.entry;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ACCESS_LOCATION;
import static org.folio.ld.dictionary.PredicateDictionary.AUTHOR;
import static org.folio.ld.dictionary.PredicateDictionary.BROADCASTER;
import static org.folio.ld.dictionary.PredicateDictionary.CARRIER;
import static org.folio.ld.dictionary.PredicateDictionary.CLASSIFICATION;
import static org.folio.ld.dictionary.PredicateDictionary.CLIENT;
import static org.folio.ld.dictionary.PredicateDictionary.CONTENT;
import static org.folio.ld.dictionary.PredicateDictionary.CONTRIBUTOR;
import static org.folio.ld.dictionary.PredicateDictionary.COPYRIGHT;
import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.DESIGNER;
import static org.folio.ld.dictionary.PredicateDictionary.EDITOR;
import static org.folio.ld.dictionary.PredicateDictionary.FILMMAKER;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.GENRE;
import static org.folio.ld.dictionary.PredicateDictionary.GOVERNMENT_PUBLICATION;
import static org.folio.ld.dictionary.PredicateDictionary.GRAPHIC_TECHNICIAN;
import static org.folio.ld.dictionary.PredicateDictionary.HONOUREE;
import static org.folio.ld.dictionary.PredicateDictionary.HOST;
import static org.folio.ld.dictionary.PredicateDictionary.ILLUSTRATOR;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.INSTRUCTOR;
import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.PredicateDictionary.JUDGE;
import static org.folio.ld.dictionary.PredicateDictionary.LAB_DIRECTOR;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.MEDIA;
import static org.folio.ld.dictionary.PredicateDictionary.MEDIUM;
import static org.folio.ld.dictionary.PredicateDictionary.NARRATOR;
import static org.folio.ld.dictionary.PredicateDictionary.ONSCREEN_PRESENTER;
import static org.folio.ld.dictionary.PredicateDictionary.PATRON;
import static org.folio.ld.dictionary.PredicateDictionary.PE_DISTRIBUTION;
import static org.folio.ld.dictionary.PredicateDictionary.PE_MANUFACTURE;
import static org.folio.ld.dictionary.PredicateDictionary.PE_PRODUCTION;
import static org.folio.ld.dictionary.PredicateDictionary.PE_PUBLICATION;
import static org.folio.ld.dictionary.PredicateDictionary.PROVIDER_PLACE;
import static org.folio.ld.dictionary.PredicateDictionary.RADIO_DIRECTOR;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
import static org.folio.ld.dictionary.PropertyDictionary.ACCESSIBILITY_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.ADDITIONAL_PHYSICAL_FORM;
import static org.folio.ld.dictionary.PropertyDictionary.AFFILIATION;
import static org.folio.ld.dictionary.PropertyDictionary.ASSIGNER;
import static org.folio.ld.dictionary.PropertyDictionary.ATTRIBUTION;
import static org.folio.ld.dictionary.PropertyDictionary.AUTHORITY_LINK;
import static org.folio.ld.dictionary.PropertyDictionary.BIBLIOGRAPHY_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.CHRONOLOGICAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.CITATION_COVERAGE;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.COMPUTER_DATA_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.CONTROL_FIELD;
import static org.folio.ld.dictionary.PropertyDictionary.CREDITS_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.DATA_QUALITY;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.DATES_OF_PUBLICATION_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.DATE_END;
import static org.folio.ld.dictionary.PropertyDictionary.DATE_START;
import static org.folio.ld.dictionary.PropertyDictionary.DESCRIPTION_SOURCE_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.DIMENSIONS;
import static org.folio.ld.dictionary.PropertyDictionary.EAN_VALUE;
import static org.folio.ld.dictionary.PropertyDictionary.EDITION_STATEMENT;
import static org.folio.ld.dictionary.PropertyDictionary.ENTITY_AND_ATTRIBUTE_INFORMATION;
import static org.folio.ld.dictionary.PropertyDictionary.EQUIVALENT;
import static org.folio.ld.dictionary.PropertyDictionary.EXHIBITIONS_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.EXTENT;
import static org.folio.ld.dictionary.PropertyDictionary.FIELD_LINK;
import static org.folio.ld.dictionary.PropertyDictionary.FORMER_TITLE_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.FORM_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.FUNDING_INFORMATION;
import static org.folio.ld.dictionary.PropertyDictionary.GENERAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_AREA_CODE;
import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_COVERAGE;
import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GOVERNING_ACCESS_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.INFORMATION_ABOUT_DOCUMENTATION;
import static org.folio.ld.dictionary.PropertyDictionary.INFORMATION_RELATING_TO_COPYRIGHT_STATUS;
import static org.folio.ld.dictionary.PropertyDictionary.ISSUANCE;
import static org.folio.ld.dictionary.PropertyDictionary.ISSUANCE_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.ISSUING_BODY;
import static org.folio.ld.dictionary.PropertyDictionary.ITEM_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LANGUAGE;
import static org.folio.ld.dictionary.PropertyDictionary.LANGUAGE_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.LCNAF_ID;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.LINKAGE;
import static org.folio.ld.dictionary.PropertyDictionary.LOCAL_ID_VALUE;
import static org.folio.ld.dictionary.PropertyDictionary.LOCATION_OF_EVENT;
import static org.folio.ld.dictionary.PropertyDictionary.LOCATION_OF_ORIGINALS_DUPLICATES;
import static org.folio.ld.dictionary.PropertyDictionary.LOCATION_OF_OTHER_ARCHIVAL_MATERIAL;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.MATERIALS_SPECIFIED;
import static org.folio.ld.dictionary.PropertyDictionary.MISC_INFO;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.NAME_ALTERNATIVE;
import static org.folio.ld.dictionary.PropertyDictionary.NON_SORT_NUM;
import static org.folio.ld.dictionary.PropertyDictionary.NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.NUMERATION;
import static org.folio.ld.dictionary.PropertyDictionary.ORIGINAL_VERSION_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.OTHER_EVENT_INFORMATION;
import static org.folio.ld.dictionary.PropertyDictionary.PARTICIPANT_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.PHYSICAL_DESCRIPTION;
import static org.folio.ld.dictionary.PropertyDictionary.PROJECTED_PROVISION_DATE;
import static org.folio.ld.dictionary.PropertyDictionary.PROVIDER_DATE;
import static org.folio.ld.dictionary.PropertyDictionary.PUBLICATION_FREQUENCY;
import static org.folio.ld.dictionary.PropertyDictionary.QUALIFIER;
import static org.folio.ld.dictionary.PropertyDictionary.REFERENCES;
import static org.folio.ld.dictionary.PropertyDictionary.RELATED_PARTS;
import static org.folio.ld.dictionary.PropertyDictionary.RELATOR_CODE;
import static org.folio.ld.dictionary.PropertyDictionary.RELATOR_TERM;
import static org.folio.ld.dictionary.PropertyDictionary.REPRODUCTION_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.RESPONSIBILITY_STATEMENT;
import static org.folio.ld.dictionary.PropertyDictionary.SCALE_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.SIMPLE_PLACE;
import static org.folio.ld.dictionary.PropertyDictionary.SOURCE;
import static org.folio.ld.dictionary.PropertyDictionary.STUDY_PROGRAM_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.SUBORDINATE_UNIT;
import static org.folio.ld.dictionary.PropertyDictionary.SUBTITLE;
import static org.folio.ld.dictionary.PropertyDictionary.SUMMARY;
import static org.folio.ld.dictionary.PropertyDictionary.SUPPLEMENT;
import static org.folio.ld.dictionary.PropertyDictionary.SYSTEM_DETAILS;
import static org.folio.ld.dictionary.PropertyDictionary.SYSTEM_DETAILS_ACCESS_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.TABLE_OF_CONTENTS;
import static org.folio.ld.dictionary.PropertyDictionary.TARGET_AUDIENCE;
import static org.folio.ld.dictionary.PropertyDictionary.TERM;
import static org.folio.ld.dictionary.PropertyDictionary.TITLES;
import static org.folio.ld.dictionary.PropertyDictionary.TYPE_OF_REPORT;
import static org.folio.ld.dictionary.PropertyDictionary.VARIANT_TYPE;
import static org.folio.ld.dictionary.PropertyDictionary.WITH_NOTE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ANNOTATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.COPYRIGHT_EVENT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FAMILY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FORM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_EAN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_ISBN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LOCAL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_UNKNOWN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.JURISDICTION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.MEETING;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PARALLEL_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PROVIDER_EVENT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TEMPORAL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TOPIC;
import static org.folio.ld.dictionary.ResourceTypeDictionary.VARIANT_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.service.marc2ld.Marc2BibframeMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Marc2BibframeMapperIT {

  @Autowired
  private Marc2BibframeMapperImpl marc2BibframeMapper;

  @Test
  void map_shouldReturnNull_ifGivenMarcIsNull() {
    // given
    String marc = null;

    // when
    var result = marc2BibframeMapper.fromMarcJson(marc);

    // then
    assertThat(result).isNull();
  }

  @Test
  void map_shouldReturnCorrectlyMappedEmptyResource() {
    // given
    var marc = loadResourceAsString("empty_marc.jsonl");

    // when
    var result = marc2BibframeMapper.fromMarcJson(marc);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getResourceHash()).isNotNull();
    assertThat(result.getLabel()).isNotEmpty();
    assertThat(result.getDoc()).isEmpty();
    assertThat(result.getInventoryId()).isNull();
    assertThat(result.getSrsId()).isNull();
    assertThat(result.getTypes()).containsOnly(INSTANCE);
    assertThat(result.getOutgoingEdges()).isEmpty();
  }

  @Test
  void map_shouldReturnCorrectlyMappedResourceWithAppendableFieldsOnly() {
    // given
    var marc = loadResourceAsString("marc_appendable_only.jsonl");

    // when
    var resource = marc2BibframeMapper.fromMarcJson(marc);

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
    assertThat(resource.getTypes()).containsOnly(INSTANCE);
    assertThat(resource.getOutgoingEdges()).hasSize(1);
    var workEdge = resource.getOutgoingEdges().iterator().next();
    assertThat(workEdge.getSource()).isEqualTo(resource);
    assertThat(workEdge.getPredicate()).isEqualTo(INSTANTIATES);
    assertThat(workEdge.getTarget().getResourceHash()).isNotNull();
    assertThat(workEdge.getTarget().getLabel()).isNotNull();
    assertThat(workEdge.getTarget().getTypes()).containsOnly(WORK);
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
    var result = marc2BibframeMapper.fromMarcJson(marc);

    // then
    assertThat(result).isNotNull();
    validateInstance(result);
    assertThat(result.getOutgoingEdges()).isNotEmpty();
    var edgeIterator = result.getOutgoingEdges().iterator();
    validateLccn(edgeIterator.next(), result.getResourceHash(), "2019493854", "current");
    validateLccn(edgeIterator.next(), result.getResourceHash(), "88888888", "canceled or invalid");
    validateLocalId(edgeIterator.next(), result.getResourceHash(), "19861509", "current");
    validateLocalId(edgeIterator.next(), result.getResourceHash(), "09151986", "canceled or invalid");
    validateIsbn(edgeIterator.next(), result.getResourceHash(), "9780143789963", "current");
    validateIsbn(edgeIterator.next(), result.getResourceHash(), "9999999", "canceled or invalid");
    validateEan(edgeIterator.next(), result.getResourceHash(), "111222", "current");
    validateEan(edgeIterator.next(), result.getResourceHash(), "333", "canceled or invalid");
    validateOtherId(edgeIterator.next(), result.getResourceHash(), "202320239999", "current");
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
    validateCategory(edgeIterator.next(), result.getResourceHash(), MEDIA, "mediaTypes");
    validateCategory(edgeIterator.next(), result.getResourceHash(), CARRIER, "carriers");
    validateAccessLocation(edgeIterator.next(), result.getResourceHash());
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateInstance(Resource resource) {
    assertThat(resource.getResourceHash()).isNotNull();
    assertThat(resource.getLabel()).isEqualTo("Instance MainTitle");
    assertThat(resource.getDoc()).hasSize(34);
    validateInstanceNotes(resource);
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
    assertThat(resource.getTypes()).containsOnly(INSTANCE);
  }

  @Test
  void twoMappedResources_shouldContainWorkWithDifferentIds() {
    // given
    var marc1 = loadResourceAsString("full_marc.jsonl");
    var marc2 = loadResourceAsString("short_marc.jsonl");

    // when
    var result1 = marc2BibframeMapper.fromMarcJson(marc1);
    var result2 = marc2BibframeMapper.fromMarcJson(marc2);

    // then
    var work1opt = result1.getOutgoingEdges().stream().filter(re -> INSTANTIATES.equals(re.getPredicate())).findFirst();
    var work2opt = result2.getOutgoingEdges().stream().filter(re -> INSTANTIATES.equals(re.getPredicate())).findFirst();
    assertThat(work1opt).isPresent();
    assertThat(work2opt).isPresent();
    assertThat(work1opt.get().getTarget().getResourceHash()).isNotEqualTo(work2opt.get().getTarget().getResourceHash());
  }

  private static Stream<Arguments> provideMarcAndPredicates() {
    return Stream.of(
      Arguments.of(
        loadResourceAsString("code_relations.jsonl"),
        List.of(AUTHOR, BROADCASTER, CLIENT, DESIGNER, EDITOR, HOST)
      ),
      Arguments.of(
        loadResourceAsString("text_relations.jsonl"),
        List.of(FILMMAKER, GRAPHIC_TECHNICIAN, ILLUSTRATOR, LAB_DIRECTOR, HONOUREE, INSTRUCTOR)
      ),
      Arguments.of(
        loadResourceAsString("code_and_text_relations.jsonl"),
        List.of(JUDGE, MEDIUM, NARRATOR, ONSCREEN_PRESENTER, PATRON, RADIO_DIRECTOR)
      )
    );
  }

  @ParameterizedTest
  @MethodSource("provideMarcAndPredicates")
  void map_shouldReturnCorrectlyMappedRelations(String marc, List<PredicateDictionary> expectedPredicates) {
    //when
    var result = marc2BibframeMapper.fromMarcJson(marc);

    //then
    var work = result.getOutgoingEdges().iterator().next().getTarget();
    assertThat(work.getOutgoingEdges()).hasSize(12);

    var workEdgesIterator = work.getOutgoingEdges().iterator();
    var workCreatorPerson = workEdgesIterator.next().getTarget();
    var creatorPersonRelation = workEdgesIterator.next();
    assertThat(creatorPersonRelation.getPredicate()).isEqualTo(expectedPredicates.get(0));
    assertThat(creatorPersonRelation.getTarget()).isEqualTo(workCreatorPerson);

    var workCreatorOrganization = workEdgesIterator.next().getTarget();
    var creatorOrganizationRelation = workEdgesIterator.next();
    assertThat(creatorOrganizationRelation.getPredicate()).isEqualTo(expectedPredicates.get(1));
    assertThat(creatorOrganizationRelation.getTarget()).isEqualTo(workCreatorOrganization);

    var workCreatorMeeting = workEdgesIterator.next().getTarget();
    var creatorMeetingRelation = workEdgesIterator.next();
    assertThat(creatorMeetingRelation.getPredicate()).isEqualTo(expectedPredicates.get(2));
    assertThat(creatorMeetingRelation.getTarget()).isEqualTo(workCreatorMeeting);

    var workContributorPerson = workEdgesIterator.next().getTarget();
    var contributorPersonRelation = workEdgesIterator.next();
    assertThat(contributorPersonRelation.getPredicate()).isEqualTo(expectedPredicates.get(3));
    assertThat(contributorPersonRelation.getTarget()).isEqualTo(workContributorPerson);

    var workContributorOrganization = workEdgesIterator.next().getTarget();
    var contributorOrganizationRelation = workEdgesIterator.next();
    assertThat(contributorOrganizationRelation.getPredicate()).isEqualTo(expectedPredicates.get(4));
    assertThat(contributorOrganizationRelation.getTarget()).isEqualTo(workContributorOrganization);

    var workContributorMeeting = workEdgesIterator.next().getTarget();
    var contributorMeetingRelation = workEdgesIterator.next();
    assertThat(contributorMeetingRelation.getPredicate()).isEqualTo(expectedPredicates.get(5));
    assertThat(contributorMeetingRelation.getTarget()).isEqualTo(workContributorMeeting);
  }

  private void validateInstanceNotes(Resource resource) {
    var doc = resource.getDoc();

    List.of(NOTE, WITH_NOTE, CREDITS_NOTE, ISSUANCE_NOTE, FORMER_TITLE_NOTE, ISSUING_BODY, EXHIBITIONS_NOTE,
        PARTICIPANT_NOTE, COMPUTER_DATA_NOTE, CITATION_COVERAGE, ADDITIONAL_PHYSICAL_FORM, ACCESSIBILITY_NOTE,
        INFORMATION_ABOUT_DOCUMENTATION, DESCRIPTION_SOURCE_NOTE, GOVERNING_ACCESS_NOTE, TYPE_OF_REPORT,
        REPRODUCTION_NOTE, ORIGINAL_VERSION_NOTE, LOCATION_OF_ORIGINALS_DUPLICATES, FUNDING_INFORMATION,
        INFORMATION_RELATING_TO_COPYRIGHT_STATUS, RELATED_PARTS, ENTITY_AND_ATTRIBUTE_INFORMATION,
        LOCATION_OF_OTHER_ARCHIVAL_MATERIAL, SYSTEM_DETAILS, SYSTEM_DETAILS_ACCESS_NOTE, PUBLICATION_FREQUENCY,
        DATES_OF_PUBLICATION_NOTE, PHYSICAL_DESCRIPTION)
      .forEach(p -> {
        assertThat(doc.has(p.getValue())).isTrue();
        assertThat(doc.get(p.getValue())).hasSize(1);
      });

    assertThat(doc.get(NOTE.getValue()).get(0).asText()).isEqualTo("general note");
    assertThat(doc.get(WITH_NOTE.getValue()).get(0).asText()).isEqualTo("with note");
    assertThat(doc.get(CREDITS_NOTE.getValue()).get(0).asText()).isEqualTo("credits note");
    assertThat(doc.get(ISSUANCE_NOTE.getValue()).get(0).asText()).isEqualTo("issuance note");
    assertThat(doc.get(FORMER_TITLE_NOTE.getValue()).get(0).asText()).isEqualTo("former title note");
    assertThat(doc.get(ISSUING_BODY.getValue()).get(0).asText()).isEqualTo("issuing body note");
    assertThat(doc.get(EXHIBITIONS_NOTE.getValue()).get(0).asText()).isEqualTo("exhibitions note");
    assertThat(doc.get(PARTICIPANT_NOTE.getValue()).get(0).asText()).isEqualTo("participant note");
    assertThat(doc.get(COMPUTER_DATA_NOTE.getValue()).get(0).asText()).isEqualTo("computer data note");
    assertThat(doc.get(CITATION_COVERAGE.getValue()).get(0).asText()).isEqualTo("citation coverage note");
    assertThat(doc.get(ADDITIONAL_PHYSICAL_FORM.getValue()).get(0).asText())
      .isEqualTo("additional physical form note");
    assertThat(doc.get(ACCESSIBILITY_NOTE.getValue()).get(0).asText()).isEqualTo("accessibility note");
    assertThat(doc.get(INFORMATION_ABOUT_DOCUMENTATION.getValue()).get(0).asText()).isEqualTo("info about doc note");
    assertThat(doc.get(DESCRIPTION_SOURCE_NOTE.getValue()).get(0).asText()).isEqualTo("description source note");
    assertThat(doc.get(GOVERNING_ACCESS_NOTE.getValue()).get(0).asText())
      .isEqualTo("terms, jurisdiction, provisions, users, authorization, terminology, date, agency");
    assertThat(doc.get(TYPE_OF_REPORT.getValue()).get(0).asText()).isEqualTo("type, period");
    assertThat(doc.get(REPRODUCTION_NOTE.getValue()).get(0).asText())
      .isEqualTo("type, place, agency, date, description, statement, note");
    assertThat(doc.get(ORIGINAL_VERSION_NOTE.getValue()).get(0).asText())
      .isEqualTo("entry, statement, publication, description, series statement, key title, details, note, title");
    assertThat(doc.get(LOCATION_OF_ORIGINALS_DUPLICATES.getValue()).get(0).asText())
      .isEqualTo("custodian, postal address, country, address, code");
    assertThat(doc.get(FUNDING_INFORMATION.getValue()).get(0).asText())
      .isEqualTo("text, contact, grant, undifferentiated, program element, project, task, work unit");
    assertThat(doc.get(INFORMATION_RELATING_TO_COPYRIGHT_STATUS.getValue()).get(0).asText())
      .isEqualTo("creator, date, corporate creator, holder, information, statement, copyright date, renewal date, "
        + "publication date, creation date, publisher, status, publication status, note, research date, country, "
        + "agency, jurisdiction");
    assertThat(doc.get(RELATED_PARTS.getValue()).get(0).asText())
      .isEqualTo("custodian, address, country, title, provenance, note");
    assertThat(doc.get(ENTITY_AND_ATTRIBUTE_INFORMATION.getValue()).get(0).asText())
      .isEqualTo("label, definition, attribute label, source, value, domain definition, range, codeset, domain, "
        + "units, date, accuracy, explanation, frequency, overview, citation, uri, note");
    assertThat(doc.get(LOCATION_OF_OTHER_ARCHIVAL_MATERIAL.getValue()).get(0).asText())
      .isEqualTo("note, source, control, reference");
    assertThat(doc.get(SYSTEM_DETAILS.getValue()).get(0).asText()).isEqualTo("note, text, uri");
    assertThat(doc.get(SYSTEM_DETAILS_ACCESS_NOTE.getValue()).get(0).asText()).isEqualTo("model, language, system");
    assertThat(doc.get(PUBLICATION_FREQUENCY.getValue()).get(0).asText()).isEqualTo("frequency, date");
    assertThat(doc.get(DATES_OF_PUBLICATION_NOTE.getValue()).get(0).asText()).isEqualTo("dates, source");
    assertThat(doc.get(PHYSICAL_DESCRIPTION.getValue()).get(0).asText()).isEqualTo("extent, details");
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
    assertThat(edge.getTarget().getTypes()).containsOnly(ID_LCCN, IDENTIFIER);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NAME.getValue()).get(0).asText()).isEqualTo(number);
    assertThat(edge.getTarget().getOutgoingEdges()).isNotEmpty();
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
    assertThat(edge.getTarget().getTypes()).containsOnly(ID_LOCAL, IDENTIFIER);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(LOCAL_ID_VALUE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(LOCAL_ID_VALUE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(LOCAL_ID_VALUE.getValue()).get(0).asText()).isEqualTo(number);
    assertThat(edge.getTarget().getOutgoingEdges()).isNotEmpty();
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
    assertThat(edge.getTarget().getTypes()).containsOnly(ID_ISBN, IDENTIFIER);
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    assertThat(edge.getTarget().getDoc().has(NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NAME.getValue()).get(0).asText()).isEqualTo(number);
    assertThat(edge.getTarget().getDoc().has(QUALIFIER.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(QUALIFIER.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(QUALIFIER.getValue()).get(0).asText()).isEqualTo("(paperback)");
    assertThat(edge.getTarget().getOutgoingEdges()).isNotEmpty();
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
    assertThat(edge.getTarget().getTypes()).containsOnly(ID_EAN, IDENTIFIER);
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    assertThat(edge.getTarget().getDoc().has(EAN_VALUE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(EAN_VALUE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(EAN_VALUE.getValue()).get(0).asText()).isEqualTo(number);
    assertThat(edge.getTarget().getDoc().has(QUALIFIER.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(QUALIFIER.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(QUALIFIER.getValue()).get(0).asText()).isEqualTo("eanIdQal");
    assertThat(edge.getTarget().getOutgoingEdges()).isNotEmpty();
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
    assertThat(edge.getTarget().getTypes()).containsOnly(ID_UNKNOWN, IDENTIFIER);
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    assertThat(edge.getTarget().getDoc().has(NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NAME.getValue()).get(0).asText()).isEqualTo(number);
    assertThat(edge.getTarget().getDoc().has(QUALIFIER.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(QUALIFIER.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(QUALIFIER.getValue()).get(0).asText()).isEqualTo("otherIdQal");
    assertThat(edge.getTarget().getOutgoingEdges()).isNotEmpty();
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
    assertThat(edge.getTarget().getTypes()).containsOnly(ResourceTypeDictionary.STATUS);
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
    var work = edge.getTarget();
    assertThat(edge.getId().getTargetHash()).isEqualTo(work.getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(INSTANTIATES.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(INSTANTIATES.getUri());
    assertThat(work.getResourceHash()).isNotNull();
    assertThat(work.getLabel()).isNotEmpty();
    assertThat(work.getTypes()).containsOnly(WORK);
    assertThat(work.getDoc()).hasSize(16);
    getWorkExpectedProperties().forEach((property, propertyValue) -> validateProperty(work, property, propertyValue));
    assertThat(work.getOutgoingEdges()).isNotEmpty();
    var edgeIterator = work.getOutgoingEdges().iterator();
    validateEdge(edgeIterator.next(), work.getResourceHash(), PredicateDictionary.GEOGRAPHIC_COVERAGE, List.of(PLACE),
      Map.of(
        NAME.getValue(), "United States",
        GEOGRAPHIC_AREA_CODE.getValue(), "n-us",
        GEOGRAPHIC_COVERAGE.getValue(), "https://id.loc.gov/vocabulary/geographicAreas/n-us"
      ), "United States");
    validateLcClassification(edgeIterator.next(), work.getResourceHash());
    validateClassification(edgeIterator.next(), work.getResourceHash());
    validateEdge(edgeIterator.next(), work.getResourceHash(), CREATOR, List.of(PERSON),
      getFamilyPersonContributorExpectedProperties("CREATOR PERSON"), "CREATOR PERSON name");
    validateEdge(edgeIterator.next(), work.getResourceHash(), CREATOR, List.of(FAMILY),
      getFamilyPersonContributorExpectedProperties("CREATOR FAMILY"), "CREATOR FAMILY name");
    validateEdge(edgeIterator.next(), work.getResourceHash(), CREATOR, List.of(ORGANIZATION),
      getOrganizationContributorExpectedProperties("CREATOR ORGANIZATION"), "CREATOR ORGANIZATION name");
    validateEdge(edgeIterator.next(), work.getResourceHash(), CREATOR, List.of(MEETING),
      getMeetingContributorExpectedProperties("CREATOR MEETING"), "CREATOR MEETING name");
    validateCategory(edgeIterator.next(), work.getResourceHash(), CONTENT, "contentTypes");
    validateSubjectEdge(edgeIterator.next(), work.getResourceHash(), List.of(CONCEPT, FAMILY),
      getFamilyPersonConceptExpectedProperties("family"));
    validateSubjectEdge(edgeIterator.next(), work.getResourceHash(), List.of(CONCEPT, PERSON),
      getFamilyPersonConceptExpectedProperties("person"));
    validateSubjectEdge(edgeIterator.next(), work.getResourceHash(), List.of(CONCEPT, JURISDICTION),
      getJurisdictionOrganizationConceptExpectedProperties("jurisdiction"));
    validateSubjectEdge(edgeIterator.next(), work.getResourceHash(), List.of(CONCEPT, ORGANIZATION),
      getJurisdictionOrganizationConceptExpectedProperties("organization"));
    validateSubjectEdge(edgeIterator.next(), work.getResourceHash(), List.of(CONCEPT, TOPIC),
      getTopicConceptExpectedProperties());
    validateSubjectEdge(edgeIterator.next(), work.getResourceHash(), List.of(CONCEPT, PLACE),
      getPlaceConceptExpectedProperties());
    validateSubjectEdge(edgeIterator.next(), work.getResourceHash(), List.of(CONCEPT, FORM),
      getFormConceptExpectedProperties());
    validateEdge(edgeIterator.next(), work.getResourceHash(), GENRE, List.of(FORM),
      removeNonFocusProperties(getFormConceptExpectedProperties()), "form name");
    validateEdge(edgeIterator.next(), work.getResourceHash(), CONTRIBUTOR, List.of(PERSON),
      getFamilyPersonContributorExpectedProperties("CONTRIBUTOR PERSON"), "CONTRIBUTOR PERSON name");
    validateEdge(edgeIterator.next(), work.getResourceHash(), CONTRIBUTOR, List.of(FAMILY),
      getFamilyPersonContributorExpectedProperties("CONTRIBUTOR FAMILY"), "CONTRIBUTOR FAMILY name");
    validateEdge(edgeIterator.next(), work.getResourceHash(), CONTRIBUTOR, List.of(ORGANIZATION),
      getOrganizationContributorExpectedProperties("CONTRIBUTOR ORGANIZATION"), "CONTRIBUTOR ORGANIZATION name");
    validateEdge(edgeIterator.next(), work.getResourceHash(), CONTRIBUTOR, List.of(MEETING),
      getMeetingContributorExpectedProperties("CONTRIBUTOR MEETING"), "CONTRIBUTOR MEETING name");
    validateEdge(edgeIterator.next(), work.getResourceHash(), GOVERNMENT_PUBLICATION, List.of(CATEGORY),
      Map.of(
        CODE.getValue(), "a",
        LINK.getValue(), "http://id.loc.gov/vocabulary/mgovtpubtype/a",
        TERM.getValue(), "Autonomous"
      ), "Autonomous");
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateContributor(ResourceEdge edge, Long parentHash, ResourceTypeDictionary type,
                                   PredicateDictionary predicate) {
    var prefix = predicate.name() + SPACE + type.name();
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(predicate.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(predicate.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo(prefix + " name");
    assertThat(edge.getTarget().getTypes()).containsOnly(type);
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    validateProperty(edge.getTarget(), NAME.getValue(), prefix + " name");
    validateProperty(edge.getTarget(), LCNAF_ID.getValue(), prefix + " LCNAF id");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateLcClassification(ResourceEdge edge, Long parentHash) {
    validateEdge(edge, parentHash, CLASSIFICATION, List.of(CATEGORY),
      Map.of(
        SOURCE.getValue(), "lc",
        CODE.getValue(), "code",
        ITEM_NUMBER.getValue(), "item number",
        ASSIGNER.getValue(), "http://id.loc.gov/vocabulary/organizations/dlc",
        PropertyDictionary.STATUS.getValue(), "http://id.loc.gov/vocabulary/mstatus/uba"
      ), "code");
    var classification = edge.getTarget();
    validateEdge(classification.getOutgoingEdges().iterator().next(), classification.getResourceHash(), IS_DEFINED_BY,
      List.of(CATEGORY_SET),
      Map.of(
        LABEL.getValue(), "lc"
      ), "lc");
  }

  private void validateClassification(ResourceEdge edge, Long parentHash) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(CLASSIFICATION.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(CLASSIFICATION.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo("Dewey Decimal Classification value");
    assertThat(edge.getTarget().getTypes()).containsOnly(CATEGORY);
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    assertThat(edge.getTarget().getDoc().has(CODE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(CODE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(CODE.getValue()).get(0).asText())
      .isEqualTo("Dewey Decimal Classification value");
    assertThat(edge.getTarget().getDoc().has(SOURCE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(SOURCE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(SOURCE.getValue()).get(0).asText()).isEqualTo("ddc");
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
    assertThat(edge.getTarget().getTypes()).containsOnly(TITLE);
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
    assertThat(edge.getTarget().getTypes()).containsOnly(TITLE);
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
    assertThat(edge.getTarget().getTypes()).containsOnly(TITLE);
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
    assertThat(edge.getTarget().getTypes()).containsOnly(VARIANT_TITLE);
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
    assertThat(edge.getTarget().getTypes()).containsOnly(PARALLEL_TITLE);
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
    var resource = edge.getTarget();
    assertThat(edge.getId().getTargetHash()).isEqualTo(resource.getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(expectedPredicate.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(expectedPredicate.getUri());
    assertThat(resource.getResourceHash()).isNotNull();
    assertThat(resource.getLabel()).isEqualTo(expectedPrefix + " Name");
    assertThat(resource.getTypes()).containsOnly(PROVIDER_EVENT);
    assertThat(resource.getDoc()).hasSize(4);
    assertThat(resource.getDoc().has(SIMPLE_PLACE.getValue())).isTrue();
    assertThat(resource.getDoc().get(SIMPLE_PLACE.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(SIMPLE_PLACE.getValue()).get(0).asText()).isEqualTo(expectedPrefix + " Place");
    assertThat(resource.getDoc().has(DATE.getValue())).isTrue();
    assertThat(resource.getDoc().get(DATE.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(DATE.getValue()).get(0).asText()).isEqualTo(expectedPrefix + " Date");
    assertThat(resource.getDoc().has(NAME.getValue())).isTrue();
    assertThat(resource.getDoc().get(NAME.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(NAME.getValue()).get(0).asText()).isEqualTo(expectedPrefix + " Name");
    assertThat(resource.getDoc().has(PROVIDER_DATE.getValue())).isTrue();
    assertThat(resource.getDoc().get(PROVIDER_DATE.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(PROVIDER_DATE.getValue()).get(0).asText()).isEqualTo("1999");
    assertThat(resource.getOutgoingEdges()).isNotEmpty();
    var edgeIterator = resource.getOutgoingEdges().iterator();
    validateProviderPlace(edgeIterator.next(), resource.getResourceHash());
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateProviderPlace(ResourceEdge edge, Long parentHash) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    var resource = edge.getTarget();
    assertThat(edge.getId().getTargetHash()).isEqualTo(resource.getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(PROVIDER_PLACE.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(PROVIDER_PLACE.getUri());
    assertThat(resource.getResourceHash()).isNotNull();
    assertThat(resource.getLabel()).isEqualTo("af");
    assertThat(resource.getTypes()).containsOnly(PLACE);
    assertThat(resource.getDoc()).hasSize(2);
    assertThat(resource.getDoc().has(CODE.getValue())).isTrue();
    assertThat(resource.getDoc().get(CODE.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(CODE.getValue()).get(0).asText()).isEqualTo("af");
    assertThat(resource.getDoc().has(LINK.getValue())).isTrue();
    assertThat(resource.getDoc().get(LINK.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(LINK.getValue()).get(0).asText())
      .isEqualTo("http://id.loc.gov/vocabulary/countries/af");
    assertThat(resource.getOutgoingEdges()).isEmpty();
  }

  private void validateCopyrightDate(ResourceEdge edge, Long parentHash) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(COPYRIGHT.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(COPYRIGHT.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo("2018");
    assertThat(edge.getTarget().getTypes()).containsOnly(COPYRIGHT_EVENT);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(DATE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(DATE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(DATE.getValue()).get(0).asText()).isEqualTo("2018");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateCategory(ResourceEdge edge, Long parentHash, PredicateDictionary predicate, String linkTerm) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    var resource = edge.getTarget();
    assertThat(edge.getId().getTargetHash()).isEqualTo(resource.getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(predicate.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(predicate.getUri());
    assertThat(resource.getResourceHash()).isNotNull();
    assertThat(resource.getLabel()).isEqualTo(predicate.name() + " term");
    assertThat(resource.getTypes()).containsOnly(CATEGORY);
    assertThat(resource.getDoc()).hasSize(4);
    assertThat(resource.getDoc().has(CODE.getValue())).isTrue();
    assertThat(resource.getDoc().get(CODE.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(CODE.getValue()).get(0).asText()).isEqualTo(predicate.name() + " code");
    assertThat(resource.getDoc().has(LINK.getValue())).isTrue();
    assertThat(resource.getDoc().get(LINK.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(LINK.getValue()).get(0).asText()).isEqualTo(
      "http://id.loc.gov/vocabulary/" + linkTerm + "/" + predicate.name() + " code");
    assertThat(resource.getDoc().has(TERM.getValue())).isTrue();
    assertThat(resource.getDoc().get(TERM.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(TERM.getValue()).get(0).asText()).isEqualTo(predicate.name() + " term");
    assertThat(resource.getDoc().has(SOURCE.getValue())).isTrue();
    assertThat(resource.getDoc().get(SOURCE.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(SOURCE.getValue()).get(0).asText()).isEqualTo(predicate.name() + " source");
    assertThat(resource.getOutgoingEdges()).isEmpty();
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
    assertThat(edge.getTarget().getTypes()).containsOnly(ANNOTATION);
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    assertThat(edge.getTarget().getDoc().has(LINK.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(LINK.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(LINK.getValue()).get(0).asText()).isEqualTo("access location URI");
    assertThat(edge.getTarget().getDoc().has(NOTE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NOTE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NOTE.getValue()).get(0).asText()).isEqualTo("access location note");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private Map<String, String> getWorkExpectedProperties() {
    return Map.ofEntries(
      entry(LANGUAGE.getValue(), "eng"),
      entry(RESPONSIBILITY_STATEMENT.getValue(), "Statement Of Responsibility"),
      entry(SUMMARY.getValue(), "work summary"),
      entry(TABLE_OF_CONTENTS.getValue(), "work table of contents"),
      entry(TARGET_AUDIENCE.getValue(), "primary"),
      entry(DATE_START.getValue(), "2022"),
      entry(DATE_END.getValue(), "2023"),
      entry(BIBLIOGRAPHY_NOTE.getValue(), "note, references"),
      entry(DATA_QUALITY.getValue(), "report, value, explanation, consistency report, completeness report, "
        + "horizontal report, horizontal value, horizontal explanation, vertical report, vertical value, "
        + "vertical explanation, cover, uri, note"),
      entry(GEOGRAPHIC_COVERAGE.getValue(), "geographic coverage note"),
      entry(LANGUAGE_NOTE.getValue(), "language note"),
      entry(OTHER_EVENT_INFORMATION.getValue(), "time, date, other, place"),
      entry(REFERENCES.getValue(), "name, coverage, location, uri, issn"),
      entry(SCALE_NOTE.getValue(), "fraction note, remainder note"),
      entry(STUDY_PROGRAM_NAME.getValue(), "program, interest, reading, title, text, nonpublic, public"),
      entry(SUPPLEMENT.getValue(), "supplement note")
    );
  }

  private Map<String, String> getCommonConceptExpectedProperties(String prefix) {
    return Map.ofEntries(
      entry(NAME.getValue(), prefix + " name"),
      entry(FORM_SUBDIVISION.getValue(), prefix + " form subdivision"),
      entry(GENERAL_SUBDIVISION.getValue(), prefix + " general subdivision"),
      entry(CHRONOLOGICAL_SUBDIVISION.getValue(), prefix + " chronological subdivision"),
      entry(GEOGRAPHIC_SUBDIVISION.getValue(), prefix + " geographic subdivision"),
      entry(SOURCE.getValue(), prefix + " source"),
      entry(MATERIALS_SPECIFIED.getValue(), prefix + " materials specified"),
      entry(RELATOR_TERM.getValue(), prefix + " relator term"),
      entry(RELATOR_CODE.getValue(), prefix + " relator code"),
      entry(AUTHORITY_LINK.getValue(), prefix + " authority link"),
      entry(EQUIVALENT.getValue(), prefix + " equivalent"),
      entry(LINKAGE.getValue(), prefix + " linkage"),
      entry(CONTROL_FIELD.getValue(), prefix + " control field"),
      entry(FIELD_LINK.getValue(), prefix + " field link")
    );
  }

  private Map<String, String> getTopicConceptExpectedProperties() {
    return Stream.concat(
        getCommonConceptExpectedProperties("topic").entrySet().stream(),
        Map.ofEntries(
          entry(GEOGRAPHIC_COVERAGE.getValue(), "topic geographic coverage"),
          entry(LOCATION_OF_EVENT.getValue(), "topic location of event"),
          entry(DATE.getValue(), "topic date"),
          entry(MISC_INFO.getValue(), "topic misc info")
        ).entrySet().stream())
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private Map<String, String> getPlaceConceptExpectedProperties() {
    return Stream.concat(
        getCommonConceptExpectedProperties("place").entrySet().stream(),
        Map.ofEntries(entry(MISC_INFO.getValue(), "place misc info")).entrySet().stream())
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private Map<String, String> getFormConceptExpectedProperties() {
    return Stream.concat(
        getCommonConceptExpectedProperties("form").entrySet().stream(),
        Map.ofEntries(entry(GEOGRAPHIC_COVERAGE.getValue(), "form geographic coverage")).entrySet().stream())
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private Map<String, String> getFamilyPersonConceptExpectedProperties(String prefix) {
    return Stream.concat(
        getCommonConceptExpectedProperties(prefix).entrySet().stream(),
        Map.ofEntries(
          entry(NUMERATION.getValue(), prefix + " numeration"),
          entry(TITLES.getValue(), prefix + " titles"),
          entry(DATE.getValue(), prefix + " date"),
          entry(ATTRIBUTION.getValue(), prefix + " attribution"),
          entry(NAME_ALTERNATIVE.getValue(), prefix + " name alternative"),
          entry(AFFILIATION.getValue(), prefix + " affiliation")
        ).entrySet().stream())
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private Map<String, String> getJurisdictionOrganizationConceptExpectedProperties(String prefix) {
    return Stream.concat(
        getCommonConceptExpectedProperties(prefix).entrySet().stream(),
        Map.ofEntries(
          entry(SUBORDINATE_UNIT.getValue(), prefix + " subordinate unit"),
          entry(PropertyDictionary.PLACE.getValue(), prefix + " place"),
          entry(DATE.getValue(), prefix + " date"),
          entry(AFFILIATION.getValue(), prefix + " affiliation")
        ).entrySet().stream())
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private Map<String, String> removeNonFocusProperties(Map<String, String> properties) {
    return properties.entrySet().stream()
      .filter(entry -> !List.of(
        FORM_SUBDIVISION.getValue(),
        GENERAL_SUBDIVISION.getValue(),
        CHRONOLOGICAL_SUBDIVISION.getValue(),
        GEOGRAPHIC_SUBDIVISION.getValue(),
        RELATOR_TERM.getValue(),
        RELATOR_CODE.getValue()
      ).contains(entry.getKey()))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private Map<String, String> getFamilyPersonContributorExpectedProperties(String prefix) {
    return Map.ofEntries(
      entry(NAME.getValue(), prefix + " name"),
      entry(NUMERATION.getValue(), prefix + " numeration"),
      entry(TITLES.getValue(), prefix + " titles"),
      entry(DATE.getValue(), prefix + " date"),
      entry(ATTRIBUTION.getValue(), prefix + " attribution"),
      entry(NAME_ALTERNATIVE.getValue(), prefix + " name alternative"),
      entry(AFFILIATION.getValue(), prefix + " affiliation"),
      entry(AUTHORITY_LINK.getValue(), prefix + " authority link"),
      entry(EQUIVALENT.getValue(), prefix + " equivalent"),
      entry(LINKAGE.getValue(), prefix + " linkage"),
      entry(CONTROL_FIELD.getValue(), prefix + " control field"),
      entry(FIELD_LINK.getValue(), prefix + " field link")
    );
  }

  private Map<String, String> getOrganizationContributorExpectedProperties(String prefix) {
    return Map.ofEntries(
      entry(NAME.getValue(), prefix + " name"),
      entry(SUBORDINATE_UNIT.getValue(), prefix + " subordinate unit"),
      entry(PropertyDictionary.PLACE.getValue(), prefix + " place"),
      entry(DATE.getValue(), prefix + " date"),
      entry(AFFILIATION.getValue(), prefix + " affiliation"),
      entry(AUTHORITY_LINK.getValue(), prefix + " authority link"),
      entry(EQUIVALENT.getValue(), prefix + " equivalent"),
      entry(LINKAGE.getValue(), prefix + " linkage"),
      entry(CONTROL_FIELD.getValue(), prefix + " control field"),
      entry(FIELD_LINK.getValue(), prefix + " field link")
    );
  }

  private Map<String, String> getMeetingContributorExpectedProperties(String prefix) {
    return Map.ofEntries(
      entry(NAME.getValue(), prefix + " name"),
      entry(PropertyDictionary.PLACE.getValue(), prefix + " place"),
      entry(DATE.getValue(), prefix + " date"),
      entry(SUBORDINATE_UNIT.getValue(), prefix + " subordinate unit"),
      entry(AFFILIATION.getValue(), prefix + " affiliation"),
      entry(AUTHORITY_LINK.getValue(), prefix + " authority link"),
      entry(EQUIVALENT.getValue(), prefix + " equivalent"),
      entry(LINKAGE.getValue(), prefix + " linkage"),
      entry(CONTROL_FIELD.getValue(), prefix + " control field"),
      entry(FIELD_LINK.getValue(), prefix + " field link")
    );
  }

  private void validateSubjectEdge(ResourceEdge subjectEdge, Long workHash, List<ResourceTypeDictionary> subjectTypes,
                                   Map<String, String> conceptProperties) {
    validateEdge(subjectEdge, workHash, SUBJECT, subjectTypes, conceptProperties,
      conceptProperties.get(NAME.getValue()));
    assertThat(subjectEdge.getTarget().getOutgoingEdges()).isNotEmpty();
    var edgeIterator = subjectEdge.getTarget().getOutgoingEdges().iterator();
    var conceptHash = subjectEdge.getTarget().getResourceHash();
    var focusEdge = edgeIterator.next();
    validateEdge(focusEdge, conceptHash, FOCUS, List.of(subjectTypes.get(1)),
      removeNonFocusProperties(conceptProperties), conceptProperties.get(NAME.getValue()));
    var formEdge = edgeIterator.next();
    validateEdge(formEdge, conceptHash, SUB_FOCUS, List.of(FORM),
      Map.of(NAME.getValue(), conceptProperties.get(FORM_SUBDIVISION.getValue())),
      conceptProperties.get(FORM_SUBDIVISION.getValue()));
    var topicEdge = edgeIterator.next();
    validateEdge(topicEdge, conceptHash, SUB_FOCUS, List.of(TOPIC),
      Map.of(NAME.getValue(), conceptProperties.get(GENERAL_SUBDIVISION.getValue())),
      conceptProperties.get(GENERAL_SUBDIVISION.getValue()));
    var temporalEdge = edgeIterator.next();
    validateEdge(temporalEdge, conceptHash, SUB_FOCUS, List.of(TEMPORAL),
      Map.of(NAME.getValue(), conceptProperties.get(CHRONOLOGICAL_SUBDIVISION.getValue())),
      conceptProperties.get(CHRONOLOGICAL_SUBDIVISION.getValue()));
    var placeEdge = edgeIterator.next();
    validateEdge(placeEdge, conceptHash, SUB_FOCUS, List.of(PLACE),
      Map.of(NAME.getValue(), conceptProperties.get(GEOGRAPHIC_SUBDIVISION.getValue())),
      conceptProperties.get(GEOGRAPHIC_SUBDIVISION.getValue()));
    assertThat(focusEdge.getTarget().getOutgoingEdges()).isEmpty();
    assertThat(formEdge.getTarget().getOutgoingEdges()).isEmpty();
    assertThat(topicEdge.getTarget().getOutgoingEdges()).isEmpty();
    assertThat(temporalEdge.getTarget().getOutgoingEdges()).isEmpty();
    assertThat(placeEdge.getTarget().getOutgoingEdges()).isEmpty();
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateEdge(ResourceEdge edge, Long parentHash, PredicateDictionary predicate,
                            List<ResourceTypeDictionary> types, Map<String, String> properties, String expectedLabel) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(predicate.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(predicate.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getTypes()).containsOnly(types.toArray(new ResourceTypeDictionary[0]));
    assertThat(edge.getTarget().getLabel()).isEqualTo(expectedLabel);
    assertThat(edge.getTarget().getDoc()).hasSize(properties.size());
    properties.forEach((property, propertyValue) -> validateProperty(edge.getTarget(), property, propertyValue));
  }

  private void validateProperty(Resource resource, String propertyKey, String propertyValue) {
    assertThat(resource.getDoc().has(propertyKey)).isTrue();
    assertThat(resource.getDoc().get(propertyKey).size()).isEqualTo(1);
    assertThat(resource.getDoc().get(propertyKey).get(0).asText()).isEqualTo(propertyValue);
  }

}
