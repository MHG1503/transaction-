package transaction.transaction_consumer.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import transaction.transaction_consumer.entity.Transaction;
import transaction.transaction_consumer.service.DynamicThreadPoolService;
import transaction.transaction_consumer.service.ProcessedMessageService;
import transaction.transaction_consumer.service.TransactionService;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionConsumer implements AcknowledgingMessageListener<UUID,Transaction> {
    private final TransactionService transactionService;
    private final ProcessedMessageService processedMessageService;
    private final DynamicThreadPoolService executor;

    @Override
    @KafkaListener(topics = {"transaction_logs"}, groupId = "transactions-listener-group", concurrency = "3")
    public void onMessage(ConsumerRecord<UUID, Transaction> consumerRecord, Acknowledgment acknowledgment) {
        String topic = consumerRecord.topic();
        int partition = consumerRecord.partition();
        long offset = consumerRecord.offset();

        // Kiem tra trung lap
        if (processedMessageService.isProcessed(topic,partition,offset)) {
            log.warn("Duplicate message detected");
            acknowledgment.acknowledge(); // vẫn commit offset để không bị xử lại nữa
            return;
        }
        System.out.printf("Kafka Consumer Thread: %s", Thread.currentThread().getName());

        // Submit task can chay vao threadPool
        executor.submit(() -> {
//            try {
//                Thread.sleep(5000);
//                System.out.println("Transaction Thread: " + Thread.currentThread().getName());
//                process(consumerRecord);
//                processedMessageService.save(topic,partition,offset);
//                Thread.sleep(3000);
//                log.info("Commit offset {} in partition {} ",consumerRecord.offset(), consumerRecord.partition());
//                acknowledgment.acknowledge();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }

            System.out.println("Transaction Thread: " + Thread.currentThread().getName());
            process(consumerRecord);
            processedMessageService.save(topic,partition,offset);
            log.info("Commit offset {} in partition {} ",consumerRecord.offset(), consumerRecord.partition());
            acknowledgment.acknowledge();
        });

    }

    private void process(ConsumerRecord<UUID, Transaction> consumerRecord) {
        transactionService.processTransaction(consumerRecord);
    }

}
