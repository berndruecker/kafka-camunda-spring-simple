package io.camunda.getstarted;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@Component
public class Worker {

  private final static Logger LOG = LoggerFactory.getLogger(Worker.class);

  @Autowired
  private NewTopic kafkaTopic;

  @Autowired
  private KafkaTemplate<String, String> kafka;

  @ZeebeWorker(type = "payment-requested")
  public void paymentRequested(final JobClient client, final ActivatedJob job) {
    LOG.info("Payment was requested: " + job);

    sendRecord();

    client.newCompleteCommand(job.getKey())
      .send()
      .exceptionally(t -> {throw new RuntimeException("Could not complete job in workflow engine: " + t.getMessage(), t);});
  }

  public void sendRecord() {
    kafka.send(kafkaTopic.name(), "{\"lala\", \"42\"}").addCallback(
      result -> {
        final RecordMetadata m;
        if (result != null) {
          m = result.getRecordMetadata();
          LOG.info("Produced record to topic {} partition {} @ offset {}", m.topic(), m.partition(), m.offset());
        }
      },
      exception -> LOG.error("Failed to produce to kafka", exception));    
  }

  @Bean
  public NewTopic kafkaTopic() {
       return new NewTopic("example", 3, (short) 3);
  }

  @KafkaListener(topics = "example")
  public void processMessage(String content) {
      LOG.info("Received record: " + content);
  }

}
