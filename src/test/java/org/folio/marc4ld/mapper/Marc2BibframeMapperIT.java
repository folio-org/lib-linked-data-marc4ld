package org.folio.marc4ld.mapper;

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
import static org.folio.ld.dictionary.PredicateDictionary.GRAPHIC_TECHNICIAN;
import static org.folio.ld.dictionary.PredicateDictionary.HONOUREE;
import static org.folio.ld.dictionary.PredicateDictionary.HOST;
import static org.folio.ld.dictionary.PredicateDictionary.ILLUSTRATOR;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.INSTRUCTOR;
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
import static org.folio.ld.dictionary.PropertyDictionary.ACCESSIBILITY_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.ADDITIONAL_PHYSICAL_FORM;
import static org.folio.ld.dictionary.PropertyDictionary.BIBLIOGRAPHY_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.CITATION_COVERAGE;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.COMPUTER_DATA_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.CREDITS_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.DATA_QUALITY;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.DATES_OF_PUBLICATION_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.DESCRIPTION_SOURCE_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.DIMENSIONS;
import static org.folio.ld.dictionary.PropertyDictionary.EAN_VALUE;
import static org.folio.ld.dictionary.PropertyDictionary.EDITION_STATEMENT;
import static org.folio.ld.dictionary.PropertyDictionary.ENTITY_AND_ATTRIBUTE_INFORMATION;
import static org.folio.ld.dictionary.PropertyDictionary.EXHIBITIONS_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.EXTENT;
import static org.folio.ld.dictionary.PropertyDictionary.FORMER_TITLE_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.FUNDING_INFORMATION;
import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_COVERAGE;
import static org.folio.ld.dictionary.PropertyDictionary.GOVERNING_ACCESS_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.INFORMATION_ABOUT_DOCUMENTATION;
import static org.folio.ld.dictionary.PropertyDictionary.INFORMATION_RELATING_TO_COPYRIGHT_STATUS;
import static org.folio.ld.dictionary.PropertyDictionary.ISSUANCE;
import static org.folio.ld.dictionary.PropertyDictionary.ISSUANCE_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.ISSUING_BODY;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LANGUAGE;
import static org.folio.ld.dictionary.PropertyDictionary.LANGUAGE_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.LCNAF_ID;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.LOCAL_ID_VALUE;
import static org.folio.ld.dictionary.PropertyDictionary.LOCATION_OF_ORIGINALS_DUPLICATES;
import static org.folio.ld.dictionary.PropertyDictionary.LOCATION_OF_OTHER_ARCHIVAL_MATERIAL;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.NON_SORT_NUM;
import static org.folio.ld.dictionary.PropertyDictionary.NOTE;
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
import static org.folio.ld.dictionary.PropertyDictionary.REPRODUCTION_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.RESPONSIBILITY_STATEMENT;
import static org.folio.ld.dictionary.PropertyDictionary.SCALE_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.SIMPLE_PLACE;
import static org.folio.ld.dictionary.PropertyDictionary.SOURCE;
import static org.folio.ld.dictionary.PropertyDictionary.STUDY_PROGRAM_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.SUBTITLE;
import static org.folio.ld.dictionary.PropertyDictionary.SUMMARY;
import static org.folio.ld.dictionary.PropertyDictionary.SUPPLEMENT;
import static org.folio.ld.dictionary.PropertyDictionary.SYSTEM_DETAILS;
import static org.folio.ld.dictionary.PropertyDictionary.SYSTEM_DETAILS_ACCESS_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.TABLE_OF_CONTENTS;
import static org.folio.ld.dictionary.PropertyDictionary.TARGET_AUDIENCE;
import static org.folio.ld.dictionary.PropertyDictionary.TERM;
import static org.folio.ld.dictionary.PropertyDictionary.TYPE_OF_REPORT;
import static org.folio.ld.dictionary.PropertyDictionary.VARIANT_TYPE;
import static org.folio.ld.dictionary.PropertyDictionary.WITH_NOTE;
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
import static org.folio.ld.dictionary.ResourceTypeDictionary.MEETING;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PARALLEL_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PROVIDER_EVENT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.VARIANT_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import java.util.List;
import java.util.stream.Stream;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc4ld.mapper.test.SpringTestConfig;
import org.folio.marc4ld.model.Resource;
import org.folio.marc4ld.model.ResourceEdge;
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
    assertThat(result.getTypes()).containsExactly(INSTANCE);
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
    validateCategory(edgeIterator.next(), result.getResourceHash(), MEDIA);
    validateCategory(edgeIterator.next(), result.getResourceHash(), CARRIER);
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
    assertThat(resource.getTypes()).containsExactly(INSTANCE);
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
    assertThat(edge.getTarget().getTypes()).containsExactly(ID_LCCN, IDENTIFIER);
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
    assertThat(edge.getTarget().getTypes()).containsExactly(ID_LOCAL, IDENTIFIER);
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
    assertThat(edge.getTarget().getTypes()).containsExactly(ID_ISBN, IDENTIFIER);
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
    assertThat(edge.getTarget().getTypes()).containsExactly(ID_EAN, IDENTIFIER);
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
    assertThat(edge.getTarget().getTypes()).containsExactly(ID_UNKNOWN, IDENTIFIER);
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
    var work = edge.getTarget();
    assertThat(edge.getId().getTargetHash()).isEqualTo(work.getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(INSTANTIATES.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(INSTANTIATES.getUri());
    assertThat(work.getResourceHash()).isNotNull();
    assertThat(work.getLabel()).isNotEmpty();
    assertThat(work.getTypes()).containsExactly(WORK);
    assertThat(work.getDoc()).hasSize(14);
    validateWorkNotes(work);
    assertThat(work.getDoc().has(LANGUAGE.getValue())).isTrue();
    assertThat(work.getDoc().get(LANGUAGE.getValue())).hasSize(1);
    assertThat(work.getDoc().get(LANGUAGE.getValue()).get(0).asText()).isEqualTo("eng");
    assertThat(work.getDoc().has(RESPONSIBILITY_STATEMENT.getValue())).isTrue();
    assertThat(work.getDoc().get(RESPONSIBILITY_STATEMENT.getValue())).hasSize(1);
    assertThat(work.getDoc().get(RESPONSIBILITY_STATEMENT.getValue()).get(0).asText()).isEqualTo(
      "Statement Of Responsibility");
    assertThat(work.getDoc().has(SUMMARY.getValue())).isTrue();
    assertThat(work.getDoc().get(SUMMARY.getValue())).hasSize(1);
    assertThat(work.getDoc().get(SUMMARY.getValue()).get(0).asText()).isEqualTo("work summary");
    assertThat(work.getDoc().has(TABLE_OF_CONTENTS.getValue())).isTrue();
    assertThat(work.getDoc().get(TABLE_OF_CONTENTS.getValue())).hasSize(1);
    assertThat(work.getDoc().get(TABLE_OF_CONTENTS.getValue()).get(0).asText()).isEqualTo(
      "work table of contents");
    assertThat(work.getDoc().has(TARGET_AUDIENCE.getValue())).isTrue();
    assertThat(work.getDoc().get(TARGET_AUDIENCE.getValue())).hasSize(1);
    assertThat(work.getDoc().get(TARGET_AUDIENCE.getValue()).get(0).asText()).isEqualTo("primary");
    assertThat(work.getOutgoingEdges()).isNotEmpty();
    var edgeIterator = work.getOutgoingEdges().iterator();
    validateClassification(edgeIterator.next(), work.getResourceHash());
    validateContributor(edgeIterator.next(), work.getResourceHash(), PERSON, CREATOR);
    validateContributor(edgeIterator.next(), work.getResourceHash(), ORGANIZATION, CREATOR);
    validateContributor(edgeIterator.next(), work.getResourceHash(), MEETING, CREATOR);
    validateCategory(edgeIterator.next(), work.getResourceHash(), CONTENT);
    validateContributor(edgeIterator.next(), work.getResourceHash(), PERSON, CONTRIBUTOR);
    validateContributor(edgeIterator.next(), work.getResourceHash(), ORGANIZATION, CONTRIBUTOR);
    validateContributor(edgeIterator.next(), work.getResourceHash(), MEETING, CONTRIBUTOR);
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateWorkNotes(Resource resource) {
    var doc = resource.getDoc();

    List.of(BIBLIOGRAPHY_NOTE, DATA_QUALITY, GEOGRAPHIC_COVERAGE, LANGUAGE_NOTE, OTHER_EVENT_INFORMATION, REFERENCES,
        SCALE_NOTE, STUDY_PROGRAM_NAME, SUPPLEMENT)
      .forEach(p -> {
        assertThat(doc.has(p.getValue())).isTrue();
        assertThat(doc.get(p.getValue())).hasSize(1);
      });

    assertThat(doc.get(BIBLIOGRAPHY_NOTE.getValue()).get(0).asText()).isEqualTo("note, references");
    assertThat(doc.get(DATA_QUALITY.getValue()).get(0).asText()).isEqualTo("report, value, explanation, "
      + "consistency report, completeness report, horizontal report, horizontal value, horizontal explanation, "
      + "vertical report, vertical value, vertical explanation, cover, uri, note");
    assertThat(doc.get(GEOGRAPHIC_COVERAGE.getValue()).get(0).asText()).isEqualTo("geographic coverage note");
    assertThat(doc.get(LANGUAGE_NOTE.getValue()).get(0).asText()).isEqualTo("language note");
    assertThat(doc.get(OTHER_EVENT_INFORMATION.getValue()).get(0).asText()).isEqualTo("time, date, other, place");
    assertThat(doc.get(REFERENCES.getValue()).get(0).asText()).isEqualTo("name, coverage, "
      + "location, uri, issn");
    assertThat(doc.get(SCALE_NOTE.getValue()).get(0).asText()).isEqualTo("fraction note, remainder note");
    assertThat(doc.get(STUDY_PROGRAM_NAME.getValue()).get(0).asText()).isEqualTo("program, interest, "
      + "reading, title, text, nonpublic, public");
    assertThat(doc.get(SUPPLEMENT.getValue()).get(0).asText()).isEqualTo("supplement note");
  }

  private void validateContributor(ResourceEdge edge, Long parentHash, ResourceTypeDictionary type,
                                   PredicateDictionary predicate) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    assertThat(edge.getId().getTargetHash()).isEqualTo(edge.getTarget().getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(predicate.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(predicate.getUri());
    assertThat(edge.getTarget().getResourceHash()).isNotNull();
    assertThat(edge.getTarget().getLabel()).isEqualTo(predicate.name() + SPACE + type.name() + " name");
    assertThat(edge.getTarget().getTypes()).containsExactly(type);
    assertThat(edge.getTarget().getDoc()).hasSize(2);
    assertThat(edge.getTarget().getDoc().has(NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NAME.getValue()).get(0).asText()).isEqualTo(
      predicate.name() + SPACE + type.name() + " name");
    assertThat(edge.getTarget().getDoc().has(LCNAF_ID.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(LCNAF_ID.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(LCNAF_ID.getValue()).get(0).asText()).isEqualTo(
      predicate.name() + SPACE + type.name() + " LCNAF id");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
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
    assertThat(edge.getTarget().getTypes()).containsExactly(CATEGORY);
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
    var resource = edge.getTarget();
    assertThat(edge.getId().getTargetHash()).isEqualTo(resource.getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(expectedPredicate.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(expectedPredicate.getUri());
    assertThat(resource.getResourceHash()).isNotNull();
    assertThat(resource.getLabel()).isEqualTo(expectedPrefix + " Name");
    assertThat(resource.getTypes()).containsExactly(PROVIDER_EVENT);
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
    validateProviderPlace(edgeIterator.next(), resource.getResourceHash(), expectedPrefix + " Place");
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateProviderPlace(ResourceEdge edge, Long parentHash, String expectedLabel) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    var resource = edge.getTarget();
    assertThat(edge.getId().getTargetHash()).isEqualTo(resource.getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(PROVIDER_PLACE.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(PROVIDER_PLACE.getUri());
    assertThat(resource.getResourceHash()).isNotNull();
    assertThat(resource.getLabel()).isEqualTo(expectedLabel);
    assertThat(resource.getTypes()).containsExactly(PLACE);
    assertThat(resource.getDoc()).hasSize(3);
    assertThat(resource.getDoc().has(CODE.getValue())).isTrue();
    assertThat(resource.getDoc().get(CODE.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(CODE.getValue()).get(0).asText()).isEqualTo("ilu");
    assertThat(resource.getDoc().has(LABEL.getValue())).isTrue();
    assertThat(resource.getDoc().get(LABEL.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(LABEL.getValue()).get(0).asText()).isEqualTo(expectedLabel);
    assertThat(resource.getDoc().has(LINK.getValue())).isTrue();
    assertThat(resource.getDoc().get(LINK.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(LINK.getValue()).get(0).asText())
      .isEqualTo("http://id.loc.gov/vocabulary/countries/ilu");
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
    assertThat(edge.getTarget().getTypes()).containsExactly(COPYRIGHT_EVENT);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(DATE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(DATE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(DATE.getValue()).get(0).asText()).isEqualTo("2018");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateCategory(ResourceEdge edge, Long parentHash, PredicateDictionary predicate) {
    assertThat(edge.getId()).isNotNull();
    assertThat(edge.getId().getSourceHash()).isEqualTo(parentHash);
    var resource = edge.getTarget();
    assertThat(edge.getId().getTargetHash()).isEqualTo(resource.getResourceHash());
    assertThat(edge.getId().getPredicateHash()).isEqualTo(edge.getPredicate().getHash());
    assertThat(edge.getPredicate().getHash()).isEqualTo(predicate.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(predicate.getUri());
    assertThat(resource.getResourceHash()).isNotNull();
    assertThat(resource.getLabel()).isEqualTo(predicate.name() + " term");
    assertThat(resource.getTypes()).containsExactly(CATEGORY);
    assertThat(resource.getDoc()).hasSize(4);
    assertThat(resource.getDoc().has(CODE.getValue())).isTrue();
    assertThat(resource.getDoc().get(CODE.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(CODE.getValue()).get(0).asText()).isEqualTo(predicate.name() + " code");
    assertThat(resource.getDoc().has(LINK.getValue())).isTrue();
    assertThat(resource.getDoc().get(LINK.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(LINK.getValue()).get(0).asText()).isEqualTo("http://id.loc.gov/vocabulary/"
      + predicate.name().toLowerCase() + "Types/" + predicate.name() + " code");
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
