package io.github.fomin.conventionalcommits.rules;

import com.google.errorprone.annotations.Var;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.yaml.snakeyaml.Yaml;

public final class RulesParser {
  private RulesParser() {}

  public static RuleSet parse(String configStr) {
    Yaml yaml = new Yaml();
    Object config = yaml.load(configStr);
    if (config == null) {
      return new RuleSet(null, null, null, null, null, null, null, null, null);
    }
    if (!(config instanceof Map)) {
      throw new IllegalArgumentException("Config should be an object");
    }
    Map<?, ?> configMap = (Map<?, ?>) config;

    @Var RuleSet baseRuleSet = null;
    @Var TextRules message = null;
    @Var TextRules header = null;
    @Var TextRules type = null;
    @Var TextRules scope = null;
    @Var TextRules description = null;
    @Var TextRules body = null;
    @Var TextRules footerToken = null;
    @Var TextRules footerValue = null;
    @Var Map<String, TextRules> footers = null;

    for (Map.Entry<?, ?> configEntry : configMap.entrySet()) {
      Object value = configEntry.getValue();
      String key = (String) configEntry.getKey();
      switch (key) {
        case "base-rule-set":
          baseRuleSet = parseBaseRuleSet(value);
          break;
        case "message":
          message = parseTextRules("message", value);
          break;
        case "header":
          header = parseTextRules("header", value);
          break;
        case "type":
          type = parseTextRules("type", value);
          break;
        case "scope":
          scope = parseTextRules("scope", value);
          break;
        case "description":
          description = parseTextRules("description", value);
          break;
        case "body":
          body = parseTextRules("body", value);
          break;
        case "footer-token":
          footerToken = parseTextRules("footer-token", value);
          break;
        case "footer-value":
          footerValue = parseTextRules("footer-value", value);
          break;
        case "footers":
          footers = parseFooters(value);
          break;
        default:
          throw new IllegalArgumentException("Unexpected property: " + key);
      }
    }

    RuleSet targetRuleSet =
        new RuleSet(
            message, header, type, scope, description, body, footerToken, footerValue, footers);
    return RuleSetMerger.merge(baseRuleSet, targetRuleSet);
  }

  private static RuleSet parseBaseRuleSet(Object value) {
    if (!(value instanceof String)) {
      throw new IllegalArgumentException("base-rule-set must be a string");
    }
    String baseRuleSetName = (String) value;
    return BaseRules.getRuleSet(baseRuleSetName);
  }

  private static Map<String, TextRules> parseFooters(Object config) {
    if (!(config instanceof Map)) {
      throw new IllegalArgumentException("footers must be an object");
    }
    Map<?, ?> configMap = (Map<?, ?>) config;
    Map<String, TextRules> footers = new HashMap<>();
    for (Map.Entry<?, ?> configEntry : configMap.entrySet()) {
      String footerToken = (String) configEntry.getKey();
      TextRules textRules;
      try {
        textRules = parseTextRules(footerToken, configEntry.getValue());
      } catch (Exception e) {
        throw new IllegalArgumentException("Error parsing property footers", e);
      }
      footers.put(footerToken, textRules);
    }
    return footers;
  }

  private static TextRules parseTextRules(String field, Object config) {
    try {
      return parseTextRulesImpl(config);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error parsing property " + field, e);
    }
  }

  private static TextRules parseTextRulesImpl(Object config) {
    if (!(config instanceof Map)) {
      throw new IllegalArgumentException("Expected object value");
    }

    Map<?, ?> configMap = (Map<?, ?>) config;
    @Var Integer maxLength = null;
    @Var Integer maxLineLength = null;
    @Var Boolean required = null;
    @Var Boolean forbidden = null;
    @Var String regex = null;
    @Var List<String> values = null;

    for (Map.Entry<?, ?> configEntry : configMap.entrySet()) {
      String key = (String) configEntry.getKey();
      switch (key) {
        case "max-length":
          maxLength = (Integer) configEntry.getValue();
          break;
        case "max-line-length":
          maxLineLength = (Integer) configEntry.getValue();
          break;
        case "required":
          required = (Boolean) configEntry.getValue();
          break;
        case "forbidden":
          forbidden = (Boolean) configEntry.getValue();
          break;
        case "regex":
          regex = (String) configEntry.getValue();
          break;
        case "values":
          List<?> listValue = (List<?>) configEntry.getValue();
          values =
              listValue.stream()
                  .map(
                      item -> {
                        if (!(item instanceof String)) {
                          throw new IllegalArgumentException("Expected string value");
                        }
                        return (String) item;
                      })
                  .collect(Collectors.toList());
          break;
        default:
          throw new IllegalArgumentException("Unexpected property: " + key);
      }
    }

    return new TextRules(maxLength, maxLineLength, required, forbidden, regex, values);
  }
}
