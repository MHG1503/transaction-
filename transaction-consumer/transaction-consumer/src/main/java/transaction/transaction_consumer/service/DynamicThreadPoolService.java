package transaction.transaction_consumer.service;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
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

    // Cap nhat lai so luong thread trong ThreadPool de giong vs gia tri trong database
    public synchronized void updateThreadPool(int threadCount) {
        if (threadCount <= 0) {
            log.warn("Attempted to set thread count to invalid value: {}, using 1 instead", threadCount);
            threadCount = 1;
        }

        // Shut down pool truoc do dung
        if (executorService != null && !executorService.isShutdown()) {
            shutdownExecutorService();
        }

        // Tao thread pool
        executorService = Executors.newFixedThreadPool(threadCount);
        currentThreadCount.set(threadCount);
        log.info("Thread pool updated to {} threads", threadCount);
    }

    // ham de executor task
    public void submit(Runnable task) {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.submit(task);
        } else {
            log.warn("Cannot submit task - executor service is not available");
            task.run();
        }
    }

    // Huy thread pool neu update sang 1 so luong thread khac cho pool hoac truoc khi dung app
    @PreDestroy
    public void shutdownExecutorService() {
        if (executorService != null && !executorService.isShutdown()) {
            log.info("Shutting down executor service");
            // shutdown ( khong nhan theo task nua, chua dung han )
            executorService.shutdown();
            try {
                // doi 5s de hoan thanh het cac task con trong pool roi moi shutdown han
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
