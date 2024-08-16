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
import org.folio.marc4ld.util.Constants;
import org.folio.marc4ld.util.MarcUtil;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorityIdentifierProcessorImpl implements AuthorityIdentifierProcessor {

  public static final String IDENTIFIER_TAG = "010";

  private final LabelService labelService;
  private final MapperHelper mapperHelper;
  private final FingerprintHashService hashService;

  // Temporary realisation of setting identifier
  @Override
  public void setIdentifier(Resource resource, DataField dataField) {
    if (ObjectUtils.notEqual(dataField.getTag(), IDENTIFIER_TAG)) {
      return;
    }
    MarcUtil.getSubfieldValueWithoutSpaces(dataField, Constants.A)
      .ifPresent(data -> {
        var target = new Resource()
          .addType(ResourceTypeDictionary.ID_LCCN)
          .addType(ResourceTypeDictionary.IDENTIFIER);

        var properties = makeProperties(data);
        labelService.setLabel(target, properties);

        target
          .setDoc(mapperHelper.getJsonNode(properties))
          .setId(hashService.hash(target));
        var edge = new ResourceEdge(resource, target, PredicateDictionary.MAP);
        resource.addOutgoingEdge(edge);
      });
  }

  private Map<String, List<String>> makeProperties(String data) {
    return new HashMap<>(Map.of(
      NAME.getValue(), List.of(data),
      LINK.getValue(), List.of("http://id.loc.gov/authorities/" + data)
    ));
  }
}
