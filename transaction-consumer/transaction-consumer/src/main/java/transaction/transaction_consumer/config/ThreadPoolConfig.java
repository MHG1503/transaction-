package transaction.transaction_consumer.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import transaction.transaction_consumer.entity.ThreadConfig;
import transaction.transaction_consumer.repository.ThreadConfigRepository;
import transaction.transaction_consumer.service.DynamicThreadPoolService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
public class ThreadPoolConfig {
    private final ThreadConfigRepository threadConfigRepository;
    private final DynamicThreadPoolService dynamicThreadPoolService;

    @Value("${app.thread.default-count:4}")
    private int defaultThreadCount;

    @PostConstruct
    public void init() {
        // Initialize thread pool with default size or from database
        int threadCount = threadConfigRepository.findById(1)
                .map(ThreadConfig::getThreadCount)
                .orElse(defaultThreadCount);
        dynamicThreadPoolService.updateThreadPool(threadCount);
        log.info("Initialized thread pool with {} threads", threadCount);
    }

//    @Bean
//    public ExecutorService transactionProcessorExecutor() {
//        return Executors.newFixedThreadPool(defaultThreadCount);
//    }

    @Scheduled(fixedDelay = 10000)
    public void checkForThreadPoolConfigChanges() {
        threadConfigRepository.findById(1).ifPresent(config -> {
            int currentThreadCount = dynamicThreadPoolService.getCurrentThreadCount().get();
            int configThreadCount = config.getThreadCount();

            if (currentThreadCount != configThreadCount) {
                log.info("Thread count changed in database from {} to {}, updating pool",
                        currentThreadCount, configThreadCount);
                dynamicThreadPoolService.updateThreadPool(configThreadCount);
            }
        });
    }
}
