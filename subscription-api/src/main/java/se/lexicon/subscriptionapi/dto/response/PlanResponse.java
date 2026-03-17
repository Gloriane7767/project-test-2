package se.lexicon.subscriptionapi.dto.response;

import se.lexicon.subscriptionapi.domain.constant.ServiceType;

import java.math.BigDecimal;

public record PlanResponse( Long id,
                    String name,
                    BigDecimal price,
                    ServiceType serviceType,
                    Integer dataLimitGb,
                    boolean active,
                    Long operatorId,
                    String operatorName
)
{}
