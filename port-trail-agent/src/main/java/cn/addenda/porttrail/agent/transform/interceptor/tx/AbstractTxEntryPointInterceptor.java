package cn.addenda.porttrail.agent.transform.interceptor.tx;

import cn.addenda.porttrail.agent.transform.interceptor.AbstractEntryPointInterceptor;
import cn.addenda.porttrail.infrastructure.exception.PortTrailException;
import cn.addenda.porttrail.infrastructure.entrypoint.EntryPointStackContext;
import cn.addenda.porttrail.infrastructure.tx.TxContext;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.Callable;

public abstract class AbstractTxEntryPointInterceptor extends AbstractEntryPointInterceptor {

  private static final ThreadLocal<Deque<String>> txStack = ThreadLocal.withInitial(() -> null);

  protected void push() {
    Deque<String> deque = txStack.get();
    if (deque == null) {
      deque = new ArrayDeque<>();
      txStack.set(deque);
    }
    deque.push(TxContext.getTxId());
  }

  protected void pop() {
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
   * zuper可能是一个方法，也可能是一个函数表达式。
   * 再zuper被调用之前，事务已经设置在当前线程的上下文中了。
   * 所以，Spring的编程式事务不支持。
   */
  @Override
  protected Object callWithEntryPoint(String detail, Callable<?> zuper)
          throws Exception {
    boolean ifPushTxEntryPoint = ifPushTxEntryPoint();
    if (ifPushTxEntryPoint) {
      push();
      EntryPointStackContext.pushEntryPoint(entryPoint(detail));
    }
    try {
      return zuper.call();
    } finally {
      if (ifPushTxEntryPoint) {
        pop();
        EntryPointStackContext.popEntryPoint();
      }
    }
  }

  private boolean ifPushTxEntryPoint() {
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
    // todo 这里是只需要判断栈顶的那个，还是栈里全部的数据
    Iterator<String> descendingIterator = entryPointList.descendingIterator();
    while (descendingIterator.hasNext()) {
      String entryPoint = descendingIterator.next();
      if (txId.equals(entryPoint)) {
        return false;
      }
    }

    return true;
  }

}
