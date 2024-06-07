package org.folio.marc4ld.service.ld2marc.processing.combine;

public interface DataFieldCombinerFactory {

  DataFieldCombiner create(DataFieldCombiner.Context context);
}
