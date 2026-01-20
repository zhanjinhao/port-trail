package cn.addenda.porttrail.server.controller;

import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.porttrail.common.pojo.http.bo.HttpRequestBo;
import cn.addenda.porttrail.common.pojo.http.bo.HttpResponseBo;
import cn.addenda.porttrail.common.pojo.http.dto.HttpRequestDto;
import cn.addenda.porttrail.common.pojo.http.dto.HttpResponseDto;
import cn.addenda.porttrail.common.util.SerializationUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("httpExecution")
public class HttpExecutionController {

  @PostMapping(value = "receiveHttpRequest", consumes = "application/octet-stream")
  public void receiveHttpRequest(@RequestBody byte[] bytes) {
    // 处理接收到的字节数组
    // 可以通过 JacksonUtils 或其他方式反序列化
    HttpRequestDto httpRequestDto = (HttpRequestDto) SerializationUtils.deserialize(bytes);
    HttpRequestBo httpRequestBo =
            HttpRequestBo.createByHttpRequestDto(httpRequestDto);
    System.out.println(JacksonUtils.toStr(httpRequestBo) + " of " + JacksonUtils.toStr(httpRequestDto.getServiceRuntimeInfo()));
  }

  @PostMapping(value = "receiveHttpResponse", consumes = "application/octet-stream")
  public void receiveHttpResponse(@RequestBody byte[] bytes) {
    // 处理接收到的字节数组
    // 可以通过 JacksonUtils 或其他方式反序列化
    HttpResponseDto httpResponseDto = (HttpResponseDto) SerializationUtils.deserialize(bytes);
    HttpResponseBo httpResponseBo =
            HttpResponseBo.createByHttpResponseDto(httpResponseDto);
    System.out.println(JacksonUtils.toStr(httpResponseBo) + " of " + JacksonUtils.toStr(httpResponseDto.getServiceRuntimeInfo()));
  }

}
