package cn.addenda.porttrail.server.biz;

import cn.addenda.component.base.datetime.DateUtils;
import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletRequestBo;
import cn.addenda.porttrail.common.pojo.FormDataList;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletResponseBo;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestDto;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletResponseDto;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.server.bo.servlet.ServletExecutionRequestBo;
import cn.addenda.porttrail.server.bo.servlet.ServletExecutionResponseBo;
import cn.addenda.porttrail.server.curd.ServletExecutionRequestEntityCurder;
import cn.addenda.porttrail.server.curd.ServletExecutionResponseEntityCurder;
import cn.addenda.porttrail.server.entity.ServletExecutionRequestEntity;
import cn.addenda.porttrail.server.entity.ServletExecutionResponseEntity;
import cn.addenda.porttrail.server.util.ServletCurlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServletExecutionBizImpl implements ServletExecutionBiz {

  @Autowired
  private PlatformTransactionHelper transactionHelperNew;

  @Autowired
  private ServletExecutionRequestEntityCurder servletExecutionRequestEntityCurder;

  @Autowired
  private ServletExecutionResponseEntityCurder servletExecutionResponseEntityCurder;

  @Override
  public ServletExecutionRequestBo handleServletRequest(ServletRequestDto servletRequestDto) {
    ServiceRuntimeInfo serviceRuntimeInfo = servletRequestDto.getServiceRuntimeInfo();

    ServletExecutionRequestEntity param = ServletExecutionRequestEntity.ofParam();
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

    ServletRequestBo servletRequestBo = new ServletRequestBo(servletRequestDto);
    if (servletRequestBo.getBody() instanceof String) {
      param.setBodyText((String) servletRequestBo.getBody());
    }
    if (servletRequestBo.getBody() instanceof FormDataList) {
      param.setBodyText(JacksonUtils.toStr(servletRequestBo.getBody()));
    }
    param.setCurl(ServletCurlUtils.toCurl(servletRequestBo));

    transactionHelperNew.doTransaction(() -> {
      servletExecutionRequestEntityCurder.insert(param);
    });

    return new ServletExecutionRequestBo(param);
  }

  @Override
  public ServletExecutionResponseBo handleServletResponse(ServletResponseDto servletResponseDto) {
    ServiceRuntimeInfo serviceRuntimeInfo = servletResponseDto.getServiceRuntimeInfo();

    ServletExecutionResponseEntity param = ServletExecutionResponseEntity.ofParam();
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

    ServletResponseBo servletResponseBo = new ServletResponseBo(servletResponseDto);
    if (servletResponseBo.getBody() instanceof String) {
      param.setBodyText((String) servletResponseBo.getBody());
    }

    transactionHelperNew.doTransaction(() -> {
      servletExecutionResponseEntityCurder.insert(param);
    });

    return new ServletExecutionResponseBo(param);
  }

}
