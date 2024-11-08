package org.folio.marc4ld.mapper;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.ACCESS_LOCATION;
import static org.folio.ld.dictionary.PredicateDictionary.AUTHOR;
import static org.folio.ld.dictionary.PredicateDictionary.BROADCASTER;
import static org.folio.ld.dictionary.PredicateDictionary.CLIENT;
import static org.folio.ld.dictionary.PredicateDictionary.COPYRIGHT;
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
import static org.folio.ld.dictionary.PredicateDictionary.MEDIUM;
import static org.folio.ld.dictionary.PredicateDictionary.NARRATOR;
import static org.folio.ld.dictionary.PredicateDictionary.ONSCREEN_PRESENTER;
import static org.folio.ld.dictionary.PredicateDictionary.PATRON;
import static org.folio.ld.dictionary.PredicateDictionary.RADIO_DIRECTOR;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.PropertyDictionary.ACCESSIBILITY_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.ADDITIONAL_PHYSICAL_FORM;
import static org.folio.ld.dictionary.PropertyDictionary.BIBLIOGRAPHY_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.CITATION_COVERAGE;
import static org.folio.ld.dictionary.PropertyDictionary.COMPUTER_DATA_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.CREDITS_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.DATA_QUALITY;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.DATES_OF_PUBLICATION_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.DATE_END;
import static org.folio.ld.dictionary.PropertyDictionary.DATE_START;
import static org.folio.ld.dictionary.PropertyDictionary.DESCRIPTION_SOURCE_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.DIMENSIONS;
import static org.folio.ld.dictionary.PropertyDictionary.EAN_VALUE;
import static org.folio.ld.dictionary.PropertyDictionary.EDITION;
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
import static org.folio.ld.dictionary.PropertyDictionary.LANGUAGE_NOTE;
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
import static org.folio.ld.dictionary.PropertyDictionary.PUBLICATION_FREQUENCY;
import static org.folio.ld.dictionary.PropertyDictionary.QUALIFIER;
import static org.folio.ld.dictionary.PropertyDictionary.REFERENCES;
import static org.folio.ld.dictionary.PropertyDictionary.RELATED_PARTS;
import static org.folio.ld.dictionary.PropertyDictionary.REPRODUCTION_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.SCALE_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.STATEMENT_OF_RESPONSIBILITY;
import static org.folio.ld.dictionary.PropertyDictionary.STUDY_PROGRAM_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.SUBTITLE;
import static org.folio.ld.dictionary.PropertyDictionary.SUMMARY;
import static org.folio.ld.dictionary.PropertyDictionary.SUPPLEMENT;
import static org.folio.ld.dictionary.PropertyDictionary.SYSTEM_DETAILS;
import static org.folio.ld.dictionary.PropertyDictionary.SYSTEM_DETAILS_ACCESS_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.TABLE_OF_CONTENTS;
import static org.folio.ld.dictionary.PropertyDictionary.TYPE_OF_REPORT;
import static org.folio.ld.dictionary.PropertyDictionary.VARIANT_TYPE;
import static org.folio.ld.dictionary.PropertyDictionary.WITH_NOTE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ANNOTATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.COPYRIGHT_EVENT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_EAN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_ISBN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LOCAL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_UNKNOWN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PARALLEL_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.VARIANT_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateProperty;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.service.marc2ld.bib.MarcBib2ldMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

class MarcBib2LdMapperIT extends Marc2LdTestBase {

  @Autowired
  private MarcBib2ldMapper marcBib2ldMapper;

  @Test
  void map_shouldReturnEmptyOptional_ifGivenMarcIsNull() {
    // given
    String marc = null;

    // when
    var result = marcBib2ldMapper.fromMarcJson(marc);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  void map_shouldReturnCorrectlyMappedEmptyResource() {
    // given
    var marc = loadResourceAsString("empty_marc.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getLabel()).isEmpty();
    assertThat(result.getDoc()).isEmpty();
    assertThat(result.getTypes()).containsOnly(INSTANCE);
    assertThat(result.getOutgoingEdges()).isEmpty();
    var folioMetadata = result.getFolioMetadata();
    assertThat(folioMetadata.getInventoryId()).isNull();
    assertThat(folioMetadata.getSrsId()).isNull();
  }

  @Test
  void map_shouldReturnCorrectlyMappedResourceWithAppendableFieldsOnly() {
    // given
    var marc = loadResourceAsString("marc_appendable_only.jsonl");

    // when
    var resource = marcBibToResource(marc);

    // then
    assertThat(resource.getLabel()).isEmpty();
    assertThat(resource.getDoc()).hasSize(2);
    assertThat(resource.getDoc().has(EDITION.getValue())).isTrue();
    assertThat(resource.getDoc().get(EDITION.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(EDITION.getValue()).get(0).asText())
      .isEqualTo("Edition Statement Edition statement2");
    assertThat(resource.getDoc().has(STATEMENT_OF_RESPONSIBILITY.getValue())).isTrue();
    assertThat(resource.getDoc().get(STATEMENT_OF_RESPONSIBILITY.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(STATEMENT_OF_RESPONSIBILITY.getValue()).get(0).asText())
      .isEqualTo("Statement Of Responsibility");
    assertThat(resource.getTypes()).containsOnly(INSTANCE);
    assertThat(resource.getOutgoingEdges()).isEmpty();
    var folioMetadata = resource.getFolioMetadata();
    assertThat(folioMetadata.getInventoryId()).isNull();
    assertThat(folioMetadata.getSrsId()).isNull();
  }

  @Test
  void map_shouldReturnCorrectlyMappedResource() {
    // given
    var marc = loadResourceAsString("full_marc.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    validateInstance(result);
    assertThat(result.getOutgoingEdges()).isNotEmpty();
    var edgeIterator = result.getOutgoingEdges().iterator();
    validateWork(edgeIterator.next());
    validateLocalId(edgeIterator.next(), "19861509", "current");
    validateLocalId(edgeIterator.next(), "09151986", "canceled or invalid");
    validateIsbn(edgeIterator.next(), "9780143789963", "current");
    validateIsbn(edgeIterator.next(), "9999999", "canceled or invalid");
    validateEan(edgeIterator.next(), "111222", "current");
    validateEan(edgeIterator.next(), "333", "canceled or invalid");
    validateOtherId(edgeIterator.next(), "202320239999", "current");
    validateOtherId(edgeIterator.next(), "231123", "canceled or invalid");
    validateTitle(edgeIterator.next());
    validateTitle2(edgeIterator.next());
    validateTitle3(edgeIterator.next());
    validateVariantTitle(edgeIterator.next());
    validateParallelTitle(edgeIterator.next());
    validateCopyrightDate(edgeIterator.next());
    validateExtent(edgeIterator.next());
    validateAccessLocation(edgeIterator.next());
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  @Test
  void twoMappedResources_shouldContainWorkWithDifferentIds() {
    // given
    var marc1 = loadResourceAsString("full_marc.jsonl");
    var marc2 = loadResourceAsString("short_marc.jsonl");

    // when
    var result1 = marcBibToResource(marc1);
    var result2 = marcBibToResource(marc2);

    // then
    var work1opt = result1.getOutgoingEdges().stream().filter(re -> INSTANTIATES.equals(re.getPredicate())).findFirst();
    var work2opt = result2.getOutgoingEdges().stream().filter(re -> INSTANTIATES.equals(re.getPredicate())).findFirst();
    assertThat(work1opt).isPresent();
    assertThat(work2opt).isPresent();
    assertThat(work1opt.get().getTarget().getId()).isNotEqualTo(work2opt.get().getTarget().getId());
  }

  @ParameterizedTest
  @MethodSource("provideMarcAndPredicates")
  void map_shouldReturnCorrectlyMappedRelations(String marc, List<PredicateDictionary> expectedPredicates) {
    //when
    var result = marcBibToResource(marc);

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

  private void validateExtent(ResourceEdge edge) {
    assertThat(edge.getId()).isNull();
    var resource = edge.getTarget();
    assertThat(edge.getPredicate().getHash()).isEqualTo(PredicateDictionary.EXTENT.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(PredicateDictionary.EXTENT.getUri());
    validateId(resource);
    assertThat(resource.getLabel()).isEqualTo("extent");
    assertThat(resource.getTypes()).containsOnly(ResourceTypeDictionary.EXTENT);
    assertThat(resource.getDoc()).hasSize(1);
    assertThat(resource.getDoc().has(LABEL.getValue())).isTrue();
    assertThat(resource.getDoc().get(LABEL.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(LABEL.getValue()).get(0).asText()).isEqualTo("extent");
  }

  private void validateInstance(Resource resource) {
    validateId(resource);
    assertThat(resource.getLabel()).isEqualTo("MainTitle");
    assertThat(resource.getDoc()).hasSize(35);
    validateInstanceNotes(resource);
    assertThat(resource.getDoc().has(EDITION.getValue())).isTrue();
    assertThat(resource.getDoc().get(EDITION.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(EDITION.getValue()).get(0).asText())
      .isEqualTo("Edition Statement Edition statement2");
    assertThat(resource.getDoc().has(STATEMENT_OF_RESPONSIBILITY.getValue())).isTrue();
    assertThat(resource.getDoc().get(STATEMENT_OF_RESPONSIBILITY.getValue())).hasSize(1);
    assertThat(resource.getDoc().get(STATEMENT_OF_RESPONSIBILITY.getValue()).get(0).asText())
      .isEqualTo("Statement Of Responsibility");
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
    var folioMetadata = resource.getFolioMetadata();
    assertThat(folioMetadata.getInventoryId()).hasToString("2165ef4b-001f-46b3-a60e-52bcdeb3d5a1");
    assertThat(folioMetadata.getSrsId()).hasToString("43d58061-decf-4d74-9747-0e1c368e861b");
    assertThat(resource.getTypes()).containsOnly(INSTANCE);
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

  private void validateLocalId(ResourceEdge edge, String number, String status) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(MAP.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(MAP.getUri());
    validateId(edge.getTarget());
    assertThat(edge.getTarget().getLabel()).isEqualTo(number);
    assertThat(edge.getTarget().getTypes()).containsOnly(ID_LOCAL, IDENTIFIER);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(LOCAL_ID_VALUE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(LOCAL_ID_VALUE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(LOCAL_ID_VALUE.getValue()).get(0).asText()).isEqualTo(number);
    assertThat(edge.getTarget().getOutgoingEdges()).isNotEmpty();
    var edgeIterator = edge.getTarget().getOutgoingEdges().iterator();
    validateIdStatus(edgeIterator.next(), status);
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateIsbn(ResourceEdge edge, String number, String status) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(MAP.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(MAP.getUri());
    validateId(edge.getTarget());
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
    validateIdStatus(edgeIterator.next(), status);
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateEan(ResourceEdge edge, String number, String status) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(MAP.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(MAP.getUri());
    validateId(edge.getTarget());
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
    validateIdStatus(edgeIterator.next(), status);
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateOtherId(ResourceEdge edge, String number, String status) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(MAP.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(MAP.getUri());
    validateId(edge.getTarget());
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
    validateIdStatus(edgeIterator.next(), status);
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateIdStatus(ResourceEdge edge, String value) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(STATUS.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(STATUS.getUri());
    validateId(edge.getTarget());
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

  private void validateWork(ResourceEdge edge) {
    assertThat(edge.getId()).isNull();
    var work = edge.getTarget();
    assertThat(edge.getPredicate().getHash()).isEqualTo(INSTANTIATES.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(INSTANTIATES.getUri());
    validateId(work);
    assertThat(work.getLabel()).isEqualTo("MainTitle");
    assertThat(work.getTypes()).containsOnly(WORK);
    assertThat(work.getDoc()).hasSize(13);
    getWorkExpectedProperties()
      .forEach((property, propertyValue) -> validateProperty(work, property, List.of(propertyValue)));
    assertThat(work.getOutgoingEdges()).isNotEmpty();
    var edgeIterator = work.getOutgoingEdges().iterator();
    validateTitle(edgeIterator.next());
    validateTitle2(edgeIterator.next());
    validateTitle3(edgeIterator.next());
    validateVariantTitle(edgeIterator.next());
    validateParallelTitle(edgeIterator.next());
    assertThat(edgeIterator.hasNext()).isFalse();
  }

  private void validateTitle(ResourceEdge edge) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(PredicateDictionary.TITLE.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(PredicateDictionary.TITLE.getUri());
    validateId(edge.getTarget());
    assertThat(edge.getTarget().getLabel()).isEqualTo("MainTitle");
    assertThat(edge.getTarget().getTypes()).containsOnly(TITLE);
    assertThat(edge.getTarget().getDoc()).hasSize(5);
    assertThat(edge.getTarget().getDoc().has(MAIN_TITLE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(MAIN_TITLE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(MAIN_TITLE.getValue()).get(0).asText()).isEqualTo("MainTitle");
    assertThat(edge.getTarget().getDoc().has(SUBTITLE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(SUBTITLE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(SUBTITLE.getValue()).get(0).asText()).isEqualTo("SubTitle");
    assertThat(edge.getTarget().getDoc().has(PART_NUMBER.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(PART_NUMBER.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(PART_NUMBER.getValue()).get(0).asText()).isEqualTo("8");
    assertThat(edge.getTarget().getDoc().has(PART_NAME.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(PART_NAME.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(PART_NAME.getValue()).get(0).asText()).isEqualTo("PartName");
    assertThat(edge.getTarget().getDoc().has(NON_SORT_NUM.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(NON_SORT_NUM.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(NON_SORT_NUM.getValue()).get(0).asText()).isEqualTo("7");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateTitle2(ResourceEdge edge) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(PredicateDictionary.TITLE.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(PredicateDictionary.TITLE.getUri());
    validateId(edge.getTarget());
    assertThat(edge.getTarget().getLabel()).isEqualTo("Title empty");
    assertThat(edge.getTarget().getTypes()).containsOnly(TITLE);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(MAIN_TITLE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(MAIN_TITLE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(MAIN_TITLE.getValue()).get(0).asText()).isEqualTo("Title empty");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateTitle3(ResourceEdge edge) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(PredicateDictionary.TITLE.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(PredicateDictionary.TITLE.getUri());
    validateId(edge.getTarget());
    assertThat(edge.getTarget().getLabel()).isNotEmpty();
    assertThat(edge.getTarget().getTypes()).containsOnly(TITLE);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(SUBTITLE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(SUBTITLE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(SUBTITLE.getValue()).get(0).asText())
      .isEqualTo("Title empty label");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateVariantTitle(ResourceEdge edge) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(PredicateDictionary.TITLE.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(PredicateDictionary.TITLE.getUri());
    validateId(edge.getTarget());
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

  private void validateParallelTitle(ResourceEdge edge) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(PredicateDictionary.TITLE.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(PredicateDictionary.TITLE.getUri());
    validateId(edge.getTarget());
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

  private void validateCopyrightDate(ResourceEdge edge) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(COPYRIGHT.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(COPYRIGHT.getUri());
    validateId(edge.getTarget());
    assertThat(edge.getTarget().getLabel()).isEqualTo("2018");
    assertThat(edge.getTarget().getTypes()).containsOnly(COPYRIGHT_EVENT);
    assertThat(edge.getTarget().getDoc()).hasSize(1);
    assertThat(edge.getTarget().getDoc().has(DATE.getValue())).isTrue();
    assertThat(edge.getTarget().getDoc().get(DATE.getValue())).hasSize(1);
    assertThat(edge.getTarget().getDoc().get(DATE.getValue()).get(0).asText()).isEqualTo("2018");
    assertThat(edge.getTarget().getOutgoingEdges()).isEmpty();
  }

  private void validateAccessLocation(ResourceEdge edge) {
    assertThat(edge.getId()).isNull();
    assertThat(edge.getPredicate().getHash()).isEqualTo(ACCESS_LOCATION.getHash());
    assertThat(edge.getPredicate().getUri()).isEqualTo(ACCESS_LOCATION.getUri());
    validateId(edge.getTarget());
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
      entry(SUMMARY.getValue(), "work summary"),
      entry(TABLE_OF_CONTENTS.getValue(), "work table of contents"),
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
}
