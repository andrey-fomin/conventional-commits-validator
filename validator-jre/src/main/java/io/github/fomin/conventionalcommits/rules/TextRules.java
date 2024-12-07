package io.github.fomin.conventionalcommits.rules;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

public final class TextRules {
  @Nullable private final Integer maxLength;
  @Nullable private final Integer maxLineLength;
  @Nullable private final Boolean required;
  @Nullable private final Boolean forbidden;
  @Nullable private final String regex;
  @Nullable private final List<String> values;

  public TextRules(
      @Nullable Integer maxLength,
      @Nullable Integer maxLineLength,
      @Nullable Boolean required,
      @Nullable Boolean forbidden,
      @Nullable String regex,
      @Nullable List<String> values) {
    this.maxLength = maxLength;
    this.maxLineLength = maxLineLength;
    this.required = required;
    this.forbidden = forbidden;
    this.regex = regex;
    this.values = values;
  }

  @Nullable
  public Integer getMaxLength() {
    return maxLength;
  }

  @Nullable
  public Integer getMaxLineLength() {
    return maxLineLength;
  }

  @Nullable
  public Boolean getRequired() {
    return required;
  }

  @Nullable
  public Boolean getForbidden() {
    return forbidden;
  }

  @Nullable
  public String getRegex() {
    return regex;
  }

  @Nullable
  public List<String> getValues() {
    return values;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TextRules textRules = (TextRules) o;
    return Objects.equals(maxLength, textRules.maxLength)
        && Objects.equals(maxLineLength, textRules.maxLineLength)
        && Objects.equals(required, textRules.required)
        && Objects.equals(forbidden, textRules.forbidden)
        && Objects.equals(regex, textRules.regex)
        && Objects.equals(values, textRules.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxLength, maxLineLength, required, forbidden, regex, values);
  }

  @Override
  public String toString() {
    return "TextRules{"
        + "maxLength="
        + maxLength
        + ", maxLineLength="
        + maxLineLength
        + ", required="
        + required
        + ", forbidden="
        + forbidden
        + ", regex='"
        + regex
        + '\''
        + ", values="
        + values
        + '}';
  }
}
