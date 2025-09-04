package main.java.com.ecohabit.service;

import main.java.com.ecohabit.model.EcoTip;
import main.java.com.ecohabit.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing eco tips
 */
public class TipsService {
    private static final String DB_URL = "jdbc:h2:~/ecohabit";
    private List<EcoTip> tipsCache;
    private Map<Integer, Set<Integer>> userFavorites; // userId -> Set of tip IDs
    private Map<Integer, Set<Integer>> userReadTips; // userId -> Set of tip IDs
    private Random random;

    public TipsService() {
        this.tipsCache = new ArrayList<>();
        this.userFavorites = new HashMap<>();
        this.userReadTips = new HashMap<>();
        this.random = new Random();
        initializeTips();
    }

    /**
     * Initialize with sample tips data
     */
    private void initializeTips() {
        tipsCache.addAll(Arrays.asList(
            createTip("Switch to LED Bulbs", "Energy", "Easy", 
                "Replace incandescent bulbs with LED bulbs to reduce energy consumption by up to 75%. LED bulbs last longer and produce less heat.",
                "💡", 4.5, 8.5),
                
            createTip("Start Composting", "Waste", "Medium", 
                "Turn kitchen scraps into nutrient-rich compost for your garden. Reduces methane emissions from landfills and creates valuable soil amendment.",
                "🌱", 4.2, 12.3),
                
            createTip("Use Public Transport", "Transportation", "Easy", 
                "Take buses, trains, or subways instead of driving. A single bus can replace 40 cars on the road, significantly reducing emissions.",
                "🚌", 4.7, 15.2),
                
            createTip("Grow Your Own Herbs", "Food", "Medium", 
                "Start a small herb garden on your windowsill. Reduces packaging waste and food miles while providing fresh ingredients.",
                "🌿", 4.3, 3.8),
                
            createTip("Install Water-Saving Showerheads", "Water", "Easy", 
                "Low-flow showerheads can reduce water usage by up to 40% without sacrificing pressure. Easy to install and immediate savings.",
                "🚿", 4.1, 0.5),
                
            createTip("Use Reusable Bags", "Shopping", "Easy", 
                "Bring reusable bags when shopping to eliminate single-use plastic bags. Keep them in your car or by your front door.",
                "🛍️", 4.8, 2.1),
                
            createTip("Unplug Electronics", "Energy", "Easy", 
                "Unplug devices when not in use to prevent phantom energy consumption. Can reduce electricity bills by 5-10%.",
                "🔌", 4.0, 6.7),
                
            createTip("Collect Rainwater", "Water", "Hard", 
                "Set up a rainwater collection system for watering plants. Can reduce water bills and help during drought conditions.",
                "☔", 4.4, 25.8),
                
            createTip("Meal Planning", "Food", "Medium", 
                "Plan your meals to reduce food waste and make more sustainable food choices. Can save money and reduce environmental impact.",
                "📝", 4.6, 18.9),
                
            createTip("DIY Natural Cleaners", "Home", "Medium", 
                "Make eco-friendly cleaning products using vinegar, baking soda, and essential oils. Safer for your family and the environment.",
                "🧽", 4.2, 4.2),
                
            createTip("Walk or Bike Short Distances", "Transportation", "Easy",
                "For trips under 2 miles, consider walking or biking instead of driving. Great exercise and zero emissions.",
                "🚶", 4.4, 8.9),
                
            createTip("Reduce Meat Consumption", "Food", "Medium",
                "Try 'Meatless Monday' or reduce meat consumption by one meal per day. Livestock farming is a major source of greenhouse gases.",
                "🥗", 4.1, 32.1),
                
            createTip("Use a Programmable Thermostat", "Energy", "Medium",
                "Install a programmable thermostat to optimize heating and cooling. Can reduce energy usage by 10-23%.",
                "🌡️", 4.3, 28.4),
                
            createTip("Buy Local Produce", "Food", "Easy",
                "Purchase locally grown produce to reduce transportation emissions and support local farmers.",
                "🍎", 4.2, 5.6),
                
            createTip("Fix Water Leaks", "Water", "Easy",
                "Repair dripping faucets and running toilets immediately. A single drip can waste over 3,000 gallons per year.",
                "🔧", 4.0, 12.7),
                
            createTip("Use Cold Water for Washing", "Energy", "Easy",
                "Wash clothes in cold water when possible. Heating water accounts for 90% of washing machine energy use.",
                "🧺", 3.9, 11.3),
                
            createTip("Install Solar Panels", "Energy", "Very Hard",
                "Consider installing solar panels to generate renewable energy. High upfront cost but significant long-term savings.",
                "☀️", 4.6, 150.2),
                
            createTip("Create a Rain Garden", "Water", "Hard",
                "Design a garden that captures and filters rainwater runoff. Helps prevent flooding and recharges groundwater.",
                "🌧️", 4.5, 35.7),
                
            createTip("Buy Energy-Efficient Appliances", "Energy", "Medium",
                "Choose ENERGY STAR certified appliances when replacing old ones. Can reduce energy usage by 10-50%.",
                "⚡", 4.3, 45.8),
                
            createTip("Participate in Community Gardens", "Food", "Medium",
                "Join or start a community garden. Builds local food security and creates green spaces in urban areas.",
                "👥", 4.4, 8.3)
        ));
    }

    /**
     * Create a sample tip
     */
    private EcoTip createTip(String title, String category, String difficulty, 
                            String description, String icon, double rating, double co2Savings) {
        EcoTip tip = new EcoTip();
        tip.setId(generateId());
        tip.setTitle(title);
        tip.setCategory(category);
        tip.setDifficulty(difficulty);
        tip.setDescription(description);
        tip.setIcon(icon);
        tip.setRating(rating);
        tip.setEstimatedCO2Savings(co2Savings);
        tip.setDateCreated(LocalDate.now().minusDays(random.nextInt(365)));
        tip.setLastUpdated(LocalDateTime.now().minusDays(random.nextInt(30)));
        tip.setReadCount(random.nextInt(1000));
        tip.setLikeCount(random.nextInt(500));
        tip.setSource("EcoHabit Community");
        tip.setApproved(true);
        
        // Add some random tags
        String[] possibleTags = {"beginner", "advanced", "money-saving", "quick-win", "lifestyle", "technology", "health"};
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < random.nextInt(3) + 1; i++) {
            String tag = possibleTags[random.nextInt(possibleTags.length)];
            if (!tags.contains(tag)) {
                tags.add(tag);
            }
        }
        tip.setTags(tags.toArray(new String[0]));
        
        return tip;
    }

    /**
     * Generate unique ID for tips
     */
    private int generateId() {
        return Math.abs(UUID.randomUUID().hashCode());
    }

    /**
     * Get all tips
     */
    public List<EcoTip> getAllTips() {
        return new ArrayList<>(tipsCache);
    }

    /**
     * Get tips by category
     */
    public List<EcoTip> getTipsByCategory(String category) {
        if ("All".equals(category)) {
            return getAllTips();
        }
        return tipsCache.stream()
                .filter(tip -> category.equals(tip.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * Get tips by difficulty
     */
    public List<EcoTip> getTipsByDifficulty(String difficulty) {
        if ("All".equals(difficulty)) {
            return getAllTips();
        }
        return tipsCache.stream()
                .filter(tip -> difficulty.equals(tip.getDifficulty()))
                .collect(Collectors.toList());
    }

    /**
     * Search tips by query
     */
    public List<EcoTip> searchTips(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllTips();
        }
        
        String searchQuery = query.toLowerCase().trim();
        return tipsCache.stream()
                .filter(tip -> 
                    tip.getTitle().toLowerCase().contains(searchQuery) ||
                    tip.getDescription().toLowerCase().contains(searchQuery) ||
                    tip.getCategory().toLowerCase().contains(searchQuery) ||
                    Arrays.stream(tip.getTags() != null ? tip.getTags() : new String[0])
                          .anyMatch(tag -> tag.toLowerCase().contains(searchQuery))
                )
                .collect(Collectors.toList());
    }

    /**
     * Get tip by ID
     */
    public EcoTip getTipById(int tipId) {
        return tipsCache.stream()
                .filter(tip -> tip.getId() == tipId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get tip of the day for a user
     */
    public EcoTip getTipOfDay(int userId) {
        // Get user's reading history
        Set<Integer> readTips = userReadTips.getOrDefault(userId, new HashSet<>());
        
        // Filter out already read tips
        List<EcoTip> unreadTips = tipsCache.stream()
                .filter(tip -> !readTips.contains(tip.getId()))
                .collect(Collectors.toList());
        
        // If all tips are read, choose from all tips
        if (unreadTips.isEmpty()) {
            unreadTips = new ArrayList<>(tipsCache);
        }
        
        // Select a random tip, weighted by rating
        return selectWeightedRandomTip(unreadTips);
    }

    /**
     * Get personalized recommendations for a user
     */
    public List<EcoTip> getPersonalizedRecommendations(int userId, int limit) {
        Set<Integer> favoriteIds = userFavorites.getOrDefault(userId, new HashSet<>());
        Set<Integer> readIds = userReadTips.getOrDefault(userId, new HashSet<>());
        
        // Get categories from user's favorites
        Set<String> favoriteCategories = tipsCache.stream()
                .filter(tip -> favoriteIds.contains(tip.getId()))
                .map(EcoTip::getCategory)
                .collect(Collectors.toSet());
        
        // Recommend tips from favorite categories that haven't been read
        List<EcoTip> recommendations = tipsCache.stream()
                .filter(tip -> !readIds.contains(tip.getId()))
                .filter(tip -> favoriteCategories.isEmpty() || favoriteCategories.contains(tip.getCategory()))
                .sorted(Comparator.comparing(EcoTip::getRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());
        
        // If not enough recommendations, fill with popular tips
        if (recommendations.size() < limit) {
            List<EcoTip> popularTips = tipsCache.stream()
                    .filter(tip -> !readIds.contains(tip.getId()))
                    .filter(tip -> !recommendations.contains(tip))
                    .filter(EcoTip::isPopular)
                    .sorted(Comparator.comparing(EcoTip::getRating).reversed())
                    .limit(limit - recommendations.size())
                    .collect(Collectors.toList());
            recommendations.addAll(popularTips);
        }
        
        return recommendations;
    }

    /**
     * Get featured tips
     */
    public List<EcoTip> getFeaturedTips() {
        return tipsCache.stream()
                .filter(EcoTip::isFeatured)
                .sorted(Comparator.comparing(EcoTip::getRating).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Get recent tips
     */
    public List<EcoTip> getRecentTips(int limit) {
        return tipsCache.stream()
                .sorted(Comparator.comparing(EcoTip::getDateCreated).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get popular tips
     */
    public List<EcoTip> getPopularTips(int limit) {
        return tipsCache.stream()
                .sorted(Comparator.comparing(EcoTip::getReadCount).reversed()
                        .thenComparing(EcoTip::getRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Mark tip as read by user
     */
    public void markTipAsRead(int userId, int tipId) {
        userReadTips.computeIfAbsent(userId, k -> new HashSet<>()).add(tipId);
        
        // Increment read count
        EcoTip tip = getTipById(tipId);
        if (tip != null) {
            tip.incrementReadCount();
        }
    }

    /**
     * Add tip to user favorites
     */
    public boolean addToFavorites(int userId, int tipId) {
        Set<Integer> favorites = userFavorites.computeIfAbsent(userId, k -> new HashSet<>());
        boolean added = favorites.add(tipId);
        
        if (added) {
            EcoTip tip = getTipById(tipId);
            if (tip != null) {
                tip.incrementLikeCount();
            }
        }
        
        return added;
    }

    /**
     * Remove tip from user favorites
     */
    public boolean removeFromFavorites(int userId, int tipId) {
        Set<Integer> favorites = userFavorites.get(userId);
        if (favorites == null) return false;
        
        boolean removed = favorites.remove(tipId);
        
        if (removed) {
            EcoTip tip = getTipById(tipId);
            if (tip != null) {
                tip.decrementLikeCount();
            }
        }
        
        return removed;
    }

    /**
     * Check if tip is in user favorites
     */
    public boolean isFavorite(int userId, int tipId) {
        Set<Integer> favorites = userFavorites.get(userId);
        return favorites != null && favorites.contains(tipId);
    }

    /**
     * Get user's favorite tips
     */
    public List<EcoTip> getUserFavorites(int userId) {
        Set<Integer> favoriteIds = userFavorites.getOrDefault(userId, new HashSet<>());
        return tipsCache.stream()
                .filter(tip -> favoriteIds.contains(tip.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Rate a tip
     */
    public void rateTip(int tipId, double rating, int userId) {
        EcoTip tip = getTipById(tipId);
        if (tip != null && rating >= 0 && rating <= 5) {
            // Simple rating update (in real app, you'd track individual ratings)
            double currentRating = tip.getRating();
            int readCount = tip.getReadCount();
            
            // Weighted average with new rating
            double newRating = ((currentRating * readCount) + rating) / (readCount + 1);
            tip.setRating(newRating);
        }
    }

    /**
     * Get tips statistics
     */
    public Map<String, Object> getTipsStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalTips", tipsCache.size());
        stats.put("averageRating", tipsCache.stream().mapToDouble(EcoTip::getRating).average().orElse(0.0));
        stats.put("totalReadCount", tipsCache.stream().mapToInt(EcoTip::getReadCount).sum());
        stats.put("totalLikes", tipsCache.stream().mapToInt(EcoTip::getLikeCount).sum());
        
        // Category breakdown
        Map<String, Long> categoryStats = tipsCache.stream()
                .collect(Collectors.groupingBy(EcoTip::getCategory, Collectors.counting()));
        stats.put("categoryBreakdown", categoryStats);
        
        // Difficulty breakdown
        Map<String, Long> difficultyStats = tipsCache.stream()
                .collect(Collectors.groupingBy(EcoTip::getDifficulty, Collectors.counting()));
        stats.put("difficultyBreakdown", difficultyStats);
        
        return stats;
    }

    /**
     * Select weighted random tip based on rating
     */
    private EcoTip selectWeightedRandomTip(List<EcoTip> tips) {
        if (tips.isEmpty()) return null;
        if (tips.size() == 1) return tips.get(0);
        
        // Calculate total weight
        double totalWeight = tips.stream().mapToDouble(tip -> Math.max(tip.getRating(), 0.1)).sum();
        
        // Random selection
        double randomValue = random.nextDouble() * totalWeight;
        double currentWeight = 0;
        
        for (EcoTip tip : tips) {
            currentWeight += Math.max(tip.getRating(), 0.1);
            if (currentWeight >= randomValue) {
                return tip;
            }
        }
        
        // Fallback to last tip
        return tips.get(tips.size() - 1);
    }

    /**
     * Add new tip
     */
    public EcoTip addTip(EcoTip tip) {
        if (tip == null) return null;
        
        tip.setId(generateId());
        tip.setDateCreated(LocalDate.now());
        tip.setLastUpdated(LocalDateTime.now());
        tip.setApproved(false); // New tips need approval
        
        tipsCache.add(tip);
        return tip;
    }

    /**
     * Update existing tip
     */
    public boolean updateTip(EcoTip updatedTip) {
        if (updatedTip == null) return false;
        
        for (int i = 0; i < tipsCache.size(); i++) {
            if (tipsCache.get(i).getId() == updatedTip.getId()) {
                updatedTip.setLastUpdated(LocalDateTime.now());
                tipsCache.set(i, updatedTip);
                return true;
            }
        }
        return false;
    }

    /**
     * Delete tip
     */
    public boolean deleteTip(int tipId) {
        return tipsCache.removeIf(tip -> tip.getId() == tipId);
    }

    /**
     * Get all categories
     */
    public List<String> getAllCategories() {
        return tipsCache.stream()
                .map(EcoTip::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get all difficulties
     */
    public List<String> getAllDifficulties() {
        return Arrays.asList("Easy", "Medium", "Hard", "Very Hard");
    }
}