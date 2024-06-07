package org.folio.marc4ld.service.ld2marc.processing.combine.factory;

import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;

import java.util.Collection;
import java.util.Set;
import org.folio.marc4ld.service.ld2marc.processing.combine.CombinerFactory;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombiner;
import org.springframework.stereotype.Component;

@Component
public class TitleCombinerFactory implements CombinerFactory {

  private static final String TAG = "245";

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
    return new TitleCombiner();
  }

  private static final class TitleCombiner extends AbstractDataFieldCombiner {

    private static final Collection<Character> NON_REPEATABLE_FIELDS = Set.of('a', 'b', 'c');
    private static final Collection<Character> REPEATABLE_FIELDS = Set.of('n', 'p');

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
