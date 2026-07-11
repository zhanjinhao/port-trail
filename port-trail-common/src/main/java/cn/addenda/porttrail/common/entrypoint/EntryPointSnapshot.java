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

  private String traceId;

  private Long seqId;

  private EntryPointSnapshot() {
  }

  public static EntryPointSnapshot of(List<EntryPoint> entryPointList, String threadName, String traceId, Long seqId) {
    EntryPointSnapshot entryPointSnapshot = new EntryPointSnapshot();
    if (entryPointList != null) {
      entryPointSnapshot.entryPointList = new ArrayList<>(entryPointList);
    }
    entryPointSnapshot.setThreadName(threadName);
    entryPointSnapshot.setTraceId(traceId);
    entryPointSnapshot.setSeqId(seqId);
    return entryPointSnapshot;
  }

  public String formatToString() {
    if (entryPointList == null) {
      return "empty_entry";
    }
    StringBuilder sb = new StringBuilder();
    for (EntryPoint entryPoint : entryPointList) {
      if (sb.length() > 0) {
        sb.append("→");
      }
      sb.append(entryPoint.getEntryPointType());
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EntryPointSnapshot that = (EntryPointSnapshot) o;
    return java.util.Objects.equals(threadName, that.threadName) &&
            java.util.Objects.equals(entryPointList, that.entryPointList) &&
            java.util.Objects.equals(traceId, that.traceId) &&
            java.util.Objects.equals(seqId, that.seqId);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(threadName, entryPointList, traceId, seqId);
  }

}
