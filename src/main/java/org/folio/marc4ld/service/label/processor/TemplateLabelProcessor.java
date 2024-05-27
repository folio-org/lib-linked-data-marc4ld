package org.folio.marc4ld.service.label.processor;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.folio.ld.dictionary.PropertyDictionary;

/**
 * Special class for convert label by pattern in settings.
 * pattern $VALUE
 * example for settings:
 * {$TERM} {$TITLE} -> termValue titleValue
 * {start - $TERM-some}-{$TITLE-d} -> "start - termValue-some-titleValue-d"
 * or if $TERM non exist -> "-titleValue-d"
 * or if $TITLE non exist "start - termValue-some"
 */
public class TemplateLabelProcessor implements LabelProcessor {

  private static final Pattern CONDITIONAL_PATTERN = Pattern.compile("\\{([^}]*)\\}");
  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$(\\w+)");
  private static final Pattern TRIM_RESULT_PATTERN = Pattern.compile("^[\\s,\\-]+|[\\s,\\-]+$");

  private final List<String> templateParts;
  private final List<String> conditionalParts;

  public TemplateLabelProcessor(String template) {
    this.templateParts = new ArrayList<>();
    this.conditionalParts = new ArrayList<>();
    parseTemplate(template);
  }

  private void parseTemplate(String template) {
    var matcher = CONDITIONAL_PATTERN.matcher(template);
    int lastIndex = 0;

    while (matcher.find()) {
      templateParts.add(template.substring(lastIndex, matcher.start()));
      conditionalParts.add(matcher.group(1));
      lastIndex = matcher.end();
    }
    templateParts.add(template.substring(lastIndex));
  }

  @Override
  public String apply(Map<String, List<String>> parameters) {
    var result = new StringBuilder();
    var lastIndex = 0;

    for (int i = 0; i < conditionalParts.size(); i++) {
      result.append(templateParts.get(i));
      var conditionalPart = conditionalParts.get(i);
      var processedPart = processConditionalPart(conditionalPart, parameters);

      if (StringUtils.isNotEmpty(processedPart)) {
        result.append(processedPart);
        lastIndex = i + 1;
      }
    }

    result.append(templateParts.get(lastIndex));

    return trim(result);
  }

  private String processConditionalPart(String part, Map<String, List<String>> parameters) {
    var result = new StringBuilder();
    var variableMatcher = VARIABLE_PATTERN.matcher(part);
    var lastIndex = 0;

    while (variableMatcher.find()) {
      var key = getPropertyKey(variableMatcher);
      var values = parameters.getOrDefault(key, Collections.emptyList());

      if (values.isEmpty()) {
        return EMPTY;
      }

      result.append(part, lastIndex, variableMatcher.start());
      result.append(String.join(SPACE, values));
      lastIndex = variableMatcher.end();
    }
    result.append(part.substring(lastIndex));

    return result.toString();
  }

  private String getPropertyKey(Matcher matcher) {
    return Optional.of(matcher.group(1))
      .map(PropertyDictionary::valueOf)
      .map(PropertyDictionary::getValue)
      .orElse(Strings.EMPTY);
  }

  private String trim(StringBuilder result) {
    return TRIM_RESULT_PATTERN.matcher(result)
      .replaceAll(EMPTY);
  }
}
