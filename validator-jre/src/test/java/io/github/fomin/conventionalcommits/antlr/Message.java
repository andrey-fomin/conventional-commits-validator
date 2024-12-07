package io.github.fomin.conventionalcommits.antlr;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

/** Represents an entire commit message, including the header, optional body, and footers. */
public final class Message {
  private final Header header;
  @Nullable private final String body;
  private final List<Footer> footers;

  public Message(Header header, @Nullable String body, List<Footer> footers) {
    this.header = header;
    this.body = body;
    this.footers = footers;
  }

  /**
   * Returns the header of the commit message.
   *
   * @return Commit header.
   */
  public Header header() {
    return header;
  }

  /**
   * Returns the body of the commit message if present.
   *
   * @return Optional containing the body or empty if not present.
   */
  public Optional<String> body() {
    return Optional.ofNullable(body);
  }

  /**
   * Returns the list of footers in the commit message.
   *
   * @return List of footers.
   */
  public List<Footer> footers() {
    return footers;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Message message = (Message) o;
    return Objects.equals(header, message.header)
        && Objects.equals(body, message.body)
        && Objects.equals(footers, message.footers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(header, body, footers);
  }

  @Override
  public String toString() {
    return "Message{"
        + "header="
        + header
        + ", body="
        + (body != null ? "'" + body + "'" : "empty")
        + ", footers="
        + footers
        + '}';
  }
}
