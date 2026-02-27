package cn.addenda.porttrail.server.config;

import cn.addenda.porttrail.server.helper.SqlHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class SqlHelperConfig {

  @Bean
  public SqlHelper sqlHelper() {
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            10,
            10,
            10000,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(10),
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
