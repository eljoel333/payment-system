package com.paymentystem.transaction.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TRANSFER_INITIATED   = "transfer.initiated";
    public static final String ACCOUNT_DEBITED      = "account.debited";
    public static final String TRANSFER_COMPLETED   = "transfer.completed";
    public static final String TRANSFER_FAILED      = "transfer.failed";
    public static final String TRANSFER_COMPENSATED = "transfer.compensated";

    @Bean
    public NewTopic transferInitiatedTopic() {
        return TopicBuilder.name(TRANSFER_INITIATED).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic accountDebitedTopic() {
        return TopicBuilder.name(ACCOUNT_DEBITED).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic transferCompletedTopic() {
        return TopicBuilder.name(TRANSFER_COMPLETED).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic transferFailedTopic() {
        return TopicBuilder.name(TRANSFER_FAILED).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic transferCompensatedTopic() {
        return TopicBuilder.name(TRANSFER_COMPENSATED).partitions(1).replicas(1).build();
    }
}