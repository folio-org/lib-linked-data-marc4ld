package org.folio.marc4ld.service.marc2ld.bib.mapper.custom;

import static java.util.Map.entry;
import static org.folio.ld.dictionary.PredicateDictionary.ILLUSTRATIONS;

import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.specific.IllustrationDictionary;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.springframework.stereotype.Component;

@Component
public class IllustrationsMapper extends AbstractCategoryMapper {

  private static final String LINK_SUFFIX = "http://id.loc.gov/vocabulary/millus/";
  private static final Set<Character> SUPPORTED_CODES = Set.of(
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'o', 'p');
  private static final Map<Character, String> CODE_TO_TERM_MAP = Map.ofEntries(
    entry('a', "Illustrations"),
    entry('b', "Maps"),
    entry('c', "Portraits"),
    entry('d', "Charts"),
    entry('e', "Plans"),
    entry('f', "Plates"),
    entry('g', "Music"),
    entry('h', "Facsimiles"),
    entry('i', "Coats of arms"),
    entry('j', "Genealogical tables"),
    entry('k', "Forms"),
    entry('l', "Samples"),
    entry('m', "Phonodisc, phonowire, etc."),
    entry('o', "Photographs"),
    entry('p', "Illuminations")
  );

  public IllustrationsMapper(LabelService labelService,
                             MapperHelper mapperHelper,
                             FingerprintHashService hashService) {
    super(labelService, mapperHelper, hashService, 18, 22,
      "Illustrative Content",
      "http://id.loc.gov/vocabulary/millus");
  }

  @Override
  protected boolean isSupportedCode(char code) {
    return SUPPORTED_CODES.contains(code);
  }

  @Override
  protected PredicateDictionary getPredicate() {
    return ILLUSTRATIONS;
  }

  @Override
  protected String getLinkSuffix(char code) {
    return IllustrationDictionary.getValue(code)
      .map(s -> s.substring(LINK_SUFFIX.length()))
      .orElse(null);
  }

  @Override
  protected String getTerm(char code) {
    return CODE_TO_TERM_MAP.get(code);
  }

  @Override
  protected String getCode(char code) {
    return "" + code;
  }
}
