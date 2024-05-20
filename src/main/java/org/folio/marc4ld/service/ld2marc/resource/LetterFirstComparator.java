package org.folio.marc4ld.service.ld2marc.resource;

import java.util.Comparator;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
public class LetterFirstComparator implements Comparator<Subfield> {
  @Override
  public int compare(Subfield sf1, Subfield sf2) {
    var code1 = sf1.getCode();
    var code2 = sf2.getCode();
    var isFirstDigit = Character.isDigit(code1);
    var isSecondDigit = Character.isDigit(code2);

    if (isFirstDigit == isSecondDigit) {
      return Character.compare(code1, code2);
    }

    return isFirstDigit ? 1 : -1;
  }
}
