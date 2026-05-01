package cn.addenda.porttrail.server.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * handle阶段异常时记录的日志(DbExecutionHandleThrowableLog)实体类
 *
 * @author makejava
 * @since 2026-03-06 22:44:42
 */
@Setter
@Getter
@ToString
public class DbExecutionHandleThrowableLog extends SimpleBaseModel implements Serializable {

  public static final String HANDLE_TYPE_STATEMENT_EXECUTION = "STATEMENT_EXECUTION";
  public static final String HANDLE_TYPE_PREPARED_STATEMENT_EXECUTION = "PREPARED_STATEMENT_EXECUTION";

  private static final long serialVersionUID = -69208116650213244L;
  /**
   * 主键
   */
  private Long id;
  /**
   * 系统编码
   */
  private String systemCode;
  /**
   * 服务名
   */
  private String serviceName;
  /**
   * 镜像名
   */
  private String imageName;
  /**
   * 环境
   */
  private String env;
  /**
   * 实例ID
   */
  private String instanceId;
  /**
   * dataSourcePortTrailId
   */
  private String dataSourcePortTrailId;
  /**
   * connectionPortTrailId
   */
  private String connectionPortTrailId;
  /**
   * statementPortTrailId
   */
  private String statementPortTrailId;
  /**
   * 处理类型
   */
  private String handleType;
  /**
   * 参数的字节数组
   */
  private byte[] bytes;
  /**
   * 参数的BO的JSON
   */
  private String boJson;
  /**
   * 异常栈
   */
  private String throwableStack;

  public static DbExecutionHandleThrowableLog ofParam() {
    return new DbExecutionHandleThrowableLog();
  }

}
