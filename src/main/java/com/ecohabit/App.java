package main.java.com.ecohabit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;
import main.java.com.ecohabit.util.SceneManager;
import main.java.com.ecohabit.dao.DatabaseConnection;
import main.java.com.ecohabit.service.DatabaseService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EcoHabit - Personal Sustainability Tracker
 * Fixed version with proper resource loading and error handling
 */ 
public class App extends Application {
	
    
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static final String APP_NAME = "EcoHabit";
    private static final String VERSION = "1.0.0";
    
    // Application configuration
    private static final int MIN_WIDTH = 1200;
    private static final int MIN_HEIGHT = 800;
    private static final int PREFERRED_WIDTH = 1400;
    private static final int PREFERRED_HEIGHT = 900;
    
    // Scene management
    private Stage primaryStage;
    private Scene currentScene;
    
    // Application state
    private boolean isDarkMode = true;

    @Override
    public void start(Stage stage) {
    	LOGGER.info("Initializing database...");
        DatabaseConnection.initializeDatabase(); // Create tables if missing
        initializeDatabase();

        LOGGER.info("Starting EcoHabit application...");
        this.primaryStage = stage;

        SceneManager.setPrimaryStage(stage);

        setupPrimaryStage();
        showSplashScreen();  
    }
    
    private void initializeDatabase() {
        try {
            DatabaseService dbService = DatabaseService.getInstance();
            dbService.initialize();
            System.out.println("Database initialized successfully");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
            // You might want to show an alert here
            Platform.exit();
        }
    }
    /**
     * Configure the primary stage with modern styling
     */
    private void setupPrimaryStage() {
        primaryStage.setTitle(APP_NAME + " " + VERSION);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        
        // Set application icon
        try {
            InputStream iconStream = getClass().getResourceAsStream("/main/resources/images/app_icon.png");
            if (iconStream != null) {
                Image icon = new Image(iconStream);
                primaryStage.getIcons().add(icon);
                LOGGER.info("Application icon loaded successfully");
            } else {
                LOGGER.warning("Could not load application icon - file not found");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load application icon", e);
        }
        
        // Configure window properties
        primaryStage.setResizable(true);
    }
    
    

    /**
     * Show animated splash screen while loading
     */
    private void showSplashScreen() {
        try {
            // Create a simple splash screen programmatically
            Parent splashRoot = createSplashScreen();
            Scene splashScene = new Scene(splashRoot, 400, 300);
            
            // Apply modern styling
            splashScene.setFill(Color.TRANSPARENT);
            
            // Configure splash stage
            Stage splashStage = new Stage(StageStyle.TRANSPARENT);
            splashStage.setScene(splashScene);
            splashStage.centerOnScreen();
            splashStage.setAlwaysOnTop(true);
            
            // Add drop shadow effect
            DropShadow shadow = new DropShadow();
            shadow.setRadius(50);
            shadow.setColor(Color.rgb(0, 0, 0, 0.5));
            splashRoot.setEffect(shadow);
            
            // Animate splash entrance
            animateSplashEntrance(splashRoot);
            
            splashStage.show();
            
            // Auto-close splash after 2 seconds and load main application
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    
                    Platform.runLater(() -> {
                        animateSplashExit(splashRoot, splashStage);
                        loadMainApplication();
                    });
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not show splash screen", e);
            // Continue without splash screen
            loadMainApplication();
        }
    }
    
    private Parent createSplashScreen() {
        // Create a simple splash screen with Java code
        VBox vbox = new VBox();
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        vbox.setSpacing(20);
        vbox.setStyle("-fx-background-color: rgba(14, 22, 40, 0.95); -fx-background-radius: 20; -fx-padding: 40;");
        
        // Add app icon/logo
        javafx.scene.text.Text logo = new javafx.scene.text.Text("🌱");
        logo.setStyle("-fx-font-size: 48px;");
        
        // Add app name
        javafx.scene.text.Text appName = new javafx.scene.text.Text("EcoHabit");
        appName.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: white;");
        
        // Add version
        javafx.scene.text.Text version = new javafx.scene.text.Text("Version " + VERSION);
        version.setStyle("-fx-font-size: 14px; -fx-fill: rgba(255,255,255,0.7);");
        
        // Add loading indicator 
        ProgressIndicator progress = new ProgressIndicator();
        progress.setStyle("-fx-progress-color: #4ecdc4;");
        
        vbox.getChildren().addAll(logo, appName, version, progress);
        return vbox;
    }

    private void animateSplashExit(Parent root, Stage splashStage) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> splashStage.close());
        fadeOut.play();
    }

    private void animateSplashEntrance(Parent root) {
        root.setScaleX(0.8);
        root.setScaleY(0.8);
        root.setOpacity(0);
        
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.8), root);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        FadeTransition fade = new FadeTransition(Duration.seconds(0.8), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        ParallelTransition entrance = new ParallelTransition(scale, fade);
        entrance.play();
    }

    /**
     * Load the main application interface
     */
    private void loadMainApplication() {
        LOGGER.info("Loading main application interface...");
        
        try {
            // Try to load the FXML file
            Parent root = loadFXMLWithFallback("/main/resources/fxml/main.fxml");
            
            // Create scene with transparent background for glassmorphism 
            currentScene = new Scene(root, PREFERRED_WIDTH, PREFERRED_HEIGHT);
            currentScene.setFill(Color.TRANSPARENT);
              
            // Apply stylesheets
            applyStylesheets(currentScene);
            
            Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
                System.err.println("Uncaught exception: " + throwable.getMessage());
                throwable.printStackTrace();
            });
            
            // Set scene with fade transition
            primaryStage.setScene(currentScene);
             
            // Show main window with animation
            primaryStage.show();
            primaryStage.centerOnScreen();  
            animateMainWindowEntrance();
            
            LOGGER.info("Main application loaded successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load main application", e);
            showErrorDialog("Fatal Error", 
                "Cannot start application. Please reinstall EcoHabit.");
            Platform.exit();
        }
    }
    
    
    /**
     * Load FXML with fallback to programmatic UI if FXML fails
     */
    private Parent loadFXMLWithFallback(String fxmlPath) {
        try {
            // Try to load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            return root;
        } catch (IOException | NullPointerException e) {
            LOGGER.log(Level.WARNING, "FXML loading failed, using fallback UI: " + fxmlPath, e);
            // Create a basic UI programmatically as fallback
            return createFallbackUI();
        }
    }
    
    /**
     * Create a fallback UI programmatically
     */
    private Parent createFallbackUI() {
        VBox fallbackUI = new VBox(20);
        fallbackUI.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #3498db); -fx-padding: 40;");
        fallbackUI.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Application title
        Label title = new Label("EcoHabit");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");
        
        // Error message
        Label errorMsg = new Label("Unable to load interface files");
        errorMsg.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 16px;");
        
        // Instructions
        Label instructions = new Label("Please check your installation and ensure all files are present.");
        instructions.setStyle("-fx-text-fill: #dfe6e9; -fx-font-size: 14px; -fx-wrap-text: true;");
        instructions.setMaxWidth(400); 
        
        // Continue button
        Button continueButton = new Button("Continue with Basic UI");
        continueButton.setStyle("-fx-background-color: #00b894; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        continueButton.setOnAction(e -> {
            // This already is the basic UI, so no action needed
        });
        
        fallbackUI.getChildren().addAll(title, errorMsg, instructions, continueButton);
        return fallbackUI;
    }

    private void animateMainWindowEntrance() {
        if (currentScene != null && currentScene.getRoot() != null) {
            currentScene.getRoot().setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), currentScene.getRoot());
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }
    }

    /**
     * Apply modern stylesheets to scene
     */
    private void applyStylesheets(Scene scene) {
        try {
            // Base stylesheet
            String baseCss = "/main/resources/css/styles.css";
            if (getClass().getResource(baseCss) != null) {
                scene.getStylesheets().add(getClass().getResource(baseCss).toExternalForm());
            } else {
                LOGGER.warning("Stylesheet not found: " + baseCss);
                // Apply some basic styles programmatically
                applyDefaultStyles();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load stylesheets", e);
            applyDefaultStyles();
        }
    }
    
    /**
     * Apply default styles programmatically if CSS files are missing
     */
    private void applyDefaultStyles() {
        // This method would apply basic styling if CSS files are missing
        // For simplicity, we're relying on inline styles in this example
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Add custom icon if available
        try {
            InputStream iconStream = getClass().getResourceAsStream("/main/resources/images/app_icon.png");
            if (iconStream != null) {
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception e) {
            // Continue without custom icon
        }
        
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        // Set better logging format
        System.setProperty("java.util.logging.SimpleFormatter.format", 
                          "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        
        LOGGER.info("Launching EcoHabit application...");
        launch(args);
    }
}