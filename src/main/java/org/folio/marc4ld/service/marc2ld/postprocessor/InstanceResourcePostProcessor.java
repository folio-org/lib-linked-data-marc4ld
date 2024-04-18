package org.folio.marc4ld.service.marc2ld.postprocessor;

import java.util.function.UnaryOperator;
import org.folio.ld.dictionary.model.Resource;

public interface InstanceResourcePostProcessor extends UnaryOperator<Resource> {
}
