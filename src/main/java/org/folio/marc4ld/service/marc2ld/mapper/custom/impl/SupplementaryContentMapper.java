package org.folio.marc4ld.service.marc2ld.mapper.custom.impl;

import static org.folio.ld.dictionary.PredicateDictionary.SUPPLEMENTARY_CONTENT;
import static org.folio.marc4ld.util.BibframeUtil.getWork;
import static org.folio.marc4ld.util.Constants.TAG_008;

import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.mapper.MapperHelper;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Component;

@Component
public class SupplementaryContentMapper extends AbstractBookMapper {

  public static final Map<Character, String> CODE_TO_LINK_SUFFIX_MAP = Map.of(
    'b', "bibliography",
    'k', "discography",
    'q', "filmography",
    '1', "index"
  );

  private static final Set<Character> SUPPORTED_CODES = Set.of('b', 'k', 'q');

  public SupplementaryContentMapper(LabelService labelService,
                                    MapperHelper mapperHelper,
                                    FingerprintHashService hashService) {
    super(labelService, mapperHelper, hashService);
  }

  @Override
  protected int getStartIndex() {
    return 24;
  }

  @Override
  protected int getEndIndex() {
    return 28;
  }

  @Override
  protected boolean isSupportedCode(char code) {
    return SUPPORTED_CODES.contains(code);
  }

  @Override
  protected PredicateDictionary getPredicate() {
    return SUPPLEMENTARY_CONTENT;
  }

  @Override
  protected String getCategorySetLink() {
    return "http://id.loc.gov/vocabulary/msupplcont";
  }

  @Override
  protected String getCategorySetLabel() {
    return "Supplementary Content";
  }

  @Override
  protected String getLinkSuffix(char code) {
    return CODE_TO_LINK_SUFFIX_MAP.get(code);
  }

  @Override
  protected String getTerm(char code) {
    return getLinkSuffix(code);
  }

  @Override
  protected String getCode(char code) {
    return getTerm(code);
  }

  @Override
  public void map(Record marcRecord, Resource instance) {
    super.map(marcRecord, instance);
    marcRecord.getControlFields()
      .stream()
      .filter(controlField -> TAG_008.equals(controlField.getTag()))
      .map(ControlField::getData)
      .map(data -> data.charAt(31))
      .filter(c -> '1' == c)
      .findAny()
      .ifPresent(c -> getWork(instance)
        .ifPresent(work -> {
          var category = createCategory(
            getCode(c),
            getCategorySetLink() + "/" + getLinkSuffix(c),
            getTerm(c)
          );
          work.addOutgoingEdge(new ResourceEdge(work, category, getPredicate()));
        }));
  }
}
