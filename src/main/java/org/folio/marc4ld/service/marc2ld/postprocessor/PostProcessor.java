package org.folio.marc4ld.service.marc2ld.postprocessor;

import java.util.function.BiConsumer;
import org.folio.ld.dictionary.model.Resource;

public interface PostProcessor extends BiConsumer<Resource, org.marc4j.marc.Record> {
}
