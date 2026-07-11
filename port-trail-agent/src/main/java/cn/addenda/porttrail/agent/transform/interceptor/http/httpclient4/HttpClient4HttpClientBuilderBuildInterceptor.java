package cn.addenda.porttrail.agent.transform.interceptor.http.httpclient4;

import cn.addenda.porttrail.agent.AgentPackage;
import cn.addenda.porttrail.agent.PortTrailAgentStartException;
import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.writer.httpclient.AgentHttpClientWriter;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.writer.HttpClientWriter;
import net.bytebuddy.implementation.bind.annotation.*;
import org.apache.http.impl.client.HttpClientBuilder;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.concurrent.Callable;

public class HttpClient4HttpClientBuilderBuildInterceptor implements Interceptor {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(HttpClient4HttpClientBuilderBuildInterceptor.class);

  private final HttpClientWriter httpClientWriter;

  private static final int requestMaxBodyLength;

  private static final int responseMaxBodyLength;

  public HttpClient4HttpClientBuilderBuildInterceptor() {
    this.httpClientWriter = AgentHttpClientWriter.getInstance();
  }

  static {
    requestMaxBodyLength = initRequestMaxBodyLength();
    responseMaxBodyLength = initResponseMaxBodyLength();
  }

  private static int initRequestMaxBodyLength() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    // in kb
    String property = agentProperties.getProperty("httpClientWriter.request.maxBodyLength");
    try {
      return Integer.parseInt(property) * 1024;
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载httpClientWriter.request.maxBodyLength异常，配置值为：%s", property), e);
    }
  }

  private static int initResponseMaxBodyLength() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    String property = agentProperties.getProperty("httpClientWriter.response.maxBodyLength");
    try {
      // in kb
      return Integer.parseInt(property) * 1024;
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载httpClientWriter.response.maxBodyLength异常，配置值为：%s", property), e);
    }
  }

  /**
   * 被@RuntimeType标注的方法就是被委托的方法
   */
  @RuntimeType
  public Object intercept(
          // byteBuddy会在运行期间给被注定注解修饰的方法参数进行赋值:

          // 当前被拦截的、动态生成的那个对象
          @This Object targetObj,
          // 被调用的原始方法
          @Origin Method targetMethod,
          // 被拦截的方法参数
          @AllArguments Object[] targetMethodArgs,
          // 当前被拦截的、动态生成的那个对象的父类对象
          @Super Object druidDataSource,
          // 用于调用父类的方法。
          @SuperCall Callable<?> zuper
  ) throws Exception {

    log.info("TargetObj's class is [{}] and it's classloader is [{}].", targetObj.getClass(), targetObj.getClass().getClassLoader());

    HttpClientBuilder httpClientBuilder = (HttpClientBuilder) targetObj;
    httpClientBuilder.addInterceptorLast(new PortTrailHttpRequestInterceptor(requestMaxBodyLength, httpClientWriter));
    httpClientBuilder.addInterceptorLast(new PortTrailHttpResponseInterceptor(responseMaxBodyLength, httpClientWriter));

    return zuper.call();
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

}
