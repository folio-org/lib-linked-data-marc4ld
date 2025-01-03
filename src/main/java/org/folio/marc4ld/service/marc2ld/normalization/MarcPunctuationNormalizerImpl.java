package org.folio.marc4ld.service.marc2ld.normalization;

import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.configuration.property.Marc2LdNormalizationRules;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarcPunctuationNormalizerImpl implements MarcPunctuationNormalizer {

  private final Marc2LdNormalizationRules marc2LdNormalizationRules;

  @Override
  public void normalize(Record marcRecord) {
    ofNullable(marcRecord)
      .map(Record::getDataFields)
      .ifPresent(dataFields -> dataFields.forEach(this::applyNormalization));
  }

  private void applyNormalization(DataField dataField) {
    var subfields = dataField.getSubfields();
    if (isEmpty(subfields)) {
      return;
    }
    normalizeSubfields(subfields, dataField.getTag());
    normalizeLastSubfield(subfields, dataField.getTag());
  }

  private void normalizeSubfields(List<Subfield> subfields, String tag) {
    if (subfields.size() == 1) {
      return;
    }
    for (int i = 1; i < subfields.size(); i++) {
      var precedingSubfield = subfields.get(i - 1);
      generateLookups(tag, subfields.get(i).getCode())
        .stream()
        .map(lookup -> marc2LdNormalizationRules.getSubfieldRules().get(lookup))
        .filter(Objects::nonNull)
        .forEach(punctuationMarksToRemove ->
          precedingSubfield.setData(removePunctuation(precedingSubfield.getData(), punctuationMarksToRemove)));
    }
  }

  private void normalizeLastSubfield(List<Subfield> subfields, String tag) {
    var lastSubfield = subfields.get(subfields.size() - 1);
    ofNullable(marc2LdNormalizationRules.getLastSubfieldRules().get(tag))
      .ifPresent(punctuationMarksToRemove ->
        lastSubfield.setData(removePunctuation(lastSubfield.getData(), punctuationMarksToRemove)));
  }

  private String removePunctuation(String data, List<String> punctuations) {
    boolean modified;
    do {
      modified = false;
      for (var punctuation : punctuations) {
        if (data.endsWith(punctuation)) {
          data = data.substring(0, data.length() - punctuation.length());
          modified = true;
          break;
        }
      }
    } while (modified);
    return data;
  }

  private List<String> generateLookups(String tag, char code) {
    var lookups = new ArrayList<String>();
    lookups.add(String.format("XX%s%s", tag.charAt(2), code));
    lookups.add(String.format("%sXX%s", tag.charAt(0), code));
    lookups.add(String.format("%sX%s", tag.substring(0, 2), code));
    lookups.add(String.format("%s%s", tag, code));
    return lookups;
  }
}
