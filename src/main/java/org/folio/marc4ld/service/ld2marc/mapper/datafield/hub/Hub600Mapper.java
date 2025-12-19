package org.folio.marc4ld.service.ld2marc.mapper.datafield.hub;

import static org.folio.ld.dictionary.PredicateDictionary.CREATOR;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PropertyDictionary.ATTRIBUTION;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.NAME_ALTERNATIVE;
import static org.folio.ld.dictionary.PropertyDictionary.NUMERATION;
import static org.folio.ld.dictionary.PropertyDictionary.TITLES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FAMILY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.B;
import static org.folio.marc4ld.util.Constants.C;
import static org.folio.marc4ld.util.Constants.D;
import static org.folio.marc4ld.util.Constants.J;
import static org.folio.marc4ld.util.Constants.Q;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.T;
import static org.folio.marc4ld.util.Constants.TAG_600;
import static org.folio.marc4ld.util.Constants.THREE;
import static org.folio.marc4ld.util.MarcUtil.addNonRepeatableSubfield;
import static org.folio.marc4ld.util.MarcUtil.addRepeatableSubfield;

import java.util.Comparator;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
public class Hub600Mapper extends Hub6xxMapper {
  private final MarcFactory marcFactory;

  public Hub600Mapper(MarcFactory marcFactory, Comparator<Subfield> comparator) {
    super(marcFactory, comparator);
    this.marcFactory = marcFactory;
  }

  @Override
  protected DataField createEmptyDatafield(Resource concept) {
    var ind1 = isCreatorOfType(concept, FAMILY) ? THREE : SPACE;
    return marcFactory.newDataField(TAG_600, ind1, SPACE);
  }

  @Override
  protected boolean isCreatorConditionSatisfied(Resource concept) {
    return isCreatorOfType(concept, PERSON) || isCreatorOfType(concept, FAMILY);
  }

  protected boolean isCreatorOfType(Resource concept, ResourceTypeDictionary creatorType) {
    return getOutgoingEdge(concept, FOCUS)
      .filter(focus -> focus.isOfType(HUB))
      .flatMap(hub -> getOutgoingEdge(hub, CREATOR))
      .filter(creator -> creator.isOfType(creatorType))
      .isPresent();
  }

  @Override
  protected void setCreatorFields(Resource creator, DataField dataField) {
    addNonRepeatableSubfield(creator, NAME.getValue(), dataField, A, marcFactory);
    addNonRepeatableSubfield(creator, NUMERATION.getValue(), dataField, B, marcFactory);
    addRepeatableSubfield(creator, TITLES.getValue(), dataField, C, marcFactory);
    addNonRepeatableSubfield(creator, DATE.getValue(), dataField, D, marcFactory);
    addRepeatableSubfield(creator, ATTRIBUTION.getValue(), dataField, J, marcFactory);
    addNonRepeatableSubfield(creator, NAME_ALTERNATIVE.getValue(), dataField, Q, marcFactory);
  }

  @Override
  protected void setTitleFields(Resource title, DataField dataField) {
    super.setTitleFields(title, dataField);
    addNonRepeatableSubfield(title, MAIN_TITLE.getValue(), dataField, T, marcFactory);
  }
}
