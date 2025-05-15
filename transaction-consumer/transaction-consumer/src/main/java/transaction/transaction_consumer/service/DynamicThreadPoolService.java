package transaction.transaction_consumer.service;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class DynamicThreadPoolService {

    private ExecutorService executorService;

    @Getter
    private final AtomicInteger currentThreadCount = new AtomicInteger(0);

    public synchronized void updateThreadPool(int threadCount) {
        if (threadCount <= 0) {
            log.warn("Attempted to set thread count to invalid value: {}, using 1 instead", threadCount);
            threadCount = 1;
        }

        if (executorService != null && !executorService.isShutdown()) {
            shutdownExecutorService();
        }

        executorService = Executors.newFixedThreadPool(threadCount);
        currentThreadCount.set(threadCount);
        log.info("Thread pool updated to {} threads", threadCount);
    }

    public void submit(Runnable task) {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.submit(task);
        } else {
            log.warn("Cannot submit task - executor service is not available");
            task.run();
        }
    }

    @PreDestroy
    public void shutdownExecutorService() {
        if (executorService != null && !executorService.isShutdown()) {
            log.info("Shutting down executor service");
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
