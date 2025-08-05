package org.folio.marc4ld.service.ld2marc.processing.combine.factory;

import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.service.ld2marc.processing.combine.CombinerFactory;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombiner;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminMetadataCombinerFactory implements CombinerFactory {

  private static final String TAG = "040";

  private final Comparator<Subfield> comparator;

  @Override
  public String getTag() {
    return TAG;
  }

  @Override
  public boolean test(DataFieldCombiner.Context context) {
    return context.resourceTypes().contains(INSTANCE);
  }

  @Override
  public DataFieldCombiner get() {
    return new AdminMetadataCombiner(comparator);
  }

  private static final class AdminMetadataCombiner extends AbstractDataFieldCombiner {

    AdminMetadataCombiner(Comparator<Subfield> comparator) {
      super(comparator);
    }

    @Override
    protected Collection<Character> getNonRepeatableFields() {
      return Set.of('a', 'b', 'c');
    }

    @Override
    protected Collection<Character> getRepeatableFields() {
      return Set.of('d');
    }
  }
}
