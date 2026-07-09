package cn.addenda.porttrail.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.PrintWriter;
import java.io.StringWriter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThrowableUtils {

  public static String getThrowableStr(Throwable throwable) {
    if (throwable == null) {
      return null;
    }
    StringWriter errors = new StringWriter();
    throwable.printStackTrace(new PrintWriter(errors));
    return errors.toString();
  }

}
