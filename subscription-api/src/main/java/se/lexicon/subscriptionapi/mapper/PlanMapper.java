package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import se.lexicon.subscriptionapi.domain.entity.Subscription;
import se.lexicon.subscriptionapi.dto.response.SubscriptionResponse;

@Mapper(componentModel = "spring")
public interface PlanMapper {
    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "plan.name", target = "planName")
    @Mapping(source = "plan.serviceType", target = "serviceType")
    @Mapping(source = "plan.operator.name", target = "operatorName")
    SubscriptionResponse toResponse(Subscription subscription);
}

