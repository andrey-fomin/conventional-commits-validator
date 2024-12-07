package io.github.fomin.conventionalcommits.rules;

import com.google.errorprone.annotations.Var;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class RuleSetMerger {
  private RuleSetMerger() {}

  public static RuleSet merge(@Nullable RuleSet base, RuleSet target) {
    if (base == null) {
      return target;
    }
    TextRules message = mergeTextRules(base.getMessage(), target.getMessage());
    TextRules header = mergeTextRules(base.getHeader(), target.getHeader());
    TextRules type = mergeTextRules(base.getType(), target.getType());
    TextRules scope = mergeTextRules(base.getScope(), target.getScope());
    TextRules description = mergeTextRules(base.getDescription(), target.getDescription());
    TextRules body = mergeTextRules(base.getBody(), target.getBody());
    TextRules footerToken = mergeTextRules(base.getFooterToken(), target.getFooterToken());
    TextRules footerValue = mergeTextRules(base.getFooterValue(), target.getFooterValue());
    Map<String, TextRules> footers = mergeFooters(base.getFooters(), target.getFooters());
    return new RuleSet(
        message, header, type, scope, description, body, footerToken, footerValue, footers);
  }

  @Nullable
  private static TextRules mergeTextRules(@Nullable TextRules base, @Nullable TextRules target) {
    if (base == null) {
      return target;
    }
    if (target == null) {
      return base;
    }
    Integer maxLength = target.getMaxLength() != null ? target.getMaxLength() : base.getMaxLength();
    Integer maxLineLength =
        target.getMaxLineLength() != null ? target.getMaxLineLength() : base.getMaxLineLength();
    Boolean required = target.getRequired() != null ? target.getRequired() : base.getRequired();
    Boolean forbidden = target.getForbidden() != null ? target.getForbidden() : base.getForbidden();
    String regex = target.getRegex() != null ? target.getRegex() : base.getRegex();
    return new TextRules(maxLength, maxLineLength, required, forbidden, regex, target.getValues());
  }

  @Nullable
  private static Map<String, TextRules> mergeFooters(
      @Var @Nullable Map<String, TextRules> base, @Var @Nullable Map<String, TextRules> target) {
    if (base == null) {
      base = new HashMap<>();
    }
    if (target == null) {
      target = new HashMap<>();
    }
    Map<String, TextRules> result = new HashMap<>(base);
    for (Map.Entry<String, TextRules> targetEntry : target.entrySet()) {
      result.put(
          targetEntry.getKey(),
          mergeTextRules(base.get(targetEntry.getKey()), targetEntry.getValue()));
    }
    if (result.isEmpty()) {
      return null;
    }
    return result;
  }
}
