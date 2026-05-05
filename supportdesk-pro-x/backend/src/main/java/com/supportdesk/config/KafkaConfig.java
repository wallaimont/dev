package com.supportdesk.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true",
                ProducerConfig.ACKS_CONFIG, "all",
                ProducerConfig.RETRIES_CONFIG, "3",
                ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1"
        ));
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> pf) {
        return new KafkaTemplate<>(pf);
    }

    // Topics
    @Bean public NewTopic ticketEventsTopic()       { return TopicBuilder.name("ticket-events").partitions(3).replicas(1).build(); }
    @Bean public NewTopic commentEventsTopic()      { return TopicBuilder.name("comment-events").partitions(3).replicas(1).build(); }
    @Bean public NewTopic notificationRequestsTopic(){ return TopicBuilder.name("notification-requests").partitions(3).replicas(1).build(); }
    @Bean public NewTopic slaAlertsTopic()          { return TopicBuilder.name("sla-alerts").partitions(3).replicas(1).build(); }
    @Bean public NewTopic auditEventsTopic()        { return TopicBuilder.name("audit-events").partitions(3).replicas(1).build(); }
}
