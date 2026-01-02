package org.folio.marc4ld.service.marc2ld.authority.identifier;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_FAST;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_GND;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCSH;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCDGT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCGFT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCMPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCNAF;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCSH;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LOCAL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_MESH;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_VIAF;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_WIKIID;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.TAG_010;
import static org.folio.marc4ld.util.MarcUtil.getSubfieldValueWithoutSpaces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdentifierResourceProviderImpl implements IdentifierResourceProvider {

  private final LabelService labelService;
  private final IdentifierLinkService identifierLinkService;
  private final MapperHelper mapperHelper;
  private final FingerprintHashService hashService;

  public Set<Resource> getIdentifierResources(Record marc) {
    return getIdentifiers(marc)
      .stream()
      .map(this::getIdentifierResource)
      .collect(toSet());
  }

  private Set<String> getIdentifiers(Record marc) {
    return Stream.of(getControlNumber(marc), get010SubfieldA(marc))
      .flatMap(Optional::stream)
      .collect(toSet());
  }

  private Optional<String> getControlNumber(Record marc) {
    return ofNullable(marc.getControlNumber())
      .map(StringUtils::deleteWhitespace);
  }

  private Optional<String> get010SubfieldA(Record marc) {
    return marc.getDataFields()
      .stream()
      .filter(df -> df.getTag().equals(TAG_010))
      .flatMap(df -> getSubfieldValueWithoutSpaces(df, A).stream())
      .findFirst();
  }

  private Resource getIdentifierResource(String identifier) {
    var identifierType = deriveIdentifierType(identifier);

    var resource = new Resource()
      .addType(IDENTIFIER)
      .addType(identifierType);

    var properties = makeProperties(identifier, identifierType);
    labelService.setLabel(resource, properties);

    resource
      .setDoc(mapperHelper.getJsonNode(properties))
      .setId(hashService.hash(resource));

    return resource;
  }

  private Map<String, List<String>> makeProperties(String identifier, ResourceTypeDictionary identifierType) {
    var properties = new HashMap<String, List<String>>();
    properties.put(NAME.getValue(), List.of(identifier));
    identifierLinkService.getIdentifierLink(identifier, identifierType)
      .ifPresent(link -> properties.put(LINK.getValue(), List.of(link)));
    return properties;
  }

  private ResourceTypeDictionary deriveIdentifierType(String identifier) {
    var prefix = identifier == null ? "" : identifier.split("[^a-zA-Z]", 2)[0].toLowerCase();

    return switch (prefix) {
      case "n", "no", "nb", "nr", "ns" -> ID_LCNAF;
      case "sh" -> ID_LCSH;
      case "sj" -> ID_LCCSH;
      case "dg" -> ID_LCDGT;
      case "gf" -> ID_LCGFT;
      case "mp" -> ID_LCMPT;
      case "fst" -> ID_FAST;
      case "d" -> ID_MESH;
      case "gnd" -> ID_GND;
      case "q" -> ID_WIKIID;
      case "viaf" -> ID_VIAF;
      default -> ID_LOCAL;
    };
  }
}
