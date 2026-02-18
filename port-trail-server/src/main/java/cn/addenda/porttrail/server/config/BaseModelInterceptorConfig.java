package cn.addenda.porttrail.server.config;

import cn.addenda.mybatisbasemodel.core.BaseModelELEvaluator;
import cn.addenda.mybatisbasemodel.core.BaseModelInterceptor;
import cn.addenda.mybatisbasemodel.spring.SpringBaseModelELEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseModelInterceptorConfig {

  @Bean
  public BaseModelELEvaluator baseModelELEvaluator() {
    return new SpringBaseModelELEvaluator();
  }

  @Bean
  public BaseModelInterceptor baseModelInterceptor(BaseModelELEvaluator baseModelELEvaluator) {
    BaseModelInterceptor baseModelInterceptor = new BaseModelInterceptor();
    baseModelInterceptor.setBaseModelELEvaluator(baseModelELEvaluator);
    return baseModelInterceptor;
  }

}
