package cn.addenda.porttrail.common.util;

import cn.addenda.porttrail.common.exception.PortTrailException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompressUtils {

  public static byte[] compress(byte[] bytes) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Deflater deflater = new Deflater(8);

    try {
      deflater.setInput(bytes);
      deflater.finish();
      while (!deflater.finished()) {
        byte[] buffer = new byte[2048];
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
      while (!inflater.finished()) {
        byte[] buffer = new byte[2048];
        int count = inflater.inflate(buffer);
        if (count <= 0)
          break;
        out.write(buffer, 0, count);
      }
    } catch (DataFormatException e) {
      throw new PortTrailException("解压缩失败", e);
    } finally {
      inflater.end();
    }

    return out.toByteArray();
  }

}