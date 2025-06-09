package org.folio.marc4ld.service.marc2ld.relation;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.folio.marc4ld.util.Constants.Dictionary.AGENT_TEXT_TO_PREDICATE;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.RoleDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.Marc2ldFieldRuleApplier;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RelationProviderImpl implements RelationProvider {

  private final DictionaryProcessor dictionaryProcessor;

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
      .map(RoleDictionary::getRole);
  }

  private Optional<PredicateDictionary> getByText(Relation relation, DataField dataField) {
    return relation.getText()
      .map(dataField::getSubfield)
      .map(Subfield::getData)
      .map(this::adjust)
      .flatMap(data -> dictionaryProcessor.getValue(AGENT_TEXT_TO_PREDICATE, data))
      .map(PredicateDictionary::valueOf);
  }

  private String adjust(String text) {
    return text.replaceAll("[^a-zA-Z]", EMPTY)
      .toLowerCase();
  }
}
