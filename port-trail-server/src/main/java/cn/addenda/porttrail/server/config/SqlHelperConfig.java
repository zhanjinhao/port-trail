package cn.addenda.porttrail.server.config;

import cn.addenda.porttrail.server.helper.SqlHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class SqlHelperConfig {

  /**
   * JSqlParser 是 CPU 密集操作，线程数不必超过容器 CPU limit。
   * <p>
   * 拒绝策略保持 {@code CallerRunsPolicy}：池打满时由提交任务的业务线程亲自解析，
   * 任务不丢，语义是「阻塞等待」而不是「放弃解析」。
   */
  @Bean
  public SqlHelper sqlHelper(
          @Value("${port-trail.analyze.sql-parse.threads:8}") int threads,
          @Value("${port-trail.analyze.sql-parse.queue-capacity:8}") int queueCapacity) {
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            threads,
            threads,
            10000,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(queueCapacity),
            new SqlHelperThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    return new SqlHelper(threadPoolExecutor);
  }

  private static class SqlHelperThreadFactory implements ThreadFactory {

    private final AtomicLong nameCounter = new AtomicLong(0L);

    @Override
    public Thread newThread(Runnable r) {
      Thread thread = new Thread(r);
      thread.setName("sqlHelper-thread-" + nameCounter.getAndIncrement());
      return thread;
    }
  }

}
