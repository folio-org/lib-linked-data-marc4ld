package org.folio.marc4ld.service.marc2ld.bib.mapper.additional;

import static org.folio.ld.dictionary.PredicateDictionary.BOOK_FORMAT;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.TERM;
import static org.folio.marc4ld.util.Constants.TAG_340;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.marc2ld.mapper.AdditionalMapper;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookFormatMapper implements AdditionalMapper {

  private static final String FULL_SHEET_TERM = "full-sheet";
  private static final String FULL_SHEET_CODE = "full";
  private static final String LINK_PREFIX = "http://id.loc.gov/vocabulary/bookformat/";

  private final MapperHelper mapperHelper;

  @Override
  public List<String> getTags() {
    return List.of(TAG_340);
  }

  @Override
  public boolean canMap(Marc4LdRules.FieldRule fieldRule) {
    return BOOK_FORMAT.name().equals(fieldRule.getPredicate());
  }

  @Override
  public void map(MarcData marcData, Resource resource) {
    var terms = mapperHelper.getProperties(resource).getOrDefault(TERM.getValue(), List.of());

    if (terms.isEmpty()) {
      return;
    }

    var codes = terms.stream().map(term -> term.equals(FULL_SHEET_TERM) ? FULL_SHEET_CODE : term).toList();
    var links = codes.stream().map(code -> LINK_PREFIX + code).toList();
    mapperHelper.addPropertiesToResource(
      resource,
      Map.of(
        CODE.getValue(), codes,
        LINK.getValue(), links
      )
    );
  }
}
