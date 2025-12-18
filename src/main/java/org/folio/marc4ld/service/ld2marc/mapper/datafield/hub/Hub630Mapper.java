package org.folio.marc4ld.service.ld2marc.mapper.datafield.hub;

import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PropertyDictionary.LEGAL_DATE;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.NON_SORT_NUM;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.D;
import static org.folio.marc4ld.util.Constants.N;
import static org.folio.marc4ld.util.Constants.P;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.TAG_630;
import static org.folio.marc4ld.util.MarcUtil.addNonRepeatableSubfield;
import static org.folio.marc4ld.util.MarcUtil.addRepeatableSubfield;
import static org.folio.marc4ld.util.MarcUtil.setInd1;

import java.util.Comparator;
import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
public class Hub630Mapper extends Hub6xxMapper {
  private final MarcFactory marcFactory;

  public Hub630Mapper(MarcFactory marcFactory, Comparator<Subfield> comparator) {
    super(marcFactory, comparator);
    this.marcFactory = marcFactory;
  }

  @Override
  protected boolean isCreatorConditionSatisfied(Resource concept) {
    return getOutgoingEdge(concept, FOCUS)
      .filter(focus -> focus.isOfType(HUB))
      .flatMap(hub -> getOutgoingEdge(hub, CREATOR))
      .isEmpty();
  }

  @Override
  protected void setFieldsFromTitle(Resource title, DataField dataField) {
    addNonRepeatableSubfield(title, MAIN_TITLE.getValue(), dataField, A, marcFactory);
    addRepeatableSubfield(title, PART_NUMBER.getValue(), dataField, N, marcFactory);
    addRepeatableSubfield(title, PART_NAME.getValue(), dataField, P, marcFactory);
    setInd1(title, NON_SORT_NUM.getValue(), dataField);
  }

  @Override
  protected void setFieldsFromConcept(Resource concept, DataField dataField) {
    super.setFieldsFromConcept(concept, dataField);
    addRepeatableSubfield(concept, LEGAL_DATE.getValue(), dataField, D, marcFactory);
  }

  @Override
  protected DataField createEmptyDatafield(Resource edge) {
    return marcFactory.newDataField(TAG_630, SPACE, SPACE);
  }
}
