package main.java.com.ecohabit.controller;

import javafx.animation.*;
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
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.util.Duration;
import main.java.com.ecohabit.SessionManager;
import main.java.com.ecohabit.model.Badge;
import main.java.com.ecohabit.model.User;
import main.java.com.ecohabit.service.BadgeService;
import main.java.com.ecohabit.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the Badges & Achievements screen
 * Displays earned badges, progress towards new badges, and achievement statistics
 */
public class BadgesController extends BaseController {
    
    // Header Statistics
    @FXML private Label totalBadgesLabel;
    @FXML private Label earnedBadgesLabel;
    @FXML private Label completionPercentageLabel;
    @FXML private Label recentBadgeLabel;
    @FXML private ProgressBar overallProgressBar;
    
    // Filter Controls
    @FXML private ComboBox<String> categoryFilterComboBox;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private ComboBox<String> sortByComboBox;
    @FXML private Button applyFiltersBtn;
    @FXML private Button clearFiltersBtn;
    
    // Search
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    
    // Badges Display
    @FXML private TabPane badgesTabPane;
    @FXML private Tab earnedTab;
    @FXML private Tab inProgressTab;
    @FXML private Tab lockedTab;
    @FXML private Tab allBadgesTab;
    
    @FXML private ScrollPane earnedScrollPane;
    @FXML private GridPane earnedBadgesGrid;
    
    @FXML private ScrollPane inProgressScrollPane;
    @FXML private VBox inProgressContainer;
    
    @FXML private ScrollPane lockedScrollPane;
    @FXML private GridPane lockedBadgesGrid;
    
    @FXML private ScrollPane allBadgesScrollPane;
    @FXML private GridPane allBadgesGrid;
    
    // Sidebar - Featured Badge
    @FXML private VBox featuredBadgeContainer;
    @FXML private Label featuredBadgeIcon;
    @FXML private Label featuredBadgeTitle;
    @FXML private Label featuredBadgeDescription;
    @FXML private ProgressBar featuredBadgeProgress;
    @FXML private Label featuredBadgeProgressText;
    @FXML private Button focusBadgeBtn;
    
    // Sidebar - Quick Stats
    @FXML private Label carbonSaverBadgesLabel;
    @FXML private Label activityMasterBadgesLabel;
    @FXML private Label streakChampionBadgesLabel;
    @FXML private Label specialEventBadgesLabel;
    
    // Sidebar - Recent Activity
    @FXML private VBox recentActivityContainer;
    
    // Achievement Celebration
    @FXML private VBox celebrationContainer;
    
    // Services and Data
    private BadgeService badgeService;
    private UserService userService;
    private User currentUser;
    private ObservableList<Badge> allBadges;
    private ObservableList<Badge> earnedBadges;
    private ObservableList<Badge> inProgressBadges;
    private ObservableList<Badge> lockedBadges;
    private ObservableList<Badge> filteredBadges;
    
    // State Management
    private String currentCategory = "All";
    private String currentStatus = "All";
    private String currentSortBy = "Date Earned";
    private String searchQuery = "";
    private Badge featuredBadge;
    private Timeline celebrationAnimation;
    
    @Override
    public void initializeScreen() {
        try {
            // Initialize services
            initializeServices();
            
            // Setup UI components
            setupComboBoxes();
            setupEventHandlers();
            setupTabPane();
            
            // Load data
            loadUserData();
            loadBadges();
            updateStatistics();
            selectFeaturedBadge();
            loadRecentActivity();
            
            // Setup animations
            setupAnimations();
            
            System.out.println("Badges screen initialized successfully");
            
        } catch (Exception e) {
            System.err.println("Error initializing badges screen: " + e.getMessage());
            e.printStackTrace();
            showError("Initialization Error", "Failed to initialize badges: " + e.getMessage());
        }
    }
    
    /**
     * Initialize services
     */
    private void initializeServices() {
        try {
            badgeService = new BadgeService();
            userService = new UserService();
            allBadges = FXCollections.observableArrayList();
            earnedBadges = FXCollections.observableArrayList();
            inProgressBadges = FXCollections.observableArrayList();
            lockedBadges = FXCollections.observableArrayList();
            filteredBadges = FXCollections.observableArrayList();
            
        } catch (Exception e) {
            System.err.println("Error initializing services: " + e.getMessage());
            createMockData();
        }
    }
    
    /**
     * Create mock data for demonstration
     */
    private void createMockData() {
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setFirstName("John");
        currentUser.setLastName("Doe");
        
        allBadges = FXCollections.observableArrayList();
        
        // Create sample badges
        allBadges.addAll(Arrays.asList(
            // Earned Badges
            createSampleBadge("First Steps", "Getting Started", "🌱", "Complete your first eco-friendly activity", 
                true, 100, 1, "Easy", LocalDateTime.now().minusDays(30)),
            createSampleBadge("Carbon Saver", "Environmental Impact", "🌍", "Save 10kg of CO₂", 
                true, 100, 10, "Medium", LocalDateTime.now().minusDays(25)),
            createSampleBadge("Week Warrior", "Streak", "🔥", "Maintain a 7-day activity streak", 
                true, 100, 7, "Medium", LocalDateTime.now().minusDays(20)),
            createSampleBadge("Recycling Hero", "Waste Reduction", "♻️", "Complete 25 recycling activities", 
                true, 100, 25, "Medium", LocalDateTime.now().minusDays(15)),
            createSampleBadge("Energy Saver", "Energy Conservation", "💡", "Save 50kWh of energy", 
                true, 100, 50, "Medium", LocalDateTime.now().minusDays(10)),
            
            // In Progress Badges
            createSampleBadge("Transport Master", "Transportation", "🚲", "Use eco-friendly transport 50 times", 
                false, 70, 35, "Medium", null),
            createSampleBadge("Plant Parent", "Food & Garden", "🌿", "Grow 10 plants or herbs", 
                false, 60, 6, "Hard", null),
            createSampleBadge("Streak Legend", "Streak", "🏆", "Maintain a 30-day activity streak", 
                false, 50, 15, "Hard", null),
            
            // Locked Badges
            createSampleBadge("Eco Champion", "Environmental Impact", "👑", "Save 500kg of CO₂", 
                false, 0, 0, "Very Hard", null),
            createSampleBadge("Community Leader", "Social Impact", "👥", "Invite 10 friends to join EcoHabit", 
                false, 0, 0, "Medium", null),
            createSampleBadge("Perfect Month", "Streak", "⭐", "Complete eco activities every day for a month", 
                false, 0, 0, "Very Hard", null),
            createSampleBadge("Water Guardian", "Water Conservation", "💧", "Save 1000 gallons of water", 
                false, 0, 0, "Hard", null),
            createSampleBadge("Zero Waste", "Waste Reduction", "🗂️", "Achieve zero waste for 7 consecutive days", 
                false, 0, 0, "Very Hard", null)
        ));
        
        // Categorize badges
        categorizeBadges();
    }
    
    /**
     * Create sample badge for demo
     */
    private Badge createSampleBadge(String title, String category, String icon, String description,
                                   boolean earned, int progress, int current, String difficulty, 
                                   LocalDateTime earnedDate) {
        Badge badge = new Badge();
        badge.setId(UUID.randomUUID().hashCode());
        badge.setTitle(title);
        badge.setCategory(category);
        badge.setIcon(icon);
        badge.setDescription(description);
        badge.setEarned(earned);
        badge.setProgress(progress);
        badge.setCurrentValue(current);
        badge.setTargetValue(earned ? current : current + (100 - progress) / 2);
        badge.setDifficulty(difficulty);
        badge.setEarnedDate(earnedDate);
        badge.setPoints(getDifficultyPoints(difficulty));
        badge.setRarity(getDifficultyRarity(difficulty));
        return badge;
    }
    
    /**
     * Get points based on difficulty
     */
    private int getDifficultyPoints(String difficulty) {
        switch (difficulty) {
            case "Easy": return 10;
            case "Medium": return 25;
            case "Hard": return 50;
            case "Very Hard": return 100;
            default: return 10;
        }
    }
    
    /**
     * Get rarity based on difficulty
     */
    private String getDifficultyRarity(String difficulty) {
        switch (difficulty) {
            case "Easy": return "Common";
            case "Medium": return "Uncommon";
            case "Hard": return "Rare";
            case "Very Hard": return "Legendary";
            default: return "Common";
        }
    }
    
    /**
     * Categorize badges into different lists
     */
    private void categorizeBadges() {
        earnedBadges.clear();
        inProgressBadges.clear();
        lockedBadges.clear();
        
        for (Badge badge : allBadges) {
            if (badge.isEarned()) {
                earnedBadges.add(badge);
            } else if (badge.getProgress() > 0) {
                inProgressBadges.add(badge);
            } else {
                lockedBadges.add(badge);
            }
        }
        
        filteredBadges.setAll(allBadges);
    }
    
    /**
     * Setup combo boxes
     */
    private void setupComboBoxes() {
        // Category filter
        if (categoryFilterComboBox != null) {
            categoryFilterComboBox.setItems(FXCollections.observableArrayList(
                "All", "Getting Started", "Environmental Impact", "Streak", 
                "Transportation", "Waste Reduction", "Energy Conservation", 
                "Food & Garden", "Water Conservation", "Social Impact"
            ));
            categoryFilterComboBox.getSelectionModel().selectFirst();
        }
        
        // Status filter
        if (statusFilterComboBox != null) {
            statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "All", "Earned", "In Progress", "Locked"
            ));
            statusFilterComboBox.getSelectionModel().selectFirst();
        }
        
        // Sort by
        if (sortByComboBox != null) {
            sortByComboBox.setItems(FXCollections.observableArrayList(
                "Date Earned", "Progress", "Difficulty", "Points", "Title"
            ));
            sortByComboBox.getSelectionModel().selectFirst();
        }
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Filter controls
        if (applyFiltersBtn != null) {
            applyFiltersBtn.setOnAction(e -> applyFilters());
        }
        if (clearFiltersBtn != null) {
            clearFiltersBtn.setOnAction(e -> clearFilters());
        }
        
        // Search
        if (searchBtn != null) {
            searchBtn.setOnAction(e -> performSearch());
        }
        if (searchField != null) {
            searchField.setOnAction(e -> performSearch());
            searchField.textProperty().addListener((obs, oldText, newText) -> {
                if (newText == null || newText.isEmpty()) {
                    clearSearch();
                }
            });
        }
        
        // Tab selection
        if (badgesTabPane != null) {
            badgesTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                if (newTab != null) {
                    handleTabChange(newTab);
                }
            });
        }
        
        // Featured badge
        if (focusBadgeBtn != null) {
            focusBadgeBtn.setOnAction(e -> focusOnBadge());
        }
    }
    
    /**
     * Setup tab pane
     */
    private void setupTabPane() {
        if (badgesTabPane != null) {
            // Set default tab
            badgesTabPane.getSelectionModel().select(earnedTab);
        }
    }
    
    /**
     * Setup animations
     */
    private void setupAnimations() {
        // Create celebration animation timeline
        celebrationAnimation = new Timeline();
        celebrationAnimation.setCycleCount(Timeline.INDEFINITE);
    }
    
    /**
     * Load user data
     */
    private void loadUserData() {
        try {
            if (userService != null) {
            	currentUser = SessionManager.getCurrentUser();
            }
            
            if (currentUser == null) {
                createMockData();
            }
            
        } catch (Exception e) {
            System.err.println("Error loading user data: " + e.getMessage());
            createMockData();
        }
    }
    
    /**
     * Load badges from service
     */
    private void loadBadges() {
        Task<List<Badge>> loadTask = new Task<List<Badge>>() {
            @Override
            protected List<Badge> call() throws Exception {
                if (badgeService != null && currentUser != null) {
                    return badgeService.getUserBadges(currentUser.getId());
                } else {
                    return new ArrayList<>(allBadges);
                }
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    List<Badge> badges = getValue();
                    allBadges.setAll(badges);
                    categorizeBadges();
                    displayBadges();
                    updateStatistics();
                    selectFeaturedBadge();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("Failed to load badges: " + getException().getMessage());
                    // Use mock data as fallback
                    if (allBadges.isEmpty()) {
                        createMockData();
                        displayBadges();
                        updateStatistics();
                        selectFeaturedBadge();
                    }
                });
            }
        };
        
        new Thread(loadTask).start();
    }
    
    /**
     * Handle tab change
     */
    private void handleTabChange(Tab selectedTab) {
        if (selectedTab == earnedTab) {
            displayEarnedBadges();
        } else if (selectedTab == inProgressTab) {
            displayInProgressBadges();
        } else if (selectedTab == lockedTab) {
            displayLockedBadges();
        } else if (selectedTab == allBadgesTab) {
            displayAllBadges();
        }
    }
    
    /**
     * Display badges based on current tab
     */
    private void displayBadges() {
        Tab selectedTab = badgesTabPane != null ? badgesTabPane.getSelectionModel().getSelectedItem() : earnedTab;
        handleTabChange(selectedTab);
    }
    
    /**
     * Display earned badges
     */
    private void displayEarnedBadges() {
        if (earnedBadgesGrid != null) {
            earnedBadgesGrid.getChildren().clear();
            populateBadgeGrid(earnedBadgesGrid, earnedBadges);
        }
    }
    
    /**
     * Display in-progress badges
     */
    private void displayInProgressBadges() {
        if (inProgressContainer != null) {
            inProgressContainer.getChildren().clear();
            
            for (Badge badge : inProgressBadges) {
                VBox progressCard = createProgressCard(badge);
                inProgressContainer.getChildren().add(progressCard);
                
                // Animate entrance
                animateBadgeEntrance(progressCard, inProgressBadges.indexOf(badge));
            }
        }
    }
    
    /**
     * Display locked badges
     */
    private void displayLockedBadges() {
        if (lockedBadgesGrid != null) {
            lockedBadgesGrid.getChildren().clear();
            populateBadgeGrid(lockedBadgesGrid, lockedBadges);
        }
    }
    
    /**
     * Display all badges
     */
    private void displayAllBadges() {
        if (allBadgesGrid != null) {
            allBadgesGrid.getChildren().clear();
            populateBadgeGrid(allBadgesGrid, filteredBadges);
        }
    }
    
    /**
     * Populate badge grid with badges
     */
    private void populateBadgeGrid(GridPane grid, ObservableList<Badge> badges) {
        grid.getChildren().clear();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));
        
        int columns = 4; // 4 badges per row
        int row = 0, col = 0;
        
        for (Badge badge : badges) {
            VBox badgeCard = createBadgeCard(badge);
            grid.add(badgeCard, col, row);
            
            // Animate entrance
            animateBadgeEntrance(badgeCard, badges.indexOf(badge));
            
            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
        
        // Show empty state if no badges
        if (badges.isEmpty()) {
            showEmptyState(grid);
        }
    }
    
    /**
     * Create badge card UI component
     */
    private VBox createBadgeCard(Badge badge) {
        VBox card = new VBox(8);
        card.getStyleClass().add("badge-card");
        card.setPadding(new Insets(15));
        card.setPrefWidth(180);
        card.setAlignment(Pos.CENTER);
        
        // Badge icon with effects
        Label iconLabel = new Label(badge.getIcon());
        iconLabel.getStyleClass().add("badge-icon");
        iconLabel.setStyle("-fx-font-size: 48px;");
        
        // Add glow effect for earned badges
        if (badge.isEarned()) {
            Glow glow = new Glow(0.8);
            iconLabel.setEffect(glow);
        } else {
            // Desaturate locked badges
            iconLabel.setOpacity(0.4);
        }
        
        // Badge title
        Label titleLabel = new Label(badge.getTitle());
        titleLabel.getStyleClass().add("badge-title");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        
        // Badge category and rarity
        HBox metaRow = new HBox(5);
        metaRow.setAlignment(Pos.CENTER);
        
        Label categoryLabel = new Label(badge.getCategory());
        categoryLabel.getStyleClass().add("badge-category");
        
        Label rarityLabel = new Label(badge.getRarity());
        rarityLabel.getStyleClass().add("badge-rarity");
        rarityLabel.getStyleClass().add("rarity-" + badge.getRarity().toLowerCase());
        
        metaRow.getChildren().addAll(categoryLabel, rarityLabel);
        
        // Progress bar for in-progress badges
        VBox progressSection = new VBox(5);
        if (!badge.isEarned() && badge.getProgress() > 0) {
            ProgressBar progressBar = new ProgressBar(badge.getProgress() / 100.0);
            progressBar.getStyleClass().add("badge-progress");
            
            Label progressText = new Label(badge.getCurrentValue() + " / " + badge.getTargetValue());
            progressText.getStyleClass().add("badge-progress-text");
            
            progressSection.getChildren().addAll(progressBar, progressText);
        }
        
        // Points display
        Label pointsLabel = new Label(badge.getPoints() + " pts");
        pointsLabel.getStyleClass().add("badge-points");
        
        // Earned date for earned badges
        VBox dateSection = new VBox();
        if (badge.isEarned() && badge.getEarnedDate() != null) {
            Label earnedLabel = new Label("Earned " + badge.getEarnedDate().toLocalDate());
            earnedLabel.getStyleClass().add("badge-earned-date");
            dateSection.getChildren().add(earnedLabel);
        }
        
        card.getChildren().addAll(iconLabel, titleLabel, metaRow, progressSection, pointsLabel, dateSection);
        
        // Add click handler for badge details
        card.setOnMouseClicked(e -> showBadgeDetails(badge));
        
        // Add hover effect
        addBadgeHoverEffect(card, badge);
        
        return card;
    }
    
    /**
     * Create progress card for in-progress badges
     */
    private VBox createProgressCard(Badge badge) {
        VBox card = new VBox(10);
        card.getStyleClass().add("progress-badge-card");
        card.setPadding(new Insets(15));
        
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Badge icon
        Label iconLabel = new Label(badge.getIcon());
        iconLabel.getStyleClass().add("progress-badge-icon");
        iconLabel.setStyle("-fx-font-size: 36px;");
        
        // Badge info
        VBox badgeInfo = new VBox(5);
        Label titleLabel = new Label(badge.getTitle());
        titleLabel.getStyleClass().add("progress-badge-title");
        
        Label descLabel = new Label(badge.getDescription());
        descLabel.getStyleClass().add("progress-badge-description");
        descLabel.setWrapText(true);
        
        badgeInfo.getChildren().addAll(titleLabel, descLabel);
        
        header.getChildren().addAll(iconLabel, badgeInfo);
        
        // Progress section
        VBox progressSection = new VBox(8);
        
        HBox progressHeader = new HBox();
        progressHeader.setAlignment(Pos.CENTER_RIGHT);
        
        Label progressText = new Label(badge.getCurrentValue() + " / " + badge.getTargetValue());
        progressText.getStyleClass().add("progress-text");
        
        Label percentageText = new Label(badge.getProgress() + "%");
        percentageText.getStyleClass().add("progress-percentage");
        
        progressHeader.getChildren().addAll(progressText, percentageText);
        
        ProgressBar progressBar = new ProgressBar(badge.getProgress() / 100.0);
        progressBar.getStyleClass().add("detailed-progress-bar");
        progressBar.setPrefWidth(300);
        
        progressSection.getChildren().addAll(progressHeader, progressBar);
        
        // Action button
        Button actionBtn = new Button("Work Towards This");
        actionBtn.setOnAction(e -> focusOnBadge(badge));
        
        card.getChildren().addAll(header, progressSection, actionBtn);
        
        return card;
    }
    
    /**
     * Add hover effect to badge card
     */
    private void addBadgeHoverEffect(VBox card, Badge badge) {
        card.setOnMouseEntered(e -> {
            if (badge.isEarned()) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
                scale.setToX(1.1);
                scale.setToY(1.1);
                scale.play();
                
                DropShadow shadow = new DropShadow();
                shadow.setColor(Color.GOLD);
                shadow.setRadius(15);
                card.setEffect(shadow);
            }
        });
        
        card.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
            card.setEffect(null);
        });
    }
    
    /**
     * Animate badge entrance
     */
    private void animateBadgeEntrance(VBox card, int index) {
        card.setOpacity(0);
        card.setTranslateY(50);
        
        FadeTransition fade = new FadeTransition(Duration.millis(600), card);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(index * 100));
        
        TranslateTransition slide = new TranslateTransition(Duration.millis(600), card);
        slide.setFromY(50);
        slide.setToY(0);
        slide.setDelay(Duration.millis(index * 100));
        
        ParallelTransition parallel = new ParallelTransition(fade, slide);
        parallel.play();
    }
    
    /**
     * Show empty state when no badges are available
     */
    private void showEmptyState(GridPane grid) {
        VBox emptyState = new VBox(15);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setPadding(new Insets(40));
        
        Label iconLabel = new Label("🏆");
        iconLabel.setStyle("-fx-font-size: 64px;");
        
        Label titleLabel = new Label("No Badges Yet");
        titleLabel.getStyleClass().add("empty-state-title");
        
        Label messageLabel = new Label("Complete eco-friendly activities to earn your first badge!");
        messageLabel.getStyleClass().add("empty-state-message");
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        
        emptyState.getChildren().addAll(iconLabel, titleLabel, messageLabel);
        
        // Add to center of grid
        GridPane.setColumnSpan(emptyState, 4);
        GridPane.setRowSpan(emptyState, 2);
        grid.add(emptyState, 0, 0);
    }
    
    /**
     * Show badge details dialog
     */
    private void showBadgeDetails(Badge badge) {
        // Create dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Badge Details");
        dialog.setHeaderText(badge.getTitle());
        
        // Create content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        
        // Badge icon
        Label iconLabel = new Label(badge.getIcon());
        iconLabel.setStyle("-fx-font-size: 72px;");
        
        if (badge.isEarned()) {
            Glow glow = new Glow(0.9);
            iconLabel.setEffect(glow);
        }
        
        // Description
        Label descLabel = new Label(badge.getDescription());
        descLabel.setWrapText(true);
        descLabel.setAlignment(Pos.CENTER);
        
        // Metadata
        GridPane metadata = new GridPane();
        metadata.setHgap(10);
        metadata.setVgap(5);
        metadata.setPadding(new Insets(10));
        
        metadata.add(new Label("Category:"), 0, 0);
        metadata.add(new Label(badge.getCategory()), 1, 0);
        
        metadata.add(new Label("Difficulty:"), 0, 1);
        metadata.add(new Label(badge.getDifficulty()), 1, 1);
        
        metadata.add(new Label("Rarity:"), 0, 2);
        Label rarityLabel = new Label(badge.getRarity());
        rarityLabel.getStyleClass().add("rarity-" + badge.getRarity().toLowerCase());
        metadata.add(rarityLabel, 1, 2);
        
        metadata.add(new Label("Points:"), 0, 3);
        metadata.add(new Label(String.valueOf(badge.getPoints())), 1, 3);
        
        // Progress section
        if (!badge.isEarned()) {
            VBox progressBox = new VBox(8);
            progressBox.setAlignment(Pos.CENTER);
            
            Label progressLabel = new Label("Progress: " + badge.getProgress() + "%");
            ProgressBar progressBar = new ProgressBar(badge.getProgress() / 100.0);
            progressBar.setPrefWidth(200);
            
            Label countLabel = new Label(badge.getCurrentValue() + " / " + badge.getTargetValue());
            
            progressBox.getChildren().addAll(progressLabel, progressBar, countLabel);
            content.getChildren().add(progressBox);
        } else if (badge.getEarnedDate() != null) {
            Label earnedDateLabel = new Label("Earned on: " + badge.getEarnedDate().toLocalDate());
            content.getChildren().add(earnedDateLabel);
        }
        
        content.getChildren().addAll(iconLabel, descLabel, metadata);
        
        // Add close button
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
    
    /**
     * Update statistics in header
     */
    private void updateStatistics() {
        if (totalBadgesLabel != null) {
            totalBadgesLabel.setText(String.valueOf(allBadges.size()));
        }
        
        if (earnedBadgesLabel != null) {
            earnedBadgesLabel.setText(String.valueOf(earnedBadges.size()));
        }
        
        if (completionPercentageLabel != null) {
            double percentage = allBadges.isEmpty() ? 0 : (earnedBadges.size() * 100.0 / allBadges.size());
            completionPercentageLabel.setText(String.format("%.1f%%", percentage));
        }
        
        if (overallProgressBar != null) {
            double progress = allBadges.isEmpty() ? 0 : (earnedBadges.size() * 1.0 / allBadges.size());
            overallProgressBar.setProgress(progress);
        }
        
        if (recentBadgeLabel != null) {
            Badge mostRecent = earnedBadges.stream()
                .filter(b -> b.getEarnedDate() != null)
                .max(Comparator.comparing(Badge::getEarnedDate))
                .orElse(null);
            
            if (mostRecent != null) {
                recentBadgeLabel.setText(mostRecent.getTitle());
            } else {
                recentBadgeLabel.setText("None yet");
            }
        }
        
        // Update category stats
        updateCategoryStatistics();
    }
    
    /**
     * Update category-specific statistics
     */
    private void updateCategoryStatistics() {
        if (carbonSaverBadgesLabel != null) {
            long carbonSaverCount = earnedBadges.stream()
                .filter(b -> "Environmental Impact".equals(b.getCategory()))
                .count();
            carbonSaverBadgesLabel.setText(String.valueOf(carbonSaverCount));
        }
        
        if (activityMasterBadgesLabel != null) {
            long activityMasterCount = earnedBadges.stream()
                .filter(b -> "Getting Started".equals(b.getCategory()) || "Streak".equals(b.getCategory()))
                .count();
            activityMasterBadgesLabel.setText(String.valueOf(activityMasterCount));
        }
        
        if (streakChampionBadgesLabel != null) {
            long streakChampionCount = earnedBadges.stream()
                .filter(b -> "Streak".equals(b.getCategory()))
                .count();
            streakChampionBadgesLabel.setText(String.valueOf(streakChampionCount));
        }
        
        if (specialEventBadgesLabel != null) {
            long specialEventCount = earnedBadges.stream()
                .filter(b -> "Social Impact".equals(b.getCategory()))
                .count();
            specialEventBadgesLabel.setText(String.valueOf(specialEventCount));
        }
    }
    
    /**
     * Select and display featured badge
     */
    private void selectFeaturedBadge() {
        // Choose a badge that's in progress but not yet earned
        Optional<Badge> potentialFeatured = inProgressBadges.stream()
            .filter(b -> b.getProgress() > 0 && b.getProgress() < 100)
            .max(Comparator.comparing(Badge::getProgress));
        
        if (potentialFeatured.isPresent()) {
            featuredBadge = potentialFeatured.get();
            updateFeaturedBadgeUI();
        } else if (!lockedBadges.isEmpty()) {
            // Fallback to a locked badge
            featuredBadge = lockedBadges.get(0);
            updateFeaturedBadgeUI();
        } else {
            // No badges available
            featuredBadge = null;
            if (featuredBadgeContainer != null) {
                featuredBadgeContainer.setVisible(false);
            }
        }
    }
    
    /**
     * Update featured badge UI
     */
    private void updateFeaturedBadgeUI() {
        if (featuredBadge == null || featuredBadgeContainer == null) return;
        
        featuredBadgeContainer.setVisible(true);
        
        if (featuredBadgeIcon != null) {
            featuredBadgeIcon.setText(featuredBadge.getIcon());
        }
        
        if (featuredBadgeTitle != null) {
            featuredBadgeTitle.setText(featuredBadge.getTitle());
        }
        
        if (featuredBadgeDescription != null) {
            featuredBadgeDescription.setText(featuredBadge.getDescription());
        }
        
        if (featuredBadgeProgress != null) {
            featuredBadgeProgress.setProgress(featuredBadge.getProgress() / 100.0);
        }
        
        if (featuredBadgeProgressText != null) {
            featuredBadgeProgressText.setText(
                featuredBadge.getCurrentValue() + " / " + featuredBadge.getTargetValue() + 
                " (" + featuredBadge.getProgress() + "%)"
            );
        }
    }
    
    /**
     * Load recent activity
     */
    private void loadRecentActivity() {
        if (recentActivityContainer == null) return;
        
        recentActivityContainer.getChildren().clear();
        
        // Get recently earned badges (last 5)
        List<Badge> recentBadges = earnedBadges.stream()
            .filter(b -> b.getEarnedDate() != null)
            .sorted(Comparator.comparing(Badge::getEarnedDate).reversed())
            .limit(5)
            .collect(Collectors.toList());
        
        if (recentBadges.isEmpty()) {
            Label noActivityLabel = new Label("No recent activity");
            noActivityLabel.getStyleClass().add("recent-activity-empty");
            recentActivityContainer.getChildren().add(noActivityLabel);
            return;
        }
        
        for (Badge badge : recentBadges) {
            HBox activityItem = new HBox(10);
            activityItem.setAlignment(Pos.CENTER_LEFT);
            activityItem.setPadding(new Insets(8));
            
            Label iconLabel = new Label(badge.getIcon());
            iconLabel.setStyle("-fx-font-size: 20px;");
            
            VBox content = new VBox(2);
            Label titleLabel = new Label("Earned: " + badge.getTitle());
            titleLabel.getStyleClass().add("recent-activity-title");
            
            Label dateLabel = new Label(badge.getEarnedDate().toLocalDate().toString());
            dateLabel.getStyleClass().add("recent-activity-date");
            
            content.getChildren().addAll(titleLabel, dateLabel);
            activityItem.getChildren().addAll(iconLabel, content);
            
            recentActivityContainer.getChildren().add(activityItem);
        }
    }
    
    /**
     * Apply filters to badges
     */
    private void applyFilters() {
        if (categoryFilterComboBox != null) {
            currentCategory = categoryFilterComboBox.getValue();
        }
        
        if (statusFilterComboBox != null) {
            currentStatus = statusFilterComboBox.getValue();
        }
        
        if (sortByComboBox != null) {
            currentSortBy = sortByComboBox.getValue();
        }
        
        filterAndSortBadges();
        displayBadges();
    }
    
    /**
     * Clear all filters
     */
    private void clearFilters() {
        if (categoryFilterComboBox != null) {
            categoryFilterComboBox.getSelectionModel().selectFirst();
        }
        
        if (statusFilterComboBox != null) {
            statusFilterComboBox.getSelectionModel().selectFirst();
        }
        
        if (sortByComboBox != null) {
            sortByComboBox.getSelectionModel().selectFirst();
        }
        
        if (searchField != null) {
            searchField.clear();
        }
        
        currentCategory = "All";
        currentStatus = "All";
        currentSortBy = "Date Earned";
        searchQuery = "";
        
        filteredBadges.setAll(allBadges);
        displayBadges();
    }
    
    /**
     * Perform search
     */
    private void performSearch() {
        if (searchField != null) {
            searchQuery = searchField.getText().toLowerCase().trim();
            filterAndSortBadges();
            displayBadges();
        }
    }
    
    /**
     * Clear search
     */
    private void clearSearch() {
        searchQuery = "";
        filterAndSortBadges();
        displayBadges();
    }
    
    /**
     * Filter and sort badges based on current criteria
     */
    private void filterAndSortBadges() {
        // Filter by category
        List<Badge> filtered = allBadges.stream()
            .filter(badge -> currentCategory.equals("All") || badge.getCategory().equals(currentCategory))
            .filter(badge -> {
                if (currentStatus.equals("All")) return true;
                if (currentStatus.equals("Earned")) return badge.isEarned();
                if (currentStatus.equals("In Progress")) return !badge.isEarned() && badge.getProgress() > 0;
                if (currentStatus.equals("Locked")) return !badge.isEarned() && badge.getProgress() == 0;
                return true;
            })
            .filter(badge -> searchQuery.isEmpty() || 
                badge.getTitle().toLowerCase().contains(searchQuery) ||
                badge.getDescription().toLowerCase().contains(searchQuery) ||
                badge.getCategory().toLowerCase().contains(searchQuery))
            .collect(Collectors.toList());
        
        // Sort
        Comparator<Badge> comparator;
        switch (currentSortBy) {
            case "Progress":
                comparator = Comparator.comparingInt(Badge::getProgress).reversed();
                break;
            case "Difficulty":
                comparator = Comparator.comparing(Badge::getDifficulty);
                break;
            case "Points":
                comparator = Comparator.comparingInt(Badge::getPoints).reversed();
                break;
            case "Title":
                comparator = Comparator.comparing(Badge::getTitle);
                break;
            case "Date Earned":
            default:
                comparator = (b1, b2) -> {
                    if (b1.isEarned() && b2.isEarned()) {
                        return b2.getEarnedDate().compareTo(b1.getEarnedDate());
                    } else if (b1.isEarned()) {
                        return -1;
                    } else if (b2.isEarned()) {
                        return 1;
                    } else {
                        return b1.getTitle().compareTo(b2.getTitle());
                    }
                };
                break;
        }
        
        filtered.sort(comparator);
        filteredBadges.setAll(filtered);
    }
    
    /**
     * Focus on a specific badge (set as featured)
     */
    private void focusOnBadge() {
        if (featuredBadge != null) {
            // Navigate to activities related to this badge
            showInfo("Focus Mode", "Now focusing on: " + featuredBadge.getTitle());
            // In a real implementation, this would filter activities related to the badge
        }
    }
    
    /**
     * Focus on a specific badge
     */
    private void focusOnBadge(Badge badge) {
        featuredBadge = badge;
        updateFeaturedBadgeUI();
        showInfo("Focus Mode", "Now focusing on: " + badge.getTitle());
        // In a real implementation, this would filter activities related to the badge
    }
    
 
	/**
     * Show achievement celebration
     */
    public void celebrateAchievement(Badge badge) {
        if (celebrationContainer == null) return;
        
        celebrationContainer.setVisible(true);
         
        VBox celebrationContent = new VBox(20);
        celebrationContent.setAlignment(Pos.CENTER);
        celebrationContent.setPadding(new Insets(30));
        
        // Animated badge icon
        Label celebrationIcon = new Label(badge.getIcon());
        celebrationIcon.setStyle("-fx-font-size: 96px;");
        
        // Celebration text
        Label congratsLabel = new Label("Congratulations!");
        congratsLabel.getStyleClass().add("celebration-title");
        
        Label badgeLabel = new Label("You earned: " + badge.getTitle());
        badgeLabel.getStyleClass().add("celebration-badge");
        
        Label pointsLabel = new Label(badge.getPoints() + " points earned!");
        pointsLabel.getStyleClass().add("celebration-points");
        
        celebrationContent.getChildren().addAll(celebrationIcon, congratsLabel, badgeLabel, pointsLabel);
        
        celebrationContainer.getChildren().setAll(celebrationContent);
        
        // Create celebration animation
        Timeline celebration = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(celebrationIcon.scaleXProperty(), 0.5),
                new KeyValue(celebrationIcon.scaleYProperty(), 0.5),
                new KeyValue(celebrationIcon.rotateProperty(), -10)
            ),
            new KeyFrame(Duration.millis(500),
                new KeyValue(celebrationIcon.scaleXProperty(), 1.2),
                new KeyValue(celebrationIcon.scaleYProperty(), 1.2),
                new KeyValue(celebrationIcon.rotateProperty(), 10)
            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(celebrationIcon.scaleXProperty(), 1.0),
                new KeyValue(celebrationIcon.scaleYProperty(), 1.0),
                new KeyValue(celebrationIcon.rotateProperty(), 0)
            )
        );
        
        celebration.setCycleCount(3);
        celebration.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> celebrationContainer.setVisible(false));
            pause.play();
        });
        
        celebration.play();
    }
    
    /**
     * Refresh badges data
     */
    public void refreshData() {
        loadBadges();
    }
    
    @FXML
    private void handleRefresh() {
        refreshData();
    }
    
    @FXML
    private void handleBack() {
        navigateTo("HOME_VIEW");
    }
}