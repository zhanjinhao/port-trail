package cn.addenda.porttrail.server.biz;

import cn.addenda.component.base.datetime.DateUtils;
import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletRequestBo;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestDto;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.server.bo.servlet.ServletExecutionRequestBo;
import cn.addenda.porttrail.server.curd.ServletExecutionRequestCurder;
import cn.addenda.porttrail.server.entity.ServletExecutionRequest;
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

  @Override
  public ServletExecutionRequestBo handleServletRequest(ServletRequestDto servletRequestDto) {
    ServiceRuntimeInfo serviceRuntimeInfo = servletRequestDto.getServiceRuntimeInfo();

    ServletRequestBo servletRequestBo = new ServletRequestBo(servletRequestDto);

    ServletExecutionRequest param = ServletExecutionRequest.ofParam();
    param.setSystemCode(serviceRuntimeInfo.getSystemCode());
    param.setServiceName(serviceRuntimeInfo.getServiceName());
    param.setImageName(serviceRuntimeInfo.getImageName());
    param.setEnv(serviceRuntimeInfo.getEnv());
    param.setInstanceId(serviceRuntimeInfo.getInstanceId());
    param.setExecutionId(servletRequestBo.getExecutionId());
    param.setVersion(servletRequestBo.getVersion());
    param.setScheme(servletRequestBo.getScheme());
    param.setMethod(servletRequestBo.getMethod());
    param.setUri(servletRequestBo.getUri());
    param.setQueryString(servletRequestBo.getQueryString());
    param.setContentType(servletRequestBo.getContentType());
    param.setCharsetEncoding(servletRequestBo.getCharsetEncoding());
    param.setDateTime(DateUtils.timestampToLocalDateTime(servletRequestBo.getDatetime()));
    param.setAllContentLength(servletRequestBo.getAllContentLength());
    param.setContentLength(servletRequestBo.getContentLength());
    param.setLocale(JacksonUtils.toStr(servletRequestBo.getLocale()));
    param.setHeaders(JacksonUtils.toStr(servletRequestBo.getHeaderMap()));
    param.setBody(CompressUtils.compress(JdkSerializationUtils.serialize(servletRequestBo.getBody())));
    if (MediaType.ifRequestTextContentType(servletRequestBo.getContentType())) {
      param.setBodyText((String) servletRequestBo.getBody());
      param.setCurl(CurlUtils.toCurl(servletRequestBo));
    }

    transactionHelperNew.doTransaction(() -> {
      servletExecutionRequestCurder.insert(param);
    });

    return new ServletExecutionRequestBo(param);
  }

}
