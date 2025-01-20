package org.folio.marc4ld.service.marc2ld.normalization;

import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.configuration.property.Marc2LdNormalizationRules;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
abstract class AbstractMarcPunctuationNormalizer implements MarcPunctuationNormalizer {

  private static final String PERIOD = ".";
  private static final String PARENTHESES_AND_SQUARE_BRACKETS_REGEX = "[\\[\\]()]";
  private static final Pattern SPACE_UPPERCASE_PERIOD = Pattern.compile(" [A-Z]\\.$");
  private static final Pattern PERIOD_UPPERCASE_PERIOD = Pattern.compile("\\.[A-Z]\\.$");

  protected final Marc2LdNormalizationRules marc2LdNormalizationRules;

  protected abstract Map<String, List<String>> getSubfieldRules();

  protected abstract Map<String, List<String>> getLastSubfieldRules();

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
    normalizeLastSubfield(dataField);
  }

  private void normalizeSubfields(List<Subfield> subfields, String tag) {
    if (subfields.size() == 1) {
      return;
    }
    for (int i = 1; i < subfields.size(); i++) {
      var precedingSubfield = subfields.get(i - 1);
      var lookups = generateLookups(tag, subfields.get(i).getCode());
      removePunctuation(lookups, precedingSubfield);
      removeParenthesesAndSquareBrackets(tag, precedingSubfield);
    }
  }

  private void normalizeLastSubfield(DataField dataField) {
    var subfields = dataField.getSubfields();
    var lastSubfield = subfields.get(subfields.size() - 1);
    ofNullable(getLastSubfieldRules().get(dataField.getTag()))
      .ifPresent(punctuationMarksToRemove ->
        lastSubfield.setData(removePunctuation(lastSubfield.getData(), punctuationMarksToRemove)));
    removeParenthesesAndSquareBrackets(dataField.getTag(), lastSubfield);
  }

  private void removeParenthesesAndSquareBrackets(String tag, Subfield subfield) {
    var lookups = generateLookups(tag, subfield.getCode());
    lookups.stream()
      .filter(lookup -> marc2LdNormalizationRules.getParenthesesAndBracketsRules().contains(lookup))
      .findAny()
      .ifPresent(lookup -> subfield.setData(removeParenthesesAndSquareBrackets(subfield.getData())));
  }

  private String removeParenthesesAndSquareBrackets(String data) {
    return data.replaceAll(PARENTHESES_AND_SQUARE_BRACKETS_REGEX, "").trim();
  }

  private void removePunctuation(List<String> lookups, Subfield precedingSubfield) {
    lookups.stream()
      .map(lookup -> getSubfieldRules().get(lookup))
      .filter(Objects::nonNull)
      .forEach(punctuationMarksToRemove ->
        precedingSubfield.setData(removePunctuation(precedingSubfield.getData(), punctuationMarksToRemove)));
  }

  private String removePunctuation(String data, List<String> punctuations) {
    boolean modified;
    do {
      modified = false;
      for (var punctuation : punctuations) {
        if (data.endsWith(punctuation) && isNotAbbreviation(punctuation, data)) {
          data = data.substring(0, data.length() - punctuation.length());
          modified = true;
          break;
        }
      }
    } while (modified);
    return data;
  }

  private boolean isNotAbbreviation(String punctuation, String data) {
    // Check if the trailing period in data is for an abbreviation
    if (!punctuation.equals(PERIOD)) {
      return true;
    }
    return !(SPACE_UPPERCASE_PERIOD.matcher(data).find() || PERIOD_UPPERCASE_PERIOD.matcher(data).find());
  }

  protected List<String> generateLookups(String tag, char code) {
    var lookups = new ArrayList<String>();
    lookups.add(String.format("XX%s%s", tag.charAt(2), code));
    lookups.add(String.format("%sXX%s", tag.charAt(0), code));
    lookups.add(String.format("%sX%s", tag.substring(0, 2), code));
    lookups.add(String.format("%s%s", tag, code));
    return lookups;
  }
}
