package io.berndruecker.example.camunda.kafka;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@Component
public class FromCamundaToKafka {

  private final static Logger LOG = LoggerFactory.getLogger(FromCamundaToKafka.class);

  @Autowired
  private NewTopic kafkaTopic;

  @Autowired
  private KafkaTemplate<String, String> kafka;

  @Autowired
  private ObjectMapper objectMapper;

  @ZeebeWorker(type = "send-record", autoComplete = true)
  public Map<String, Object> sendRecord(final ActivatedJob job) throws JsonProcessingException {
    String correlationIdInKafkaRecord = UUID.randomUUID().toString();

    Map<String, Object> variables = job.getVariablesAsMap();
    variables.put("correlationId", correlationIdInKafkaRecord);

    sendRecordToKafka(variables);

    return Collections.singletonMap("correlationIdInKafkaRecord", correlationIdInKafkaRecord);
  }

  public void sendRecordToKafka(Map<String, Object> variables) throws JsonProcessingException {
    String dataAsJson = objectMapper.writeValueAsString(variables);

    kafka.send(kafkaTopic.name(), dataAsJson).addCallback(
      result -> {
        if (result != null) {
          LOG.info("Produced record: " + result.getRecordMetadata());
        }
      },
      exception -> LOG.error("Failed to produce to kafka", exception));    
  }

}
