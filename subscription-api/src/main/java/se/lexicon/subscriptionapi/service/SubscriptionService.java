package se.lexicon.subscriptionapi.service;

import se.lexicon.subscriptionapi.dto.request.SubscriptionRequest;
import se.lexicon.subscriptionapi.dto.response.SubscriptionResponse;

import java.util.List;
public interface SubscriptionService {
    SubscriptionResponse subscribe(Long customerId, SubscriptionRequest request);
    List<SubscriptionResponse> findMySubscriptions(Long customerId);
    SubscriptionResponse changePlan(Long subscriptionId, Long customerId, SubscriptionRequest request);
    SubscriptionResponse cancel(Long subscriptionId, Long customerId);
}
