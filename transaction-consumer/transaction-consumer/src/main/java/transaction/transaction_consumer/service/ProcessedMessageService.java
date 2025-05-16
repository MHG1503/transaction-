package transaction.transaction_consumer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import transaction.transaction_consumer.entity.ProcessedMessage;
import transaction.transaction_consumer.entity.ProcessedMessageId;
import transaction.transaction_consumer.repository.ProcessedMessageRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessedMessageService {
    private final ProcessedMessageRepository processedMessageRepository;

    public boolean isProcessed(String topic, int partitionId, long offsetValue){
        return processedMessageRepository.existsById(
                ProcessedMessageId
                        .builder()
                        .topic(topic)
                        .partitionId(partitionId)
                        .offsetValue(offsetValue)
                        .build());
    }

    public void save(String topic, int partitionId, long offsetValue){
         processedMessageRepository.save(new ProcessedMessage(ProcessedMessageId
                .builder()
                .topic(topic)
                .partitionId(partitionId)
                .offsetValue(offsetValue)
                .build()
         ));
         log.info("Save processed message");
    }
}
