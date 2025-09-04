package main.java.com.ecohabit.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import main.java.com.ecohabit.SessionManager;
import main.java.com.ecohabit.model.User;
import main.java.com.ecohabit.model.UserSession;
import main.java.com.ecohabit.service.AuthenticationService;
import main.java.com.ecohabit.service.GoogleAuthService;
import main.java.com.ecohabit.util.OAuthCallbackServer;
import main.java.com.ecohabit.util.SceneManager;
import main.java.com.ecohabit.util.ValidationUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends BaseController {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private Button togglePasswordButton;
    @FXML private Button loginButton;
    @FXML private Button googleSigninButton;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private Hyperlink signupLink;
    @FXML private ProgressIndicator loginProgress;
    @FXML private VBox statusContainer;
    @FXML private Label statusMessage;
    @FXML private Label emailErrorLabel;
    @FXML private Label passwordErrorLabel;
    
    private AuthenticationService authService;
    private boolean isPasswordVisible = false;
    private SceneManager sceneManager;
    
    @Override
    public void initializeScreen() { 
        authService = AuthenticationService.getInstance();
        sceneManager = SceneManager.getInstance();
        setupEventHandlers();
        setupPasswordToggle();
        updateNavigationState("login");
        
        // Initialize error labels
        clearFieldErrors();
    }
    
    private void setupEventHandlers() {
        loginButton.setOnAction(event -> handleLogin());
        googleSigninButton.setOnAction(event -> handleGoogleSignin());
        forgotPasswordLink.setOnAction(event -> handleForgotPassword());
        signupLink.setOnAction(event -> handleSignupLink());
         
        // Enter key support
        emailField.setOnAction(event -> passwordField.requestFocus());
        passwordField.setOnAction(event -> handleLogin());
        visiblePasswordField.setOnAction(event -> handleLogin());
        
        // Clear errors when user starts typing
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                emailErrorLabel.setVisible(false);
                emailErrorLabel.setManaged(false);
            }
        });
        
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                passwordErrorLabel.setVisible(false);
                passwordErrorLabel.setManaged(false);
            }
        });
        
        visiblePasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                passwordErrorLabel.setVisible(false);
                passwordErrorLabel.setManaged(false);
            }
        });
    }
    
    private void setupPasswordToggle() {
        togglePasswordButton.setText("👁");
        togglePasswordButton.setOnAction(event -> togglePasswordVisibility());
    }
    
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            togglePasswordButton.setText("👁");
            isPasswordVisible = false;
        } else {
            // Show password
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordButton.setText("🙈");
            isPasswordVisible = true;
        }
    }
    
    private void handleLogin() {
        clearFieldErrors();
        
        String email = emailField.getText().trim();
        String password = isPasswordVisible ? visiblePasswordField.getText() : passwordField.getText();
        
        // Validate input
        boolean hasErrors = false;
        
        if (email.isEmpty()) {
            showFieldError(emailErrorLabel, "Email is required");
            hasErrors = true;
        } else if (!ValidationUtil.isValidEmail(email)) {
            showFieldError(emailErrorLabel, "Please enter a valid email address");
            hasErrors = true;
        }
        
        if (password.isEmpty()) {
            showFieldError(passwordErrorLabel, "Password is required");
            hasErrors = true;
        }
        
        if (hasErrors) {
            return;
        }
        
        if (!authService.isDatabaseInitialized()) {
            showStatus("Database not initialized. Please restart the application.", "error");
            return;
        }
        
        // Disable login button and show progress
        setLoginInProgress(true);
        
        // Perform login in background thread
        Task<User> loginTask = new Task<User>() {
            @Override
            protected User call() throws Exception {
                // Authenticate user
                User user = authService.login(email, password);

                if (user == null) {
                    throw new Exception("Invalid email or password");
                }
                return user;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    setLoginInProgress(false);
                    User user = getValue();
                    if (user != null) {
                        UserSession.getInstance().login(user);
                        showStatus("Login successful! Welcome back!", "success");
                        System.out.println("Login successful! Welcome back!");
                        SessionManager.setCurrentUser(user);
                        
                        // Delay before switching scenes to show success message
                        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                            sceneManager.switchToMain();
                        }));
                        timeline.play();
                    } else {
                        showFieldError(passwordErrorLabel, "Invalid email or password");
                        showStatus("Invalid email or password. Please try again.", "error");
                        System.out.println("Invalid email or password. Please try again.");
                    }
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    setLoginInProgress(false);
                    String errorMessage = getException().getMessage();
                    
                    if (errorMessage.contains("Invalid email or password")) {
                        showFieldError(passwordErrorLabel, "Invalid email or password");
                    }
                    
                    showStatus("Login failed: " + errorMessage, "error");
                });
            } 
        };
        
        new Thread(loginTask).start();
    }
    
    private void handleForgotPassword() {
        clearFieldErrors();
        
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) { 
            showFieldError(emailErrorLabel, "Please enter your email address first");
            emailField.requestFocus();
            return;
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            showFieldError(emailErrorLabel, "Please enter a valid email address");
            return;
        }
        
        // TODO: Implement password reset functionality
        showStatus("Password reset instructions will be sent to: " + email, "info");
    }
    
    private void handleSignupLink() {
        // Switch to signup screen
        sceneManager.switchToSignup();
    }

    private void setLoginInProgress(boolean inProgress) {
        loginButton.setDisable(inProgress);
        googleSigninButton.setDisable(inProgress);
        loginProgress.setVisible(inProgress);
        
        if (inProgress) {
            loginButton.setText("Signing In...");
        } else {
            loginButton.setText("Sign In");
        }
    }

    private void handleGoogleSignin() {
        setLoginInProgress(true);
        
        Task<User> googleLoginTask = new Task<User>() {
            @Override
            protected User call() throws Exception {
                try {
                    // Start callback server
                    OAuthCallbackServer callbackServer = new OAuthCallbackServer();
                    callbackServer.start();
                    
                    // Get authorization URL and open in browser
                    String authUrl = GoogleAuthService.getInstance().getAuthorizationUrl();
                    Platform.runLater(() -> {
                        try {
                            java.awt.Desktop.getDesktop().browse(new java.net.URI(authUrl));
                        } catch (Exception e) {
                            showStatus("Failed to open browser. Please visit: " + authUrl, "info");
                        }
                    });
                    
                    // Wait for authorization code
                    String code = callbackServer.getCodeFuture().get();
                    if (code == null) {
                        throw new Exception("Authorization failed");
                    }
                    
                    // Exchange code for tokens and login
                    return AuthenticationService.getInstance().loginWithGoogle(code);
                    
                } catch (Exception e) {
                    throw new Exception("Google Sign-In failed: " + e.getMessage());
                }
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    setLoginInProgress(false);
                    User user = getValue();
                    if (user != null) {
                        UserSession.getInstance().login(user);
                        showStatus("Google Sign-In successful! Welcome!", "success");
                        
                        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                            navigateTo("dashboard");
                        }));
                        timeline.play();
                    } else {
                        showStatus("Google Sign-In failed. Please try again.", "error");
                    }
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    setLoginInProgress(false);
                    showStatus("Google Sign-In failed: " + getException().getMessage(), "error");
                });
            }
        };
        
        new Thread(googleLoginTask).start();
    }
    
    private void showFieldError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void clearFieldErrors() {
        emailErrorLabel.setVisible(false);
        emailErrorLabel.setManaged(false);
        passwordErrorLabel.setVisible(false);
        passwordErrorLabel.setManaged(false);
    }
    
    private void showStatus(String message, String type) {
        System.out.println("showStatus called: " + message + " | type: " + type);
        
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(type.toUpperCase());
            alert.setHeaderText(null);
            alert.setContentText(message);
            
            // Style based on type
            DialogPane dialogPane = alert.getDialogPane();
            switch (type) {
                case "error":
                    alert.setAlertType(Alert.AlertType.ERROR);
                    dialogPane.setStyle("-fx-border-color: #DC3545; -fx-border-width: 2px;");
                    break;
                case "success":
                    alert.setAlertType(Alert.AlertType.INFORMATION);
                    dialogPane.setStyle("-fx-border-color: #28A745; -fx-border-width: 2px;");
                    break;
                case "info":
                    alert.setAlertType(Alert.AlertType.INFORMATION);
                    dialogPane.setStyle("-fx-border-color: #17A2B8; -fx-border-width: 2px;");
                    break;
            }
            
            alert.show();
            
            // Auto-close after 3 seconds
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> alert.close()));
            timeline.play();
        });
    }
}