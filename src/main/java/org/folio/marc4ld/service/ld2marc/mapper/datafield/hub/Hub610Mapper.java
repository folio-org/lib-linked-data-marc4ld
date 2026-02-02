package org.folio.marc4ld.service.ld2marc.mapper.datafield.hub;

import static org.folio.ld.dictionary.PropertyDictionary.AFFILIATION;
import static org.folio.ld.dictionary.PropertyDictionary.DATE;
import static org.folio.ld.dictionary.PropertyDictionary.MAIN_TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.MISC_INFO;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PLACE;
import static org.folio.ld.dictionary.PropertyDictionary.SUBORDINATE_UNIT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.JURISDICTION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.B;
import static org.folio.marc4ld.util.Constants.C;
import static org.folio.marc4ld.util.Constants.D;
import static org.folio.marc4ld.util.Constants.G;
import static org.folio.marc4ld.util.Constants.ONE;
import static org.folio.marc4ld.util.Constants.T;
import static org.folio.marc4ld.util.Constants.TAG_610;
import static org.folio.marc4ld.util.Constants.U;
import static org.folio.marc4ld.util.MarcUtil.addNonRepeatableSubfield;
import static org.folio.marc4ld.util.MarcUtil.addRepeatableSubfield;

import java.util.Comparator;
import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
public class Hub610Mapper extends Hub6xxMapper {
  private final MarcFactory marcFactory;

  public Hub610Mapper(MarcFactory marcFactory,
                      Comparator<Subfield> subfieldComparator,
                      HubCreatorComparator hubCreatorComparator) {
    super(marcFactory, subfieldComparator, hubCreatorComparator);
    this.marcFactory = marcFactory;
  }

  @Override
  protected String getTag() {
    return TAG_610;
  }

  @Override
  protected boolean isCreatorConditionMet(Resource hub) {
    return getHubCreator(hub)
      .filter(agent -> agent.isOfType(JURISDICTION) || agent.isOfType(ORGANIZATION))
      .isPresent();
  }

  @Override
  protected void setTitleFields(Resource title, DataField dataField) {
    super.setTitleFields(title, dataField);
    addNonRepeatableSubfield(title, MAIN_TITLE.getValue(), dataField, T, marcFactory);
  }

  @Override
  protected void setCreatorFields(Resource creator, DataField dataField) {
    if (creator.isOfType(JURISDICTION)) {
      dataField.setIndicator1(ONE);
    }
    addNonRepeatableSubfield(creator, NAME.getValue(), dataField, A, marcFactory);
    addRepeatableSubfield(creator, SUBORDINATE_UNIT.getValue(), dataField, B, marcFactory);
    addRepeatableSubfield(creator, PLACE.getValue(), dataField, C, marcFactory);
    addRepeatableSubfield(creator, DATE.getValue(), dataField, D, marcFactory);
    addRepeatableSubfield(creator, MISC_INFO.getValue(), dataField, G, marcFactory);
    addNonRepeatableSubfield(creator, AFFILIATION.getValue(), dataField, U, marcFactory);
  }
}
