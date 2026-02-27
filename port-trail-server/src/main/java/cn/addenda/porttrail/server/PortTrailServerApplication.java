package cn.addenda.porttrail.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement(order = Ordered.LOWEST_PRECEDENCE - 70)
@MapperScan("cn.addenda.porttrail.server.mapper")
@SpringBootApplication
public class PortTrailServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(PortTrailServerApplication.class, args);
  }

}
