package io.camunda.getstarted;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String TOPIC_NAME = "example";

    @Bean
    public NewTopic kafkaTopic() {
        return new NewTopic(TOPIC_NAME, 3, (short) 3);
    }  

}