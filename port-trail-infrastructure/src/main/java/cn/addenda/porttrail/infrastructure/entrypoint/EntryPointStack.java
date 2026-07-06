package cn.addenda.porttrail.infrastructure.entrypoint;

import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.util.UuidUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Setter
@Getter
@ToString
public class EntryPointStack {

  private final Deque<EntryPoint> deque = new ArrayDeque<>();

  private final String traceId = UuidUtils.generateUuid();

  private final AtomicLong seqCounter = new AtomicLong(0);

  public void push(EntryPoint entryPoint) {
    deque.push(entryPoint);
  }

  public void pop() {
    deque.pop();
  }

  public boolean ifEmpty() {
    return deque.isEmpty();
  }

  public int size() {
    return deque.size();
  }

  public EntryPointSnapshot snapshot() {
    List<EntryPoint> entryPointList = new ArrayList<>();
    for (EntryPoint entryPoint : deque) {
      entryPointList.add(EntryPoint.of(entryPoint));
    }

    return EntryPointSnapshot.of(entryPointList.isEmpty() ? null : reverse(entryPointList),
            Thread.currentThread().getName(), this.traceId, this.seqCounter.getAndIncrement());
  }

  private List<EntryPoint> reverse(List<EntryPoint> entryPointList) {
    // 翻转，实现
    List<EntryPoint> reversed = new ArrayList<>();
    for (int i = entryPointList.size() - 1; i != -1; i--) {
      reversed.add(entryPointList.get(i));
    }
    return reversed;
  }

}
