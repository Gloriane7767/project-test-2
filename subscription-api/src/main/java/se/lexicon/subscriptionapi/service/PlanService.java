package se.lexicon.subscriptionapi.service;

import se.lexicon.subscriptionapi.domain.constant.ServiceType;
import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;

import java.util.List;

public interface PlanService {
    PlanResponse create(PlanRequest request);
    PlanResponse update(Long id, PlanRequest request);
    void delete(Long id);
    List<PlanResponse> findAll();
    List<PlanResponse> findAllActive();
    List<PlanResponse> findActiveByServiceType(ServiceType serviceType);
    List<PlanResponse> findByOperator(Long operatorId);
}
