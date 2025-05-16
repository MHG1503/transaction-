package transaction.transaction_producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class SpringConfiguration {

    @Value("${spring.kafka.topic}")
    public String topic;

    // tao topic transactions_log
    @Bean
    public NewTopic transactionsLog(){
        return TopicBuilder
                .name(topic)
                .replicas(3)
                .partitions(3)
                .build();
    }
}
