package com.merryblue.api.service;

import com.merryblue.api.repository.BlogPostRepository;
import com.merryblue.api.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that performs time-series analysis and linear regression on historical data 
 * to forecast future trends, such as expected job application volume or blog traffic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PredictiveAnalyticsService {

    private final JobApplicationRepository applicationRepository;
    private final BlogPostRepository blogRepository;

    /**
     * Forecasts the expected number of job applications for the next N days 
     * based on historical velocity and seasonal (day-of-week) adjustments.
     */
    public Map<String, Object> forecastJobApplicationVolume(int daysToForecast, int historicalLookbackDays) {
        log.info("Generating predictive forecast for job applications over the next {} days using {} days history", daysToForecast, historicalLookbackDays);

        if (historicalLookbackDays < 14) {
            log.warn("Lookback period is very short ({} days). Forecast may be inaccurate.", historicalLookbackDays);
        }

        LocalDate today = LocalDate.now();
        OffsetDateTime historyStart = OffsetDateTime.now().minusDays(historicalLookbackDays);

        // Fetch historical data
        var historicalApps = applicationRepository.findAll().stream()
                .filter(app -> app.getCreatedAt().isAfter(historyStart))
                .toList();

        // 1. Group by Date to build time series
        Map<LocalDate, Long> dailyCounts = new HashMap<>();
        for (int i = 0; i <= historicalLookbackDays; i++) {
            dailyCounts.put(today.minusDays(i), 0L); // Initialize all days
        }
        historicalApps.forEach(app -> {
            LocalDate d = app.getCreatedAt().toLocalDate();
            dailyCounts.put(d, dailyCounts.getOrDefault(d, 0L) + 1);
        });

        // 2. Calculate Day-of-Week Seasonality Factors
        Map<DayOfWeek, Double> seasonalityFactors = calculateSeasonalityFactors(dailyCounts);

        // 3. Perform Simple Linear Regression on the trend (ignoring seasonality for base slope)
        LinearRegressionResult trend = performLinearRegression(dailyCounts);
        
        // 4. Generate Forecast
        List<Map<String, Object>> forecastData = new ArrayList<>();
        long totalForecasted = 0;

        for (int i = 1; i <= daysToForecast; i++) {
            LocalDate targetDate = today.plusDays(i);
            int xValue = historicalLookbackDays + i; // Continue the x-axis from history
            
            // Base prediction from regression line: y = mx + b
            double basePrediction = (trend.slope * xValue) + trend.intercept;
            
            // Apply seasonality factor
            double seasonalFactor = seasonalityFactors.getOrDefault(targetDate.getDayOfWeek(), 1.0);
            double finalPrediction = Math.max(0, basePrediction * seasonalFactor); // Cannot have negative applications
            
            long predictedCount = Math.round(finalPrediction);
            totalForecasted += predictedCount;

            Map<String, Object> dailyForecast = new HashMap<>();
            dailyForecast.put("date", targetDate.toString());
            dailyForecast.put("dayOfWeek", targetDate.getDayOfWeek().name());
            dailyForecast.put("predictedVolume", predictedCount);
            forecastData.add(dailyForecast);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("lookbackPeriodDays", historicalLookbackDays);
        result.put("historicalDataPoints", dailyCounts.size());
        result.put("trendSlope", trend.slope); // Positive means growing, negative means shrinking
        result.put("seasonalityFactors", seasonalityFactors);
        result.put("forecastHorizonDays", daysToForecast);
        result.put("totalForecastedVolume", totalForecasted);
        result.put("dailyForecasts", forecastData);

        return result;
    }

    private Map<DayOfWeek, Double> calculateSeasonalityFactors(Map<LocalDate, Long> dailyCounts) {
        Map<DayOfWeek, Long> sumsByDay = new EnumMap<>(DayOfWeek.class);
        Map<DayOfWeek, Integer> countsByDay = new EnumMap<>(DayOfWeek.class);

        long totalSum = 0;
        int totalDays = 0;

        for (Map.Entry<LocalDate, Long> entry : dailyCounts.entrySet()) {
            DayOfWeek day = entry.getKey().getDayOfWeek();
            sumsByDay.put(day, sumsByDay.getOrDefault(day, 0L) + entry.getValue());
            countsByDay.put(day, countsByDay.getOrDefault(day, 0) + 1);
            totalSum += entry.getValue();
            totalDays++;
        }

        double overallAverage = totalDays > 0 ? (double) totalSum / totalDays : 0;
        Map<DayOfWeek, Double> factors = new EnumMap<>(DayOfWeek.class);

        for (DayOfWeek day : DayOfWeek.values()) {
            int count = countsByDay.getOrDefault(day, 0);
            if (count > 0 && overallAverage > 0) {
                double dayAverage = (double) sumsByDay.get(day) / count;
                factors.put(day, dayAverage / overallAverage);
            } else {
                factors.put(day, 1.0); // Neutral factor if no data
            }
        }
        return factors;
    }

    private LinearRegressionResult performLinearRegression(Map<LocalDate, Long> dailyCounts) {
        if (dailyCounts.size() < 2) return new LinearRegressionResult(0, 0);

        // Sort by date to create an ordered x-axis (0, 1, 2...)
        List<Map.Entry<LocalDate, Long>> sortedData = dailyCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();

        int n = sortedData.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            double x = i;
            double y = sortedData.get(i).getValue();
            
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double denominator = (n * sumX2) - (sumX * sumX);
        if (denominator == 0) return new LinearRegressionResult(0, sumY / n); // Flat line at average if X variance is 0

        double slope = ((n * sumXY) - (sumX * sumY)) / denominator;
        double intercept = (sumY - (slope * sumX)) / n;

        return new LinearRegressionResult(slope, intercept);
    }

    private record LinearRegressionResult(double slope, double intercept) {}
}
