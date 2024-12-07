package io.github.fomin.conventionalcommits.rules;

import io.github.fomin.conventionalcommits.antlr.ConventionalCommitLexer;
import io.github.fomin.conventionalcommits.antlr.ConventionalCommitParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class CommitMessageValidator {
  private CommitMessageValidator() {}

  public static List<String> validate(RuleSet ruleSet, String message) {
    CharStream input = CharStreams.fromString(message);
    ErrorListener errorListener = new ErrorListener();
    ConventionalCommitLexer lexer = new ConventionalCommitLexer(input);
    lexer.removeErrorListeners();
    lexer.addErrorListener(errorListener);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ConventionalCommitParser parser = new ConventionalCommitParser(tokens);
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);
    ConventionalCommitParser.CommitMessageContext commitMessageContext = parser.commitMessage();
    List<String> errors = errorListener.getErrors();
    if (!errors.isEmpty()) {
      return errors;
    }
    return validate(ruleSet, commitMessageContext);
  }

  public static List<String> validate(
      RuleSet ruleSet, ConventionalCommitParser.CommitMessageContext commitMessageContext) {
    List<String> errors = new ArrayList<>();
    ConventionalCommitParser.MessageContext messageContext = commitMessageContext.message();
    TextRulesValidator.validate(messageContext.getText(), ruleSet.getMessage(), "message", errors);

    ConventionalCommitParser.HeaderContext headerContext = messageContext.header();
    TextRulesValidator.validate(headerContext.getText(), ruleSet.getHeader(), "header", errors);
    TextRulesValidator.validate(headerContext.type().getText(), ruleSet.getType(), "type", errors);
    ConventionalCommitParser.ScopeContext scopeContext = headerContext.scope();
    if (scopeContext != null) {
      TextRulesValidator.validate(scopeContext.getText(), ruleSet.getScope(), "scope", errors);
    }
    TextRulesValidator.validate(
        headerContext.description().getText(), ruleSet.getDescription(), "description", errors);
    ConventionalCommitParser.BodyContext bodyContext = messageContext.body();
    if (bodyContext != null) {
      TextRulesValidator.validate(bodyContext.getText(), ruleSet.getBody(), "body", errors);
    }
    Map<String, TextRules> footerRules = ruleSet.getFooters();
    Set<String> requiredFooters;
    if (footerRules != null) {
      requiredFooters =
          footerRules.entrySet().stream()
              .filter(entry -> Boolean.TRUE.equals(entry.getValue().getRequired()))
              .map(Map.Entry::getKey)
              .collect(Collectors.toCollection(TreeSet::new));
    } else {
      requiredFooters = new TreeSet<>();
    }
    for (ConventionalCommitParser.FootersContext footer : messageContext.footers()) {
      String token = footer.token().getText();
      requiredFooters.remove(token);
      TextRulesValidator.validate(
          token, ruleSet.getFooterToken(), "token of footer '" + token + "'", errors);
      TextRulesValidator.validate(
          footer.value().getText(),
          ruleSet.getFooterValue(),
          "value of footer '" + token + "'",
          errors);
      if (footerRules != null) {
        TextRules textRules = footerRules.get(token);
        TextRulesValidator.validate(
            footer.value().getText(), textRules, "value of footer '" + token + "'", errors);
      }
    }
    if (!requiredFooters.isEmpty()) {
      for (String requiredFooter : requiredFooters) {
        errors.add(
            "required footer '"
                + requiredFooter
                + "' is missing, required footers: "
                + requiredFooters);
      }
    }

    return errors;
  }
}
