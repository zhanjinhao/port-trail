package cn.addenda.porttrail.server.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Collection;

/**
 * 去重记录的去重键计算。
 *
 * <p>去重键 = SHA-256(日期 + systemCode + serviceName + env + detail集合 + SQL)。
 * detail 集合来自 entry_point snapshot，按调用链顺序排列（外层入口在前），
 * 顺序是确定的，且顺序本身是区分上下文的信息。
 * <p><b>不排序、不去重</b>：
 * <ul>
 *   <li>顺序有意义：{@code [A, B]} 与 {@code [B, A]} 是不同的调用链；</li>
 *   <li>重复有意义：同一个 entry point 在调用链里出现多次（重复调用、递归）时，
 *       出现次数本身区分上下文，{@code [/a, /b]} 与 {@code [/a, /a, /b]} 必须算出不同的键。</li>
 * </ul>
 *
 * @author addenda
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyzeDedupHandlerKeySupport {

  private static final String SEPARATOR = "|";

  private static final char[] HEX = "0123456789abcdef".toCharArray();

  /**
   * detail 集合按入参顺序逗号拼接，<b>不排序、不去重</b>；null 元素忽略。入参为 null 时返回空串。
   */
  public static String joinDetails(Collection<String> details) {
    if (details == null || details.isEmpty()) {
      return "";
    }
    StringBuilder builder = new StringBuilder();
    boolean first = true;
    for (String detail : details) {
      if (detail == null) {
        continue;
      }
      if (!first) {
        builder.append(',');
      }
      builder.append(detail);
      first = false;
    }
    return builder.toString();
  }

  /**
   * 去重键。前像按 {@code 日期|systemCode|serviceName|env|detail集合|SQL} 拼接后做 SHA-256，
   * 与参数顺序一致。
   */
  public static String computeDedupKey(LocalDate statDate, String systemCode, String serviceName,
                                       String env, Collection<String> details, String parameterizedSql) {
    String raw = (statDate == null ? "" : statDate.toString())
            + SEPARATOR + nullToEmpty(systemCode)
            + SEPARATOR + nullToEmpty(serviceName)
            + SEPARATOR + nullToEmpty(env)
            + SEPARATOR + joinDetails(details)
            + SEPARATOR + nullToEmpty(parameterizedSql);
    return sha256Hex(raw);
  }

  public static String computeSqlHash(String parameterizedSql) {
    return sha256Hex(nullToEmpty(parameterizedSql));
  }

  private static String nullToEmpty(String s) {
    return s == null ? "" : s;
  }

  /**
   * SHA-256 输出 64 位小写十六进制，正好匹配 {@code VARCHAR(64)} 唯一索引。
   */
  private static String sha256Hex(String content) {
    MessageDigest messageDigest;
    try {
      // MessageDigest 不是线程安全的，且没有可复用的无状态实现，所以每次新建
      messageDigest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      // 标准 JRE 一定支持 SHA-256
      throw new IllegalStateException("SHA-256 is not supported.", e);
    }

    // 32 字节的二进制摘要，不是可打印字符串，需要手工转成十六进制
    byte[] digest = messageDigest.digest(content.getBytes(StandardCharsets.UTF_8));

    // 1 字节 = 8 bit = 2 个十六进制位，所以 32 字节对应 64 个字符
    char[] chars = new char[digest.length * 2];
    for (int i = 0; i < digest.length; i++) {
      // byte 在 Java 里是有符号的（-128~127），0xA5 会存成 -91，
      // 直接转 int 会符号扩展成 0xFFFFFFA5；& 0xFF 只保留低 8 位，得到无符号值 0~255
      int b = digest[i] & 0xFF;
      // 高 4 位：右移 4 位后落在 0~15，查表得到高位的十六进制字符
      chars[i * 2] = HEX[b >>> 4];
      // 低 4 位：& 0x0F 保留低 4 位，查表得到低位的十六进制字符
      chars[i * 2 + 1] = HEX[b & 0x0F];
    }
    return new String(chars);
  }

}
