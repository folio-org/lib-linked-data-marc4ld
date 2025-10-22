package org.folio.marc4ld.service.marc2ld.bib.mapper.custom;

import static java.lang.Character.isDigit;
import static java.lang.Character.toLowerCase;
import static java.lang.String.join;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.EXPRESSION_OF;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LANGUAGE;
import static org.folio.ld.dictionary.PropertyDictionary.LEGAL_DATE;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.MUSIC_KEY;
import static org.folio.ld.dictionary.PropertyDictionary.NON_SORT_NUM;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.VERSION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.marc4ld.util.Constants.D;
import static org.folio.marc4ld.util.Constants.F;
import static org.folio.marc4ld.util.Constants.H;
import static org.folio.marc4ld.util.Constants.L;
import static org.folio.marc4ld.util.Constants.N;
import static org.folio.marc4ld.util.Constants.P;
import static org.folio.marc4ld.util.Constants.R;
import static org.folio.marc4ld.util.Constants.S;
import static org.folio.marc4ld.util.Constants.TAG_240;
import static org.folio.marc4ld.util.LdUtil.getOutgoingEdges;
import static org.folio.marc4ld.util.LdUtil.getWork;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.CustomMapper;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Hub240Mapper implements CustomMapper {
  private final FingerprintHashService hashService;
  private final LabelService labelService;
  private final MapperHelper mapperHelper;

  @Override
  public boolean isApplicable(Record marcRecord) {
    return marcRecord.getDataFields()
      .stream()
      .anyMatch(df -> df.getTag().equals(TAG_240));
  }

  @Override
  public void map(Record marcRecord, Resource instance) {
    var workOpt = getWork(instance);
    if (workOpt.isEmpty()) {
      return;
    }

    var work = workOpt.get();
    var creators = getOutgoingEdges(work, CREATOR).stream()
      .map(ResourceEdge::getTarget)
      .collect(toSet());

    marcRecord.getDataFields().stream()
      .filter(df -> df.getTag().equals(TAG_240))
      .map(df -> createHubResource(df, creators))
      .map(hub -> new ResourceEdge(work, hub, EXPRESSION_OF))
      .forEach(work::addOutgoingEdge);
  }

  private Resource createHubResource(DataField df, Set<Resource> creators) {
    var title = createTitleResource(df);
    return createHubResource(df, title, creators);
  }

  private Resource createHubResource(DataField df, Resource title, Set<Resource> creators) {
    var label = creators.stream()
      .map(Resource::getLabel)
      .findFirst()
      .map(creatorLabel -> join(" ", creatorLabel, getLabel(df)))
      .orElseGet(() -> getLabel(df));

    var properties = new LinkedHashMap<String, List<String>>();
    setPropertyValue(properties, LABEL, List.of(label));
    setPropertyValue(properties, LEGAL_DATE, getSubfieldValues(df, D));
    setPropertyValue(properties, DATE, getSubfieldValues(df, F));
    setPropertyValue(properties, LANGUAGE, getSubfieldValues(df, L));
    setPropertyValue(properties, MUSIC_KEY, getSubfieldValues(df, R));
    setPropertyValue(properties, VERSION, getSubfieldValues(df, S));

    var hub = createResource(HUB, properties);
    hub.addOutgoingEdge(new ResourceEdge(hub, title, TITLE));
    creators.stream()
      .map(creator -> new ResourceEdge(hub, creator, CREATOR))
      .forEach(hub::addOutgoingEdge);
    hub.setId(hashService.hash(hub));
    return hub;
  }

  private Resource createTitleResource(DataField df) {
    var properties = new LinkedHashMap<String, List<String>>();
    setPropertyValue(properties, MAIN_TITLE, List.of(getLabel(df)));
    setPropertyValue(properties, PART_NAME, getSubfieldValues(df, P));
    setPropertyValue(properties, PART_NUMBER, getSubfieldValues(df, N));
    setPropertyValue(properties, NON_SORT_NUM, List.of(String.valueOf(df.getIndicator2())));

    var title = createResource(ResourceTypeDictionary.TITLE, properties);
    title.setId(hashService.hash(title));
    return title;
  }

  private Resource createResource(ResourceTypeDictionary type, LinkedHashMap<String, List<String>> properties) {
    var resource = new Resource()
      .addType(type)
      .setDoc(mapperHelper.getJsonNode(properties));
    labelService.setLabel(resource, properties);
    return resource;
  }

  private List<String> getSubfieldValues(DataField df, char code) {
    return df.getSubfields().stream()
      .filter(sf -> sf.getCode() == code)
      .map(Subfield::getData)
      .distinct()
      .toList();
  }

  private String getLabel(DataField df) {
    return df.getSubfields().stream()
      .filter(this::notNumericAndNotSubfieldH)
      .map(Subfield::getData)
      .collect(joining(" "));
  }

  private boolean notNumericAndNotSubfieldH(Subfield sf) {
    char code = sf.getCode();
    return !isDigit(code) && toLowerCase(code) != H;
  }

  private void setPropertyValue(Map<String, List<String>> properties, PropertyDictionary key, List<String> values) {
    if (isNotEmpty(values)) {
      properties.put(key.getValue(), values);
    }
  }
}
