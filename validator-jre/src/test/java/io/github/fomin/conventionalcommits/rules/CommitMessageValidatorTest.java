package io.github.fomin.conventionalcommits.rules;

import static io.github.fomin.conventionalcommits.rules.CommitMessageValidator.validate;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CommitMessageValidatorTest {

  @Test
  void ok_minimal() {
    RuleSet ruleSet = RulesParser.parse("");
    List<String> errors = validate(ruleSet, "type1: description text\n");
    assertTrue(errors.isEmpty());
  }

  @Test
  void ko_parsing_error() {
    RuleSet ruleSet = RulesParser.parse("");
    List<String> errors = validate(ruleSet, "description text\n");
    assertEquals(
        asList(
            "Parsing error at line 1:11 token recognition error at: ' '",
            "Parsing error at line 1:12 mismatched input 'text' expecting {'(', COLON_SPACE, '!'}"),
        errors);
  }

  @Test
  void ko_too_wide_message() {
    RuleSet ruleSet =
        RulesParser.parse(
            "message:\n" //
                + "  max-length: 10\n" //
                + "  max-line-length: 10\n" //
            );
    List<String> errors = validate(ruleSet, "type1: description text\n");
    assertEquals(
        asList("message length 24 is greater than 10", "message line length 23 is greater than 10"),
        errors);
  }

  @Test
  void ko_unknown_footer() {
    RuleSet ruleSet =
        RulesParser.parse(
            "footer-token:\n" //
                + "  values: [footer1, footer2]\n" //
            );
    List<String> errors =
        validate(
            ruleSet,
            "type1: description text\n" //
                + "\n" //
                + "footer1: value\n" //
                + "footer2: value\n" //
                + "footer3: value\n" //
            );
    assertEquals(
        singletonList(
            "token of footer 'footer3' has invalid value, value footer3 is not in [footer1, footer2]"),
        errors);
  }

  @Test
  void ko_missing_required_footer() {
    RuleSet ruleSet =
        RulesParser.parse(
            "footers:\n" //
                + "  footer1:\n" //
                + "    required: true\n" //
            );
    List<String> errors =
        validate(
            ruleSet,
            "type1: description text\n" //
                + "\n" //
                + "footer2: value\n" //
            );
    assertEquals(
        singletonList("required footer 'footer1' is missing, required footers: [footer1]"), errors);
  }
}
