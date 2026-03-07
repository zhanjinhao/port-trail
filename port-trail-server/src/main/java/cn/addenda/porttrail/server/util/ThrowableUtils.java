package cn.addenda.porttrail.server.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.PrintWriter;
import java.io.StringWriter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThrowableUtils {

  public static String getThrowableStr(Throwable throwable) {
    StringWriter errors = new StringWriter();
    throwable.printStackTrace(new PrintWriter(errors));
    return errors.toString();
  }

}
