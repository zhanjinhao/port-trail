package cn.addenda.porttrail.server.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * ServletExecution在handle阶段异常时记录的日志(ServletExecutionHandleThrowableLog)实体类
 *
 * @author makejava
 * @since 2026-05-01 16:40:10
 */
@Setter
@Getter
@ToString
public class ServletExecutionHandleThrowableLog extends SimpleBaseModel implements Serializable {

  public static final String HANDLE_TYPE_SERVLET_REQUEST = "SERVLET_REQUEST";

  private static final long serialVersionUID = -99917216965182332L;
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
   * ExecutionId
   */
  private String executionId;
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

  public static ServletExecutionHandleThrowableLog ofParam() {
    return new ServletExecutionHandleThrowableLog();
  }

}
