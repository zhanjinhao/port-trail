package cn.addenda.porttrail.server.biz;

import cn.addenda.component.base.datetime.DateUtils;
import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.pojo.FormDataList;
import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientRequestBo;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientResponseBo;
import cn.addenda.porttrail.common.pojo.httpclient.dto.HttpClientRequestDto;
import cn.addenda.porttrail.common.pojo.httpclient.dto.HttpClientResponseDto;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.server.bo.httpclient.HttpClientExecutionRequestBo;
import cn.addenda.porttrail.server.bo.httpclient.HttpClientExecutionResponseBo;
import cn.addenda.porttrail.server.curd.HttpClientExecutionRequestCurder;
import cn.addenda.porttrail.server.curd.HttpClientExecutionResponseCurder;
import cn.addenda.porttrail.server.entity.HttpClientExecutionRequest;
import cn.addenda.porttrail.server.entity.HttpClientExecutionResponse;
import cn.addenda.porttrail.server.util.HttpClientCurlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpClientExecutionBizImpl implements HttpClientExecutionBiz {

  @Autowired
  private PlatformTransactionHelper transactionHelperNew;

  @Autowired
  private HttpClientExecutionRequestCurder httpClientExecutionRequestCurder;

  @Autowired
  private HttpClientExecutionResponseCurder httpClientExecutionResponseCurder;

  @Override
  public HttpClientExecutionRequestBo handleHttpClientRequest(HttpClientRequestDto httpClientRequestDto) {
    ServiceRuntimeInfo serviceRuntimeInfo = httpClientRequestDto.getServiceRuntimeInfo();

    HttpClientExecutionRequest param = HttpClientExecutionRequest.ofParam();
    param.setSystemCode(serviceRuntimeInfo.getSystemCode());
    param.setServiceName(serviceRuntimeInfo.getServiceName());
    param.setImageName(serviceRuntimeInfo.getImageName());
    param.setEnv(serviceRuntimeInfo.getEnv());
    param.setInstanceId(serviceRuntimeInfo.getInstanceId());
    param.setExecutionId(httpClientRequestDto.getExecutionId());
    param.setClientName(httpClientRequestDto.getClientName());
    param.setVersion(httpClientRequestDto.getVersion());
    param.setScheme(httpClientRequestDto.getScheme());
    param.setMethod(httpClientRequestDto.getMethod());
    param.setUri(httpClientRequestDto.getUri());
    param.setQueryString(httpClientRequestDto.getQueryString());
    param.setContentType(httpClientRequestDto.getContentType());
    param.setCharsetEncoding(httpClientRequestDto.getCharsetEncoding());
    param.setDateTime(DateUtils.timestampToLocalDateTime(httpClientRequestDto.getDatetime()));
    param.setContentLength(httpClientRequestDto.getContentLength());
    param.setLocale(JacksonUtils.toStr(httpClientRequestDto.getLocale()));
    param.setHeaders(JacksonUtils.toStr(httpClientRequestDto.getHeaderMap()));
    param.setBody(CompressUtils.compress(JdkSerializationUtils.serialize(httpClientRequestDto.getBody())));

    HttpClientRequestBo httpClientRequestBo = new HttpClientRequestBo(httpClientRequestDto);
    if (httpClientRequestBo.getBody() instanceof String) {
      param.setBodyText((String) httpClientRequestBo.getBody());
    }
    if (httpClientRequestBo.getBody() instanceof FormDataList) {
      param.setBodyText(JacksonUtils.toStr(httpClientRequestBo.getBody()));
    }
    param.setCurl(HttpClientCurlUtils.toCurl(httpClientRequestBo));

    transactionHelperNew.doTransaction(() -> {
      httpClientExecutionRequestCurder.insert(param);
    });

    return new HttpClientExecutionRequestBo(param);
  }

  @Override
  public HttpClientExecutionResponseBo handleHttpClientResponse(HttpClientResponseDto httpClientResponseDto) {
    ServiceRuntimeInfo serviceRuntimeInfo = httpClientResponseDto.getServiceRuntimeInfo();

    HttpClientExecutionResponse param = HttpClientExecutionResponse.ofParam();
    param.setSystemCode(serviceRuntimeInfo.getSystemCode());
    param.setServiceName(serviceRuntimeInfo.getServiceName());
    param.setImageName(serviceRuntimeInfo.getImageName());
    param.setEnv(serviceRuntimeInfo.getEnv());
    param.setInstanceId(serviceRuntimeInfo.getInstanceId());
    param.setExecutionId(httpClientResponseDto.getExecutionId());
    param.setClientName(httpClientResponseDto.getClientName());
    param.setContentType(httpClientResponseDto.getContentType());
    param.setCharsetEncoding(httpClientResponseDto.getCharsetEncoding());
    param.setDateTime(DateUtils.timestampToLocalDateTime(httpClientResponseDto.getDatetime()));
    param.setContentLength(httpClientResponseDto.getContentLength());
    param.setLocale(JacksonUtils.toStr(httpClientResponseDto.getLocale()));
    param.setStatus(httpClientResponseDto.getStatus());
    param.setHeaders(JacksonUtils.toStr(httpClientResponseDto.getHeaderMap()));
    param.setBody(CompressUtils.compress(JdkSerializationUtils.serialize(httpClientResponseDto.getBody())));

    HttpClientResponseBo httpClientResponseBo = new HttpClientResponseBo(httpClientResponseDto);
    if (httpClientResponseBo.getBody() instanceof String) {
      param.setBodyText((String) httpClientResponseBo.getBody());
    }

    transactionHelperNew.doTransaction(() -> {
      httpClientExecutionResponseCurder.insert(param);
    });

    return new HttpClientExecutionResponseBo(param);
  }
}
