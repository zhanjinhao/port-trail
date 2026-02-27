package cn.addenda.porttrail.agent.link;

import cn.addenda.porttrail.agent.AgentPackage;
import cn.addenda.porttrail.agent.PortTrailAgentStartException;
import cn.addenda.porttrail.agent.util.FileUtils;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.facade.*;
import cn.addenda.porttrail.infrastructure.jvm.JVMShutdown;
import cn.addenda.porttrail.infrastructure.jvm.JVMShutdownCallback;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LinkFacade {

  static {
    initConfig();
    initHttpFacade();
    initJsonFacade();
    initLogFacade();
  }

  // ------------------------
  //   初始化HttpFacade及实现
  // ------------------------

  static String httpFacadeImpl;
  static HttpFacade httpFacade;
  static String sendPreparedStatementExecutionUrl;
  static String sendStatementExecutionUrl;
  static String sendDbConfigUrl;
  static String sendServletRequestUrl;
  static String sendServletResponseUrl;

  private static void initHttpFacade() {
    httpFacadeImpl = AgentPackage.getAgentProperties().getProperty("httpFacade.impl");
    Consumer<ClassLoader> runnable = linkClassLoader -> {
      try {
        Class<?> portTrailDefaultHttpImplClass = Class.forName(httpFacadeImpl, true, linkClassLoader);
        httpFacade = (HttpFacade) portTrailDefaultHttpImplClass
                .getConstructor()
                .newInstance();
        if (httpFacade instanceof JVMShutdownCallback) {
          JVMShutdown.getInstance().addJvmShutdownCallback((JVMShutdownCallback) httpFacade);
        }
      } catch (InstantiationException | IllegalAccessException |
               InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
        throw new PortTrailAgentStartException(
                String.format("Cannot init httpFacade: [%s].", httpFacadeImpl), e);
      }
    };
    acceptWithLinkClassLoader(runnable);
    sendPreparedStatementExecutionUrl = AgentPackage.getAgentProperties().getProperty("sendPreparedStatementExecution.url");
    sendStatementExecutionUrl = AgentPackage.getAgentProperties().getProperty("sendStatementExecution.url");
    sendDbConfigUrl = AgentPackage.getAgentProperties().getProperty("sendDbConfig.url");
    sendServletRequestUrl = AgentPackage.getAgentProperties().getProperty("sendServletRequest.url");
    sendServletResponseUrl = AgentPackage.getAgentProperties().getProperty("sendServletResponse.url");
  }

  public static void sendStatementExecution(byte[] bytes) {
    httpFacade.sendRequest(sendStatementExecutionUrl, CompressUtils.compress(bytes));
  }

  public static void sendPreparedStatementExecution(byte[] bytes) {
    httpFacade.sendRequest(sendPreparedStatementExecutionUrl, CompressUtils.compress(bytes));
  }

  public static void sendDbConfig(String dbConfig) {
    httpFacade.sendRequest(sendDbConfigUrl, dbConfig);
  }

  public static void sendServletRequest(byte[] bytes) {
    httpFacade.sendRequest(sendServletRequestUrl, CompressUtils.compress(bytes));
  }

  public static void sendServletResponse(byte[] bytes) {
    httpFacade.sendRequest(sendServletResponseUrl, CompressUtils.compress(bytes));
  }

  // ------------------------
  //   初始化JsonFacade及实现
  // ------------------------

  static String jsonFacadeImpl;

  static JsonFacade jsonFacade;

  private static void initJsonFacade() {
    jsonFacadeImpl = AgentPackage.getAgentProperties().getProperty("jsonFacade.impl");

    Consumer<ClassLoader> runnable = linkClassLoader -> {
      try {
        Class<?> portTrailDefaultJsonImplClass = Class.forName(jsonFacadeImpl, true, linkClassLoader);
        jsonFacade = (JsonFacade) portTrailDefaultJsonImplClass
                .getConstructor()
                .newInstance();
        if (jsonFacade instanceof JVMShutdownCallback) {
          JVMShutdown.getInstance().addJvmShutdownCallback((JVMShutdownCallback) jsonFacade);
        }
      } catch (ClassNotFoundException | InvocationTargetException |
               InstantiationException | IllegalAccessException | NoSuchMethodException e) {
        throw new PortTrailAgentStartException(
                String.format("Cannot init jsonFacade: [%s].", jsonFacadeImpl), e);
      }
    };
    acceptWithLinkClassLoader(runnable);

  }

  public static String toStr(Object input) {
    return jsonFacade.toStr(input);
  }

  public static <T> T toObj(String inputJson, Class<T> clazz) {
    return jsonFacade.toObj(inputJson, clazz);
  }

  // ------------------
  //   初始化LogFacade
  // ------------------

  static String logFacadeImpl;

  private static void initLogFacade() {
    logFacadeImpl = AgentPackage.getAgentProperties().getProperty("logFacade.impl");
  }

  private static LogFacade _createLogFacade(String name) {
    Function<ClassLoader, LogFacade> runnable = linkClassLoader -> {
      try {
        Class<?> portTrailDefaultLogImplClass = Class.forName(logFacadeImpl, true, linkClassLoader);
        return (LogFacade) portTrailDefaultLogImplClass
                .getConstructor(String.class)
                .newInstance(name);

      } catch (InstantiationException | IllegalAccessException |
               InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
        throw new PortTrailAgentStartException(
                String.format("Cannot create logFacade: [%s].", logFacadeImpl), e);
      }
    };
    return applyWithLinkClassLoader(runnable);
  }

  private static LogFacade _createLogFacade(String name, String fqcn) {
    Function<ClassLoader, LogFacade> runnable = linkClassLoader -> {
      try {
        Class<?> portTrailDefaultLogImplClass = Class.forName(logFacadeImpl, true, linkClassLoader);
        return (LogFacade) portTrailDefaultLogImplClass
                .getConstructor(String.class, String.class)
                .newInstance(name, fqcn);

      } catch (InstantiationException | IllegalAccessException |
               InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
        throw new PortTrailAgentStartException(
                String.format("Cannot create logFacade: [%s].", logFacadeImpl), e);
      }
    };
    return applyWithLinkClassLoader(runnable);
  }

  private static final Map<String, LogFacade> logFacadeMap = new ConcurrentHashMap<>();

  public static LogFacade createLogFacade(String name) {
    return logFacadeMap.computeIfAbsent(name, s -> {
      LogFacade logFacade = _createLogFacade(s);
      if (logFacade instanceof JVMShutdownCallback) {
        JVMShutdown.getInstance().addJvmShutdownCallback((JVMShutdownCallback) logFacade);
      }
      return logFacade;
    });
  }

  public static LogFacade createLogFacade(Class<?> clazz) {
    return createLogFacade(clazz.getName());
  }

  public static LogFacade createLogFacade(String name, String fqcn) {
    return logFacadeMap.computeIfAbsent(name, s -> {
      LogFacade logFacade = _createLogFacade(s, fqcn);
      if (logFacade instanceof JVMShutdownCallback) {
        JVMShutdown.getInstance().addJvmShutdownCallback((JVMShutdownCallback) logFacade);
      }
      return logFacade;
    });
  }

  public static LogFacade createLogFacade(Class<?> clazz, String fqcn) {
    return createLogFacade(clazz.getName(), fqcn);
  }

  // --------------------------------------------------------------------
  //  在agent启动的时候，加载配置文件并将配置文件分发到各个ext的PropertiesAware里面
  // --------------------------------------------------------------------

  static File httpConfigFile;
  static Properties httpConfigProperties;
  static File jsonConfigFile;
  static Properties jsonConfigProperties;
  static File logConfigFile;
  static Properties logConfigProperties;

  private static void initConfig() {
    httpConfigFile = new File(LinkClassLoader.getLinkHttpPath(), "http.properties");
    httpConfigProperties = FileUtils.loadProperties(httpConfigFile);
    jsonConfigFile = new File(LinkClassLoader.getLinkJsonPath(), "json.properties");
    jsonConfigProperties = FileUtils.loadProperties(jsonConfigFile);
    logConfigFile = new File(LinkClassLoader.getLinkLogPath(), "log.properties");
    logConfigProperties = FileUtils.loadProperties(logConfigFile);
    dispatchConfig();
  }

  private static void dispatchConfig() {
    Consumer<ClassLoader> consumer = classLoader -> {
      List<String> awareNameList = getAwareNameList(classLoader);
      for (String awareName : awareNameList) {
        loadClassAndInvokeAware(classLoader, awareName);
      }
    };

    acceptWithLinkClassLoader(consumer);
  }

  private static void loadClassAndInvokeAware(ClassLoader classLoader, String awareName) {
    Class<?> awareClass;
    try {
      awareClass = classLoader.loadClass(awareName);
    } catch (ClassNotFoundException e) {
      throw new PortTrailAgentStartException(
              String.format("Error loading awareClass, awareName: %s.", awareName), e);
    }
    Method[] declaredMethods = awareClass.getDeclaredMethods();
    for (Method declaredMethod : declaredMethods) {
      if (!Modifier.isStatic(declaredMethod.getModifiers())) {
        continue;
      }
      int parameterCount = declaredMethod.getParameterCount();
      if (parameterCount != 1) {
        continue;
      }
      Class<?> parameterType = declaredMethod.getParameterTypes()[0];
      if (!parameterType.isAssignableFrom(Properties.class)) {
        continue;
      }

      AgentPropertiesAware agentPropertiesAware = declaredMethod.getAnnotation(AgentPropertiesAware.class);
      if (agentPropertiesAware != null) {
        invokeAwareMethod(declaredMethod, AgentPackage.getAgentProperties(), awareClass);
      }
      LinkPropertiesAware linkPropertiesAware = declaredMethod.getAnnotation(LinkPropertiesAware.class);
      if (linkPropertiesAware != null) {
        if (agentPropertiesAware != null) {
          throw new PortTrailAgentStartException(
                  String.format("%s and %s can not annotate the same method: %s.",
                          AgentPropertiesAware.class, LinkPropertiesAware.class, declaredMethod));
        }

        FacadeType facadeType = linkPropertiesAware.value();
        Properties customProperties;
        switch (facadeType) {
          case LOG:
            customProperties = logConfigProperties;
            break;
          case HTTP:
            customProperties = httpConfigProperties;
            break;
          case JSON:
            customProperties = jsonConfigProperties;
            break;
          default:
            throw new PortTrailAgentStartException(
                    String.format("Unsupported facade type: %s, awareClass: %s, awareMethod: %s.", facadeType, awareClass, declaredMethod));
        }
        invokeAwareMethod(declaredMethod, customProperties, awareClass);
      }
    }
  }

  private static void invokeAwareMethod(Method method, Properties properties, Class<?> awareClass) {
    try {
      method.invoke(null, properties);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new PortTrailAgentStartException(
              String.format("invoke aware method error, awareClass: %s, awareMethod: %s.", awareClass, method), e);
    }
  }

  private static final String AWARE_CONF_RESOURCE = "META-INF/porttrail.link.conf.aware";

  private static List<String> getAwareNameList(ClassLoader classLoader) {
    List<String> nameList = new ArrayList<>();
    Enumeration<URL> resources;
    try {
      resources = classLoader.getResources(AWARE_CONF_RESOURCE);
    } catch (IOException e) {
      throw new PortTrailAgentStartException(
              String.format("Error getting resources from '%s'.", AWARE_CONF_RESOURCE), e);
    }
    while (resources.hasMoreElements()) {
      URL url = resources.nextElement();
      try (InputStream inputStream = url.openStream();
           InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
           BufferedReader bufferedReader = new BufferedReader(inputStreamReader);) {
        while (true) {
          String s = bufferedReader.readLine();
          if (s != null && !s.trim().isEmpty()) {
            nameList.add(s);
          } else {
            break;
          }
        }
      } catch (IOException x) {
        throw new PortTrailAgentStartException(
                String.format("Error reading configuration file, url: %s.", url.getPath()), x);
      }
    }
    return nameList;
  }

  // ---------------------
  // classLoader的函数表达式
  // ---------------------

  private static void acceptWithLinkClassLoader(Consumer<ClassLoader> consumer) {
    LinkClassLoader linkClassLoader = LinkClassLoader.getInstance();
//    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
//    Thread.currentThread().setContextClassLoader(extClassLoader);
//    try {
    consumer.accept(linkClassLoader);
//    } finally {
//      Thread.currentThread().setContextClassLoader(contextClassLoader);
//    }
  }

  private static <T> T applyWithLinkClassLoader(Function<ClassLoader, T> function) {
    LinkClassLoader linkClassLoader = LinkClassLoader.getInstance();
//    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
//    Thread.currentThread().setContextClassLoader(extClassLoader);
//    try {
    return function.apply(linkClassLoader);
//    } finally {
//      Thread.currentThread().setContextClassLoader(contextClassLoader);
//    }
  }

}
