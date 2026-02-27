package cn.addenda.porttrail.server.bo.analyze.result;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class AnalyzeTableNameResult implements AnalyzeResult {

  @Setter
  private String source;

  @Getter
  private final List<AnalyzeTableNameSingleResult> analyzeTableNameSingleResultList = new ArrayList<>();

  @Override
  public String getSource() {
    return source;
  }

  @Getter
  @Setter
  @ToString
  public class AnalyzeTableNameSingleResult {

    private String sqlType;

    private Long outerId;

    private String tableNames;

    private Integer tableCount;

  }

}
