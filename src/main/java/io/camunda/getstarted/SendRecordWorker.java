package io.camunda.getstarted;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

  @ZeebeWorker(type = "send-record", autoComplete = true)
  public Map<String, Object> sendRecord(final JobClient client, final ActivatedJob job) {
    String correlationIdInKafkaRecord = UUID.randomUUID().toString();
    
    sendRecordToKafka(correlationIdInKafkaRecord);

    return Collections.singletonMap("correlationIdInKafkaRecord", correlationIdInKafkaRecord);
  }

  public void sendRecordToKafka(String correlationId) {
    kafka.send(kafkaTopic.name(), "{\"correlationId\": \""+correlationId+"\"}").addCallback(
      result -> {
        if (result != null) {
          LOG.info("Produced record: " + result.getRecordMetadata());
        }
      },
      exception -> LOG.error("Failed to produce to kafka", exception));    
  }

}
