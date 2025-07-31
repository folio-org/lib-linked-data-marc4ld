package org.folio.marc4ld.util;

import static java.util.stream.Collectors.joining;
import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONTINUING_RESOURCES;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;

@UtilityClass
public class TypeUtil {
  private static final Set<TypeDefinition> TYPE_DEFINITIONS = Set.of(
    new TypeDefinition(BOOKS, MarcUtil::isBook),
    new TypeDefinition(CONTINUING_RESOURCES, MarcUtil::isSerial)
  );

  public static Optional<ResourceTypeDictionary> getWorkType(Record marcRecord) {
    return Optional.of(marcRecord.getLeader())
      .flatMap(leader -> TYPE_DEFINITIONS.stream()
        .filter(th -> th.checker.test(leader))
        .map(th -> th.type)
        .findFirst());
  }

  public static String getSupportedRecordTypes() {
    return TYPE_DEFINITIONS.stream()
      .map(definition -> definition.type.name())
      .collect(joining(", "));
  }

  private record TypeDefinition(ResourceTypeDictionary type, Predicate<Leader> checker) {
  }
}
