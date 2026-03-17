package se.lexicon.subscriptionapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.subscriptionapi.domain.constant.ServiceType;
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;
import se.lexicon.subscriptionapi.exception.ResourceNotFoundException;
import se.lexicon.subscriptionapi.mapper.PlanMapper;
import se.lexicon.subscriptionapi.repository.OperatorRepository;
import se.lexicon.subscriptionapi.repository.PlanRepository;
import se.lexicon.subscriptionapi.service.PlanService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final OperatorRepository operatorRepository;
    private final PlanMapper planMapper;

    @Override
    @Transactional
    public PlanResponse create(PlanRequest request) {
        Operator operator = operatorRepository.findById(request.operatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found: " + request.operatorId()));
        Plan plan = planMapper.toEntity(request);
        plan.setOperator(operator);
        return planMapper.toResponse(planRepository.save(plan));
    }

    @Override
    @Transactional
    public PlanResponse update(Long id, PlanRequest request) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + id));
        Operator operator = operatorRepository.findById(request.operatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found: " + request.operatorId()));
        planMapper.updateEntity(request, plan);
        plan.setOperator(operator);
        return planMapper.toResponse(planRepository.save(plan));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!planRepository.existsById(id))
            throw new ResourceNotFoundException("Plan not found: " + id);
        planRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> findAll() {
        return planRepository.findAll().stream().map(planMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> findAllActive() {
        return planRepository.findByActiveTrue().stream().map(planMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> findActiveByServiceType(ServiceType serviceType) {
        return planRepository.findByActiveTrueAndServiceType(serviceType).stream()
                .map(planMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> findByOperator(Long operatorId) {
        return planRepository.findByOperatorId(operatorId).stream()
                .map(planMapper::toResponse).toList();
    }
}
