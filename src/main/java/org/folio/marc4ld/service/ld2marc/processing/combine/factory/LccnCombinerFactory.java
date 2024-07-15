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
public class LccnCombinerFactory implements CombinerFactory {

  private static final String TAG = "010";

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
    return new LccnCombiner(comparator);
  }

  private static final class LccnCombiner extends AbstractDataFieldCombiner {

    private static final Collection<Character> NON_REPEATABLE_FIELDS = Set.of('a');
    private static final Collection<Character> REPEATABLE_FIELDS = Set.of('z');

    LccnCombiner(Comparator<Subfield> comparator) {
      super(comparator);
    }

    @Override
    protected Collection<Character> getNonRepeatableFields() {
      return NON_REPEATABLE_FIELDS;
    }

    @Override
    protected Collection<Character> getRepeatableFields() {
      return REPEATABLE_FIELDS;
    }
  }
}
