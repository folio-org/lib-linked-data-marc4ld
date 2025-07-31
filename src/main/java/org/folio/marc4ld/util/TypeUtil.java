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
import org.marc4j.marc.impl.LeaderImpl;

@UtilityClass
public class TypeUtil {
  private static final Set<TypeDefinition> TYPE_DEFINITIONS = Set.of(
    new TypeDefinition(BOOKS, MarcUtil::isBook),
    new TypeDefinition(CONTINUING_RESOURCES, MarcUtil::isSerial)
  );

  public static boolean isSupported(String leader) {
    return getWorkType(new LeaderImpl(leader))
      .isPresent();
  }

  public static Optional<ResourceTypeDictionary> getWorkType(Leader leader) {
    return Optional.of(leader)
      .flatMap(l -> TYPE_DEFINITIONS.stream()
        .filter(th -> th.checker.test(l))
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
