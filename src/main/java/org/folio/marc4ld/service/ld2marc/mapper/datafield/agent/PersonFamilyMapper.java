package org.folio.marc4ld.service.ld2marc.mapper.datafield.agent;

import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PropertyDictionary.AFFILIATION;
import static org.folio.ld.dictionary.PropertyDictionary.ATTRIBUTION;
import static org.folio.ld.dictionary.PropertyDictionary.AUTHORITY_LINK;
import static org.folio.ld.dictionary.PropertyDictionary.CONTROL_FIELD;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.EQUIVALENT;
import static org.folio.ld.dictionary.PropertyDictionary.FIELD_LINK;
import static org.folio.ld.dictionary.PropertyDictionary.LINKAGE;
import static org.folio.ld.dictionary.PropertyDictionary.MISC_INFO;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.NAME_ALTERNATIVE;
import static org.folio.ld.dictionary.PropertyDictionary.NUMBER_OF_PARTS;
import static org.folio.ld.dictionary.PropertyDictionary.NUMERATION;
import static org.folio.ld.dictionary.PropertyDictionary.TITLES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FAMILY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.TAG_100;
import static org.folio.marc4ld.util.Constants.TAG_700;
import static org.folio.marc4ld.util.Constants.THREE;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
public class PersonFamilyMapper extends AgentMapper {

  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(PERSON, FAMILY);
  private static final Map<Character, String> REPEATABLE_SUBFIELD_PROPERTY_MAP = Map.of(
    'c', TITLES.getValue(),
    'g', MISC_INFO.getValue(),
    'j', ATTRIBUTION.getValue(),
    'n', NUMBER_OF_PARTS.getValue(),
    '0', AUTHORITY_LINK.getValue(),
    '1', EQUIVALENT.getValue(),
    '7', CONTROL_FIELD.getValue(),
    '8', FIELD_LINK.getValue()
  );
  private static final Map<Character, String> NON_REPEATABLE_SUBFIELD_PROPERTY_MAP = Map.of(
    'a', NAME.getValue(),
    'b', NUMERATION.getValue(),
    'd', DATE.getValue(),
    'q', NAME_ALTERNATIVE.getValue(),
    'u', AFFILIATION.getValue(),
    '6', LINKAGE.getValue()
  );

  protected PersonFamilyMapper(MarcFactory marcFactory, Comparator<Subfield> comparator) {
    super(marcFactory, comparator);
  }

  @Override
  protected Set<ResourceTypeDictionary> getSupportedTypes() {
    return SUPPORTED_TYPES;
  }

  @Override
  protected String getTag(ResourceEdge resourceEdge) {
    return resourceEdge.getPredicate() == CREATOR ? TAG_100 : TAG_700;
  }

  @Override
  protected Map<Character, String> getRepeatableSubfieldPropertyMap() {
    return REPEATABLE_SUBFIELD_PROPERTY_MAP;
  }

  @Override
  protected Map<Character, String> getNonRepeatableSubfieldPropertyMap() {
    return NON_REPEATABLE_SUBFIELD_PROPERTY_MAP;
  }

  @Override
  protected char getIndicator1(Resource resource) {
    return resource.getTypes().contains(FAMILY) ? THREE : SPACE;
  }
}
