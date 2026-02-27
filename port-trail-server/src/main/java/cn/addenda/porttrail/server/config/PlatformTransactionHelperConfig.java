package cn.addenda.porttrail.server.config;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author addenda
 * @since 2023/8/18 15:22
 */
@Configuration
public class PlatformTransactionHelperConfig {

  @Bean
  public PlatformTransactionHelper platformTransactionHelper(PlatformTransactionManager platformTransactionManager) {
    return new PlatformTransactionHelper(platformTransactionManager);
  }

}
