package org.folio.marc4ld.configuration;

import java.util.function.Supplier;
import org.folio.marc4ld.service.ld2marc.processing.DataFieldPostProcessor;
import org.folio.marc4ld.service.ld2marc.processing.combine.impl.DataFieldCombinerFactoryImpl;
import org.folio.marc4ld.service.ld2marc.processing.impl.DataFieldPostProcessorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataFieldPostProcessorConfig {
  @Bean
  public Supplier<DataFieldPostProcessor> dataFieldPostProcessorSupplier() {
    return () -> new DataFieldPostProcessorImpl(new DataFieldCombinerFactoryImpl());
  }
}
