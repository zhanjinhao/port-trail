package cn.addenda.porttrail.infrastructure.jvm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JVMShutdown {

  private final List<JVMShutdownCallback> jvmShutdownCallbackList = new ArrayList<>();

  private static final JVMShutdown instance = new JVMShutdown();

  public static JVMShutdown getInstance() {
    return instance;
  }

  private JVMShutdown() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        List<JVMShutdownCallback> snapshot;
        synchronized (JVMShutdown.class) {
          snapshot = new ArrayList<>(jvmShutdownCallbackList);
        }
        snapshot.sort(Comparator.comparing(JVMShutdownCallback::getOrder));
        for (JVMShutdownCallback jvmShutdownCallback : snapshot) {
          try {
            jvmShutdownCallback.shutdown();
          } catch (Throwable e) {
            e.printStackTrace();
          }
        }
      }
    });
  }

  public void addJvmShutdownCallback(JVMShutdownCallback jvmShutdownCallback) {
    synchronized (JVMShutdown.class) {
      jvmShutdownCallbackList.add(jvmShutdownCallback);
    }
  }

}
