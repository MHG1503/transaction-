package transaction.transaction_producer.domain;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionRequest(@NotNull UUID userId,
                                 @NotNull Double amount) {
}
