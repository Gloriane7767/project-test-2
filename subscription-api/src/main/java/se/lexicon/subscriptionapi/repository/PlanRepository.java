package se.lexicon.subscriptionapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lexicon.subscriptionapi.domain.constant.ServiceType;
import se.lexicon.subscriptionapi.domain.entity.Plan;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByActiveTrue();
    List<Plan> findByActiveTrueAndServiceType(ServiceType serviceType);
    List<Plan> findByOperatorId(Long operatorId);
}
