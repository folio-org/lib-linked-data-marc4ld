package org.folio.marc4ld.service.marc2ld.normalization;

import java.util.List;
import java.util.Map;
import org.folio.marc4ld.configuration.property.Marc2LdNormalizationRules;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Component;

@Component
public class MarcAuthorityPunctuationNormalizerImpl extends AbstractMarcPunctuationNormalizer {

  public MarcAuthorityPunctuationNormalizerImpl(Marc2LdNormalizationRules marc2LdNormalizationRules) {
    super(marc2LdNormalizationRules);
  }

  @Override
  protected Map<String, List<String>> getSubfieldRules() {
    return marc2LdNormalizationRules.getAuthoritySubfieldRules();
  }

  @Override
  protected Map<String, List<String>> getLastSubfieldRules() {
    return Map.of();
  }

  @Override
  protected void normalizeLastSubfield(DataField dataField) {
    var subfields = dataField.getSubfields();
    var lastSubfield = subfields.get(subfields.size() - 1);
    var lookups = generateLookups(dataField.getTag(), lastSubfield.getCode());
    lookups.stream().filter(lookup -> marc2LdNormalizationRules.getParenthesesAndBracketsRules().contains(lookup))
      .findAny()
      .ifPresent(lookup -> lastSubfield.setData(removeParenthesesAndSquareBrackets(lastSubfield.getData())));
  }
}
