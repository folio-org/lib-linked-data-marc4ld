package org.folio.marc4ld.service.label;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.NAME;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PERSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.label.LabelGeneratorService;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.property.Marc4LdRules;
import org.folio.marc4ld.service.label.processor.LabelProcessor;
import org.folio.spring.testing.type.UnitTest;
import org.junit.jupiter.api.Test;

@UnitTest
class LabelServiceImplTest {

  @Test
  void setLabel_shouldUseLabelGeneratorService_whenLabelIsAvailable() {
    // given
    var deprecatedLabelProcessor = new TestLabelProcessor("fallback label");
    var service = createService(false, "generated label", deprecatedLabelProcessor);
    var resource = new Resource().addType(PERSON);
    var properties = Map.of(NAME.getValue(), List.of("fallback name"));

    // when
    service.setLabel(resource, properties);

    // then
    assertThat(resource.getLabel()).isEqualTo("generated label");
    assertThat(deprecatedLabelProcessor.getApplyCount()).isZero();
  }

  @Test
  void setLabel_shouldUseDeprecatedFallback_whenLabelGeneratorServiceReturnsNull() {
    // given
    var deprecatedLabelProcessor = new TestLabelProcessor("fallback label");
    var service = createService(true, null, deprecatedLabelProcessor);
    var resource = new Resource().addType(PERSON);
    var properties = new HashMap<>(Map.of(NAME.getValue(), List.of("fallback name")));

    // when
    service.setLabel(resource, properties);

    // then
    assertThat(resource.getLabel()).isEqualTo("fallback label");
    assertThat(properties).containsEntry(LABEL.getValue(), List.of("fallback label"));
    assertThat(deprecatedLabelProcessor.getApplyCount()).isOne();
  }

  @Test
  void setLabel_shouldUseDeprecatedFallback_whenLabelGeneratorServiceReturnsBlankLabel() {
    // given
    var deprecatedLabelProcessor = new TestLabelProcessor("fallback label");
    var service = createService(false, "   ", deprecatedLabelProcessor);
    var resource = new Resource().addType(PERSON);
    var properties = Map.of(NAME.getValue(), List.of("fallback name"));

    // when
    service.setLabel(resource, properties);

    // then
    assertThat(resource.getLabel()).isEqualTo("fallback label");
    assertThat(deprecatedLabelProcessor.getApplyCount()).isOne();
  }

  @Test
  void setLabel_shouldUseDeprecatedFallback_whenGeneratedLabelEqualsCurrentResourceLabel() {
    // given
    var deprecatedLabelProcessor = new TestLabelProcessor("fallback label");
    var service = createService(false, "same label", deprecatedLabelProcessor);
    var resource = new Resource().addType(PERSON).setLabel("same label");
    var properties = Map.of(NAME.getValue(), List.of("fallback name"));

    // when
    service.setLabel(resource, properties);

    // then
    assertThat(resource.getLabel()).isEqualTo("fallback label");
    assertThat(deprecatedLabelProcessor.getApplyCount()).isOne();
  }

  private LabelServiceImpl createService(boolean addLabelProperty, String generatedLabel,
                                         LabelProcessor deprecatedLabelProcessor) {
    var rules = new Marc4LdRules();
    var labelRule = new Marc4LdRules.LabelRule();
    labelRule.setTypes(List.of(PERSON.name()));
    labelRule.setAddLabelProperty(addLabelProperty);
    rules.setLabelRules(List.of(labelRule));
    var labelGeneratorService = new TestLabelGeneratorService(generatedLabel);
    LabelProcessorFactory labelProcessorFactory = rule -> List.of(deprecatedLabelProcessor);
    return new LabelServiceImpl(labelGeneratorService, rules, labelProcessorFactory);
  }

  private static final class TestLabelGeneratorService extends LabelGeneratorService {

    private final String label;

    private TestLabelGeneratorService(String label) {
      this.label = label;
    }

    @Override
    public String getLabel(Resource resource) {
      return label;
    }
  }

  private static final class TestLabelProcessor implements LabelProcessor {

    private final String label;
    private int applyCount;

    private TestLabelProcessor(String label) {
      this.label = label;
    }

    @Override
    public String apply(Map<String, List<String>> properties) {
      applyCount++;
      return label;
    }

    private int getApplyCount() {
      return applyCount;
    }
  }
}
