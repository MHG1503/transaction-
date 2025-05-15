package transaction.transaction_consumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import transaction.transaction_consumer.entity.Transaction;
import transaction.transaction_consumer.service.DynamicThreadPoolService;
import transaction.transaction_consumer.service.TransactionService;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionConsumer implements AcknowledgingMessageListener<UUID,Transaction> {
    private final TransactionService transactionService;
    private final DynamicThreadPoolService executor;

    private final Set<String> processedMessageIds = ConcurrentHashMap.newKeySet();

//    @KafkaListener(topics = {"transaction_logs"}, groupId = "transactions-listener-group", concurrency = "3")
//    public void listen(ConsumerRecord<UUID, Transaction> consumerRecord) {
//        log.info("ConsumerRecord : {}",consumerRecord);
//        System.out.printf("Kafka Consumer Thread: %s", Thread.currentThread().getName());
//        executor.submit(() -> {
//            process(consumerRecord);
//        });
//    }

    @Override
    @KafkaListener(topics = {"transaction_logs"}, groupId = "transactions-listener-group", concurrency = "3")
    public void onMessage(ConsumerRecord<UUID, Transaction> consumerRecord, Acknowledgment acknowledgment) {
        String key = consumerRecord.topic() + "-" + consumerRecord.partition() + "-" + consumerRecord.offset();
        log.info("ConsumerRecord : {}",consumerRecord);
        if(processedMessageIds.contains(key)){
            log.warn("Duplicate consumer record!!!");
            return;
        }
        System.out.printf("Kafka Consumer Thread: %s", Thread.currentThread().getName());
        executor.submit(() -> {
            process(consumerRecord);
            processedMessageIds.add(key);
            acknowledgment.acknowledge();
        });

    }

    private void process(ConsumerRecord<UUID, Transaction> consumerRecord) {
        System.out.println("Transaction Thread: " + Thread.currentThread().getName());
        transactionService.processTransaction(consumerRecord);
    }

}
