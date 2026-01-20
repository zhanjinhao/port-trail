package cn.addenda.porttrail.agent.link;

import cn.addenda.porttrail.agent.AgentPackage;
import cn.addenda.porttrail.agent.PortTrailAgentStartException;
import cn.addenda.porttrail.agent.util.IOUtils;
import cn.addenda.porttrail.common.util.ArrayUtils;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class LinkClassLoader extends URLClassLoader {

  @Getter
  private final List<String> prefixList = new ArrayList<>();

  static {
    ClassLoader.registerAsParallelCapable();
  }

  private static LinkClassLoader instance;

  @Getter
  private static final File linkPath;

  @Getter
  private static final File linkLogPath;

  @Getter
  private static final File linkHttpPath;

  @Getter
  private static final File linkJsonPath;

  static {
    linkPath = new File(AgentPackage.getPath(), "link");
    linkLogPath = new File(linkPath, "log");
    linkHttpPath = new File(linkPath, "http");
    linkJsonPath = new File(linkPath, "json");
  }

  public LinkClassLoader(ClassLoader parent) {
    super(new URL[0], parent);
    this.addUrls();
    this.addPrefixes();
  }

  private void addPrefixes() {
    List<String> linkPrefixList = getLinkPrefixList();
    this.prefixList.addAll(linkPrefixList);
  }

  private void addUrls() {
    List<File> linkLibPathList = Arrays.asList(linkLogPath, linkHttpPath, linkJsonPath);
    List<URL> urlList = new ArrayList<>();
    for (File linkLibPath : linkLibPathList) {
      Collections.addAll(urlList, AgentPackage.findJarUrls(linkLibPath));
    }
    // 添加扩展jar文件
    for (URL url : urlList) {
      addURL(url);
    }
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    synchronized (getClassLoadingLock(name)) {
      Class<?> loadedClass = findLoadedClass(name);
      if (loadedClass != null) {
        return loadedClass;
      }

      if (ORG_SLF4J_IMPL_STATICLOGGERBINDER_CLASS_NAME.equals(name)) {
        URL resource = findOrgSlf4jImplStaticLoggerBinder();
        byte[] byteArray;
        try {
          byteArray = IOUtils.toByteArray(resource);
        } catch (IOException e) {
          throw new PortTrailAgentStartException(
                  String.format("Error loading byte array from resource: %s.", resource), e);
        }
        return defineClass(name, byteArray, 0, byteArray.length);
      }

      boolean shouldIsolate = false;
      for (String prefix : prefixList) {
        if (name.startsWith(prefix)) {
          shouldIsolate = true;
          break;
        }
      }

      if (shouldIsolate) {
        loadedClass = findClass(name);
        if (resolve) {
          resolveClass(loadedClass);
        }
        return loadedClass;
      }

      return super.loadClass(name, resolve);
    }
  }

  private static List<String> getLinkPrefixList() {
    List<String> httpPrefixList = new ArrayList<>();
    httpPrefixList.add("org.apache.hc.");
    httpPrefixList.add("cn.addenda.porttrail.link.http.");

    httpPrefixList.add("com.fasterxml.jackson.");
    httpPrefixList.add("cn.addenda.porttrail.link.json.");

    httpPrefixList.add("org.slf4j.");
    httpPrefixList.add("org.apache.logging.log4j.");
    httpPrefixList.add("org.apache.logging.slf4j.");
    httpPrefixList.add("cn.addenda.porttrail.link.log.");

    return httpPrefixList;
  }

  public static synchronized LinkClassLoader getInstance() {
    initInstance();
    return instance;
  }

  public static synchronized void initInstance() {
    if (instance == null) {
      ClassLoader parentClassLoader = ClassLoader.getSystemClassLoader().getParent();
      instance = new LinkClassLoader(parentClassLoader);
    }
  }

  private static final String ORG_SLF4J_IMPL_STATICLOGGERBINDER_CLASS_NAME = "org.slf4j.impl.StaticLoggerBinder";
  private static final String ORG_SLF4J_IMPL_STATICLOGGERBINDER_RESOURCES_NAME = "org/slf4j/impl/StaticLoggerBinder.class";

  @Override
  public Enumeration<URL> getResources(String name) throws IOException {
    if (ORG_SLF4J_IMPL_STATICLOGGERBINDER_RESOURCES_NAME.equals(name)) {
      Iterator<URL> iterator = ArrayUtils.asArrayList(findOrgSlf4jImplStaticLoggerBinder()).iterator();
      return new Enumeration<URL>() {
        @Override
        public boolean hasMoreElements() {
          return iterator.hasNext();
        }

        @Override
        public URL nextElement() {
          return iterator.next();
        }
      };
    }
    return super.getResources(name);
  }

  @Override
  public URL getResource(String name) {
    if (ORG_SLF4J_IMPL_STATICLOGGERBINDER_RESOURCES_NAME.equals(name)) {
      return findOrgSlf4jImplStaticLoggerBinder();
    }
    return super.getResource(name);
  }

  private URL findOrgSlf4jImplStaticLoggerBinder() {
    Enumeration<URL> resources;
    try {
      resources = findResources(ORG_SLF4J_IMPL_STATICLOGGERBINDER_RESOURCES_NAME);
    } catch (IOException e) {
      throw new PortTrailAgentStartException(
              String.format("Error loading '%s'.", ORG_SLF4J_IMPL_STATICLOGGERBINDER_RESOURCES_NAME), e);
    }

    List<URL> urlList = new ArrayList<>();
    while (resources.hasMoreElements()) {
      URL url = resources.nextElement();
      // todo 这里强依赖了子包！！！
      if (url.toString().contains("port-trail-link-log")) {
        urlList.add(url);
      }
    }

    if (urlList.size() != 1) {
      throw new PortTrailAgentStartException(
              String.format("Only one '%s' is required.", ORG_SLF4J_IMPL_STATICLOGGERBINDER_RESOURCES_NAME));
    }

    return urlList.get(0);
  }

}
