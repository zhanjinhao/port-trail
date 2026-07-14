package cn.addenda.porttrail.common.util;

import cn.addenda.porttrail.common.exception.PortTrailException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompressUtils {

  public static byte[] compress(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    // todo 可配置
    Deflater deflater = new Deflater(1);

    try {
      deflater.setInput(bytes);
      deflater.finish();
      byte[] buffer = new byte[512];
      while (!deflater.finished()) {
        int count = deflater.deflate(buffer);
        out.write(buffer, 0, count);
      }
    } finally {
      deflater.end();
    }
    return out.toByteArray();
  }

  public static byte[] decompress(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Inflater inflater = new Inflater();

    try {
      inflater.setInput(bytes);
      byte[] buffer = new byte[512];
      while (!inflater.finished()) {
        int count = inflater.inflate(buffer);
        if (count > 0) {
          out.write(buffer, 0, count);
          continue;
        }
        // count == 0：inflate 本次未产出数据，需区分具体原因，
        // 既要正确处理合法的空内容流（原始数据为空），也要避免死循环。
        if (inflater.finished()) {
          // 流已正常结束（例如原始数据为空）
          break;
        }
        if (inflater.needsDictionary()) {
          throw new PortTrailException(
                  String.format("Deflate解压异常(需要预置字典):inflater#finished()=%s, inflater#needsInput()=%s, inflater#needsDictionary()=%s.",
                          inflater.finished(), inflater.needsInput(), inflater.needsDictionary()));
        }
        if (inflater.needsInput()) {
          // 已一次性setInput全部数据仍需要更多输入，说明数据不完整
          throw new PortTrailException(
                  String.format("Deflate解压异常(数据不完整):inflater#finished()=%s, inflater#needsInput()=%s, inflater#needsDictionary()=%s.",
                          inflater.finished(), inflater.needsInput(), inflater.needsDictionary()));
        }
        // 兜底：既未结束、又不需要输入/字典却产出0字节，跳出避免死循环
        throw new PortTrailException(
                String.format("Deflate解压异常(未知异常):inflater#finished()=%s, inflater#needsInput()=%s, inflater#needsDictionary()=%s.",
                        inflater.finished(), inflater.needsInput(), inflater.needsDictionary()));
      }
    } catch (DataFormatException e) {
      throw new PortTrailException("Deflate解压失败", e);
    } finally {
      inflater.end();
    }

    return out.toByteArray();
  }

  public static byte[] gzipCompress(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (GZIPOutputStream gos = new GZIPOutputStream(out)) {
      gos.write(bytes);
    } catch (Exception e) {
      throw new PortTrailException("GZIP压缩失败", e);
    }
    return out.toByteArray();
  }

  public static byte[] gzipDecompress(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
      byte[] buffer = new byte[2048];
      int len;
      while ((len = gis.read(buffer)) != -1) {
        out.write(buffer, 0, len);
      }
    } catch (Exception e) {
      throw new PortTrailException("GZIP解压失败", e);
    }
    return out.toByteArray();
  }

}