package org.folio.marc4ld.service.marc2ld.label;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.folio.marc4ld.service.label.processor.TemplateLabelProcessor;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@UnitTest
class TemplateLabelProcessorTest {

  @ParameterizedTest
  @MethodSource("provideProperties")
  void whenCompileByPattern(String expectedLabel, String pattern, Map<String, List<String>> properties) {
    // given
    var processor = new TemplateLabelProcessor(pattern);

    // when
    var label = processor.apply(properties);

    // then
    assertThat(label)
      .isEqualTo(expectedLabel);
  }

  public static Stream<Arguments> provideProperties() {
    return Stream.of(
      Arguments.of("Hello, world and moon",
        "Hello, {$TERM} and {$TITLES}",
        Map.of(
          "http://bibfra.me/vocab/marc/term", List.of("world"),
          "http://bibfra.me/vocab/marc/titles", List.of("moon")
        )),
      Arguments.of("Hello, only moon",
        "Hello,{ $TERM} only {$TITLES}",
        Map.of("http://bibfra.me/vocab/marc/titles", List.of("moon")
        )),
      Arguments.of("bVal, aVal, cVal, qVal, dVal -- vVal -- xVal -- yVal -- zVal1 zVal2",
        "{$LINK}, {$AFFILIATION}, {$CODE}, {$TERM}, {$DATE} -- {$NOTE} -- {$SUMMARY} -- {$TITLES} --{ $WITH_NOTE}",
        Map.of(
          "http://bibfra.me/vocab/scholar/affiliation", List.of("aVal"),
          "http://bibfra.me/vocab/lite/link", List.of("bVal"),
          "http://bibfra.me/vocab/marc/code", List.of("cVal"),
          "http://bibfra.me/vocab/lite/date", List.of("dVal"),
          "http://bibfra.me/vocab/marc/term", List.of("qVal"),
          "http://bibfra.me/vocab/lite/note", List.of("vVal"),
          "http://bibfra.me/vocab/marc/summary", List.of("xVal"),
          "http://bibfra.me/vocab/marc/titles", List.of("yVal"),
          "http://bibfra.me/vocab/marc/withNote", List.of("zVal1", "zVal2")
        )),
      Arguments.of("bVal, aVal, cVal, qVal, dVal -- vVal -- xVal, -- yVal -- zVal1 -- zVal2",
        "{$LINK}{, $AFFILIATION}{, $CODE}{, $TERM, }{$DATE}{ -- $NOTE}{ -- $SUMMARY,}{ -- $TITLES}{ -- $WITH_NOTE}",
        Map.of(
          "http://bibfra.me/vocab/scholar/affiliation", List.of("aVal"),
          "http://bibfra.me/vocab/lite/link", List.of("bVal"),
          "http://bibfra.me/vocab/marc/code", List.of("cVal"),
          "http://bibfra.me/vocab/lite/date", List.of("dVal"),
          "http://bibfra.me/vocab/marc/term", List.of("qVal"),
          "http://bibfra.me/vocab/lite/note", List.of("vVal"),
          "http://bibfra.me/vocab/marc/summary", List.of("xVal"),
          "http://bibfra.me/vocab/marc/titles", List.of("yVal"),
          "http://bibfra.me/vocab/marc/withNote", List.of("zVal1", "zVal2")
        )),
      Arguments.of("aVal -- xVal",
        "{$LINK}{, $AFFILIATION}{, $CODE}{, $TERM, }{$DATE}{ -- $NOTE}{ -- $SUMMARY,}{ -- $TITLES}{ -- $WITH_NOTE}",
        Map.of(
          "http://bibfra.me/vocab/scholar/affiliation", List.of("aVal"),
          "http://bibfra.me/vocab/marc/summary", List.of("xVal")
        )),
      Arguments.of("non-trimmed,,,,,,,,,,   ,,,,,--,,,: titles",
        "{$TERM,,,,,,,,,,   ,,,,,--,,,}{: $TITLES}",
        Map.of(
          "http://bibfra.me/vocab/marc/term", List.of("non-trimmed"),
          "http://bibfra.me/vocab/marc/titles", List.of("titles")
        )),
      Arguments.of("trimmed",
        "{$TERM,,,,,,,,,,   ,,,,,--,,,}{: $TITLES}",
        Map.of(
          "http://bibfra.me/vocab/marc/term", List.of("trimmed")
        )),
      Arguments.of("Prelude to the wedding, Batgirl vs. Riddler",
        "{, $TERM}",
        Map.of(
          "http://bibfra.me/vocab/marc/term", List.of("Prelude to the wedding", "Batgirl vs. Riddler")
        )),
      Arguments.of("f1--, f2--, f3",
        "{, $LINK--}",
        Map.of(
          "http://bibfra.me/vocab/lite/link", List.of("f1", "f2", "f3")
        ))
    );
  }
}
