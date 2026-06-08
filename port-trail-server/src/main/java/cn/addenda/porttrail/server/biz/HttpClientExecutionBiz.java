package cn.addenda.porttrail.server.biz;

import cn.addenda.porttrail.common.pojo.httpclient.dto.HttpClientRequestDto;
import cn.addenda.porttrail.common.pojo.httpclient.dto.HttpClientResponseDto;
import cn.addenda.porttrail.server.bo.httpclient.HttpClientExecutionRequestBo;
import cn.addenda.porttrail.server.bo.httpclient.HttpClientExecutionResponseBo;

public interface HttpClientExecutionBiz {

  HttpClientExecutionRequestBo handleHttpClientRequest(HttpClientRequestDto httpClientRequestDto);

  HttpClientExecutionResponseBo handleHttpClientResponse(HttpClientResponseDto httpClientResponseDto);

}
