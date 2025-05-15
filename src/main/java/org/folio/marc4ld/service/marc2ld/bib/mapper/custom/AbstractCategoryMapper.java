package org.folio.marc4ld.service.marc2ld.bib.mapper.custom;

import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.TERM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.service.label.LabelService;
import org.folio.marc4ld.service.marc2ld.mapper.MapperHelper;

public abstract class AbstractCategoryMapper extends AbstractBookMapper {

  protected final String categorySetLink;

  private final String categorySetLabel;

  protected AbstractCategoryMapper(LabelService labelService, MapperHelper mapperHelper,
                                   FingerprintHashService hashService, int startIndex, int endIndex,
                                   String categorySetLabel, String categorySetLink) {
    super(labelService, mapperHelper, hashService, startIndex, endIndex);
    this.categorySetLabel = categorySetLabel;
    this.categorySetLink = categorySetLink;
  }

  protected abstract String getLinkSuffix(char code);

  protected abstract String getTerm(char code);

  protected abstract String getCode(char code);

  protected abstract PredicateDictionary getPredicate();

  @Override
  protected void addSubResource(Resource resource, char code) {
    var category = createCategory(
      getCode(code),
      categorySetLink + "/" + getLinkSuffix(code),
      getTerm(code)
    );
    resource.addOutgoingEdge(new ResourceEdge(resource, category, getPredicate()));
  }

  protected Resource createCategory(String code, String link, String term) {
    var categorySet = createResource(Set.of(CATEGORY_SET), Map.of(
      LINK.getValue(), List.of(categorySetLink),
      LABEL.getValue(), List.of(categorySetLabel)
    ), Collections.emptyMap());
    return createResource(Set.of(CATEGORY), Map.of(
      CODE.getValue(), List.of(code),
      LINK.getValue(), List.of(link),
      TERM.getValue(), List.of(term)
    ), Map.of(IS_DEFINED_BY, categorySet));
  }
}
