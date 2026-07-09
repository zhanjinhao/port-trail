package cn.addenda.porttrail.agent.transform.interceptor.tx.transactiontemplate;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.NameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class SpringTransactionTemplateInterceptorPointDefine implements InterceptorPointDefine {

  public static final String TRANSACTION_TEMPLATE_NAME = "org.springframework.transaction.support.TransactionTemplate";

  @Override
  public NameMatch getEnhancedClass() {
    return NameMatch.of(TRANSACTION_TEMPLATE_NAME);
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new SpringTransactionTemplateExecuteInterceptorPoint());
  }

}
