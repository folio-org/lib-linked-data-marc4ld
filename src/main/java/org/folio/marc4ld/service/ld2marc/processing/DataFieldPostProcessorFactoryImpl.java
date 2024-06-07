package org.folio.marc4ld.service.ld2marc.processing;

import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombinerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataFieldPostProcessorFactoryImpl implements DataFieldPostProcessorFactory {

  private final DataFieldCombinerFactory combinerFactory;

  @Override
  public DataFieldPostProcessor get() {
    return new DataFieldPostProcessorImpl(combinerFactory);
  }
}
