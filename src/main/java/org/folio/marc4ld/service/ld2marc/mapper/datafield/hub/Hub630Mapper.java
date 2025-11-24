package org.folio.marc4ld.service.ld2marc.mapper.datafield.hub;

import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PropertyDictionary.CHRONOLOGICAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.FORM_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GENERAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.LANGUAGE;
import static org.folio.ld.dictionary.PropertyDictionary.LEGAL_DATE;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.MUSIC_KEY;
import static org.folio.ld.dictionary.PropertyDictionary.NON_SORT_NUM;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.VERSION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.D;
import static org.folio.marc4ld.util.Constants.F;
import static org.folio.marc4ld.util.Constants.L;
import static org.folio.marc4ld.util.Constants.N;
import static org.folio.marc4ld.util.Constants.P;
import static org.folio.marc4ld.util.Constants.R;
import static org.folio.marc4ld.util.Constants.S;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.TAG_630;
import static org.folio.marc4ld.util.Constants.V;
import static org.folio.marc4ld.util.Constants.X;
import static org.folio.marc4ld.util.Constants.Y;
import static org.folio.marc4ld.util.Constants.Z;
import static org.folio.marc4ld.util.MarcUtil.addNonRepeatableSubfield;
import static org.folio.marc4ld.util.MarcUtil.addRepeatableSubfield;
import static org.folio.marc4ld.util.MarcUtil.isSubfieldPresent;
import static org.folio.marc4ld.util.MarcUtil.orderSubfields;
import static org.folio.marc4ld.util.MarcUtil.setInd1;

import java.util.Comparator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.mapper.CustomDataFieldsMapper;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Hub630Mapper implements CustomDataFieldsMapper {
  private final MarcFactory marcFactory;
  private final Comparator<Subfield> comparator;

  @Override
  public boolean test(ResourceEdge edge) {
    return edge.getSource() != null
      && edge.getSource().isOfType(WORK)
      && edge.getPredicate().equals(SUBJECT)
      && edge.getTarget().isOfType(HUB)
      && isHubWithNoCreator(edge);
  }

  @Override
  public DataField apply(ResourceEdge edge) {
    var concept = edge.getTarget();
    var dataField = marcFactory.newDataField(TAG_630, SPACE, SPACE);
    getOutgoingEdge(concept, FOCUS)
      .ifPresent(hub -> {
        setFieldsFromTitle(hub, dataField);
        setLanguageField(hub, dataField);
        setFieldsFromConcept(concept, dataField);
        orderSubfields(dataField, comparator);
      });
    return dataField;
  }

  private boolean isHubWithNoCreator(ResourceEdge edgeToConcept) {
    return getOutgoingEdge(edgeToConcept.getTarget(), FOCUS)
      .filter(focus -> focus.isOfType(HUB))
      .flatMap(hub -> getOutgoingEdge(hub, CREATOR))
      .isEmpty();
  }

  private void setFieldsFromTitle(Resource hub, DataField dataField) {
    getOutgoingEdge(hub, PredicateDictionary.TITLE)
      .ifPresent(title -> {
        addNonRepeatableSubfield(title, MAIN_TITLE.getValue(), dataField, A, marcFactory);
        addRepeatableSubfield(title, PART_NUMBER.getValue(), dataField, N, marcFactory);
        addRepeatableSubfield(title, PART_NAME.getValue(), dataField, P, marcFactory);
        setInd1(title, NON_SORT_NUM.getValue(), dataField);
      });
  }

  private void setFieldsFromConcept(Resource concept, DataField dataField) {
    addRepeatableSubfield(concept, LEGAL_DATE.getValue(), dataField, D, marcFactory);
    addNonRepeatableSubfield(concept, DATE.getValue(), dataField, F, marcFactory);
    addNonRepeatableSubfield(concept, MUSIC_KEY.getValue(), dataField, R, marcFactory);
    addRepeatableSubfield(concept, VERSION.getValue(), dataField, S, marcFactory);
    addRepeatableSubfield(concept, FORM_SUBDIVISION.getValue(), dataField, V, marcFactory);
    addRepeatableSubfield(concept, GENERAL_SUBDIVISION.getValue(), dataField, X, marcFactory);
    addRepeatableSubfield(concept, CHRONOLOGICAL_SUBDIVISION.getValue(), dataField, Y, marcFactory);
    addRepeatableSubfield(concept, GEOGRAPHIC_SUBDIVISION.getValue(), dataField, Z, marcFactory);
  }

  private void setLanguageField(Resource hub, DataField dataField) {
    addNonRepeatableSubfield(hub, LANGUAGE.getValue(), dataField, L, marcFactory);
    if (!isSubfieldPresent(L, dataField)) {
      getOutgoingEdge(hub, PredicateDictionary.LANGUAGE)
        .ifPresent(language -> addNonRepeatableSubfield(language, CODE.getValue(), dataField, L, marcFactory));
    }
  }

  private static Optional<Resource> getOutgoingEdge(Resource concept, PredicateDictionary predicate) {
    return concept.getOutgoingEdges().stream()
      .filter(e -> e.getPredicate().equals(predicate))
      .map(ResourceEdge::getTarget)
      .findFirst();
  }
}
