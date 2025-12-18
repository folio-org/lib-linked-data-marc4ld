package org.folio.marc4ld.service.ld2marc.mapper.datafield.hub;

import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.SUBJECT;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.CHRONOLOGICAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.FORM_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GENERAL_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.GEOGRAPHIC_SUBDIVISION;
import static org.folio.ld.dictionary.PropertyDictionary.LANGUAGE;
import static org.folio.ld.dictionary.PropertyDictionary.MUSIC_KEY;
import static org.folio.ld.dictionary.PropertyDictionary.VERSION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.util.Constants.F;
import static org.folio.marc4ld.util.Constants.L;
import static org.folio.marc4ld.util.Constants.R;
import static org.folio.marc4ld.util.Constants.S;
import static org.folio.marc4ld.util.Constants.V;
import static org.folio.marc4ld.util.Constants.X;
import static org.folio.marc4ld.util.Constants.Y;
import static org.folio.marc4ld.util.Constants.Z;
import static org.folio.marc4ld.util.MarcUtil.addNonRepeatableSubfield;
import static org.folio.marc4ld.util.MarcUtil.addRepeatableSubfield;
import static org.folio.marc4ld.util.MarcUtil.orderSubfields;

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

@RequiredArgsConstructor
public abstract class Hub6xxMapper implements CustomDataFieldsMapper {
  private final MarcFactory marcFactory;
  private final Comparator<Subfield> comparator;

  protected abstract DataField createEmptyDatafield(Resource concept);

  protected abstract boolean isCreatorConditionSatisfied(Resource concept);

  @Override
  public DataField apply(ResourceEdge edge) {
    var concept = edge.getTarget();
    var dataField = createEmptyDatafield(edge.getTarget());
    getOutgoingEdge(concept, FOCUS)
      .ifPresent(hub -> {
        setLanguageField(hub, dataField);
        getOutgoingEdge(hub, TITLE).ifPresent(title -> setFieldsFromTitle(title, dataField));
        getOutgoingEdge(hub, CREATOR).ifPresent(creator -> setFieldsFromCreator(creator, dataField));
        setFieldsFromConcept(concept, dataField);
        orderSubfields(dataField, comparator);
      });
    return dataField;
  }

  @Override
  public boolean test(ResourceEdge edge) {
    return edge.getSource() != null
      && edge.getSource().isOfType(WORK)
      && edge.getPredicate().equals(SUBJECT)
      && edge.getTarget().isOfType(HUB)
      && edge.getTarget().isOfType(CONCEPT)
      && isCreatorConditionSatisfied(edge.getTarget());
  }

  protected Optional<Resource> getOutgoingEdge(Resource concept, PredicateDictionary predicate) {
    return concept.getOutgoingEdges().stream()
      .filter(e -> e.getPredicate().equals(predicate))
      .map(ResourceEdge::getTarget)
      .findFirst();
  }

  protected void setFieldsFromTitle(Resource title, DataField dataField) {
  }

  protected void setFieldsFromConcept(Resource concept, DataField dataField) {
    addNonRepeatableSubfield(concept, DATE.getValue(), dataField, F, marcFactory);
    addNonRepeatableSubfield(concept, MUSIC_KEY.getValue(), dataField, R, marcFactory);
    addRepeatableSubfield(concept, VERSION.getValue(), dataField, S, marcFactory);
    addRepeatableSubfield(concept, FORM_SUBDIVISION.getValue(), dataField, V, marcFactory);
    addRepeatableSubfield(concept, GENERAL_SUBDIVISION.getValue(), dataField, X, marcFactory);
    addRepeatableSubfield(concept, CHRONOLOGICAL_SUBDIVISION.getValue(), dataField, Y, marcFactory);
    addRepeatableSubfield(concept, GEOGRAPHIC_SUBDIVISION.getValue(), dataField, Z, marcFactory);
  }

  protected void setFieldsFromCreator(Resource creator, DataField dataField) {
  }

  private void setLanguageField(Resource hub, DataField dataField) {
    addNonRepeatableSubfield(hub, LANGUAGE.getValue(), dataField, L, marcFactory);
    getOutgoingEdge(hub, PredicateDictionary.LANGUAGE)
      .ifPresent(language -> addNonRepeatableSubfield(language, CODE.getValue(), dataField, L, marcFactory));
  }
}
