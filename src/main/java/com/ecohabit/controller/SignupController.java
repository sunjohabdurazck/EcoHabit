package main.java.com.ecohabit.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import main.java.com.ecohabit.model.User;
import main.java.com.ecohabit.service.UserService;
import main.java.com.ecohabit.util.*;
import main.java.com.ecohabit.SessionManager;

public class SignupController extends BaseController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField ageField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private ComboBox<String> dietComboBox;
    @FXML
    private ComboBox<String> transportComboBox;
    @FXML
    private ComboBox<String> userTypeComboBox;
    @FXML
    private Button signupButton;
    @FXML
    private Button googleSignupButton;
    @FXML
    private Hyperlink loginLink;
    @FXML
    private VBox statusContainer;
    @FXML
    private Label statusMessage;
    
    // Error labels for field-specific validation
    @FXML private Label nameErrorLabel;
    @FXML private Label emailErrorLabel;
    @FXML private Label ageErrorLabel;
    @FXML private Label genderErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label confirmPasswordErrorLabel;
    @FXML private Label dietErrorLabel;
    @FXML private Label transportErrorLabel;
    @FXML private Label userTypeErrorLabel;

    private UserService userService;

    @Override
    public void initializeScreen() {
        userService = new UserService();
        initializeComboBoxes();
        setupEventHandlers();
        updateNavigationState("signup");
        
        // Add real-time password validation
        setupPasswordValidation();
        
        // Initialize error labels
        clearFieldErrors();
        
        // Setup text change listeners to clear errors when user types
        setupTextChangeListeners();
    }

    private void initializeComboBoxes() {
        // Initialize gender options
        genderComboBox.getItems().addAll("Male", "Female");
        
        // Initialize diet options
        dietComboBox.getItems().addAll("Omnivore", "Vegetarian", "Vegan", "Pescatarian", "Flexitarian");
        
        // Initialize transport options
        transportComboBox.getItems().addAll("Car", "Public Transport", "Bicycle", "Walk", "Mixed");
        
        // Initialize user type options
        userTypeComboBox.getItems().addAll("Casual User", "Eco Enthusiast", "Environmental Student", "Sustainability Professional");
    }

    private void setupEventHandlers() {
        signupButton.setOnAction(event -> handleSignup());
        googleSignupButton.setOnAction(event -> handleGoogleSignup());
        loginLink.setOnAction(event -> navigateToLogin());
    }
    
    private void setupTextChangeListeners() {
        // Add listeners to clear error messages when user starts typing
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                nameErrorLabel.setVisible(false);
                nameErrorLabel.setManaged(false);
            }
        });
        
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                emailErrorLabel.setVisible(false);
                emailErrorLabel.setManaged(false);
            }
        });
        
        ageField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                ageErrorLabel.setVisible(false);
                ageErrorLabel.setManaged(false);
            }
        });
        
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                passwordErrorLabel.setVisible(false);
                passwordErrorLabel.setManaged(false);
            }
        });
        
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                confirmPasswordErrorLabel.setVisible(false);
                confirmPasswordErrorLabel.setManaged(false);
            }
        });
        
        // ComboBox listeners
        genderComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                genderErrorLabel.setVisible(false);
                genderErrorLabel.setManaged(false);
            }
        });
        
        dietComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                dietErrorLabel.setVisible(false);
                dietErrorLabel.setManaged(false);
            }
        });
        
        transportComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                transportErrorLabel.setVisible(false);
                transportErrorLabel.setManaged(false);
            }
        });
        
        userTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                userTypeErrorLabel.setVisible(false);
                userTypeErrorLabel.setManaged(false);
            }
        });
    }

    private void setupPasswordValidation() {
        // Add listeners for real-time password validation
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswordStrength();
        });
        
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswordConfirmation();
        });
    }

    private void validatePasswordStrength() {
        String password = passwordField.getText();
        
        if (password.isEmpty()) {
            passwordField.setStyle("");
            return;
        }
        
        if (PasswordValidator.isValid(password)) {
            passwordField.setStyle("-fx-border-color: #38ef7d; -fx-border-width: 2px;");
        } else {
            passwordField.setStyle("-fx-border-color: #ff6b9d; -fx-border-width: 2px;");
        }
    }

    private void validatePasswordConfirmation() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (confirmPassword.isEmpty()) {
            confirmPasswordField.setStyle("");
            return;
        }
        
        if (password.equals(confirmPassword)) {
            confirmPasswordField.setStyle("-fx-border-color: #38ef7d; -fx-border-width: 2px;");
        } else {
            confirmPasswordField.setStyle("-fx-border-color: #ff6b9d; -fx-border-width: 2px;");
        }
    }

    private void handleSignup() {
        clearFieldErrors();
        
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String gender = genderComboBox.getValue();
        String diet = dietComboBox.getValue();
        String transport = transportComboBox.getValue();
        String userType = userTypeComboBox.getValue();
        
        // Validate input
        boolean hasErrors = false;
        
        if (name.isEmpty()) {
            showFieldError(nameErrorLabel, "Name is required");
            hasErrors = true;
        }
        
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
        } else if (!PasswordValidator.isValid(password)) {
            String feedback = PasswordValidator.getValidationFeedback(password);
            showFieldError(passwordErrorLabel, feedback);
            hasErrors = true;
        }
        
        if (confirmPassword.isEmpty()) {
            showFieldError(confirmPasswordErrorLabel, "Please confirm your password");
            hasErrors = true;
        } else if (!password.equals(confirmPassword)) {
            showFieldError(confirmPasswordErrorLabel, "Passwords do not match");
            hasErrors = true;
        }
        
        if (!ageStr.isEmpty()) {
            try {
                int age = Integer.parseInt(ageStr);
                if (age < 0 || age > 120) {
                    showFieldError(ageErrorLabel, "Please enter a valid age (0-120)");
                    hasErrors = true;
                }
            } catch (NumberFormatException e) {
                showFieldError(ageErrorLabel, "Please enter a valid age");
                hasErrors = true;
            }
        }
        
        if (hasErrors) {
            return;
        }
        
        // Check if email already exists
        if (userService.doesEmailExist(email)) {
            showFieldError(emailErrorLabel, "Email already exists. Please use a different email.");
            return;
        }
        
        int age = 0;
        if (!ageStr.isEmpty()) {
            age = Integer.parseInt(ageStr);
        }
        
        // Create new user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setHashedPassword(password); // This should hash the password in the User model
        newUser.setFirstName(name);
        newUser.setLastName(""); // You might want to split name into first/last
        newUser.setAge(age);
        newUser.setGender(gender);
        newUser.setDietPreference(diet);
        newUser.setTransportPreference(transport);
        newUser.setUserType(userType);
        newUser.setCurrentStreak(0);
        newUser.setTotalCO2Saved(0.0);
         
        // Save user to database
        boolean success = userService.createUser(newUser);
        
        if (success) { 
            showStatus("Account created successfully!", "success");
            
            // Navigate to dashboard after successful signup
            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(2), e -> {
                    SessionManager.setCurrentUser(newUser);
                    SceneManager sceneManager = new SceneManager();
                    sceneManager.switchToMain();
                })
            );
            timeline.play();
        } else {
            showStatus("Failed to create account. Please try again.", "error");
        }
    }

    private void handleGoogleSignup() {
        // TODO: Implement Google sign-up integration
        showStatus("Google sign-up functionality coming soon!", "info");
    }

    private void navigateToLogin() {
        SceneManager sceneManager = new SceneManager();
        sceneManager.switchToLogin();
    }
    
    private void showFieldError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void clearFieldErrors() {
        nameErrorLabel.setVisible(false);
        nameErrorLabel.setManaged(false);
        emailErrorLabel.setVisible(false);
        emailErrorLabel.setManaged(false);
        ageErrorLabel.setVisible(false);
        ageErrorLabel.setManaged(false);
        genderErrorLabel.setVisible(false);
        genderErrorLabel.setManaged(false);
        passwordErrorLabel.setVisible(false);
        passwordErrorLabel.setManaged(false);
        confirmPasswordErrorLabel.setVisible(false);
        confirmPasswordErrorLabel.setManaged(false);
        dietErrorLabel.setVisible(false);
        dietErrorLabel.setManaged(false);
        transportErrorLabel.setVisible(false);
        transportErrorLabel.setManaged(false);
        userTypeErrorLabel.setVisible(false);
        userTypeErrorLabel.setManaged(false);
    }

    private void showStatus(String message, String type) {
        statusMessage.setText(message);
        statusContainer.setVisible(true);
        
        if ("error".equals(type)) {
            statusContainer.setStyle("-fx-background-color: rgba(255,107,157,0.2);");
            statusMessage.getStyleClass().removeAll("success-text", "info-text");
            statusMessage.getStyleClass().add("error-text");
        } else if ("success".equals(type)) {
            statusContainer.setStyle("-fx-background-color: rgba(56,239,125,0.2);");
            statusMessage.getStyleClass().removeAll("error-text", "info-text");
            statusMessage.getStyleClass().add("success-text");
        } else {
            statusContainer.setStyle("-fx-background-color: rgba(78,205,196,0.2);");
            statusMessage.getStyleClass().removeAll("error-text", "success-text");
            statusMessage.getStyleClass().add("info-text");
        }
        
        // Auto-hide after 5 seconds
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(5), e -> {
                statusContainer.setVisible(false);
            })
        );
        timeline.play();
    }

    @Override
    public void onScreenActivated() {
        super.onScreenActivated();
        // Clear form fields when screen is activated
        clearForm();
    }

    private void clearForm() {
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        nameField.clear();
        ageField.clear();
        genderComboBox.getSelectionModel().clearSelection();
        dietComboBox.getSelectionModel().clearSelection();
        transportComboBox.getSelectionModel().clearSelection();
        userTypeComboBox.getSelectionModel().clearSelection();
        statusContainer.setVisible(false);
        
        // Reset field styles
        passwordField.setStyle("");
        confirmPasswordField.setStyle("");
        
        // Clear error messages
        clearFieldErrors();
    }
}