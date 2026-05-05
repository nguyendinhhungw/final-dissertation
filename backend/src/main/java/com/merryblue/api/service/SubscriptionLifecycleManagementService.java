package com.merryblue.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Highly complex service managing the lifecycle of customer subscriptions.
 * Handles states: ACTIVE, PAST_DUE, CANCELED, PAUSED.
 * Responsible for calculating prorated upgrades/downgrades, handling dunning logic 
 * (failed payment retries), and generating complex billing events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionLifecycleManagementService {

    // Mock constants for plans
    private static final Map<String, BigDecimal> PLAN_PRICES = Map.of(
            "BASIC", new BigDecimal("29.99"),
            "PRO", new BigDecimal("99.99"),
            "ENTERPRISE", new BigDecimal("499.99")
    );

    /**
     * Handles the complex logic of changing a user's subscription plan mid-cycle.
     * Calculates prorated refunds for unused time on the old plan and 
     * prorated charges for the remaining time on the new plan.
     */
    @Transactional
    public Map<String, Object> calculateProratedPlanChange(UUID subscriptionId, String currentPlanId, String newPlanId, LocalDate cycleStart, LocalDate cycleEnd) {
        log.info("Calculating prorated plan change for subscription {}. {} -> {}", subscriptionId, currentPlanId, newPlanId);

        BigDecimal oldPlanPrice = PLAN_PRICES.get(currentPlanId);
        BigDecimal newPlanPrice = PLAN_PRICES.get(newPlanId);

        if (oldPlanPrice == null || newPlanPrice == null) {
            throw new IllegalArgumentException("Invalid plan IDs provided.");
        }

        LocalDate today = LocalDate.now();
        if (today.isBefore(cycleStart) || today.isAfter(cycleEnd)) {
            throw new IllegalStateException("Current date is outside the billing cycle.");
        }

        long totalDaysInCycle = ChronoUnit.DAYS.between(cycleStart, cycleEnd) + 1; // inclusive
        long daysUsed = ChronoUnit.DAYS.between(cycleStart, today);
        long daysRemaining = totalDaysInCycle - daysUsed;

        // Calculate unused value of the old plan (Credit)
        BigDecimal dailyRateOld = oldPlanPrice.divide(new BigDecimal(totalDaysInCycle), 4, RoundingMode.HALF_UP);
        BigDecimal unusedCredit = dailyRateOld.multiply(new BigDecimal(daysRemaining)).setScale(2, RoundingMode.HALF_UP);

        // Calculate cost of the new plan for the remaining days (Debit)
        BigDecimal dailyRateNew = newPlanPrice.divide(new BigDecimal(totalDaysInCycle), 4, RoundingMode.HALF_UP);
        BigDecimal remainingCostNew = dailyRateNew.multiply(new BigDecimal(daysRemaining)).setScale(2, RoundingMode.HALF_UP);

        // Net amount due immediately (can be negative if downgrading)
        BigDecimal netAmountDue = remainingCostNew.subtract(unusedCredit);

        Map<String, Object> billingDetails = new HashMap<>();
        billingDetails.put("totalDaysInCycle", totalDaysInCycle);
        billingDetails.put("daysRemaining", daysRemaining);
        billingDetails.put("oldPlanCredit", unusedCredit);
        billingDetails.put("newPlanCost", remainingCostNew);
        billingDetails.put("netAmountDue", netAmountDue);
        
        boolean isUpgrade = newPlanPrice.compareTo(oldPlanPrice) > 0;
        billingDetails.put("isUpgrade", isUpgrade);

        if (netAmountDue.compareTo(BigDecimal.ZERO) > 0) {
            billingDetails.put("actionRequired", "CHARGE_CUSTOMER");
        } else if (netAmountDue.compareTo(BigDecimal.ZERO) < 0) {
            billingDetails.put("actionRequired", "APPLY_ACCOUNT_BALANCE");
        } else {
            billingDetails.put("actionRequired", "NO_ACTION_EXCHANGE_EVEN");
        }

        return billingDetails;
    }

    /**
     * Executes the Dunning Process for subscriptions that failed to renew.
     * Escalates actions based on how many days the subscription is past due.
     */
    @Transactional
    public void processDunningLogic(UUID subscriptionId, LocalDate dueDate, int failedAttemptCount) {
        log.info("Processing Dunning Logic for Subscription {}. Failed Attempts: {}", subscriptionId, failedAttemptCount);
        
        LocalDate today = LocalDate.now();
        long daysPastDue = ChronoUnit.DAYS.between(dueDate, today);

        if (daysPastDue < 0) {
            log.warn("Subscription is not past due yet. Due date: {}", dueDate);
            return;
        }

        // Complex Escalation Matrix
        if (daysPastDue == 1) {
            // Day 1: Soft retry & friendly email
            log.info("Dunning Step 1: Initiating soft payment retry.");
            // mockPaymentRetry(subscriptionId);
            sendDunningEmail(subscriptionId, "friendly_reminder");
            
        } else if (daysPastDue == 3) {
            // Day 3: Hard retry & firmer email
            log.info("Dunning Step 2: Hard retry.");
            sendDunningEmail(subscriptionId, "firm_reminder");
            
        } else if (daysPastDue == 7) {
            // Day 7: Pause services, final warning
            log.info("Dunning Step 3: Pausing services.");
            pauseSubscriptionServices(subscriptionId);
            sendDunningEmail(subscriptionId, "service_paused_warning");
            
        } else if (daysPastDue >= 14) {
            // Day 14: Cancel subscription, send to collections/churn
            log.info("Dunning Step 4: Canceling subscription due to non-payment.");
            cancelSubscription(subscriptionId, "NON_PAYMENT");
            sendDunningEmail(subscriptionId, "cancellation_notice");
        }
    }
    
    // --- Mock Integration Methods ---
    
    private void sendDunningEmail(UUID subId, String templateName) {
        log.debug("MOCK: Sending email template '{}' for sub {}", templateName, subId);
    }
    
    private void pauseSubscriptionServices(UUID subId) {
        log.debug("MOCK: Changing status to PAUSED for sub {}", subId);
    }
    
    private void cancelSubscription(UUID subId, String reason) {
        log.debug("MOCK: Changing status to CANCELED for sub {}. Reason: {}", subId, reason);
    }
}
