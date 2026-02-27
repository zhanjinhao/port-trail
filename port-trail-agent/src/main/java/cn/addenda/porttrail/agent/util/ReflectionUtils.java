package cn.addenda.porttrail.agent.util;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionUtils {

  private static final PortTrailLogger log = AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(ReflectionUtils.class);

  public static Field getField(Object o, String fieldName) {
    if (o == null || fieldName == null || fieldName.isEmpty()) {
      log.error("can not execute getField() with param[{},{}]", o, fieldName);
      return null;
    }
    Class<?> clazz = o.getClass();
    try {
      return clazz.getDeclaredField(fieldName);
    } catch (Exception e) {
      log.error("can not get field [{}] from [{}].", fieldName, o.getClass(), e);
      return null;
    }
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
