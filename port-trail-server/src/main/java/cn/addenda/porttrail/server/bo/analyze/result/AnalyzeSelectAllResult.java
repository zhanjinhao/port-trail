package cn.addenda.porttrail.server.bo.analyze.result;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeSelectAllResult implements AnalyzeResult {

  @Setter
  private String source;

  @Getter
  private final List<AnalyzeSelectAllResult.AnalyzeSelectAllSingleResult> analyzeSelectAllSingleResultList = new ArrayList<>();

  @Override
  public String getSource() {
    return source;
  }

  @Getter
  @Setter
  @ToString
  public class AnalyzeSelectAllSingleResult {

    private Long outerId;

    private Integer ifSelectAll;

  }

}
