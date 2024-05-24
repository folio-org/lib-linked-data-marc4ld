package org.folio.marc4ld.service.marc2ld.label;

import static org.apache.commons.lang3.StringUtils.SPACE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.util.Strings;
import org.folio.ld.dictionary.PropertyDictionary;

/** Special class for convert label by pattern in settings.
 *  pattern ${VALUE}
 *  example for settings:
 *  ${TERM}-${TITLE} -> termValue-titleValue
 */
public class TemplateLabelProcessor implements LabelProcessor {

  private static final Pattern PATTERN = Pattern.compile("\\$\\{(\\w+)}");

  private final List<String> templateParts;
  private final List<String> keys;

  public TemplateLabelProcessor(String template) {
    this.templateParts = new ArrayList<>();
    this.keys = new ArrayList<>();
    parseTemplate(template);
  }

  private void parseTemplate(String template) {
    var matcher = PATTERN.matcher(template);
    int lastIndex = 0;

    while (matcher.find()) {
      templateParts.add(template.substring(lastIndex, matcher.start()));
      keys.add(getPropertyKey(matcher));
      lastIndex = matcher.end();
    }
    templateParts.add(template.substring(lastIndex));
  }

  @Override
  public String apply(Map<String, List<String>> parameters) {
    var result = new StringBuilder();
    for (int i = 0; i < keys.size(); i++) {
      result.append(templateParts.get(i));
      var value = String.join(SPACE, parameters.getOrDefault(keys.get(i), Collections.emptyList()));
      result.append(value);
    }
    result.append(templateParts.get(keys.size()));
    return result.toString();
  }

  private String getPropertyKey(Matcher matcher) {
    return Optional.of(matcher.group(1))
      .map(PropertyDictionary::valueOf)
      .map(PropertyDictionary::getValue)
      .orElse(Strings.EMPTY);
  }
}
