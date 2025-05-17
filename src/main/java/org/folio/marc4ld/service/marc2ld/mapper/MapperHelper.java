package org.folio.marc4ld.service.marc2ld.mapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.ControlField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapperHelper {

  private final ObjectMapper objectMapper;

  /**
   * From the given list of control fields, return the first control field with the given tag and data length greater
   * than or equal to the given minimum length.
   */
  public Optional<ControlField> getControlField(List<ControlField> controlFields, String tag, int minLength) {
    return controlFields
      .stream()
      .filter(cf -> tag.equals(cf.getTag()))
      .filter(cf -> nonNull(cf.getData()))
      .filter(cf -> cf.getData().length() >= minLength)
      .findFirst();
  }

  /**
   * Add the given additional properties to the resource's doc.
   */
  public void addPropertiesToResource(Resource resource, Map<String, List<String>> additionalProperties) {
    var originalProperties = new HashMap<>(getProperties(resource));
    additionalProperties.forEach((k, v) ->
      originalProperties.merge(k, v, (v1, v2) -> {
        v1.addAll(v2);
        return v1;
      })
    );
    resource.setDoc(objectMapper.convertValue(originalProperties, JsonNode.class));
  }

  public Map<String, List<String>> getProperties(Resource resource) {
    if (isNull(resource.getDoc())) {
      return new HashMap<>();
    }
    return objectMapper.convertValue(resource.getDoc(), new TypeReference<>() {
    });
  }

  public JsonNode getJsonNode(Map<String, List<String>> map) {
    return objectMapper.convertValue(map, JsonNode.class);
  }
}
