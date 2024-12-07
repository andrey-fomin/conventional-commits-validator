package io.github.fomin.conventionalcommits.rules;

import com.google.errorprone.annotations.Var;
import java.util.List;
import javax.annotation.Nullable;

public class TextRulesValidator {
  private TextRulesValidator() {}

  public static void validate(
      String text, @Nullable TextRules textRules, String field, List<String> errors) {
    if (textRules == null) {
      return;
    }
    validateMaxLength(text, textRules, field, errors);
    validateMaxLineLength(text, textRules, field, errors);
    validateRequired(text, textRules, field, errors);
    validateForbidden(text, textRules, field, errors);
    validateRegex(text, textRules, field, errors);
    validateValues(text, textRules, field, errors);
  }

  private static void validateMaxLength(
      String text, TextRules textRules, String field, List<String> errors) {
    Integer maxLength = textRules.getMaxLength();
    if (maxLength == null) {
      return;
    }
    int length = text.length();
    if (length > maxLength) {
      errors.add(String.format("%s length %d is greater than %d", field, length, maxLength));
    }
  }

  private static void validateMaxLineLength(
      String text, TextRules textRules, String field, List<String> errors) {
    Integer maxLineLength = textRules.getMaxLineLength();
    if (maxLineLength == null) {
      return;
    }
    @Var int lineLength = 0;
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c == '\n') {
        if (lineLength > maxLineLength) {
          errors.add(
              String.format(
                  "%s line length %d is greater than %d", field, lineLength, maxLineLength));
          return;
        }
        lineLength = 0;
      } else {
        lineLength++;
      }
    }
    if (text.charAt(text.length() - 1) != '\n') {
      throw new IllegalArgumentException("The text does not end with a newline character");
    }
  }

  private static void validateRequired(
      String text, TextRules textRules, String field, List<String> errors) {
    Boolean required = textRules.getRequired();
    if (required != null && required && text.isEmpty()) {
      errors.add(String.format("%s is required", field));
    }
  }

  private static void validateForbidden(
      String text, TextRules textRules, String field, List<String> errors) {
    Boolean forbidden = textRules.getForbidden();
    if (forbidden != null && forbidden && !text.isEmpty()) {
      errors.add(String.format("%s is forbidden", field));
    }
  }

  private static void validateRegex(
      String text, TextRules textRules, String field, List<String> errors) {
    String regex = textRules.getRegex();
    if (regex != null && !text.matches(regex)) {
      errors.add(String.format("%s does not match regex %s", field, regex));
    }
  }

  private static void validateValues(
      String text, TextRules textRules, String field, List<String> errors) {
    List<String> values = textRules.getValues();
    if (values != null && !values.contains(text)) {
      errors.add(String.format("%s has invalid value, value %s is not in %s", field, text, values));
    }
  }
}
