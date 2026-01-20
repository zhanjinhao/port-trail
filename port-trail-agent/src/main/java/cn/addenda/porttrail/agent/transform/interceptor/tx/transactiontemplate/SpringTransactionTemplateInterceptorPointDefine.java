package cn.addenda.porttrail.agent.transform.interceptor.tx.transactiontemplate;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.IndirectMatch;
import cn.addenda.porttrail.agent.transform.match.MethodAnnotationMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class SpringTransactionTemplateInterceptorPointDefine implements InterceptorPointDefine {

  private static final String TRANSACTION_TEMPLATE_NAME = "org.springframework.transaction.support.TransactionTemplate";

  @Override
  public IndirectMatch getEnhancedClass() {
    return MethodAnnotationMatch.of(TRANSACTION_TEMPLATE_NAME);
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new SpringTransactionTemplateExecuteInterceptorPoint());
  }

}
