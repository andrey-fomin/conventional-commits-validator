package io.github.fomin.conventionalcommits.antlr;

import java.util.Objects;

/** Represents a footer in a commit message. */
public final class Footer {
  private final String token;
  private final String value;

  public Footer(String token, String value) {
    this.token = token;
    this.value = value;
  }

  /**
   * Returns the token of the footer.
   *
   * @return Footer token.
   */
  public String token() {
    return token;
  }

  /**
   * Returns the value of the footer.
   *
   * @return Footer value.
   */
  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Footer footer = (Footer) o;
    return Objects.equals(token, footer.token) && Objects.equals(value, footer.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(token, value);
  }

  @Override
  public String toString() {
    return "Footer{" + "token='" + token + '\'' + ", value='" + value + '\'' + '}';
  }
}
