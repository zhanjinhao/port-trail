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
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Inflater inflater = new Inflater();

    try {
      inflater.setInput(bytes);
      byte[] buffer = new byte[512];
      while (!inflater.finished()) {
        int count = inflater.inflate(buffer);
        if (count <= 0) {
          throw new PortTrailException(
                  String.format("Deflate解压异常: 数据不完整, inflater.finished=%s, inflater.needsInput=%s, inflater.needsDictionary=%s",
                          inflater.finished(), inflater.needsInput(), inflater.needsDictionary()));
        }
        out.write(buffer, 0, count);
      }
    } catch (DataFormatException e) {
      throw new PortTrailException("Deflate解压失败", e);
    } finally {
      inflater.end();
    }

    return out.toByteArray();
  }

  public static byte[] gzipCompress(byte[] bytes) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (GZIPOutputStream gos = new GZIPOutputStream(out)) {
      gos.write(bytes);
    } catch (Exception e) {
      throw new PortTrailException("GZIP压缩失败", e);
    }
    return out.toByteArray();
  }

  public static byte[] gzipDecompress(byte[] bytes) {
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