package cn.addenda.porttrail.agent.writer;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.exception.PortTrailException;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class AbstractAgentWriter {

  protected <T> void retrySend(Consumer<T> sendAction, T dbExecution, int maxCount) {
    int sendCount = 0;

    while (true) {
      try {
        sendAction.accept(dbExecution);
        // 成功则返回
        return;
      } catch (Exception e) {
        sendCount++;
        if (sendCount > maxCount) {
          // 达到最大重试次数，记录错误并退出
          String msg = String.format("发送数据[%s]至服务器失败,已达到最大发送次数[%s].", LinkFacade.toStr(dbExecution), maxCount);
          throw new PortTrailException(msg, e);
        } else {
          String msg = String.format("发送数据[%s]至服务器失败,最大发送次数[%s],当前第%s次发送.", LinkFacade.toStr(dbExecution), maxCount, sendCount);
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
    int maxCount = 10;
    int sendCount = 0;

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

      sendCount++;
      if (sendCount > maxCount) {
        String msg = String.format("提交数据[%s]至队列失败,已达到最大提交次数[%s].", LinkFacade.toStr(dbExecution), maxCount);
        // 达到最大重试次数，记录错误并退出
        if (t != null) {
          getLogger().error(msg, t);
        } else {
          getLogger().error(msg);
        }
        return false;
      } else {
        String msg = String.format("提交数据[%s]至队列失败,最大提交次数[%s],当前第%s次发送.", LinkFacade.toStr(dbExecution), maxCount, sendCount);
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
