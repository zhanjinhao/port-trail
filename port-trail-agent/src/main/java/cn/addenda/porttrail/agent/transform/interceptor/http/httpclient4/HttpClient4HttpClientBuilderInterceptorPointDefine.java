package cn.addenda.porttrail.agent.transform.interceptor.http.httpclient4;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.NameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class HttpClient4HttpClientBuilderInterceptorPointDefine implements InterceptorPointDefine {

  @Override
  public NameMatch getEnhancedClass() {
    return NameMatch.of("org.apache.http.impl.client.HttpClientBuilder");
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new HttpClient4HttpClientBuilderInterceptorPoint());
  }

}
