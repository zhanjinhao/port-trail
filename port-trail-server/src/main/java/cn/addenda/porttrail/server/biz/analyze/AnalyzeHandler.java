package cn.addenda.porttrail.server.biz.analyze;

import cn.addenda.porttrail.server.bo.analyze.param.AnalyzePreparedStatementExecutionParam;
import cn.addenda.porttrail.server.bo.analyze.param.AnalyzeStatementExecutionParam;
import cn.addenda.porttrail.server.bo.analyze.result.AnalyzeResult;

public interface AnalyzeHandler<T extends AnalyzeResult> {

  String handlerName();

  T handle(AnalyzePreparedStatementExecutionParam analyzePreparedStatementExecutionParam);

  T handle(AnalyzeStatementExecutionParam analyzeStatementExecutionParam);

  boolean canConsume(AnalyzeResult analyzeResult);

  void consume(AnalyzeResult analyzeResult);

}
