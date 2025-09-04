package main.java.com.ecohabit.service;

import main.java.com.ecohabit.model.Badge;
import main.java.com.ecohabit.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing badges and achievements
 */
public class BadgeService {
    private List<Badge> allBadges;
    private Map<Integer, List<Badge>> userBadges; // userId -> List of user's badges
    private Map<String, Badge> badgeTemplates; // badgeId -> template for creating user badges
    private Random random;

    public BadgeService() {
        this.allBadges = new ArrayList<>();
        this.userBadges = new HashMap<>();
        this.badgeTemplates = new HashMap<>();
        this.random = new Random();
        
        initializeBadgeTemplates();
        initializeSampleUserBadges();
    }

    /**
     * Initialize badge templates (available badges to earn)
     */
    private void initializeBadgeTemplates() {
        List<Badge> templates = Arrays.asList(
            // Getting Started Badges
            createBadgeTemplate("first_steps", "First Steps", "Getting Started", "Easy", "🌱",
                "Complete your first eco-friendly activity", 1, 10,
                "Complete any eco-friendly activity"),
                
            createBadgeTemplate("first_week", "First Week", "Getting Started", "Easy", "📅",
                "Complete activities for 7 consecutive days", 7, 15,
                "Maintain activity streak for 7 days"),
                
            createBadgeTemplate("habit_builder", "Habit Builder", "Getting Started", "Medium", "🏗️",
                "Complete 30 eco-friendly activities", 30, 25,
                "Complete 30 activities total"),

            // Environmental Impact Badges
            createBadgeTemplate("carbon_saver", "Carbon Saver", "Environmental Impact", "Medium", "🌍",
                "Save 25kg of CO₂ through your activities", 25, 25,
                "Achieve 25kg CO₂ savings"),
                
            createBadgeTemplate("eco_champion", "Eco Champion", "Environmental Impact", "Hard", "👑",
                "Save 100kg of CO₂ through your activities", 100, 50,
                "Achieve 100kg CO₂ savings"),
                
            createBadgeTemplate("planet_protector", "Planet Protector", "Environmental Impact", "Very Hard", "🛡️",
                "Save 500kg of CO₂ through your activities", 500, 100,
                "Achieve 500kg CO₂ savings"),

            // Streak Badges
            createBadgeTemplate("week_warrior", "Week Warrior", "Streak", "Medium", "🔥",
                "Maintain a 7-day activity streak", 7, 25,
                "Complete activities 7 days in a row"),
                
            createBadgeTemplate("streak_legend", "Streak Legend", "Streak", "Hard", "🏆",
                "Maintain a 30-day activity streak", 30, 50,
                "Complete activities 30 days in a row"),
                
            createBadgeTemplate("perfect_month", "Perfect Month", "Streak", "Very Hard", "⭐",
                "Complete eco activities every day for a month", 30, 100,
                "Complete activities every day for 30 days"),

            // Transportation Badges
            createBadgeTemplate("transport_master", "Transport Master", "Transportation", "Medium", "🚲",
                "Use eco-friendly transport 50 times", 50, 25,
                "Use public transport, bike, or walk 50 times"),
                
            createBadgeTemplate("car_free_champion", "Car-Free Champion", "Transportation", "Hard", "🚌",
                "Go car-free for 30 days", 30, 50,
                "Use only eco-friendly transport for 30 days"),
                
            createBadgeTemplate("commute_hero", "Commute Hero", "Transportation", "Medium", "🚇",
                "Use public transport for daily commute 20 times", 20, 30,
                "Take public transport to work/school 20 times"),

            // Waste Reduction Badges
            createBadgeTemplate("recycling_hero", "Recycling Hero", "Waste Reduction", "Medium", "♻️",
                "Complete 25 recycling activities", 25, 25,
                "Recycle items 25 times"),
                
            createBadgeTemplate("zero_waste_warrior", "Zero Waste Warrior", "Waste Reduction", "Very Hard", "🗂️",
                "Achieve zero waste for 7 consecutive days", 7, 100,
                "Produce no waste for 7 days straight"),
                
            createBadgeTemplate("compost_king", "Compost King", "Waste Reduction", "Medium", "🌱",
                "Start and maintain composting for 30 days", 30, 30,
                "Compost organic waste for 30 days"),

            // Energy Conservation Badges
            createBadgeTemplate("energy_saver", "Energy Saver", "Energy Conservation", "Medium", "💡",
                "Save 100kWh of energy", 100, 25,
                "Reduce energy consumption by 100kWh"),
                
            createBadgeTemplate("solar_pioneer", "Solar Pioneer", "Energy Conservation", "Hard", "☀️",
                "Install or use solar power", 1, 50,
                "Set up solar energy system"),
                
            createBadgeTemplate("led_champion", "LED Champion", "Energy Conservation", "Easy", "💡",
                "Switch 10 bulbs to LED", 10, 15,
                "Replace 10 traditional bulbs with LED"),

            // Food & Garden Badges
            createBadgeTemplate("plant_parent", "Plant Parent", "Food & Garden", "Hard", "🌿",
                "Grow 10 plants or herbs", 10, 50,
                "Successfully grow 10 plants"),
                
            createBadgeTemplate("local_food_lover", "Local Food Lover", "Food & Garden", "Medium", "🍎",
                "Buy local produce 20 times", 20, 25,
                "Purchase locally grown food 20 times"),
                
            createBadgeTemplate("veggie_champion", "Veggie Champion", "Food & Garden", "Medium", "🥗",
                "Eat plant-based meals 50 times", 50, 30,
                "Choose vegetarian/vegan meals 50 times"),

            // Water Conservation Badges
            createBadgeTemplate("water_guardian", "Water Guardian", "Water Conservation", "Hard", "💧",
                "Save 1000 gallons of water", 1000, 50,
                "Conserve 1000 gallons through water-saving activities"),
                
            createBadgeTemplate("rain_harvester", "Rain Harvester", "Water Conservation", "Hard", "☔",
                "Collect and use rainwater", 1, 45,
                "Set up rainwater collection system"),
                
            createBadgeTemplate("leak_detective", "Leak Detective", "Water Conservation", "Easy", "🔧",
                "Fix 5 water leaks", 5, 15,
                "Repair water leaks in home"),

            // Social Impact Badges
            createBadgeTemplate("community_leader", "Community Leader", "Social Impact", "Medium", "👥",
                "Invite 5 friends to join EcoHabit", 5, 30,
                "Refer 5 friends to the app"),
                
            createBadgeTemplate("eco_educator", "Eco Educator", "Social Impact", "Medium", "📚",
                "Share 10 eco-tips with others", 10, 25,
                "Share environmental knowledge 10 times"),
                
            createBadgeTemplate("change_maker", "Change Maker", "Social Impact", "Hard", "🌟",
                "Inspire 20 people to take eco-action", 20, 50,
                "Motivate others to adopt eco-friendly habits")
        );
        
        // Store templates
        for (Badge template : templates) {
            badgeTemplates.put(template.getTitle().toLowerCase().replace(" ", "_"), template);
            allBadges.add(template);
        }
    }

    /**
     * Create a badge template
     */
    private Badge createBadgeTemplate(String key, String title, String category, String difficulty, 
                                    String icon, String description, int targetValue, int points,
                                    String unlockCondition) {
        Badge badge = new Badge(title, description, category, difficulty);
        badge.setId(generateId());
        badge.setIcon(icon);
        badge.setTargetValue(targetValue);
        badge.setPoints(points);
        badge.setUnlockCondition(unlockCondition);
        badge.setBadgeType("achievement");
        badge.setSortOrder(getSortOrderForCategory(category));
        
        // Add relevant tags
        List<String> tags = new ArrayList<>();
        tags.add(category.toLowerCase().replace(" ", "_"));
        tags.add(difficulty.toLowerCase().replace(" ", "_"));
        if (targetValue <= 10) tags.add("beginner");
        if (points >= 50) tags.add("challenging");
        
        badge.setTags(tags.toArray(new String[0]));
        
        return badge;
    }

    /**
     * Get sort order for category
     */
    private int getSortOrderForCategory(String category) {
        switch (category) {
            case "Getting Started": return 1;
            case "Environmental Impact": return 2;
            case "Streak": return 3;
            case "Transportation": return 4;
            case "Waste Reduction": return 5;
            case "Energy Conservation": return 6;
            case "Food & Garden": return 7;
            case "Water Conservation": return 8;
            case "Social Impact": return 9;
            default: return 10;
        }
    }

    /**
     * Initialize sample user badges for demo
     */
    private void initializeSampleUserBadges() {
        int userId = 1; // Sample user ID
        List<Badge> userBadgeList = new ArrayList<>();
        
        // Create earned badges
        Badge firstSteps = createUserBadge("first_steps", userId, true, 100, 1);
        firstSteps.setEarnedDate(LocalDateTime.now().minusDays(30));
        userBadgeList.add(firstSteps);
        
        Badge carbonSaver = createUserBadge("carbon_saver", userId, true, 100, 25);
        carbonSaver.setEarnedDate(LocalDateTime.now().minusDays(25));
        userBadgeList.add(carbonSaver);
        
        Badge weekWarrior = createUserBadge("week_warrior", userId, true, 100, 7);
        weekWarrior.setEarnedDate(LocalDateTime.now().minusDays(20));
        userBadgeList.add(weekWarrior);
        
        Badge recyclingHero = createUserBadge("recycling_hero", userId, true, 100, 25);
        recyclingHero.setEarnedDate(LocalDateTime.now().minusDays(15));
        userBadgeList.add(recyclingHero);
        
        Badge energySaver = createUserBadge("energy_saver", userId, true, 100, 100);
        energySaver.setEarnedDate(LocalDateTime.now().minusDays(10));
        userBadgeList.add(energySaver);
        
        // Create in-progress badges
        Badge transportMaster = createUserBadge("transport_master", userId, false, 70, 35);
        userBadgeList.add(transportMaster);
        
        Badge plantParent = createUserBadge("plant_parent", userId, false, 60, 6);
        userBadgeList.add(plantParent);
        
        Badge streakLegend = createUserBadge("streak_legend", userId, false, 50, 15);
        userBadgeList.add(streakLegend);
        
        // Create locked badges
        Badge ecoChampion = createUserBadge("eco_champion", userId, false, 0, 0);
        userBadgeList.add(ecoChampion);
        
        Badge communityLeader = createUserBadge("community_leader", userId, false, 0, 0);
        userBadgeList.add(communityLeader);
        
        Badge perfectMonth = createUserBadge("perfect_month", userId, false, 0, 0);
        userBadgeList.add(perfectMonth);
        
        Badge waterGuardian = createUserBadge("water_guardian", userId, false, 0, 0);
        userBadgeList.add(waterGuardian);
        
        Badge zeroWasteWarrior = createUserBadge("zero_waste_warrior", userId, false, 0, 0);
        userBadgeList.add(zeroWasteWarrior);
        
        userBadges.put(userId, userBadgeList);
    }

    /**
     * Create user badge from template
     */
    private Badge createUserBadge(String templateKey, int userId, boolean earned, int progress, int currentValue) {
        Badge template = badgeTemplates.get(templateKey);
        if (template == null) return null;
        
        Badge userBadge = template.copy();
        userBadge.setId(generateId());
        userBadge.setUserId(userId);
        userBadge.setEarned(earned);
        userBadge.setProgress(progress);
        userBadge.setCurrentValue(currentValue);
        
        return userBadge;
    }

    /**
     * Generate unique ID
     */
    private int generateId() {
        return Math.abs(UUID.randomUUID().hashCode());
    }

    /**
     * Get all badges for a user
     */
    public List<Badge> getUserBadges(int userId) {
        return userBadges.getOrDefault(userId, new ArrayList<>());
    }

    /**
     * Get earned badges for a user
     */
    public List<Badge> getEarnedBadges(int userId) {
        return getUserBadges(userId).stream()
                .filter(Badge::isEarned)
                .sorted(Comparator.comparing(Badge::getEarnedDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get in-progress badges for a user
     */
    public List<Badge> getInProgressBadges(int userId) {
        return getUserBadges(userId).stream()
                .filter(Badge::isInProgress)
                .sorted(Comparator.comparing(Badge::getProgress).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get locked badges for a user
     */
    public List<Badge> getLockedBadges(int userId) {
        return getUserBadges(userId).stream()
                .filter(Badge::isLocked)
                .sorted(Comparator.comparing(Badge::getPoints).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get badges by category for a user
     */
    public List<Badge> getBadgesByCategory(int userId, String category) {
        return getUserBadges(userId).stream()
                .filter(badge -> category.equals("All") || category.equals(badge.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * Get badge by ID
     */
    public Badge getBadgeById(int badgeId) {
        return userBadges.values().stream()
                .flatMap(List::stream)
                .filter(badge -> badge.getId() == badgeId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Update badge progress
     */
    public boolean updateBadgeProgress(int userId, String badgeKey, int newValue) {
        List<Badge> badges = userBadges.get(userId);
        if (badges == null) return false;
        
        Badge badge = badges.stream()
                .filter(b -> badgeKey.equals(b.getTitle().toLowerCase().replace(" ", "_")))
                .findFirst()
                .orElse(null);
        
        if (badge != null && !badge.isEarned()) {
            badge.setCurrentValue(newValue);
            
            // Check if badge should be earned
            if (badge.getProgress() >= 100) {
                awardBadge(userId, badge);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Award badge to user
     */
    public void awardBadge(int userId, Badge badge) {
        if (badge == null || badge.isEarned()) return;
        
        badge.setEarned(true);
        badge.setEarnedDate(LocalDateTime.now());
        badge.setProgress(100);
        
        // In a real app, you might trigger notifications, celebrations, etc.
        System.out.println("Badge awarded to user " + userId + ": " + badge.getTitle());
    }

    /**
     * Create new badge for user from template
     */
    public Badge createUserBadgeFromTemplate(String templateKey, int userId) {
        Badge template = badgeTemplates.get(templateKey);
        if (template == null) return null;
        
        // Check if user already has this badge
        List<Badge> existing = userBadges.getOrDefault(userId, new ArrayList<>());
        boolean hasBadge = existing.stream()
                .anyMatch(b -> b.getTitle().equals(template.getTitle()));
        
        if (hasBadge) return null;
        
        Badge userBadge = createUserBadge(templateKey, userId, false, 0, 0);
        
        userBadges.computeIfAbsent(userId, k -> new ArrayList<>()).add(userBadge);
        
        return userBadge;
    }

    /**
     * Get badge statistics for user
     */
    public Map<String, Object> getUserBadgeStatistics(int userId) {
        List<Badge> badges = getUserBadges(userId);
        
        Map<String, Object> stats = new HashMap<>();
        
        long earnedCount = badges.stream().filter(Badge::isEarned).count();
        long inProgressCount = badges.stream().filter(Badge::isInProgress).count();
        long lockedCount = badges.stream().filter(Badge::isLocked).count();
        
        int totalPoints = badges.stream()
                .filter(Badge::isEarned)
                .mapToInt(Badge::getPoints)
                .sum();
        
        double completionRate = badges.isEmpty() ? 0 : (earnedCount * 100.0) / badges.size();
        
        // Category breakdown
        Map<String, Long> categoryBreakdown = badges.stream()
                .filter(Badge::isEarned)
                .collect(Collectors.groupingBy(Badge::getCategory, Collectors.counting()));
        
        // Rarity breakdown
        Map<String, Long> rarityBreakdown = badges.stream()
                .filter(Badge::isEarned)
                .collect(Collectors.groupingBy(Badge::getRarity, Collectors.counting()));
        
        // Recent achievements (last 30 days)
        long recentAchievements = badges.stream()
                .filter(Badge::isEarned)
                .filter(b -> b.getEarnedDate() != null && 
                            b.getEarnedDate().isAfter(LocalDateTime.now().minusDays(30)))
                .count();
        
        stats.put("totalBadges", badges.size());
        stats.put("earnedBadges", earnedCount);
        stats.put("inProgressBadges", inProgressCount);
        stats.put("lockedBadges", lockedCount);
        stats.put("totalPoints", totalPoints);
        stats.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
        stats.put("categoryBreakdown", categoryBreakdown);
        stats.put("rarityBreakdown", rarityBreakdown);
        stats.put("recentAchievements", recentAchievements);
        
        return stats;
    }

    /**
     * Get global badge statistics
     */
    public Map<String, Object> getGlobalBadgeStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        int totalUsers = userBadges.size();
        
        // Total badges across all users
        long totalUserBadges = userBadges.values().stream()
                .mapToLong(List::size)
                .sum();
        
        // Total earned badges
        long totalEarnedBadges = userBadges.values().stream()
                .flatMap(List::stream)
                .filter(Badge::isEarned)
                .count();
        
        // Most popular badges
        Map<String, Long> badgePopularity = userBadges.values().stream()
                .flatMap(List::stream)
                .filter(Badge::isEarned)
                .collect(Collectors.groupingBy(Badge::getTitle, Collectors.counting()));
        
        List<Map.Entry<String, Long>> popularBadges = badgePopularity.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalBadgeTemplates", badgeTemplates.size());
        stats.put("totalUserBadges", totalUserBadges);
        stats.put("totalEarnedBadges", totalEarnedBadges);
        stats.put("averageBadgesPerUser", totalUsers > 0 ? totalUserBadges / totalUsers : 0);
        stats.put("popularBadges", popularBadges);
        
        return stats;
    }

    /**
     * Get featured badge for user (next achievable badge)
     */
    public Badge getFeaturedBadge(int userId) {
        return getInProgressBadges(userId).stream()
                .max(Comparator.comparing(Badge::getProgress))
                .orElse(
                    getLockedBadges(userId).stream()
                        .min(Comparator.comparing(Badge::getPoints))
                        .orElse(null)
                );
    }

    /**
     * Get badges close to completion
     */
    public List<Badge> getBadgesCloseToCompletion(int userId, int threshold) {
        return getInProgressBadges(userId).stream()
                .filter(badge -> badge.getProgress() >= threshold)
                .sorted(Comparator.comparing(Badge::getProgress).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Search badges by title or description
     */
    public List<Badge> searchBadges(int userId, String query) {
        if (query == null || query.trim().isEmpty()) {
            return getUserBadges(userId);
        }
        
        String searchQuery = query.toLowerCase().trim();
        return getUserBadges(userId).stream()
                .filter(badge -> 
                    badge.getTitle().toLowerCase().contains(searchQuery) ||
                    badge.getDescription().toLowerCase().contains(searchQuery) ||
                    badge.getCategory().toLowerCase().contains(searchQuery) ||
                    Arrays.stream(badge.getTags() != null ? badge.getTags() : new String[0])
                          .anyMatch(tag -> tag.toLowerCase().contains(searchQuery))
                )
                .collect(Collectors.toList());
    }

    /**
     * Get recommended badges for user
     */
    public List<Badge> getRecommendedBadges(int userId, int limit) {
        List<Badge> earnedBadges = getEarnedBadges(userId);
        
        // Get categories user has earned badges in
        Set<String> userCategories = earnedBadges.stream()
                .map(Badge::getCategory)
                .collect(Collectors.toSet());
        
        // Recommend badges from same categories that are not yet earned
        List<Badge> recommendations = getUserBadges(userId).stream()
                .filter(badge -> !badge.isEarned())
                .filter(badge -> userCategories.contains(badge.getCategory()))
                .sorted(Comparator.comparing(Badge::getDifficulty)
                        .thenComparing(Badge::getPoints))
                .limit(limit)
                .collect(Collectors.toList());
        
        // If not enough recommendations, add some easy badges
        if (recommendations.size() < limit) {
            List<Badge> easyBadges = getUserBadges(userId).stream()
                    .filter(badge -> !badge.isEarned())
                    .filter(badge -> "Easy".equals(badge.getDifficulty()))
                    .filter(badge -> !recommendations.contains(badge))
                    .limit(limit - recommendations.size())
                    .collect(Collectors.toList());
            
            recommendations.addAll(easyBadges);
        }
        
        return recommendations;
    }

    /**
     * Get all available categories
     */
    public List<String> getAllCategories() {
        return badgeTemplates.values().stream()
                .map(Badge::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get all available difficulties
     */
    public List<String> getAllDifficulties() {
        return Arrays.asList("Easy", "Medium", "Hard", "Very Hard");
    }

    /**
     * Get all available rarities
     */
    public List<String> getAllRarities() {
        return Arrays.asList("Common", "Uncommon", "Rare", "Epic", "Legendary");
    }

    /**
     * Simulate badge progress update based on activity
     */
    public List<Badge> updateProgressFromActivity(int userId, String activityType, double co2Savings) {
        List<Badge> updatedBadges = new ArrayList<>();
        List<Badge> userBadgeList = userBadges.get(userId);
        
        if (userBadgeList == null) return updatedBadges;
        
        for (Badge badge : userBadgeList) {
            if (badge.isEarned()) continue;
            
            boolean updated = false;
            
            // Update based on activity type and badge category
            switch (badge.getCategory()) {
                case "Getting Started":
                    badge.incrementCurrentValue(1);
                    updated = true;
                    break;
                    
                case "Environmental Impact":
                    if (co2Savings > 0) {
                        badge.incrementCurrentValue((int) co2Savings);
                        updated = true;
                    }
                    break;
                    
                case "Transportation":
                    if ("transport".equals(activityType)) {
                        badge.incrementCurrentValue(1);
                        updated = true;
                    }
                    break;
                    
                case "Energy Conservation":
                    if ("energy".equals(activityType)) {
                        badge.incrementCurrentValue(1);
                        updated = true;
                    }
                    break;
                    
                // Add more categories as needed
            }
            
            if (updated) {
                updatedBadges.add(badge);
                
                // Check if badge is now earned
                if (badge.getProgress() >= 100) {
                    awardBadge(userId, badge);
                }
            }
        }
        
        return updatedBadges;
    }

    /**
     * Reset user's badge progress (for testing)
     */
    public void resetUserBadges(int userId) {
        List<Badge> badges = userBadges.get(userId);
        if (badges != null) {
            badges.forEach(badge -> {
                badge.setEarned(false);
                badge.setEarnedDate(null);
                badge.setProgress(0);
                badge.setCurrentValue(0);
            });
        }
    }
}