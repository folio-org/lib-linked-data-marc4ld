package org.folio.marc4ld.service.ld2marc.processing.combine.factory;

import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.util.Constants.TAG_245;

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
public class TitleCombinerFactory implements CombinerFactory {

  private final Comparator<Subfield> comparator;

  @Override
  public String getTag() {
    return TAG_245;
  }

  @Override
  public boolean test(DataFieldCombiner.Context context) {
    return context.resourceTypes().contains(INSTANCE);
  }

  @Override
  public DataFieldCombiner get() {
    return new TitleCombiner(comparator);
  }

  private static final class TitleCombiner extends AbstractDataFieldCombiner {

    private static final Collection<Character> NON_REPEATABLE_FIELDS = Set.of('a', 'b', 'c');
    private static final Collection<Character> REPEATABLE_FIELDS = Set.of('n', 'p');

    TitleCombiner(Comparator<Subfield> comparator) {
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
