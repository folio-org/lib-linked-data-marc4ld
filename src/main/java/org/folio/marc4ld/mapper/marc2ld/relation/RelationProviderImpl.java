package org.folio.marc4ld.mapper.marc2ld.relation;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.folio.ld.dictionary.PredicateDictionary.valueOf;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.dictionary.DictionaryProcessor;
import org.folio.marc4ld.model.Resource;
import org.folio.marc4ld.model.ResourceEdge;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RelationProviderImpl implements RelationProvider {

  private static final String CODE_TO_PREDICATE = "AGENT_CODE_TO_PREDICATE";
  private static final String TEXT_TO_PREDICATE = "AGENT_TEXT_TO_PREDICATE";

  private final DictionaryProcessor dictionaryProcessor;

  @Override
  public Optional<ResourceEdge> findRelation(Resource source, Resource target, DataField dataField,
                                             Marc4BibframeRules.FieldRule fieldRule) {
    return getPredicate(dataField, fieldRule).map(predicate -> new ResourceEdge(source, target, valueOf(predicate)));
  }

  private Optional<String> getPredicate(DataField dataField, Marc4BibframeRules.FieldRule fieldRule) {
    return Optional.ofNullable(dataField.getSubfield(fieldRule.getRelation().getCode()))
      .flatMap(codeSubfield -> dictionaryProcessor.getValue(CODE_TO_PREDICATE, codeSubfield.getData()))
      .or(() -> Optional.ofNullable(dataField.getSubfield(fieldRule.getRelation().getText()))
        .flatMap(textSubfield -> dictionaryProcessor.getValue(TEXT_TO_PREDICATE, adjust(textSubfield.getData()))));
  }

  private String adjust(String text) {
    return text.replaceAll("[^a-zA-Z]", EMPTY).toLowerCase();
  }
}
