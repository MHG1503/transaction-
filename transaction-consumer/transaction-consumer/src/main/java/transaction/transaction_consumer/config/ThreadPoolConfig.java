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

    // Khi start app -> chay method de lay so thread se dung trong executorService (thread pool)
    @PostConstruct
    public void init() {
        // Neu trong DB da co record ThreadConfig -> lay ra || neu khong co thi mac dinh la 4 thread trong pool
        int threadCount = threadConfigRepository.findById(1)
                .map(ThreadConfig::getThreadCount)
                .orElse(defaultThreadCount);

        // Tao threadPool vs so thread da lay
        dynamicThreadPoolService.updateThreadPool(threadCount);
        log.info("Initialized thread pool with {} threads", threadCount);
    }

    // chay moi 10s de cap nhat so luong thread se dung de xu ly message
    @Scheduled(fixedDelay = 10000)
    public void checkForThreadPoolConfigChanges() {
        // Tim kiem record threadConfig de lay ra dong so thread dang dung hien tai trong Database
        threadConfigRepository.findById(1).ifPresent(config -> {
            int currentThreadCount = dynamicThreadPoolService.getCurrentThreadCount().get();
            int configThreadCount = config.getThreadCount();

            // Kiem tra xem so thread hien tai dang dung va so thread lay tu database co khac nhau ko
            // != cap nhat lai thread pool vs so luong thread tuong ung trong DB
            // == khong lam gi ca
            if (currentThreadCount != configThreadCount) {
                log.info("Thread count changed in database from {} to {}, updating pool",
                        currentThreadCount, configThreadCount);
                dynamicThreadPoolService.updateThreadPool(configThreadCount);
            }
        });
    }
}
