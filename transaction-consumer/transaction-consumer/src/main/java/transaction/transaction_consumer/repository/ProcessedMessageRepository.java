package transaction.transaction_consumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transaction.transaction_consumer.entity.ProcessedMessage;
import transaction.transaction_consumer.entity.ProcessedMessageId;

@Repository
public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, ProcessedMessageId> {
    boolean existsById(ProcessedMessageId id);
}
