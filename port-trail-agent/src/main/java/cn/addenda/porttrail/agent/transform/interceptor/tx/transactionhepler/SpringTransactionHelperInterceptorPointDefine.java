package cn.addenda.porttrail.agent.transform.interceptor.tx.transactionhepler;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.NameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class SpringTransactionHelperInterceptorPointDefine implements InterceptorPointDefine {

  public static final String TRANSACTION_HELPER_NAME =
          "cn.addenda.component.transaction.PlatformTransactionHelper";

  public static final String SUPPLIER_ADAPTER_NAME =
          TRANSACTION_HELPER_NAME + "$TransactionHelperSupplierAdapter";

  @Override
  public NameMatch getEnhancedClass() {
    return NameMatch.of(SUPPLIER_ADAPTER_NAME);
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new SpringTransactionHelperDoTransactionInterceptorPoint());
  }

}
