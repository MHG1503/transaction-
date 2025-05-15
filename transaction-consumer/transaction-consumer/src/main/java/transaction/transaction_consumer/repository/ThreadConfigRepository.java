package transaction.transaction_consumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import transaction.transaction_consumer.entity.ThreadConfig;

import java.util.Optional;

@Repository
public interface ThreadConfigRepository extends JpaRepository<ThreadConfig, Integer> {

    @Query(nativeQuery = true,value = "SELECT * FROM thread_config tc WHERE tc.id = :id")
    Optional<ThreadConfig> findByThreadConfigId(@Param("id") Integer id);
}
