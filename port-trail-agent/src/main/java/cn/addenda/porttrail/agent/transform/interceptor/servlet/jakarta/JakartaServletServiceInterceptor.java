//package cn.addenda.porttrail.agent.transform.interceptor.servlet.jakarta;
//
//import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
//import cn.addenda.porttrail.agent.transform.OverrideCallable;
//import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
//import cn.addenda.porttrail.common.entrypoint.EntryPoint;
//import cn.addenda.porttrail.common.entrypoint.EntryPointContext;
//import cn.addenda.porttrail.common.entrypoint.EntryPointType;
//import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
//import net.bytebuddy.implementation.bind.annotation.*;
//
//import jakarta.servlet.http.HttpServletRequest;
//import java.lang.reflect.Method;
//import java.nio.charset.StandardCharsets;
//
//public class JakartaServletServiceInterceptor implements Interceptor {
//
//  private static final PortTrailLogger PORT_TRAIL_LOGGER =
//          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(JakartaServletServiceInterceptor.class);
//
//  /**
//   * 被@RuntimeType标注的方法就是被委托的方法
//   */
//  @RuntimeType
//  public Object intercept(
//          // byteBuddy会在运行期间给被注定注解修饰的方法参数进行赋值:
//
//          // 当前被拦截的、动态生成的那个对象
//          @This Object targetObj,
//          // 被调用的原始方法
//          @Origin Method targetMethod,
//          // 被拦截的方法参数
//          @AllArguments Object[] targetMethodArgs,
//          // 当前被拦截的、动态生成的那个对象的父类对象
//          @Super Object concurrentBag,
//          // 用于调用父类的方法。
//          @Morph OverrideCallable zuper
//  ) throws Exception {
//    PORT_TRAIL_LOGGER.info("{}.class.classLoader = {}.", JakartaServletServiceInterceptor.class, this.getClass().getClassLoader());
//
//    HttpServletRequest request = (HttpServletRequest) targetMethodArgs[0];
//
//    EntryPoint entryPoint = EntryPoint.of(
//            EntryPointType.HTTP_SERVER_SPRINGBOOT2, request.getRequestURI());
//    EntryPointContext.addEntryPoint(entryPoint);
//
//    try {
//      JakartaContentCachingRequestWrapper requestWrapper = new JakartaContentCachingRequestWrapper(request);
//      targetMethodArgs[0] = requestWrapper;
//      Object call = zuper.call(targetMethodArgs);
//
//      String resquestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
//
//      System.out.println(JakartaServletServiceInterceptor.class + ":" + resquestBody);
//      return call;
//    } finally {
//      EntryPointContext.removeEntryPoint(entryPoint);
//    }
//
//  }
//
//
//  @Override
//  public boolean ifOverride() {
//    return true;
//  }
//
//}
