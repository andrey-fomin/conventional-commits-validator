package io.github.fomin.conventionalcommits.antlr;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

public final class ParsingResult {
  @Nullable private final Message message;
  @Nullable private final List<String> errors;

  private ParsingResult(@Nullable Message message, @Nullable List<String> errors) {
    this.message = message;
    this.errors = errors;
  }

  public static ParsingResult message(Message messageContext) {
    return new ParsingResult(messageContext, null);
  }

  public static ParsingResult errors(List<String> errors) {
    return new ParsingResult(null, errors);
  }

  public Message getMessage() {
    return Objects.requireNonNull(message);
  }

  public List<String> getErrors() {
    return Objects.requireNonNull(errors);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParsingResult that = (ParsingResult) o;
    return Objects.equals(message, that.message) && Objects.equals(errors, that.errors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, errors);
  }

  @Override
  public String toString() {
    return "ParsingResult{" + "message=" + message + ", errors=" + errors + '}';
  }
}
