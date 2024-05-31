package org.folio.marc4ld.service.marc2ld;

import java.util.Collection;

public interface Marc2ldRules {
  Collection<Marc2ldFieldRuleApplier> findFiledRules(String tag);

  //todo TEMPORARY ??
  Collection<Marc2ldFieldRuleApplier> findAuthorityFiledRules(String tag);
}
