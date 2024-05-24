package org.folio.marc4ld.service.ld2marc.mapper.impl.classification;

import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.ITEM_NUMBER;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.B;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.mapper.Ld2MarcMapper;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;

@RequiredArgsConstructor
public abstract class AbstractClassificationMapper implements Ld2MarcMapper {

  protected final ObjectMapper objectMapper;
  protected final MarcFactory marcFactory;

  protected abstract Set<ResourceTypeDictionary> getSupportedTypes();

  protected abstract String getTag();

  protected abstract char getIndicator1(Resource resource);

  protected abstract char getIndicator2(Resource resource);


  @Override
  public boolean canMap(PredicateDictionary predicate, Resource resource) {
    return predicate == PredicateDictionary.CLASSIFICATION
      && Objects.equals(resource.getTypes(), getSupportedTypes());
  }

  @Override
  public DataField map(Resource resource) {
    var dataField = marcFactory.newDataField(getTag(), getIndicator1(resource), getIndicator2(resource));
    getPropertyValues(resource, CODE.getValue()).ifPresent(codes -> codes
      .forEach(code -> dataField.addSubfield(marcFactory.newSubfield(A, code))));
    getPropertyValue(resource, ITEM_NUMBER.getValue())
      .ifPresent(itemNumber -> dataField.addSubfield(marcFactory.newSubfield(B, itemNumber)));
    return dataField;
  }

  protected Optional<String> getPropertyValue(Resource resource, String property) {
    return resource.getDoc().get(property) != null
      ? Optional.of(resource.getDoc().get(property).get(0).asText())
      : Optional.empty();
  }

  private Optional<List<String>> getPropertyValues(Resource resource, String property) {
    return resource.getDoc().get(property) != null
      ? Optional.of(objectMapper.convertValue(resource.getDoc().get(property), new TypeReference<>() {}))
      : Optional.empty();
  }
}
