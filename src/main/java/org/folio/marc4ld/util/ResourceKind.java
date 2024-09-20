package org.folio.marc4ld.util;

import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FAMILY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FORM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.JURISDICTION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.MEETING;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ORGANIZATION;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;

import java.util.Set;
import lombok.experimental.UtilityClass;
import org.folio.ld.dictionary.ResourceTypeDictionary;

@UtilityClass
public class ResourceKind {
  public static final Set<ResourceTypeDictionary> BIBLIOGRAPHIC = Set.of(INSTANCE, WORK);
  public static final Set<ResourceTypeDictionary> AUTHORITY = Set.of(
    CONCEPT, PERSON, FAMILY, MEETING, JURISDICTION, ORGANIZATION, FORM
  );
}
