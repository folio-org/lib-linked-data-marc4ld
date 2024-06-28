package org.folio.marc4ld.util;

import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FAMILY;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;
import static org.folio.ld.dictionary.ResourceTypeDictionary.WORK;

import java.util.Set;
import lombok.experimental.UtilityClass;
import org.folio.ld.dictionary.ResourceTypeDictionary;

@UtilityClass
public class IdentifierTypes {
  public static final Set<ResourceTypeDictionary> BIBLIOGRAPHIC_TYPES = Set.of(INSTANCE, WORK);
  public static final Set<ResourceTypeDictionary> AUTHORITY_TYPES = Set.of(CONCEPT, PERSON, FAMILY);
}
