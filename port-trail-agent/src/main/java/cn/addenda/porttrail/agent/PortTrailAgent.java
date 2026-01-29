package cn.addenda.porttrail.agent;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.AgentTransformer;
import cn.addenda.porttrail.agent.transform.InterceptorPointDefineGather;
import cn.addenda.porttrail.agent.transform.interceptor.datasource.druid.DruidDruidDataSourceInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.datasource.hikari.HikariConcurrentBagInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.driver.mysql.MySQLDriverInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.driver.oracle.OracleDriverInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.jdbc.PortTrailStatementInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.job.xxl.glue.XxlGlueInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.job.xxl.jobhandler.XxlJobHandlerInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.job.xxl.method.XxlMethodInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.job.xxl.script.XxlScriptInterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.interceptor.mybatis.MybatisExecutorInterceptorPointDefine;
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
import java.util.List;
import java.util.jar.JarFile;

import static net.bytebuddy.matcher.ElementMatchers.nameContains;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;

public class PortTrailAgent {

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
              @Override
              public TypePool typePool(ClassFileLocator classFileLocator, ClassLoader classLoader) {
                File bootDir = new File(AgentPackage.getPath(), "boot");
                File linkDir = new File(AgentPackage.getPath(), "link");

                // 收集所有JAR文件
                List<File> bootJars = searchJars(bootDir);
                List<File> linkJars = searchJars(linkDir);

                // 创建复合ClassFileLocator
                List<ClassFileLocator> locators = new java.util.ArrayList<>();
                locators.add(classFileLocator);

                for (File jar : bootJars) {
                  try {
                    locators.add(ClassFileLocator.ForJarFile.of(jar));
                  } catch (IOException e) {
                    log.error("Cannot create ClassFileLocator for jar: {}", jar.getAbsolutePath(), e);
                  }
                }

                for (File jar : linkJars) {
                  try {
                    locators.add(ClassFileLocator.ForJarFile.of(jar));
                  } catch (IOException e) {
                    log.error("Cannot create ClassFileLocator for jar: {}", jar.getAbsolutePath(), e);
                  }
                }

                return TypePool.Default.WithLazyResolution.of(
                        new ClassFileLocator.Compound(locators.toArray(new ClassFileLocator[0]))
                );
              }

              @Override
              public TypePool typePool(ClassFileLocator classFileLocator, ClassLoader classLoader, String name) {
                return typePool(classFileLocator, classLoader);
              }
            })
            .ignore(
                    nameStartsWith("net.bytebuddy.")
                            .or(nameStartsWith("org.springframework.")
//                                    .and(ElementMatchers.not(ElementMatchers.named(SERVER_PROPERTIES_SERVLET_NAME)))
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

  private static InterceptorPointDefineGather getInterceptorGather() {
    InterceptorPointDefineGather interceptorPointDefineGather = new InterceptorPointDefineGather();
    // Server层拦截
    interceptorPointDefineGather.addInterceptorPointDefine(new TomcatAbstractProtocolInterceptorPointDefine());
    interceptorPointDefineGather.addInterceptorPointDefine(new JettyServerInterceptorPointDefine());
    // 入口层拦截（Servlet/Task/XxlJob）
    interceptorPointDefineGather.addInterceptorPointDefine(new JavaxServletInterceptorPointDefine());
//    interceptorPointDefineGather.addInterceptorPointDefine(new JakartaServletInterceptorPointDefine());
    interceptorPointDefineGather.addInterceptorPointDefine(new TaskInterceptorPointDefine());
    interceptorPointDefineGather.addInterceptorPointDefine(new XxlJobHandlerInterceptorPointDefine());
    interceptorPointDefineGather.addInterceptorPointDefine(new XxlMethodInterceptorPointDefine());
    interceptorPointDefineGather.addInterceptorPointDefine(new XxlGlueInterceptorPointDefine());
    interceptorPointDefineGather.addInterceptorPointDefine(new XxlScriptInterceptorPointDefine());
    // Tx层拦截
    interceptorPointDefineGather.addInterceptorPointDefine(new SpringTransactionalInterceptorPointDefine());
    interceptorPointDefineGather.addInterceptorPointDefine(new SpringTransactionHelperInterceptorPointDefine());
    interceptorPointDefineGather.addInterceptorPointDefine(new SpringTransactionTemplateInterceptorPointDefine());
    // ORM层拦截
    interceptorPointDefineGather.addInterceptorPointDefine(new MybatisExecutorInterceptorPointDefine());
    // DataSource层拦截
    interceptorPointDefineGather.addInterceptorPointDefine(new HikariConcurrentBagInterceptorPointDefine());
    interceptorPointDefineGather.addInterceptorPointDefine(new DruidDruidDataSourceInterceptorPointDefine());
    // JDBC层拦截
    interceptorPointDefineGather.addInterceptorPointDefine(new PortTrailStatementInterceptorPointDefine());
    // DB层拦截
    interceptorPointDefineGather.addInterceptorPointDefine(new MySQLDriverInterceptorPointDefine());
    interceptorPointDefineGather.addInterceptorPointDefine(new OracleDriverInterceptorPointDefine());
    return interceptorPointDefineGather;
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
