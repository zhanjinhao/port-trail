package cn.addenda.porttrail.common.pojo.db.bo;

import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementExecutionDto;
import cn.addenda.porttrail.common.util.ArrayUtils;
import cn.addenda.porttrail.common.util.StringUtils;
import lombok.*;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 1、一次{@link PreparedStatement#executeBatch()}或{@link PreparedStatement#executeLargeBatch()} 执行，preparedStatementParameterWrapperList的size()大于1。 <br/>
 * 2、一次{@link PreparedStatement}的其它execute执行，preparedStatementParameterWrapperList的size()等于1。
 */
@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PreparedStatementExecutionBo extends AbstractStatementExecutionBo {

  private String parameterizedSql;

  private List<PreparedStatementParameter> preparedStatementParameterList;

  public PreparedStatementExecutionBo(String parameterizedSql) {
    super();
    this.parameterizedSql = parameterizedSql;
    this.preparedStatementParameterList = new ArrayList<>();
  }

  public PreparedStatementExecutionBo(String dataSourcePortTrailId, String connectionPortTrailId, String statementPortTrailId,
                                      String statementState, String parameterizedSql, PreparedStatementParameter preparedStatementParameter, String txId, long start, long end) {
    super(dataSourcePortTrailId, connectionPortTrailId, statementPortTrailId, statementState, txId, start, end);
    this.parameterizedSql = parameterizedSql;
    this.preparedStatementParameterList = ArrayUtils.asArrayList(preparedStatementParameter);
  }

  public void clear() {
    this.preparedStatementParameterList = new ArrayList<>();
  }

  @Override
  public String getDbExecutionType() {
    return DB_EXECUTION_TYPE_PREPARED_STATEMENT;
  }

  public PreparedStatementExecutionBo(PreparedStatementExecutionDto preparedStatementExecutionDto) {
    this.setDataSourcePortTrailId(preparedStatementExecutionDto.getDataSourcePortTrailId());
    this.setConnectionPortTrailId(preparedStatementExecutionDto.getConnectionPortTrailId());
    this.setStatementPortTrailId(preparedStatementExecutionDto.getStatementPortTrailId());
    this.setStatementState(preparedStatementExecutionDto.getStatementState());
    this.setTxId(preparedStatementExecutionDto.getTxId());
    this.setStart(preparedStatementExecutionDto.getStart());
    this.setEnd(preparedStatementExecutionDto.getEnd());
    this.setEntryPointSnapshot(preparedStatementExecutionDto.getEntryPointSnapshot());
    this.setParameterizedSql(preparedStatementExecutionDto.getParameterizedSql());
    this.setPreparedStatementParameterList(
            preparedStatementExecutionDto.getPreparedStatementParameterDtoList().stream()
                    .map(PreparedStatementParameter::new).collect(Collectors.toList())
    );
  }

  @Override
  public boolean ifKeepAlive() {
    return StringUtils.endsWithIgnoreBlankAndCase(parameterizedSql, "select1")
            || StringUtils.startsWithIgnoreBlankAndCase(parameterizedSql, "select1fromdual");
  }

}
