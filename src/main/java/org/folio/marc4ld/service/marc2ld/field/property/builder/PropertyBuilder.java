package org.folio.marc4ld.service.marc2ld.field.property.builder;

import java.util.Collection;
import java.util.function.Function;
import org.folio.marc4ld.service.marc2ld.field.property.Property;

public interface PropertyBuilder<T> extends Function<T, Collection<Property>> {
}
