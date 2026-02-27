package cn.addenda.porttrail.server.bo.analyze.result;

public interface AnalyzeResult {

  String SOURCE_PREPARED_STATEMENT_EXECUTION = "PREPARED_STATEMENT_EXECUTION";

  String SOURCE_PREPARED_STATEMENT_PARAMETER = "PREPARED_STATEMENT_PARAMETER";

  String SOURCE_STATEMENT_SQL = "STATEMENT_SQL";

  String getSource();

}
