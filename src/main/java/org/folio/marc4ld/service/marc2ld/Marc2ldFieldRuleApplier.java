package org.folio.marc4ld.service.marc2ld;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.service.marc2ld.relation.Relation;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

public interface Marc2ldFieldRuleApplier {

  Marc4LdRules.FieldRule getOriginal();

  Resource computeParentIfAbsent(Resource resource);

  Collection<Marc2ldFieldRuleApplier> getEdgeRules();

  Resource createResource();

  Optional<Resource> selectResourceFromEdges(Resource resource);

  boolean isAppend();

  Optional<Relation> getRelation();

  Collection<Map<String, List<String>>> createProperties(DataField dataField, Collection<ControlField> controlFields);

  Map<String, List<String>> mergeProperties(DataField dataField, Collection<ControlField> controlFields);

  Collection<ResourceTypeDictionary> getTypes();

  PredicateDictionary getPredicate();
}
