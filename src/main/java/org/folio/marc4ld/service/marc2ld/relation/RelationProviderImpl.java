package org.folio.marc4ld.service.marc2ld.relation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.dictionary.specific.RoleDictionary;
import org.folio.ld.dictionary.specific.RoleLabelDictionary;
import org.folio.marc4ld.service.marc2ld.Marc2ldFieldRuleApplier;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Service;

@Service
public class RelationProviderImpl implements RelationProvider {

  @Override
  public void checkRelation(Resource source,
                            Resource target,
                            DataField dataField,
                            Marc2ldFieldRuleApplier fieldRule) {
    fieldRule.getRelation()
      .ifPresent(relation -> getPredicates(relation, dataField)
        .stream()
        .map(predicate -> new ResourceEdge(source, target, predicate))
        .forEach(source::addOutgoingEdge)
      );
  }

  private List<PredicateDictionary> getPredicates(Relation relation, DataField dataField) {
    return Stream.of(getByCode(relation, dataField), getByText(relation, dataField))
      .flatMap(Optional::stream)
      .toList();
  }

  private Optional<PredicateDictionary> getByCode(Relation relation, DataField dataField) {
    return relation.getCode()
      .map(dataField::getSubfield)
      .map(Subfield::getData)
      .flatMap(RoleDictionary::getValue);
  }

  private Optional<PredicateDictionary> getByText(Relation relation, DataField dataField) {
    return relation.getText()
      .map(dataField::getSubfield)
      .map(Subfield::getData)
      .flatMap(RoleLabelDictionary::getValue);
  }
}
