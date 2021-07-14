package io.camunda.getstarted;

import java.util.Collections;
import java.util.UUID;

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
public class SendRecordWorker {

  private final static Logger LOG = LoggerFactory.getLogger(SendRecordWorker.class);

  @Autowired
  private NewTopic kafkaTopic;

  @Autowired
  private KafkaTemplate<String, String> kafka;

  @ZeebeWorker(type = "send-record")
  public void sendRecord(final JobClient client, final ActivatedJob job) {
    String correlationIdInKafkaRecord = UUID.randomUUID().toString();
    
    sendRecordToKafka(correlationIdInKafkaRecord);

    client.newCompleteCommand(job.getKey())
      .variables(Collections.singletonMap("correlationIdInKafkaRecord", correlationIdInKafkaRecord))
      .send()
      .exceptionally(t -> {throw new RuntimeException("Could not complete job in workflow engine: " + t.getMessage(), t);});
  }

  public void sendRecordToKafka(String correlationId) {
    kafka.send(kafkaTopic.name(), "{\"correlationId\": \""+correlationId+"\"}").addCallback(
      result -> {
        final RecordMetadata m;
        if (result != null) {
          m = result.getRecordMetadata();
          LOG.info("Produced record to topic {} partition {} @ offset {}", m.topic(), m.partition(), m.offset());
        }
      },
      exception -> LOG.error("Failed to produce to kafka", exception));    
  }

}
