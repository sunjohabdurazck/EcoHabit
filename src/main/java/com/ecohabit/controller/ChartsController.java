package main.java.com.ecohabit.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import main.java.com.ecohabit.SessionManager;
import main.java.com.ecohabit.model.User;
import main.java.com.ecohabit.service.UserService;

public  class ChartsController extends BaseController{
	
	private User currentUser;

    // Time range toggle group
    @FXML private ToggleGroup timeRangeGroup;
    @FXML private RadioButton weekRadio;
    @FXML private RadioButton monthRadio;
    @FXML private RadioButton quarterRadio;
    @FXML private RadioButton yearRadio;
    @FXML private RadioButton allTimeRadio;
    
    // Trend toggle group
    @FXML private ToggleGroup trendGroup;
    @FXML private RadioButton dailyTrendRadio;
    @FXML private RadioButton weeklyTrendRadio;
    @FXML private RadioButton monthlyTrendRadio;
    
    // Navigation buttons
    @FXML private Button overviewBtn;
    @FXML private Button trendsBtn;
    @FXML private Button breakdownBtn;
    @FXML private Button comparisonBtn;
    @FXML private Button goalsBtn;
    
    // Charts
    @FXML private LineChart<String, Number> trendChart;
    @FXML private PieChart activityPieChart;
    @FXML private BarChart<String, Number> categoryBarChart;
    @FXML private AreaChart<String, Number> goalsChart;
    
    // Combo boxes
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> goalsTimeframeComboBox;
    
    // Labels
    @FXML private Label totalCO2Label;
    @FXML private Label equivalentLabel;
    @FXML private Label totalActivitiesLabel;
    @FXML private Label currentStreakLabel;
    @FXML private Label dateRangeLabel;
    @FXML private Label weeklyCO2Label;
    @FXML private Label weeklyChangeLabel;
    @FXML private Label topCategoryLabel;
    @FXML private Label topCategoryValue;
    @FXML private Label goalProgressLabel;
    @FXML private Label goalRemainingLabel;
    
    // Containers
    @FXML private VBox topActivitiesContainer;
    @FXML private VBox notificationContainer;
    
    // Buttons
    @FXML private Button applyFiltersBtn;
    @FXML private Button exportChartBtn;
    
    // Data for charts
    private ObservableList<PieChart.Data> pieChartData;
    private ObservableList<XYChart.Series<String, Number>> lineChartData;
    private ObservableList<XYChart.Series<String, Number>> barChartData;
    private ObservableList<XYChart.Series<String, Number>> areaChartData;
    
    @FXML
    public void initialize() {
        // Initialize toggle groups
        initializeToggleGroups();
        
        // Initialize combo boxes
        initializeComboBoxes();
        
        // Initialize charts
        initializeCharts();
        
        // Set up event handlers
        setupEventHandlers();
        
        // Load initial data
        loadData();
    }
    
    private void initializeToggleGroups() {
        // Time range group
        timeRangeGroup = new ToggleGroup();
        weekRadio.setToggleGroup(timeRangeGroup);
        monthRadio.setToggleGroup(timeRangeGroup);
        quarterRadio.setToggleGroup(timeRangeGroup);
        yearRadio.setToggleGroup(timeRangeGroup);
        allTimeRadio.setToggleGroup(timeRangeGroup);
        weekRadio.setSelected(true);
        
        // Trend group
        trendGroup = new ToggleGroup();
        dailyTrendRadio.setToggleGroup(trendGroup);
        weeklyTrendRadio.setToggleGroup(trendGroup);
        monthlyTrendRadio.setToggleGroup(trendGroup);
        dailyTrendRadio.setSelected(true);
    }
    
    private void initializeComboBoxes() {
        // Category combo box
        ObservableList<String> categories = FXCollections.observableArrayList(
            "All Categories", "Transportation", "Food", "Energy", "Waste", "Shopping"
        );
        categoryComboBox.setItems(categories);
        categoryComboBox.getSelectionModel().selectFirst();
        
        // Goals timeframe combo box
        ObservableList<String> timeframes = FXCollections.observableArrayList(
            "Weekly", "Monthly", "Quarterly", "Yearly"
        );
        goalsTimeframeComboBox.setItems(timeframes);
        goalsTimeframeComboBox.getSelectionModel().selectFirst();
    }
    
    private void initializeCharts() {
        // Initialize pie chart data
        pieChartData = FXCollections.observableArrayList();
        activityPieChart.setData(pieChartData);
        
        // Initialize line chart data
        lineChartData = FXCollections.observableArrayList();
        trendChart.setData(lineChartData);
        
        // Initialize bar chart data
        barChartData = FXCollections.observableArrayList();
        categoryBarChart.setData(barChartData);
        
        // Initialize area chart data
        areaChartData = FXCollections.observableArrayList();
        goalsChart.setData(areaChartData);
        
        // Style charts
        styleCharts();
    }
    
    private void styleCharts() {
        // Remove legend from pie chart for cleaner look
        activityPieChart.setLegendVisible(false);
        
        // Set animated to false for better performance
        trendChart.setAnimated(false);
        activityPieChart.setAnimated(false);
        categoryBarChart.setAnimated(false);
        goalsChart.setAnimated(false);
    }
    
    private void setupEventHandlers() {
        // Time range radio buttons
        timeRangeGroup.selectedToggleProperty().addListener((_, _, newVal) -> {
            if (newVal != null) {
                applyFilters();
            }
        });
        
        // Trend radio buttons
        trendGroup.selectedToggleProperty().addListener((_, _, newVal) -> {
            if (newVal != null) {
                updateTrendChart();
            }
        });
        
        // Category combo box
        categoryComboBox.valueProperty().addListener((_, _, _) -> {
            applyFilters();
        });
        
        // Goals timeframe combo box
        goalsTimeframeComboBox.valueProperty().addListener((_, _, _) -> {
            updateGoalsChart();
        });
        
        // Apply filters button
        applyFiltersBtn.setOnAction(_ -> applyFilters());
        
        // Export chart button
        exportChartBtn.setOnAction(_ -> exportData());
        
        // Navigation buttons
        overviewBtn.setOnAction(_ -> switchView("overview"));
        trendsBtn.setOnAction(_ -> switchView("trends"));
        breakdownBtn.setOnAction(_ -> switchView("breakdown"));
        comparisonBtn.setOnAction(_ -> switchView("comparison"));
        goalsBtn.setOnAction(_ -> switchView("goals"));
    }
     

    private void loadData() {
        currentUser = SessionManager.getCurrentUser();
        
        if (currentUser != null) {
            // Load user-specific chart data instead of sample data
            loadUserSpecificChartData(currentUser.getId());
        } else {
            loadSampleData(); // fallback
        }
    }
    private void loadUserSpecificChartData(int id) {
		// TODO Auto-generated method stub
		
	}

	private void updateDateRangeLabel() {
        RadioButton selectedRadio = (RadioButton) timeRangeGroup.getSelectedToggle();
        String rangeText = "";
        
        if (selectedRadio == weekRadio) {
            rangeText = "This Week";
        } else if (selectedRadio == monthRadio) {
            rangeText = "This Month";
        } else if (selectedRadio == quarterRadio) {
            rangeText = "Last 3 Months";
        } else if (selectedRadio == yearRadio) {
            rangeText = "This Year";
        } else if (selectedRadio == allTimeRadio) {
            rangeText = "All Time";
        }
        
        // Format current date range
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        String currentDate = now.format(formatter);
        
        dateRangeLabel.setText(currentDate + " • " + rangeText);
    }
    
    private void loadSampleData() {
        // This would typically come from your data model
        // For now, we'll use sample data
        
        totalCO2Label.setText("247.5");
        equivalentLabel.setText("≈ planting 12 trees");
        totalActivitiesLabel.setText("142");
        currentStreakLabel.setText("15 days");
        weeklyCO2Label.setText("42.5");
        weeklyChangeLabel.setText("+15% from last week");
        topCategoryLabel.setText("Transportation");
        topCategoryValue.setText("18.2 kg");
        goalProgressLabel.setText("78%");
        goalRemainingLabel.setText("22 kg remaining");
    }
    
    private void updateAllCharts() {
        updateTrendChart();
        updatePieChart();
        updateBarChart();
        updateGoalsChart();
    }
    
    private void updateTrendChart() {
        RadioButton selectedTrend = (RadioButton) trendGroup.getSelectedToggle();
        lineChartData.clear();
        
        // Sample data based on selected trend
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("CO₂ Savings");
        
        if (selectedTrend == dailyTrendRadio) {
            // Daily data
            series.getData().add(new XYChart.Data<>("Mon", 5.2));
            series.getData().add(new XYChart.Data<>("Tue", 7.8));
            series.getData().add(new XYChart.Data<>("Wed", 6.5));
            series.getData().add(new XYChart.Data<>("Thu", 8.1));
            series.getData().add(new XYChart.Data<>("Fri", 9.4));
            series.getData().add(new XYChart.Data<>("Sat", 3.2));
            series.getData().add(new XYChart.Data<>("Sun", 2.3));
        } else if (selectedTrend == weeklyTrendRadio) {
            // Weekly data
            series.getData().add(new XYChart.Data<>("Week 1", 32.5));
            series.getData().add(new XYChart.Data<>("Week 2", 36.8));
            series.getData().add(new XYChart.Data<>("Week 3", 42.1));
            series.getData().add(new XYChart.Data<>("Week 4", 45.7));
        } else if (selectedTrend == monthlyTrendRadio) {
            // Monthly data
            series.getData().add(new XYChart.Data<>("Jan", 120.5));
            series.getData().add(new XYChart.Data<>("Feb", 135.2));
            series.getData().add(new XYChart.Data<>("Mar", 157.8));
            series.getData().add(new XYChart.Data<>("Apr", 142.3));
        }
        
        lineChartData.add(series);
    }
    
    private void updatePieChart() {
        pieChartData.clear();
        
        // Sample data for activity breakdown
        pieChartData.addAll(
            new PieChart.Data("Transportation", 86),
            new PieChart.Data("Food", 52),
            new PieChart.Data("Energy", 45),
            new PieChart.Data("Waste", 28),
            new PieChart.Data("Shopping", 19)
        );
    }
    
    @SuppressWarnings("unchecked")
	private void updateBarChart() {
        barChartData.clear();
        
        // Sample data for category comparison
        XYChart.Series<String, Number> currentSeries = new XYChart.Series<>();
        currentSeries.setName("Current Period");
        
        XYChart.Series<String, Number> previousSeries = new XYChart.Series<>();
        previousSeries.setName("Previous Period");
        
        // Add data based on selected time range
        RadioButton selectedRange = (RadioButton) timeRangeGroup.getSelectedToggle();
        
        if (selectedRange == weekRadio || selectedRange == monthRadio) {
            currentSeries.getData().add(new XYChart.Data<>("Transportation", 18.2));
            currentSeries.getData().add(new XYChart.Data<>("Food", 12.5));
            currentSeries.getData().add(new XYChart.Data<>("Energy", 8.7));
            currentSeries.getData().add(new XYChart.Data<>("Waste", 5.3));
            currentSeries.getData().add(new XYChart.Data<>("Shopping", 3.8));
            
            previousSeries.getData().add(new XYChart.Data<>("Transportation", 15.8));
            previousSeries.getData().add(new XYChart.Data<>("Food", 10.2));
            previousSeries.getData().add(new XYChart.Data<>("Energy", 7.4));
            previousSeries.getData().add(new XYChart.Data<>("Waste", 4.9));
            previousSeries.getData().add(new XYChart.Data<>("Shopping", 3.1));
        } else {
            // For longer time ranges, use different data
            currentSeries.getData().add(new XYChart.Data<>("Transportation", 86.2));
            currentSeries.getData().add(new XYChart.Data<>("Food", 52.5));
            currentSeries.getData().add(new XYChart.Data<>("Energy", 45.7));
            currentSeries.getData().add(new XYChart.Data<>("Waste", 28.3));
            currentSeries.getData().add(new XYChart.Data<>("Shopping", 19.8));
            
            previousSeries.getData().add(new XYChart.Data<>("Transportation", 72.8));
            previousSeries.getData().add(new XYChart.Data<>("Food", 45.2));
            previousSeries.getData().add(new XYChart.Data<>("Energy", 38.4));
            previousSeries.getData().add(new XYChart.Data<>("Waste", 24.9));
            previousSeries.getData().add(new XYChart.Data<>("Shopping", 16.1));
        }
        
        barChartData.addAll(currentSeries, previousSeries);
    }
    
    private void updateGoalsChart() {
        areaChartData.clear();
        
        // Sample data for goals progress
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Goal Progress");
        
        String timeframe = goalsTimeframeComboBox.getValue();
        
        if ("Weekly".equals(timeframe)) {
            series.getData().add(new XYChart.Data<>("Mon", 8));
            series.getData().add(new XYChart.Data<>("Tue", 15));
            series.getData().add(new XYChart.Data<>("Wed", 22));
            series.getData().add(new XYChart.Data<>("Thu", 28));
            series.getData().add(new XYChart.Data<>("Fri", 35));
            series.getData().add(new XYChart.Data<>("Sat", 42));
            series.getData().add(new XYChart.Data<>("Sun", 50));
        } else if ("Monthly".equals(timeframe)) {
            series.getData().add(new XYChart.Data<>("Week 1", 20));
            series.getData().add(new XYChart.Data<>("Week 2", 45));
            series.getData().add(new XYChart.Data<>("Week 3", 65));
            series.getData().add(new XYChart.Data<>("Week 4", 85));
        } else if ("Quarterly".equals(timeframe)) {
            series.getData().add(new XYChart.Data<>("Month 1", 30));
            series.getData().add(new XYChart.Data<>("Month 2", 60));
            series.getData().add(new XYChart.Data<>("Month 3", 90));
        } else if ("Yearly".equals(timeframe)) {
            series.getData().add(new XYChart.Data<>("Q1", 25));
            series.getData().add(new XYChart.Data<>("Q2", 50));
            series.getData().add(new XYChart.Data<>("Q3", 75));
            series.getData().add(new XYChart.Data<>("Q4", 100));
        }
        
        areaChartData.add(series);
    }
    
    private void applyFilters() {
        updateDateRangeLabel();
        updateAllCharts();
        
        // Show notification that filters were applied
        showNotification("Filters applied successfully!");
    }
    
    private void exportData() {
        // Implementation for exporting chart data
        // This would typically save data to a file or generate a report
        
        showNotification("Data exported successfully!");
    }
    
    private void switchView(String viewName) {
        // Implementation for switching between different chart views
        // This would typically show/hide different chart sections
        
        // Reset all buttons to default style
        overviewBtn.getStyleClass().remove("active");
        trendsBtn.getStyleClass().remove("active");
        breakdownBtn.getStyleClass().remove("active");
        comparisonBtn.getStyleClass().remove("active");
        goalsBtn.getStyleClass().remove("active");
        
        // Add active class to the selected button
        switch (viewName) {
            case "overview":
                overviewBtn.getStyleClass().add("active");
                break;
            case "trends":
                trendsBtn.getStyleClass().add("active");
                break;
            case "breakdown":
                breakdownBtn.getStyleClass().add("active");
                break;
            case "comparison":
                comparisonBtn.getStyleClass().add("active");
                break;
            case "goals":
                goalsBtn.getStyleClass().add("active");
                break;
        }
        
        showNotification("Switched to " + viewName + " view");
    }
    
    private void showNotification(String message) {
        // Create a temporary notification
        Label notification = new Label(message);
        notification.getStyleClass().add("notification");
        
        // Add to notification container
        notificationContainer.getChildren().add(notification);
        notificationContainer.setVisible(true);
        
        // Set timer to remove notification after 3 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(_ -> {
            notificationContainer.getChildren().remove(notification);
            if (notificationContainer.getChildren().isEmpty()) {
                notificationContainer.setVisible(false);
            }
        });
        pause.play();
    }

	@Override
	public void initializeScreen() {
		// TODO Auto-generated method stub
		
	}
}