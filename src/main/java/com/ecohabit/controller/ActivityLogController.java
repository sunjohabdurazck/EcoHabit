package main.java.com.ecohabit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.geometry.Insets;
import main.java.com.ecohabit.SessionManager;
import main.java.com.ecohabit.model.Activity;
import main.java.com.ecohabit.model.User;
import main.java.com.ecohabit.service.ActivityService;
import main.java.com.ecohabit.service.NotificationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the Activity Log screen
 * Handles activity logging, filtering, and display functionality
 */
public class ActivityLogController extends BaseController {
    
    // Sidebar - Daily Summary
    @FXML private Label dailyCO2Label;
    @FXML private Label dailyEquivalentLabel;
    @FXML private Label dailyActivitiesLabel;
    @FXML private Label currentStreakLabel;
    
    // Sidebar - Filter Buttons
    @FXML private Button todayBtn;
    @FXML private Button yesterdayBtn;
    @FXML private Button thisWeekBtn;
    @FXML private Button customDateBtn;
    
    // Sidebar - Filter Options
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> categoryFilterComboBox;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Button applyFiltersBtn;
    @FXML private Button clearFiltersBtn;
    
    // Sidebar - Quick Actions
    @FXML private Button quickAddBtn;
    @FXML private Button exportLogBtn;
    @FXML private Button viewStatsBtn;
    
    // Main Content - Header
    @FXML private Label selectedDateLabel;
    @FXML private Button addActivityBtn;
    
    // Main Content - Add Activity Form
    @FXML private VBox addActivityForm;
    @FXML private ComboBox<String> activityTypeComboBox;
    @FXML private TextField quantityField;
    @FXML private ComboBox<String> unitComboBox;
    @FXML private TextField customActivityField;
    @FXML private TextField co2Field;
    @FXML private TextField notesField;
    @FXML private Button cancelAddBtn;
    @FXML private Button saveActivityBtn;
    
    // Main Content - Activities List
    @FXML private VBox activitiesContainer;
    @FXML private VBox emptyState;
    @FXML private Button addFirstActivityBtn;
    
    // Main Content - Pagination
    @FXML private Button prevPageBtn;
    @FXML private Label pageLabel;
    @FXML private Button nextPageBtn;
    
    // Notifications
    @FXML private VBox notificationContainer;
    
    // Services and Data
    private ActivityService activityService;
    private NotificationService notificationService;
    private User currentUser;
    private ObservableList<Activity> allActivities;
    private ObservableList<Activity> filteredActivities;
    
    // Pagination
    private int currentPage = 1;
    private int itemsPerPage = 10;
    private int totalPages = 1;
    
    // Current filter settings
    private LocalDate currentStartDate;
    private LocalDate currentEndDate;
    private String currentCategory;
    private String currentSortBy;
    private String activeFilter = "today";
    
    @Override
    public void initializeScreen() {
        try {
            // Initialize services
            initializeServices();
            
            // Setup UI components
            setupComboBoxes();
            setupEventHandlers();
            setupDatePickers();
            
            // Load initial data
            loadActivities();
            updateDailySummary();
            
            // Set initial filter
            setActiveFilter("today");
            
            // Hide add form initially
            addActivityForm.setVisible(false);
            
            System.out.println("Activity Log screen initialized successfully");
            
        } catch (Exception e) {
            System.err.println("Error initializing Activity Log screen: " + e.getMessage());
            e.printStackTrace();
            showError("Initialization Error", "Failed to initialize Activity Log: " + e.getMessage());
        }
    }
    
    /**
     * Initialize services
     */
    private void initializeServices() {
        try {
            System.out.println("Initializing services...");
            
            activityService = new ActivityService();
            notificationService = new NotificationService();
            currentUser = getCurrentUser();
            
            // Always initialize with modifiable lists
            allActivities = FXCollections.observableArrayList();
            filteredActivities = FXCollections.observableArrayList();
            
            System.out.println("Services initialized with modifiable lists");
            
        } catch (Exception e) {
            System.err.println("Error initializing services: " + e.getMessage());
            e.printStackTrace();

            allActivities = FXCollections.observableArrayList();
            filteredActivities = FXCollections.observableArrayList();
            
            System.out.println("Using fallback mock services with modifiable lists");
        }
    }
    
    /**
     * Get current user (to be implemented with proper session management)
     */
    private User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }

    
    /**
     * Create mock data for demonstration
     */

    
    /**
     * Create sample activity for demo
     */
    private Activity createSampleActivity(String description, LocalDate date, double co2, String category, String notes) {
        Activity activity = new Activity();
        activity.setId(UUID.randomUUID().hashCode()); // Convert to int
        activity.setDescription(description);
        activity.setDate(date);
        activity.setCo2Saved(co2);
        activity.setCategory(category);
        activity.setNotes(notes);
        activity.setCompleted(true);
        activity.setQuantity(1.0);
        activity.setUnit("unit");
        return activity;
    }
    
    /**
     * Setup combo boxes with options
     */
    private void setupComboBoxes() {
        // Activity types
        activityTypeComboBox.setItems(FXCollections.observableArrayList(
            "Biking", "Walking", "Public Transport", "Electric Vehicle",
            "Vegetarian Meal", "Vegan Meal", "Local Food", "Organic Food",
            "Recycling", "Composting", "Reduce Water Usage", "Reusable Products",
            "Second-hand Shopping", "Energy Conservation", "Water Conservation", "Other"
        ));
        
        // Units
        unitComboBox.setItems(FXCollections.observableArrayList(
            "km", "miles", "hours", "meals", "items", "days", "weeks", "Other"
        ));
        
        // Category filter
        categoryFilterComboBox.setItems(FXCollections.observableArrayList(
            "All Categories", "Transportation", "Food", "Energy", "Waste", "Shopping", "Other"
        ));
        categoryFilterComboBox.getSelectionModel().selectFirst();
        
        // Sort options
        sortComboBox.setItems(FXCollections.observableArrayList(
            "Date (Newest First)", "Date (Oldest First)", "CO₂ Impact (High to Low)", 
            "CO₂ Impact (Low to High)", "Category"
        ));
        sortComboBox.getSelectionModel().selectFirst();
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Filter buttons
        todayBtn.setOnAction(e -> setActiveFilter("today"));
        yesterdayBtn.setOnAction(e -> setActiveFilter("yesterday"));
        thisWeekBtn.setOnAction(e -> setActiveFilter("thisWeek"));
        customDateBtn.setOnAction(e -> setActiveFilter("custom"));
        
        // Apply and clear filters
        applyFiltersBtn.setOnAction(e -> applyFilters());
        clearFiltersBtn.setOnAction(e -> clearFilters());
        
        // Quick actions
        quickAddBtn.setOnAction(e -> showAddActivityForm());
        exportLogBtn.setOnAction(e -> exportLog());
        viewStatsBtn.setOnAction(e -> navigateTo("charts"));
        
        // Add activity form
        addActivityBtn.setOnAction(e -> showAddActivityForm());
        addFirstActivityBtn.setOnAction(e -> showAddActivityForm());
        cancelAddBtn.setOnAction(e -> hideAddActivityForm());
        saveActivityBtn.setOnAction(e -> saveActivity());
        
        // Pagination
        prevPageBtn.setOnAction(e -> goToPreviousPage());
        nextPageBtn.setOnAction(e -> goToNextPage());
        
        // Activity type selection should update CO2 field
        activityTypeComboBox.setOnAction(e -> estimateCO2Savings());
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> estimateCO2Savings());
        unitComboBox.setOnAction(e -> estimateCO2Savings());
    }
    
    /**
     * Setup date pickers with constraints
     */
    private void setupDatePickers() {
        startDatePicker.setValue(LocalDate.now().minusDays(7));
        endDatePicker.setValue(LocalDate.now());
        
        // Prevent selecting end date before start date
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && endDatePicker.getValue() != null && 
                newVal.isAfter(endDatePicker.getValue())) {
                endDatePicker.setValue(newVal);
            }
        });
        
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && startDatePicker.getValue() != null && 
                newVal.isBefore(startDatePicker.getValue())) {
                startDatePicker.setValue(newVal);
            }
        });
    }
    
    /**
     * Set active filter and update UI
     */
    private void setActiveFilter(String filter) {
        activeFilter = filter;
        
        // Update button styles
        todayBtn.getStyleClass().remove("active");
        yesterdayBtn.getStyleClass().remove("active");
        thisWeekBtn.getStyleClass().remove("active");
        customDateBtn.getStyleClass().remove("active");
        
        switch (filter) {
            case "today":
                todayBtn.getStyleClass().add("active");
                filterToday();
                break;
            case "yesterday":
                yesterdayBtn.getStyleClass().add("active");
                filterYesterday();
                break;
            case "thisWeek":
                thisWeekBtn.getStyleClass().add("active");
                filterThisWeek();
                break;
            case "custom":
                customDateBtn.getStyleClass().add("active");
                enableCustomDateFilter();
                break;
        }
        
        applyFilters();
    }
    
    /**
     * Filter activities for today
     */
    private void filterToday() {
        currentStartDate = LocalDate.now();
        currentEndDate = LocalDate.now();
        updateSelectedDateLabel();
    }
    
    /**
     * Filter activities for yesterday
     */
    private void filterYesterday() {
        currentStartDate = LocalDate.now().minusDays(1);
        currentEndDate = LocalDate.now().minusDays(1);
        updateSelectedDateLabel();
    }
    
    /**
     * Filter activities for this week
     */
    private void filterThisWeek() {
        currentStartDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        currentEndDate = LocalDate.now();
        updateSelectedDateLabel();
    }
    
    /**
     * Enable custom date filtering
     */
    private void enableCustomDateFilter() {
        currentStartDate = startDatePicker.getValue();
        currentEndDate = endDatePicker.getValue();
        updateSelectedDateLabel();
    }
    
    /**
     * Update the selected date label
     */
    private void updateSelectedDateLabel() {
        if (currentStartDate != null && currentEndDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
            
            if (currentStartDate.equals(currentEndDate)) {
                selectedDateLabel.setText(currentStartDate.format(formatter));
            } else {
                selectedDateLabel.setText(currentStartDate.format(formatter) + " - " + 
                                         currentEndDate.format(formatter));
            }
        }
    }
    
    private void applyFilters() {
        // Always work with a new modifiable list
        ObservableList<Activity> newFilteredActivities = FXCollections.observableArrayList(allActivities);
        
        // Date filter
        if (currentStartDate != null && currentEndDate != null) {
            newFilteredActivities = newFilteredActivities.filtered(activity -> 
                !activity.getDate().isBefore(currentStartDate) && 
                !activity.getDate().isAfter(currentEndDate)
            );
        }
        
        // Category filter
        String selectedCategory = categoryFilterComboBox.getValue();
        if (selectedCategory != null && !"All Categories".equals(selectedCategory)) {
            newFilteredActivities = newFilteredActivities.filtered(activity -> 
                selectedCategory.equals(activity.getCategory())
            );
        }
        
        // Sort activities using the new method
        sortActivities(newFilteredActivities);
        
        // Update the filtered activities reference
        filteredActivities = newFilteredActivities;
        
        // Update pagination
        updatePagination();
        
        // Refresh activities display
        displayActivities();
        
        showNotification("Filters applied", "success");
    }
    
    private void sortActivities(ObservableList<Activity> activities) {
        String sortBy = sortComboBox.getValue();
        
        if (sortBy != null && activities != null && !activities.isEmpty()) {
            // Create a new modifiable list for sorting
            List<Activity> sortedList = new ArrayList<>(activities);
            
            switch (sortBy) {
                case "Date (Newest First)":
                    sortedList.sort((a1, a2) -> a2.getDate().compareTo(a1.getDate()));
                    break;
                case "Date (Oldest First)":
                    sortedList.sort(Comparator.comparing(Activity::getDate));
                    break;
                case "CO₂ Impact (High to Low)":
                    sortedList.sort((a1, a2) -> Double.compare(a2.getCo2Saved(), a1.getCo2Saved()));
                    break;
                case "CO₂ Impact (Low to High)":
                    sortedList.sort(Comparator.comparingDouble(Activity::getCo2Saved));
                    break;
                case "Category":
                    sortedList.sort(Comparator.comparing(Activity::getCategory));
                    break;
            }
            
            // Safely replace the contents of the original list
            try {
                activities.setAll(sortedList);
            } catch (UnsupportedOperationException e) {
                // If the list is unmodifiable, create a new modifiable one
                ObservableList<Activity> newList = FXCollections.observableArrayList(sortedList);
                
                // Update the filteredActivities reference if needed
                if (activities == filteredActivities) {
                    filteredActivities = newList;
                }
                
                // For the applyFilters method, we need to return the new list
                // Since we can't return from here, we'll handle this in applyFilters
            }
        }
    }
    /**
     * Clear all filters
     */
    private void clearFilters() {
        categoryFilterComboBox.getSelectionModel().selectFirst();
        sortComboBox.getSelectionModel().selectFirst();
        setActiveFilter("today");
        showNotification("Filters cleared", "info");
    }
    
    /**
     * Load activities from service
     */
   
    private void loadActivities() {
        try {
            if (activityService != null && currentUser != null) {
                List<Activity> activities = activityService.getUserActivities(String.valueOf(currentUser.getId()));
                
                // Always create new observable lists to ensure they're modifiable
                allActivities = FXCollections.observableArrayList(activities);
                filteredActivities = FXCollections.observableArrayList(activities);
                
                System.out.println("Loaded " + activities.size() + " activities into modifiable lists");
            }
        } catch (Exception e) {
            System.err.println("Error loading activities: " + e.getMessage());
            // Ensure we always have modifiable lists
            allActivities = FXCollections.observableArrayList();
            filteredActivities = FXCollections.observableArrayList();
        }
        
        applyFilters();
    }
    
    /**
     * Update daily summary metrics
     */
    private void updateDailySummary() {
        if (currentUser != null) {
            currentStreakLabel.setText(currentUser.getCurrentStreak() + " days");
        }
        
        // Calculate today's metrics
        double todayCO2 = allActivities.stream()
            .filter(activity -> activity.getDate().equals(LocalDate.now()))
            .mapToDouble(Activity::getCo2Saved)
            .sum();
        
        long todayActivities = allActivities.stream()
            .filter(activity -> activity.getDate().equals(LocalDate.now()))
            .count();
        
        dailyCO2Label.setText(String.format("%.1f kg", todayCO2));
        dailyActivitiesLabel.setText(String.valueOf(todayActivities));
        
        // Set equivalent text (e.g., "like planting X trees")
        int treesEquivalent = (int) (todayCO2 * 0.048); // Approximate conversion
        dailyEquivalentLabel.setText("≈ planting " + treesEquivalent + " trees");
    }
    
    /**
     * Display activities with pagination
     */
    private void displayActivities() {
        activitiesContainer.getChildren().clear();
        
        if (filteredActivities.isEmpty()) {
            emptyState.setVisible(true);
            activitiesContainer.setVisible(false);
            return;
        }
        
        emptyState.setVisible(false);
        activitiesContainer.setVisible(true);
        
        // Calculate pagination bounds
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, filteredActivities.size());
        
        // Create activity cards for current page
        for (int i = startIndex; i < endIndex; i++) {
            Activity activity = filteredActivities.get(i);
            activitiesContainer.getChildren().add(createActivityCard(activity));
        }
    }
    
    /**
     * Create an activity card UI component
     */
    private HBox createActivityCard(Activity activity) {
        HBox card = new HBox();
        card.getStyleClass().add("activity-card");
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        
        // Activity icon based on category
        Circle icon = new Circle(20);
        icon.setFill(getCategoryColor(activity.getCategory()));
        
        // Activity details
        VBox details = new VBox(5);
        Label title = new Label(activity.getDescription());
        title.getStyleClass().add("activity-title");
        
        HBox metaInfo = new HBox(10);
        Label date = new Label(activity.getDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
        Label category = new Label(activity.getCategory());
        Label co2 = new Label(String.format("%.1f kg CO₂", activity.getCo2Saved()));
        
        metaInfo.getChildren().addAll(date, category, co2);
        
        if (activity.getNotes() != null && !activity.getNotes().isEmpty()) {
            Label notes = new Label(activity.getNotes());
            notes.getStyleClass().add("activity-notes");
            details.getChildren().addAll(title, metaInfo, notes);
        } else {
            details.getChildren().addAll(title, metaInfo);
        }
        
        // Action buttons
        HBox actions = new HBox(5);
        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");
        
        editBtn.setOnAction(e -> editActivity(activity));
        deleteBtn.setOnAction(e -> deleteActivity(activity));
        
        actions.getChildren().addAll(editBtn, deleteBtn);
        
        card.getChildren().addAll(icon, details, actions);
        return card;
    }
    
    /**
     * Get color for activity category
     */
    private Color getCategoryColor(String category) {
        switch (category != null ? category : "Other") {
            case "Transportation": return Color.web("#4ecdc4");
            case "Food": return Color.web("#ff6b9d");
            case "Energy": return Color.web("#bb86fc");
            case "Waste": return Color.web("#64ffda");
            case "Shopping": return Color.web("#38ef7d");
            default: return Color.web("#a0a0a0");
        }
    }
    
    /**
     * Update pagination controls
     */
    private void updatePagination() {
        totalPages = (int) Math.ceil((double) filteredActivities.size() / itemsPerPage);
        
        if (totalPages == 0) {
            totalPages = 1;
        }
        
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        
        pageLabel.setText(currentPage + " / " + totalPages);
        prevPageBtn.setDisable(currentPage <= 1);
        nextPageBtn.setDisable(currentPage >= totalPages);
    }
    
    /**
     * Go to previous page
     */
    private void goToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            displayActivities();
        }
    }
    
    /**
     * Go to next page
     */
    private void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            displayActivities();
        }
    }
    
    /**
     * Show add activity form
     */
    private void showAddActivityForm() {
        addActivityForm.setVisible(true);
        activityTypeComboBox.getSelectionModel().clearSelection();
        quantityField.clear();
        unitComboBox.getSelectionModel().clearSelection();
        customActivityField.clear();
        co2Field.clear();
        notesField.clear();
    }
    
    /**
     * Hide add activity form
     */
    private void hideAddActivityForm() {
        addActivityForm.setVisible(false);
    }
    
    /**
     * Estimate CO2 savings based on activity type and quantity
     */
    private void estimateCO2Savings() {
        String activityType = activityTypeComboBox.getValue();
        String quantityText = quantityField.getText();
        String unit = unitComboBox.getValue();
        
        if (activityType != null && quantityText != null && !quantityText.isEmpty() && unit != null) {
            try {
                double quantity = Double.parseDouble(quantityText);
                double co2Saved = calculateCO2Estimate(activityType, quantity, unit);
                co2Field.setText(String.format("%.2f", co2Saved));
            } catch (NumberFormatException e) {
                co2Field.clear();
            }
        } else {
            co2Field.clear();
        }
    }
    
    /**
     * Calculate CO2 estimate based on activity parameters
     */
    private double calculateCO2Estimate(String activityType, double quantity, String unit) {
        // These are approximate values for demonstration
        switch (activityType) {
            case "Biking":
                return quantity * 0.21; // kg CO2 saved per km vs driving
            case "Vegetarian Meal":
                return quantity * 2.5; // kg CO2 saved per meal vs meat
            case "Recycling":
                return quantity * 0.5; // kg CO2 saved per item
            case "Reduce Water Usage":
                return quantity * 0.1; // kg CO2 saved per hour of reduced usage
            default:
                return quantity * 0.5; // Default estimate
        }
    }
    
    /**
     * Save new activity
     */
    private void saveActivity() {
        try {
            System.out.println("=== SAVE ACTIVITY DEBUG START ===");
            
            String activityType = activityTypeComboBox.getValue();
            String customActivity = customActivityField.getText();
            String quantityText = quantityField.getText();
            String unit = unitComboBox.getValue();
            String co2Text = co2Field.getText();
            String notes = notesField.getText();
            
            System.out.println("Input values - Type: " + activityType + ", Custom: " + customActivity + 
                              ", Qty: " + quantityText + ", Unit: " + unit + ", CO2: " + co2Text);
            
            // Validate required fields
            if ((activityType == null || activityType.isEmpty()) && 
                (customActivity == null || customActivity.isEmpty())) {
                System.out.println("Validation failed: No activity type");
                showNotification("Please select an activity type or enter a custom activity", "error");
                return;
            }
            
            if (quantityText == null || quantityText.isEmpty()) {
                System.out.println("Validation failed: No quantity");
                showNotification("Please enter a quantity", "error");
                return;
            } 
            
            if (unit == null || unit.isEmpty()) {
                System.out.println("Validation failed: No unit");
                showNotification("Please select a unit", "error");
                return;
            }
            
            // Parse quantity
            double quantity;
            try {
                quantity = Double.parseDouble(quantityText);
                if (quantity <= 0) {
                    System.out.println("Validation failed: Quantity <= 0");
                    showNotification("Quantity must be greater than 0", "error");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Validation failed: Invalid quantity format");
                showNotification("Please enter a valid number for quantity", "error");
                return;
            }
            
            // Create new activity
            Activity activity = new Activity();
            String description = activityType != null ? activityType : customActivity;
            activity.setDescription(description);
            activity.setDate(LocalDate.now());
            activity.setCategory(determineCategory(description));
            activity.setNotes(notes);
            activity.setQuantity(quantity);
            activity.setUnit(unit);
            activity.setCompleted(true);
            
            System.out.println("Activity created: " + activity.getDescription() + 
                              ", Category: " + activity.getCategory());
            
            // Handle CO2 calculation
            double co2Saved;
            if (co2Text != null && !co2Text.isEmpty()) {
                try {
                    co2Saved = Double.parseDouble(co2Text);
                    if (co2Saved <= 0) {
                        System.out.println("Validation failed: CO2 <= 0");
                        showNotification("CO₂ savings must be greater than 0", "error");
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Validation failed: Invalid CO2 format");
                    showNotification("Please enter a valid number for CO₂ savings", "error");
                    return;
                }
            } else {
                // Auto-calculate if not provided
                co2Saved = calculateCO2Estimate(description, quantity, unit);
                System.out.println("CO2 auto-calculated: " + co2Saved);
            }
            activity.setCo2Saved(co2Saved);
            
            // Check if services are available
            System.out.println("Checking services - ActivityService: " + activityService + 
                              ", CurrentUser: " + currentUser);
            
            if (activityService == null) {
                System.out.println("ERROR: ActivityService is null");
                throw new IllegalStateException("ActivityService is not initialized");
            }
            
            if (currentUser == null) {
                System.out.println("ERROR: CurrentUser is null");
                throw new IllegalStateException("No user is currently logged in");
            }
            
            String userId = String.valueOf(currentUser.getId());
            System.out.println("User ID: " + userId);
            
            // Save to service
            System.out.println("Attempting to save to database...");
            boolean saveSuccess = activityService.saveActivity(userId, activity);
            System.out.println("Database save result: " + saveSuccess);
            
            // Add to local lists regardless of database success
            System.out.println("Adding to local lists...");
            addActivityToLocalLists(activity);
            filteredActivities.add(activity);
            
            // Update UI
            applyFilters();
            updateDailySummary();
            hideAddActivityForm();
            
            if (saveSuccess) {
                System.out.println("Save completed successfully");
                showNotification("Activity saved successfully!", "success");
            } else {
                System.out.println("Save failed but added to local list");
                showNotification("Activity saved locally (database issue)", "info");
            }
            
            System.out.println("=== SAVE ACTIVITY DEBUG END ===");
            
        } catch (IllegalStateException e) {
            System.err.println("CRITICAL ERROR: " + e.getMessage());
            showNotification("System error: " + e.getMessage(), "error");
        } catch (NumberFormatException e) {
            System.err.println("Number format error: " + e.getMessage());
            showNotification("Please enter valid numbers for quantity and CO₂ fields", "error");
        } catch (Exception e) {
            System.err.println("UNEXPECTED ERROR in saveActivity:");
            e.printStackTrace();
            showNotification("Error saving activity: " + 
                (e.getMessage() != null ? e.getMessage() : "Unknown error"), "error");
        }
    }
    
    /**
     * Determine category based on activity description
     */
    private String determineCategory(String activityDescription) {
        if (activityDescription == null) return "Other";
        
        activityDescription = activityDescription.toLowerCase();
        
        if (activityDescription.contains("bik") || activityDescription.contains("cycl") ||
            activityDescription.contains("walk") || activityDescription.contains("transport")) {
            return "Transportation";
        } else if (activityDescription.contains("meal") || activityDescription.contains("food") ||
                   activityDescription.contains("vegetarian") || activityDescription.contains("vegan")) {
            return "Food";
        } else if (activityDescription.contains("energy") || activityDescription.contains("led") ||
                   activityDescription.contains("bulb") || activityDescription.contains("power")) {
            return "Energy";
        } else if (activityDescription.contains("recycl") || activityDescription.contains("compost") ||
                   activityDescription.contains("waste")) {
            return "Waste";
        } else if (activityDescription.contains("shop") || activityDescription.contains("buy") ||
                   activityDescription.contains("purchase")) {
            return "Shopping";
        } else {
            return "Other";
        }
    }
    
    /**
     * Edit existing activity
     */
    private void editActivity(Activity activity) {
        // For simplicity, we'll delete and re-add with editing
        // In a real application, you'd have a proper edit form
        deleteActivity(activity);
        
        // Pre-fill the form with activity data
        activityTypeComboBox.setValue(activity.getDescription());
        quantityField.setText(String.valueOf(activity.getQuantity()));
        unitComboBox.setValue(activity.getUnit());
        co2Field.setText(String.valueOf(activity.getCo2Saved()));
        notesField.setText(activity.getNotes());
        
        showAddActivityForm();
    }
    
    /**
     * Delete activity
     */
    private void deleteActivity(Activity activity) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Activity");
        confirmation.setHeaderText("Are you sure you want to delete this activity?");
        confirmation.setContentText("This action cannot be undone.");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Delete from service - convert int parameters to String
                if (activityService != null && currentUser != null) {
                	activityService.deleteActivity(String.valueOf(currentUser.getId()), String.valueOf(activity.getId()));
                }
                
                // Remove from local lists
                allActivities.remove(activity);
                filteredActivities.remove(activity);
                
                // Update UI
                applyFilters();
                updateDailySummary();
                
                showNotification("Activity deleted successfully", "success");
            } catch (Exception e) {
                showNotification("Error deleting activity: " + e.getMessage(), "error");
            }
        }
    }
    
    /**
     * Export activity log
     */
    private void exportLog() {
        try {
            // This would typically use the ExportController
            showNotification("Export functionality will be implemented soon", "info");
        } catch (Exception e) {
            showNotification("Error exporting log: " + e.getMessage(), "error");
        }
    }
    
    /**
     * Show notification
     */
    private void showNotification(String message, String type) {
        // Implementation would show a temporary notification to the user
        System.out.println(type.toUpperCase() + ": " + message);
        
        // You would typically use a notification system like in other controllers
        if (notificationContainer != null) {
            Label notification = new Label(message);
            notification.getStyleClass().addAll("notification", "notification-" + type);
            notificationContainer.getChildren().add(notification);
            
            // Auto-remove after 3 seconds
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), notification);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> notificationContainer.getChildren().remove(notification));
            fadeOut.play();
        }
    }
    
    private void addActivityToLocalLists(Activity activity) {
        try {
            // Try to add to the existing lists
            allActivities.add(activity);
            filteredActivities.add(activity);
        } catch (UnsupportedOperationException e) {
            System.out.println("Lists are unmodifiable, creating new lists...");
            
            // Create new modifiable lists
            ObservableList<Activity> newAllActivities = FXCollections.observableArrayList(allActivities);
            ObservableList<Activity> newFilteredActivities = FXCollections.observableArrayList(filteredActivities);
            
            // Add the new activity
            newAllActivities.add(activity);
            newFilteredActivities.add(activity);
            
            // Replace the references
            allActivities = newAllActivities;
            filteredActivities = newFilteredActivities;
            
            System.out.println("New lists created and activity added");
        }
    }
    
    @Override
    public void onScreenActivated() {
        super.onScreenActivated();
        // Refresh data when screen is activated
        loadActivities();
        updateDailySummary();
    }
    
    @Override
    public void refreshScreen() {
        loadActivities();
        updateDailySummary();
    }
}