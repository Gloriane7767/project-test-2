package se.lexicon.subscriptionapi.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import se.lexicon.subscriptionapi.domain.constant.ServiceType;

import java.math.BigDecimal;

public record PlanRequest(
        @NotBlank String name,
        @NotNull @Positive BigDecimal price,
        @NotNull ServiceType serviceType,
        Integer dataLimitGb,
        boolean active,
        @NotNull Long operatorId
) {}
