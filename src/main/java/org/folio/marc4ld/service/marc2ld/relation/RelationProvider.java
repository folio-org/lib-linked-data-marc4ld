package org.folio.marc4ld.service.marc2ld.relation;

import java.util.Optional;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.marc4j.marc.DataField;

public interface RelationProvider {

  Optional<ResourceEdge> findRelation(Resource source, Resource target, DataField dataField,
                                      Marc4BibframeRules.FieldRule fieldRule);
}
