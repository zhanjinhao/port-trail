package cn.addenda.porttrail.infrastructure.entrypoint;

import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.exception.PortTrailException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntryPointStackContext {

  private static final ThreadLocal<EntryPointStack> tl = ThreadLocal.withInitial(() -> null);

  public static void pushEntryPoint(EntryPoint entryPoint) {
    EntryPointStack entryPointStack = tl.get();
    if (entryPointStack == null) {
      entryPointStack = new EntryPointStack();
      tl.set(entryPointStack);
    }
    entryPointStack.push(entryPoint);
  }

  public static void popEntryPoint() {
    EntryPointStack entryPointStack = tl.get();
    if (entryPointStack == null) {
      throw new PortTrailException("Unexpected pop operation, current tl is null.");
    }
    if (!entryPointStack.ifEmpty()) {
      entryPointStack.pop();
    } else {
      throw new PortTrailException("Unexpected pop operation, current entryPointStack is empty.");
    }
    if (entryPointStack.ifEmpty()) {
      tl.remove();
    }
  }

  public static EntryPointSnapshot snapshot() {
    EntryPointStack entryPointStack = tl.get();
    if (entryPointStack == null) {
      return EntryPointSnapshot.of(Collections.emptyList(), Thread.currentThread().getName());
    }
    return entryPointStack.snapshot();
  }

}
