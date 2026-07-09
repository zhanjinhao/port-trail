package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.exception.PortTrailException;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import io.lettuce.core.protocol.DecoratedCommand;
import io.lettuce.core.protocol.RedisCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LettuceRedisCommandUtils {

  /**
   * 每个线程复用 CharsetDecoder，避免重复创建。
   * CharsetDecoder 不是线程安全的，所以使用 ThreadLocal。
   */
  private static final ThreadLocal<CharsetDecoder> UTF8_DECODER = ThreadLocal.withInitial(
          () -> StandardCharsets.UTF_8.newDecoder()
                  .onMalformedInput(CodingErrorAction.REPORT)
                  .onUnmappableCharacter(CodingErrorAction.REPORT)
  );

  public static RedisCommand<?, ?, ?> resolveCommand(RedisCommand<?, ?, ?> command) {
    if (command instanceof DecoratedCommand) {
      return resolveCommand(((DecoratedCommand<?, ?, ?>) command).getDelegate());
    }
    return command;
  }

  /**
   * 将字节数组转换为可读字符串：
   * 1. 如果是 JDK 序列化的字节数组（魔数 0xACED0005），尝试反序列化
   * 2. 如果是 GZIP（魔数 0x1F8B）或 zlib/Deflate（0x78xx）压缩数据，尝试解压后递归处理
   * 3. 否则尝试 UTF-8 解码
   * 本方法不捕获异常，由调用方决定 fallback 策略。
   */
  public static String bytesToString(byte[] bytes)
          throws CharacterCodingException {
    if (bytes == null) {
      return null;
    }
    // JDK 序列化
    if (bytes.length >= 4
            && bytes[0] == (byte) 0xAC && bytes[1] == (byte) 0xED
            && bytes[2] == (byte) 0x00 && bytes[3] == (byte) 0x05) {
      Object deserialized = JdkSerializationUtils.deserialize(bytes);
      if (deserialized == null) {
        return null;
      }
      if (deserialized instanceof String) {
        return (String) deserialized;
      }
      if (deserialized instanceof CharSequence) {
        return deserialized.toString();
      }
      if (deserialized instanceof byte[]) {
        return bytesToString((byte[]) deserialized);
      }
      return LinkFacade.toStr(deserialized);
    }
    // GZIP 解压
    if (bytes.length >= 2
            && (bytes[0] & 0xFF) == 0x1F && (bytes[1] & 0xFF) == 0x8B) {
      return bytesToString(CompressUtils.gzipDecompress(bytes));
    }
    // Deflate/zlib 解压：首字节 0x78，且 (b[0]*256 + b[1]) % 31 == 0（RFC 1950 FCHECK）
    if (bytes.length >= 2
            && (bytes[0] & 0xFF) == 0x78
            && (((bytes[0] & 0xFF) * 256 + (bytes[1] & 0xFF)) % 31 == 0)) {
      try {
        return bytesToString(CompressUtils.decompress(bytes));
      } catch (PortTrailException e) {
        // UTF-8 解码
        CharsetDecoder decoder = UTF8_DECODER.get();
        try {
          return decoder.decode(ByteBuffer.wrap(bytes)).toString();
        } catch (CharacterCodingException s) {
          decoder.reset();
          throw s;
        }
      }
    }
    // UTF-8 解码
    CharsetDecoder decoder = UTF8_DECODER.get();
    decoder.reset();
    try {
      return decoder.decode(ByteBuffer.wrap(bytes)).toString();
    } catch (CharacterCodingException s) {
      decoder.reset();
      throw s;
    }
  }

}
