package com.merryblue.api.service;

import com.merryblue.api.model.Service;
import com.merryblue.api.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service responsible for calculating dynamic pricing for digital services based on
 * multiple real-time factors including current system load (mocked), time of day,
 * day of week, urgency multipliers, and historical conversion rates.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicPricingEngineService {

    private final ServiceRepository serviceRepository;

    // Base pricing models (mock mapping)
    private static final Map<String, BigDecimal> BASE_PRICES = Map.of(
            "WEB_DEV", new BigDecimal("5000.00"),
            "SEO", new BigDecimal("1000.00"),
            "MARKETING", new BigDecimal("2000.00")
    );

    /**
     * Calculates the current dynamic price quote for a specific service offering.
     */
    public Map<String, Object> calculateQuote(UUID serviceId, String urgencyLevel, String clientTier) {
        log.info("Calculating dynamic price quote for service: {}, urgency: {}, tier: {}", serviceId, urgencyLevel, clientTier);

        Service targetService = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service offering not found"));

        // Determine base category key (mock logic based on title)
        String categoryKey = determineCategoryKey(targetService.getTitleEn());
        BigDecimal basePrice = BASE_PRICES.getOrDefault(categoryKey, new BigDecimal("1500.00"));

        Map<String, Object> quoteDetails = new HashMap<>();
        quoteDetails.put("basePrice", basePrice);

        // 1. Time-based surge pricing
        BigDecimal timeSurgeMultiplier = calculateTimeSurgeMultiplier();
        quoteDetails.put("timeSurgeMultiplier", timeSurgeMultiplier);

        // 2. Urgency multiplier
        BigDecimal urgencyMultiplier = calculateUrgencyMultiplier(urgencyLevel);
        quoteDetails.put("urgencyMultiplier", urgencyMultiplier);

        // 3. Client tier discount (Loyalty/Enterprise discounts)
        BigDecimal tierDiscountMultiplier = calculateTierDiscount(clientTier);
        quoteDetails.put("tierDiscountMultiplier", tierDiscountMultiplier);

        // 4. Current Capacity/Load surge (Mocked system load)
        BigDecimal capacitySurgeMultiplier = calculateCapacitySurge();
        quoteDetails.put("capacitySurgeMultiplier", capacitySurgeMultiplier);

        // Final Price Calculation Formula:
        // Final = Base * (TimeSurge + CapacitySurge - 1) * Urgency * (1 - TierDiscount)
        
        // Combine additive surges
        BigDecimal combinedSurge = timeSurgeMultiplier.add(capacitySurgeMultiplier).subtract(BigDecimal.ONE);
        
        BigDecimal interimPrice = basePrice.multiply(combinedSurge).multiply(urgencyMultiplier);
        
        // Apply discount multiplier
        BigDecimal discountFactor = BigDecimal.ONE.subtract(tierDiscountMultiplier);
        BigDecimal finalPrice = interimPrice.multiply(discountFactor);

        // Ensure price doesn't drop below a minimum threshold (e.g., 80% of base)
        BigDecimal minThreshold = basePrice.multiply(new BigDecimal("0.80"));
        if (finalPrice.compareTo(minThreshold) < 0) {
            finalPrice = minThreshold;
            quoteDetails.put("floorPriceApplied", true);
        } else {
            quoteDetails.put("floorPriceApplied", false);
        }

        // Round to 2 decimal places
        finalPrice = finalPrice.setScale(2, RoundingMode.HALF_UP);
        quoteDetails.put("finalQuotePrice", finalPrice);
        
        log.info("Calculated final quote: {}", finalPrice);
        
        // Generate a breakdown string for the client invoice
        quoteDetails.put("breakdownText", generateBreakdownText(basePrice, combinedSurge, urgencyMultiplier, tierDiscountMultiplier, finalPrice));

        return quoteDetails;
    }

    private String determineCategoryKey(String title) {
        if (title == null) return "DEFAULT";
        String lower = title.toLowerCase();
        if (lower.contains("web") || lower.contains("app") || lower.contains("develop")) return "WEB_DEV";
        if (lower.contains("seo") || lower.contains("search")) return "SEO";
        if (lower.contains("marketing") || lower.contains("ads")) return "MARKETING";
        return "DEFAULT";
    }

    private BigDecimal calculateTimeSurgeMultiplier() {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek day = now.getDayOfWeek();
        int hour = now.getHour();

        BigDecimal surge = BigDecimal.ONE; // Default 1.0

        // Weekend surge (+10%)
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            surge = surge.add(new BigDecimal("0.10"));
        }

        // Outside business hours surge (+5%)
        if (hour < 9 || hour >= 18) {
            surge = surge.add(new BigDecimal("0.05"));
        }

        return surge;
    }

    private BigDecimal calculateUrgencyMultiplier(String urgency) {
        if (urgency == null) return BigDecimal.ONE;
        return switch (urgency.toUpperCase()) {
            case "RUSH" -> new BigDecimal("1.50");     // 50% premium
            case "EXPRESS" -> new BigDecimal("1.25");  // 25% premium
            case "STANDARD" -> BigDecimal.ONE;         // No change
            case "FLEXIBLE" -> new BigDecimal("0.90"); // 10% discount for flexibility
            default -> BigDecimal.ONE;
        };
    }

    private BigDecimal calculateTierDiscount(String tier) {
        if (tier == null) return BigDecimal.ZERO;
        return switch (tier.toUpperCase()) {
            case "ENTERPRISE" -> new BigDecimal("0.15"); // 15% discount
            case "GOLD" -> new BigDecimal("0.10");       // 10% discount
            case "SILVER" -> new BigDecimal("0.05");     // 5% discount
            default -> BigDecimal.ZERO;
        };
    }

    private BigDecimal calculateCapacitySurge() {
        // Mock logic: Simulate system load check. If we are busy, prices go up.
        // In reality, this would query active project counts.
        double randomLoad = Math.random(); // 0.0 to 1.0
        
        if (randomLoad > 0.8) {
            // High capacity -> 20% surge
            return new BigDecimal("1.20");
        } else if (randomLoad > 0.5) {
            // Medium capacity -> 5% surge
            return new BigDecimal("1.05");
        }
        
        return BigDecimal.ONE; // Normal capacity
    }
    
    private String generateBreakdownText(BigDecimal base, BigDecimal surge, BigDecimal urgency, BigDecimal discount, BigDecimal finalPrice) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Base Service Rate: $%.2f\n", base));
        
        if (surge.compareTo(BigDecimal.ONE) > 0) {
            BigDecimal surgeAmount = base.multiply(surge.subtract(BigDecimal.ONE));
            sb.append(String.format("Market Demand Adjustment: +$%.2f\n", surgeAmount));
        }
        
        if (urgency.compareTo(BigDecimal.ONE) > 0) {
            sb.append(String.format("Urgency Premium: x%.2f\n", urgency));
        } else if (urgency.compareTo(BigDecimal.ONE) < 0) {
            sb.append(String.format("Flexible Timeline Discount: x%.2f\n", urgency));
        }
        
        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(String.format("Loyalty Tier Discount Applied: -%.0f%%\n", discount.multiply(new BigDecimal("100"))));
        }
        
        sb.append("-----------------------------\n");
        sb.append(String.format("Estimated Total: $%.2f", finalPrice));
        
        return sb.toString();
    }
}
