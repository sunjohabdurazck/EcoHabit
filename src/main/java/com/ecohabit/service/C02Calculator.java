// C02Calculator.java
package main.java.com.ecohabit.service;

import main.java.com.ecohabit.model.Activity;
import main.java.com.ecohabit.model.User;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class C02Calculator {
    private final Map<String, Double> activityCO2Map;
    private final Map<String, Double> dietMultiplierMap;
    private final Map<String, Double> transportMultiplierMap;
    private final Map<String, Double> userTypeMultiplierMap;
    
    public C02Calculator() {
        activityCO2Map = new HashMap<>();
        dietMultiplierMap = new HashMap<>();
        transportMultiplierMap = new HashMap<>();
        userTypeMultiplierMap = new HashMap<>();
        
        initializeActivityCO2Map();
        initializeMultiplierMaps();
    }
    
    private void initializeActivityCO2Map() {
        // CO2 savings in kg for common activities (base values)
        activityCO2Map.put("bike", 2.6); // per 10km vs car
        activityCO2Map.put("walk", 2.6);
        activityCO2Map.put("public transport", 1.3);
        activityCO2Map.put("reusable", 0.3);
        activityCO2Map.put("recycle", 0.5);
        activityCO2Map.put("plant-based", 2.5);
        activityCO2Map.put("led", 0.8);
        activityCO2Map.put("solar", 5.0);
        activityCO2Map.put("compost", 0.4);
        activityCO2Map.put("local food", 1.2);
    }
    
    private void initializeMultiplierMaps() {
        // Diet preference multipliers (vegan activities get higher impact)
        dietMultiplierMap.put("Vegan", 1.3);
        dietMultiplierMap.put("Vegetarian", 1.2);
        dietMultiplierMap.put("Pescatarian", 1.1);
        dietMultiplierMap.put("Flexitarian", 1.05);
        dietMultiplierMap.put("Omnivore", 1.0);
        
        // Transport preference multipliers (eco-friendly transport users get higher impact)
        transportMultiplierMap.put("Walk", 1.4);
        transportMultiplierMap.put("Bicycle", 1.3);
        transportMultiplierMap.put("Public Transport", 1.2);
        transportMultiplierMap.put("Mixed", 1.1);
        transportMultiplierMap.put("Car", 1.0);
        
        // User type multipliers (professionals/enthusiasts might do activities more effectively)
        userTypeMultiplierMap.put("Sustainability Professional", 1.4);
        userTypeMultiplierMap.put("Eco Enthusiast", 1.3);
        userTypeMultiplierMap.put("Environmental Student", 1.2);
        userTypeMultiplierMap.put("Casual User", 1.0);
    }
    
    public double calculatePersonalizedCO2(Activity activity, User user) {
        double baseCO2 = estimateCO2Savings(activity.getDescription());
        double multiplier = getUserMultiplier(user);
        
        return baseCO2 * multiplier;
    }
    
    public double calculateDailyC02(List<Activity> activities, User user) {
        return activities.stream()
            .mapToDouble(activity -> calculatePersonalizedCO2(activity, user))
            .sum();
    }
    
    public double calculateWeeklyCO2(List<Activity> activities, User user) {
        return calculateDailyC02(activities, user) * 7;
    }
    
    public double estimateCO2Savings(String activityDescription) {
        String description = activityDescription.toLowerCase();
        
        for (Map.Entry<String, Double> entry : activityCO2Map.entrySet()) {
            if (description.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // Default estimate for unknown activities
        return 0.5;
    }
    
    private double getUserMultiplier(User user) {
        double multiplier = 1.0;
        
        // Apply diet multiplier
        if (user.getDietPreference() != null) {
            multiplier *= dietMultiplierMap.getOrDefault(user.getDietPreference(), 1.0);
        }
        
        // Apply transport multiplier
        if (user.getTransportPreference() != null) {
            multiplier *= transportMultiplierMap.getOrDefault(user.getTransportPreference(), 1.0);
        }
        
        // Apply user type multiplier
        if (user.getUserType() != null) {
            multiplier *= userTypeMultiplierMap.getOrDefault(user.getUserType(), 1.0);
        }
        
        // Age-based adjustment (younger users might have more impactful activities)
        if (user.getAge() > 0) {
            if (user.getAge() < 25) multiplier *= 1.1;
            else if (user.getAge() > 65) multiplier *= 0.9;
        }
        
        // Gender-based adjustment (optional - could be based on different consumption patterns)
        if ("Female".equals(user.getGender())) {
            multiplier *= 1.05; // Slight adjustment based on general consumption patterns
        }
        
        return Math.max(0.5, Math.min(2.0, multiplier)); // Cap between 0.5x and 2.0x
    }
    
    // Method to calculate user's baseline CO2 footprint based on demographics
    public double calculateBaselineFootprint(User user) {
        double baseline = 10000.0; // Average annual kg CO2 per person
        
        // Adjust based on diet
        if (user.getDietPreference() != null) {
            switch (user.getDietPreference()) {
                case "Vegan": baseline *= 0.6; break;
                case "Vegetarian": baseline *= 0.7; break;
                case "Pescatarian": baseline *= 0.8; break;
                case "Flexitarian": baseline *= 0.9; break;
                default: break;
            }
        }
        
        // Adjust based on transport
        if (user.getTransportPreference() != null) {
            switch (user.getTransportPreference()) {
                case "Walk": baseline *= 0.5; break;
                case "Bicycle": baseline *= 0.6; break;
                case "Public Transport": baseline *= 0.7; break;
                case "Mixed": baseline *= 0.8; break;
                default: break;
            }
        }
        
        return baseline / 365; // Convert to daily average
    }
    public double calculateDailyC02(List<Activity> activities) {
        return activities.stream()
            .mapToDouble(Activity::getCo2Saved)
            .sum();
    }
    
    public double calculateWeeklyCO2(List<Activity> activities) {
        return calculateDailyC02(activities);
    }
    
}