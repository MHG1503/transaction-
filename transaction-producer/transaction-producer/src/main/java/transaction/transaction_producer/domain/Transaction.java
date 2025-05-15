package transaction.transaction_producer.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record Transaction(UUID id,
                          @NotNull UUID userId,
                          @NotNull LocalDateTime timeStamp,
                          @NotNull Double amount) {
}
