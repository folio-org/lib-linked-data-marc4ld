package org.folio.marc4ld.mapper.test;

import static java.util.Collections.emptyMap;
import static java.util.Map.entry;
import static org.folio.ld.dictionary.PredicateDictionary.ACCESS_LOCATION;
import static org.folio.ld.dictionary.PredicateDictionary.CARRIER;
import static org.folio.ld.dictionary.PredicateDictionary.CLASSIFICATION;
import static org.folio.ld.dictionary.PredicateDictionary.CONTENT;
import static org.folio.ld.dictionary.PredicateDictionary.CONTRIBUTOR;
import static org.folio.ld.dictionary.PredicateDictionary.COPYRIGHT;
import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.INSTANTIATES;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.MEDIA;
import static org.folio.ld.dictionary.PredicateDictionary.PE_DISTRIBUTION;
import static org.folio.ld.dictionary.PredicateDictionary.PE_MANUFACTURE;
import static org.folio.ld.dictionary.PredicateDictionary.PE_PRODUCTION;
import static org.folio.ld.dictionary.PredicateDictionary.PE_PUBLICATION;
import static org.folio.ld.dictionary.PredicateDictionary.PROVIDER_PLACE;
import static org.folio.ld.dictionary.PredicateDictionary.STATUS;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.ACCESSIBILITY_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.ADDITIONAL_PHYSICAL_FORM;
import static org.folio.ld.dictionary.PropertyDictionary.CITATION_COVERAGE;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.COMPUTER_DATA_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.CREDITS_NOTE;
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
import static org.folio.ld.dictionary.PropertyDictionary.GOVERNING_ACCESS_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.INFORMATION_ABOUT_DOCUMENTATION;
import static org.folio.ld.dictionary.PropertyDictionary.INFORMATION_RELATING_TO_COPYRIGHT_STATUS;
import static org.folio.ld.dictionary.PropertyDictionary.ISSUANCE;
import static org.folio.ld.dictionary.PropertyDictionary.ISSUANCE_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.ISSUING_BODY;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LANGUAGE;
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
import static org.folio.ld.dictionary.PropertyDictionary.PARTICIPANT_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.PHYSICAL_DESCRIPTION;
import static org.folio.ld.dictionary.PropertyDictionary.PROJECTED_PROVISION_DATE;
import static org.folio.ld.dictionary.PropertyDictionary.PROVIDER_DATE;
import static org.folio.ld.dictionary.PropertyDictionary.PUBLICATION_FREQUENCY;
import static org.folio.ld.dictionary.PropertyDictionary.QUALIFIER;
import static org.folio.ld.dictionary.PropertyDictionary.RELATED_PARTS;
import static org.folio.ld.dictionary.PropertyDictionary.REPRODUCTION_NOTE;
import static org.folio.ld.dictionary.PropertyDictionary.RESPONSIBILITY_STATEMENT;
import static org.folio.ld.dictionary.PropertyDictionary.SIMPLE_PLACE;
import static org.folio.ld.dictionary.PropertyDictionary.SOURCE;
import static org.folio.ld.dictionary.PropertyDictionary.SUBTITLE;
import static org.folio.ld.dictionary.PropertyDictionary.SUMMARY;
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
import static org.folio.ld.dictionary.ResourceTypeDictionary.FAMILY;
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
import static org.folio.ld.dictionary.ResourceTypeDictionary.SUPPLEMENTARY_CONTENT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.VARIANT_TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.mapper.test.TestUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc4ld.model.Resource;
import org.folio.marc4ld.model.ResourceEdge;
import org.folio.marc4ld.util.BibframeUtil;


@UtilityClass
public class MonographTestUtil {

  public static Resource getSampleInstanceResource() {
    var instanceTitle = createResource(
      Map.of(
        PART_NAME, List.of("Instance PartName"),
        PART_NUMBER, List.of("8"),
        MAIN_TITLE, List.of("Instance MainTitle"),
        NON_SORT_NUM, List.of("7"),
        SUBTITLE, List.of("Instance SubTitle")
      ),
      Set.of(ResourceTypeDictionary.TITLE),
      emptyMap()
    ).setLabel("Instance MainTitle");

    var instanceTitle2 = createResource(
      Map.of(
        MAIN_TITLE, List.of("Instance Title empty")
      ),
      Set.of(ResourceTypeDictionary.TITLE),
      emptyMap()
    ).setLabel("Instance Title empty");

    var instanceTitle3 = createResource(
      Map.of(
        SUBTITLE, List.of("Instance Title empty label")
      ),
      Set.of(ResourceTypeDictionary.TITLE),
      emptyMap()
    ).setLabel("Instance Title empty label");

    var parallelTitle = createResource(
      Map.of(
        PART_NAME, List.of("Parallel-PartName"),
        PART_NUMBER, List.of("6"),
        MAIN_TITLE, List.of("Parallel-MainTitle"),
        DATE, List.of("2023-01-01"),
        SUBTITLE, List.of("Parallel-SubTitle"),
        NOTE, List.of("Parallel-Note")
      ),
      Set.of(PARALLEL_TITLE),
      emptyMap()
    ).setLabel("Parallel-MainTitle");

    var variantTitle = createResource(
      Map.of(
        PART_NAME, List.of("Variant-PartName"),
        PART_NUMBER, List.of("5"),
        MAIN_TITLE, List.of("Variant-MainTitle"),
        DATE, List.of("2023-02-02"),
        SUBTITLE, List.of("Variant-SubTitle"),
        VARIANT_TYPE, List.of("9"),
        NOTE, List.of("Variant-Note")
      ),
      Set.of(VARIANT_TITLE),
      emptyMap()
    ).setLabel("Variant-MainTitle");

    var production = providerEvent("Production");
    var publication = providerEvent("Publication");
    var distribution = providerEvent("Distribution");
    var manufacture = providerEvent("Manufacture");

    var supplementaryContent = createResource(
      Map.of(
        LINK, List.of("supplementaryContent link"),
        NAME, List.of("supplementaryContent name")
      ),
      Set.of(SUPPLEMENTARY_CONTENT),
      emptyMap()
    ).setLabel("supplementaryContent label");

    var accessLocation = createResource(
      Map.of(
        LINK, List.of("access location URI"),
        NOTE, List.of("access location note")
      ),
      Set.of(ANNOTATION),
      emptyMap()
    ).setLabel("access location URI");

    var lccn = createResource(
      Map.of(NAME, List.of("2019493854")),
      Set.of(IDENTIFIER, ID_LCCN),
      Map.of(STATUS, List.of(status("current")))
    ).setLabel("2019493854");

    var lccnCancelled = createResource(
      Map.of(NAME, List.of("88888888")),
      Set.of(IDENTIFIER, ID_LCCN),
      Map.of(STATUS, List.of(status("canceled or invalid")))
    ).setLabel("88888888");

    var isbn = createResource(
      Map.of(
        NAME, List.of("9780143789963"),
        QUALIFIER, List.of("(paperback)")
      ),
      Set.of(IDENTIFIER, ID_ISBN),
      Map.of(STATUS, List.of(status("current")))
    ).setLabel("9780143789963");

    var isbnCancelled = createResource(
      Map.of(
        NAME, List.of("9999999"),
        QUALIFIER, List.of("(paperback)")
      ),
      Set.of(IDENTIFIER, ID_ISBN),
      Map.of(STATUS, List.of(status("canceled or invalid")))
    ).setLabel("9999999");

    var ean = createResource(
      Map.of(
        EAN_VALUE, List.of("111222"),
        QUALIFIER, List.of("eanIdQal")
      ),
      Set.of(IDENTIFIER, ID_EAN),
      Map.of(STATUS, List.of(status("current")))
    ).setLabel("111222");

    var eanCancelled = createResource(
      Map.of(
        EAN_VALUE, List.of("333"),
        QUALIFIER, List.of("eanIdQal")
      ),
      Set.of(IDENTIFIER, ID_EAN),
      Map.of(STATUS, List.of(status("canceled or invalid")))
    ).setLabel("333");

    var localId = createResource(
      Map.of(
        LOCAL_ID_VALUE, List.of("19861509")
      ),
      Set.of(IDENTIFIER, ID_LOCAL),
      Map.of(STATUS, List.of(status("current")))
    ).setLabel("19861509");

    var localIdCancelled = createResource(
      Map.of(
        LOCAL_ID_VALUE, List.of("09151986")
      ),
      Set.of(IDENTIFIER, ID_LOCAL),
      Map.of(STATUS, List.of(status("canceled or invalid")))
    ).setLabel("09151986");

    var otherId = createResource(
      Map.of(
        NAME, List.of("20232023"),
        QUALIFIER, List.of("otherIdQal")
      ),
      Set.of(IDENTIFIER, ID_UNKNOWN),
      Map.of(STATUS, List.of(status("current")))
    ).setLabel("20232023");

    var otherIdCancelled = createResource(
      Map.of(
        NAME, List.of("231123"),
        QUALIFIER, List.of("otherIdQal")
      ),
      Set.of(IDENTIFIER, ID_UNKNOWN),
      Map.of(STATUS, List.of(status("canceled or invalid")))
    ).setLabel("231123");

    var media = createResource(
      Map.of(
        CODE, List.of("MEDIA code"),
        TERM, List.of("MEDIA term"),
        LINK, List.of("http://id.loc.gov/vocabulary/mediaTypes/MEDIA code")
      ),
      Set.of(CATEGORY),
      emptyMap()
    ).setLabel("MEDIA term");

    var carrier = createResource(
      Map.of(
        CODE, List.of("CARRIER code"),
        TERM, List.of("CARRIER term"),
        LINK, List.of("http://id.loc.gov/vocabulary/carrierTypes/CARRIER code")
      ),
      Set.of(CATEGORY),
      emptyMap()
    ).setLabel("CARRIER term");

    var copyrightEvent = createResource(
      Map.of(
        DATE, List.of("2018")
      ),
      Set.of(COPYRIGHT_EVENT),
      emptyMap()
    ).setLabel("2018");

    var pred2OutgoingResources = new LinkedHashMap<PredicateDictionary, List<Resource>>();
    pred2OutgoingResources.put(TITLE,
      List.of(instanceTitle, parallelTitle, variantTitle, instanceTitle2, instanceTitle3));
    pred2OutgoingResources.put(PE_PRODUCTION, List.of(production));
    pred2OutgoingResources.put(PE_PUBLICATION, List.of(publication));
    pred2OutgoingResources.put(PE_DISTRIBUTION, List.of(distribution));
    pred2OutgoingResources.put(PE_MANUFACTURE, List.of(manufacture));
    //pred2OutgoingResources.put(PredicateDictionary.SUPPLEMENTARY_CONTENT, List.of(supplementaryContent));
    pred2OutgoingResources.put(ACCESS_LOCATION, List.of(accessLocation));
    pred2OutgoingResources.put(MAP,
      List.of(lccn, lccnCancelled, isbn, isbnCancelled, ean, eanCancelled, localId, localIdCancelled, otherId,
        otherIdCancelled));
    pred2OutgoingResources.put(MEDIA, List.of(media));
    pred2OutgoingResources.put(CARRIER, List.of(carrier));
    pred2OutgoingResources.put(COPYRIGHT, List.of(copyrightEvent));
    pred2OutgoingResources.put(INSTANTIATES, List.of(createSampleWork()));

    var instance = createResource(
      Map.ofEntries(
        entry(EXTENT, List.of("extent")),
        entry(DIMENSIONS, List.of("dimensions")),
        entry(EDITION_STATEMENT, List.of("Edition Statement Edition statement2")),
        entry(PROJECTED_PROVISION_DATE, List.of("projectedProvisionDate")),
        entry(ISSUANCE, List.of("issuance")),
        entry(ACCESSIBILITY_NOTE, List.of("accessibility note")),
        entry(ADDITIONAL_PHYSICAL_FORM, List.of("additional physical form note")),
        entry(CITATION_COVERAGE, List.of("citation coverage note")),
        entry(COMPUTER_DATA_NOTE, List.of("computer data note")),
        entry(CREDITS_NOTE, List.of("credits note")),
        entry(DATES_OF_PUBLICATION_NOTE, List.of("dates, source")),
        entry(DESCRIPTION_SOURCE_NOTE, List.of("description source note")),
        entry(ENTITY_AND_ATTRIBUTE_INFORMATION,
          List.of("label, definition, attribute label, source, value, domain definition, range, codeset, domain, "
            + "units, date, accuracy, explanation, frequency, overview, citation, uri, note")),
        entry(EXHIBITIONS_NOTE, List.of("exhibitions note")),
        entry(FORMER_TITLE_NOTE, List.of("former title note")),
        entry(FUNDING_INFORMATION,
          List.of("text, contact, grant, undifferentiated, program element, project, task, work unit")),
        entry(GOVERNING_ACCESS_NOTE,
          List.of("terms, jurisdiction, provisions, users, authorization, terminology, date, agency")),
        entry(INFORMATION_ABOUT_DOCUMENTATION, List.of("info about doc note")),
        entry(INFORMATION_RELATING_TO_COPYRIGHT_STATUS,
          List.of("creator, date, corporate creator, holder, information, statement, copyright date, renewal date, "
            + "publication date, creation date, publisher, status, publication status, note, research date, country, "
            + "agency, jurisdiction")),
        entry(ISSUANCE_NOTE, List.of("issuance note")),
        entry(ISSUING_BODY, List.of("issuing body note")),
        entry(LOCATION_OF_ORIGINALS_DUPLICATES, List.of("custodian, postal address, country, address, code")),
        entry(LOCATION_OF_OTHER_ARCHIVAL_MATERIAL, List.of("note, source, control, reference")),
        entry(NOTE, List.of("general note")),
        entry(ORIGINAL_VERSION_NOTE,
          List.of("entry, statement, publication, description, series statement, key title, details, note, title")),
        entry(PARTICIPANT_NOTE, List.of("participant note")),
        entry(PHYSICAL_DESCRIPTION, List.of("extent, details")),
        entry(PUBLICATION_FREQUENCY, List.of("frequency, date")),
        entry(RELATED_PARTS, List.of("custodian, address, country, title, provenance, note")),
        entry(REPRODUCTION_NOTE, List.of("type, place, agency, date, description, statement, note")),
        entry(SYSTEM_DETAILS, List.of("note, text, uri")),
        entry(SYSTEM_DETAILS_ACCESS_NOTE, List.of("model, language, system")),
        entry(TYPE_OF_REPORT, List.of("type, period")),
        entry(WITH_NOTE, List.of("with note"))
      ),
      Set.of(INSTANCE),
      pred2OutgoingResources)
      .setInventoryId(UUID.fromString("2165ef4b-001f-46b3-a60e-52bcdeb3d5a1"))
      .setSrsId(UUID.fromString("43d58061-decf-4d74-9747-0e1c368e861b"));

    setEdgesId(instance);
    return instance;
  }

  public static Resource createSampleWork() {
    var content = createResource(
      Map.of(
        TERM, List.of("CONTENT term"),
        LINK, List.of("http://id.loc.gov/vocabulary/contentTypes/CONTENT code"),
        CODE, List.of("CONTENT code")
      ),
      Set.of(CATEGORY),
      emptyMap()
    ).setLabel("CONTENT term");

    var deweyClassification = createResource(
      Map.of(
        CODE, List.of("Dewey Decimal Classification value"),
        SOURCE, List.of("ddc")
      ),
      Set.of(CATEGORY),
      emptyMap()
    ).setLabel("Dewey Decimal Classification value");

    var meetingCreator = createResource(
      Map.of(
        NAME, List.of("CREATOR MEETING name"),
        LCNAF_ID, List.of("CREATOR MEETING LCNAF id")
      ),
      Set.of(MEETING),
      emptyMap()
    ).setLabel("CREATOR MEETING name");

    var personCreator = createResource(
      Map.of(
        NAME, List.of("CREATOR PERSON name"),
        LCNAF_ID, List.of("CREATOR PERSON LCNAF id")
      ),
      Set.of(PERSON),
      emptyMap()
    ).setLabel("CREATOR PERSON name");

    var familyCreator = createResource(
            Map.of(
                    NAME, List.of("CREATOR FAMILY name"),
                    LCNAF_ID, List.of("CREATOR FAMILY LCNAF id")
            ),
            Set.of(FAMILY),
            emptyMap()
    ).setLabel("CREATOR FAMILY name");

    var organizationCreator = createResource(
      Map.of(
        NAME, List.of("CREATOR ORGANIZATION name"),
        LCNAF_ID, List.of("CREATOR ORGANIZATION LCNAF id")
      ),
      Set.of(ORGANIZATION),
      emptyMap()
    ).setLabel("CREATOR ORGANIZATION name");

    var meetingContributor = createResource(
      Map.of(
        NAME, List.of("CONTRIBUTOR MEETING name"),
        LCNAF_ID, List.of("CONTRIBUTOR MEETING LCNAF id")
      ),
      Set.of(MEETING),
      emptyMap()
    ).setLabel("CONTRIBUTOR MEETING name");

    var personContributor = createResource(
      Map.of(
        NAME, List.of("CONTRIBUTOR PERSON name"),
        LCNAF_ID, List.of("CONTRIBUTOR PERSON LCNAF id")
      ),
      Set.of(PERSON),
      emptyMap()
    ).setLabel("CONTRIBUTOR PERSON name");

    var familyContributor = createResource(
            Map.of(
                    NAME, List.of("CONTRIBUTOR FAMILY name"),
                    LCNAF_ID, List.of("CONTRIBUTOR FAMILY LCNAF id")
            ),
            Set.of(FAMILY),
            emptyMap()
    ).setLabel("CONTRIBUTOR FAMILY name");

    var organizationContributor = createResource(
      Map.of(
        NAME, List.of("CONTRIBUTOR ORGANIZATION name"),
        LCNAF_ID, List.of("CONTRIBUTOR ORGANIZATION LCNAF id")
      ),
      Set.of(ORGANIZATION),
      emptyMap()
    ).setLabel("CONTRIBUTOR ORGANIZATION name");

    var pred2OutgoingResources = new LinkedHashMap<PredicateDictionary, List<Resource>>();
    pred2OutgoingResources.put(CLASSIFICATION, List.of(deweyClassification));
    pred2OutgoingResources.put(CREATOR, List.of(meetingCreator, personCreator, organizationCreator, familyCreator));
    pred2OutgoingResources.put(CONTRIBUTOR, List.of(meetingContributor, personContributor, organizationContributor,
            familyContributor));
    pred2OutgoingResources.put(CONTENT, List.of(content));

    return createResource(
      Map.of(
        TARGET_AUDIENCE, List.of("primary"),
        LANGUAGE, List.of("eng"),
        SUMMARY, List.of("work summary"),
        TABLE_OF_CONTENTS, List.of("work table of contents"),
        RESPONSIBILITY_STATEMENT, List.of("Statement Of Responsibility")
      ),
      Set.of(WORK),
      pred2OutgoingResources
    ).setLabel("Work: label");
  }

  private Resource status(String value) {
    return createResource(
      Map.of(
        LABEL, List.of(value),
        LINK, List.of("http://id.loc.gov/vocabulary/mstatus/" + (value.equals("canceled or invalid") ? "cancinv" : value))
      ),
      Set.of(ResourceTypeDictionary.STATUS),
      emptyMap()
    ).setLabel(value);
  }

  private Resource providerEvent(String prefix) {
    return createResource(
      Map.of(
        DATE, List.of(prefix + " Date"),
        NAME, List.of(prefix + " Name"),
        PROVIDER_DATE, List.of("1999"),
        SIMPLE_PLACE, List.of(prefix + " Place")
      ),
      Set.of(PROVIDER_EVENT),
      Map.of(PROVIDER_PLACE, List.of(providerPlace(prefix)))
    ).setLabel(prefix + " label");
  }

  private Resource providerPlace(String providerEventType) {
    return createResource(
      Map.of(
        CODE, List.of("ilu"),
        LABEL, List.of(providerEventType + " Place"),
        LINK, List.of("http://id.loc.gov/vocabulary/countries/ilu")
      ),
      Set.of(PLACE),
      emptyMap()
    ).setLabel(providerEventType + " Place");
  }

  private Resource createResource(Map<PropertyDictionary, List<String>> propertiesDic,
                                  Set<ResourceTypeDictionary> types,
                                  Map<PredicateDictionary, List<Resource>> pred2OutgoingResources) {
    var resource = new Resource();
    pred2OutgoingResources.keySet()
      .stream()
      .flatMap(pred -> pred2OutgoingResources.get(pred)
        .stream()
        .map(target -> new ResourceEdge(resource, target, pred)))
      .forEach(edge -> resource.getOutgoingEdges().add(edge));

    Map<String, List<String>> properties = propertiesDic.entrySet().stream()
      .collect(Collectors.toMap(e -> e.getKey().getValue(), Map.Entry::getValue));
    resource.setDoc(getJsonNode(properties));
    types.forEach(resource::addType);
    resource.setResourceHash(BibframeUtil.hash(resource, OBJECT_MAPPER));
    return resource;
  }

  public static JsonNode getJsonNode(Map<String, ?> map) {
    return OBJECT_MAPPER.convertValue(map, JsonNode.class);
  }

  private void setEdgesId(Resource resource) {
    resource.getOutgoingEdges().forEach(edge -> {
      edge.getId().setSourceHash(edge.getSource().getResourceHash());
      edge.getId().setTargetHash(edge.getTarget().getResourceHash());
      edge.getId().setPredicateHash(edge.getPredicate().getHash());
      setEdgesId(edge.getTarget());
    });
  }

}
