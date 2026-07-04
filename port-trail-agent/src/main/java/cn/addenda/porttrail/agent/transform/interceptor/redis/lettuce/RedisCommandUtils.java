package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce;

import io.lettuce.core.protocol.DecoratedCommand;
import io.lettuce.core.protocol.RedisCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisCommandUtils {

  public static RedisCommand<?, ?, ?> resolveCommand(RedisCommand<?, ?, ?> command) {
    if (command instanceof DecoratedCommand) {
      return resolveCommand(((DecoratedCommand<?, ?, ?>) command).getDelegate());
    }
    return command;
  }

}
