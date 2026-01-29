package cn.addenda.porttrail.agent.writer;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.exception.PortTrailException;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class AbstractAgentWriter {

  protected <T> void retrySend(Consumer<T> sendAction, T dbExecution) {
    int maxRetries = 100;
    int retryCount = 0;

    while (true) {
      try {
        sendAction.accept(dbExecution);
        // 成功则返回
        return;
      } catch (Exception e) {
        retryCount++;
        if (retryCount > maxRetries) {
          // 达到最大重试次数，记录错误并退出
          String msg = String.format("发送数据[%s]至服务器失败,已达到最大重试次数:%s.", LinkFacade.toStr(dbExecution), maxRetries);
          throw new PortTrailException(msg, e);
        } else {
          String msg = String.format("发送数据[%s]至服务器失败,最大重试次数:%s,当前重试次数:%s.", LinkFacade.toStr(dbExecution), maxRetries, retryCount);
          getLogger().error(msg, e);
        }

        try {
          // 间隔3秒
          Thread.sleep(3000);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          return;
        }
      }
    }
  }

  protected <T> boolean retryOffer(Predicate<T> offerAction, T dbExecution) {
    int maxRetries = 10;
    int retryCount = 0;

    while (true) {
      Throwable t = null;
      try {
        boolean b = offerAction.test(dbExecution);
        if (b) {
          // 成功则返回
          return true;
        }
      } catch (Exception e) {
        t = e;
      }

      retryCount++;
      if (retryCount > maxRetries) {
        String msg = String.format("提交数据[%s]至队列失败,已达到最大重试次数:%s.", LinkFacade.toStr(dbExecution), maxRetries);
        // 达到最大重试次数，记录错误并退出
        if (t != null) {
          getLogger().error(msg, t);
        } else {
          getLogger().error(msg);
        }
        return false;
      } else {
        String msg = String.format("提交数据[%s]至队列失败,最大重试次数:%s,当前重试次数:%s.", LinkFacade.toStr(dbExecution), maxRetries, retryCount);
        if (t != null) {
          getLogger().error(msg, t);
        } else {
          getLogger().error(msg);
        }
      }

      try {
        // 间隔500ms
        Thread.sleep(500);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        return false;
      }

    }
  }

  protected abstract PortTrailLogger getLogger();


}
