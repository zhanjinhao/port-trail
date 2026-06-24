package cn.addenda.porttrail.common.pojo.redis.bo;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;

public interface RedisExecution {

  String RESULT_TYPE_CANCEL = "CANCEL";
  String RESULT_TYPE_SUCCESS = "SUCCESS";
  String RESULT_TYPE_ERROR = "ERROR";

  String getResultType();

  void setEntryPointSnapshot(EntryPointSnapshot entryPointSnapshot);

  EntryPointSnapshot getEntryPointSnapshot();

}
