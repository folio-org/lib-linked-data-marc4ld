package org.folio.marc4ld.util;

import static java.util.stream.Collectors.joining;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONTINUING_RESOURCES;
import static org.folio.marc4ld.enums.BibliographLevel.COLLECTION;
import static org.folio.marc4ld.enums.BibliographLevel.MONOGRAPHIC_COMPONENT_PART;
import static org.folio.marc4ld.enums.BibliographLevel.MONOGRAPH_OR_ITEM;
import static org.folio.marc4ld.enums.BibliographLevel.SERIAL;
import static org.folio.marc4ld.enums.BibliographLevel.SERIAL_COMPONENT_PART;
import static org.folio.marc4ld.enums.BibliographLevel.SUBUNIT;
import static org.folio.marc4ld.enums.RecordType.LANGUAGE_MATERIAL;
import static org.folio.marc4ld.enums.RecordType.MANUSCRIPT_LANGUAGE_MATERIAL;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.LdUtil.getWork;
import static org.folio.marc4ld.util.MarcUtil.getBibliographicLevel;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.enums.BibliographLevel;
import org.folio.marc4ld.enums.RecordType;
import org.marc4j.marc.Leader;
import org.marc4j.marc.impl.LeaderImpl;

@UtilityClass
public class TypeUtil {
  private static final Set<TypeDefinition> TYPE_DEFINITIONS = Set.of(
    new TypeDefinition(BOOKS,
      List.of(LANGUAGE_MATERIAL, MANUSCRIPT_LANGUAGE_MATERIAL),
      List.of(MONOGRAPH_OR_ITEM, MONOGRAPHIC_COMPONENT_PART, COLLECTION, SUBUNIT)),
    new TypeDefinition(CONTINUING_RESOURCES,
      List.of(),
      List.of(SERIAL, SERIAL_COMPONENT_PART))
  );

  public static boolean isSupported(String leader) {
    return getWorkType(new LeaderImpl(leader))
      .isPresent();
  }

  public static Optional<ResourceTypeDictionary> getWorkType(Leader leader) {
    return Optional.of(leader)
      .flatMap(l -> TYPE_DEFINITIONS.stream()
        .filter(td -> recordTypesMatch(leader, td) && bibliographLevelsMatch(leader, td))
        .map(th -> th.type)
        .findFirst());
  }

  public static char getBibliographLevel(Resource instance) {
    return extractValue(instance, td -> td.bibliographLevels, bl -> bl.value);
  }

  public static char getRecordType(Resource instance) {
    return extractValue(instance, td -> td.recordTypes, rt -> rt.value);
  }

  public static String getSupportedRecordTypes() {
    return TYPE_DEFINITIONS.stream()
      .map(definition -> definition.type.name())
      .collect(joining(", "));
  }

  private static boolean bibliographLevelsMatch(Leader leader, TypeDefinition td) {
    return valuesMatch(getBibliographicLevel(leader), td, TypeDefinition::bibliographLevels, bl -> bl.value);
  }

  private static boolean recordTypesMatch(Leader leader, TypeDefinition td) {
    return valuesMatch(leader.getTypeOfRecord(), td, TypeDefinition::recordTypes, rt -> rt.value);
  }

  private static <T> boolean valuesMatch(char charValue,
                                         TypeDefinition td,
                                         Function<TypeDefinition, List<T>> valuesProvider,
                                         Function<T, Character> valueExtractor) {
    var values = valuesProvider.apply(td);
    return values.isEmpty() || values.stream()
      .map(valueExtractor)
      .anyMatch(v -> v == charValue);
  }

  private static <T> char extractValue(Resource instance,
                                       Function<TypeDefinition, List<T>> valuesProvider,
                                       Function<T, Character> valueExtractor) {
    return getWork(instance)
      .stream()
      .flatMap(work -> TYPE_DEFINITIONS.stream()
        .filter(td -> work.isOfType(td.type))
        .map(valuesProvider)
        .filter(values -> !values.isEmpty())
        .map(List::getFirst)
        .map(valueExtractor))
      .findFirst()
      .orElse(SPACE);
  }

  private record TypeDefinition(ResourceTypeDictionary type,
                                List<RecordType> recordTypes,
                                List<BibliographLevel> bibliographLevels) {
  }
}
