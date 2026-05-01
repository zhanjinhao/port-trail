package cn.addenda.porttrail.server.controller;

import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletRequestBo;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletResponseBo;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestDto;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletResponseDto;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.server.biz.ServletExecutionBiz;
import cn.addenda.porttrail.server.bo.servlet.ServletExecutionRequestBo;
import cn.addenda.porttrail.server.entity.PortTrailDeserializeThrowableLog;
import cn.addenda.porttrail.server.entity.ServletExecutionHandleThrowableLog;
import cn.addenda.porttrail.server.manager.PortTrailDeserializeThrowableLogManager;
import cn.addenda.porttrail.server.manager.ServletExecutionHandleThrowableLogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("servletExecution")
public class ServletExecutionController {

  @Autowired
  private ServletExecutionBiz servletExecutionBiz;

  @Autowired
  private PortTrailDeserializeThrowableLogManager portTrailDeserializeThrowableLogManager;

  @Autowired
  private ServletExecutionHandleThrowableLogManager servletExecutionHandleThrowableLogManager;

  @PostMapping(value = "receiveServletRequest", consumes = "application/octet-stream")
  public void receiveServletRequest(@RequestBody byte[] bytes) {
    ServletRequestDto servletRequestDto;
    ServletRequestBo servletRequestBo;
    try {
      // 处理接收到的字节数组
      bytes = CompressUtils.decompress(bytes);
      servletRequestDto = (ServletRequestDto) JdkSerializationUtils.deserialize(bytes);
      servletRequestBo = new ServletRequestBo(servletRequestDto);
    } catch (Throwable throwable) {
      portTrailDeserializeThrowableLogManager.insert(bytes, PortTrailDeserializeThrowableLog.DESERIALIZE_TYPE_SERVLET_REQUEST, throwable);
      return;
    }

    ServletExecutionRequestBo servletExecutionRequestBo;
    try {
      servletExecutionRequestBo = servletExecutionBiz.handleServletRequest(servletRequestDto);
    } catch (Throwable throwable) {
      servletExecutionHandleThrowableLogManager.insert(bytes, ServletExecutionHandleThrowableLog.HANDLE_TYPE_SERVLET_REQUEST,
              servletRequestBo, servletRequestDto.getServiceRuntimeInfo(), throwable);
      return;
    }
  }

  @PostMapping(value = "receiveServletResponse", consumes = "application/octet-stream")
  public void receiveServletResponse(@RequestBody byte[] bytes) {
    // 处理接收到的字节数组
    bytes = CompressUtils.decompress(bytes);
    // 可以通过 JacksonUtils 或其他方式反序列化
    ServletResponseDto servletResponseDto = (ServletResponseDto) JdkSerializationUtils.deserialize(bytes);
    ServletResponseBo servletResponseBo = new ServletResponseBo(servletResponseDto);
    System.out.println(JacksonUtils.toStr(servletResponseBo) + " of " + JacksonUtils.toStr(servletResponseDto.getServiceRuntimeInfo()));
  }

}
