package io.github.fomin.conventionalcommits.antlr;

import static io.github.fomin.conventionalcommits.antlr.ParserUtils.parse;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class ConventionalCommitParserTest {

  @Test
  void ok_minimal() {
    assertEquals(
        ParsingResult.message(
            new Message(
                new Header("type1", null, /* breaking= */ false, "description text"),
                null,
                Collections.emptyList())),
        parse("type1: description text\n"));
  }

  @Test
  void ok_with_scope() {
    assertEquals(
        ParsingResult.message(
            new Message(
                new Header("type1", "scope1", /* breaking= */ false, "description text"),
                null,
                Collections.emptyList())),
        parse("type1(scope1): description text\n"));
  }

  @Test
  void ok_with_body() {
    assertEquals(
        ParsingResult.message(
            new Message(
                new Header("type1", null, /* breaking= */ false, "description text"),
                "body line 1\nbody line 2\n",
                Collections.emptyList())),
        parse("type1: description text\n" + "\n" + "body line 1\n" + "body line 2\n"));
  }

  @Test
  void ok_with_footers() {
    assertEquals(
        ParsingResult.message(
            new Message(
                new Header("type1", null, /* breaking= */ false, "description text"),
                null,
                asList(
                    new Footer("footer-token1", "footer value 1\n"),
                    new Footer("footer-token2", "footer value 2\n")))),
        parse(
            "type1: description text\n"
                + "\n"
                + "footer-token1: footer value 1\n"
                + "footer-token2: footer value 2\n"));
  }

  @Test
  void ok_with_scope_braking_body_and_footers() {
    assertEquals(
        ParsingResult.message(
            new Message(
                new Header("type1", "scope1", /* breaking= */ true, "description text"),
                "body line 1\nbody line 2\n",
                asList(
                    new Footer("footer-token1", "footer value 1\n"),
                    new Footer("footer-token2", "footer value 2\n")))),
        parse(
            "type1(scope1)!: description text\n"
                + "\n"
                + "body line 1\n"
                + "body line 2\n"
                + "\n"
                + "footer-token1: footer value 1\n"
                + "footer-token2: footer value 2\n"));
  }

  @Test
  void ok_body_with_empty_lines() {
    assertEquals(
        ParsingResult.message(
            new Message(
                new Header("type1", null, /* breaking= */ false, "description text"),
                "body line 1\n\nbody line 2\n",
                Collections.emptyList())),
        parse("type1: description text\n" + "\n" + "body line 1\n" + "\n" + "body line 2\n"));
  }

  @Test
  void ok_multiline_footers() {
    assertEquals(
        ParsingResult.message(
            new Message(
                new Header("type1", null, /* breaking= */ false, "description text"),
                null,
                Collections.singletonList(
                    new Footer("footer-token1", "footer line 1\nfooter line 2\n")))),
        parse(
            "type1: description text\n"
                + "\n"
                + "footer-token1: footer line 1\n"
                + "footer line 2\n"));
  }

  @Test
  void ko_missing_type() {
    assertEquals(
        ParsingResult.errors(
            asList(
                "Parsing error at line 1:11 token recognition error at: ' '",
                "Parsing error at line 1:12 mismatched input 'text' expecting {'(', COLON_SPACE, '!'}")),
        parse("description text\n"));
  }

  @Test
  void ko_missing_description() {
    assertEquals(
        ParsingResult.errors(
            asList(
                "Parsing error at line 1:13 token recognition error at: ':\\n'",
                "Parsing error at line 2:0 mismatched input '<EOF>' expecting {COLON_SPACE, '!'}")),
        parse("type1(scope1):\n"));
  }
}
