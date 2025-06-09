package org.folio.marc4ld.service.marc2ld.relation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.PERFORMER;
import static org.folio.ld.dictionary.PredicateDictionary.PHOTOGRAPHER;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.Marc2ldFieldRuleApplier;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class RelationProviderImplTest {

  @Mock
  private DictionaryProcessor dictionaryProcessor;
  @InjectMocks
  private RelationProviderImpl relationProvider;

  private static Stream<Arguments> provideArguments() {
    return Stream.of(
      Arguments.of(
        Optional.empty(),
        null,
        null,
        Collections.emptyList()
      ),
      Arguments.of(
        Optional.of(getRelation()),
        getSubfield("photographer"),
        getSubfield("prf"),
        List.of(PHOTOGRAPHER, PERFORMER)
      ),
      Arguments.of(
        Optional.of(getRelation()),
        getSubfield("photographer"),
        null,
        List.of(PHOTOGRAPHER)
      ),
      Arguments.of(
        Optional.of(getRelation()),
        null,
        getSubfield("prf"),
        List.of(PERFORMER)
      )
    );
  }

  @ParameterizedTest
  @MethodSource("provideArguments")
  void checkRelationShouldAddRelationsAppropriately(Optional<Relation> relation, Subfield subfieldE, Subfield subfield4,
                                                    List<PredicateDictionary> expectedRelations) {
    //given
    var source = new Resource();
    var target = new Resource();
    var fieldRule = mock(Marc2ldFieldRuleApplier.class);
    when(fieldRule.getRelation()).thenReturn(relation);
    var dataField = mock(DataField.class);
    if (relation.isPresent()) {
      when(dataField.getSubfield('e')).thenReturn(subfieldE);
      when(dataField.getSubfield('4')).thenReturn(subfield4);
      if (subfieldE != null) {
        when(dictionaryProcessor.getValue("AGENT_TEXT_TO_PREDICATE", subfieldE.getData()))
          .thenReturn(Optional.of("PHOTOGRAPHER"));
      }
    }

    //when
    relationProvider.checkRelation(source, target, dataField, fieldRule);

    //then
    var relations = source.getOutgoingEdges()
      .stream()
      .map(ResourceEdge::getPredicate)
      .toList();
    assertThat(relations)
      .containsExactlyInAnyOrderElementsOf(expectedRelations);
  }

  private static Relation getRelation() {
    return new Relation() {
      @Override
      public Optional<Character> getCode() {
        return Optional.of('4');
      }

      @Override
      public Optional<Character> getText() {
        return Optional.of('e');
      }
    };
  }

  private static Subfield getSubfield(String data) {
    var subfield = mock(Subfield.class);
    when(subfield.getData()).thenReturn(data);
    return subfield;
  }
}
