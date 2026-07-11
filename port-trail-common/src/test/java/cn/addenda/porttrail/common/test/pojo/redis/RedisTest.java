package cn.addenda.porttrail.common.test.pojo.redis;

import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.common.pojo.redis.bo.RedisBo;
import cn.addenda.porttrail.common.pojo.redis.bo.RedisExecution;
import cn.addenda.porttrail.common.pojo.redis.dto.RedisDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class RedisTest {

  @Test
  void testDtoFromBo() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.REMOTE_REDIS, "setCommand")),
            "main", "trace-r1", 1L);
    RedisBo bo = new RedisBo(RedisExecution.RESULT_TYPE_SUCCESS, "SET");
    bo.setCommandArgString("key value EX 60");
    bo.setPeer("127.0.0.1:6379");
    bo.setResult("OK");
    bo.setError(null);
    bo.setStartTime(1000L);
    bo.setEndTime(1050L);
    bo.setCost(50);
    bo.setEntryPointSnapshot(snapshot);

    RedisDto dto = new RedisDto(bo);

    Assertions.assertEquals(RedisExecution.RESULT_TYPE_SUCCESS, dto.getResultType());
    Assertions.assertEquals("SET", dto.getCommandName());
    Assertions.assertEquals("key value EX 60", dto.getCommandArgString());
    Assertions.assertEquals("127.0.0.1:6379", dto.getPeer());
    Assertions.assertEquals("OK", dto.getResult());
    Assertions.assertNull(dto.getError());
    Assertions.assertEquals(1000L, dto.getStartTime());
    Assertions.assertEquals(1050L, dto.getEndTime());
    Assertions.assertEquals(50, dto.getCost());
    Assertions.assertEquals(snapshot, dto.getEntryPointSnapshot());
  }

  @Test
  void testDtoFromBoWithError() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.REMOTE_REDIS, "errorCommand")),
            "error-thread", "trace-re", 99L);
    RedisBo bo = new RedisBo(RedisExecution.RESULT_TYPE_ERROR, "INCR");
    bo.setCommandArgString("counter");
    bo.setPeer("10.0.0.1:6379");
    bo.setResult(null);
    bo.setError("ERR wrong number of arguments");
    bo.setStartTime(5000L);
    bo.setEndTime(5001L);
    bo.setCost(1);
    bo.setEntryPointSnapshot(snapshot);

    RedisDto dto = new RedisDto(bo);

    Assertions.assertEquals(RedisExecution.RESULT_TYPE_ERROR, dto.getResultType());
    Assertions.assertEquals("INCR", dto.getCommandName());
    Assertions.assertEquals("counter", dto.getCommandArgString());
    Assertions.assertEquals("10.0.0.1:6379", dto.getPeer());
    Assertions.assertNull(dto.getResult());
    Assertions.assertEquals("ERR wrong number of arguments", dto.getError());
    Assertions.assertEquals(5000L, dto.getStartTime());
    Assertions.assertEquals(5001L, dto.getEndTime());
    Assertions.assertEquals(1, dto.getCost());
    Assertions.assertEquals(snapshot, dto.getEntryPointSnapshot());
  }

  @Test
  void testBoFromDto() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.REMOTE_REDIS, "getCommand")),
            "worker", "trace-r2", 2L);
    RedisDto dto = new RedisDto(RedisExecution.RESULT_TYPE_ERROR, "GET");
    dto.setCommandArgString("key");
    dto.setPeer("192.168.1.1:6379");
    dto.setResult(null);
    dto.setError("Connection timeout");
    dto.setStartTime(2000L);
    dto.setEndTime(3000L);
    dto.setCost(1000);
    dto.setEntryPointSnapshot(snapshot);

    RedisBo bo = new RedisBo(dto);

    Assertions.assertEquals(RedisExecution.RESULT_TYPE_ERROR, bo.getResultType());
    Assertions.assertEquals("GET", bo.getCommandName());
    Assertions.assertEquals("key", bo.getCommandArgString());
    Assertions.assertEquals("192.168.1.1:6379", bo.getPeer());
    Assertions.assertNull(bo.getResult());
    Assertions.assertEquals("Connection timeout", bo.getError());
    Assertions.assertEquals(2000L, bo.getStartTime());
    Assertions.assertEquals(3000L, bo.getEndTime());
    Assertions.assertEquals(1000, bo.getCost());
    Assertions.assertEquals(snapshot, bo.getEntryPointSnapshot());
  }

  @Test
  void testFullRoundTrip() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.REMOTE_REDIS, "hgetCommand")),
            "roundtrip", "trace-r3", 3L);
    RedisBo bo1 = new RedisBo(RedisExecution.RESULT_TYPE_SUCCESS, "HGET");
    bo1.setCommandArgString("hashKey field");
    bo1.setPeer("redis://localhost:6379");
    bo1.setResult("value");
    bo1.setError(null);
    bo1.setStartTime(100L);
    bo1.setEndTime(200L);
    bo1.setCost(100);
    bo1.setEntryPointSnapshot(snapshot);

    RedisDto dto1 = new RedisDto(bo1);
    RedisBo bo2 = new RedisBo(dto1);
    RedisDto dto2 = new RedisDto(bo2);

    Assertions.assertEquals(bo1.getResultType(), bo2.getResultType());
    Assertions.assertEquals(bo1.getCommandName(), bo2.getCommandName());
    Assertions.assertEquals(bo1.getCommandArgString(), bo2.getCommandArgString());
    Assertions.assertEquals(bo1.getPeer(), bo2.getPeer());
    Assertions.assertEquals(bo1.getResult(), bo2.getResult());
    Assertions.assertEquals(bo1.getError(), bo2.getError());
    Assertions.assertEquals(bo1.getStartTime(), bo2.getStartTime());
    Assertions.assertEquals(bo1.getEndTime(), bo2.getEndTime());
    Assertions.assertEquals(bo1.getCost(), bo2.getCost());

    Assertions.assertEquals(dto1.getResultType(), dto2.getResultType());
    Assertions.assertEquals(dto1.getCommandName(), dto2.getCommandName());
    Assertions.assertEquals(dto1.getCommandArgString(), dto2.getCommandArgString());
    Assertions.assertEquals(dto1.getPeer(), dto2.getPeer());
    Assertions.assertEquals(dto1.getResult(), dto2.getResult());
    Assertions.assertEquals(dto1.getError(), dto2.getError());
    Assertions.assertEquals(dto1.getStartTime(), dto2.getStartTime());
    Assertions.assertEquals(dto1.getEndTime(), dto2.getEndTime());
    Assertions.assertEquals(dto1.getCost(), dto2.getCost());
  }

}
