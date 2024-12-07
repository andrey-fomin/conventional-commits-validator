package io.github.fomin.conventionalcommits.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

class RulesParserTest {

  @Test
  void ok_empty() {
    assertEquals(
        new RuleSet(null, null, null, null, null, null, null, null, null), RulesParser.parse(""));
  }

  @Test
  void ok_all_rules_set() {
    TextRules message =
        new TextRules(10, 11, true, false, "regex1", Arrays.asList("value10", "value11"));
    TextRules header =
        new TextRules(20, 21, false, true, "regex2", Arrays.asList("value20", "value21"));
    TextRules type =
        new TextRules(30, 31, true, false, "regex3", Arrays.asList("value30", "value31"));
    TextRules scope =
        new TextRules(40, 41, false, true, "regex4", Arrays.asList("value40", "value41"));
    TextRules description =
        new TextRules(45, 46, true, false, "regexDesc", Arrays.asList("value45", "value46"));
    TextRules body =
        new TextRules(50, 51, true, false, "regex5", Arrays.asList("value50", "value51"));
    TextRules footerToken =
        new TextRules(60, 61, false, true, "regex6", Arrays.asList("value60", "value61"));
    TextRules footerValue =
        new TextRules(70, 71, true, false, "regex7", Arrays.asList("value70", "value71"));
    HashMap<String, TextRules> footers = new HashMap<>();
    footers.put(
        "footer1",
        new TextRules(80, 81, false, true, "regex8", Arrays.asList("value80", "value81")));
    footers.put(
        "footer2",
        new TextRules(90, 91, true, false, "regex9", Arrays.asList("value90", "value91")));
    assertEquals(
        new RuleSet(
            message, header, type, scope, description, body, footerToken, footerValue, footers),
        RulesParser.parse(
            "message:\n"
                + "  max-length: 10\n"
                + "  max-line-length: 11\n"
                + "  required: true\n"
                + "  forbidden: false\n"
                + "  regex: regex1\n"
                + "  values:\n"
                + "    - value10\n"
                + "    - value11\n"
                + "header:\n"
                + "  max-length: 20\n"
                + "  max-line-length: 21\n"
                + "  required: false\n"
                + "  forbidden: true\n"
                + "  regex: regex2\n"
                + "  values:\n"
                + "    - value20\n"
                + "    - value21\n"
                + "type:\n"
                + "  max-length: 30\n"
                + "  max-line-length: 31\n"
                + "  required: true\n"
                + "  forbidden: false\n"
                + "  regex: regex3\n"
                + "  values:\n"
                + "    - value30\n"
                + "    - value31\n"
                + "scope:\n"
                + "  max-length: 40\n"
                + "  max-line-length: 41\n"
                + "  required: false\n"
                + "  forbidden: true\n"
                + "  regex: regex4\n"
                + "  values:\n"
                + "    - value40\n"
                + "    - value41\n"
                + "description:\n"
                + "  max-length: 45\n"
                + "  max-line-length: 46\n"
                + "  required: true\n"
                + "  forbidden: false\n"
                + "  regex: regexDesc\n"
                + "  values:\n"
                + "    - value45\n"
                + "    - value46\n"
                + "body:\n"
                + "  max-length: 50\n"
                + "  max-line-length: 51\n"
                + "  required: true\n"
                + "  forbidden: false\n"
                + "  regex: regex5\n"
                + "  values:\n"
                + "    - value50\n"
                + "    - value51\n"
                + "footer-token:\n"
                + "  max-length: 60\n"
                + "  max-line-length: 61\n"
                + "  required: false\n"
                + "  forbidden: true\n"
                + "  regex: regex6\n"
                + "  values:\n"
                + "    - value60\n"
                + "    - value61\n"
                + "footer-value:\n"
                + "  max-length: 70\n"
                + "  max-line-length: 71\n"
                + "  required: true\n"
                + "  forbidden: false\n"
                + "  regex: regex7\n"
                + "  values:\n"
                + "    - value70\n"
                + "    - value71\n"
                + "footers:\n"
                + "  footer1:\n"
                + "    max-length: 80\n"
                + "    max-line-length: 81\n"
                + "    required: false\n"
                + "    forbidden: true\n"
                + "    regex: regex8\n"
                + "    values:\n"
                + "      - value80\n"
                + "      - value81\n"
                + "  footer2:\n"
                + "    max-length: 90\n"
                + "    max-line-length: 91\n"
                + "    required: true\n"
                + "    forbidden: false\n"
                + "    regex: regex9\n"
                + "    values:\n"
                + "      - value90\n"
                + "      - value91\n"));
  }

  @Test
  void ok_with_base_rule_set() {
    assertEquals(
        new RuleSet(
            new TextRules(null, 100, null, null, null, null),
            null,
            new TextRules(
                null,
                null,
                null,
                null,
                null,
                Arrays.asList(
                    "build", "ci", "docs", "feat", "fix", "perf", "refactor", "style", "test")),
            null,
            null,
            null,
            null,
            null,
            null),
        RulesParser.parse("base-rule-set: default\n" + "message:\n" + "  max-line-length: 100\n"));
  }
}
