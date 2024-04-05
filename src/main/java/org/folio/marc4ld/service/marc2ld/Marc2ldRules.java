package org.folio.marc4ld.service.marc2ld;

import java.util.Collection;

public interface Marc2ldRules {
  Collection<Marc2ldFieldRule> findFiledRules(String tag);
}
