package cn.addenda.porttrail.common.entrypoint;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class EntryPointSnapshot implements Serializable {

  private static final long serialVersionUID = 1L;

  private String threadName;

  private List<EntryPoint> entryPointList;

  private EntryPointSnapshot() {
  }

  public static EntryPointSnapshot of(List<EntryPoint> entryPointList, String threadName) {
    EntryPointSnapshot entryPointSnapshot = new EntryPointSnapshot();
    if (entryPointList != null) {
      entryPointSnapshot.entryPointList = new ArrayList<>(entryPointList);
    }
    entryPointSnapshot.setThreadName(threadName);
    return entryPointSnapshot;
  }

}
