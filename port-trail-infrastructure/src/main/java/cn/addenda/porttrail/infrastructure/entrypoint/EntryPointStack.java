package cn.addenda.porttrail.infrastructure.entrypoint;

import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Setter
@Getter
@ToString
public class EntryPointStack {

  private final Deque<EntryPoint> deque = new ArrayDeque<>();

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

    return EntryPointSnapshot.of(entryPointList.isEmpty() ? null : reverse(entryPointList), Thread.currentThread().getName());
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
