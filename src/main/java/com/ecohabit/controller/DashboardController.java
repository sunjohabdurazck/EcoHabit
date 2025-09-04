package main.java.com.ecohabit.controller;

import main.java.com.ecohabit.model.Activity;
import main.java.com.ecohabit.model.User;
import main.java.com.ecohabit.service.C02Calculator;
import main.java.com.ecohabit.service.ChartUtils;
import main.java.com.ecohabit.service.ChatbotEngine;
import main.java.com.ecohabit.dao.ActivityDAO;
import main.java.com.ecohabit.dao.UserDAO;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import main.java.com.ecohabit.SessionManager;

import java.net.URL;
import java.time.LocalDate; 
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Ultra Modern Dashboard Controller with Glassmorphic UI and Smooth Animations
 * Features: Real-time updates, particle effects, micro-interactions, and AI-powered insights
 */
public class DashboardController extends BaseController {
    
    // FXML injected components
    @FXML private Label userNameLabel, userStatusLabel, streakLabel;
    @FXML private Label todayCO2Label, activitiesCountLabel, dateLabel;
    @FXML private Label totalCO2Label, equivalentLabel, dailyTipLabel;
    @FXML private ProgressBar weeklyProgressBar;
    @FXML private TextField quickActivityField;
    @FXML private VBox achievementsContainer, notificationContainer;
    
    // Charts
    @FXML private AreaChart<String, Number> weeklyChart;
    @FXML private PieChart activityPieChart;
    
    // Services and DAOs
    private C02Calculator co2Calculator;
    private ChartUtils chartUtils;
    private ChatbotEngine chatbot;
    private ActivityDAO activityDAO;
    private UserDAO userDAO;
     
    // Animation controllers
    private final Timeline particleAnimation = new Timeline();
    private final List<Circle> floatingParticles = new ArrayList<>();
    private final Random random = new Random();
    
    // Data
    private User currentUser;
    private ObservableList<Activity> todaysActivities;
    private List<String> ecoTips;
    private int currentTipIndex = 0;
    
    /**
     * Screen-specific initialization
     */
    @Override
    public void initializeScreen() {
        initializeServices();
        initializeAnimations();
        loadUserData();
        setupEventHandlers();
        //startRealTimeUpdates(); 
        createFloatingParticles();
    }
    
    /**
     * Initialize services and data access objects
     */ 
    private void initializeServices() {
        co2Calculator = new C02Calculator();
        chartUtils = new ChartUtils();
        chatbot = new ChatbotEngine();
        activityDAO = new ActivityDAO();
        userDAO = new UserDAO();
        todaysActivities = FXCollections.observableArrayList();
        
        // Load eco tips from JSON
        ecoTips = Arrays.asList(
            "💡 Switching to LED bulbs can reduce energy consumption by up to 75%!",
            "🚴‍♂️ Biking just 10km instead of driving saves 2.6kg of CO₂!",
            "♻️ Recycling 1 ton of paper saves 17 trees and 7000 gallons of water!",
            "🌱 A plant-based meal can save up to 2.5kg of CO₂ compared to meat!",
            "💧 Taking shorter showers can save up to 2.5 gallons per minute!"
        );
    }
    
    /**
     * Create stunning floating particle animations
     */
    private void createFloatingParticles() {
        // Create ethereal floating particles for ambient effect
        for (int i = 0; i < 15; i++) {
            Circle particle = new Circle();
            particle.setRadius(random.nextDouble() * 3 + 1);
            particle.setFill(Color.rgb(100, 255, 218, 0.3 + random.nextDouble() * 0.4));
            
            // Random positioning
            particle.setCenterX(random.nextDouble() * 1200);
            particle.setCenterY(random.nextDouble() * 800);
            
            // Floating animation
            createFloatingAnimation(particle);
            floatingParticles.add(particle);
        }
    }
    
    /**
     * Create smooth floating animation for particles
     */
    private void createFloatingAnimation(Circle particle) {
        TranslateTransition float1 = new TranslateTransition(
            Duration.seconds(8 + random.nextDouble() * 4), particle);
        float1.setByX(random.nextDouble() * 100 - 50);
        float1.setByY(random.nextDouble() * 100 - 50);
        float1.setAutoReverse(true);
        float1.setCycleCount(Timeline.INDEFINITE);
        
        FadeTransition fade = new FadeTransition(Duration.seconds(3), particle);
        fade.setFromValue(0.2);
        fade.setToValue(0.8);
        fade.setAutoReverse(true);
        fade.setCycleCount(Timeline.INDEFINITE);
        
        ParallelTransition animation = new ParallelTransition(float1, fade);
        animation.play();
    }
    
    /**
     * Initialize smooth animations and transitions
     */
    private void initializeAnimations() {
        // Staggered card entrance animations
        Platform.runLater(() -> {
            animateCardEntrance(todayCO2Label.getParent(), 0.2);
            animateCardEntrance(activitiesCountLabel.getParent(), 0.4);
            animateCardEntrance(weeklyProgressBar.getParent(), 0.6);
        });
        
        // Pulsing effect for important metrics
        createPulseAnimation(totalCO2Label);
        createPulseAnimation(todayCO2Label);
    }
    
    /**
     * Create elegant card entrance animation
     */
    private void animateCardEntrance(javafx.scene.Node card, double delay) {
        card.setOpacity(0);
        card.setTranslateY(30);
        card.setScaleX(0.95);
        card.setScaleY(0.95);
        
        FadeTransition fade = new FadeTransition(Duration.seconds(0.8), card);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.8), card);
        slide.setFromY(30);
        slide.setToY(0);
        
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.8), card);
        scale.setFromX(0.95);
        scale.setFromY(0.95);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        ParallelTransition entrance = new ParallelTransition(fade, slide, scale);
        entrance.setDelay(Duration.seconds(delay));
        entrance.setInterpolator(Interpolator.EASE_OUT);
        entrance.play();
    }
    
    /**
     * Create subtle pulse animation for metrics
     */
    private void createPulseAnimation(Label label) {
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), label);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setInterpolator(Interpolator.EASE_BOTH);
        pulse.play();
    }
    
    /**
     * Load user data and initialize UI with current user information
     */
    /**
     * Load user data and initialize UI with current user information
     */
    public void loadUserData() {
        CompletableFuture.runAsync(() -> {
            // Get the actual logged-in user from SessionManager
            currentUser = SessionManager.getCurrentUser();
            
            // If no user is in session, try to get from database as fallback
            if (currentUser == null) {
                System.err.println("No user in session, trying to get current user from database");
                currentUser = userDAO.getCurrentUser();
            }
            
            Platform.runLater(() -> {
                // Add null checks for all UI elements
                if (currentUser != null) {
                    if (userNameLabel != null) {
                        userNameLabel.setText(currentUser.getFullName());
                    }
                    if (userStatusLabel != null) {
                        userStatusLabel.setText(determineUserStatus(currentUser));
                    }
                    if (streakLabel != null) {
                        streakLabel.setText(currentUser.getCurrentStreak() + " day streak");
                    }
                    
                    // Update main controller sidebar with real user data
                    if (mainController != null) {
                        mainController.updateSidebarCO2(currentUser.getTotalCO2Saved());
                        mainController.updateSidebarStreak(currentUser.getCurrentStreak());
                    }
                } else {
                    System.err.println("No user data available");
                    // Set default values if no user
                    if (userNameLabel != null) userNameLabel.setText("Guest User");
                    if (userStatusLabel != null) userStatusLabel.setText("🌿 Eco Beginner");
                    if (streakLabel != null) streakLabel.setText("0 day streak");
                }
                
                if (dateLabel != null) {
                    updateDateLabel();
                }
                loadTodaysData();
                updateCharts();
                if (dailyTipLabel != null) {
                    loadRandomTip();
                }
            });
        });
    }
 
    private String determineUserStatus(User user) {
        double totalCO2 = user.getTotalCO2Saved();
        if (totalCO2 >= 500) return "🌟 Eco Legend";
        else if (totalCO2 >= 200) return "🌱 Eco Warrior";
        else if (totalCO2 >= 50) return "♻️ Green Enthusiast";
        else return "🌿 Eco Beginner";
    }
    
    /**
     * Update the current date label with modern formatting
     */
    private void updateDateLabel() {
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("EEEE • MMMM d, yyyy"));
        dateLabel.setText("Today • " + formattedDate);
    }
    
    /**
     * Load today's activity data and update metrics
     */
    private void loadTodaysData() {
        CompletableFuture.runAsync(() -> {
            List<Activity> activities = activityDAO.getActivitiesForDate(LocalDate.now());
            
            Platform.runLater(() -> {
                todaysActivities.clear();
                todaysActivities.addAll(activities);
                
                // Calculate today's CO2 savings
                double todaysCO2 = co2Calculator.calculateDailyC02(activities);
                if (todayCO2Label != null) {
                    animateNumberChange(todayCO2Label, 0, todaysCO2, "kg CO₂");
                }
                
                // Update activity count
                if (activitiesCountLabel != null) {
                    animateNumberChange(activitiesCountLabel, 0, activities.size(), "actions");
                }
                
                // Update weekly progress
                double weeklyProgress = calculateWeeklyProgress();
                if (weeklyProgressBar != null) {
                    animateProgressBar(weeklyProgressBar, weeklyProgress);
                }
                
                // Update total savings
                double totalCO2 = currentUser != null ? currentUser.getTotalCO2Saved() : 0;
                if (totalCO2Label != null) {
                    animateNumberChange(totalCO2Label, 0, totalCO2, "");
                }
                if (equivalentLabel != null) {
                    updateEquivalentLabel(totalCO2);
                }
            });
        });
    }
    /**
     * Animate number changes with smooth transitions
     */
    private void animateNumberChange(Label label, double from, double to, String suffix) {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
            new KeyFrame(Duration.seconds(1.5), new KeyValue(
                new javafx.beans.property.SimpleDoubleProperty(from) {
                    @Override
                    protected void invalidated() {
                        double value = get();
                        if (suffix.isEmpty()) {
                            label.setText(String.format("%.1f", value));
                        } else { 
                            label.setText(String.format("%.1f %s", value, suffix));
                        }
                    }
                }, to, Interpolator.EASE_OUT))
        );
        timeline.play();
    }
    
    /**
     * Animate progress bar with smooth filling effect
     */
    private void animateProgressBar(ProgressBar progressBar, double targetProgress) {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
            new KeyFrame(Duration.seconds(2), new KeyValue(
                progressBar.progressProperty(), targetProgress, Interpolator.EASE_OUT))
        );
        timeline.play();
        
        // Add glow effect when progress is high
        if (targetProgress > 0.75) {
            DropShadow glow = new DropShadow();
            glow.setColor(Color.rgb(78, 205, 196, 0.8));
            glow.setRadius(15);
            progressBar.setEffect(glow);
        }
    }
    
    /**
     * Calculate weekly progress towards goal
     */
    private double calculateWeeklyProgress() {
        LocalDate weekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        List<Activity> weeklyActivities = activityDAO.getActivitiesForWeek(weekStart);
        double weeklyCO2 = co2Calculator.calculateWeeklyCO2(weeklyActivities);
        double weeklyGoal = 50.0; // 50kg CO2 weekly goal
        return Math.min(weeklyCO2 / weeklyGoal, 1.0);
    }
    
    /**
     * Update the equivalent impact label with creative comparisons
     */
    private void updateEquivalentLabel(double co2Saved) {
        String[] equivalents = {
            "≈ planting " + Math.round(co2Saved * 0.048) + " trees",
            "≈ driving " + Math.round(co2Saved * 4.6) + " km less",
            "≈ saving " + Math.round(co2Saved * 1.2) + " gallons of gasoline",
            "≈ powering a home for " + Math.round(co2Saved * 0.12) + " days"
        };
        
        String equivalent = equivalents[random.nextInt(equivalents.length)];
        equivalentLabel.setText(equivalent);
        
        // Animate the text change
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), equivalentLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), equivalentLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        SequentialTransition sequence = new SequentialTransition(fadeOut, fadeIn);
        sequence.play();
    }
    
    /**
     * Update charts with latest data and beautiful styling
     */
    private void updateCharts() {
        CompletableFuture.runAsync(() -> {
            // Weekly trend data
            XYChart.Series<String, Number> weeklySeries = chartUtils.generateWeeklyTrendData();
            
            // Activity breakdown data
            ObservableList<PieChart.Data> pieData = chartUtils.generateActivityBreakdownData();
            
            Platform.runLater(() -> {
                // Update area chart
                weeklyChart.getData().clear();
                weeklyChart.getData().add(weeklySeries);
                styleAreaChart();
                
                // Update pie chart
                activityPieChart.setData(pieData);
                stylePieChart();
                
                // Animate chart appearance
                animateChartEntrance(weeklyChart);
                animateChartEntrance(activityPieChart);
            });
        });
    }
    
    /**
     * Apply modern styling to area chart
     */
    private void styleAreaChart() {
        weeklyChart.setCreateSymbols(true);
        weeklyChart.setLegendVisible(false);
        weeklyChart.lookup(".chart-series-area-fill").setStyle("-fx-fill: rgba(78, 205, 196, 0.3);");
        weeklyChart.lookup(".chart-series-area-line").setStyle("-fx-stroke: #4ecdc4; -fx-stroke-width: 3px;");
    }
    
    /**
     * Apply modern styling to pie chart
     */
    private void stylePieChart() {
        activityPieChart.setLegendVisible(true);
        activityPieChart.setLabelsVisible(false);
        
        // Custom colors for pie slices
        String[] colors = {"#4ecdc4", "#ff6b9d", "#bb86fc", "#64ffda", "#38ef7d"};
        for (int i = 0; i < activityPieChart.getData().size(); i++) {
            PieChart.Data data = activityPieChart.getData().get(i);
            data.getNode().setStyle("-fx-pie-color: " + colors[i % colors.length] + ";");
        }
    }
    
    /**
     * Animate chart entrance with smooth scaling
     */
    private void animateChartEntrance(javafx.scene.Node chart) {
        chart.setScaleX(0.8);
        chart.setScaleY(0.8);
        chart.setOpacity(0);
        
        ScaleTransition scale = new ScaleTransition(Duration.seconds(1), chart);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        FadeTransition fade = new FadeTransition(Duration.seconds(1), chart);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        ParallelTransition entrance = new ParallelTransition(scale, fade);
        entrance.setInterpolator(Interpolator.EASE_OUT);
        entrance.play();
    }
    
    /**
     * Setup event handlers for interactive elements
     */
    private void setupEventHandlers() {
        // Quick add functionality
       // addActivityBtn.setOnAction(e -> handleQuickAdd());
        quickActivityField.setOnAction(e -> handleQuickAdd());
        
        // Additional navigation handlers
        setupAdditionalNavigation();
        
        // Add hover effects to interactive elements
        addHoverEffects();
    }
    
    /**
     * Setup additional navigation handlers
     */
    @Override
    protected void setupAdditionalNavigation() {
        // Add handlers for buttons not covered by BaseController
        if (badgesBtn != null) {
            badgesBtn.setOnAction(e -> navigateTo("badges"));
        }
        if (tipsBtn != null) {
            tipsBtn.setOnAction(e -> navigateTo("tips"));
        }
        if (chatbotBtn != null) {
            chatbotBtn.setOnAction(e -> navigateTo("chatbot"));
        }
    }
    
    /**
     * Add beautiful hover effects to interactive elements
     */
    private void addHoverEffects() {
        //addHoverEffect(addActivityBtn, 1.05, Color.rgb(78, 205, 196, 0.6));
      //  addHoverEffect(nextTipBtn, 1.03, Color.rgb(100, 255, 218, 0.5));
    }
    
    /**
     * Add hover effect to a button
     */
    private void addHoverEffect(Button button, double scale, Color glowColor) {
        DropShadow glow = new DropShadow();
        glow.setColor(glowColor);
        glow.setRadius(20);
        
        button.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
            scaleUp.setToX(scale);
            scaleUp.setToY(scale);
            scaleUp.play();
            button.setEffect(glow);
        });
        
        button.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), button);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
            button.setEffect(null);
        });
    }
    
    /**
     * Handle quick activity addition
     */
    private void handleQuickAdd() {
        String activityText = quickActivityField.getText().trim();
        if (!activityText.isEmpty()) {
            // Create new activity
            Activity activity = new Activity();
            activity.setDescription(activityText);
            activity.setDate(LocalDate.now());
            activity.setCo2Saved(co2Calculator.estimateCO2Savings(activityText)); 
            
            // Save to database
            CompletableFuture.runAsync(() -> {
                activityDAO.addActivity(activity);
                
                Platform.runLater(() -> {
                    // Show success notification using BaseController method
                    showSuccess("Activity Added", "Saved " + activity.getCo2Saved() + " kg CO₂!");
                    
                    // Clear input and refresh data
                    quickActivityField.clear();
                    loadTodaysData();
                     
                    // Celebrate with animation
                    celebrateActivityAdd();
                });
            });
        }
    }
    
    /**
     * Show celebration animation when activity is added
     */
    private void celebrateActivityAdd() {
        // Create temporary celebration particles
        for (int i = 0; i < 8; i++) {
            Circle particle = new Circle(3, Color.rgb(78, 205, 196, 0.8));
            particle.setCenterX(quickActivityField.getLayoutX() + 30);
            particle.setCenterY(quickActivityField.getLayoutY() + 30);
            
            // Random burst animation
            TranslateTransition burst = new TranslateTransition(Duration.seconds(1), particle);
            burst.setByX((random.nextDouble() - 0.5) * 100);
            burst.setByY((random.nextDouble() - 0.5) * 100);
            
            FadeTransition fade = new FadeTransition(Duration.seconds(1), particle);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            
            ParallelTransition celebration = new ParallelTransition(burst, fade);
            celebration.setOnFinished(e -> ((Pane) particle.getParent()).getChildren().remove(particle));
            celebration.play();
        }
    }
    
    /**
     * Load and display next eco tip
     */
    private void loadNextTip() {
        currentTipIndex = (currentTipIndex + 1) % ecoTips.size();
        String newTip = ecoTips.get(currentTipIndex);
        
        // Animate tip change
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), dailyTipLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            dailyTipLabel.setText(newTip);
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), dailyTipLabel);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }
    
    /**
     * Load random tip on startup
     */
    private void loadRandomTip() {
        currentTipIndex = random.nextInt(ecoTips.size());
        dailyTipLabel.setText(ecoTips.get(currentTipIndex));
    }
    
    /**
     * Called when screen becomes active
     */
    @Override
    public void onScreenActivated() {
        super.onScreenActivated();
        // Refresh data when dashboard becomes active
        refreshScreen();
    }
    
    /**
     * Refresh screen data
     */
    @Override
    public void refreshScreen() {
        loadUserData();
        updateCharts();
    }
    
    /**
     * Cleanup resources when controller is destroyed
     */
    public void cleanup() {
        particleAnimation.stop();
        // Stop any other running animations
    }
}