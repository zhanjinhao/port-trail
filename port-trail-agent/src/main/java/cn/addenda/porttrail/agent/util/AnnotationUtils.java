package cn.addenda.porttrail.agent.util;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.common.util.StringUtils;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static cn.addenda.porttrail.agent.transform.interceptor.job.xxl.jobhandler.XxlJobHandlerInterceptorPointDefine.JOB_HANDLER_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnnotationUtils {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(AnnotationUtils.class);

  public static Annotation getAnnotation(Method method, String annotationName) {
    if (method == null) {
      throw new IllegalArgumentException("method can not be null.");
    }
    if (annotationName == null) {
      throw new IllegalArgumentException("annotationName can not be null.");
    }
    Annotation[] annotations = method.getAnnotations();
    for (Annotation annotation : annotations) {
      if (annotationName.equals(annotation.annotationType().getName())) {
        return annotation;
      }
    }
    return null;
  }

  public static Annotation getAnnotation(Class<?> aClass, String annotationName) {
    if (aClass == null) {
      throw new IllegalArgumentException("class can not be null.");
    }
    if (annotationName == null) {
      throw new IllegalArgumentException("annotationName can not be null.");
    }
    Annotation[] annotations = aClass.getAnnotations();
    for (Annotation annotation : annotations) {
      if (JOB_HANDLER_NAME.equals(annotation.annotationType().getName())) {
        return annotation;
      }
    }
    return null;
  }

  public static Object getAttributeValue(Annotation annotation, String attributeName) {
    if (annotation == null || !StringUtils.hasText(attributeName)) {
      return null;
    }
    try {
      Method method = annotation.annotationType().getDeclaredMethod(attributeName);
      method.setAccessible(true);
      return method.invoke(annotation);
    } catch (Exception ex) {
      log.error("cannot get value of [{}] from [{}].", attributeName, annotation);
      return null;
    }
  }

}
