package org.folio.marc4ld.service.ld2marc.postprocessor;

import static org.folio.marc4ld.util.Constants.TAG_130;
import static org.folio.marc4ld.util.Constants.TAG_730;

import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Component;

@Component
public class Marc130To730Normalizer implements Ld2MarcPostProcessor {
  @Override
  public void postProcess(Resource instance, Record generatedMarc) {
    generatedMarc.getDataFields().stream()
      .filter(f -> TAG_130.equals(f.getTag()))
      .skip(1)
      .forEach(f -> f.setTag(TAG_730));
  }
}
