package cn.addenda.porttrail.agent.transform.interceptor.tx;

import cn.addenda.porttrail.agent.transform.interceptor.AbstractEntryPointInterceptor;
import cn.addenda.porttrail.common.exception.PortTrailException;
import cn.addenda.porttrail.infrastructure.tx.TxContext;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.Callable;

public abstract class AbstractTxEntryPointInterceptor extends AbstractEntryPointInterceptor {

  private static final ThreadLocal<Deque<String>> txStack = ThreadLocal.withInitial(() -> null);

  /**
   * 事务去重，不使用detail，使用上下文里的事务ID
   */
  @Override
  protected void beforePush(String detail) {
    Deque<String> deque = txStack.get();
    if (deque == null) {
      deque = new ArrayDeque<>();
      txStack.set(deque);
    }
    deque.push(TxContext.getTxId());
  }

  @Override
  protected void beforePop(String detail) {
    Deque<String> deque = txStack.get();
    if (deque == null) {
      throw new PortTrailException("Unexpected pop operation, current txStack is null.");
    }
    if (!deque.isEmpty()) {
      deque.pop();
    } else {
      throw new PortTrailException("Unexpected pop operation, current txStack is empty.");
    }
    if (deque.isEmpty()) {
      txStack.remove();
    }
  }

  /**
   * 事务去重，不使用detail，使用上下文里的事务ID
   */
  @Override
  protected boolean ifPushEntryPoint(String detail) {
    String txId = TxContext.getTxId();
    if (TxContext.WITHOUT_TX.equals(txId)) {
      // 如果没有事务ID，但是遇到了事务EntryPoint，压一个EntryPoint。
      // 再后续做snapshot的时候，只会取最后一个EntryPoint。
      return true;
    }
    Deque<String> entryPointList = txStack.get();
    if (entryPointList == null || entryPointList.isEmpty()) {
      return true;
    }
    // 只需要判断栈顶的那个
    Iterator<String> descendingIterator = entryPointList.descendingIterator();
    if (descendingIterator.hasNext()) {
      String entryPoint = descendingIterator.next();
      return !txId.equals(entryPoint);
    }
    return true;
  }

  /**
   * zuper可能是一个方法，也可能是一个函数表达式。
   * 再zuper被调用之前，事务已经设置在当前线程的上下文中了。
   * 所以，Spring的编程式事务不支持。
   */
  @Override
  protected Object callWithEntryPoint(String detail, Callable<?> zuper)
          throws Exception {
    return super.callWithEntryPoint(detail, zuper);
  }

}
