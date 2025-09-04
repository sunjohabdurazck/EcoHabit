package main.java.com.ecohabit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.concurrent.Task;
import main.java.com.ecohabit.SessionManager;
import main.java.com.ecohabit.model.User;
import main.java.com.ecohabit.service.UserService;
import main.java.com.ecohabit.util.SceneManager;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

/**
 * Main controller that manages the application's main layout and screen navigation
 * Now includes the sidebar functionality moved from DashboardController
 */
public class MainController implements Initializable {
    
    // Content area
    @FXML private StackPane contentArea;
    
    // Sidebar components (moved from DashboardController)
    @FXML private Label userNameLabel;
    @FXML private Label userStatusLabel;
    @FXML private Label streakLabel;
    @FXML private Label totalCO2Label;
    @FXML private Label equivalentLabel;
    
    // Navigation buttons
    @FXML private Button dashboardBtn;
    @FXML private Button activityLogBtn;
    @FXML private Button chartsBtn;
    @FXML private Button badgesBtn;
    @FXML private Button tipsBtn;
    @FXML private Button chatbotBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;
    
    @FXML
    private StackPane profileContainer;
    private ImageView profileImageView;
    private Label profileInitialsLabel;
    // Services
    private UserService userService;
    
    // State management
    private Map<String, BaseController> screenControllers = new HashMap<>();
    private String currentScreen;
    private User currentUser;
    private String currentTheme = "eco";
    private int currentFontSize = 12;
    private Stage primaryStage;
    private Parent mainContainer;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        initializeServices();
        
        // Initialize profile components
        initializeProfileComponents();
        

        // Load user data for sidebar
        loadUserData();
        
        // Setup navigation button styles
        setupNavigationStyles();
        
        // Load the dashboard as the default screen
        navigateTo("dashboard");
    }
    
    /**
     * Initialize services
     */
    private void initializeServices() {
        try {
            userService = new UserService();
        } catch (Exception e) {
            System.err.println("Error initializing services: " + e.getMessage());
            // Create mock user data as fallback
            createMockUserData();
        }
    } 
    

    private void loadUserData() {
        Platform.runLater(() -> {
            // Get the actual logged-in user from SessionManager
            currentUser = SessionManager.getCurrentUser();
            
            // If no user is in session (shouldn't happen after login), use mock as fallback
            if (currentUser == null) {
                System.err.println("No user in session, using mock data");
                currentUser = createMockUser();
            }
            
            updateSidebarUI();
            updateProfilePhoto(currentUser.getProfilePicture()); // Load profile photo
        });
    }
    /**
     * Create mock user data for demonstration
     */
    private User createMockUser() {
        User user = new User();
        user.setId(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCurrentStreak(15);
        user.setTotalCO2Saved(247.5);
        return user;
    }
    
    /**
     * Create mock user data as fallback
     */
    private void createMockUserData() {
        currentUser = createMockUser();
        updateSidebarUI();
    }
    
    /**
     * Update sidebar UI with user data
     */
    private void updateSidebarUI() {
        if (currentUser == null) return;
        
        if (userNameLabel != null) {
            userNameLabel.setText(currentUser.getFullName());
        }
        
        if (userStatusLabel != null) {
            userStatusLabel.setText(determineUserStatus(currentUser));
        }
        
        if (streakLabel != null) {
            streakLabel.setText(currentUser.getCurrentStreak() + " day streak");
        }
        
        if (totalCO2Label != null) {
            totalCO2Label.setText(String.format("%.1f", currentUser.getTotalCO2Saved()));
        }
        
        if (equivalentLabel != null) {
            updateEquivalentLabel(currentUser.getTotalCO2Saved());
        }
        
        // Update profile photo/initials
        updateProfilePhoto(currentUser.getProfilePicture());
    }
    
    /**
     * Determine user eco status based on their activity level
     */
    private String determineUserStatus(User user) {
        double totalCO2 = user.getTotalCO2Saved();
        if (totalCO2 >= 500) return "🌟 Eco Legend";
        else if (totalCO2 >= 200) return "🌱 Eco Warrior";
        else if (totalCO2 >= 50) return "♻️ Green Enthusiast";
        else return "🌿 Eco Beginner";
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
        
        java.util.Random random = new java.util.Random();
        String equivalent = equivalents[random.nextInt(equivalents.length)];
        equivalentLabel.setText(equivalent);
    }
    
    /**
     * Setup navigation button styles and active states
     */
    private void setupNavigationStyles() {
        // Remove active class from all buttons first
        clearActiveStates();
    }
    
    /**
     * Clear active states from all navigation buttons
     */
    private void clearActiveStates() {
        Button[] navButtons = {dashboardBtn, activityLogBtn, chartsBtn, badgesBtn, tipsBtn, chatbotBtn, settingsBtn};
        for (Button btn : navButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("active");
            }
        }
    }
    
    /**
     * Set active state for specific button
     */
    private void setActiveButton(String screenName) {
        clearActiveStates();
        
        Button activeButton = null;
        switch (screenName.toLowerCase()) {
            case "dashboard":
                activeButton = dashboardBtn;
                break;
            case "activity_log":
                activeButton = activityLogBtn;
                break;
            case "charts":
                activeButton = chartsBtn;
                break;
            case "badges":
                activeButton = badgesBtn;
                break;
            case "tips":
                activeButton = tipsBtn;
                break;
            case "chatbot":
                activeButton = chatbotBtn;
                break;
            case "settings":
                activeButton = settingsBtn;
                break;
        }
        
        if (activeButton != null) {
            activeButton.getStyleClass().add("active");
        }
    }
    
    // Navigation event handlers
    @FXML
    private void handleDashboard() {
        handleNavigation("dashboard");
    }
    
    @FXML
    private void handleActivityLog() {
        handleNavigation("activity_log");
    }
    
    @FXML
    private void handleCharts() {
        handleNavigation("charts");
    }
    
    @FXML
    private void handleBadges() {
        handleNavigation("badges");
    }
    
    @FXML
    private void handleTips() {
        handleNavigation("tips");
    }
    
    @FXML
    private void handleChatbot() {
        handleNavigation("chatbot");
    }
    
    @FXML
    private void handleSettings() {
        handleNavigation("settings");
    }
    
    /**
     * Handle navigation to different screens
     */
    public void handleNavigation(String destination) {
        switch (destination.toLowerCase()) {
            case "dashboard":
                navigateTo("dashboard");
                break;
            case "activity_log":
                navigateTo("activity_log");
                break;
            case "charts":
                navigateTo("charts");
                break;
            case "badges":
                navigateTo("badges");
                break;
            case "tips":
                navigateTo("tips");
                break;
            case "chatbot":
                navigateTo("chatbot");
                break;
            case "settings":
                navigateTo("settings");
                break;
            case "logout":
                handleLogout();
                break;
            default:
                navigateTo("dashboard");
        }
    }
    
    /**
     * Navigate to a specific screen
     */
    private void navigateTo(String screenName) {
        try {
            // Notify current controller it's being deactivated
            if (currentScreen != null && screenControllers.containsKey(currentScreen)) {
                screenControllers.get(currentScreen).onScreenDeactivated();
            }
            
            // Load the FXML file for the requested screen
            String fxmlFile = "/main/resources/fxml/" + screenName + ".fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent screen = loader.load();
            
            // Get the controller and set its reference to this main controller
            BaseController controller = loader.getController();
            controller.setMainController(this);
            
            // Store the controller for later use
            screenControllers.put(screenName, controller);
            
            // Set the new screen in the content area
            contentArea.getChildren().setAll(screen);
            
            // Update current screen and notify controller
            currentScreen = screenName;
            controller.onScreenActivated();
            
            // Update navigation button states
            setActiveButton(screenName);
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation Error", "Could not load the " + screenName + " screen: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogout() {
        boolean confirm = showConfirmation("Logout", "Are you sure you want to logout?");
        if (confirm) {
        	SceneManager sceneManager = new SceneManager();
    		sceneManager.switchToLogin();
        }
    } 
 
    public void refreshSidebar() {
        loadUserData();
    }
    
    /**
     * Update sidebar CO2 data (called by child controllers when CO2 changes)
     */
    public void updateSidebarCO2(double newTotal) {
        if (currentUser != null) {
            currentUser.setTotalCO2Saved(newTotal);
            
            if (totalCO2Label != null) {
                totalCO2Label.setText(String.format("%.1f", newTotal));
            }
            
            if (equivalentLabel != null) {
                updateEquivalentLabel(newTotal);
            }
        }
    }
    
    /**
     * Update sidebar streak (called by child controllers when streak changes)
     */
    public void updateSidebarStreak(int newStreak) {
        if (currentUser != null) {
            currentUser.setCurrentStreak(newStreak);
            
            if (streakLabel != null) {
                streakLabel.setText(newStreak + " day streak");
            }
        }
    }
    
    /**
     * Show success message
     */
    public void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show error message
     */
    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show confirmation dialog
     */
    public boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    
    /**
     * Get current screen name
     */
    public String getCurrentScreen() {
        return currentScreen;
    }
    
    /**
     * Get current user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Refresh current screen
     */
    public void refreshCurrentScreen() {
        if (currentScreen != null && screenControllers.containsKey(currentScreen)) {
            screenControllers.get(currentScreen).refreshScreen();
        }
    }
    
    // Theme and appearance methods (keeping existing functionality)
    
    /**
     * Apply theme changes to the entire application
     */
    public void applyTheme(String themeName) {
        if (themeName == null || themeName.isEmpty()) {
            themeName = "eco"; // Default theme
        }
        
        try {
            Scene scene = getMainScene();
            if (scene == null) {
                System.err.println("Cannot apply theme: Main scene is null");
                return;
            }
            
            removeExistingThemeStylesheets(scene);
            
            String themeStylesheet = getThemeStylesheet(themeName);
            if (themeStylesheet != null) {
                scene.getStylesheets().add(themeStylesheet);
                
                Parent root = scene.getRoot();
                if (root != null) {
                    root.getStyleClass().removeIf(styleClass -> 
                        styleClass.startsWith("theme-"));
                    
                    root.getStyleClass().add("theme-" + themeName.toLowerCase());
                    applyThemeSpecificProperties(root, themeName);
                }
                
                updateAllStageThemes(themeName);
                currentTheme = themeName;
                
                System.out.println("Theme applied successfully: " + themeName);
            } else {
                System.err.println("Theme stylesheet not found: " + themeName);
            }
            
        } catch (Exception e) {
            System.err.println("Failed to apply theme '" + themeName + "': " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Apply font size changes to the entire application
     */
    public void applyFontSize(int fontSize) {
        if (fontSize < 8 || fontSize > 72) {
            System.err.println("Font size out of valid range (8-72): " + fontSize);
            return;
        }
        
        try {
            Scene scene = getMainScene();
            if (scene == null) {
                System.err.println("Cannot apply font size: Main scene is null");
                return;
            }
            
            String fontSizeStyle = createFontSizeStyle(fontSize);
            
            Parent root = scene.getRoot();
            if (root != null) {
                applyFontSizeToNode(root, fontSizeStyle, fontSize);
            }
            
            updateAllStagesFontSize(fontSize);
            currentFontSize = fontSize;
            
            System.out.println("Font size applied successfully: " + fontSize + "px");
            
        } catch (Exception e) {
            System.err.println("Failed to apply font size '" + fontSize + "': " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Scene getMainScene() {
        if (primaryStage != null) {
            return primaryStage.getScene();
        }
        
        if (mainContainer != null) {
            return mainContainer.getScene();
        }
        
        return null;
    }
    
    private void removeExistingThemeStylesheets(Scene scene) {
        scene.getStylesheets().removeIf(stylesheet -> 
            stylesheet.contains("/styles/themes/") || 
            stylesheet.contains("theme-") ||
            stylesheet.endsWith("-theme.css")
        );
    }
    
    private String getThemeStylesheet(String themeName) {
        String stylesheetPath = null;
        
        switch (themeName.toLowerCase()) {
            case "dark":
                stylesheetPath = "/main/resources/css/dark-theme.css";
                break;
            case "light":
                stylesheetPath = "/main/resources/css/light-theme.css";
                break;
            case "eco":
                stylesheetPath = "/main/resources/css/styles.css";
                break;
            default:
                stylesheetPath = "/main/resources/css/styles.css";
                break;
        }
        
        try {
            if (getClass().getResource(stylesheetPath) != null) {
                return getClass().getResource(stylesheetPath).toExternalForm();
            }
        } catch (Exception e) {
            System.err.println("Theme stylesheet not found: " + stylesheetPath);
        }
        
        return null;
    }
    
    private void applyThemeSpecificProperties(Parent root, String themeName) {
        root.getStyleClass().removeIf(styleClass -> 
            styleClass.equals("dark-theme") || 
            styleClass.equals("light-theme") || 
            styleClass.equals("eco-theme")
        );
        
        root.getStyleClass().add(themeName.toLowerCase() + "-theme");
        
        switch (themeName.toLowerCase()) {
            case "dark":
                applyDarkThemeProperties(root);
                break;
            case "light":
                applyLightThemeProperties(root);
                break;
            case "eco":
                applyEcoThemeProperties(root);
                break;
        }
    }
    
    private void applyDarkThemeProperties(Parent root) {
        root.setStyle(root.getStyle() + "; -fx-background-color: #2b2b2b;");
    }
    
    private void applyLightThemeProperties(Parent root) {
        root.setStyle(root.getStyle() + "; -fx-background-color: #ffffff;");
    }
    
    private void applyEcoThemeProperties(Parent root) {
        root.setStyle(root.getStyle() + "; -fx-background-color: #f0f8f0;");
    }
    
    private void updateAllStageThemes(String themeName) {
        for (Stage stage : Stage.getWindows().stream()
                .filter(window -> window instanceof Stage)
                .map(window -> (Stage) window)
                .toList()) {
            
            Scene scene = stage.getScene();
            if (scene != null && scene != getMainScene()) {
                removeExistingThemeStylesheets(scene);
                
                String themeStylesheet = getThemeStylesheet(themeName);
                if (themeStylesheet != null) {
                    scene.getStylesheets().add(themeStylesheet);
                    
                    if (scene.getRoot() != null) {
                        applyThemeSpecificProperties(scene.getRoot(), themeName);
                    }
                }
            }
        }
    }
    
    private String createFontSizeStyle(int fontSize) {
        return String.format(
            "-fx-font-size: %dpx; " +
            ".label { -fx-font-size: %dpx; } " +
            ".button { -fx-font-size: %dpx; } " +
            ".text-field { -fx-font-size: %dpx; } " +
            ".text-area { -fx-font-size: %dpx; } " +
            ".combo-box { -fx-font-size: %dpx; } " +
            ".menu-item { -fx-font-size: %dpx; }",
            fontSize, fontSize, fontSize, fontSize, fontSize, fontSize, fontSize
        );
    }
    
    private void applyFontSizeToNode(Node node, String fontSizeStyle, int fontSize) {
        if (node == null) return;
        
        String currentStyle = node.getStyle() != null ? node.getStyle() : "";
        currentStyle = currentStyle.replaceAll("-fx-font-size:\\s*\\d+(\\.\\d+)?(px|em|pt|%)?;?", "");
        currentStyle += "; -fx-font-size: " + fontSize + "px;";
        node.setStyle(currentStyle);
        
        if (node instanceof Labeled) {
            ((Labeled) node).setStyle(currentStyle);
        } else if (node instanceof TextInputControl) {
            ((TextInputControl) node).setStyle(currentStyle);
        }
        
        if (node instanceof Parent) {
            Parent parent = (Parent) node;
            for (Node child : parent.getChildrenUnmodifiable()) {
                applyFontSizeToNode(child, fontSizeStyle, fontSize);
            }
        }
    }
    
    private void updateAllStagesFontSize(int fontSize) {
        String fontSizeStyle = createFontSizeStyle(fontSize);
        
        for (Stage stage : Stage.getWindows().stream()
                .filter(window -> window instanceof Stage)
                .map(window -> (Stage) window)
                .toList()) {
            
            Scene scene = stage.getScene();
            if (scene != null && scene != getMainScene()) {
                if (scene.getRoot() != null) {
                    applyFontSizeToNode(scene.getRoot(), fontSizeStyle, fontSize);
                }
            }
        }
    }
    
    public String getCurrentTheme() {
        return currentTheme != null ? currentTheme : "eco";
    }
    
    public int getCurrentFontSize() {
        return currentFontSize > 0 ? currentFontSize : 12;
    }
    
    public void previewTheme(String themeName) {
        applyTheme(themeName);
    }
    
    public void previewFontSize(int fontSize) {
        applyFontSize(fontSize);
    }
    
    public void resetThemeToDefault() {
        applyTheme("eco");
    }
    
    public void resetFontSizeToDefault() {
        applyFontSize(12);
    }

    private void initializeProfileComponents() {
        try {
            // Create image view for profile photo
            profileImageView = new ImageView();
            profileImageView.setFitWidth(60);
            profileImageView.setFitHeight(60);
            profileImageView.setPreserveRatio(true);
            profileImageView.setVisible(false); // Initially hidden until we check for photo
            
            // Create initials label
            profileInitialsLabel = new Label();
            profileInitialsLabel.getStyleClass().add("title-medium");
            profileInitialsLabel.setTextFill(Color.web("#0a0a23"));
            
            // Add image view and label to the profile container
            profileContainer.getChildren().addAll(profileImageView, profileInitialsLabel);
            
        } catch (Exception e) {
            System.err.println("Error initializing profile components: " + e.getMessage());
        }
    }
 public void updateProfilePhoto(String imagePath) {
     Platform.runLater(() -> {
         if (imagePath != null && !imagePath.isEmpty()) {
             try {
                 File imageFile = new File(imagePath);
                 if (imageFile.exists()) {
                     // Show image, hide initials
                     Image profileImage = new Image(imageFile.toURI().toString());
                     profileImageView.setImage(profileImage);
                     profileImageView.setVisible(true);
                     
                     if (profileInitialsLabel != null) {
                         profileInitialsLabel.setVisible(false);
                     }
                     
                     // Apply circular clip to image
                     Circle clip = new Circle(
                         profileImageView.getFitWidth() / 2,
                         profileImageView.getFitHeight() / 2,
                         profileImageView.getFitWidth() / 2
                     );
                     profileImageView.setClip(clip);
                     
                     // Update user object
                     if (currentUser != null) {
                         currentUser.setProfilePicture(imagePath);
                     }
                 } else {
                     // File doesn't exist, show initials instead
                     showUserInitials();
                 }
             } catch (Exception e) {
                 System.err.println("Error loading profile image: " + e.getMessage());
                 showUserInitials();
             }
         } else {
             // No profile photo, show initials
             showUserInitials();
         }
     });
 }
    public void applyAppearanceSettings(String themeName, int fontSize) {
        applyTheme(themeName);
        applyFontSize(fontSize);
    }
    private void showUserInitials() {
        if (profileInitialsLabel != null && currentUser != null) {
            profileInitialsLabel.setText(currentUser.getInitials());
            profileInitialsLabel.setVisible(true);
        }
        
        if (profileImageView != null) {
            profileImageView.setVisible(false);
            profileImageView.setImage(null); // Clear any previous image
        }
    }

}