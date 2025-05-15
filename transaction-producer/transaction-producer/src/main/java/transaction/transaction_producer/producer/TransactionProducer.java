package transaction.transaction_producer.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import transaction.transaction_producer.domain.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionProducer {

    @Value("${spring.kafka.topic}")
    public String topic;

    private final KafkaTemplate<UUID, Transaction> kafkaTemplate;

    public CompletableFuture<SendResult<UUID, Transaction>> sendTransaction(Transaction transaction){

        var producerRecord = buildProducerRecord(transaction.id(),transaction);

        var completableFuture = kafkaTemplate.send(producerRecord);

        completableFuture.whenComplete((sendResult, throwable) -> {
            if(throwable != null){
                handleFailure(transaction.id(), transaction.toString(), throwable);
            }
        });
        return completableFuture;
    }

    public void send100TransactionsTest(){
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            Transaction tx = generateRandomTransaction();
            CompletableFuture<?> future = sendTransaction(tx);
            futures.add(future);
            log.info("Sending Message: {}", tx);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        System.out.println("Gửi xong 1000 transaction");
    }

    private Transaction generateRandomTransaction() {
        return Transaction.builder()
                .id(null)
                .userId(UUID.randomUUID())
                .amount(Math.random() * 1000)
                .timeStamp(LocalDateTime.now())
                .build();
    }

    private ProducerRecord<UUID, Transaction> buildProducerRecord(UUID key, Transaction transaction) {
        return new ProducerRecord<>(topic,key,transaction);
    }

    private void handleSuccess(UUID key, String value, SendResult<Integer, String> sendResult) {
        log.info("Message Sent Successfully for the key: {}, the value : {}, partition is {}",key,value,sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(UUID key, String value, Throwable ex){
        log.error("Error sending the message and the exception is {}",ex.getMessage(),ex);
    }
}
