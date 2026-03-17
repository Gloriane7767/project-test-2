package se.lexicon.subscriptionapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import se.lexicon.subscriptionapi.dto.request.SubscriptionRequest;
import se.lexicon.subscriptionapi.dto.response.SubscriptionResponse;
import se.lexicon.subscriptionapi.exception.ResourceNotFoundException;
import se.lexicon.subscriptionapi.repository.CustomerRepository;
import se.lexicon.subscriptionapi.service.SubscriptionService;

import java.util.List;

@Tag(name = "Subscriptions", description = "Subscription management endpoints.")
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final CustomerRepository customerRepository;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Subscribe to a plan", description = "Roles: USER")
    public ResponseEntity<SubscriptionResponse> subscribe(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SubscriptionRequest request) {
        Long customerId = resolveCustomerId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.subscribe(customerId, request));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "View my subscriptions", description = "Roles: USER")
    public ResponseEntity<List<SubscriptionResponse>> mySubscriptions(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subscriptionService.findMySubscriptions(resolveCustomerId(userDetails)));
    }

    @PutMapping("/{id}/change-plan")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Change subscription plan", description = "Roles: USER")
    public ResponseEntity<SubscriptionResponse> changePlan(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SubscriptionRequest request) {
        Long customerId = resolveCustomerId(userDetails);
        return ResponseEntity.ok(subscriptionService.changePlan(id, customerId, request));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Cancel subscription", description = "Roles: USER")
    public ResponseEntity<SubscriptionResponse> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subscriptionService.cancel(id, resolveCustomerId(userDetails)));
    }

    private Long resolveCustomerId(UserDetails userDetails) {
        return customerRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"))
                .getId();
    }
}

