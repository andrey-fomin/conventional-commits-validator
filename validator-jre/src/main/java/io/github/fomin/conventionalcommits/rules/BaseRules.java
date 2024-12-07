package io.github.fomin.conventionalcommits.rules;

import com.google.errorprone.annotations.Var;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BaseRules {

  private BaseRules() {}

  public static RuleSet getRuleSet(String name) {
    try (InputStream inputStream =
        BaseRules.class.getResourceAsStream(
            "/io/github/fomin/conventionalcommits/rules/" + name + ".yaml")) {
      if (inputStream == null) {
        throw new RuntimeException("Resource not found");
      }
      String configStr = readAllBytes(inputStream);
      return RulesParser.parse(configStr);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static String readAllBytes(InputStream inputStream) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    @Var int nRead;
    byte[] data = new byte[1024];
    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }
    return buffer.toString("UTF-8");
  }
}
