package io.github.fomin.conventionalcommits.rules;

import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

public final class RuleSet {
  @Nullable private final TextRules message;
  @Nullable private final TextRules header;
  @Nullable private final TextRules type;
  @Nullable private final TextRules scope;
  @Nullable private final TextRules description;
  @Nullable private final TextRules body;
  @Nullable private final TextRules footerToken;
  @Nullable private final TextRules footerValue;
  @Nullable private final Map<String, TextRules> footers;

  public RuleSet(
      @Nullable TextRules message,
      @Nullable TextRules header,
      @Nullable TextRules type,
      @Nullable TextRules scope,
      @Nullable TextRules description,
      @Nullable TextRules body,
      @Nullable TextRules footerToken,
      @Nullable TextRules footerValue,
      @Nullable Map<String, TextRules> footers) {
    this.message = message;
    this.header = header;
    this.type = type;
    this.scope = scope;
    this.description = description;
    this.body = body;
    this.footerToken = footerToken;
    this.footerValue = footerValue;
    this.footers = footers;
  }

  @Nullable
  public TextRules getMessage() {
    return message;
  }

  @Nullable
  public TextRules getHeader() {
    return header;
  }

  @Nullable
  public TextRules getType() {
    return type;
  }

  @Nullable
  public TextRules getScope() {
    return scope;
  }

  @Nullable
  public TextRules getDescription() {
    return description;
  }

  @Nullable
  public TextRules getBody() {
    return body;
  }

  @Nullable
  public TextRules getFooterToken() {
    return footerToken;
  }

  @Nullable
  public TextRules getFooterValue() {
    return footerValue;
  }

  @Nullable
  public Map<String, TextRules> getFooters() {
    return footers;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RuleSet ruleSet = (RuleSet) o;
    return Objects.equals(message, ruleSet.message)
        && Objects.equals(header, ruleSet.header)
        && Objects.equals(type, ruleSet.type)
        && Objects.equals(scope, ruleSet.scope)
        && Objects.equals(description, ruleSet.description)
        && Objects.equals(body, ruleSet.body)
        && Objects.equals(footerToken, ruleSet.footerToken)
        && Objects.equals(footerValue, ruleSet.footerValue)
        && Objects.equals(footers, ruleSet.footers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        message, header, type, scope, description, body, footerToken, footerValue, footers);
  }

  @Override
  public String toString() {
    return "RuleSet{"
        + "message="
        + message
        + ", header="
        + header
        + ", type="
        + type
        + ", scope="
        + scope
        + ", description="
        + description
        + ", body="
        + body
        + ", footerToken="
        + footerToken
        + ", footerValue="
        + footerValue
        + ", footers="
        + footers
        + '}';
  }
}
