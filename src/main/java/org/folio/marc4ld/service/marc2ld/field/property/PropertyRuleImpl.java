package org.folio.marc4ld.service.marc2ld.field.property;

import static java.util.Objects.nonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.marc4ld.service.marc2ld.field.property.builder.PropertyBuilder;
import org.folio.marc4ld.service.marc2ld.field.property.merger.PropertyMerger;
import org.folio.marc4ld.service.marc2ld.field.property.transformer.PropertyTransformer;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;
import org.springframework.lang.Nullable;

@Builder
public class PropertyRuleImpl implements PropertyRule {

  @NonNull
  private final PropertyTransformer propertyTransformer;
  @NonNull
  private final PropertyMerger propertyMerger;
  @NonNull
  private final PropertyMerger constantMerger;
  @NonNull
  private final Map<Character, List<PropertyBuilder<DataField>>> subFieldBuilders;
  @Nullable
  private final PropertyBuilder<DataField> marcKeyBuilder;
  @NonNull
  private final Collection<PropertyBuilder<DataField>> indicatorBuilders;
  @NonNull
  private final Collection<PropertyBuilder<Collection<ControlField>>> controlFieldBuilders;
  @NonNull
  private final Collection<Property> constants;

  @Override
  public Collection<Map<String, List<String>>> create(DataField dataField, Collection<ControlField> controlFields) {
    var values = this.merge(dataField, controlFields);
    return propertyTransformer.apply(values);
  }

  private Map<String, List<String>> merge(
    DataField dataField,
    Collection<ControlField> fields
  ) {
    var properties = Stream.of(
        this.getDataFieldProperties(dataField),
        this.getControlFieldProperties(fields)
      )
      .flatMap(Collection::stream)
      .toList();
    var values = new HashMap<String, List<String>>();
    properties.forEach(property -> propertyMerger.merge(values, property));
    constants.forEach(property -> constantMerger.merge(values, property));
    return values;
  }

  private Collection<Property> getDataFieldProperties(DataField dataField) {
    var subfieldBuildersOrdered = getSubfieldBuildersInOrder(dataField);
    var marcKeyBuilders = nonNull(marcKeyBuilder) ? List.of(marcKeyBuilder) : List.<PropertyBuilder<DataField>>of();
    return Stream.of(
        marcKeyBuilders,
        subfieldBuildersOrdered,
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

  private List<PropertyBuilder<DataField>> getSubfieldBuildersInOrder(DataField dataField) {
    return dataField
      .getSubfields()
      .stream()
      .map(Subfield::getCode)
      .distinct()
      .map(subFieldBuilders::get)
      .filter(Objects::nonNull)
      .flatMap(Collection::stream)
      .toList();
  }
}
