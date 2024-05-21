package org.folio.marc4ld.service.marc2ld.relation;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
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

  private static final String CODE_TO_PREDICATE = "AGENT_CODE_TO_PREDICATE";
  private static final String TEXT_TO_PREDICATE = "AGENT_TEXT_TO_PREDICATE";

  private final DictionaryProcessor dictionaryProcessor;

  @Override
  public void checkRelation(Resource source,
                            Resource target,
                            DataField dataField,
                            Marc2ldFieldRuleApplier fieldRule) {
    fieldRule.getRelation()
      .ifPresent(relation -> getPredicates(relation, dataField)
        .stream()
        .map(PredicateDictionary::valueOf)
        .map(predicate -> new ResourceEdge(source, target, predicate))
        .forEach(source::addOutgoingEdge)
      );
  }

  private List<String> getPredicates(Relation relation, DataField dataField) {
    return Stream.of(getByCode(relation, dataField), getByText(relation, dataField))
      .flatMap(Optional::stream)
      .toList();
  }

  private Optional<String> getByCode(Relation relation, DataField dataField) {
    return relation.getCode()
      .map(dataField::getSubfield)
      .map(Subfield::getData)
      .flatMap(data -> dictionaryProcessor.getValue(CODE_TO_PREDICATE, data));
  }

  private Optional<String> getByText(Relation relation, DataField dataField) {
    return relation.getText()
      .map(dataField::getSubfield)
      .map(Subfield::getData)
      .map(this::adjust)
      .flatMap(data -> dictionaryProcessor.getValue(TEXT_TO_PREDICATE, data));
  }

  private String adjust(String text) {
    return text.replaceAll("[^a-zA-Z]", EMPTY)
      .toLowerCase();
  }
}
