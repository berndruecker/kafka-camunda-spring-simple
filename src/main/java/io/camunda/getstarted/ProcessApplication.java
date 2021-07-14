package io.camunda.getstarted;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import io.camunda.zeebe.spring.client.EnableZeebeClient;

@SpringBootApplication
@EnableZeebeClient
public class ProcessApplication {

  public static void main(String[] args) {
    ApplicationContext ctx = SpringApplication.run(ProcessApplication.class, args);
    Worker w = ctx.getBean(Worker.class);
    w.sendRecord();
  }

}
