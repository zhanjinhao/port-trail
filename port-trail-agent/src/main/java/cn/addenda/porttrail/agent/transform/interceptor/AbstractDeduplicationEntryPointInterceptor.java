package cn.addenda.porttrail.agent.transform.interceptor;

import cn.addenda.porttrail.common.exception.PortTrailException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;

public abstract class AbstractDeduplicationEntryPointInterceptor extends AbstractEntryPointInterceptor {

  @Override
  protected void beforePush(String detail) {
    ThreadLocal<Deque<String>> deduplicationStack = getDeduplicationStack();
    Deque<String> deque = deduplicationStack.get();
    if (deque == null) {
      deque = new ArrayDeque<>();
      deduplicationStack.set(deque);
    }
    deque.push(detail);
  }

  @Override
  protected void beforePop(String detail) {
    ThreadLocal<Deque<String>> deduplicationStack = getDeduplicationStack();
    Deque<String> deque = deduplicationStack.get();
    if (deque == null) {
      throw new PortTrailException(
              String.format("Unexpected pop operation, current deduplicationStack[%s] is null.", deduplicationStack));
    }
    if (!deque.isEmpty()) {
      deque.pop();
    } else {
      throw new PortTrailException(
              String.format("Unexpected pop operation, current deduplicationStack[%s] is empty.", deduplicationStack));
    }
    if (deque.isEmpty()) {
      deduplicationStack.remove();
    }
  }

  @Override
  protected boolean ifPushEntryPoint(String detail) {
    ThreadLocal<Deque<String>> deduplicationStack = getDeduplicationStack();
    Deque<String> entryPointDeque = deduplicationStack.get();
    if (entryPointDeque == null || entryPointDeque.isEmpty()) {
      return true;
    }
    // 只需要判断栈顶的那个
    Iterator<String> descendingIterator = entryPointDeque.descendingIterator();
    if (descendingIterator.hasNext()) {
      String entryPoint = descendingIterator.next();
      return !Objects.equals(detail, entryPoint);
    }
    return true;
  }

  protected abstract ThreadLocal<Deque<String>> getDeduplicationStack();

}
