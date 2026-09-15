package cn.addenda.porttrail.agent.transform.interceptor;

import cn.addenda.porttrail.agent.util.AnnotationUtils;
import cn.addenda.porttrail.agent.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Interceptor的概念是借鉴skywalking。所以没有使用自定义类加载器来执行Interceptor。因为我的Agent暂不需要这么复杂。
 */
public interface Interceptor {

  boolean ifOverride();

  /**
   * 被拦截目标的标识，格式：类名#方法名。
   * <p>
   * 必须是static：{@link AbstractEntryPointInterceptor}是类而非Interceptor，无法调用本接口的default方法。
   */
  static String assembleDetail(Object targetObj, Method targetMethod) {
    return targetObj.getClass().getName() + "#" + targetMethod.getName();
  }

  default Object getFieldValueFromObject(Object o, Field field, Object defaultValue) {
    if (o == null) {
      return defaultValue;
    }
    if (field == null) {
      return defaultValue;
    }
    Object fieldValueFromObject = ReflectionUtils.getFieldValueFromObject(o, field);
    return fieldValueFromObject == null ? defaultValue : fieldValueFromObject;
  }

  default Object getFieldValueFromObject(Object o, Field field) {
    return getFieldValueFromObject(o, field, null);
  }

  default Object getAttributeValue(Annotation annotation, String attributeName, Object defaultValue) {
    if (annotation == null) {
      return defaultValue;
    }
    if (attributeName == null) {
      return defaultValue;
    }
    Object attributeValueFromObject = AnnotationUtils.getAttributeValue(annotation, attributeName);
    return attributeValueFromObject == null ? defaultValue : attributeValueFromObject;
  }

  default Object getAttributeValue(Annotation annotation, String attributeName) {
    return getAttributeValue(annotation, attributeName, null);
  }

}
