package se.lexicon.subscriptionapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lexicon.subscriptionapi.domain.constant.ServiceType;
import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;
import se.lexicon.subscriptionapi.domain.entity.Subscription;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository <Subscription, Long> {
    List<Subscription> findByCustomerId(Long customerId);
    Optional<Subscription> findByCustomerIdAndPlan_ServiceTypeAndStatus(
            Long customerId, ServiceType serviceType, SubscriptionStatus status);
}
