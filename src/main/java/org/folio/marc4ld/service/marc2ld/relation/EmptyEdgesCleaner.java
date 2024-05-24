package org.folio.marc4ld.service.marc2ld.relation;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.folio.ld.dictionary.model.Resource;

public interface EmptyEdgesCleaner extends UnaryOperator<Resource>, Consumer<Resource> {
}
