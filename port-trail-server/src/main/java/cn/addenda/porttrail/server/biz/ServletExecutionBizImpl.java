package cn.addenda.porttrail.server.biz;

import cn.addenda.component.base.datetime.DateUtils;
import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletRequestBo;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestDto;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletResponseDto;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.server.bo.servlet.ServletExecutionRequestBo;
import cn.addenda.porttrail.server.bo.servlet.ServletExecutionResponseBo;
import cn.addenda.porttrail.server.curd.ServletExecutionRequestCurder;
import cn.addenda.porttrail.server.curd.ServletExecutionResponseCurder;
import cn.addenda.porttrail.server.entity.ServletExecutionRequest;
import cn.addenda.porttrail.server.entity.ServletExecutionResponse;
import cn.addenda.porttrail.server.util.CurlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServletExecutionBizImpl implements ServletExecutionBiz {

  @Autowired
  private PlatformTransactionHelper transactionHelperNew;

  @Autowired
  private ServletExecutionRequestCurder servletExecutionRequestCurder;

  @Autowired
  private ServletExecutionResponseCurder servletExecutionResponseCurder;

  @Override
  public ServletExecutionRequestBo handleServletRequest(ServletRequestDto servletRequestDto) {
    ServiceRuntimeInfo serviceRuntimeInfo = servletRequestDto.getServiceRuntimeInfo();

    ServletExecutionRequest param = ServletExecutionRequest.ofParam();
    param.setSystemCode(serviceRuntimeInfo.getSystemCode());
    param.setServiceName(serviceRuntimeInfo.getServiceName());
    param.setImageName(serviceRuntimeInfo.getImageName());
    param.setEnv(serviceRuntimeInfo.getEnv());
    param.setInstanceId(serviceRuntimeInfo.getInstanceId());
    param.setExecutionId(servletRequestDto.getExecutionId());
    param.setVersion(servletRequestDto.getVersion());
    param.setScheme(servletRequestDto.getScheme());
    param.setMethod(servletRequestDto.getMethod());
    param.setUri(servletRequestDto.getUri());
    param.setQueryString(servletRequestDto.getQueryString());
    param.setContentType(servletRequestDto.getContentType());
    param.setCharsetEncoding(servletRequestDto.getCharsetEncoding());
    param.setDateTime(DateUtils.timestampToLocalDateTime(servletRequestDto.getDatetime()));
    param.setAllContentLength(servletRequestDto.getAllContentLength());
    param.setContentLength(servletRequestDto.getContentLength());
    param.setLocale(JacksonUtils.toStr(servletRequestDto.getLocale()));
    param.setHeaders(JacksonUtils.toStr(servletRequestDto.getHeaderMap()));
    param.setBody(CompressUtils.compress(JdkSerializationUtils.serialize(servletRequestDto.getBody())));
    if (MediaType.ifRequestTextContentType(servletRequestDto.getContentType())) {
      ServletRequestBo servletRequestBo = new ServletRequestBo(servletRequestDto);
      param.setBodyText((String) servletRequestBo.getBody());
      param.setCurl(CurlUtils.toCurl(servletRequestBo));
    }

    transactionHelperNew.doTransaction(() -> {
      servletExecutionRequestCurder.insert(param);
    });

    return new ServletExecutionRequestBo(param);
  }

  @Override
  public ServletExecutionResponseBo handleServletResponse(ServletResponseDto servletResponseDto) {
    ServiceRuntimeInfo serviceRuntimeInfo = servletResponseDto.getServiceRuntimeInfo();

    ServletExecutionResponse param = ServletExecutionResponse.ofParam();
    param.setSystemCode(serviceRuntimeInfo.getSystemCode());
    param.setServiceName(serviceRuntimeInfo.getServiceName());
    param.setImageName(serviceRuntimeInfo.getImageName());
    param.setEnv(serviceRuntimeInfo.getEnv());
    param.setInstanceId(serviceRuntimeInfo.getInstanceId());
    param.setExecutionId(servletResponseDto.getExecutionId());
    param.setContentType(servletResponseDto.getContentType());
    param.setCharsetEncoding(servletResponseDto.getCharsetEncoding());
    param.setDateTime(DateUtils.timestampToLocalDateTime(servletResponseDto.getDatetime()));
    param.setContentLength(servletResponseDto.getContentLength());
    param.setLocale(JacksonUtils.toStr(servletResponseDto.getLocale()));
    param.setStatus(servletResponseDto.getStatus());
    param.setHeaders(JacksonUtils.toStr(servletResponseDto.getHeaderMap()));
    param.setBody(CompressUtils.compress(JdkSerializationUtils.serialize(servletResponseDto.getBody())));
    if (MediaType.ifRequestTextContentType(servletResponseDto.getContentType())) {
      param.setBodyText((String) JdkSerializationUtils.deserialize(servletResponseDto.getBody()));
    }

    transactionHelperNew.doTransaction(() -> {
      servletExecutionResponseCurder.insert(param);
    });

    return new ServletExecutionResponseBo(param);
  }

}
