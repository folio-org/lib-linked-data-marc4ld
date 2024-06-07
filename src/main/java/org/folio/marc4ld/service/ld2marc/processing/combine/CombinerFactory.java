package org.folio.marc4ld.service.ld2marc.processing.combine;

import java.util.function.Predicate;
import java.util.function.Supplier;

public interface CombinerFactory extends Predicate<DataFieldCombiner.Context>, Supplier<DataFieldCombiner> {

  String getTag();

}
