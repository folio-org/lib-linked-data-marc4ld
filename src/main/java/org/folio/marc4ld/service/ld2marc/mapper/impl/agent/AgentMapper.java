package org.folio.marc4ld.service.ld2marc.mapper.impl.agent;

import static java.util.Optional.ofNullable;
import static org.folio.ld.dictionary.PredicateDictionary.CONTRIBUTOR;
import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.marc4ld.util.BibframeUtil.getPropertyValue;
import static org.folio.marc4ld.util.BibframeUtil.getPropertyValues;
import static org.folio.marc4ld.util.Constants.Dictionary.AGENT_CODE_TO_PREDICATE;
import static org.folio.marc4ld.util.Constants.E;
import static org.folio.marc4ld.util.Constants.FOUR;
import static org.folio.marc4ld.util.Constants.NINE;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.ZERO;
import static org.folio.marc4ld.util.MarcUtil.orderSubfields;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.FolioMetadata;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.ld2marc.mapper.Ld2MarcMapper;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;

public abstract class AgentMapper implements Ld2MarcMapper {

  private static final Set<PredicateDictionary> SUPPORTED_PREDICATES = Set.of(CREATOR, CONTRIBUTOR);
  private static final String RELATION_PREFIX = "http://bibfra.me/vocab/relation/";

  private final ObjectMapper objectMapper;
  private final DictionaryProcessor dictionaryProcessor;
  private final MarcFactory marcFactory;
  private final Comparator<Subfield> comparator;
  private final DataField emptyDataField;

  protected AgentMapper(ObjectMapper objectMapper, DictionaryProcessor dictionaryProcessor, MarcFactory marcFactory,
                        Comparator<Subfield> comparator) {
    this.objectMapper = objectMapper;
    this.dictionaryProcessor = dictionaryProcessor;
    this.marcFactory = marcFactory;
    this.comparator = comparator;
    emptyDataField = marcFactory.newDataField();
  }

  protected abstract Set<ResourceTypeDictionary> getSupportedTypes();

  protected abstract String getTag(ResourceEdge resourceEdge);

  protected abstract Map<Character, String> getRepeatableSubfieldPropertyMap();

  protected abstract Map<Character, String> getNonRepeatableSubfieldPropertyMap();

  protected char getIndicator1(Resource resource) {
    return SPACE;
  }

  protected char getRelationTextSubfield() {
    return E;
  }

  @Override
  public boolean test(ResourceEdge resourceEdge) {
    return resourceEdge.getPredicate() != null
      && (isAgentEdge(resourceEdge) || isRelationEdge(resourceEdge));
  }

  @Override
  public DataField apply(ResourceEdge resourceEdge) {
    if (isRelationEdge(resourceEdge)) {
      return emptyDataField;
    }
    var resource = resourceEdge.getTarget();
    var dataField = marcFactory.newDataField(getTag(resourceEdge), getIndicator1(resource), SPACE);
    addRepeatableSubfields(dataField, resource);
    addNonRepeatableSubfields(dataField, resource);
    addRelationCodes(dataField, resourceEdge);
    addRelationNames(dataField, resourceEdge);
    addIdentifierLinks(dataField, resource);
    addMetadataSubfields(dataField, resource);
    orderSubfields(dataField, comparator);
    return dataField;
  }

  protected void addMetadataSubfields(DataField dataField, Resource resource) {
    ofNullable(resource.getFolioMetadata())
      .map(FolioMetadata::getInventoryId)
      .filter(StringUtils::isNotBlank)
      .ifPresent(inventoryId -> dataField.addSubfield(marcFactory.newSubfield(NINE, inventoryId)));
  }

  private boolean isAgentEdge(ResourceEdge resourceEdge) {
    return SUPPORTED_PREDICATES.contains(resourceEdge.getPredicate())
      && getSupportedTypes().containsAll(resourceEdge.getTarget().getTypes());
  }

  private boolean isRelationEdge(ResourceEdge resourceEdge) {
    return resourceEdge.getPredicate().getUri().startsWith(RELATION_PREFIX);
  }

  private void addRepeatableSubfields(DataField dataField, Resource resource) {
    getRepeatableSubfieldPropertyMap().forEach((subfield, property) ->
      getPropertyValues(resource, property, getPropertiesConversionFunction())
        .stream()
        .map(value -> marcFactory.newSubfield(subfield, value))
        .forEach(dataField::addSubfield));
  }

  private Function<JsonNode, List<String>> getPropertiesConversionFunction() {
    return node -> objectMapper.convertValue(node, new TypeReference<>() {});
  }

  private void addNonRepeatableSubfields(DataField dataField, Resource resource) {
    getNonRepeatableSubfieldPropertyMap().forEach((subfield, property) ->
      getPropertyValue(resource, property)
        .map(value -> marcFactory.newSubfield(subfield, value))
        .ifPresent(dataField::addSubfield));
  }

  private void addRelationCodes(DataField dataField, ResourceEdge resourceEdge) {
    var agent = resourceEdge.getTarget();
    resourceEdge.getSource().getOutgoingEdges()
      .stream()
      .filter(re -> agent.equals(re.getTarget()))
      .map(ResourceEdge::getPredicate)
      .map(Enum::name)
      .map(predicate -> dictionaryProcessor.getKey(AGENT_CODE_TO_PREDICATE, predicate))
      .flatMap(Optional::stream)
      .map(code -> marcFactory.newSubfield(FOUR, code))
      .forEach(dataField::addSubfield);
  }

  private void addRelationNames(DataField dataField, ResourceEdge resourceEdge) {
    var agent = resourceEdge.getTarget();
    resourceEdge.getSource().getOutgoingEdges()
      .stream()
      .filter(re -> agent.equals(re.getTarget()))
      .map(ResourceEdge::getPredicate)
      .map(PredicateDictionary::getUri)
      .filter(uri -> uri.startsWith(RELATION_PREFIX))
      .map(uri -> uri.substring(RELATION_PREFIX.length()))
      .map(role -> marcFactory.newSubfield(getRelationTextSubfield(), role))
      .forEach(dataField::addSubfield);
  }

  private void addIdentifierLinks(DataField dataField, Resource resource) {
    resource.getOutgoingEdges()
      .stream()
      .filter(this::isIdentifier)
      .map(ResourceEdge::getTarget)
      .map(r -> getPropertyValue(r, LINK.getValue()))
      .flatMap(Optional::stream)
      .map(value -> marcFactory.newSubfield(ZERO, value))
      .forEach(dataField::addSubfield);
  }

  private boolean isIdentifier(ResourceEdge resourceEdge) {
    return MAP.equals(resourceEdge.getPredicate()) && resourceEdge.getTarget().getTypes().contains(IDENTIFIER);
  }
}
