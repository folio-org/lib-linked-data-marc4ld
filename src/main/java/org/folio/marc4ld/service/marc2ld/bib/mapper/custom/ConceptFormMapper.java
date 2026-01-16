package org.folio.marc4ld.service.marc2ld.bib.mapper.custom;

import static java.util.Collections.emptyMap;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.GENRE;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FORM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.authority.identifier.IdentifierLinkProvider;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.springframework.stereotype.Component;

@Component
public class ConceptFormMapper extends AbstractBookMapper {

  private static final String LAW_MATERIALS = "Law materials";
  private static final String LAW_MATERIALS_LCCN = "gf2011026351";
  private static final Map<Character, String> CODE_TO_LABEL_MAP = Map.ofEntries(
    Map.entry('c', "Catalogs"),
    Map.entry('d', "Dictionaries"),
    Map.entry('e', "Encyclopedias"),
    Map.entry('f', "Handbooks and manuals"),
    Map.entry('i', "Indexes"),
    Map.entry('j', "Patents"),
    Map.entry('l', LAW_MATERIALS),
    Map.entry('m', "Academic theses"),
    Map.entry('r', "Directories"),
    Map.entry('s', "Statistics"),
    Map.entry('t', "Technical reports"),
    Map.entry('v', LAW_MATERIALS),
    Map.entry('w', LAW_MATERIALS),
    Map.entry('y', "Yearbooks"),
    Map.entry('z', "Treaties"),
    Map.entry('5', "Calendars"),
    Map.entry('6', "Comics (Graphic works)")
  );
  private static final Map<Character, String> CODE_TO_LCCN_MAP = Map.ofEntries(
    Map.entry('c', "gf2014026057"),
    Map.entry('d', "gf2014026086"),
    Map.entry('e', "gf2014026092"),
    Map.entry('f', "gf2014026109"),
    Map.entry('i', "gf2014026112"),
    Map.entry('j', "gf2011026438"),
    Map.entry('l', LAW_MATERIALS_LCCN),
    Map.entry('m', "gf2014026039"),
    Map.entry('r', "gf2014026087"),
    Map.entry('s', "gf2014026181"),
    Map.entry('t', "gf2015026093"),
    Map.entry('v', LAW_MATERIALS_LCCN),
    Map.entry('w', LAW_MATERIALS_LCCN),
    Map.entry('y', "gf2014026208"),
    Map.entry('z', "gf2011026707"),
    Map.entry('5', "gf2014026055"),
    Map.entry('6', "gf2014026266")
  );

  private final MapperHelper mapperHelper;
  private final FingerprintHashService hashService;
  private final IdentifierLinkProvider identifierLinkProvider;

  public ConceptFormMapper(LabelService labelService,
                           MapperHelper mapperHelper,
                           FingerprintHashService hashService,
                           IdentifierLinkProvider identifierLinkProvider) {
    super(labelService, mapperHelper, hashService, 24, 28);
    this.mapperHelper = mapperHelper;
    this.hashService = hashService;
    this.identifierLinkProvider = identifierLinkProvider;
  }

  @Override
  protected boolean isSupportedCode(char code) {
    return CODE_TO_LABEL_MAP.containsKey(code) && CODE_TO_LCCN_MAP.containsKey(code);
  }

  @Override
  protected void addSubResource(Resource resource, char code) {
    var lccnResource = getLccnResource(CODE_TO_LCCN_MAP.get(code));
    var form = createResource(Set.of(FORM), Map.of(
      NAME.getValue(), List.of(CODE_TO_LABEL_MAP.get(code))
    ), Map.of(MAP, lccnResource));
    var conceptForm = createResource(Set.of(CONCEPT, FORM), Map.of(
      NAME.getValue(), List.of(CODE_TO_LABEL_MAP.get(code))
    ), Map.of(FOCUS, form));
    resource.addOutgoingEdge(new ResourceEdge(resource, conceptForm, SUBJECT));
    resource.addOutgoingEdge(new ResourceEdge(resource, form, GENRE));
  }

  private Resource getLccnResource(String lccn) {
    var props = new HashMap<String, List<String>>();
    props.put(NAME.getValue(), List.of(lccn));
    props.put(LABEL.getValue(), List.of(lccn));
    identifierLinkProvider.getIdentifierLink(lccn)
      .ifPresent(link -> props.put(LINK.getValue(), List.of(link)));
    return createResource(Set.of(ID_LCCN, IDENTIFIER), props, emptyMap());
  }

  @Override
  protected Resource createResource(Set<ResourceTypeDictionary> types, Map<String, List<String>> properties,
                                    Map<PredicateDictionary, Resource> outgoingResources) {
    var resource = new Resource()
      .setTypes(types)
      .setDoc(mapperHelper.getJsonNode(properties));
    outgoingResources.forEach((key, value) -> resource.addOutgoingEdge(new ResourceEdge(resource, value, key)));
    resource.setLabel(properties.get(NAME.getValue()).getFirst());
    resource.setId(hashService.hash(resource));
    return resource;
  }
}
