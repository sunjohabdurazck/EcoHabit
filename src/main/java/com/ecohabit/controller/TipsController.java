package main.java.com.ecohabit.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
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
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import main.java.com.ecohabit.SessionManager;
import main.java.com.ecohabit.model.EcoTip;
import main.java.com.ecohabit.model.User;
import main.java.com.ecohabit.service.TipsService;
import main.java.com.ecohabit.service.UserService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the Eco Tips screen
 * Provides daily tips, categories, and personalized recommendations
 */
public class TipsController extends BaseController {
    
    // Header Components
    @FXML private Label todaysTipLabel;
    @FXML private Label tipOfDayContent;
    @FXML private Button refreshTipBtn;
    @FXML private Button shareTipBtn;
    @FXML private Button favoriteTipBtn;
    
    // Category Filter
    @FXML private ComboBox<String> categoryFilterComboBox;
    @FXML private ComboBox<String> difficultyFilterComboBox;
    @FXML private ComboBox<String> sortByComboBox;
    @FXML private Button applyFiltersBtn;
    @FXML private Button clearFiltersBtn;
    
    // Search
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    
    // Tips Display
    @FXML private ScrollPane tipsScrollPane;
    @FXML private VBox tipsContainer;
    @FXML private VBox featuredTipsContainer;
    
    // Sidebar - Quick Categories
    @FXML private VBox quickCategoriesContainer;
    @FXML private Button allTipsBtn;
    @FXML private Button energyTipsBtn;
    @FXML private Button transportTipsBtn;
    @FXML private Button foodTipsBtn;
    @FXML private Button wasteTipsBtn;
    @FXML private Button waterTipsBtn;
    @FXML private Button homeTipsBtn;
    
    // Statistics
    @FXML private Label totalTipsLabel;
    @FXML private Label readTipsLabel;
    @FXML private Label favoriteTipsLabel;
    @FXML private Label streakLabel;
    
    // Personal Recommendations
    @FXML private VBox personalRecommendationsContainer;
    @FXML private Button refreshRecommendationsBtn;
    
    // Pagination
    @FXML private Button prevPageBtn;
    @FXML private Label pageLabel;
    @FXML private Button nextPageBtn;
    @FXML private ComboBox<Integer> itemsPerPageComboBox;
    
    // Services and Data
    private TipsService tipsService;
    private UserService userService;
    private User currentUser;
    private ObservableList<EcoTip> allTips;
    private ObservableList<EcoTip> filteredTips;
    private ObservableList<EcoTip> favoriteTips;
    
    // State Management
    private String currentCategory = "All";
    private String currentDifficulty = "All";
    private String currentSortBy = "Date";
    private String searchQuery = "";
    private int currentPage = 1;
    private int itemsPerPage = 12;
    private int totalPages = 1;
    private EcoTip currentTipOfDay;
    
    @Override
    public void initializeScreen() {
        try {
            // Initialize services
            initializeServices();
            
            // Setup UI components
            setupComboBoxes();
            setupEventHandlers();
            
            // Load data
            loadUserData();
            loadTips();
            loadTipOfDay();
            loadPersonalRecommendations();
            
            // Update statistics
            updateStatistics();
            
            // Set initial category
            setActiveCategory("All");
            
            System.out.println("Tips screen initialized successfully");
            
        } catch (Exception e) {
            System.err.println("Error initializing tips screen: " + e.getMessage());
            e.printStackTrace();
            showError("Initialization Error", "Failed to initialize tips: " + e.getMessage());
        }
    }
    
    /**
     * Initialize services
     */
    private void initializeServices() {
        try {
            tipsService = new TipsService();
            userService = new UserService();
            allTips = FXCollections.observableArrayList();
            filteredTips = FXCollections.observableArrayList();
            favoriteTips = FXCollections.observableArrayList();
            
        } catch (Exception e) {
            System.err.println("Error initializing services: " + e.getMessage());
            createMockData();
        }
    }
    
    /**
     * Create mock data for demonstration
     */
    private void createMockData() {
        allTips = FXCollections.observableArrayList();
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setFirstName("John");
        currentUser.setLastName("Doe");
        
        // Create sample tips
        allTips.addAll(Arrays.asList(
            createSampleTip("Switch to LED Bulbs", "Energy", "Easy", 
                "Replace incandescent bulbs with LED bulbs to reduce energy consumption by up to 75%.", 
                4.5, "💡"),
            createSampleTip("Start Composting", "Waste", "Medium", 
                "Turn kitchen scraps into nutrient-rich compost for your garden and reduce waste.", 
                4.2, "🌱"),
            createSampleTip("Use Public Transport", "Transportation", "Easy", 
                "Take buses, trains, or subways instead of driving to significantly reduce carbon emissions.", 
                4.7, "🚌"),
            createSampleTip("Grow Your Own Herbs", "Food", "Medium", 
                "Start a small herb garden on your windowsill to have fresh herbs and reduce packaging waste.", 
                4.3, "🌿"),
            createSampleTip("Install Water-Saving Showerheads", "Water", "Easy", 
                "Low-flow showerheads can reduce water usage by up to 40% without sacrificing pressure.", 
                4.1, "🚿"),
            createSampleTip("Use Reusable Bags", "Shopping", "Easy", 
                "Bring reusable bags when shopping to eliminate single-use plastic bags.", 
                4.8, "🛍️"),
            createSampleTip("Unplug Electronics", "Energy", "Easy", 
                "Unplug devices when not in use to prevent phantom energy consumption.", 
                4.0, "🔌"),
            createSampleTip("Collect Rainwater", "Water", "Hard", 
                "Set up a rainwater collection system for watering plants and reducing water bills.", 
                4.4, "☔"),
            createSampleTip("Meal Planning", "Food", "Medium", 
                "Plan your meals to reduce food waste and make more sustainable food choices.", 
                4.6, "📝"),
            createSampleTip("DIY Natural Cleaners", "Home", "Medium", 
                "Make eco-friendly cleaning products using vinegar, baking soda, and essential oils.", 
                4.2, "🧽")
        ));
        
        filteredTips = FXCollections.observableArrayList(allTips);
        
        // Set tip of the day
        currentTipOfDay = allTips.get(new Random().nextInt(allTips.size()));
    }
    
    /**
     * Create sample tip for demo
     */
    private EcoTip createSampleTip(String title, String category, String difficulty, 
                                  String description, double rating, String icon) {
        EcoTip tip = new EcoTip();
        tip.setId(UUID.randomUUID().hashCode());
        tip.setTitle(title);
        tip.setCategory(category);
        tip.setDifficulty(difficulty);
        tip.setDescription(description);
        tip.setRating(rating);
        tip.setIcon(icon);
        tip.setDateCreated(LocalDate.now().minusDays(new Random().nextInt(30)));
        tip.setReadCount(new Random().nextInt(1000));
        tip.setLikeCount(new Random().nextInt(500));
        tip.setEstimatedCO2Savings(Math.random() * 10 + 1);
        return tip;
    }
    
    /**
     * Setup combo boxes
     */
    private void setupComboBoxes() {
        // Category filter
        if (categoryFilterComboBox != null) {
            categoryFilterComboBox.setItems(FXCollections.observableArrayList(
                "All", "Energy", "Transportation", "Food", "Water", "Waste", "Home", "Shopping"
            ));
            categoryFilterComboBox.getSelectionModel().selectFirst();
        }
        
        // Difficulty filter
        if (difficultyFilterComboBox != null) {
            difficultyFilterComboBox.setItems(FXCollections.observableArrayList(
                "All", "Easy", "Medium", "Hard"
            ));
            difficultyFilterComboBox.getSelectionModel().selectFirst();
        }
        
        // Sort by
        if (sortByComboBox != null) {
            sortByComboBox.setItems(FXCollections.observableArrayList(
                "Date", "Rating", "Popularity", "Title", "Difficulty"
            ));
            sortByComboBox.getSelectionModel().selectFirst();
        }
        
        // Items per page
        if (itemsPerPageComboBox != null) {
            itemsPerPageComboBox.setItems(FXCollections.observableArrayList(6, 12, 18, 24));
            itemsPerPageComboBox.setValue(12);
        }
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Tip of the day controls
        if (refreshTipBtn != null) {
            refreshTipBtn.setOnAction(e -> loadNewTipOfDay());
        }
        if (shareTipBtn != null) {
            shareTipBtn.setOnAction(e -> shareTipOfDay());
        }
        if (favoriteTipBtn != null) {
            favoriteTipBtn.setOnAction(e -> toggleFavoriteTipOfDay());
        }
        
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
        
        // Category buttons
        if (allTipsBtn != null) {
            allTipsBtn.setOnAction(e -> setActiveCategory("All"));
        }
        if (energyTipsBtn != null) {
            energyTipsBtn.setOnAction(e -> setActiveCategory("Energy"));
        }
        if (transportTipsBtn != null) {
            transportTipsBtn.setOnAction(e -> setActiveCategory("Transportation"));
        }
        if (foodTipsBtn != null) {
            foodTipsBtn.setOnAction(e -> setActiveCategory("Food"));
        }
        if (wasteTipsBtn != null) {
            wasteTipsBtn.setOnAction(e -> setActiveCategory("Waste"));
        }
        if (waterTipsBtn != null) {
            waterTipsBtn.setOnAction(e -> setActiveCategory("Water"));
        }
        if (homeTipsBtn != null) {
            homeTipsBtn.setOnAction(e -> setActiveCategory("Home"));
        }
        
        // Recommendations
        if (refreshRecommendationsBtn != null) {
            refreshRecommendationsBtn.setOnAction(e -> loadPersonalRecommendations());
        }
        
        // Pagination
        if (prevPageBtn != null) {
            prevPageBtn.setOnAction(e -> goToPreviousPage());
        }
        if (nextPageBtn != null) {
            nextPageBtn.setOnAction(e -> goToNextPage());
        }
        if (itemsPerPageComboBox != null) {
            itemsPerPageComboBox.setOnAction(e -> {
                itemsPerPage = itemsPerPageComboBox.getValue();
                currentPage = 1;
                displayTips();
            });
        }
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
     * Load tips from service
     */
    private void loadTips() {
        Task<List<EcoTip>> loadTask = new Task<List<EcoTip>>() {
            @Override
            protected List<EcoTip> call() throws Exception {
                if (tipsService != null) {
                    return tipsService.getAllTips();
                } else {
                    return new ArrayList<>(allTips);
                }
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    List<EcoTip> tips = getValue();
                    allTips.setAll(tips);
                    filteredTips.setAll(tips);
                    applyFilters();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("Failed to load tips: " + getException().getMessage());
                    // Use mock data as fallback
                    if (allTips.isEmpty()) {
                        createMockData();
                        applyFilters();
                    }
                });
            }
        };
        
        new Thread(loadTask).start();
    }
    
    /**
     * Load tip of the day
     */
    private void loadTipOfDay() {
        try {
            if (tipsService != null && currentUser != null) {
                currentTipOfDay = tipsService.getTipOfDay(currentUser.getId());
            } else if (!allTips.isEmpty()) {
                currentTipOfDay = allTips.get(new Random().nextInt(allTips.size()));
            }
            
            displayTipOfDay();
            
        } catch (Exception e) {
            System.err.println("Error loading tip of the day: " + e.getMessage());
            if (!allTips.isEmpty()) {
                currentTipOfDay = allTips.get(0);
                displayTipOfDay();
            }
        }
    }
    
    /**
     * Display tip of the day
     */
    private void displayTipOfDay() {
        if (currentTipOfDay != null) {
            if (todaysTipLabel != null) {
                todaysTipLabel.setText("💡 Today's Eco Tip");
            }
            if (tipOfDayContent != null) {
                tipOfDayContent.setText(currentTipOfDay.getTitle() + "\n\n" + 
                                      currentTipOfDay.getDescription());
                tipOfDayContent.setWrapText(true);
            }
            
            // Animate tip display
            animateTipOfDay();
        }
    }
    
    /**
     * Animate tip of the day display
     */
    private void animateTipOfDay() {
        if (tipOfDayContent != null) {
            FadeTransition fade = new FadeTransition(Duration.seconds(1), tipOfDayContent);
            fade.setFromValue(0);
            fade.setToValue(1);
            
            ScaleTransition scale = new ScaleTransition(Duration.seconds(1), tipOfDayContent);
            scale.setFromX(0.9);
            scale.setFromY(0.9);
            scale.setToX(1.0);
            scale.setToY(1.0);
            
            fade.play();
            scale.play();
        }
    }
    
    /**
     * Load new tip of the day
     */
    private void loadNewTipOfDay() {
        if (!allTips.isEmpty()) {
            EcoTip newTip;
            do {
                newTip = allTips.get(new Random().nextInt(allTips.size()));
            } while (newTip.equals(currentTipOfDay) && allTips.size() > 1);
            
            currentTipOfDay = newTip;
            displayTipOfDay();
            
            showNotification("New tip loaded!", "success");
        }
    }
    
    /**
     * Share tip of the day
     */
    private void shareTipOfDay() {
        if (currentTipOfDay != null) {
            String shareText = "💡 Eco Tip: " + currentTipOfDay.getTitle() + "\n\n" +
                              currentTipOfDay.getDescription() + "\n\n#EcoHabit #Sustainability";
            
            // In a real app, this would integrate with sharing platforms
            showSuccess("Share", "Tip copied to clipboard!\n\n" + shareText);
        }
    }
    
    /**
     * Toggle favorite status for tip of the day
     */
    private void toggleFavoriteTipOfDay() {
        if (currentTipOfDay != null) {
            boolean isFavorite = favoriteTips.contains(currentTipOfDay);
            
            if (isFavorite) {
                favoriteTips.remove(currentTipOfDay);
                showNotification("Removed from favorites", "info");
            } else {
                favoriteTips.add(currentTipOfDay);
                showNotification("Added to favorites", "success");
            }
            
            // Update button appearance
            updateFavoriteButton(!isFavorite);
            updateStatistics();
        }
    }
    
    /**
     * Update favorite button appearance
     */
    private void updateFavoriteButton(boolean isFavorite) {
        if (favoriteTipBtn != null) {
            favoriteTipBtn.setText(isFavorite ? "❤️ Favorited" : "🤍 Favorite");
            favoriteTipBtn.getStyleClass().removeAll("favorite-active", "favorite-inactive");
            favoriteTipBtn.getStyleClass().add(isFavorite ? "favorite-active" : "favorite-inactive");
        }
    }
    
    /**
     * Set active category
     */
    private void setActiveCategory(String category) {
        currentCategory = category;
        
        // Update button states
        Button[] categoryButtons = {allTipsBtn, energyTipsBtn, transportTipsBtn, 
                                   foodTipsBtn, wasteTipsBtn, waterTipsBtn, homeTipsBtn};
        
        for (Button btn : categoryButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("active");
            }
        }
        
        // Set active button
        Button activeBtn = null;
        switch (category) {
            case "All": activeBtn = allTipsBtn; break;
            case "Energy": activeBtn = energyTipsBtn; break;
            case "Transportation": activeBtn = transportTipsBtn; break;
            case "Food": activeBtn = foodTipsBtn; break;
            case "Waste": activeBtn = wasteTipsBtn; break;
            case "Water": activeBtn = waterTipsBtn; break;
            case "Home": activeBtn = homeTipsBtn; break;
        }
        
        if (activeBtn != null) {
            activeBtn.getStyleClass().add("active");
        }
        
        // Update category filter combo box
        if (categoryFilterComboBox != null) {
            categoryFilterComboBox.setValue(category);
        }
        
        // Apply filters
        applyFilters();
    }
    
    /**
     * Apply filters to tips
     */
    private void applyFilters() {
        filteredTips.setAll(allTips);
        
        // Category filter
        String selectedCategory = categoryFilterComboBox != null ? 
            categoryFilterComboBox.getValue() : currentCategory;
        if (selectedCategory != null && !"All".equals(selectedCategory)) {
            filteredTips = filteredTips.filtered(tip -> 
                selectedCategory.equals(tip.getCategory()));
        }
        
        // Difficulty filter
        String selectedDifficulty = difficultyFilterComboBox != null ? 
            difficultyFilterComboBox.getValue() : "All";
        if (selectedDifficulty != null && !"All".equals(selectedDifficulty)) {
            filteredTips = filteredTips.filtered(tip -> 
                selectedDifficulty.equals(tip.getDifficulty()));
        }
        
        // Search filter
        if (searchQuery != null && !searchQuery.isEmpty()) {
            String query = searchQuery.toLowerCase();
            filteredTips = filteredTips.filtered(tip -> 
                tip.getTitle().toLowerCase().contains(query) ||
                tip.getDescription().toLowerCase().contains(query) ||
                tip.getCategory().toLowerCase().contains(query));
        }
        
        // Sort tips
        sortTips();
        
        // Reset to first page and display
        currentPage = 1;
        updatePagination();
        displayTips();
        
        showNotification("Filters applied - " + filteredTips.size() + " tips found", "info");
    }
    
    /**
     * Sort tips based on selected criteria
     */
    private void sortTips() {
        String sortBy = sortByComboBox != null ? sortByComboBox.getValue() : "Date";
        
        if (sortBy != null) {
            switch (sortBy) {
                case "Date":
                    filteredTips.sort((t1, t2) -> t2.getDateCreated().compareTo(t1.getDateCreated()));
                    break;
                case "Rating":
                    filteredTips.sort((t1, t2) -> Double.compare(t2.getRating(), t1.getRating()));
                    break;
                case "Popularity":
                    filteredTips.sort((t1, t2) -> Integer.compare(t2.getReadCount(), t1.getReadCount()));
                    break;
                case "Title":
                    filteredTips.sort(Comparator.comparing(EcoTip::getTitle));
                    break;
                case "Difficulty":
                    filteredTips.sort(Comparator.comparing(tip -> getDifficultyOrder(tip.getDifficulty())));
                    break;
            }
        }
    }
    
    /**
     * Get difficulty order for sorting
     */
    private int getDifficultyOrder(String difficulty) {
        switch (difficulty) {
            case "Easy": return 1;
            case "Medium": return 2;
            case "Hard": return 3;
            default: return 0;
        }
    }
    
    /**
     * Clear all filters
     */
    private void clearFilters() {
        if (categoryFilterComboBox != null) {
            categoryFilterComboBox.getSelectionModel().selectFirst();
        }
        if (difficultyFilterComboBox != null) {
            difficultyFilterComboBox.getSelectionModel().selectFirst();
        }
        if (sortByComboBox != null) {
            sortByComboBox.getSelectionModel().selectFirst();
        }
        if (searchField != null) {
            searchField.clear();
        }
        
        searchQuery = "";
        currentCategory = "All";
        setActiveCategory("All");
        
        showNotification("Filters cleared", "info");
    }
    
    /**
     * Perform search
     */
    private void performSearch() {
        String query = searchField != null ? searchField.getText().trim() : "";
        searchQuery = query;
        applyFilters();
    }
    
    /**
     * Clear search
     */
    private void clearSearch() {
        searchQuery = "";
        applyFilters();
    }
    
    /**
     * Display tips with pagination
     */
    private void displayTips() {
        if (tipsContainer != null) {
            tipsContainer.getChildren().clear();
            
            if (filteredTips.isEmpty()) {
                showEmptyState();
                return;
            }
            
            // Calculate pagination bounds
            int startIndex = (currentPage - 1) * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, filteredTips.size());
            
            // Create grid layout for tips
            GridPane tipsGrid = new GridPane();
            tipsGrid.setHgap(15);
            tipsGrid.setVgap(15);
            tipsGrid.setPadding(new Insets(10));
            
            int columns = 3; // 3 tips per row
            int row = 0, col = 0;
            
            for (int i = startIndex; i < endIndex; i++) {
                EcoTip tip = filteredTips.get(i);
                VBox tipCard = createTipCard(tip);
                
                tipsGrid.add(tipCard, col, row);
                
                col++;
                if (col >= columns) {
                    col = 0;
                    row++;
                }
                
                // Animate card entrance
                animateTipCardEntrance(tipCard, i - startIndex);
            }
            
            tipsContainer.getChildren().add(tipsGrid);
        }
    }
    
    /**
     * Show empty state when no tips found
     */
    private void showEmptyState() {
        if (tipsContainer != null) {
            VBox emptyState = new VBox(20);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(50));
            
            Label emptyLabel = new Label("🔍 No tips found");
            emptyLabel.getStyleClass().add("empty-state-title");
            
            Label emptyMessage = new Label("Try adjusting your filters or search terms");
            emptyMessage.getStyleClass().add("empty-state-message");
            
            Button clearFiltersBtn = new Button("Clear Filters");
            clearFiltersBtn.setOnAction(e -> clearFilters());
            
            emptyState.getChildren().addAll(emptyLabel, emptyMessage, clearFiltersBtn);
            tipsContainer.getChildren().add(emptyState);
        }
    }
    
    /**
     * Create tip card UI component
     */
    private VBox createTipCard(EcoTip tip) {
        VBox card = new VBox(10);
        card.getStyleClass().add("tip-card");
        card.setPadding(new Insets(15));
        card.setPrefWidth(300);
        card.setMinHeight(200);
        
        // Header with icon and category
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(tip.getIcon());
        iconLabel.getStyleClass().add("tip-icon");
        
        Label categoryLabel = new Label(tip.getCategory());
        categoryLabel.getStyleClass().add("tip-category");
        
        Circle difficultyDot = new Circle(5);
        difficultyDot.setFill(getDifficultyColor(tip.getDifficulty()));
        
        header.getChildren().addAll(iconLabel, categoryLabel, difficultyDot);
        
        // Title
        Label titleLabel = new Label(tip.getTitle());
        titleLabel.getStyleClass().add("tip-title");
        titleLabel.setWrapText(true);
        
        // Description
        Label descLabel = new Label(tip.getDescription());
        descLabel.getStyleClass().add("tip-description");
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(60);
        descLabel.setTextAlignment(TextAlignment.LEFT);
        
        // Stats row
        HBox statsRow = new HBox(15);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        
        Label ratingLabel = new Label("⭐ " + String.format("%.1f", tip.getRating()));
        Label readCountLabel = new Label("👁 " + tip.getReadCount());
        Label co2Label = new Label("🌱 " + String.format("%.1f kg", tip.getEstimatedCO2Savings()));
        
        statsRow.getChildren().addAll(ratingLabel, readCountLabel, co2Label);
        
        // Action buttons
        HBox actionRow = new HBox(10);
        actionRow.setAlignment(Pos.CENTER);
        
        Button readBtn = new Button("Read More");
        Button favoriteBtn = new Button(favoriteTips.contains(tip) ? "❤️" : "🤍");
        Button shareBtn = new Button("📤");
        
        readBtn.setOnAction(e -> readTip(tip));
        favoriteBtn.setOnAction(e -> toggleFavorite(tip, favoriteBtn));
        shareBtn.setOnAction(e -> shareTip(tip));
        
        actionRow.getChildren().addAll(readBtn, favoriteBtn, shareBtn);
        
        card.getChildren().addAll(header, titleLabel, descLabel, statsRow, actionRow);
        
        // Add hover effect
        addHoverEffect(card);
        
        return card;
    }
    
    /**
     * Get color for difficulty level
     */
    private Color getDifficultyColor(String difficulty) {
        switch (difficulty) {
            case "Easy": return Color.GREEN;
            case "Medium": return Color.ORANGE;
            case "Hard": return Color.RED;
            default: return Color.GRAY;
        }
    }
    
    /**
     * Add hover effect to tip card
     */
    private void addHoverEffect(VBox card) {
        card.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });
        
        card.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }
    
    /**
     * Animate tip card entrance
     */
    private void animateTipCardEntrance(VBox card, int index) {
        card.setOpacity(0);
        card.setTranslateY(30);
        
        FadeTransition fade = new FadeTransition(Duration.millis(500), card);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(index * 100));
        
        Timeline slide = new Timeline(
            new KeyFrame(Duration.millis(500), e -> card.setTranslateY(0))
        );
        slide.setDelay(Duration.millis(index * 100));
        
        fade.play();
        slide.play();
    }
    
    /**
     * Read tip (show detailed view)
     */
    private void readTip(EcoTip tip) {
        // In a real app, this would show a detailed view dialog
        showSuccess("Tip Details", 
                   tip.getTitle() + "\n\n" + 
                   tip.getDescription() + "\n\n" +
                   "Category: " + tip.getCategory() + "\n" +
                   "Difficulty: " + tip.getDifficulty() + "\n" +
                   "Estimated CO₂ Savings: " + String.format("%.1f kg", tip.getEstimatedCO2Savings()));
        
        // Mark as read
        tip.setReadCount(tip.getReadCount() + 1);
    }
    
    /**
     * Toggle favorite status
     */
    private void toggleFavorite(EcoTip tip, Button favoriteBtn) {
        boolean isFavorite = favoriteTips.contains(tip);
        
        if (isFavorite) {
            favoriteTips.remove(tip);
            favoriteBtn.setText("🤍");
            showNotification("Removed from favorites", "info");
        } else {
            favoriteTips.add(tip);
            favoriteBtn.setText("❤️");
            showNotification("Added to favorites", "success");
        }
        
        updateStatistics();
    }
    
    /**
     * Share tip
     */
    private void shareTip(EcoTip tip) {
        String shareText = tip.getIcon() + " " + tip.getTitle() + "\n\n" +
                          tip.getDescription() + "\n\n#EcoHabit #" + tip.getCategory();
        
        showSuccess("Share Tip", "Tip shared!\n\n" + shareText);
    }
    
    /**
     * Load personal recommendations
     */
    private void loadPersonalRecommendations() {
        if (personalRecommendationsContainer != null) {
            personalRecommendationsContainer.getChildren().clear();
            
            // Get personalized tips based on user activity
            List<EcoTip> recommendations = getPersonalizedRecommendations();
            
            for (EcoTip tip : recommendations.subList(0, Math.min(3, recommendations.size()))) {
                HBox recommendationCard = createRecommendationCard(tip);
                personalRecommendationsContainer.getChildren().add(recommendationCard);
            }
        }
    }
    
    /**
     * Get personalized recommendations
     */
    private List<EcoTip> getPersonalizedRecommendations() {
        // In a real app, this would analyze user behavior and preferences
        return allTips.stream()
                     .sorted((t1, t2) -> Double.compare(t2.getRating(), t1.getRating()))
                     .limit(5)
                     .collect(Collectors.toList());
    }
    
    /**
     * Create recommendation card
     */
    private HBox createRecommendationCard(EcoTip tip) {
        HBox card = new HBox(10);
        card.getStyleClass().add("recommendation-card");
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(tip.getIcon());
        iconLabel.getStyleClass().add("recommendation-icon");
        
        VBox content = new VBox(5);
        Label titleLabel = new Label(tip.getTitle());
        titleLabel.getStyleClass().add("recommendation-title");
        
        Label descLabel = new Label(tip.getDescription().substring(0, Math.min(80, tip.getDescription().length())) + "...");
        descLabel.getStyleClass().add("recommendation-description");
        descLabel.setWrapText(true);
        
        content.getChildren().addAll(titleLabel, descLabel);
        
        Button tryBtn = new Button("Try Now");
        tryBtn.setOnAction(e -> readTip(tip));
        
        card.getChildren().addAll(iconLabel, content, tryBtn);
        return card;
    }
    
    /**
     * Update pagination controls
     */
    private void updatePagination() {
        totalPages = (int) Math.ceil((double) filteredTips.size() / itemsPerPage);
        
        if (totalPages == 0) {
            totalPages = 1;
        }
        
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        
        if (pageLabel != null) {
            pageLabel.setText(currentPage + " / " + totalPages);
        }
        
        if (prevPageBtn != null) {
            prevPageBtn.setDisable(currentPage <= 1);
        }
        
        if (nextPageBtn != null) {
            nextPageBtn.setDisable(currentPage >= totalPages);
        }
    }
    
    /**
     * Go to previous page
     */
    private void goToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            displayTips();
        }
    }
    
    /**
     * Go to next page
     */
    private void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            displayTips();
        }
    }
    
    /**
     * Update statistics display
     */
    private void updateStatistics() {
        if (totalTipsLabel != null) {
            totalTipsLabel.setText(String.valueOf(allTips.size()));
        }
        
        if (readTipsLabel != null) {
            long readTips = allTips.stream().mapToLong(EcoTip::getReadCount).sum();
            readTipsLabel.setText(String.valueOf(readTips));
        }
        
        if (favoriteTipsLabel != null) {
            favoriteTipsLabel.setText(String.valueOf(favoriteTips.size()));
        }
        
        if (streakLabel != null) {
            // In a real app, this would track daily tip reading streak
            streakLabel.setText("7 days");
        }
    }
    
    /**
     * Show notification
     */
    private void showNotification(String message, String type) {
        System.out.println(type.toUpperCase() + ": " + message);
        // Implementation would show actual notification to user
    }
    
    /**
     * Show error dialog
     */

    
    @Override
    public void onScreenActivated() {
        super.onScreenActivated();
        // Refresh recommendations when screen becomes active
        loadPersonalRecommendations();
        updateStatistics();
    }
    
    @Override
    public void refreshScreen() {
        loadTips();
        loadTipOfDay();
        loadPersonalRecommendations();
        updateStatistics();
    }
}