package org.folio.marc2ld.model;

import java.util.Objects;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.folio.ld.dictionary.PredicateDictionary;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor
public class ResourceEdge {

  @ToString.Exclude
  private ResourceEdgePk id = new ResourceEdgePk();

  @ToString.Exclude
  private final Resource source;

  private final Resource target;

  private final PredicateDictionary predicate;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResourceEdge that = (ResourceEdge) o;
    return Objects.equals(source.getResourceHash(), that.source.getResourceHash())
        && Objects.equals(target.getResourceHash(), that.target.getResourceHash())
        && Objects.equals(predicate.getHash(), that.predicate.getHash());
  }

  @Override
  public int hashCode() {
    return Objects.hash(source.getResourceHash(), target.getResourceHash(), predicate.getHash());
  }
}
