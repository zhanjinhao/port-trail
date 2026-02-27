package cn.addenda.porttrail.server.config;

import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchDmlHelperConfig {

  @Bean
  public BatchDmlHelper batchDmlHelper(SqlSessionFactory sqlSessionFactory) {
    return new BatchDmlHelper(sqlSessionFactory);
  }

}
