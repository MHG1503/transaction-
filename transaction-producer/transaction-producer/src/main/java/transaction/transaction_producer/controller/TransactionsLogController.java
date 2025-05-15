package transaction.transaction_producer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import transaction.transaction_producer.domain.Transaction;
import transaction.transaction_producer.domain.TransactionRequest;
import transaction.transaction_producer.producer.TransactionProducer;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("api/transactions")
@RequiredArgsConstructor
public class TransactionsLogController {
    private final TransactionProducer producer;

    @PostMapping
    public ResponseEntity<?> addTransactions(@RequestBody TransactionRequest request){
        var transaction = new Transaction(
                null,
                request.userId(),
                LocalDateTime.now(),
                request.amount()
        );
        log.info("transaction : {}",transaction);

        producer.sendTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PostMapping("/add-1000")
    public ResponseEntity<?> addTransactionsTest(){
        producer.send100TransactionsTest();
        return ResponseEntity.status(HttpStatus.CREATED).body("Send");
    }
}
