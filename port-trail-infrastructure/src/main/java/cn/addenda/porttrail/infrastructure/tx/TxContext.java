package cn.addenda.porttrail.infrastructure.tx;

import cn.addenda.porttrail.common.util.Assert;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TxContext {

  public static final String WITHOUT_TX = "WITHOUT_TX";

  private static final ThreadLocal<String> tl = ThreadLocal.withInitial(() -> null);

  public static void setTxId(String txId) {
    Assert.notNull(txId, "txId must not be null.");
    tl.set(txId);
  }

  public static String getTxId() {
    String txId = tl.get();
    if (txId == null) {
      return WITHOUT_TX;
    }
    return txId;
  }

  public static void remove() {
    tl.remove();
  }

}
