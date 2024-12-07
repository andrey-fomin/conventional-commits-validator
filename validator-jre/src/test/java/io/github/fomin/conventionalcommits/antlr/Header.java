package io.github.fomin.conventionalcommits.antlr;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

/** Represents the header of a commit message. */
public final class Header {
  private final String type;
  @Nullable private final String scope;
  private final boolean breaking;
  private final String description;

  public Header(String type, @Nullable String scope, boolean breaking, String description) {
    this.type = type;
    this.scope = scope;
    this.breaking = breaking;
    this.description = description;
  }

  /**
   * Returns the type of the commit.
   *
   * @return Commit type.
   */
  public String type() {
    return type;
  }

  /**
   * Returns the scope of the commit if present.
   *
   * @return Optional containing the scope or empty if not present.
   */
  public Optional<String> scope() {
    return Optional.ofNullable(scope);
  }

  /**
   * Indicates whether the commit is a breaking change.
   *
   * @return True if breaking, false otherwise.
   */
  public boolean breaking() {
    return breaking;
  }

  /**
   * Returns the description of the commit.
   *
   * @return Commit description.
   */
  public String description() {
    return description;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Header header = (Header) o;
    return breaking == header.breaking
        && Objects.equals(type, header.type)
        && Objects.equals(scope, header.scope)
        && Objects.equals(description, header.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, scope, breaking, description);
  }

  @Override
  public String toString() {
    return "Header{"
        + "type='"
        + type
        + '\''
        + ", scope="
        + (scope != null ? "'" + scope + "'" : "null")
        + ", breaking="
        + breaking
        + ", description='"
        + description
        + '\''
        + '}';
  }
}
