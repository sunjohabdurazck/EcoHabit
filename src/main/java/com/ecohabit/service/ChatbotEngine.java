// ChatbotEngine.java
package main.java.com.ecohabit.service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChatbotEngine {
    private final List<String> responses;
    private final Random random;
    
    public ChatbotEngine() {
        this.random = new Random();
        this.responses = Arrays.asList(
            "Great job on your eco-friendly activities! 🌱",
            "Every small action makes a big difference! 🌍",
            "You're on the right track to reducing your carbon footprint! ♻️",
            "Keep up the excellent work! 💚",
            "Your commitment to the environment is inspiring! 🌿"
        );
    }
    
    public String getRandomResponse() {
        return responses.get(random.nextInt(responses.size()));
    }
    
    public String processMessage(String userMessage) {
        // Simple keyword-based response system
        String message = userMessage.toLowerCase();
        
        if (message.contains("help") || message.contains("tip")) {
            return "Here are some eco-friendly tips: Use LED bulbs, bike instead of driving, and choose reusable products! 💡";
        } else if (message.contains("co2") || message.contains("carbon")) {
            return "Your current CO₂ savings are making a real impact! Keep tracking your activities! 📊";
        } else if (message.contains("thanks") || message.contains("thank")) {
            return "You're welcome! I'm here to help you on your eco-journey! 🌱";
        } else {
            return getRandomResponse();
        }
    }
}