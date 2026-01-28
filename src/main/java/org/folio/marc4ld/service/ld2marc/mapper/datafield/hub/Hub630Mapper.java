package org.folio.marc4ld.service.ld2marc.mapper.datafield.hub;

import static org.folio.ld.dictionary.PropertyDictionary.LEGAL_DATE;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.NON_SORT_NUM;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.D;
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

  public Hub630Mapper(MarcFactory marcFactory,
                      Comparator<Subfield> subfieldComparator,
                      HubCreatorComparator hubCreatorComparator) {
    super(marcFactory, subfieldComparator, hubCreatorComparator);
    this.marcFactory = marcFactory;
  }

  @Override
  protected String getTag() {
    return TAG_630;
  }

  @Override
  protected boolean isCreatorConditionMet(Resource hub) {
    return getHubCreator(hub).isEmpty();
  }

  @Override
  protected void setTitleFields(Resource title, DataField dataField) {
    super.setTitleFields(title, dataField);
    addNonRepeatableSubfield(title, MAIN_TITLE.getValue(), dataField, A, marcFactory);
    setInd1(title, NON_SORT_NUM.getValue(), dataField);
  }

  @Override
  protected void setConceptFields(Resource concept, DataField dataField) {
    super.setConceptFields(concept, dataField);
    addRepeatableSubfield(concept, LEGAL_DATE.getValue(), dataField, D, marcFactory);
  }
}
