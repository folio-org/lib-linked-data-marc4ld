package org.folio.marc2ld.model;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.folio.ld.dictionary.ResourceTypeDictionary;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(of = "resourceHash")
public class Resource {

  private Long resourceHash;

  private String label;

  private JsonNode doc;

  private UUID inventoryId;

  private UUID srsId;

  private Set<ResourceTypeDictionary> types;

  private Set<ResourceEdge> outgoingEdges = new LinkedHashSet<>();

  public Resource addType(ResourceTypeDictionary type) {
    if (isNull(types)) {
      types = new LinkedHashSet<>();
    }
    types.add(type);
    return this;
  }
}
