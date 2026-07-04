package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.DefaultEndpoint;
import io.lettuce.core.protocol.RedisCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用于在 {@link DefaultEndpoint#write(RedisCommand)} 线程和
 * <ol>
 *   <li>{@link RedisCommand#complete()}</li>
 *   <li>{@link RedisCommand#cancel()}</li>
 *   <li>{@link RedisCommand#completeExceptionally(Throwable)}</li>
 * </ol>
 * 线程之间跨线程传递上下文。
 */
@Setter
@Getter
@ToString
public class LettuceRedisCommandContext {

  /**
   * Redis 地址，格式 host:port
   */
  private String peer;

  /**
   * Redis 命令名，如 GET、SET
   */
  private String commandName;

  /**
   * {@link CommandArgs#toCommandString()}
   */
  private String commandArgString;

  /**
   * 执行开始时间（毫秒）
   */
  private long startTime;

  /**
   * 入口调用栈快照（在 write() 线程中捕获）
   */
  private EntryPointSnapshot entryPointSnapshot;

}
