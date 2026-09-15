package cn.addenda.porttrail.agent.util;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionUtils {

  private static final PortTrailLogger log = AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(ReflectionUtils.class);

  /**
   * 沿类继承链查找字段：字段可能声明在父类上。
   */
  public static Field getField(Object o, String fieldName) {
    if (o == null || fieldName == null || fieldName.isEmpty()) {
      log.error("can not execute getField() with param[{},{}]", o, fieldName);
      return null;
    }
    for (Class<?> clazz = o.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
      try {
        return clazz.getDeclaredField(fieldName);
      } catch (NoSuchFieldException ignored) {
        // 继续沿继承链往上找
      } catch (Exception e) {
        log.error("can not get field [{}] from [{}].", fieldName, o.getClass(), e);
        return null;
      }
    }
    log.error("can not get field [{}] from [{}].", fieldName, o.getClass());
    return null;
  }

  public static Object getFieldValueFromObject(Object object, Field field) {
    try {
      field.setAccessible(true);
      return field.get(object);
    } catch (Exception e) {
      log.error("can not get value of field [{}] from [{}].", field, object, e);
      return null;
    }
  }

}
