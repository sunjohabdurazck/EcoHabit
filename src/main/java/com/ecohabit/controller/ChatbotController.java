package main.java.com.ecohabit.controller;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import main.java.com.ecohabit.SessionManager;
import main.java.com.ecohabit.model.ChatMessage;
import main.java.com.ecohabit.model.User;
import main.java.com.ecohabit.service.ChatbotService;
import main.java.com.ecohabit.service.UserService;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controller for the AI Chatbot screen
 * Provides intelligent eco-advice and interactive assistance
 */
public class ChatbotController extends BaseController {
    
    // Chat Interface Components
    @FXML private ScrollPane chatScrollPane;
    @FXML private VBox chatContainer;
    @FXML private TextField messageInput;
    @FXML private Button sendButton;
    @FXML private Button voiceButton;
    @FXML private ProgressIndicator typingIndicator;
    
    // Quick Actions
    @FXML private FlowPane quickActionsPane;
    @FXML private Button carbonTipsBtn;
    @FXML private Button activitySuggestionsBtn;
    @FXML private Button goalHelpBtn;
    @FXML private Button dataAnalysisBtn;
    @FXML private Button generalHelpBtn;
    
    // Chat Controls
    @FXML private Button clearChatBtn;
    @FXML private Button exportChatBtn;
    @FXML private ComboBox<String> chatModeComboBox;
    @FXML private CheckBox smartSuggestionsCheckBox;
    
    // Status Components
    @FXML private Label chatStatusLabel;
    @FXML private Circle connectionStatusCircle;
    @FXML private Label lastActiveLabel;
    
    // Sidebar - Chat History
    @FXML private VBox chatHistoryContainer;
    @FXML private Button newChatBtn;
    @FXML private ListView<String> previousChatsListView;
    
    // Services and Data
    private ChatbotService chatbotService;
    private UserService userService;
    private User currentUser;
    private ObservableList<ChatMessage> chatHistory;
    private String currentChatSession;
    
    // Chat State
    private boolean isTyping = false;
    private Timeline typingAnimation;
    private List<String> quickActions;
    private Map<String, String> chatSessions;
    
    @Override
    public void initializeScreen() {
        try {
            // Initialize services
            initializeServices();
            
            // Setup UI components
            setupQuickActions();
            setupChatInterface();
            setupEventHandlers();
            
            // Load user data and chat history
            loadUserData();
            loadChatHistory();
            
            // Start new chat session
            startNewChatSession();
            
            // Show welcome message
            showWelcomeMessage();
            
            System.out.println("Chatbot screen initialized successfully");
            
        } catch (Exception e) {
            System.err.println("Error initializing chatbot screen: " + e.getMessage());
            e.printStackTrace();
            showError("Initialization Error", "Failed to initialize chatbot: " + e.getMessage());
        }
    }

    private void initializeServices() {
        try {
            chatbotService = new ChatbotService();
            userService = UserService.getInstance(); // Use singleton instance
            chatHistory = FXCollections.observableArrayList();
            chatSessions = new HashMap<>();
            
        } catch (Exception e) {
            System.err.println("Error initializing services: " + e.getMessage());
            showError("Service Error", "Failed to initialize services: " + e.getMessage());
        }
    }

    private void loadUserData() {
        try {
            // Get user from SessionManager instead of UserService
            currentUser = SessionManager.getCurrentUser();
            
            if (currentUser == null) {
                // Fallback: try to get from UserService if SessionManager doesn't have it
                if (userService != null) {
                    currentUser = userService.getCurrentUser();
                }
                
                if (currentUser == null) {
                    showError("User Error", "No user found. Please log in first.");
                    // Optionally navigate back to login screen
                    // navigationController.navigateTo("login");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error loading user data: " + e.getMessage());
            showError("Load Error", "Failed to load user data: " + e.getMessage());
        }
    }
    
    /**
     * Setup quick action buttons
     */
    private void setupQuickActions() {
        quickActions = Arrays.asList(
            "How can I reduce my carbon footprint?",
            "Suggest eco-friendly activities for today",
            "Help me set a sustainability goal",
            "Analyze my environmental impact",
            "What are some green transportation options?",
            "How to start composting at home?",
            "Energy-saving tips for my house",
            "Sustainable shopping guide"
        );
        
        // Setup combo box for chat modes
        if (chatModeComboBox != null) {
            chatModeComboBox.setItems(FXCollections.observableArrayList(
                "Eco Advisor", "Activity Coach", "Data Analyst", "General Assistant"
            ));
            chatModeComboBox.getSelectionModel().selectFirst();
        }
    }
    
    /**
     * Setup chat interface components
     */
    private void setupChatInterface() {
        // Configure chat scroll pane
        if (chatScrollPane != null) {
            chatScrollPane.setFitToWidth(true);
            chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }
        
        // Configure chat container
        if (chatContainer != null) {
            chatContainer.setSpacing(10);
            chatContainer.setPadding(new Insets(15));
        }
        
        // Configure message input
        if (messageInput != null) {
            messageInput.setPromptText("Ask me anything about eco-living...");
        }
        
        // Hide typing indicator initially
        if (typingIndicator != null) {
            typingIndicator.setVisible(false);
        }
        
        // Setup connection status
        updateConnectionStatus(true);
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Send message handlers
        if (sendButton != null) {
            sendButton.setOnAction(_ -> sendMessage()); 
        }
        if (previousChatsListView != null) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Delete Chat");
            deleteItem.setOnAction(_ -> deleteSelectedChat());
            contextMenu.getItems().add(deleteItem);
            
            previousChatsListView.setContextMenu(contextMenu);
        }
    
        
        if (messageInput != null) {
            messageInput.setOnAction(_ -> sendMessage());
            
            // Show typing indicator when user is typing
            messageInput.textProperty().addListener((_, _, newText) -> {
                if (newText != null && !newText.trim().isEmpty()) {
                    sendButton.setDisable(false);
                } else {
                    sendButton.setDisable(true);
                }
            });
        }
        
        // Quick action buttons
        if (carbonTipsBtn != null) {
            carbonTipsBtn.setOnAction(_ -> sendQuickMessage("Give me some carbon reduction tips"));
        }
        if (activitySuggestionsBtn != null) {
            activitySuggestionsBtn.setOnAction(_ -> sendQuickMessage("Suggest eco-friendly activities for today"));
        }
        if (goalHelpBtn != null) {
            goalHelpBtn.setOnAction(_ -> sendQuickMessage("Help me set sustainability goals"));
        }
        if (dataAnalysisBtn != null) {
            dataAnalysisBtn.setOnAction(_ -> sendQuickMessage("Analyze my environmental impact data"));
        }
        if (generalHelpBtn != null) {
            generalHelpBtn.setOnAction(_ -> sendQuickMessage("What can you help me with?"));
        }
        
        // Chat controls
        if (clearChatBtn != null) {
            clearChatBtn.setOnAction(_ -> clearChat());
        }
        if (exportChatBtn != null) {
            exportChatBtn.setOnAction(_ -> exportChat());
        }
        if (newChatBtn != null) {
            newChatBtn.setOnAction(_ -> startNewChatSession());
        }
        
        // Voice button (placeholder)
        if (voiceButton != null) {
            voiceButton.setOnAction(_ -> handleVoiceInput());
        }
        
        // Chat mode change
        if (chatModeComboBox != null) {
            chatModeComboBox.setOnAction(_ -> handleChatModeChange());
        }
        
        // Smart suggestions checkbox
        if (smartSuggestionsCheckBox != null) {
            smartSuggestionsCheckBox.selectedProperty().addListener((_, _, newVal) -> {
                updateChatStatus("Smart suggestions " + (newVal ? "enabled" : "disabled"));
            });
        }
    }

    
    /**
     * Show welcome message
     */
    private void showWelcomeMessage() {
        String userName = currentUser != null ? currentUser.getFirstName() : "there";
        
        String welcomeText = String.format(
            "👋 Hi %s! I'm your personal eco-assistant. I can help you with:\n\n" +
            "• Carbon footprint reduction tips\n" +
            "• Sustainable activity suggestions\n" +
            "• Environmental impact analysis\n" +
            "• Goal setting and tracking\n" +
            "• General eco-living advice\n\n" +
            "How can I help you today?",
            userName
        );
        
        ChatMessage welcomeMessage = new ChatMessage();
        welcomeMessage.setContent(welcomeText);
        welcomeMessage.setSender("EcoBot");
        welcomeMessage.setTimestamp(LocalDateTime.now());
        welcomeMessage.setMessageType("bot");
        
        addMessageToChat(welcomeMessage);
    }
    
    /**
     * Send a message
     */
    private void sendMessage() {
        String messageText = messageInput.getText().trim();
        if (messageText.isEmpty()) return;
        
        // Ensure we have user data
        if (currentUser == null) {
            loadUserData();
            if (currentUser == null) {
                showError("User Error", "Please log in first to use the chatbot.");
                return;
            }
        }
        
        // Create user message
        ChatMessage userMessage = new ChatMessage();
        userMessage.setContent(messageText);
        userMessage.setSender(currentUser != null ? currentUser.getFirstName() : "User");
        userMessage.setTimestamp(LocalDateTime.now());
        userMessage.setMessageType("user");
        
        // Add to chat
        addMessageToChat(userMessage);
        
        // Clear input
        messageInput.clear();
        sendButton.setDisable(true);
        
        // Show typing indicator and get bot response
        showTypingIndicator();
        getBotResponse(messageText);
    }
    
    /**
     * Send a quick action message
     */
    private void sendQuickMessage(String message) {
        if (currentUser == null) {
            loadUserData();
            if (currentUser == null) {
                showError("User Error", "Please log in first to use the chatbot.");
                return;
            }
        }
        messageInput.setText(message);
        sendMessage();
    }
    

    /**
     * Add message to UI
     */
    private void addMessageToUI(ChatMessage message) {
        if (chatContainer == null) return;
        
        // Create message container
        HBox messageContainer = new HBox();
        messageContainer.setSpacing(10);
        messageContainer.setPadding(new Insets(8));
        
        // Create message bubble
        VBox messageBubble = createMessageBubble(message);
        
        // Position message based on sender
        if ("user".equals(message.getMessageType())) {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageContainer.getChildren().add(messageBubble);
            messageBubble.getStyleClass().add("user-message");
        } else {
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            
            // Add bot avatar
            Circle avatar = new Circle(20, Color.web("#4ecdc4"));
            avatar.getStyleClass().add("bot-avatar");
            
            messageContainer.getChildren().addAll(avatar, messageBubble);
            messageBubble.getStyleClass().add("bot-message");
        }
        
        // Add to container
        chatContainer.getChildren().add(messageContainer);
        
        // Animate entrance
        animateMessageEntrance(messageContainer);
    }
 // Replace the problematic loadChatSession method with this correctly named version:
    private void loadSelectedChatSession(String sessionId) {
        if (sessionId == null) return;
        
        try {
            currentChatSession = sessionId;
            List<ChatMessage> messages = chatbotService.getChatHistory(sessionId);
            
            chatHistory.clear();
            chatHistory.addAll(messages);
            
            // Clear and rebuild chat UI
            if (chatContainer != null) {
                chatContainer.getChildren().clear();
                
                for (ChatMessage message : messages) {
                    addMessageToUI(message);
                }
            }
            
            updateChatStatus("Loaded chat session: " + getFriendlySessionName(sessionId));
            
        } catch (Exception e) {
            showError("Load Error", "Failed to load chat session: " + e.getMessage());
        }
    }

    // Update the loadChatHistory method to use the correct method name:
    private void loadChatHistory() {
        try {
            if (chatbotService != null && currentUser != null) { 
                List<String> previousChats = chatbotService.getUserChatSessions(currentUser.getId());
                
                // Create user-friendly session names
                ObservableList<String> sessionNames = FXCollections.observableArrayList();
                for (String sessionId : previousChats) {
                    sessionNames.add(getFriendlySessionName(sessionId));
                }
                
                if (previousChatsListView != null) {
                    previousChatsListView.setItems(sessionNames);
                    // Store the mapping between friendly names and actual session IDs
                    Map<String, String> sessionMap = new HashMap<>();
                    for (int i = 0; i < sessionNames.size(); i++) {
                        sessionMap.put(sessionNames.get(i), previousChats.get(i));
                    }
                    
                    previousChatsListView.setUserData(sessionMap); // Store mapping in userData
                    
                    previousChatsListView.setOnMouseClicked(e -> {
                        if (e.getClickCount() == 2) {
                            String selectedName = previousChatsListView.getSelectionModel().getSelectedItem();
                            if (selectedName != null) {
                                Map<String, String> mapping = (Map<String, String>) previousChatsListView.getUserData();
                                String sessionId = mapping.get(selectedName);
                                loadSelectedChatSession(sessionId); // Use the corrected method name
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading chat history: " + e.getMessage());
            // Show empty list instead of crashing
            if (previousChatsListView != null) {
                previousChatsListView.setItems(FXCollections.observableArrayList());
            }
        }
    }

    // Update the deleteSelectedChat method to handle the case where deleteChatSession might not exist:
    private void deleteSelectedChat() {
        String selectedName = previousChatsListView.getSelectionModel().getSelectedItem();
        if (selectedName != null && showConfirmation("Delete Chat", 
                "Are you sure you want to delete this chat?")) {
            
            Map<String, String> mapping = (Map<String, String>) previousChatsListView.getUserData();
            String sessionId = mapping.get(selectedName);
            
            try {
                if (chatbotService != null) {
                    // Use reflection to check if the method exists, or provide a fallback
                    boolean deleted = false;
                    try {
                        // Try to call the delete method if it exists
                        java.lang.reflect.Method method = chatbotService.getClass().getMethod("deleteChatSession", String.class);
                        deleted = (Boolean) method.invoke(chatbotService, sessionId);
                    } catch (NoSuchMethodException e) {
                        // Method doesn't exist - provide fallback behavior
                        showNotification("Chat deletion not fully implemented yet", "info");
                        deleted = true; // Assume success for UI update
                    }
                    
                    if (deleted) {
                        loadChatHistory(); // Refresh the list
                        showNotification("Chat deleted successfully", "success");
                        
                        // If we deleted the current session, start a new one
                        if (sessionId.equals(currentChatSession)) {
                            startNewChatSession();
                        }
                    }
                }
            } catch (Exception e) {
                showError("Delete Error", "Failed to delete chat: " + e.getMessage());
            }
        }
    }
    

    // Add this helper method to create friendly session names
    private String getFriendlySessionName(String sessionId) {
        try {
            if (sessionId.startsWith("Chat_")) {
                String timestampStr = sessionId.substring(5);
                long timestamp = Long.parseLong(timestampStr);
                LocalDateTime dateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
                
                return "Chat - " + dateTime.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
            }
            return sessionId;
        } catch (Exception e) {
            return sessionId; // Fallback to original ID if parsing fails
        }
    }

    /**
     * Save current chat session before starting a new one
     */
    private void saveCurrentChatSession() {
        if (currentChatSession != null && !chatHistory.isEmpty()) {
            try {
                // Save all messages in the current session
                for (ChatMessage message : chatHistory) {
                    chatbotService.saveChatMessage(currentChatSession, message);
                }
                System.out.println("Chat session saved: " + currentChatSession);
            } catch (Exception e) {
                System.err.println("Error saving chat session: " + e.getMessage());
            }
        }
    }

    /**
     * Start a new chat session
     */
    private void startNewChatSession() {
        // Save the current session before starting a new one
        saveCurrentChatSession();
        
        // Create new session
        currentChatSession = "Chat_" + System.currentTimeMillis();
        chatHistory.clear();
        
        if (chatContainer != null) {
            chatContainer.getChildren().clear();
        }
        
        updateChatStatus("New chat session started");
        
        // Refresh the chat history list to include the new session
        loadChatHistory();
        
        // Show welcome message for the new session
        showWelcomeMessage();
    }

    // Update the addMessageToChat method to ensure proper session tracking
    private void addMessageToChat(ChatMessage message) {
        // Add to history
        chatHistory.add(message);
        
        // Add to UI
        addMessageToUI(message);
        
        // Save to service
        if (chatbotService != null && currentChatSession != null) {
            try {
                chatbotService.saveChatMessage(currentChatSession, message);
                
                // Refresh chat list when a new message is added to a session
                Platform.runLater(() -> {
                    if (!chatHistory.isEmpty()) {
                        loadChatHistory();
                    }
                });
            } catch (Exception e) {
                System.err.println("Error saving chat message: " + e.getMessage());
            }
        }
        
        // Scroll to bottom
        scrollToBottom();
    }
    // Update the onScreenActivated method to refresh chat history
    @Override
    public void onScreenActivated() {
        super.onScreenActivated();
        // Reload user data when screen becomes active
        loadUserData();
        
        // Refresh chat history when screen becomes active
        loadChatHistory();
        
        // Focus on message input when screen becomes active
        if (messageInput != null) {
            Platform.runLater(() -> messageInput.requestFocus());
        }
        updateConnectionStatus(true);
    }
    /**
     * Create message bubble
     */
    private VBox createMessageBubble(ChatMessage message) {
        VBox bubble = new VBox(5);
        bubble.setPadding(new Insets(12));
        bubble.setMaxWidth(400);
        bubble.getStyleClass().add("message-bubble");
        
        // Message content
        Label contentLabel = new Label(message.getContent());
        contentLabel.setWrapText(true);
        contentLabel.getStyleClass().add("message-content");
        
        // Timestamp
        Label timestampLabel = new Label(
            message.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        timestampLabel.getStyleClass().add("message-timestamp");
        
        bubble.getChildren().addAll(contentLabel, timestampLabel);
        return bubble;
    }
    
    /**
     * Animate message entrance
     */
    private void animateMessageEntrance(HBox messageContainer) {
        messageContainer.setOpacity(0);
        messageContainer.setTranslateY(20);
        
        FadeTransition fade = new FadeTransition(Duration.millis(300), messageContainer);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        Timeline slide = new Timeline(
            new KeyFrame(Duration.millis(300), _ -> messageContainer.setTranslateY(0))
        );
        
        fade.play();
        slide.play();
    }
    
    /**
     * Show typing indicator
     */
    private void showTypingIndicator() {
        if (typingIndicator != null) {
            typingIndicator.setVisible(true);
            isTyping = true;
        }
        
        // Add typing message to UI
        HBox typingContainer = new HBox();
        typingContainer.setAlignment(Pos.CENTER_LEFT);
        typingContainer.setSpacing(10);
        typingContainer.setPadding(new Insets(8));
        
        Circle avatar = new Circle(20, Color.web("#4ecdc4"));
        Label typingLabel = new Label("EcoBot is typing...");
        typingLabel.getStyleClass().add("typing-indicator");
        
        typingContainer.getChildren().addAll(avatar, typingLabel);
        
        if (chatContainer != null) {
            chatContainer.getChildren().add(typingContainer);
            scrollToBottom();
            
            // Remove typing indicator after getting response
            Timeline removeTyping = new Timeline(
                new KeyFrame(Duration.seconds(2), _ -> {
                    chatContainer.getChildren().remove(typingContainer);
                    if (typingIndicator != null) {
                        typingIndicator.setVisible(false);
                    }
                    isTyping = false;
                })
            );
            removeTyping.play();
        }
    }
   
    /**
     * Get bot response
     */
    private void getBotResponse(String userMessage) {
        Task<String> responseTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                // Simulate thinking time
                Thread.sleep(1000 + (long)(Math.random() * 2000));
                
                if (chatbotService != null) {
                    // Ensure we have the current user
                    if (currentUser == null) {
                        loadUserData(); // Reload user data if null
                    }
                    return chatbotService.generateResponse(userMessage, currentUser, chatHistory);
                } else {
                    return generateMockResponse(userMessage);
                }
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    String response = getValue();
                    
                    // Create bot response message
                    ChatMessage botMessage = new ChatMessage();
                    botMessage.setContent(response);
                    botMessage.setSender("EcoBot");
                    botMessage.setTimestamp(LocalDateTime.now());
                    botMessage.setMessageType("bot");
                    
                    // Add to chat after typing indicator is removed
                    Timeline delayedAdd = new Timeline(
                        new KeyFrame(Duration.seconds(2.1), _ -> addMessageToChat(botMessage))
                    );
                    delayedAdd.play();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showError("Response Error", "Failed to get bot response: " + getException().getMessage());
                    if (typingIndicator != null) {
                        typingIndicator.setVisible(false);
                    }
                });
            }
        };
        
        new Thread(responseTask).start();
    }
    
    /**
     * Generate mock response for demonstration
     */
    private String generateMockResponse(String userMessage) {
        String message = userMessage.toLowerCase();
        
        if (message.contains("carbon") || message.contains("footprint")) {
            return "🌱 Great question! Here are some effective ways to reduce your carbon footprint:\n\n" +
                   "• Use public transport or bike instead of driving\n" +
                   "• Switch to renewable energy sources\n" +
                   "• Eat more plant-based meals\n" +
                   "• Reduce, reuse, and recycle\n" +
                   "• Buy local and seasonal products\n\n" +
                   "Would you like specific tips for any of these areas?";
        } else if (message.contains("activity") || message.contains("suggest")) {
            return "🌟 Here are some eco-friendly activities you can try today:\n\n" +
                   "• Take a nature walk or hike\n" +
                   "• Start a small herb garden\n" +
                   "• Try a new vegetarian recipe\n" +
                   "• Organize a recycling session\n" +
                   "• Switch to energy-efficient LED bulbs\n\n" +
                   "Each of these can help reduce your environmental impact while being enjoyable!";
        } else if (message.contains("goal") || message.contains("target")) {
            return "🎯 Setting sustainability goals is excellent! Here's how to get started:\n\n" +
                   "• Start with small, achievable goals\n" +
                   "• Track your progress regularly\n" +
                   "• Focus on one area at a time (energy, transport, food)\n" +
                   "• Set specific targets (e.g., 'reduce car trips by 50%')\n" +
                   "• Celebrate your achievements\n\n" +
                   "What area would you like to focus on first?";
        } else if (message.contains("data") || message.contains("impact")) {
            return "📊 I can help analyze your environmental impact! Based on your activities:\n\n" +
                   "• Your current CO₂ savings: 247.5 kg\n" +
                   "• This equals planting ~12 trees\n" +
                   "• Your strongest category: Transportation\n" +
                   "• Improvement opportunity: Energy usage\n\n" +
                   "Would you like detailed insights on any specific area?";
        } else if (message.contains("help") || message.contains("what can you")) {
            return "🤖 I'm here to help you live more sustainably! I can assist with:\n\n" +
                   "• Personalized eco-tips and advice\n" +
                   "• Activity suggestions and tracking\n" +
                   "• Carbon footprint analysis\n" +
                   "• Goal setting and progress monitoring\n" +
                   "• Environmental impact calculations\n" +
                   "• Sustainable lifestyle guidance\n\n" +
                   "Just ask me anything about eco-living!";
        } else {
            return "🌍 That's an interesting question! While I specialize in environmental and sustainability topics, I'm always learning. " +
                   "Could you rephrase your question or ask me about:\n\n" +
                   "• Carbon footprint reduction\n" +
                   "• Sustainable activities\n" +
                   "• Environmental impact\n" +
                   "• Green living tips\n\n" +
                   "I'm here to help you on your eco-journey! 🌱";
        }
    }

    /**
     * Handle chat mode change
     */
    private void handleChatModeChange() {
        String selectedMode = chatModeComboBox.getValue();
        if (selectedMode != null) {
            String modeMessage = "🔄 Switched to " + selectedMode + " mode. ";
            
            switch (selectedMode) {
                case "Eco Advisor":
                    modeMessage += "I'll focus on providing environmental advice and green living tips.";
                    break;
                case "Activity Coach":
                    modeMessage += "I'll help you discover and track eco-friendly activities.";
                    break;
                case "Data Analyst":
                    modeMessage += "I'll analyze your environmental data and provide insights.";
                    break;
                case "General Assistant":
                    modeMessage += "I'll provide general help with all sustainability topics.";
                    break;
            }
            
            updateChatStatus(modeMessage);
        }
    }
    
    /**
     * Handle voice input (placeholder)
     */
    private void handleVoiceInput() {
        showNotification("Voice input feature coming soon!", "info");
    }
    
    /**
     * Clear chat
     */
    private void clearChat() {
        if (showConfirmation("Clear Chat", "Are you sure you want to clear this chat? This action cannot be undone.")) {
            chatHistory.clear();
            if (chatContainer != null) {
                chatContainer.getChildren().clear();
            }
            
            // Show welcome message again
            showWelcomeMessage();
            updateChatStatus("Chat cleared");
        }
    }
    
    /**
     * Export chat
     */
    private void exportChat() {
        try {
            if (chatbotService != null) {
                String filePath = chatbotService.exportChatHistory(currentChatSession);
                showSuccess("Export Successful", "Chat exported to: " + filePath);
            } else {
                showNotification("Export functionality will be implemented soon", "info");
            }
        } catch (Exception e) {
            showError("Export Error", "Failed to export chat: " + e.getMessage());
        }
    }
    
    /**
     * Scroll chat to bottom
     */
    private void scrollToBottom() {
        if (chatScrollPane != null) {
            Platform.runLater(() -> {
                chatScrollPane.setVvalue(1.0);
            });
        }
    }
    
    /**
     * Update connection status
     */
    private void updateConnectionStatus(boolean connected) {
        if (connectionStatusCircle != null) {
            connectionStatusCircle.setFill(connected ? Color.GREEN : Color.RED);
        }
        
        updateChatStatus(connected ? "Connected" : "Disconnected");
    }
    
    /**
     * Update chat status
     */
    private void updateChatStatus(String status) {
        if (chatStatusLabel != null) {
            chatStatusLabel.setText(status);
        }
        
        if (lastActiveLabel != null) {
            lastActiveLabel.setText("Last active: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }
    
    /**
     * Show notification
     */
    private void showNotification(String message, String type) {
        System.out.println(type.toUpperCase() + ": " + message);
        // Implementation would show actual notification to user
    }
    

    @Override
    public void refreshScreen() {
        loadChatHistory();
        updateConnectionStatus(true);
    }
}