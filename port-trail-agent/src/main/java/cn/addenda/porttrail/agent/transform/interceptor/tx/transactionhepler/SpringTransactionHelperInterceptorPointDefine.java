package cn.addenda.porttrail.agent.transform.interceptor.tx.transactionhepler;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.MultiClassNameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class SpringTransactionHelperInterceptorPointDefine implements InterceptorPointDefine {

  private static final String HELPER_NAME = "cn.addenda.component.transaction.PlatformTransactionHelper";

  @Override
  public MultiClassNameMatch getEnhancedClass() {
    return MultiClassNameMatch.of(HELPER_NAME);
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new SpringTransactionHelperDoTransactionInterceptorPoint());
  }

}
