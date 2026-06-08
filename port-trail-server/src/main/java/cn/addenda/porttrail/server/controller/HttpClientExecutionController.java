package cn.addenda.porttrail.server.controller;

import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientRequestBo;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientResponseBo;
import cn.addenda.porttrail.common.pojo.httpclient.dto.HttpClientRequestDto;
import cn.addenda.porttrail.common.pojo.httpclient.dto.HttpClientResponseDto;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.server.biz.HttpClientExecutionBiz;
import cn.addenda.porttrail.server.bo.httpclient.HttpClientExecutionRequestBo;
import cn.addenda.porttrail.server.bo.httpclient.HttpClientExecutionResponseBo;
import cn.addenda.porttrail.server.entity.HttpClientExecutionHandleThrowableLog;
import cn.addenda.porttrail.server.entity.PortTrailDeserializeThrowableLog;
import cn.addenda.porttrail.server.manager.HttpClientExecutionHandleThrowableLogManager;
import cn.addenda.porttrail.server.manager.PortTrailDeserializeThrowableLogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("httpClientExecution")
public class HttpClientExecutionController {

  @Autowired
  private HttpClientExecutionBiz httpClientExecutionBiz;

  @Autowired
  private PortTrailDeserializeThrowableLogManager portTrailDeserializeThrowableLogManager;

  @Autowired
  private HttpClientExecutionHandleThrowableLogManager httpClientExecutionHandleThrowableLogManager;

  @PostMapping(value = "receiveHttpClientRequest", consumes = "application/octet-stream")
  public void receiveHttpClientRequest(@RequestBody byte[] bytes) {
    HttpClientRequestDto httpClientRequestDto;
    HttpClientRequestBo httpClientRequestBo;
    try {
      // 处理接收到的字节数组
      bytes = CompressUtils.decompress(bytes);
      httpClientRequestDto = (HttpClientRequestDto) JdkSerializationUtils.deserialize(bytes);
      httpClientRequestBo = new HttpClientRequestBo(httpClientRequestDto);
    } catch (Throwable throwable) {
      portTrailDeserializeThrowableLogManager.insert(bytes, PortTrailDeserializeThrowableLog.DESERIALIZE_TYPE_HTTP_CLIENT_REQUEST, throwable);
      return;
    }

    HttpClientExecutionRequestBo httpClientExecutionRequestBo;
    try {
      httpClientExecutionRequestBo = httpClientExecutionBiz.handleHttpClientRequest(httpClientRequestDto);
    } catch (Throwable throwable) {
      httpClientExecutionHandleThrowableLogManager.insert(bytes, HttpClientExecutionHandleThrowableLog.HANDLE_TYPE_HTTP_CLIENT_REQUEST,
              httpClientRequestBo, httpClientRequestDto.getServiceRuntimeInfo(), throwable);
      return;
    }
  }

  @PostMapping(value = "receiveHttpClientResponse", consumes = "application/octet-stream")
  public void receiveHttpClientResponse(@RequestBody byte[] bytes) {
    HttpClientResponseDto httpClientResponseDto;
    HttpClientResponseBo httpClientResponseBo;

    try {
      // 处理接收到的字节数组
      bytes = CompressUtils.decompress(bytes);
      // 可以通过 JacksonUtils 或其他方式反序列化
      httpClientResponseDto = (HttpClientResponseDto) JdkSerializationUtils.deserialize(bytes);
      httpClientResponseBo = new HttpClientResponseBo(httpClientResponseDto);
    } catch (Throwable throwable) {
      portTrailDeserializeThrowableLogManager.insert(bytes, PortTrailDeserializeThrowableLog.DESERIALIZE_TYPE_HTTP_CLIENT_RESPONSE, throwable);
      return;
    }

    HttpClientExecutionResponseBo httpClientExecutionResponseBo;
    try {
      httpClientExecutionResponseBo = httpClientExecutionBiz.handleHttpClientResponse(httpClientResponseDto);
    } catch (Throwable throwable) {
      httpClientExecutionHandleThrowableLogManager.insert(bytes, HttpClientExecutionHandleThrowableLog.HANDLE_TYPE_HTTP_CLIENT_RESPONSE,
              httpClientResponseBo, httpClientResponseDto.getServiceRuntimeInfo(), throwable);
      return;
    }
  }

}
