package io.github.fomin.conventionalcommits.rules;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ErrorListener extends BaseErrorListener {

  private final ArrayList<String> errors = new ArrayList<>();

  @Override
  public void syntaxError(
      Recognizer<?, ?> recognizer,
      Object offendingSymbol,
      int line,
      int charPositionInLine,
      String msg,
      RecognitionException e) {
    errors.add("Parsing error at line " + line + ":" + charPositionInLine + " " + msg);
  }

  public List<String> getErrors() {
    return errors;
  }
}
