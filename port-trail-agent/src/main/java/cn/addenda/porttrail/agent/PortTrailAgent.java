package cn.addenda.porttrail.agent;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.AgentTransformer;
import cn.addenda.porttrail.agent.transform.InterceptorPointDefineGather;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.datasource.druid.DruidDruidDataSourceInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.datasource.hikari.HikariConcurrentBagInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.driver.mysql.MySQLDriverInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.driver.oracle.OracleDriverInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.http.httpclient4.HttpClient4HttpClientBuilderInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.jdbc.PortTrailStatementInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.job.xxl.glue.XxlGlueInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.job.xxl.jobhandler.XxlJobHandlerInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.job.xxl.method.XxlMethodInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.job.xxl.script.XxlScriptInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.mybatis.MybatisExecutorInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.channelwriter.LettuceChannelWriterInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.command.LettuceCommandInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.peer.LettuceDefaultEndpointInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.server.jetty.JettyServerInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.server.tomcat.TomcatAbstractProtocolInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.servlet.javax.JavaxServletInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.task.TaskInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.tx.transactional.SpringTransactionalInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.tx.transactionhepler.SpringTransactionHelperInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.tx.transactiontemplate.SpringTransactionTemplateInterceptorPointDefine;
import cn.addenda.porttrail.common.util.StringUtils;
import cn.addenda.porttrail.common.util.UuidUtils;
import cn.addenda.porttrail.infrastructure.context.AgentContext;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;

import static cn.addenda.porttrail.agent.transform.interceptor.tx.transactiontemplate.SpringTransactionTemplateInterceptorPointDefine.TRANSACTION_TEMPLATE_NAME;
import static net.bytebuddy.matcher.ElementMatchers.nameContains;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;

public class PortTrailAgent {

  private static final String INTERCEPTOR_POINT_DEFINE_IMPL_KEY = "interceptorPointDefine.impl";

  /**
   * 内置的拦截点定义白名单。interceptorPointDefine.impl缺失时使用这份清单，与历史行为保持一致。
   * <p>
   * 顺序即注册顺序：InterceptorPointDefineGather按插入顺序保存同一个类名下的多个define，
   * matcher相同时后注册的会覆盖先注册的。
   */
  private static final List<Class<? extends InterceptorPointDefine>> DEFAULT_INTERCEPTOR_POINT_DEFINE_CLASS_LIST =
          Collections.unmodifiableList(Arrays.asList(
                  // Server层拦截
                  TomcatAbstractProtocolInterceptorPointDefine.class,
                  JettyServerInterceptorPointDefine.class,
                  // 入口层拦截（Servlet/Task/XxlJob）
                  JavaxServletInterceptorPointDefine.class,
                  // JakartaServletInterceptorPointDefine整个类被注释掉，写进配置会导致agent启动失败
                  TaskInterceptorPointDefine.class,
                  XxlJobHandlerInterceptorPointDefine.class,
                  XxlMethodInterceptorPointDefine.class,
                  XxlGlueInterceptorPointDefine.class,
                  XxlScriptInterceptorPointDefine.class,
                  // Tx层拦截
                  SpringTransactionalInterceptorPointDefine.class,
                  SpringTransactionHelperInterceptorPointDefine.class,
                  SpringTransactionTemplateInterceptorPointDefine.class,
                  // ORM层拦截
                  MybatisExecutorInterceptorPointDefine.class,
                  // DataSource层拦截
                  HikariConcurrentBagInterceptorPointDefine.class,
                  DruidDruidDataSourceInterceptorPointDefine.class,
                  // JDBC层拦截
                  PortTrailStatementInterceptorPointDefine.class,
                  // DB层拦截
                  MySQLDriverInterceptorPointDefine.class,
                  OracleDriverInterceptorPointDefine.class,
                  // HTTP层拦截
                  HttpClient4HttpClientBuilderInterceptorPointDefine.class,
                  // Redis层拦截
                  LettuceDefaultEndpointInterceptorPointDefine.class,
                  LettuceCommandInterceptorPointDefine.class,
                  LettuceChannelWriterInterceptorPointDefine.class
          ));

  public static void premain(String args, Instrumentation instrumentation) {

    addBootLibToBootstrapClassLoaderSearch(instrumentation);

    initAgentContext(args);

    PortTrailLogger log = AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(PortTrailAgent.class);

    log.info("{} start enhancement, and classLoader = {}, args:{}",
            PortTrailAgent.class, PortTrailAgent.class.getClassLoader(), args);

    InterceptorPointDefineGather interceptorPointDefineGather = getInterceptorGather();

    ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.of(true));

    AgentBuilder with = new AgentBuilder.Default(byteBuddy)
            .with(new AgentBuilder.PoolStrategy() {

              private List<File> bootJarList;

              private List<File> linkJarList;

              private ClassFileLocator.Compound cachedJarLocator;

              private List<File> getBootJarList() {
                if (bootJarList == null) {
                  File bootDir = new File(AgentPackage.getPath(), "boot");
                  bootJarList = searchJars(bootDir);
                }
                return bootJarList;
              }

              private List<File> getLinkJarList() {
                if (linkJarList == null) {
                  File linkDir = new File(AgentPackage.getPath(), "link");
                  linkJarList = searchJars(linkDir);
                }
                return linkJarList;
              }

              private ClassFileLocator.Compound getJarLocator() {
                if (cachedJarLocator == null) {
                  // 创建复合ClassFileLocator
                  List<ClassFileLocator> locators = new ArrayList<>();

                  for (File jar : getBootJarList()) {
                    try {
                      locators.add(ClassFileLocator.ForJarFile.of(jar));
                    } catch (IOException e) {
                      log.error("Cannot create ClassFileLocator for jar: {}", jar.getAbsolutePath(), e);
                    }
                  }

                  for (File jar : getLinkJarList()) {
                    try {
                      locators.add(ClassFileLocator.ForJarFile.of(jar));
                    } catch (IOException e) {
                      log.error("Cannot create ClassFileLocator for jar: {}", jar.getAbsolutePath(), e);
                    }
                  }

                  cachedJarLocator = new ClassFileLocator.Compound(locators.toArray(new ClassFileLocator[0]));
                }
                return cachedJarLocator;
              }

              @Override
              public TypePool typePool(ClassFileLocator classFileLocator, ClassLoader classLoader) {
                return TypePool.Default.WithLazyResolution
                        .of(new ClassFileLocator.Compound(classFileLocator, getJarLocator()));
              }

              @Override
              public TypePool typePool(ClassFileLocator classFileLocator, ClassLoader classLoader, String name) {
                return typePool(classFileLocator, classLoader);
              }
            })
            .ignore(
                    nameStartsWith("net.bytebuddy.")
                            .or(nameStartsWith("org.springframework.")
                                    .and(ElementMatchers.not(ElementMatchers.named(TRANSACTION_TEMPLATE_NAME)))
                            )
                            .or(nameStartsWith("org.slf4j."))
                            .or(nameStartsWith("org.groovy."))
                            .or(nameContains("javassist"))
                            .or(nameContains(".asm."))
                            .or(nameContains(".reflectasm."))
                            .or(nameStartsWith("sun.reflect"))
                            .or(ElementMatchers.isSynthetic())
            )
            // 当要被拦截的type第一次要被加载的时候会进入这里
            .type(interceptorPointDefineGather.buildMatch())
            .transform(new AgentTransformer(interceptorPointDefineGather))
            .with(new AgentListener());

    with.installOn(instrumentation);
  }

  // 参数的使用：porttrail-agent.jar=SERVICE_NAME=AUTH-CORE,IMAGE_NAME=LOCAL
  private static void initAgentContext(String args) {
    AgentContext.setInstanceId(UuidUtils.generateUuid());
    if (args != null) {
      args = args.trim();
      // 解析参数
      String[] properties = args.split(",");
      for (String property : properties) {
        String[] propertyPair = property.split("=");
        if (propertyPair.length != 2) {
          continue;
        }
        if ("SYSTEM_CODE".equals(propertyPair[0])) {
          AgentContext.setSystemCode(propertyPair[1]);
        }
        if ("SERVICE_NAME".equals(propertyPair[0])) {
          AgentContext.setServiceName(propertyPair[1]);
        }
        if ("IMAGE_NAME".equals(propertyPair[0])) {
          AgentContext.setImageName(propertyPair[1]);
        }
        if ("ENV".equalsIgnoreCase(propertyPair[0])) {
          AgentContext.setEnv(propertyPair[1]);
        }
      }
    }
    if (!StringUtils.hasText(AgentContext.getSystemCode())) {
      AgentContext.setSystemCode("UNKNOWN_SYSTEM_CODE");
    }
    if (!StringUtils.hasText(AgentContext.getServiceName())) {
      AgentContext.setServiceName("UNKNOWN_SERVICE_NAME");
    }
    if (!StringUtils.hasText(AgentContext.getImageName())) {
      AgentContext.setImageName("UNKNOWN_IMAGE_NAME");
    }
    if (!StringUtils.hasText(AgentContext.getEnv())) {
      AgentContext.setEnv("UNKNOWN_ENV");
    }
    AgentContext.postInit();
  }

  /**
   * 注意：本类里不能引用继承自boot（bootstrap classloader）中类型的异常，只能用PortTrailAgentBootstrapException。
   * JVM在premain真正执行之前会反射PortTrailAgent，为了校验athrow会加载被抛出的异常类。
   * 此时boot下的jar还没被appendToBootstrapClassLoaderSearch，而PortTrailAgentStartException继承自
   * port-trail-common里的PortTrailException，一旦引用就会以NoClassDefFoundError导致agent启动失败。
   * 同理logger只在方法体内以局部变量的形式使用，不进入方法签名。
   */
  private static InterceptorPointDefineGather getInterceptorGather() {
    PortTrailLogger log = AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(PortTrailAgent.class);
    InterceptorPointDefineGather interceptorPointDefineGather = new InterceptorPointDefineGather();
    for (InterceptorPointDefine interceptorPointDefine : resolveInterceptorPointDefineList()) {
      interceptorPointDefineGather.addInterceptorPointDefine(interceptorPointDefine);
      log.info("interceptorPointDefine.impl[{}] enabled.", interceptorPointDefine.getClass().getName());
    }
    return interceptorPointDefineGather;
  }

  private static List<InterceptorPointDefine> resolveInterceptorPointDefineList() {
    PortTrailLogger log = AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(PortTrailAgent.class);
    String property = AgentPackage.getAgentProperties().getProperty(INTERCEPTOR_POINT_DEFINE_IMPL_KEY);
    if (StringUtils.hasText(property)) {
      log.debug("interceptorPointDefine.impl is configured: {}", property);
    } else {
      property = defaultInterceptorPointDefineImpl();
      log.info("interceptorPointDefine.impl is absent, fallback to the built-in default list.");
    }
    return resolveInterceptorPointDefineList(property);
  }

  /**
   * 包级可见，便于单元测试校验内置默认清单与随包发布的agent.properties保持一致。
   */
  static String defaultInterceptorPointDefineImpl() {
    List<String> defaultClassNameList = new ArrayList<>();
    for (Class<? extends InterceptorPointDefine> clazz : DEFAULT_INTERCEPTOR_POINT_DEFINE_CLASS_LIST) {
      defaultClassNameList.add(clazz.getName());
    }
    return StringUtils.join(",", defaultClassNameList.toArray(new String[0]));
  }

  /**
   * 包级可见，便于单元测试直接验证解析逻辑。
   */
  static List<InterceptorPointDefine> resolveInterceptorPointDefineList(String property) {
    List<InterceptorPointDefine> interceptorPointDefineList = new ArrayList<>();
    for (String className : property.split(",")) {
      String trimmedClassName = className.trim();
      if (trimmedClassName.isEmpty()) {
        continue;
      }
      if (containsClassName(interceptorPointDefineList, trimmedClassName)) {
        throw new PortTrailAgentBootstrapException(String.format(
                "加载interceptorPointDefine.impl异常，类名[%s]重复，配置值为：%s", trimmedClassName, property));
      }
      interceptorPointDefineList.add(newInterceptorPointDefine(trimmedClassName));
    }
    if (interceptorPointDefineList.isEmpty()) {
      throw new PortTrailAgentBootstrapException(String.format(
              "加载interceptorPointDefine.impl异常，没有有效的类名，配置值为：%s", property));
    }
    return interceptorPointDefineList;
  }

  private static boolean containsClassName(List<InterceptorPointDefine> interceptorPointDefineList, String className) {
    for (InterceptorPointDefine interceptorPointDefine : interceptorPointDefineList) {
      if (interceptorPointDefine.getClass().getName().equals(className)) {
        return true;
      }
    }
    return false;
  }

  private static InterceptorPointDefine newInterceptorPointDefine(String className) {
    Class<?> clazz;
    try {
      clazz = Class.forName(className);
    } catch (Throwable t) {
      throw new PortTrailAgentBootstrapException(String.format(
              "加载interceptorPointDefine.impl[%s]异常，无法加载该类。", className), t);
    }
    if (!InterceptorPointDefine.class.isAssignableFrom(clazz)) {
      throw new PortTrailAgentBootstrapException(String.format(
              "加载interceptorPointDefine.impl[%s]异常，该类未实现InterceptorPointDefine接口。", className));
    }
    try {
      return (InterceptorPointDefine) clazz.newInstance();
    } catch (Throwable t) {
      throw new PortTrailAgentBootstrapException(String.format(
              "加载interceptorPointDefine.impl[%s]异常，无法通过public无参构造函数实例化。", className), t);
    }
  }

  private static void addBootLibToBootstrapClassLoaderSearch(Instrumentation instrumentation) {
    File bootDir = new File(AgentPackage.getPath(), "boot");
    File facadeDir = new File(bootDir, "facade");
    File infrastructureDir = new File(bootDir, "infrastructure");
    File commonDir = new File(bootDir, "common");

    addBootLibToBootstrapClassLoaderSearch(instrumentation, facadeDir);
    addBootLibToBootstrapClassLoaderSearch(instrumentation, infrastructureDir);
    addBootLibToBootstrapClassLoaderSearch(instrumentation, commonDir);
  }

  private static void addBootLibToBootstrapClassLoaderSearch(Instrumentation instrumentation, File dir) {
    File[] bootLibs = AgentPackage.findJarFiles(dir);
    for (File bootLib : bootLibs) {
      try {
        instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(bootLib));
      } catch (IOException e) {
        throw new PortTrailAgentBootstrapException(String.format("cannot append %s to BootstrapClassLoaderSearch", bootLib.getAbsolutePath()), e);
      }
    }
  }

  public static List<File> searchJars(File file) {
    List<File> jarFiles = new ArrayList<>();

    if (file == null || !file.exists()) {
      return jarFiles;
    }

    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null) {
        for (File childFile : files) {
          jarFiles.addAll(searchJars(childFile));
        }
      }
    } else if (file.getName().toLowerCase().endsWith(".jar")) {
      jarFiles.add(file);
    }

    return jarFiles;
  }

}
