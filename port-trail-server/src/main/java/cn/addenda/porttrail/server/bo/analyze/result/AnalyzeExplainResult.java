package cn.addenda.porttrail.server.bo.analyze.result;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeExplainResult implements AnalyzeResult {

  @Setter
  private String source;

  @Getter
  private final List<AnalyzeExplainSqlResult> analyzeExplainSqlResultList = new ArrayList<>();

  @Override
  public String getSource() {
    return source;
  }

  @Getter
  @Setter
  @ToString
  public class AnalyzeExplainSqlResult {
    /**
     * EST_PREPARED_STATEMENT_PARAMETER的ID或EST_STATEMENT_SQL的ID
     */
    private Long outerId;
    /**
     * SQL类型
     */
    private String sqlType;

    private final List<AnalyzeExplainSingleResult> analyzeExplainSingleResultList = new ArrayList<>();

    @Getter
    @Setter
    @ToString
    public class AnalyzeExplainSingleResult {
      /**
       * explain的id字段
       */
      private Long explainId;
      /**
       * explain的select_type字段
       */
      private String explainSelectType;
      /**
       * explain的table字段
       */
      private String explainTable;
      /**
       * explain的partitions字段
       */
      private String explainPartitions;
      /**
       * explain的type字段
       */
      private String explainType;
      /**
       * explain的possible_keys字段
       */
      private String explainPossibleKeys;
      /**
       * explain的key字段
       */
      private String explainKey;
      /**
       * explain的key_len字段
       */
      private Integer explainKeyLen;
      /**
       * explain的ref字段
       */
      private String explainRef;
      /**
       * explain的rows字段
       */
      private Integer explainRows;
      /**
       * explain的filtered字段
       */
      private Integer explainFiltered;
      /**
       * explain的extra字段
       */
      private String explainExtra;

    }
  }

}
