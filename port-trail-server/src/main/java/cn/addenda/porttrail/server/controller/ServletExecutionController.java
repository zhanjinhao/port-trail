package cn.addenda.porttrail.server.controller;

import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletRequestBo;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletResponseBo;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestDto;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletResponseDto;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("servletExecution")
public class ServletExecutionController {

  @PostMapping(value = "receiveServletRequest", consumes = "application/octet-stream")
  public void receiveServletRequest(@RequestBody byte[] bytes) {
    // 处理接收到的字节数组
    bytes = CompressUtils.decompress(bytes);
    // 可以通过 JacksonUtils 或其他方式反序列化
    ServletRequestDto servletRequestDto = (ServletRequestDto) JdkSerializationUtils.deserialize(bytes);
    ServletRequestBo servletRequestBo = new ServletRequestBo(servletRequestDto);
    System.out.println(JacksonUtils.toStr(servletRequestBo) + " of " + JacksonUtils.toStr(servletRequestDto.getServiceRuntimeInfo()));
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
