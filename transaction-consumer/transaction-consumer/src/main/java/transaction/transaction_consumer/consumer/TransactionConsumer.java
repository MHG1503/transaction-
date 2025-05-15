package transaction.transaction_consumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import transaction.transaction_consumer.entity.Transaction;
import transaction.transaction_consumer.service.DynamicThreadPoolService;
import transaction.transaction_consumer.service.TransactionService;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionConsumer {
    private final TransactionService transactionService;
    private final DynamicThreadPoolService executor; // hoặc cấu hình động

    @KafkaListener(topics = {"transaction_logs"}, groupId = "transactions-listener-group", concurrency = "3")
    public void listen(ConsumerRecord<UUID, Transaction> consumerRecord) {
        log.info("ConsumerRecord : {}",consumerRecord);
        System.out.printf("Kafka Consumer Thread: %s", Thread.currentThread().getName());
        executor.submit(() -> process(consumerRecord));
    }

    private void process(ConsumerRecord<UUID, Transaction> consumerRecord) {
        System.out.println("Transaction Thread: " + Thread.currentThread().getName());
        transactionService.processTransaction(consumerRecord);
    }
}
