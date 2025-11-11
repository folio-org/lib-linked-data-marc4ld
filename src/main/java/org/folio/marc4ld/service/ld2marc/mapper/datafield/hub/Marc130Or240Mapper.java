package org.folio.marc4ld.service.ld2marc.mapper.datafield.hub;

import static org.folio.ld.dictionary.PredicateDictionary.EXPRESSION_OF;
import static org.folio.ld.dictionary.PredicateDictionary.LANGUAGE;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.NON_SORT_NUM;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NAME;
import static org.folio.ld.dictionary.PropertyDictionary.PART_NUMBER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.HUB;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;
import static org.folio.marc4ld.util.Constants.L;
import static org.folio.marc4ld.util.Constants.N;
import static org.folio.marc4ld.util.Constants.P;
import static org.folio.marc4ld.util.Constants.TAG_130;
import static org.folio.marc4ld.util.Constants.TAG_240;
import static org.folio.marc4ld.util.LdUtil.getOutgoingEdges;
import static org.folio.marc4ld.util.LdUtil.getPropertyValue;
import static org.folio.marc4ld.util.MarcUtil.isSubfieldPresent;
import static org.folio.marc4ld.util.MarcUtil.orderSubfields;

import java.util.Comparator;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.mapper.AdditionalDataFieldsMapper;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Marc130Or240Mapper implements AdditionalDataFieldsMapper {
  private final MarcFactory marcFactory;
  private final Comparator<Subfield> subfieldComparator;

  @Override
  public boolean test(ResourceEdge edge) {
    return edge.getSource() != null
      && edge.getSource().isOfType(WORK)
      && edge.getPredicate() == EXPRESSION_OF
      && edge.getTarget().isOfType(HUB);
  }

  @Override
  public DataField apply(ResourceEdge edge, DataField dataField) {
    if (!(dataField.getTag().equals(TAG_240) || dataField.getTag().equals(TAG_130))) {
      return dataField;
    }
    processEdge(edge.getTarget(), TITLE, title -> updateDataFieldWithTitleProps(dataField, title));
    if (!isSubfieldPresent(L, dataField)) {
      processEdge(edge.getTarget(), LANGUAGE, lang -> setDataFieldValue(lang, CODE, dataField, L));
    }
    orderSubfields(dataField, subfieldComparator);
    return dataField;
  }

  private void processEdge(Resource resource, PredicateDictionary predicate, Consumer<Resource> processor) {
    getOutgoingEdges(resource, predicate).stream()
      .findFirst()
      .map(ResourceEdge::getTarget)
      .ifPresent(processor);
  }

  private void updateDataFieldWithTitleProps(DataField dataField, Resource titleResource) {
    getPropertyValue(titleResource, NON_SORT_NUM.getValue())
      .ifPresent(val -> setIndicator(dataField, val));
    setDataFieldValue(titleResource, PART_NAME, dataField, P);
    setDataFieldValue(titleResource, PART_NUMBER, dataField, N);
  }

  private void setDataFieldValue(Resource resource, PropertyDictionary property, DataField dataField, char subfield) {
    getPropertyValue(resource, property.getValue())
      .ifPresent(val -> dataField.addSubfield(marcFactory.newSubfield(subfield, val)));
  }

  private void setIndicator(DataField dataField, String val) {
    if (val.length() != 1) {
      return;
    }
    if (TAG_130.equals(dataField.getTag())) {
      dataField.setIndicator1(val.charAt(0));
    } else {
      dataField.setIndicator2(val.charAt(0));
    }
  }
}
