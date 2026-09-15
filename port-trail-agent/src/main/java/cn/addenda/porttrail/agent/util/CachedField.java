package cn.addenda.porttrail.agent.util;

import java.lang.reflect.Field;

/**
 * 懒加载并缓存字段查找结果。
 *
 * <p>拦截器需要反射读取被增强对象上的字段，字段查找只做一次、结果缓存在这里。
 * 命中缓存后只花一次 volatile 读；未命中时才进入锁，并做双重检查保证只查找一次。
 *
 * <p>不用「{@code static synchronized} getter」的原因：那会让每次调用都去抢类级 monitor，
 * 同一拦截器的多个 getter 还会共用同一个锁；在多线程热路径上会退化成互斥量排队。
 */
public class CachedField {

  private final String fieldName;

  private final String fallbackFieldName;

  private volatile Field field;

  public CachedField(String fieldName) {
    this(fieldName, null);
  }

  /**
   * @param fallbackFieldName 备用字段名，两个都找不到才返回 null。用于字段名随第三方版本变化的场景。
   */
  public CachedField(String fieldName, String fallbackFieldName) {
    this.fieldName = fieldName;
    this.fallbackFieldName = fallbackFieldName;
  }

  /**
   * 取字段。找不到（或查找过程出错）时返回 null，调用方自行 fallback。
   */
  public Field get(Object target) {
    Field result = field;
    if (result == null) {
      synchronized (this) {
        result = field;
        if (result == null) {
          result = ReflectionUtils.getField(target, fieldName);
          if (result == null && fallbackFieldName != null) {
            result = ReflectionUtils.getField(target, fallbackFieldName);
          }
          field = result;
        }
      }
    }
    return result;
  }

}
