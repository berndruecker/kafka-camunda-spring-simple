package io.berndruecker.example.camunda.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableZeebeClient
@ZeebeDeployment(resources = "classpath:*.bpmn")
public class CamundaKafkaExampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(CamundaKafkaExampleApplication.class, args);
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
