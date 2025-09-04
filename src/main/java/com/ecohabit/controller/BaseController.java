package main.java.com.ecohabit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Base controller class that provides common functionality for all screen controllers.
 * All specific controllers should extend this class to get MainController integration.
 */
public abstract class BaseController implements Initializable {
    
    protected MainController mainController;
    
    // Common navigation buttons that appear in sidebar
    @FXML protected Button dashboardBtn;
    @FXML protected Button activityLogBtn;
    @FXML protected Button chartsBtn;
    @FXML protected Button badgesBtn;
    @FXML protected Button tipsBtn;
    @FXML protected Button chatbotBtn;
    @FXML protected Button settingsBtn;
    @FXML protected Button logoutBtn;
    
    /**
     * Set the MainController reference
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    /**
     * Initialize method called by FXML loader
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupNavigationButtons();
        initializeScreen();
    } 
    
    /** 
     * Setup common navigation button handlers
     */
    private void setupNavigationButtons() {
        if (dashboardBtn != null) {
            dashboardBtn.setOnAction(_ -> navigateTo("dashboard"));
        }
        if (activityLogBtn != null) {
            activityLogBtn.setOnAction(_ -> navigateTo("activity_log"));
        }
        if (chartsBtn != null) {
            chartsBtn.setOnAction(_ -> navigateTo("charts"));
        }
        if (settingsBtn != null) {
            settingsBtn.setOnAction(_ -> navigateTo("settings"));
        }
        if (logoutBtn != null) {
            logoutBtn.setOnAction(_ -> navigateTo("logout"));
        } 
        
        // Add handlers for other navigation buttons as needed
        setupAdditionalNavigation();
    }
    
    /**
     * Navigate to a specific screen
     */
    protected void navigateTo(String destination) {
        if (mainController != null) {
            mainController.handleNavigation(destination);
        }
    }
    
    /**
     * Abstract method for screen-specific initialization
     * Override this in subclasses instead of initialize()
     */
    public abstract void initializeScreen();
    
    /**
     * Override this to setup additional navigation buttons
     */
    protected void setupAdditionalNavigation() {
        // Override in subclasses if needed
    }
    
    /**
     * Utility method to show success message
     */
    protected void showSuccess(String title, String message) {
        if (mainController != null) {
            mainController.showSuccess(title, message);
        }
    }
    
    /**
     * Utility method to show confirmation dialog
     */
    protected boolean showConfirmation(String title, String message) {
        if (mainController != null) {
            return mainController.showConfirmation(title, message);
        }
        return false;
    }
    
    /**
     * Get current screen name
     */
    protected String getCurrentScreen() {
        if (mainController != null) {
            return mainController.getCurrentScreen();
        }
        return null;
    }
    
    /**
     * Update navigation button states based on current screen
     */
    protected void updateNavigationState(String activeScreen) {
        // Remove active class from all buttons
        clearActiveStates();
        
        // Set active state for current screen
        switch (activeScreen.toLowerCase()) {
            case "dashboard":
                if (dashboardBtn != null) {
                    dashboardBtn.getStyleClass().add("active");
                }
                break;
            case "activity_log":
                if (activityLogBtn != null) {
                    activityLogBtn.getStyleClass().add("active");
                }
                break;
            case "charts":
                if (chartsBtn != null) {
                    chartsBtn.getStyleClass().add("active");
                }
                break;
            case "settings":
                if (settingsBtn != null) {
                    settingsBtn.getStyleClass().add("active");
                }
                break;
        }
    }
    
 // In your BaseController class
    protected void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Clear active states from all navigation buttons
     */
    private void clearActiveStates() {
        Button[] navButtons = {dashboardBtn, activityLogBtn, chartsBtn, settingsBtn};
        for (Button btn : navButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("active");
            }
        }
    }
    
    /**
     * Called when screen becomes active
     */
    public void onScreenActivated() {
        // Override in subclasses if needed
        String currentScreen = getCurrentScreen();
        if (currentScreen != null) {
            updateNavigationState(currentScreen);
        }
    }
    
    /**
     * Called when screen becomes inactive
     */
    public void onScreenDeactivated() {
        // Override in subclasses if needed
    }
    
    /**
     * Refresh screen data
     */
    public void refreshScreen() {
        // Override in subclasses to implement refresh logic
    }
    public MainController getMainController() {
        return mainController;
    }
}