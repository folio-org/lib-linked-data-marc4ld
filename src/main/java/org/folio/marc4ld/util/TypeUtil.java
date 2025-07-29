package org.folio.marc4ld.util;

import static org.folio.ld.dictionary.ResourceTypeDictionary.BOOKS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONTINUING_RESOURCES;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;

@UtilityClass
public class TypeUtil {
  private static final Set<TypeDefinition> TYPE_DEFINITIONS = Set.of(
    new TypeDefinition("Book", BOOKS, MarcUtil::isBook),
    new TypeDefinition("Serial", CONTINUING_RESOURCES, MarcUtil::isSerial)
  );

  public static Optional<ResourceTypeDictionary> getWorkType(Record marcRecord) {
    return Optional.of(marcRecord.getLeader())
      .flatMap(leader -> TYPE_DEFINITIONS.stream()
        .filter(th -> th.checker.apply(leader))
        .map(th -> th.type)
        .findFirst());
  }

  public static String getSupportedRecordTypes() {
    return TYPE_DEFINITIONS.stream()
      .map(TypeDefinition::name)
      .collect(Collectors.joining(", "));
  }

  private record TypeDefinition(String name, ResourceTypeDictionary type, Function<Leader, Boolean> checker) {
  }
}
