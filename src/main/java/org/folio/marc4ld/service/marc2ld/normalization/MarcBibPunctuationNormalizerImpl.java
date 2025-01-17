package org.folio.marc4ld.service.marc2ld.normalization;

import java.util.List;
import java.util.Map;
import org.folio.marc4ld.configuration.property.Marc2LdNormalizationRules;
import org.springframework.stereotype.Component;

@Component
public class MarcBibPunctuationNormalizerImpl extends AbstractMarcPunctuationNormalizer {

  public MarcBibPunctuationNormalizerImpl(Marc2LdNormalizationRules marc2LdNormalizationRules) {
    super(marc2LdNormalizationRules);
  }

  @Override
  protected Map<String, List<String>> getSubfieldRules() {
    return marc2LdNormalizationRules.getBibSubfieldRules();
  }

  @Override
  protected Map<String, List<String>> getLastSubfieldRules() {
    return marc2LdNormalizationRules.getBibLastSubfieldRules();
  }
}
