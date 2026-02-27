package cn.addenda.porttrail.server.biz.analyze;

import cn.addenda.porttrail.server.bo.analyze.param.AnalyzePreparedStatementExecutionParam;
import cn.addenda.porttrail.server.bo.analyze.param.AnalyzeStatementExecutionParam;
import cn.addenda.porttrail.server.bo.analyze.result.AnalyzeResult;
import cn.addenda.porttrail.server.bo.analyze.result.AnalyzeTableColumnResult;
import org.springframework.stereotype.Component;

@Component
public class AnalyzeTableColumnHandler extends AbstractDataSourceAnalyzeHandler<AnalyzeTableColumnResult> {

  @Override
  public String handlerName() {
    return "AnalyzeTableColumnHandler";
  }

  @Override
  public AnalyzeTableColumnResult handle(AnalyzePreparedStatementExecutionParam analyzePreparedStatementExecutionParam) {
    return null;
  }

  @Override
  public AnalyzeTableColumnResult handle(AnalyzeStatementExecutionParam analyzeStatementExecutionParam) {
    return null;
  }

  @Override
  public boolean canConsume(AnalyzeResult analyzeResult) {
    return false;
  }

  @Override
  public void consume(AnalyzeResult analyzeResult) {

  }

}
