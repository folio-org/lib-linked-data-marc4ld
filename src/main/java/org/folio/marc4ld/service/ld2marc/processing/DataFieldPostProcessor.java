package org.folio.marc4ld.service.ld2marc.processing;

import java.util.Collection;
import java.util.function.UnaryOperator;
import org.marc4j.marc.DataField;

public interface DataFieldPostProcessor extends UnaryOperator<Collection<DataField>> {
}
