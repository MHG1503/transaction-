package transaction.transaction_consumer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import transaction.transaction_consumer.entity.Transaction;
import transaction.transaction_consumer.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository repository;

    public void processTransaction(ConsumerRecord<UUID, Transaction> consumerRecord){
        var transaction = consumerRecord.value();
        transaction.setTimeStamp(LocalDateTime.now());
        // Luu vao db
        repository.save(transaction);
        log.info("Successfully Persisted the transaction: {} ",transaction);

    }

}
