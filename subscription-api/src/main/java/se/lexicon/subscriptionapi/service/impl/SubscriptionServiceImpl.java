package se.lexicon.subscriptionapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;
import se.lexicon.subscriptionapi.domain.entity.Customer;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.domain.entity.Subscription;
import se.lexicon.subscriptionapi.dto.request.SubscriptionRequest;
import se.lexicon.subscriptionapi.dto.response.SubscriptionResponse;
import se.lexicon.subscriptionapi.exception.BusinessRuleException;
import se.lexicon.subscriptionapi.exception.ResourceNotFoundException;
import se.lexicon.subscriptionapi.mapper.SubscriptionMapper;
import se.lexicon.subscriptionapi.repository.CustomerRepository;
import se.lexicon.subscriptionapi.repository.PlanRepository;
import se.lexicon.subscriptionapi.repository.SubscriptionRepository;
import se.lexicon.subscriptionapi.service.SubscriptionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final CustomerRepository customerRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    @Transactional
    public SubscriptionResponse subscribe(Long customerId, SubscriptionRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        Plan plan = getActivePlan(request.planId());

        // Business rule: one active subscription per service type
        subscriptionRepository.findByCustomerIdAndPlan_ServiceTypeAndStatus(
                        customerId, plan.getServiceType(), SubscriptionStatus.ACTIVE)
                .ifPresent(s -> { throw new BusinessRuleException(
                        "Already have an active " + plan.getServiceType() + " subscription"); });

        Subscription subscription = new Subscription();
        subscription.setCustomer(customer);
        subscription.setPlan(plan);
        return subscriptionMapper.toResponse(subscriptionRepository.save(subscription));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> findMySubscriptions(Long customerId) {
        return subscriptionRepository.findByCustomerId(customerId).stream()
                .map(subscriptionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubscriptionResponse changePlan(Long subscriptionId, Long customerId, SubscriptionRequest request) {
        Subscription subscription = getOwnedActiveSubscription(subscriptionId, customerId);
        Plan newPlan = getActivePlan(request.planId());
        Plan currentPlan = subscription.getPlan();

        // Business rule: same operator and same service type
        if (!currentPlan.getOperator().getId().equals(newPlan.getOperator().getId()))
            throw new BusinessRuleException("Plan change must be within the same operator");
        if (!currentPlan.getServiceType().equals(newPlan.getServiceType()))
            throw new BusinessRuleException("Plan change must be within the same service type");

        subscription.setPlan(newPlan);
        return subscriptionMapper.toResponse(subscriptionRepository.save(subscription));
    }

    @Override
    @Transactional
    public SubscriptionResponse cancel(Long subscriptionId, Long customerId) {
        Subscription subscription = getOwnedActiveSubscription(subscriptionId, customerId);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setCancelledAt(LocalDateTime.now());
        return subscriptionMapper.toResponse(subscriptionRepository.save(subscription));
    }

    private Plan getActivePlan(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + planId));
        if (!plan.isActive()) throw new BusinessRuleException("Plan is not active: " + planId);
        return plan;
    }

    private Subscription getOwnedActiveSubscription(Long subscriptionId, Long customerId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + subscriptionId));
        if (!subscription.getCustomer().getId().equals(customerId))
            throw new BusinessRuleException("Subscription does not belong to this customer");
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE)
            throw new BusinessRuleException("Subscription is not active");
        return subscription;
    }
}
