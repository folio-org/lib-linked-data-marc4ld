package org.folio.marc4ld.service.marc2ld.field.property;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.marc4ld.service.marc2ld.field.property.builder.PropertyBuilder;
import org.folio.marc4ld.service.marc2ld.field.property.merger.PropertyMerger;
import org.folio.marc4ld.service.marc2ld.field.property.transformer.PropertyTransformer;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

@Builder
public class PropertyRuleImpl implements PropertyRule {

  @NonNull
  private final PropertyTransformer propertyTransformer;
  @NonNull
  private final PropertyMerger propertyMerger;
  @NonNull
  private final PropertyMerger constantMerger;
  @NonNull
  private final Collection<PropertyBuilder<DataField>> subFieldBuilders;
  @NonNull
  private final Collection<PropertyBuilder<DataField>> indicatorBuilders;
  @NonNull
  private final Collection<PropertyBuilder<Collection<ControlField>>> controlFieldBuilders;
  @NonNull
  private final Collection<Property> constants;

  @Override
  public Collection<Map<String, List<String>>> create(DataField dataField, Collection<ControlField> controlFields) {
    var values = this.merge(dataField, controlFields, new HashMap<>());
    return propertyTransformer.apply(values);
  }

  @Override
  public Map<String, List<String>> merge(
    DataField dataField,
    Collection<ControlField> fields,
    Map<String, List<String>> values
  ) {
    var properties = Stream.of(
        this.getDataFieldProperties(dataField),
        this.getControlFieldProperties(fields)
      )
      .flatMap(Collection::stream)
      .toList();
    properties.forEach(property -> propertyMerger.merge(values, property));
    constants.forEach(property -> constantMerger.merge(values, property));
    return values;
  }

  private Collection<Property> getDataFieldProperties(DataField dataField) {
    return Stream.of(
        subFieldBuilders,
        indicatorBuilders
      )
      .flatMap(Collection::stream)
      .map(propertyBuilder -> propertyBuilder.apply(dataField))
      .flatMap(Collection::stream)
      .toList();
  }

  private Collection<Property> getControlFieldProperties(Collection<ControlField> controlFields) {
    if (CollectionUtils.isEmpty(controlFields)) {
      return Collections.emptyList();
    }
    return controlFieldBuilders
      .stream()
      .map(propertyBuilder -> propertyBuilder.apply(controlFields))
      .flatMap(Collection::stream)
      .toList();
  }
}
