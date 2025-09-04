package main.java.com.ecohabit.service;

import main.java.com.ecohabit.model.ChatMessage;
import main.java.com.ecohabit.model.User;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service class for managing chatbot interactions and responses
 */
public class ChatbotService {
    private static final int MAX_CONTEXT_MESSAGES = 10;
    private static final double MIN_CONFIDENCE_THRESHOLD = 0.3;
    
    private Map<String, List<ChatMessage>> sessionMessages;
    private Map<Integer, List<String>> userSessions;
    private Map<String, String> intentPatterns;
    private Map<String, List<String>> responseTemplates;
    private Random random;
    
    public ChatbotService() {
        this.sessionMessages = new ConcurrentHashMap<>();
        this.userSessions = new ConcurrentHashMap<>();
        this.intentPatterns = new HashMap<>();
        this.responseTemplates = new HashMap<>();
        this.random = new Random();
        
        initializeIntentPatterns();
        initializeResponseTemplates();
    }

    /**
     * Initialize intent recognition patterns
     */
    private void initializeIntentPatterns() {
        intentPatterns.put("carbon_footprint", "carbon|footprint|emissions|co2|greenhouse|climate");
        intentPatterns.put("energy_saving", "energy|electricity|power|solar|renewable|efficient");
        intentPatterns.put("transportation", "transport|car|bike|bus|walk|drive|commute|travel");
        intentPatterns.put("food_sustainability", "food|eat|meal|diet|vegetarian|organic|local");
        intentPatterns.put("waste_reduction", "waste|recycle|compost|plastic|packaging|garbage");
        intentPatterns.put("water_conservation", "water|save|conservation|shower|irrigation|leak");
        intentPatterns.put("goal_setting", "goal|target|achieve|plan|objective|aim");
        intentPatterns.put("data_analysis", "analyze|data|progress|track|statistics|report");
        intentPatterns.put("tips_request", "tip|advice|suggest|recommend|help|how");
        intentPatterns.put("greeting", "hello|hi|hey|good morning|good afternoon|good evening");
        intentPatterns.put("gratitude", "thank|thanks|appreciate|grateful");
        intentPatterns.put("farewell", "bye|goodbye|see you|farewell|exit|quit");
    }

    /**
     * Initialize response templates for different intents
     */
    private void initializeResponseTemplates() {
        // Carbon Footprint responses
        responseTemplates.put("carbon_footprint", Arrays.asList(
            "🌱 Great question about carbon footprint! Here are some effective ways to reduce it:\n\n• Use public transport or bike instead of driving\n• Switch to renewable energy sources\n• Eat more plant-based meals\n• Reduce, reuse, and recycle\n• Buy local and seasonal products\n\nWould you like specific tips for any of these areas?",
            "🌍 Your carbon footprint is the total amount of greenhouse gases you produce. To reduce it:\n\n• Energy: Use LED bulbs, unplug devices\n• Transport: Walk, bike, or use public transport\n• Food: Eat less meat, buy local produce\n• Home: Improve insulation, use efficient appliances\n\nWhat area interests you most?",
            "♻️ Reducing your carbon footprint is one of the best things you can do for the planet! Start with these high-impact changes:\n\n• Transportation (biggest impact)\n• Energy usage at home\n• Dietary choices\n• Consumption habits\n\nI can help you create a personalized plan!"
        ));

        // Energy Saving responses
        responseTemplates.put("energy_saving", Arrays.asList(
            "💡 Excellent! Here are top energy-saving tips:\n\n• Replace bulbs with LED lights (75% less energy)\n• Unplug electronics when not in use\n• Use a programmable thermostat\n• Seal air leaks around windows/doors\n• Upgrade to energy-efficient appliances\n\nThese changes can reduce your electricity bill by 20-30%!",
            "⚡ Energy conservation is key to sustainability! Try these:\n\n• Adjust thermostat by 2°F (saves 6% on bills)\n• Use cold water for washing clothes\n• Air-dry clothes instead of using dryer\n• Install solar panels if possible\n• Use power strips to eliminate phantom loads\n\nWhat's your current biggest energy expense?",
            "🏠 Smart energy use saves money and the planet:\n\n• Home heating/cooling: 42% of energy use\n• Water heating: 18% of energy use\n• Electronics: 6% of energy use\n• Lighting: 5% of energy use\n\nFocus on the biggest users first for maximum impact!"
        ));

        // Transportation responses
        responseTemplates.put("transportation", Arrays.asList(
            "🚲 Transportation is a major source of emissions. Here are eco-friendly options:\n\n• Walk or bike for trips under 2 miles\n• Use public transport (bus, train, subway)\n• Carpool or use rideshare services\n• Work from home when possible\n• Combine errands into one trip\n\nEven small changes make a big difference!",
            "🚌 Great question about sustainable transport! Consider:\n\n• Electric or hybrid vehicles\n• Public transportation (40x more efficient than cars)\n• Cycling (zero emissions + great exercise)\n• Telecommuting to reduce commute days\n• Planning routes to minimize driving\n\nWhat's your typical commute like?",
            "🚶 Every mile not driven saves about 1 pound of CO₂! Try:\n\n• Walking: Best for health and environment\n• Cycling: 5-10x more efficient than cars\n• Public transit: Reduces traffic and emissions\n• Electric vehicles: Zero direct emissions\n• Trip planning: Combine multiple errands\n\nWhich option works best for your situation?"
        ));

        // Food Sustainability responses
        responseTemplates.put("food_sustainability", Arrays.asList(
            "🌿 Food choices have huge environmental impact! Try:\n\n• Eat more plant-based meals (start with 1 day/week)\n• Buy local and seasonal produce\n• Reduce food waste through meal planning\n• Choose organic when possible\n• Start a small garden or herb windowsill\n\nEven small dietary changes help significantly!",
            "🥗 Sustainable eating benefits you and the planet:\n\n• Livestock produces 14.5% of global greenhouse gases\n• Local food reduces transport emissions\n• Organic farming protects soil and water\n• Reducing waste saves money and resources\n\nWould you like help with meal planning or recipes?",
            "🍎 Your food choices matter! Consider:\n\n• Meatless Monday (or any day)\n• Shopping at farmers markets\n• Growing your own herbs/vegetables\n• Composting food scraps\n• Choosing minimal packaging\n\nWhat aspect of sustainable eating interests you most?"
        ));

        // Tips Request responses
        responseTemplates.put("tips_request", Arrays.asList(
            "💡 I'd love to help! What specific area are you interested in:\n\n• Energy conservation\n• Sustainable transportation\n• Food and diet\n• Waste reduction\n• Water conservation\n• Home improvements\n\nOr would you like personalized recommendations based on your current activities?",
            "🌟 Here are some quick eco-wins to get started:\n\n• Switch 5 bulbs to LED\n• Take 5-minute showers\n• Use reusable bags\n• Unplug devices when not in use\n• Walk instead of drive for short trips\n\nThese are easy changes with immediate impact! Need more specific advice?",
            "🚀 Ready to make a difference? Here's how:\n\n• Start small: Pick 1-2 changes to focus on\n• Track progress: Use our activity tracker\n• Set goals: Aim for specific targets\n• Stay consistent: Small daily actions add up\n• Get support: Join our community challenges\n\nWhat area would you like to tackle first?"
        ));

        // Goal Setting responses
        responseTemplates.put("goal_setting", Arrays.asList(
            "🎯 Great decision to set sustainability goals! Here's how:\n\n• Start specific: 'Reduce car trips by 50%' vs 'drive less'\n• Make it measurable: Track your progress\n• Set realistic timelines: 30-90 day goals work well\n• Choose 1-3 goals maximum to start\n• Celebrate achievements along the way\n\nWhat area would you like to focus on first?",
            "📈 Effective goal setting drives real change! Try the SMART method:\n\n• Specific: Clear, well-defined goals\n• Measurable: Track with numbers\n• Achievable: Realistic for your lifestyle\n• Relevant: Aligned with your values\n• Time-bound: Set deadlines\n\nExample: 'Reduce energy bill by 20% in 3 months'\n\nWhat's your main environmental concern?",
            "🌟 Let's create your sustainability action plan:\n\n• Assess current habits\n• Identify improvement areas\n• Set 2-3 specific goals\n• Choose tracking methods\n• Plan rewards for milestones\n\nI can help you set goals for energy, transport, food, or waste. What interests you most?"
        ));

        // Data Analysis responses
        responseTemplates.put("data_analysis", Arrays.asList(
            "📊 I'd be happy to analyze your environmental impact! Here's what I can track:\n\n• Carbon footprint by category\n• Energy usage trends\n• Transportation patterns\n• Waste reduction progress\n• Water conservation metrics\n• Goal achievement rates\n\nWhat specific data would you like me to analyze?",
            "📈 Your sustainability journey deserves tracking! I can provide:\n\n• Weekly/monthly progress reports\n• Comparison with eco-friendly benchmarks\n• Identification of your biggest impact areas\n• Trend analysis over time\n• Achievement celebrations\n\nWould you like a comprehensive report or focus on specific areas?",
            "🔍 Let me help you understand your environmental data:\n\n• Current performance vs. goals\n• Areas of greatest improvement\n• Cost savings from eco-actions\n• Environmental impact calculations\n• Recommendations for next steps\n\nShare what specific insights you're looking for!"
        ));

        // Greeting responses
        responseTemplates.put("greeting", Arrays.asList(
            "👋 Hello! I'm your personal eco-assistant. I'm here to help you live more sustainably. How can I assist you today?",
            "🌱 Hi there! Great to see you're interested in sustainable living. I can help with eco-tips, goal setting, data analysis, and more. What would you like to explore?",
            "🌍 Welcome! I'm excited to help you on your sustainability journey. Whether you need advice, want to set goals, or track progress, I'm here for you. What's on your mind?"
        ));

        // Gratitude responses
        responseTemplates.put("gratitude", Arrays.asList(
            "😊 You're very welcome! I'm always happy to help with your sustainability journey. Is there anything else you'd like to know?",
            "🌟 My pleasure! Every step toward sustainability makes a difference. Feel free to ask me anything else!",
            "💚 Glad I could help! Keep up the great work on your eco-friendly lifestyle. What else can I assist you with?"
        ));

        // Farewell responses
        responseTemplates.put("farewell", Arrays.asList(
            "👋 Goodbye! Keep up the great work on your sustainability journey. See you next time!",
            "🌱 Take care! Remember, every small action makes a difference. Looking forward to our next chat!",
            "🌍 Farewell! Thanks for choosing to make a positive environmental impact. Have a wonderful day!"
        ));

        // Default responses
        responseTemplates.put("default", Arrays.asList(
            "🤔 That's interesting! While I specialize in environmental topics, I'd love to help you with:\n\n• Carbon footprint reduction\n• Energy conservation tips\n• Sustainable transportation\n• Eco-friendly food choices\n• Waste reduction strategies\n• Goal setting and tracking\n\nWhat eco-topic interests you most?",
            "🌍 I'm focused on helping you live more sustainably! I can assist with:\n\n• Personalized eco-advice\n• Environmental impact analysis\n• Activity recommendations\n• Progress tracking\n• Goal achievement support\n\nHow can I help you be more eco-friendly today?",
            "💡 I'm your sustainability companion! Let me help you with:\n\n• Reducing your environmental impact\n• Saving energy and money\n• Making eco-friendly choices\n• Tracking your progress\n• Setting and achieving green goals\n\nWhat aspect of sustainable living would you like to explore?"
        ));
    }

    /**
     * Generate a response to user input
     */
    public String generateResponse(String userMessage, User user, List<ChatMessage> chatHistory) {
        try {
            // Analyze user intent
            String intent = detectIntent(userMessage);
            
            // Generate contextual response
            String response = generateContextualResponse(intent, userMessage, user, chatHistory);
            
            return response;
            
        } catch (Exception e) {
            System.err.println("Error generating response: " + e.getMessage());
            return "I apologize, but I'm having trouble processing your request right now. Could you please try rephrasing your question about sustainability or eco-living?";
        }
    }

    /**
     * Detect user intent from message
     */
    private String detectIntent(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "default";
        }
        
        String lowerMessage = message.toLowerCase();
        
        // Check each intent pattern
        for (Map.Entry<String, String> entry : intentPatterns.entrySet()) {
            String intent = entry.getKey();
            String pattern = entry.getValue();
            String[] keywords = pattern.split("\\|");
            
            for (String keyword : keywords) {
                if (lowerMessage.contains(keyword.trim())) {
                    return intent;
                }
            }
        }
        
        return "default";
    }

    /**
     * Generate contextual response based on intent and context
     */
    private String generateContextualResponse(String intent, String userMessage, User user, List<ChatMessage> chatHistory) {
        // Get response templates for the intent
        List<String> templates = responseTemplates.getOrDefault(intent, responseTemplates.get("default"));
        
        // Select response based on context
        String baseResponse = selectResponseTemplate(templates, chatHistory);
        
        // Personalize response if user information is available
        if (user != null) {
            baseResponse = personalizeResponse(baseResponse, user, userMessage);
        }
        
        return baseResponse;
    }

    /**
     * Select appropriate response template based on context
     */
    private String selectResponseTemplate(List<String> templates, List<ChatMessage> chatHistory) {
        // Simple random selection (could be enhanced with ML)
        return templates.get(random.nextInt(templates.size()));
    }

    /**
     * Personalize response based on user data
     */
    private String personalizeResponse(String baseResponse, User user, String userMessage) {
        // Simple personalization - could be enhanced with user preferences/history
        if (user.getFirstName() != null && random.nextDouble() < 0.3) {
            // Occasionally use user's name
            String[] greetings = {
                user.getFirstName() + ", " + baseResponse,
                baseResponse + "\n\nHope this helps, " + user.getFirstName() + "!",
                "Hi " + user.getFirstName() + "! " + baseResponse
            };
            return greetings[random.nextInt(greetings.length)];
        }
        
        return baseResponse;
    }

    /**
     * Save chat message to session
     */
    public void saveChatMessage(String sessionId, ChatMessage message) {
        sessionMessages.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(message);
        
        // Limit session size to prevent memory issues
        List<ChatMessage> messages = sessionMessages.get(sessionId);
        if (messages.size() > 1000) { // Keep last 1000 messages
            messages.subList(0, messages.size() - 1000).clear();
        }
    }

    /**
     * Get chat history for a session
     */
    public List<ChatMessage> getChatHistory(String sessionId) {
        return sessionMessages.getOrDefault(sessionId, new ArrayList<>());
    }

    /**
     * Get user's chat sessions
     */
    public List<String> getUserChatSessions(int userId) {
        return userSessions.getOrDefault(userId, new ArrayList<>());
    }

    /**
     * Create new chat session for user
     */
    public String createChatSession(int userId) {
        String sessionId = "session_" + userId + "_" + System.currentTimeMillis();
        userSessions.computeIfAbsent(userId, k -> new ArrayList<>()).add(sessionId);
        return sessionId;
    }

    /**
     * Export chat history to file
     */
    public String exportChatHistory(String sessionId) throws IOException {
        List<ChatMessage> messages = getChatHistory(sessionId);
        if (messages.isEmpty()) {
            throw new IOException("No messages found for session: " + sessionId);
        }
        
        String filename = "chat_export_" + sessionId + "_" + 
                         LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
        
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("EcoHabit Chat Export\n");
            writer.write("Session: " + sessionId + "\n");
            writer.write("Exported: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("Total Messages: " + messages.size() + "\n");
            writer.write("\n" + "=".repeat(50) + "\n\n");
            
            for (ChatMessage message : messages) {
                writer.write(String.format("[%s] %s:\n%s\n\n", 
                    message.getFormattedTimestamp(),
                    message.getSender(),
                    message.getContent()
                ));
            }
        }
        
        return filename;
    }

    /**
     * Get conversation summary
     */
    public String getConversationSummary(String sessionId) {
        List<ChatMessage> messages = getChatHistory(sessionId);
        if (messages.isEmpty()) {
            return "No conversation history available.";
        }
        
        // Simple summary generation
        Map<String, Long> intentCounts = messages.stream()
                .filter(m -> m.getIntent() != null)
                .collect(Collectors.groupingBy(ChatMessage::getIntent, Collectors.counting()));
        
        StringBuilder summary = new StringBuilder();
        summary.append("Conversation Summary:\n");
        summary.append("Total messages: ").append(messages.size()).append("\n");
        summary.append("Duration: ").append(calculateConversationDuration(messages)).append("\n");
        
        if (!intentCounts.isEmpty()) {
            summary.append("\nMain topics discussed:\n");
            intentCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(5)
                    .forEach(entry -> summary.append("• ").append(formatIntent(entry.getKey()))
                            .append(" (").append(entry.getValue()).append(" times)\n"));
        }
        
        return summary.toString();
    }

    /**
     * Calculate conversation duration
     */
    private String calculateConversationDuration(List<ChatMessage> messages) {
        if (messages.size() < 2) return "Less than a minute";
        
        LocalDateTime start = messages.get(0).getTimestamp();
        LocalDateTime end = messages.get(messages.size() - 1).getTimestamp();
        
        long minutes = java.time.Duration.between(start, end).toMinutes();
        
        if (minutes < 60) {
            return minutes + " minutes";
        } else {
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            return hours + " hours " + remainingMinutes + " minutes";
        }
    }

    /**
     * Format intent for display
     */
    private String formatIntent(String intent) {
        return intent.replace("_", " ")
                    .substring(0, 1).toUpperCase() + intent.replace("_", " ").substring(1);
    }

    /**
     * Get suggested responses based on context
     */
    public List<String> getSuggestedResponses(String sessionId) {
        List<ChatMessage> recentMessages = getChatHistory(sessionId).stream()
                .sorted(Comparator.comparing(ChatMessage::getTimestamp).reversed())
                .limit(5)
                .collect(Collectors.toList());
        
        if (recentMessages.isEmpty()) {
            return Arrays.asList(
                "How can I reduce my carbon footprint?",
                "Give me some energy saving tips",
                "What are sustainable transportation options?",
                "Help me set an eco-friendly goal"
            );
        }
        
        // Generate context-aware suggestions
        List<String> suggestions = new ArrayList<>();
        
        String lastIntent = recentMessages.get(0).getIntent();
        if ("carbon_footprint".equals(lastIntent)) {
            suggestions.addAll(Arrays.asList(
                "How can I track my carbon savings?",
                "What's the biggest impact I can make?",
                "Give me a carbon reduction action plan"
            ));
        } else if ("energy_saving".equals(lastIntent)) {
            suggestions.addAll(Arrays.asList(
                "How much money can I save?",
                "What about solar panels?",
                "Help me create an energy audit"
            ));
        } else {
            suggestions.addAll(Arrays.asList(
                "What else can I do?",
                "How do I track my progress?",
                "Set a goal for me"
            ));
        }
        
        return suggestions.stream().limit(3).collect(Collectors.toList());
    }

    /**
     * Analyze user message for entities and context
     */
    public void analyzeMessage(ChatMessage message) {
        if (message == null || message.getContent() == null) return;
        
        String content = message.getContent().toLowerCase();
        
        // Detect intent
        String intent = detectIntent(content);
        message.setIntent(intent);
        
        // Extract entities (simple keyword extraction)
        List<String> entities = extractEntities(content);
        if (!entities.isEmpty()) {
            message.setEntities(entities.toArray(new String[0]));
        }
        
        // Set confidence based on intent match strength
        double confidence = calculateConfidence(content, intent);
        message.setConfidence(confidence);
        
        // Categorize message
        String category = categorizeMessage(intent, content);
        message.setMessageCategory(category);
    }

    /**
     * Extract entities from message content
     */
    private List<String> extractEntities(String content) {
        List<String> entities = new ArrayList<>();
        
        // Environmental entities
        String[] envEntities = {"energy", "carbon", "co2", "solar", "wind", "renewable", 
                               "electric", "hybrid", "organic", "local", "sustainable",
                               "recycle", "compost", "waste", "water", "conservation"};
        
        for (String entity : envEntities) {
            if (content.contains(entity)) {
                entities.add(entity);
            }
        }
        
        // Numbers (for quantities, percentages, etc.)
        java.util.regex.Pattern numberPattern = java.util.regex.Pattern.compile("\\b\\d+(%|kg|kwh|miles|gallons|dollars)?\\b");
        java.util.regex.Matcher matcher = numberPattern.matcher(content);
        while (matcher.find()) {
            entities.add(matcher.group());
        }
        
        return entities;
    }

    /**
     * Calculate confidence score for intent detection
     */
    private double calculateConfidence(String content, String intent) {
        if ("default".equals(intent)) {
            return 0.5; // Medium confidence for default responses
        }
        
        String pattern = intentPatterns.get(intent);
        if (pattern == null) return 0.3;
        
        String[] keywords = pattern.split("\\|");
        int matches = 0;
        
        for (String keyword : keywords) {
            if (content.contains(keyword.trim())) {
                matches++;
            }
        }
        
        // Calculate confidence based on keyword matches
        double confidence = Math.min(0.3 + (matches * 0.2), 1.0);
        return confidence;
    }

    /**
     * Categorize message based on intent and content
     */
    private String categorizeMessage(String intent, String content) {
        if (content.contains("?")) {
            return "question";
        } else if (intent.equals("tips_request")) {
            return "tip";
        } else if (intent.equals("data_analysis")) {
            return "analysis";
        } else {
            return "general";
        }
    }

    /**
     * Get chatbot statistics
     */
    public Map<String, Object> getChatbotStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        int totalSessions = sessionMessages.size();
        int totalMessages = sessionMessages.values().stream()
                .mapToInt(List::size)
                .sum();
        
        // Calculate average messages per session
        double avgMessagesPerSession = totalSessions > 0 ? (double) totalMessages / totalSessions : 0;
        
        // Intent distribution
        Map<String, Long> intentDistribution = sessionMessages.values().stream()
                .flatMap(List::stream)
                .filter(m -> m.getIntent() != null)
                .collect(Collectors.groupingBy(ChatMessage::getIntent, Collectors.counting()));
        
        // Active sessions (had activity in last 24 hours)
        long activeSessions = sessionMessages.values().stream()
                .filter(messages -> !messages.isEmpty())
                .filter(messages -> {
                    ChatMessage lastMessage = messages.get(messages.size() - 1);
                    return lastMessage.getTimestamp().isAfter(LocalDateTime.now().minusDays(1));
                })
                .count();
        
        stats.put("totalSessions", totalSessions);
        stats.put("totalMessages", totalMessages);
        stats.put("averageMessagesPerSession", Math.round(avgMessagesPerSession * 100.0) / 100.0);
        stats.put("activeSessions", activeSessions);
        stats.put("intentDistribution", intentDistribution);
        
        return stats;
    }

    /**
     * Clear old chat sessions to manage memory
     */
    public void cleanupOldSessions(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        
        sessionMessages.entrySet().removeIf(entry -> {
            List<ChatMessage> messages = entry.getValue();
            if (messages.isEmpty()) return true;
            
            // Remove session if last message is older than cutoff
            ChatMessage lastMessage = messages.get(messages.size() - 1);
            return lastMessage.getTimestamp().isBefore(cutoffDate);
        });
        
        // Also clean up user sessions list
        userSessions.values().forEach(sessionList -> {
            sessionList.removeIf(sessionId -> !sessionMessages.containsKey(sessionId));
        });
    }

    /**
     * Get popular conversation topics
     */
    public List<Map.Entry<String, Long>> getPopularTopics(int limit) {
        return sessionMessages.values().stream()
                .flatMap(List::stream)
                .filter(m -> m.getIntent() != null && !"default".equals(m.getIntent()))
                .collect(Collectors.groupingBy(ChatMessage::getIntent, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get conversation insights for a user
     */
    public Map<String, Object> getUserConversationInsights(int userId) {
        List<String> userSessionIds = getUserChatSessions(userId);
        
        List<ChatMessage> allUserMessages = userSessionIds.stream()
                .flatMap(sessionId -> getChatHistory(sessionId).stream())
                .collect(Collectors.toList());
        
        Map<String, Object> insights = new HashMap<>();
        
        insights.put("totalMessages", allUserMessages.size());
        insights.put("totalSessions", userSessionIds.size());
        
        if (!allUserMessages.isEmpty()) {
            // Most active topics
            Map<String, Long> topicFrequency = allUserMessages.stream()
                    .filter(m -> m.getIntent() != null)
                    .collect(Collectors.groupingBy(ChatMessage::getIntent, Collectors.counting()));
            
            String mostDiscussedTopic = topicFrequency.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(entry -> formatIntent(entry.getKey()))
                    .orElse("General conversation");
            
            // Activity pattern
            LocalDateTime firstMessage = allUserMessages.stream()
                    .min(Comparator.comparing(ChatMessage::getTimestamp))
                    .map(ChatMessage::getTimestamp)
                    .orElse(LocalDateTime.now());
            
            LocalDateTime lastMessage = allUserMessages.stream()
                    .max(Comparator.comparing(ChatMessage::getTimestamp))
                    .map(ChatMessage::getTimestamp)
                    .orElse(LocalDateTime.now());
            
            insights.put("mostDiscussedTopic", mostDiscussedTopic);
            insights.put("firstInteraction", firstMessage);
            insights.put("lastInteraction", lastMessage);
            insights.put("topicDistribution", topicFrequency);
        }
        
        return insights;
    }

    /**
     * Generate smart reply suggestions based on conversation context
     */
    public List<String> generateSmartReplies(String sessionId, String lastUserMessage) {
        List<String> smartReplies = new ArrayList<>();
        
        String intent = detectIntent(lastUserMessage);
        
        switch (intent) {
            case "carbon_footprint":
                smartReplies.addAll(Arrays.asList(
                    "Show me my carbon footprint data",
                    "What's my biggest emission source?",
                    "Create a carbon reduction plan"
                ));
                break;
            case "energy_saving":
                smartReplies.addAll(Arrays.asList(
                    "Calculate my potential savings",
                    "What's the easiest energy tip to start?",
                    "Help me do an energy audit"
                ));
                break;
            case "transportation":
                smartReplies.addAll(Arrays.asList(
                    "Find eco-friendly routes near me",
                    "Calculate transport emissions",
                    "Suggest car alternatives"
                ));
                break;
            case "food_sustainability":
                smartReplies.addAll(Arrays.asList(
                    "Plan sustainable meals",
                    "Find local farmers markets",
                    "Calculate food impact"
                ));
                break;
            default:
                smartReplies.addAll(Arrays.asList(
                    "Tell me more about this",
                    "How do I get started?",
                    "What's the biggest impact I can make?"
                ));
        }
        
        return smartReplies.stream().limit(3).collect(Collectors.toList());
    }

    /**
     * Process and validate user input
     */
    public boolean isValidInput(String input) {
        if (input == null) return false;
        
        String trimmed = input.trim();
        
        // Check for empty input
        if (trimmed.isEmpty()) return false;
        
        // Check for excessively long input
        if (trimmed.length() > 2000) return false;
        
        // Check for spam patterns (repeated characters)
        if (trimmed.matches(".*(..)\\1{10,}.*")) return false;
        
        return true;
    }

    /**
     * Generate welcome message for new users
     */
    public String generateWelcomeMessage(User user) {
        String name = (user != null && user.getFirstName() != null) ? user.getFirstName() : "there";
        
        return String.format(
            "👋 Hi %s! I'm your personal eco-assistant. I can help you with:\n\n" +
            "• Carbon footprint reduction tips\n" +
            "• Sustainable activity suggestions\n" +
            "• Environmental impact analysis\n" +
            "• Goal setting and tracking\n" +
            "• General eco-living advice\n\n" +
            "How can I help you today?", name
        );
    }
}