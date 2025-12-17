package org.folio.marc4ld.service.ld2marc.postprocessor;

import static org.folio.marc4ld.util.Constants.TAG_100;
import static org.folio.marc4ld.util.Constants.TAG_110;
import static org.folio.marc4ld.util.Constants.TAG_111;
import static org.folio.marc4ld.util.Constants.TAG_130;
import static org.folio.marc4ld.util.Constants.TAG_700;
import static org.folio.marc4ld.util.Constants.TAG_710;
import static org.folio.marc4ld.util.Constants.TAG_711;
import static org.folio.marc4ld.util.Constants.TAG_730;
import static org.folio.marc4ld.util.Constants.THREE;
import static org.folio.marc4ld.util.MarcUtil.sortFields;

import java.util.Comparator;
import java.util.Map;
import org.folio.ld.dictionary.model.Resource;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.springframework.stereotype.Component;

@Component
public class Marc1xxTo7xxNormalizer implements Ld2MarcPostProcessor {
  private static final Map<String, String> MAIN_ENTRY_TO_ADDED_ENTRY_MAP = Map.of(
    TAG_100, TAG_700,
    TAG_110, TAG_710,
    TAG_111, TAG_711,
    TAG_130, TAG_730
  );

  private static final Comparator<VariableField> MARC_1XX_PRIORITY_COMPARATOR =
    field130BeforeOther1xxComparator()
      .thenComparing(personBeforeFamilyComparator())
      .thenComparing(tagComparator());

  @Override
  public void postProcess(Resource instance, Record generatedMarc) {
    var fieldsToChange = generatedMarc.getDataFields()
      .stream()
      .filter(f -> MAIN_ENTRY_TO_ADDED_ENTRY_MAP.containsKey(f.getTag()))
      .sorted(MARC_1XX_PRIORITY_COMPARATOR)
      .skip(1)
      .toList();

    fieldsToChange.forEach(f -> f.setTag(MAIN_ENTRY_TO_ADDED_ENTRY_MAP.get(f.getTag())));

    if (!fieldsToChange.isEmpty()) {
      sortFields(generatedMarc);
    }
  }

  private static Comparator<VariableField> tagComparator() {
    return Comparator.comparing(VariableField::getTag);
  }

  private static Comparator<VariableField> field130BeforeOther1xxComparator() {
    return (f1, f2) -> Boolean.compare(is130Field(f2), is130Field(f1));
  }

  private static Comparator<VariableField> personBeforeFamilyComparator() {
    return (f1, f2) -> Boolean.compare(isPerson(f2), isPerson(f1));
  }

  private static boolean isPerson(VariableField field) {
    return is100Field(field) && ((DataField) field).getIndicator1() != THREE;
  }

  private static boolean is130Field(VariableField field) {
    return TAG_130.equals(field.getTag());
  }

  private static boolean is100Field(VariableField field) {
    return TAG_100.equals(field.getTag());
  }
}
