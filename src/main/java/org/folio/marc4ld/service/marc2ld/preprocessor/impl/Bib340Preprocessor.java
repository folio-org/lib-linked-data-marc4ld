package org.folio.marc4ld.service.marc2ld.preprocessor.impl;

import static org.folio.marc4ld.util.Constants.M;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.Constants.TAG_340;
import static org.folio.marc4ld.util.Constants.Z;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Bib340Preprocessor implements DataFieldPreprocessor {
  private static final List<String> TAGS = List.of(TAG_340);
  // Source: https://www.rdaregistry.info/termList/bookFormat/
  private static final List<String> STANDARD_BOOK_FORMATS = List.of(
    "folio",
    "4to",
    "8vo",
    "12mo",
    "16mo",
    "24mo",
    "32mo",
    "48mo",
    "64mo",
    "18mo",
    "36mo",
    "72mo",
    "96mo",
    "128mo",
    "full-sheet"
  );

  private final MarcFactory marcFactory;

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  @Override
  public List<DataField> preprocess(PreprocessorContext context) {
    var field = context.dataField();
    var processedField = marcFactory.newDataField(field.getTag(), field.getIndicator1(), field.getIndicator2());
    var additionalDataFields = new ArrayList<DataField>();

    field.getSubfields().stream()
      .map(this::processSubfield)
      .forEach(subfieldOrDataField -> {
        if (subfieldOrDataField.isSubfield()) {
          processedField.addSubfield(subfieldOrDataField.subfield());
        } else {
          additionalDataFields.add(subfieldOrDataField.dataField());
        }
      });

    var output = new ArrayList<>(additionalDataFields);
    if (isValid(processedField)) {
      output.add(processedField);
    }
    return output;
  }

  private SubFieldOrDataField processSubfield(Subfield subfield) {
    if (subfield.getCode() != M || STANDARD_BOOK_FORMATS.contains(subfield.getData())) {
      return SubFieldOrDataField.subfield(subfield);
    }

    var cleansedData = stripNonAlphaNumeric(subfield.getData());
    if (STANDARD_BOOK_FORMATS.contains(cleansedData)) {
      return SubFieldOrDataField.subfield(marcFactory.newSubfield(M, cleansedData));
    }

    var new340Field = createNew340Field(subfield);
    return SubFieldOrDataField.dataField(new340Field);
  }

  private DataField createNew340Field(Subfield subfield) {
    var new340Field = marcFactory.newDataField(TAG_340, SPACE, SPACE);
    new340Field.addSubfield(subfield);
    // Add a fake subfield z so that a string property is created in the Instance node rather than creating a
    // Category node. Refer to the mapping in the marc4ld yml file.
    new340Field.addSubfield(marcFactory.newSubfield(Z, subfield.getData()));
    return new340Field;
  }

  private String stripNonAlphaNumeric(String data) {
    return data.chars()
      .filter(Character::isLetterOrDigit)
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();
  }

  private boolean isValid(DataField df) {
    return !df.getSubfields().isEmpty();
  }

  private record SubFieldOrDataField(Subfield subfield, DataField dataField) {
    public static SubFieldOrDataField subfield(Subfield subfield) {
      return new SubFieldOrDataField(subfield, null);
    }

    public static SubFieldOrDataField dataField(DataField dataField) {
      return new SubFieldOrDataField(null, dataField);
    }

    public boolean isSubfield() {
      return subfield != null;
    }
  }
}
