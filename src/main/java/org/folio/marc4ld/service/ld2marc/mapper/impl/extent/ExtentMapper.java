package org.folio.marc4ld.service.ld2marc.mapper.impl.extent;

import static org.folio.ld.dictionary.PredicateDictionary.EXTENT;
import static org.folio.ld.dictionary.PropertyDictionary.ACCOMPANYING_MATERIAL;
import static org.folio.ld.dictionary.PropertyDictionary.DIMENSIONS;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.MATERIALS_SPECIFIED;
import static org.folio.ld.dictionary.PropertyDictionary.PHYSICAL_DESCRIPTION;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.B;
import static org.folio.marc4ld.util.Constants.C;
import static org.folio.marc4ld.util.Constants.E;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.TAG_300;
import static org.folio.marc4ld.util.Constants.THREE;
import static org.folio.marc4ld.util.LdUtil.isInstance;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.mapper.Ld2MarcMapper;
import org.folio.marc4ld.util.MarcUtil;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExtentMapper implements Ld2MarcMapper {

  private final MarcFactory marcFactory;

  @Override
  public boolean test(ResourceEdge resourceEdge) {
    return isInstance(resourceEdge.getSource()) && hasExtentRelation(resourceEdge);
  }

  private boolean hasExtentRelation(ResourceEdge edge) {
    return EXTENT.equals(edge.getPredicate())
      && Optional.of(edge.getTarget())
      .map(Resource::getTypes).filter(types -> types.contains(ResourceTypeDictionary.EXTENT))
      .isPresent();
  }

  @Override
  public DataField apply(ResourceEdge resourceEdge) {
    var extent = resourceEdge.getTarget();
    var dataField = marcFactory.newDataField(TAG_300, SPACE, SPACE);
    addLabels(extent, dataField);
    var instance = resourceEdge.getSource();
    addPhysicalDescription(instance, dataField);
    addDimensions(instance, dataField);
    addAccompanyingMaterial(instance, dataField);
    addMaterialSpecified(extent, dataField);
    return dataField;
  }

  private void addLabels(Resource extent, DataField dataField) {
    addRepeatableSubfield(extent, LABEL.getValue(), dataField, A);
  }

  private void addPhysicalDescription(Resource instance, DataField dataField) {
    addNonRepeatableSubfield(instance, PHYSICAL_DESCRIPTION.getValue(), dataField, B);
  }

  private void addDimensions(Resource instance, DataField dataField) {
    addRepeatableSubfield(instance, DIMENSIONS.getValue(), dataField, C);
  }

  private void addAccompanyingMaterial(Resource instance, DataField dataField) {
    addNonRepeatableSubfield(instance, ACCOMPANYING_MATERIAL.getValue(), dataField, E);
  }

  private void addMaterialSpecified(Resource extent, DataField dataField) {
    addNonRepeatableSubfield(extent, MATERIALS_SPECIFIED.getValue(), dataField, THREE);
  }

  private void addNonRepeatableSubfield(Resource resource, String property, DataField dataField, char subfield) {
    MarcUtil.addNonRepeatableSubfield(resource, property, dataField, subfield, marcFactory);
  }

  private void addRepeatableSubfield(Resource resource, String property, DataField dataField, char subfield) {
    MarcUtil.addRepeatableSubfield(resource, property, dataField, subfield, marcFactory);
  }
}
