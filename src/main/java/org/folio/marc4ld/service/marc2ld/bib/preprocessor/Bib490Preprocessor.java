package org.folio.marc4ld.service.marc2ld.bib.preprocessor;

import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.TAG_490;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

/**
 * Preprocessor for MARC field 490 (Series Statement).
 *
 * <p>
 * Handles the preprocessing logic to split repeated $a subfields into separate 490 fields
 * unless they are separated by "=" which indicates multi-script cataloging.
 *
 * <p>
 * Examples:
 * - 490 $a Technical assistance publication (TAP) series ; $v 19. $a Criminal justice subseries ; $v v. 2
 *   Should be split into two separate 490 fields
 *
 * <p>
 * - 490 $a Zhongguo Jing ju bai bu jing dian wai yi xi lie. Di 3 ji = $a Translation series...
 *   Should remain as one field (multi-script cataloging)
 */
@Component
@RequiredArgsConstructor
public class Bib490Preprocessor implements DataFieldPreprocessor {

  private static final List<String> TAGS = List.of(TAG_490);
  private static final String MULTISCRIPT_SEPARATOR = "=";
  
  private final MarcFactory marcFactory;

  @Override
  public List<String> getTags() {
    return TAGS;
  }

  @Override
  public List<DataField> preprocess(PreprocessorContext context) {
    var originalField = context.dataField();
    var subfields = originalField.getSubfields();
    
    // Find all $a subfields
    var subfieldsA = new ArrayList<Subfield>();
    for (var subfield : subfields) {
      if (subfield.getCode() == A) {
        subfieldsA.add(subfield);
      }
    }
    
    // If there's only one $a subfield, no processing needed
    if (subfieldsA.size() <= 1) {
      return List.of(originalField);
    }
    
    // Check if this is multi-script cataloging (first $a ends with "=")
    var firstSubfieldA = subfieldsA.get(0);
    var firstData = firstSubfieldA.getData();
    
    if (firstData != null && firstData.trim().endsWith(MULTISCRIPT_SEPARATOR)) {
      // For multi-script cataloging, keep the original field intact
      return List.of(originalField);
    }
    
    // Split into separate fields
    return splitIntoSeparateFields(originalField, subfields);
  }
  
  private List<DataField> splitIntoSeparateFields(DataField originalField, List<Subfield> allSubfields) {
    var result = new ArrayList<DataField>();
    var currentField = createNewField(originalField);
    
    for (var subfield : allSubfields) {
      if (subfield.getCode() == A && currentField.getSubfields().size() > 0) {
        // Start a new field when we encounter a new $a (except for the first one)
        result.add(currentField);
        currentField = createNewField(originalField);
      }
      currentField.addSubfield(subfield);
    }
    
    // Add the last field if it has subfields
    if (currentField.getSubfields().size() > 0) {
      result.add(currentField);
    }
    
    return result;
  }
  
  private DataField createNewField(DataField originalField) {
    return marcFactory.newDataField(
      originalField.getTag(),
      originalField.getIndicator1(),
      originalField.getIndicator2()
    );
  }
}
