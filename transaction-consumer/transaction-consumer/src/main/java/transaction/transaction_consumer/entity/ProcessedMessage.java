package transaction.transaction_consumer.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "processed_messages")
public class ProcessedMessage {

    @EmbeddedId
    private ProcessedMessageId processedMessageId;
}
