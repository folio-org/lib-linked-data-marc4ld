package org.folio.marc4ld.service.ld2marc.mapper.datafield.hub;

import static org.folio.ld.dictionary.PropertyDictionary.ATTRIBUTION;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.NAME_ALTERNATIVE;
import static org.folio.ld.dictionary.PropertyDictionary.NUMERATION;
import static org.folio.ld.dictionary.PropertyDictionary.TITLES;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FAMILY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.B;
import static org.folio.marc4ld.util.Constants.C;
import static org.folio.marc4ld.util.Constants.D;
import static org.folio.marc4ld.util.Constants.J;
import static org.folio.marc4ld.util.Constants.Q;
import static org.folio.marc4ld.util.Constants.T;
import static org.folio.marc4ld.util.Constants.TAG_600;
import static org.folio.marc4ld.util.Constants.THREE;
import static org.folio.marc4ld.util.MarcUtil.addNonRepeatableSubfield;
import static org.folio.marc4ld.util.MarcUtil.addRepeatableSubfield;

import java.util.Comparator;
import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
public class Hub600Mapper extends Hub6xxMapper {
  private final MarcFactory marcFactory;

  public Hub600Mapper(MarcFactory marcFactory,
                      Comparator<Subfield> subfieldComparator,
                      HubCreatorComparator hubCreatorComparator) {
    super(marcFactory, subfieldComparator, hubCreatorComparator);
    this.marcFactory = marcFactory;
  }

  @Override
  protected String getTag() {
    return TAG_600;
  }

  @Override
  protected boolean isCreatorConditionMet(Resource hub) {
    return getHubCreator(hub)
      .filter(agent -> agent.isOfType(PERSON) || agent.isOfType(FAMILY))
      .isPresent();
  }

  @Override
  protected void setCreatorFields(Resource creator, DataField dataField) {
    if (creator.isOfType(FAMILY)) {
      dataField.setIndicator1(THREE);
    }
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
