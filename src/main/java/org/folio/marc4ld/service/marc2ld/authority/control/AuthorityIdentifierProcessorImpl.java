package org.folio.marc4ld.service.marc2ld.authority.control;

import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.mapper.MapperHelper;
import org.marc4j.marc.ControlField;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorityIdentifierProcessorImpl implements AuthorityIdentifierProcessor {

  public static final String IDENTIFIER_TAG = "001";

  private final LabelService labelService;
  private final MapperHelper mapperHelper;
  private final FingerprintHashService hashService;

  // Temporary realisation of setting identifier
  @Override
  public void setIdentifier(Resource resource, ControlField controlField) {
    if (ObjectUtils.notEqual(controlField.getTag(), IDENTIFIER_TAG)) {
      return;
    }
    var data = controlField.getData();
    var properties = new HashMap<>(Map.of(
      NAME.getValue(), List.of(data),
      LINK.getValue(), List.of("http://id.loc.gov/authorities/" + data)
    ));
    var target = new Resource();
    target.addType(ResourceTypeDictionary.ID_LCCN);
    target.addType(ResourceTypeDictionary.IDENTIFIER);

    labelService.setLabel(target, properties);
    target.setDoc(mapperHelper.getJsonNode(properties));
    target.setId(hashService.hash(target));
    var edge = new ResourceEdge(resource, target, PredicateDictionary.MAP);
    resource.addOutgoingEdge(edge);
  }
}
