package org.folio.marc2ld.mapper.field.relation;

import java.util.Optional;
import org.folio.marc2ld.configuration.property.Marc2BibframeRules;
import org.folio.marc2ld.model.Resource;
import org.folio.marc2ld.model.ResourceEdge;
import org.marc4j.marc.DataField;

public interface RelationProvider {

  Optional<ResourceEdge> findRelation(Resource source, Resource target, DataField dataField,
                                      Marc2BibframeRules.FieldRule fieldRule);
}
