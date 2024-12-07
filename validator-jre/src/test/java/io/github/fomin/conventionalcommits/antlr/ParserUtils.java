package io.github.fomin.conventionalcommits.antlr;

import io.github.fomin.conventionalcommits.rules.ErrorListener;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class ParserUtils {

  private ParserUtils() {}

  public static ParsingResult parse(String messageStr) {
    CharStream input = CharStreams.fromString(messageStr);
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
      return ParsingResult.errors(errors);
    }
    Message message = convert(commitMessageContext);
    return ParsingResult.message(message);
  }

  private static Message convert(
      ConventionalCommitParser.CommitMessageContext commitMessageContext) {
    ConventionalCommitParser.MessageContext messageContext = commitMessageContext.message();
    ConventionalCommitParser.HeaderContext headerContext = messageContext.header();
    ConventionalCommitParser.ScopeContext scopeContext = headerContext.scope();
    Header header =
        new Header(
            headerContext.type().getText(),
            scopeContext != null ? scopeContext.getText() : null,
            headerContext.breaking() != null,
            headerContext.description().getText());
    ConventionalCommitParser.BodyContext bodyContext = messageContext.body();
    String body = bodyContext != null ? bodyContext.getText() : null;
    List<Footer> footers =
        messageContext.footers().stream()
            .map(
                footerContext ->
                    new Footer(footerContext.token().getText(), footerContext.value().getText()))
            .collect(Collectors.toList());
    return new Message(header, body, footers);
  }
}
