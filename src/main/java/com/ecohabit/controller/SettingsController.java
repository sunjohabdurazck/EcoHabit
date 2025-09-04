package main.java.com.ecohabit.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.geometry.Insets;
import java.util.prefs.Preferences;

import main.java.com.ecohabit.SessionManager;
import main.java.com.ecohabit.model.User;
import main.java.com.ecohabit.model.UserSettings;
import main.java.com.ecohabit.service.SettingsService;
import main.java.com.ecohabit.util.ThemeManager;
import main.java.com.ecohabit.service.NotificationService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.Locale;

/**
 * Complete Settings Controller with full functionality
 * Handles all settings sections: Profile, Appearance, Notifications, Data Management, Privacy, and About
 */
public class SettingsController extends BaseController {
	 private Preferences preferences;
    
    // Sidebar Navigation
    @FXML private Button profileSettingsBtn;
    @FXML private Button appearanceBtn;
    @FXML private Button notificationsBtn;
    @FXML private Button dataManagementBtn;
    @FXML private Button privacyBtn;
    @FXML private Button aboutBtn;
    @FXML private Button teamProfileBtn;
    
    // Quick Actions
    @FXML private Button exportDataBtn;
    @FXML private Button backupBtn;
    
    // Section Containers
    @FXML private VBox profileSection;
    @FXML private VBox appearanceSection;
    @FXML private VBox notificationsSection;
    @FXML private VBox dataSection;
    @FXML private VBox privacySection;
    @FXML private VBox aboutSection;
    @FXML private VBox teamProfileSection;
    
    // User Profile Section
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;
    @FXML private Button changePhotoBtn;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField locationField;
    @FXML private Button resetProfileBtn;
    @FXML private Button saveProfileBtn;
    
    // Lifestyle Preferences
    @FXML private ComboBox<String> dietComboBox;
    @FXML private ComboBox<String> transportComboBox;
    @FXML private ComboBox<String> userTypeComboBox;
    @FXML private TextField dailyGoalField;
    @FXML private Button resetLifestyleBtn;
    @FXML private Button saveLifestyleBtn;
    
    // Appearance Settings
    @FXML private RadioButton lightThemeRadio;
    @FXML private RadioButton darkThemeRadio;
    @FXML private RadioButton ecoThemeRadio;
    @FXML private RadioButton systemThemeRadio;
    @FXML private ToggleGroup themeGroup;
    @FXML private Slider fontSizeSlider;
    @FXML private Label fontSizePreviewLabel;
    @FXML private ComboBox<Locale> languageComboBox;
    @FXML private Button applyLanguageBtn;
    @FXML private CheckBox highContrastCheckbox;
    @FXML private CheckBox largeButtonsCheckbox;
    @FXML private CheckBox reduceAnimationsCheckbox;
    @FXML private CheckBox screenReaderSupportCheckbox;
    @FXML private Button resetAppearanceBtn;
    @FXML private Button previewAppearanceBtn;
    @FXML private Button saveAppearanceBtn;
    
    // Notification Settings
    @FXML private CheckBox dailyReminderCheckbox;
    @FXML private CheckBox weeklySummaryCheckbox;
    @FXML private CheckBox achievementCheckbox;
    @FXML private CheckBox ecoTipsCheckbox;
    @FXML private CheckBox goalCheckbox;
    @FXML private CheckBox socialCheckbox;
    @FXML private CheckBox updateCheckbox;
    @FXML private Spinner<LocalTime> reminderTimeSpinner;
    @FXML private Spinner<LocalTime> quietHoursStartSpinner;
    @FXML private Spinner<LocalTime> quietHoursEndSpinner;
    @FXML private ComboBox<DayOfWeek> weeklySummaryDayComboBox;
    @FXML private CheckBox weekendsOnlyCheckbox;
    @FXML private ComboBox<String> soundComboBox;
    @FXML private Button playSoundBtn;
    @FXML private Slider volumeSlider;
    @FXML private CheckBox vibrateCheckbox;
    @FXML private Button testNotificationBtn;
    @FXML private Button resetNotificationBtn;
    @FXML private Button saveNotificationBtn;
    
    // Data Management
    @FXML private Button exportCSVBtn;
    @FXML private Button exportJSONBtn;
    @FXML private Button exportPDFBtn;
    @FXML private CheckBox exportActivitiesCheckbox;
    @FXML private CheckBox exportStatsCheckbox;
    @FXML private CheckBox exportBadgesCheckbox;
    @FXML private CheckBox exportSettingsCheckbox;
    @FXML private Button importDataBtn;
    @FXML private Button importCSVBtn;
    @FXML private Button backupDataBtn;
    @FXML private Button restoreDataBtn;
    @FXML private Button scheduleBackupBtn;
    @FXML private CheckBox autoBackupCheckbox;
    @FXML private ComboBox<String> backupRetentionComboBox;
    @FXML private Label totalActivitiesLabel;
    @FXML private Label dataSizeLabel;
    @FXML private Label lastBackupLabel;
    @FXML private Button clearActivitiesBtn;
    @FXML private Button clearStatsBtn;
    @FXML private Button clearDataBtn;
    
    // Privacy & Security
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button changePasswordBtn;
    @FXML private Button generatePasswordBtn;
    @FXML private CheckBox twoFactorCheckbox;
    @FXML private CheckBox loginAlertsCheckbox;
    @FXML private CheckBox dataCollectionCheckbox;
    @FXML private CheckBox personalizedAdsCheckbox;
    @FXML private CheckBox shareStatsCheckbox;
    @FXML private CheckBox locationTrackingCheckbox;
    @FXML private CheckBox crashReportsCheckbox;
    @FXML private Button downloadDataBtn;
    @FXML private Button viewDataBtn;
    @FXML private Button privacyPolicyBtn;
    @FXML private Button logoutAllDevicesBtn;
    @FXML private Button viewActiveSessionsBtn;
    @FXML private CheckBox rememberLoginCheckbox;
    @FXML private Button deactivateAccountBtn;
    @FXML private Button deleteAccountBtn;
    
    // About Section
    @FXML private Label versionLabel;
    @FXML private Label buildLabel;
    @FXML private Label javaVersionLabel;
    @FXML private Label osLabel;
    @FXML private Label dbVersionLabel;
    @FXML private Label installDateLabel;
    @FXML private Button websiteBtn;
    @FXML private Button feedbackBtn;
    @FXML private Button supportBtn;
    @FXML private Button rateAppBtn;
    @FXML private Button shareAppBtn;
    @FXML private Button donateBtn;
    @FXML private Button checkUpdatesBtn;
    @FXML private Button updateHistoryBtn;
    @FXML private CheckBox autoUpdateCheckbox;
    @FXML private Label lastUpdateCheckLabel;
    @FXML private Button licensesBtn;
    @FXML private Button termsBtn;
    @FXML private Button privacyPolicyAboutBtn;
    @FXML private TextArea creditsTextArea;
    @FXML private ScrollPane scrollPane;
    
    // Notification Container
    @FXML private VBox notificationContainer;
    
    // Services
    private SettingsService settingsService;
    private NotificationService notificationService;
    private UserSettings currentSettings;
    private User currentUser;
    
    // State
    private String currentActiveSection = "profile";
    private UserSettings originalSettings;
    
    /**
     * Initialize team profile section
     */

    @Override
    public void initializeScreen() {
        try {
            // Initialize services
            initializeServices();
            
            // Load current settings
            loadCurrentSettings();
            
            // Setup UI components
            setupComboBoxes();
            setupSpinners();
            setupToggleGroups();
            setupEventHandlers();
            
            // Load user data
            populateUserData();
            loadSystemInformation();
            
            // Load appearance preferences
            loadAppearancePreferences();
            
            // Set default active section
            setActiveSection("profile");
            
            // Update storage information
            updateStorageInformation();
            
            // Initialize team profile section if it exists
            initializeTeamProfileSection();
            debugTeamProfileComponents();

            System.out.println("Settings screen initialized successfully");
            
        } catch (Exception e) {
            System.err.println("Error initializing settings screen: " + e.getMessage());
            e.printStackTrace();
            
            // Initialize with default values if there's an error
            createDefaultSettings();
            setupBasicComponents();
        }
    }
    
    /**
     * Initialize all services
     */
    private void initializeServices() {
        settingsService = new SettingsService();
        notificationService = new NotificationService();
    }
    
    /**
     * Create default settings when initialization fails
     */
    private void createDefaultSettings() {
        currentSettings = new UserSettings();
        currentUser = new User();
        currentUser.setFirstName("Demo");
        currentUser.setLastName("User");
        currentUser.setEmail("demo@ecohabit.com");
        originalSettings = new UserSettings();
    }
    
    /**
     * Setup basic components when there's an initialization error
     */
    private void setupBasicComponents() {
        try {
            if (genderComboBox != null) {
                genderComboBox.setItems(FXCollections.observableArrayList(
                    "Male", "Female"
                ));
            }
            
            if (userNameLabel != null) {
                userNameLabel.setText("Demo User");
            }
            
            if (userEmailLabel != null) {
                userEmailLabel.setText("demo@ecohabit.com");
            }
        } catch (Exception e) {
            System.err.println("Error in setupBasicComponents: " + e.getMessage());
        }
    }
    
    /**
     * Setup all combo boxes with their options 
     */
    private void setupComboBoxes() {
        try {
            // Gender options
            if (genderComboBox != null) {
                genderComboBox.setItems(FXCollections.observableArrayList(
                    "Male", "Female"
                ));
            }
            
            // Diet options
            if (dietComboBox != null) {
                dietComboBox.setItems(FXCollections.observableArrayList(
                    "Omnivore", "Vegetarian", "Vegan", "Pescatarian", "Flexitarian", "Keto", "Paleo"
                ));
            }
            
            // Transportation options
            if (transportComboBox != null) {
                transportComboBox.setItems(FXCollections.observableArrayList(
                    "Car", "Public Transport", "Bicycle", "Walking", "Motorcycle", "Electric Vehicle", "Mixed"
                ));
            }
            
            // User type options
            if (userTypeComboBox != null) {
                userTypeComboBox.setItems(FXCollections.observableArrayList(
                    "Beginner", "Intermediate", "Advanced", "Environmental Enthusiast", "Researcher"
                ));
            }
            
            // Language options
            if (languageComboBox != null) {
                languageComboBox.setItems(FXCollections.observableArrayList(
                    Locale.ENGLISH, new Locale("es"), Locale.FRENCH, Locale.GERMAN, 
                    Locale.ITALIAN, new Locale("pt"), new Locale("zh"), Locale.JAPANESE
                ));
                languageComboBox.setValue(Locale.getDefault());
            }
            
            // Notification sound options
            if (soundComboBox != null) {
                soundComboBox.setItems(FXCollections.observableArrayList(
                    "Default", "Nature Sounds", "Chime", "Bell", "Gentle Ping", "None"
                ));
            }
            
            // Weekly summary day options
            if (weeklySummaryDayComboBox != null) {
                weeklySummaryDayComboBox.setItems(FXCollections.observableArrayList(
                    DayOfWeek.values()
                ));
                weeklySummaryDayComboBox.setValue(DayOfWeek.SUNDAY);
            }
            
            // Backup retention options
            if (backupRetentionComboBox != null) {
                backupRetentionComboBox.setItems(FXCollections.observableArrayList(
                    "1 week", "2 weeks", "1 month", "3 months", "6 months", "1 year", "Forever"
                ));
                backupRetentionComboBox.setValue("1 month");
            }
            
        } catch (Exception e) {
            System.err.println("Error setting up combo boxes: " + e.getMessage());
        }
    }
    
    /**
     * Setup spinner controls
     */
    private void setupSpinners() {
        try {
            // Reminder time spinner
            if (reminderTimeSpinner != null) {
                SpinnerValueFactory<LocalTime> factory = new SpinnerValueFactory<LocalTime>() {
                    {
                        setValue(LocalTime.of(9, 0));
                    }
                    
                    @Override
                    public void decrement(int steps) {
                        LocalTime current = getValue();
                        setValue(current.minusMinutes(15 * steps));
                    }
                    
                    @Override
                    public void increment(int steps) {
                        LocalTime current = getValue();
                        setValue(current.plusMinutes(15 * steps));
                    }
                };
                reminderTimeSpinner.setValueFactory(factory);
            }
            
            // Quiet hours spinners
            if (quietHoursStartSpinner != null) {
                SpinnerValueFactory<LocalTime> factory = new SpinnerValueFactory<LocalTime>() {
                    {
                        setValue(LocalTime.of(22, 0));
                    }
                    
                    @Override
                    public void decrement(int steps) {
                        LocalTime current = getValue();
                        setValue(current.minusMinutes(15 * steps));
                    }
                    
                    @Override
                    public void increment(int steps) {
                        LocalTime current = getValue();
                        setValue(current.plusMinutes(15 * steps));
                    }
                };
                quietHoursStartSpinner.setValueFactory(factory);
            }
            
            if (quietHoursEndSpinner != null) {
                SpinnerValueFactory<LocalTime> factory = new SpinnerValueFactory<LocalTime>() {
                    {
                        setValue(LocalTime.of(7, 0));
                    }
                    
                    @Override
                    public void decrement(int steps) {
                        LocalTime current = getValue();
                        setValue(current.minusMinutes(15 * steps));
                    }
                    
                    @Override
                    public void increment(int steps) {
                        LocalTime current = getValue();
                        setValue(current.plusMinutes(15 * steps));
                    }
                };
                quietHoursEndSpinner.setValueFactory(factory);
            }
            
        } catch (Exception e) {
            System.err.println("Error setting up spinners: " + e.getMessage());
        }
    }
    
    /**
     * Setup toggle groups
     */
    private void setupToggleGroups() {
        try {
            if (lightThemeRadio != null && darkThemeRadio != null && ecoThemeRadio != null && systemThemeRadio != null) {
                themeGroup = new ToggleGroup();
                lightThemeRadio.setToggleGroup(themeGroup);
                darkThemeRadio.setToggleGroup(themeGroup);
                ecoThemeRadio.setToggleGroup(themeGroup);
                systemThemeRadio.setToggleGroup(themeGroup);
            }
        } catch (Exception e) {
            System.err.println("Error setting up toggle groups: " + e.getMessage());
        }
    }
    
    /**
     * Setup all event handlers
     */
    private void setupEventHandlers() {
        try {
            setupNavigationHandlers();
            setupProfileHandlers();
            setupAppearanceHandlers();
            setupNotificationHandlers();
            setupDataManagementHandlers();
            setupPrivacyHandlers();
            setupAboutHandlers();
            setupQuickActionHandlers();
        } catch (Exception e) {
            System.err.println("Error setting up event handlers: " + e.getMessage());
        }
    }
    
    /**
     * Setup navigation handlers
     */
    private void setupNavigationHandlers() {
        if (profileSettingsBtn != null) {
            profileSettingsBtn.setOnAction(_ -> setActiveSection("profile"));
        }
        if (appearanceBtn != null) {
            appearanceBtn.setOnAction(_ -> setActiveSection("appearance"));
        }
        if (notificationsBtn != null) {
            notificationsBtn.setOnAction(_ -> setActiveSection("notifications"));
        }
        if (dataManagementBtn != null) {
            dataManagementBtn.setOnAction(_ -> setActiveSection("data"));
        }
        if (privacyBtn != null) {
            privacyBtn.setOnAction(_ -> setActiveSection("privacy"));
        }
        if (aboutBtn != null) {
            aboutBtn.setOnAction(_ -> setActiveSection("about"));
        }
        if (teamProfileBtn != null) {
            teamProfileBtn.setOnAction(_ -> setActiveSection("team"));
        }

    }
    
    /**
     * Setup profile section handlers
     */
    private void setupProfileHandlers() {
        if (changePhotoBtn != null) {
            changePhotoBtn.setOnAction(_ -> handleChangePhoto());
        }
        if (saveProfileBtn != null) {
            saveProfileBtn.setOnAction(_ -> handleSaveProfile());
        }
        if (resetProfileBtn != null) {
            resetProfileBtn.setOnAction(_ -> handleResetProfile());
        }
        if (saveLifestyleBtn != null) {
            saveLifestyleBtn.setOnAction(_ -> handleSaveLifestyle());
        }
        if (resetLifestyleBtn != null) {
            resetLifestyleBtn.setOnAction(_ -> handleResetLifestyle());
        }
    }
    
    /**
     * Setup appearance section handlers
     */
    private void setupAppearanceHandlers() {
        if (saveAppearanceBtn != null) {
            saveAppearanceBtn.setOnAction(_ -> handleSaveAppearance());
        }
        if (resetAppearanceBtn != null) {
            resetAppearanceBtn.setOnAction(_ -> handleResetAppearance());
        }
        if (previewAppearanceBtn != null) {
            previewAppearanceBtn.setOnAction(_ -> handlePreviewAppearance());
        }
        if (applyLanguageBtn != null) {
            applyLanguageBtn.setOnAction(_ -> handleApplyLanguage());
        }
        
        // Font size slider real-time preview
        if (fontSizeSlider != null) {
            fontSizeSlider.valueProperty().addListener((_, _, newVal) -> 
                updateFontSizePreview(newVal.doubleValue()));
        }
    }
    
    /**
     * Setup notification section handlers
     */
    private void setupNotificationHandlers() {
        if (saveNotificationBtn != null) {
            saveNotificationBtn.setOnAction(_ -> handleSaveNotifications());
        }
        if (resetNotificationBtn != null) {
            resetNotificationBtn.setOnAction(_ -> handleResetNotifications());
        }
        if (testNotificationBtn != null) {
            testNotificationBtn.setOnAction(_ -> handleTestNotification());
        }
        if (playSoundBtn != null) {
            playSoundBtn.setOnAction(_ -> handlePlaySound());
        }
    }
    
    /**
     * Setup data management section handlers
     */
    private void setupDataManagementHandlers() {
        // Export handlers
        if (exportCSVBtn != null) {
            exportCSVBtn.setOnAction(_ -> handleExportCSV());
        }
        if (exportJSONBtn != null) {
            exportJSONBtn.setOnAction(_ -> handleExportJSON());
        }
        if (exportPDFBtn != null) {
            exportPDFBtn.setOnAction(_ -> handleExportPDF());
        }
        
        // Import handlers
        if (importDataBtn != null) {
            importDataBtn.setOnAction(_ -> handleImportData());
        }
        if (importCSVBtn != null) {
            importCSVBtn.setOnAction(_ -> handleImportCSV());
        }
        
        // Backup handlers
        if (backupDataBtn != null) {
            backupDataBtn.setOnAction(_ -> handleCreateBackup());
        }
        if (restoreDataBtn != null) {
            restoreDataBtn.setOnAction(_ -> handleRestoreData());
        }
        if (scheduleBackupBtn != null) {
            scheduleBackupBtn.setOnAction(_ -> handleScheduleBackup());
        }
        
        // Clear data handlers
        if (clearActivitiesBtn != null) {
            clearActivitiesBtn.setOnAction(_ -> handleClearActivities());
        }
        if (clearStatsBtn != null) {
            clearStatsBtn.setOnAction(_ -> handleClearStats());
        }
        if (clearDataBtn != null) {
            clearDataBtn.setOnAction(_ -> handleClearAllData());
        }
    }
    
    /**
     * Setup privacy section handlers
     */
    private void setupPrivacyHandlers() {
        if (changePasswordBtn != null) {
            changePasswordBtn.setOnAction(_ -> handleChangePassword());
        }
        if (generatePasswordBtn != null) {
            generatePasswordBtn.setOnAction(_ -> handleGeneratePassword());
        }
        if (downloadDataBtn != null) {
            downloadDataBtn.setOnAction(_ -> handleDownloadData());
        }
        if (viewDataBtn != null) {
            viewDataBtn.setOnAction(_ -> handleViewData());
        }
        if (privacyPolicyBtn != null) {
            privacyPolicyBtn.setOnAction(_ -> handleViewPrivacyPolicy());
        }
        if (logoutAllDevicesBtn != null) {
            logoutAllDevicesBtn.setOnAction(_ -> handleLogoutAllDevices());
        }
        if (viewActiveSessionsBtn != null) {
            viewActiveSessionsBtn.setOnAction(_ -> handleViewActiveSessions());
        }
        if (deactivateAccountBtn != null) {
            deactivateAccountBtn.setOnAction(_ -> handleDeactivateAccount());
        }
        if (deleteAccountBtn != null) {
            deleteAccountBtn.setOnAction(_ -> handleDeleteAccount());
        }
    }
    
    /**
     * Setup about section handlers
     */
    private void setupAboutHandlers() {
        if (websiteBtn != null) {
            websiteBtn.setOnAction(_ -> handleOpenWebsite());
        }
        if (feedbackBtn != null) {
            feedbackBtn.setOnAction(_ -> handleSendFeedback());
        }
        if (supportBtn != null) {
            supportBtn.setOnAction(_ -> handleGetSupport());
        }
        if (rateAppBtn != null) {
            rateAppBtn.setOnAction(_ -> handleRateApp());
        }
        if (shareAppBtn != null) {
            shareAppBtn.setOnAction(_ -> handleShareApp());
        }
        if (donateBtn != null) {
            donateBtn.setOnAction(_ -> handleDonate());
        }
        if (checkUpdatesBtn != null) {
            checkUpdatesBtn.setOnAction(_ -> handleCheckUpdates());
        }
        if (updateHistoryBtn != null) {
            updateHistoryBtn.setOnAction(_ -> handleUpdateHistory());
        }
        if (licensesBtn != null) {
            licensesBtn.setOnAction(_ -> handleViewLicenses());
        }
        if (termsBtn != null) {
            termsBtn.setOnAction(_ -> handleViewTerms());
        }
        if (privacyPolicyAboutBtn != null) {
            privacyPolicyAboutBtn.setOnAction(_ -> handleViewPrivacyPolicy());
        }
    }
    
    /**
     * Setup quick action handlers
     */
    private void setupQuickActionHandlers() {
        if (exportDataBtn != null) {
            exportDataBtn.setOnAction(_ -> handleQuickExport());
        }
        if (backupBtn != null) {
            backupBtn.setOnAction(_ -> handleQuickBackup());
        }
    }
    
    /**
     * Load current user settings
     */
    private void loadCurrentSettings() {
        try {
            currentUser = SessionManager.getCurrentUser();
            
            if (currentUser != null && settingsService != null) {
                currentSettings = settingsService.getUserSettings(currentUser.getId());
                if (currentSettings == null) {
                    currentSettings = new UserSettings();
                }
                originalSettings = new UserSettings();
            } else {
                createDefaultSettings();
            }
        } catch (Exception e) {
            System.err.println("Error loading current settings: " + e.getMessage());
            createDefaultSettings();
        }
    }
    
    /**
     * Populate UI with current user data
     */
    private void populateUserData() {
        try {
            populateProfileData();
            populateAppearanceData();
            populateNotificationData();
            populatePrivacyData();
        } catch (Exception e) {
            System.err.println("Error populating user data: " + e.getMessage());
        }
    }
    
    /**
     * Populate profile section data
     */
    private void populateProfileData() {
        if (currentUser != null) {
            // Profile information
            if (firstNameField != null) {
                firstNameField.setText(currentUser.getFirstName());
            }
            if (lastNameField != null) {
                lastNameField.setText(currentUser.getLastName());
            }
            if (emailField != null) {
                emailField.setText(currentUser.getEmail());
            }
            if (ageField != null) {
                ageField.setText(String.valueOf(currentUser.getAge()));
            }
            if (genderComboBox != null) {
                genderComboBox.setValue(currentUser.getGender());
            }
            if (locationField != null) {
                locationField.setText(currentUser.getLocation());
            }
            
            // Update labels
            if (userNameLabel != null) {
                userNameLabel.setText(currentUser.getFullName());
            }
            if (userEmailLabel != null) {
                userEmailLabel.setText(currentUser.getEmail());
            }
        }
        
        // For now, we'll set default values since UserSettings methods are missing
        if (dietComboBox != null) {
            dietComboBox.setValue("Omnivore");
        }
        if (transportComboBox != null) {
            transportComboBox.setValue("Mixed");
        }
        if (userTypeComboBox != null) {
            userTypeComboBox.setValue("Beginner");
        }
        if (dailyGoalField != null) {
            dailyGoalField.setText("5");
        }
    }
    
    /**
     * Populate appearance section data
     */
    private void populateAppearanceData() {
        // Set default values since UserSettings methods are missing
        setThemeSelection("eco");
        
        if (fontSizeSlider != null) {
            fontSizeSlider.setValue(14);
            updateFontSizePreview(14);
        }
        
        if (languageComboBox != null) {
            languageComboBox.setValue(Locale.getDefault());
        }
        
        if (highContrastCheckbox != null) {
            highContrastCheckbox.setSelected(false);
        }
        if (largeButtonsCheckbox != null) {
            largeButtonsCheckbox.setSelected(false);
        }
        if (reduceAnimationsCheckbox != null) {
            reduceAnimationsCheckbox.setSelected(false);
        }
        if (screenReaderSupportCheckbox != null) {
            screenReaderSupportCheckbox.setSelected(false);
        }
    }
    
    /**
     * Populate notification section data
     */
    private void populateNotificationData() {
        // Set default values since UserSettings methods are missing
        if (dailyReminderCheckbox != null) {
            dailyReminderCheckbox.setSelected(true);
        }
        if (weeklySummaryCheckbox != null) {
            weeklySummaryCheckbox.setSelected(true);
        }
        if (achievementCheckbox != null) {
            achievementCheckbox.setSelected(true);
        }
        if (ecoTipsCheckbox != null) {
            ecoTipsCheckbox.setSelected(true);
        }
        if (goalCheckbox != null) {
            goalCheckbox.setSelected(true);
        }
        if (socialCheckbox != null) {
            socialCheckbox.setSelected(false);
        }
        if (updateCheckbox != null) {
            updateCheckbox.setSelected(true);
        }
        
        if (reminderTimeSpinner != null) {
            reminderTimeSpinner.getValueFactory().setValue(LocalTime.of(9, 0));
        }
        if (quietHoursStartSpinner != null) {
            quietHoursStartSpinner.getValueFactory().setValue(LocalTime.of(22, 0));
        }
        if (quietHoursEndSpinner != null) {
            quietHoursEndSpinner.getValueFactory().setValue(LocalTime.of(7, 0));
        }
        if (weeklySummaryDayComboBox != null) {
            weeklySummaryDayComboBox.setValue(DayOfWeek.SUNDAY);
        }
        if (weekendsOnlyCheckbox != null) {
            weekendsOnlyCheckbox.setSelected(false);
        }
        
        if (soundComboBox != null) {
            soundComboBox.setValue("Default");
        }
        if (volumeSlider != null) {
            volumeSlider.setValue(80);
        }
        if (vibrateCheckbox != null) {
            vibrateCheckbox.setSelected(true);
        }
    }
    
    /**
     * Populate privacy section data
     */
    private void populatePrivacyData() {
        // Set default values since UserSettings methods are missing
        if (dataCollectionCheckbox != null) {
            dataCollectionCheckbox.setSelected(true);
        }
        if (personalizedAdsCheckbox != null) {
            personalizedAdsCheckbox.setSelected(false);
        }
        if (shareStatsCheckbox != null) {
            shareStatsCheckbox.setSelected(false);
        }
        if (locationTrackingCheckbox != null) {
            locationTrackingCheckbox.setSelected(false);
        }
        if (crashReportsCheckbox != null) {
            crashReportsCheckbox.setSelected(true);
        }
        
        if (twoFactorCheckbox != null) {
            twoFactorCheckbox.setSelected(false);
        }
        if (loginAlertsCheckbox != null) {
            loginAlertsCheckbox.setSelected(true);
        }
        if (rememberLoginCheckbox != null) {
            rememberLoginCheckbox.setSelected(true);
        }
    }
    
    /**
     * Load system information
     */
    private void loadSystemInformation() {
        try {
            // Version information
            if (versionLabel != null) {
                versionLabel.setText("Version 1.0.0");
            }
            if (buildLabel != null) {
                buildLabel.setText("Build 1001");
            }
            
            // System information
            if (javaVersionLabel != null) {
                javaVersionLabel.setText(System.getProperty("java.version"));
            }
            if (osLabel != null) {
                osLabel.setText(System.getProperty("os.name") + " " + System.getProperty("os.version"));
            }
            if (dbVersionLabel != null) {
                dbVersionLabel.setText("SQLite 3.0");
            }
            if (installDateLabel != null) {
                installDateLabel.setText(java.time.LocalDate.now().toString());
            }
            
            // Update information
            if (lastUpdateCheckLabel != null) {
                lastUpdateCheckLabel.setText("Last checked: " + java.time.LocalDateTime.now().toString());
            }
        } catch (Exception e) {
            System.err.println("Error loading system information: " + e.getMessage());
        }
    }
    
    /**
     * Update storage information
     */
    private void updateStorageInformation() {
        try {
            if (totalActivitiesLabel != null) {
                totalActivitiesLabel.setText("0");
            }
            if (dataSizeLabel != null) {
                dataSizeLabel.setText("0 MB");
            }
            if (lastBackupLabel != null) {
                lastBackupLabel.setText("Never");
            }
        } catch (Exception e) {
            System.err.println("Error updating storage information: " + e.getMessage());
        }
    }
    
    /**
     * Set active section and update UI
     */
   
    
    /**
     * Get current section VBox
     */
    private VBox getCurrentSectionVBox(String section) {
        switch (section.toLowerCase()) {
            case "profile": return profileSection;
            case "appearance": return appearanceSection;
            case "notifications": return notificationsSection;
            case "data": return dataSection;
            case "privacy": return privacySection;
            case "about": return aboutSection;
            case "team": return teamProfileSection;
            default: return null;
        }
    }

 
    private void setThemeSelection(String themeName) {
        if (themeName == null) return;
        
        switch (themeName.toLowerCase()) {
            case "light":
                if (lightThemeRadio != null) lightThemeRadio.setSelected(true);
                break;
            case "dark":
                if (darkThemeRadio != null) darkThemeRadio.setSelected(true);
                break;
            case "eco":
                if (ecoThemeRadio != null) ecoThemeRadio.setSelected(true);
                break;
            case "system":
                if (systemThemeRadio != null) systemThemeRadio.setSelected(true);
                break;
            default:
                if (ecoThemeRadio != null) ecoThemeRadio.setSelected(true);
                break;
        }
    }
    
    /**
     * Update font size preview
     */
    private void updateFontSizePreview(double fontSize) {
        if (fontSizePreviewLabel != null) {
            fontSizePreviewLabel.setStyle("-fx-font-size: " + fontSize + "px;");
        }
    }
    
    // ==============================================
    // PROFILE SECTION HANDLERS
    // ==============================================
    
 // In SettingsController.java - update the handleChangePhoto method
    private void handleChangePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Copy the file to application directory
                String destinationPath = copyImageToAppDirectory(selectedFile);
                
                // Update user profile
                currentUser.setProfilePicture(destinationPath);
                
                // Save to database
                boolean success = settingsService.saveUserProfile(currentUser);
                
                if (success) {
                    showNotification("Profile photo updated successfully", "success");
                    
                    // Notify main controller to update the UI
                    if (getMainController() != null) {
                        getMainController().updateProfilePhoto(destinationPath);
                    }
                } else {
                    showNotification("Failed to update profile photo", "error");
                }
            } catch (Exception e) {
                System.err.println("Error updating profile photo: " + e.getMessage());
                showNotification("Error updating profile photo: " + e.getMessage(), "error");
            }
        }
    }

    private String copyImageToAppDirectory(File sourceFile) throws IOException {
        // Create directory if it doesn't exist
        File profilePicsDir = new File("user_data/profile_pics/");
        if (!profilePicsDir.exists()) {
            profilePicsDir.mkdirs();
        }
        
        // Generate unique filename
        String extension = getFileExtension(sourceFile.getName());
        String newFileName = "user_" + currentUser.getId() + "_" + 
                            System.currentTimeMillis() + "." + extension;
        
        File destinationFile = new File(profilePicsDir, newFileName);
        
        // Copy file
        Files.copy(sourceFile.toPath(), destinationFile.toPath(), 
                  StandardCopyOption.REPLACE_EXISTING);
        
        return destinationFile.getAbsolutePath();
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }



    
    /**
     * Handle save profile
     */
    private void handleSaveProfile() {
        try {
            if (validateProfileInput()) {
                // Update user object
                currentUser.setFirstName(firstNameField.getText());
                currentUser.setLastName(lastNameField.getText());
                currentUser.setEmail(emailField.getText());
                
                if (!ageField.getText().isEmpty()) {
                    currentUser.setAge(Integer.parseInt(ageField.getText()));
                }
                
                currentUser.setGender(genderComboBox.getValue());
                currentUser.setLocation(locationField.getText());
                
                // Save to database - this would be implemented in your SettingsService
                boolean success = settingsService.saveUserProfile(currentUser);
                
                if (success) {
                    // Update UI
                    userNameLabel.setText(currentUser.getFullName());
                    userEmailLabel.setText(currentUser.getEmail());
                    
                    showNotification("Profile updated successfully", "success");
                } else {
                    showNotification("Failed to update profile", "error");
                }
            }
        } catch (Exception e) {
            System.err.println("Error saving profile: " + e.getMessage());
            showNotification("Error saving profile: " + e.getMessage(), "error");
        }
    }
    
    /**
     * Handle reset profile 
     */
    private void handleResetProfile() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Profile"); 
        alert.setHeaderText("Are you sure you want to reset your profile?");
        alert.setContentText("This will revert all changes made to your profile information.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                populateProfileData();
                showNotification("Profile reset to original values", "info");
            }
        });
    }
    
    /**
     * Handle save lifestyle preferences
     */
    private void handleSaveLifestyle() {
        try {
            // In a real implementation, you would save these to UserSettings
            // For now, we'll just show a notification
            
            showNotification("Lifestyle preferences saved successfully", "success");
        } catch (Exception e) {
            System.err.println("Error saving lifestyle preferences: " + e.getMessage());
            showNotification("Error saving lifestyle preferences: " + e.getMessage(), "error");
        }
    }
    
    /**
     * Handle reset lifestyle preferences
     */
    private void handleResetLifestyle() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Lifestyle Preferences");
        alert.setHeaderText("Are you sure you want to reset your lifestyle preferences?");
        alert.setContentText("This will revert all changes made to your lifestyle settings.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                populateProfileData();
                showNotification("Lifestyle preferences reset to original values", "info");
            }
        });
    }
    
    /**
     * Validate profile input
     */
    private boolean validateProfileInput() {
        // Validate email
        if (!emailField.getText().contains("@")) {
            showNotification("Please enter a valid email address", "error");
            return false;
        }
        
        // Validate age
        if (!ageField.getText().isEmpty()) {
            try {
                int age = Integer.parseInt(ageField.getText());
                if (age < 0 || age > 150) {
                    showNotification("Please enter a valid age", "error");
                    return false;
                }
            } catch (NumberFormatException e) {
                showNotification("Please enter a valid number for age", "error");
                return false;
            }
        }
        
        // Validate required fields
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
            showNotification("First name and last name are required", "error");
            return false;
        }
        
        return true;
    }
    
    // ==============================================
    // APPEARANCE SECTION HANDLERS
    // ==============================================
    private void handleSaveAppearance() {
        try {
            // Save appearance settings to user preferences
            ThemeManager.Theme selectedTheme = getSelectedTheme();
            ThemeManager.getInstance().setTheme(selectedTheme);
            
            // Save other appearance preferences
            saveAppearancePreferences();
            
            showNotification("Appearance settings saved successfully", "success");
        } catch (Exception e) {
            System.err.println("Error saving appearance settings: " + e.getMessage());
            showNotification("Error saving appearance settings: " + e.getMessage(), "error");
        }
    }

    /**
     * Handle reset appearance settings
     */
    private void handleResetAppearance() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Appearance Settings");
        alert.setHeaderText("Are you sure you want to reset your appearance settings?");
        alert.setContentText("This will revert all changes made to your appearance preferences.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                resetAppearanceToDefaults();
                showNotification("Appearance settings reset to default values", "info");
            }
        });
    }

    /**
     * Handle preview appearance
     */
    private void handlePreviewAppearance() {
        ThemeManager.Theme theme = getSelectedTheme();
        ThemeManager.getInstance().setTheme(theme);
        showNotification("Previewing " + theme.getDisplayName() + " theme", "info");
    }

    /**
     * Handle apply language
     */
    private void handleApplyLanguage() {
        Locale selectedLocale = languageComboBox.getValue();
        if (selectedLocale != null) {
            // Save language preference
            preferences.put("language", selectedLocale.toLanguageTag());
            showNotification("Language will be applied after restart: " + selectedLocale.getDisplayName(), "info");
        }
    }

    /**
     * Get selected theme from radio buttons
     */
    private ThemeManager.Theme getSelectedTheme() {
        if (lightThemeRadio != null && lightThemeRadio.isSelected()) return ThemeManager.Theme.LIGHT;
        if (darkThemeRadio != null && darkThemeRadio.isSelected()) return ThemeManager.Theme.DARK;
        if (ecoThemeRadio != null && ecoThemeRadio.isSelected()) return ThemeManager.Theme.ECO;
        if (systemThemeRadio != null && systemThemeRadio.isSelected()) return ThemeManager.Theme.SYSTEM;
        return ThemeManager.Theme.ECO; // default to eco theme
    }

    /**
     * Save appearance preferences to persistent storage
     */
    private void saveAppearancePreferences() {
        try {
            Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
            
            // Save theme
            prefs.put("theme", getSelectedTheme().name());
            
            // Save font size
            if (fontSizeSlider != null) {
                prefs.putDouble("fontSize", fontSizeSlider.getValue());
            }
            
            // Save accessibility settings
            if (highContrastCheckbox != null) {
                prefs.putBoolean("highContrast", highContrastCheckbox.isSelected());
            }
            if (largeButtonsCheckbox != null) {
                prefs.putBoolean("largeButtons", largeButtonsCheckbox.isSelected());
            }
            if (reduceAnimationsCheckbox != null) {
                prefs.putBoolean("reduceAnimations", reduceAnimationsCheckbox.isSelected());
            }
            if (screenReaderSupportCheckbox != null) {
                prefs.putBoolean("screenReaderSupport", screenReaderSupportCheckbox.isSelected());
            }
            
            // Save language preference
            if (languageComboBox != null && languageComboBox.getValue() != null) {
                prefs.put("language", languageComboBox.getValue().toLanguageTag());
            }
            
        } catch (Exception e) {
            System.err.println("Error saving appearance preferences: " + e.getMessage());
        }
    }

    /**
     * Reset appearance settings to default values
     */
    private void resetAppearanceToDefaults() {
        // Set default theme
        setThemeSelection("eco");
        ThemeManager.getInstance().setTheme(ThemeManager.Theme.ECO);
        
        // Reset font size
        if (fontSizeSlider != null) {
            fontSizeSlider.setValue(14.0);
            updateFontSizePreview(14.0);
        }
        
        // Reset language
        if (languageComboBox != null) {
            languageComboBox.setValue(Locale.getDefault());
        }
        
        // Reset accessibility options
        if (highContrastCheckbox != null) {
            highContrastCheckbox.setSelected(false);
        }
        if (largeButtonsCheckbox != null) {
            largeButtonsCheckbox.setSelected(false);
        }
        if (reduceAnimationsCheckbox != null) {
            reduceAnimationsCheckbox.setSelected(false);
        }
        if (screenReaderSupportCheckbox != null) {
            screenReaderSupportCheckbox.setSelected(false);
        }
        
        // Clear preferences
        try {
            Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
            prefs.remove("theme");
            prefs.remove("fontSize");
            prefs.remove("highContrast");
            prefs.remove("largeButtons");
            prefs.remove("reduceAnimations");
            prefs.remove("screenReaderSupport");
            prefs.remove("language");
        } catch (Exception e) {
            System.err.println("Error resetting appearance preferences: " + e.getMessage());
        }
    }

    /**
     * Load appearance preferences from persistent storage
     */
    private void loadAppearancePreferences() {
        try {
            Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
            
            // Load theme
            String themeName = prefs.get("theme", ThemeManager.Theme.ECO.name());
            ThemeManager.Theme savedTheme = ThemeManager.Theme.valueOf(themeName);
            setThemeSelection(savedTheme.name().toLowerCase());
            ThemeManager.getInstance().setTheme(savedTheme);
            
            // Load font size
            if (fontSizeSlider != null) {
                double fontSize = prefs.getDouble("fontSize", 14.0);
                fontSizeSlider.setValue(fontSize);
                updateFontSizePreview(fontSize);
            }
            
            // Load accessibility settings
            if (highContrastCheckbox != null) {
                highContrastCheckbox.setSelected(prefs.getBoolean("highContrast", false));
            }
            if (largeButtonsCheckbox != null) {
                largeButtonsCheckbox.setSelected(prefs.getBoolean("largeButtons", false));
            }
            if (reduceAnimationsCheckbox != null) {
                reduceAnimationsCheckbox.setSelected(prefs.getBoolean("reduceAnimations", false));
            }
            if (screenReaderSupportCheckbox != null) {
                screenReaderSupportCheckbox.setSelected(prefs.getBoolean("screenReaderSupport", false));
            }
            
            // Load language preference
            if (languageComboBox != null) {
                String languageTag = prefs.get("language", Locale.getDefault().toLanguageTag());
                try {
                    Locale savedLocale = Locale.forLanguageTag(languageTag);
                    languageComboBox.setValue(savedLocale);
                } catch (Exception e) {
                    languageComboBox.setValue(Locale.getDefault());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error loading appearance preferences: " + e.getMessage());
        }
    }

    /**
     * Set theme radio button selection based on theme name
     */




   
    
    // ==============================================
    // NOTIFICATION SECTION HANDLERS
    // ==============================================
    
    /**
     * Handle save notification settings
     */
    private void handleSaveNotifications() {
        try {
            // In a real implementation, you would save these to UserSettings
            // For now, we'll just show a notification
            
            showNotification("Notification settings saved successfully", "success");
        } catch (Exception e) {
            System.err.println("Error saving notification settings: " + e.getMessage());
            showNotification("Error saving notification settings: " + e.getMessage(), "error");
        }
    }
    
    /**
     * Handle reset notification settings
     */
    private void handleResetNotifications() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Notification Settings");
        alert.setHeaderText("Are you sure you want to reset your notification settings?");
        alert.setContentText("This will revert all changes made to your notification preferences.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                populateNotificationData();
                showNotification("Notification settings reset to original values", "info");
            }
        });
    }
    
    /**
     * Handle test notification
     */
    private void handleTestNotification() {
        try {
            showNotification("Test notification sent", "success");
        } catch (Exception e) {
            System.err.println("Error sending test notification: " + e.getMessage());
            showNotification("Error sending test notification", "error");
        }
    }
    
    /**
     * Handle play sound
     */
    private void handlePlaySound() {
        try {
            showNotification("Playing sound", "info");
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }
    
    // ==============================================
    // DATA MANAGEMENT SECTION HANDLERS
    // ==============================================
    
    /**
     * Handle export to CSV
     */
    private void handleExportCSV() {
        performExport("CSV");
    }
    
    /**
     * Handle export to JSON
     */
    private void handleExportJSON() {
        performExport("JSON");
    }
    
    /**
     * Handle export to PDF
     */
    private void handleExportPDF() {
        performExport("PDF");
    }
    
    /**
     * Handle import data
     */
    private void handleImportData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Data");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Supported Formats", "*.json", "*.csv", "*.xml"),
            new FileChooser.ExtensionFilter("JSON", "*.json"),
            new FileChooser.ExtensionFilter("CSV", "*.csv"),
            new FileChooser.ExtensionFilter("XML", "*.xml")
        );
        
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            performImport("Data", selectedFile);
        }
    }
    
    /**
     * Handle import CSV
     */
    private void handleImportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import CSV");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            performImport("CSV", selectedFile);
        }
    }
    
    /**
     * Handle create backup
     */
    private void handleCreateBackup() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Backup Location");
        
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            showProgressDialog("Creating Backup", "Please wait while we create your backup...");
        }
    }
    
    /**
     * Handle restore data
     */
    private void handleRestoreData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Backup File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Backup Files", "*.backup", "*.zip", "*.bak")
        );
        
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Restore Backup");
            alert.setHeaderText("Are you sure you want to restore from backup?");
            alert.setContentText("This will replace all current data with the backup data. This action cannot be undone.");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    showProgressDialog("Restoring Backup", "Please wait while we restore your data...");
                }
            });
        }
    }
    
    /**
     * Handle schedule backup
     */
    private void handleScheduleBackup() {
        showNotification("Backup scheduling feature coming soon", "info");
    }
    
    /**
     * Handle clear activities
     */
    private void handleClearActivities() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Activities");
        alert.setHeaderText("Are you sure you want to clear all activities?");
        alert.setContentText("This will permanently delete all your activity history. This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showProgressDialog("Clearing Activities", "Please wait while we clear your activities...");
            }
        });
    }
    
    /**
     * Handle clear statistics
     */
    private void handleClearStats() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Statistics");
        alert.setHeaderText("Are you sure you want to clear all statistics?");
        alert.setContentText("This will reset all your statistics data. This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showProgressDialog("Clearing Statistics", "Please wait while we clear your statistics...");
            }
        });
    }
    
    /**
     * Handle clear all data
     */
    private void handleClearAllData() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear All Data");
        alert.setHeaderText("Are you sure you want to clear ALL data?");
        alert.setContentText("This will permanently delete all your data including activities, statistics, and settings. This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Alert confirmAlert = new Alert(Alert.AlertType.WARNING);
                confirmAlert.setTitle("Final Confirmation");
                confirmAlert.setHeaderText("This will delete ALL your data!");
                confirmAlert.setContentText("Type 'DELETE ALL' to confirm this action:");
                
                TextField confirmationField = new TextField();
                confirmationField.setPromptText("DELETE ALL");
                
                confirmAlert.getDialogPane().setContent(confirmationField);
                
                confirmAlert.showAndWait().ifPresent(confirmResponse -> {
                    if (confirmResponse == ButtonType.OK && "DELETE ALL".equals(confirmationField.getText())) {
                        showProgressDialog("Clearing All Data", "Please wait while we clear all your data...");
                    }
                });
            }
        });
    }
    
    // ==============================================
    // PRIVACY SECTION HANDLERS
    // ==============================================
    
    /**
     * Handle change password
     */
    private void handleChangePassword() {
        // Validate password fields
        if (currentPasswordField.getText().isEmpty()) {
            showNotification("Current password is required", "error");
            return;
        }
        
        if (newPasswordField.getText().isEmpty()) {
            showNotification("New password is required", "error");
            return;
        }
        
        if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
            showNotification("New passwords do not match", "error");
            return;
        }
        
        if (newPasswordField.getText().length() < 8) {
            showNotification("New password must be at least 8 characters", "error");
            return;
        }
        
        // Show progress and change password
        showProgressDialog("Changing Password", "Updating your password...");
    }
    
    /**
     * Handle generate password
     */
    private void handleGeneratePassword() {
        // Generate a secure random password
        String generatedPassword = generateSecurePassword(12);
        newPasswordField.setText(generatedPassword);
        confirmPasswordField.setText(generatedPassword);
        
        showNotification("Secure password generated", "info");
    }
    
    /**
     * Handle download data
     */
    private void handleDownloadData() {
        performExport("All Data");
    }
    
    /**
     * Handle view data
     */
    private void handleViewData() {
        showNotification("Data preview feature coming soon", "info");
    }
    
    /**
     * Handle view privacy policy
     */
    private void handleViewPrivacyPolicy() {
        showNotification("Privacy policy feature coming soon", "info");
    }
    
    /**
     * Handle logout all devices
     */
    private void handleLogoutAllDevices() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout All Devices");
        alert.setHeaderText("Are you sure you want to logout from all devices?");
        alert.setContentText("This will sign you out from all devices except this one.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showProgressDialog("Logging Out", "Signing out from all devices...");
            }
        });
    }
    
    /**
     * Handle view active sessions
     */
    private void handleViewActiveSessions() {
        showNotification("Active sessions feature coming soon", "info");
    }
    
    /**
     * Handle deactivate account
     */
    private void handleDeactivateAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deactivate Account");
        alert.setHeaderText("Are you sure you want to deactivate your account?");
        alert.setContentText("Your account will be temporarily disabled. You can reactivate it by logging in again.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showProgressDialog("Deactivating Account", "Processing your request...");
            }
        });
    }
    
    /**
     * Handle delete account
     */
    private void handleDeleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Are you sure you want to permanently delete your account?");
        alert.setContentText("This will permanently delete all your data and cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Alert confirmAlert = new Alert(Alert.AlertType.WARNING);
                confirmAlert.setTitle("Final Confirmation");
                confirmAlert.setHeaderText("This will permanently delete your account!");
                confirmAlert.setContentText("Type 'DELETE ACCOUNT' to confirm:");
                
                TextField confirmationField = new TextField();
                confirmationField.setPromptText("DELETE ACCOUNT");
                
                confirmAlert.getDialogPane().setContent(confirmationField);
                
                confirmAlert.showAndWait().ifPresent(confirmResponse -> {
                    if (confirmResponse == ButtonType.OK && "DELETE ACCOUNT".equals(confirmationField.getText())) {
                        showProgressDialog("Deleting Account", "Permanently deleting your account...");
                    }
                });
            }
        });
    }
    
    // ==============================================
    // ABOUT SECTION HANDLERS
    // ==============================================
    
    /**
     * Handle open website
     */
    private void handleOpenWebsite() {
        showNotification("Website feature coming soon", "info");
    }
    
    /**
     * Handle send feedback
     */
    private void handleSendFeedback() {
        showNotification("Feedback feature coming soon", "info");
    }
    
    /**
     * Handle get support
     */
    private void handleGetSupport() {
        showNotification("Support feature coming soon", "info");
    }
    
    /**
     * Handle rate app
     */
    private void handleRateApp() {
        showNotification("Rating feature coming soon", "info");
    }
    
    /**
     * Handle share app
     */
    private void handleShareApp() {
        showNotification("Share feature coming soon", "info");
    }
    
    /**
     * Handle donate
     */
    private void handleDonate() {
        showNotification("Donation feature coming soon", "info");
    }
    
    /**
     * Handle check updates
     */
    private void handleCheckUpdates() {
        showProgressDialog("Checking for Updates", "Looking for the latest version...");
    }
    
    /**
     * Handle update history
     */
    private void handleUpdateHistory() {
        showNotification("Update history feature coming soon", "info");
    }
    
    /**
     * Handle view licenses
     */
    private void handleViewLicenses() {
        showNotification("Licenses feature coming soon", "info");
    }
    
    /**
     * Handle view terms
     */
    private void handleViewTerms() {
        showNotification("Terms of service feature coming soon", "info");
    }
    
    // ==============================================
    // QUICK ACTION HANDLERS
    // ==============================================
    
    /**
     * Handle quick export
     */
    private void handleQuickExport() {
        performExport("Quick Export");
    }
    
    /**
     * Handle quick backup
     */
    private void handleQuickBackup() {
        showProgressDialog("Quick Backup", "Creating a quick backup...");
    }
    
    // ==============================================
    // UTILITY METHODS
    // ==============================================
    
    /**
     * Show notification
     */
    private void showNotification(String message, String type) {
        System.out.println(type.toUpperCase() + ": " + message);
        // In a real application, you would show this in the UI
    }
    
    /**
     * Show progress dialog for long-running operations
     */
    private void showProgressDialog(String title, String message) {
        System.out.println(title + ": " + message);
        // In a real application, you would show a progress dialog
    }
    
    /**
     * Perform export operation
     */
    private void performExport(String format) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Export Location for " + format);
        
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            showProgressDialog("Exporting " + format, "Please wait while we export your data...");
        }
    }
    
    /**
     * Perform import operation
     */
    private void performImport(String format, File file) {
        showProgressDialog("Importing " + format, "Please wait while we import your data...");
    }
    
    /**
     * Generate secure random password
     */
    
    private void setActiveSection(String section) {
        try {
            currentActiveSection = section;
            
            // Hide all sections AND set managed=false to remove from layout
            VBox[] sections = {profileSection, appearanceSection, notificationsSection, 
                    dataSection, privacySection, aboutSection, teamProfileSection};
            for (VBox sectionVBox : sections) {
                if (sectionVBox != null) {
                    sectionVBox.setVisible(false);
                    sectionVBox.setManaged(false); // This removes it from layout calculations
                }
            }
            
            // Remove active class from all buttons
            Button[] sectionButtons = {profileSettingsBtn, appearanceBtn, notificationsBtn, 
                                     dataManagementBtn, privacyBtn, aboutBtn, teamProfileBtn};
            for (Button btn : sectionButtons) {
                if (btn != null) {
                    btn.getStyleClass().remove("active");
                }
            }
            
            // Show selected section and set active button
            switch (section.toLowerCase()) {
                case "profile":
                    if (profileSection != null) {
                        profileSection.setVisible(true);
                        profileSection.setManaged(true);
                    }
                    if (profileSettingsBtn != null) profileSettingsBtn.getStyleClass().add("active");
                    break;
                case "appearance":
                    if (appearanceSection != null) {
                        appearanceSection.setVisible(true);
                        appearanceSection.setManaged(true);
                    }
                    if (appearanceBtn != null) appearanceBtn.getStyleClass().add("active");
                    break;
                case "notifications":
                    if (notificationsSection != null) {
                        notificationsSection.setVisible(true);
                        notificationsSection.setManaged(true);
                    }
                    if (notificationsBtn != null) notificationsBtn.getStyleClass().add("active");
                    break;
                case "data":
                    if (dataSection != null) {
                        dataSection.setVisible(true);
                        dataSection.setManaged(true);
                    }
                    if (dataManagementBtn != null) dataManagementBtn.getStyleClass().add("active");
                    updateStorageInformation(); // Refresh data when viewing this section
                    break;
                case "privacy":
                    if (privacySection != null) {
                        privacySection.setVisible(true);
                        privacySection.setManaged(true);
                    }
                    if (privacyBtn != null) privacyBtn.getStyleClass().add("active");
                    break;
                case "about":
                    if (aboutSection != null) {
                        aboutSection.setVisible(true);
                        aboutSection.setManaged(true);
                    }
                    if (aboutBtn != null) aboutBtn.getStyleClass().add("active");
                    break;
                case "team":
                    if (teamProfileSection != null) {
                        teamProfileSection.setVisible(true);
                        teamProfileSection.setManaged(true);
                    }
                    if (teamProfileBtn != null) teamProfileBtn.getStyleClass().add("active");
                    break;
            }
            
            // CRITICAL FIX: Scroll to top after switching sections
            scrollToTop();
            
            // Animate section transition
            animateSectionTransition(section);
            
        } catch (Exception e) {
            System.err.println("Error setting active section: " + e.getMessage());
        }
    }

    // Add this new method to handle scrolling to top:
    private void scrollToTop() {
        if (scrollPane != null) {
            // Use Platform.runLater to ensure UI updates are complete
            Platform.runLater(() -> {
                scrollPane.setVvalue(0.0); // 0.0 = top, 1.0 = bottom
            });
        }
    }
   

    /**
     * Create a fallback team profile if FXML loading fails
     */
    private void createFallbackTeamProfile() {
        try {
            if (teamProfileSection != null) {
                System.out.println("Creating fallback team profile...");
                teamProfileSection.getChildren().clear();
                
                // Create a simple team profile without FXML
                Label titleLabel = new Label("Team GreenSpark");
                titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #64FFDA;");
                
                Label subtitleLabel = new Label("Islamic University of Technology Bangladesh - Batch 22");
                subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #8892b0;");
                
                // Create member information
                VBox membersInfo = new VBox(15);
                membersInfo.getChildren().addAll(
                    createMemberInfoBox("Sunjoh Abdurazack", "ID: 220041258", "+8801540375774", 
                                      "facebook.com/sunjoabdurazack", "github.com/sunjoabdurazack"),
                    createMemberInfoBox("Usman Jabir", "ID: 220041262", "+8801310547691",
                                      "facebook.com/usmanjabir", "github.com/usmanjabir"),
                    createMemberInfoBox("Amadu Gbanyawai", "ID: 220041266", "+8801944223426",
                                      "facebook.com/AmaduGbanyawaijr", "github.com/amadugbanyawai")
                );
                
                // Add everything to the team profile section
                teamProfileSection.getChildren().addAll(titleLabel, subtitleLabel, membersInfo);
                teamProfileSection.setSpacing(20);
                teamProfileSection.setPadding(new Insets(30));
                teamProfileSection.setStyle("-fx-background-color: linear-gradient(to bottom, #0a192f 0%, #112240 100%); -fx-background-radius: 10px;");
                
                System.out.println("Fallback team profile created successfully");
            }
        } catch (Exception e) {
            System.err.println("Error creating fallback team profile: " + e.getMessage());
            // If everything fails, hide the team profile button
            if (teamProfileBtn != null) {
                teamProfileBtn.setVisible(false);
                teamProfileBtn.setManaged(false);
            }
        }
    }

    /**
     * Create a member info box for fallback display
     */
    private VBox createMemberInfoBox(String name, String id, String phone, String facebook, String github) {
        VBox memberBox = new VBox(5);
        memberBox.setStyle("-fx-background-color: rgba(100, 255, 218, 0.1); -fx-background-radius: 10px; -fx-padding: 15px;");
        
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ccd6f6;");
        
        Label idLabel = new Label(id);
        idLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64FFDA;");
        
        Label phoneLabel = new Label("📞 " + phone);
        phoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892b0;");
        
        Label facebookLabel = new Label("📘 " + facebook);
        facebookLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892b0;");
        
        Label githubLabel = new Label("🐙 " + github);
        githubLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892b0;");
        
        memberBox.getChildren().addAll(nameLabel, idLabel, phoneLabel, facebookLabel, githubLabel);
        
        return memberBox;
    }

    /**
     * Also add this method to ensure proper debugging
     */
    private void debugTeamProfileComponents() {
        System.out.println("=== Team Profile Debug Info ===");
        System.out.println("teamProfileSection: " + (teamProfileSection != null ? "Found" : "NULL"));
        System.out.println("teamProfileBtn: " + (teamProfileBtn != null ? "Found" : "NULL"));
        
        if (teamProfileSection != null) {
            System.out.println("teamProfileSection children count: " + teamProfileSection.getChildren().size());
            System.out.println("teamProfileSection visible: " + teamProfileSection.isVisible());
            System.out.println("teamProfileSection managed: " + teamProfileSection.isManaged());
        }
        
        try {
            URL fxmlUrl = getClass().getResource("/main/resources/fxml/team-profile.fxml");
            System.out.println("team-profile.fxml URL: " + (fxmlUrl != null ? fxmlUrl.toString() : "NOT FOUND"));
        } catch (Exception e) {
            System.out.println("Error checking FXML URL: " + e.getMessage());
        }
        System.out.println("=== End Debug Info ===");
    }
    // Modify your existing animateSectionTransition method to work better:
    private void animateSectionTransition(String section) {
        VBox activeSection = getCurrentSectionVBox(section);
        if (activeSection != null) {
            // Start with the section visible but transparent
            activeSection.setOpacity(0);
            
            // Animate fade in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), activeSection);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            
            // Ensure scroll position is set after animation starts
            fadeIn.setOnFinished(_ -> scrollToTop());
            
            fadeIn.play();
        }
    }
    private String generateSecurePassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        
        return password.toString();
    }
 // Fixed initializeTeamProfileSection method for SettingsController.java
    private void initializeTeamProfileSection() {
        try {
            if (teamProfileSection != null) {
                System.out.println("Initializing team profile section...");
                
                // Clear any existing content first
                teamProfileSection.getChildren().clear();
                
                // Create team profile content directly (without separate FXML)
                createTeamProfileContent();
                
                // Initially hide the team profile section
                teamProfileSection.setVisible(false);
                teamProfileSection.setManaged(false);
                
                System.out.println("Team profile section loaded successfully");
            } else {
                System.out.println("Warning: teamProfileSection is null");
            }
        } catch (Exception e) {
            System.err.println("Error initializing team profile section: " + e.getMessage());
            e.printStackTrace();
            
            // If team profile fails to load, create a fallback
            createFallbackTeamProfile();
        }
    }

    /**
     * Create team profile content directly without separate FXML
     */
    private void createTeamProfileContent() {
        try {
            System.out.println("Creating team profile content...");
            
            // Create title
            Label titleLabel = new Label("Team GreenSpark");
            titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #64FFDA; -fx-padding: 0 0 10 0;");
            
            // Create subtitle
            Label subtitleLabel = new Label("Islamic University of Technology Bangladesh - Batch 22");
            subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #8892b0; -fx-padding: 0 0 30 0;");
            
            // Create members container
            HBox membersContainer = new HBox(30);
            membersContainer.setAlignment(javafx.geometry.Pos.TOP_CENTER);
            
            // Create member cards
            VBox member1Card = createMemberCard(
                "Sunjoh Abdurazack", "ID: 220041258", "+8801540375774",
                "facebook.com/sunjoabdurazack", "github.com/sunjoabdurazack",
                "/main/resources/images/member1.png"
            );
            
            VBox member2Card = createMemberCard(
                "Usman Jabir", "ID: 220041262", "+8801310547691",
                "facebook.com/usmanjabir", "github.com/usmanjabir",
                "/main/resources/images/member2.png"
            );
            
            VBox member3Card = createMemberCard(
                "Amadu Gbanyawai", "ID: 220041266", "+8801944223426",
                "facebook.com/AmaduGbanyawaijr", "github.com/amadugbanyawai",
                "/main/resources/images/member3.png"
            );
            
            // Add member cards to container
            membersContainer.getChildren().addAll(member1Card, member2Card, member3Card);
            
            // Add everything to the team profile section
            teamProfileSection.getChildren().addAll(titleLabel, subtitleLabel, membersContainer);
            teamProfileSection.setSpacing(20);
            teamProfileSection.setPadding(new javafx.geometry.Insets(30));
            teamProfileSection.setStyle("-fx-background-color: linear-gradient(to bottom, #0a192f 0%, #112240 100%); -fx-background-radius: 10px;");
            
            System.out.println("Team profile content created successfully");
        } catch (Exception e) {
            System.err.println("Error creating team profile content: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create individual member card
     */
    private VBox createMemberCard(String name, String id, String phone, String facebook, String github, String imagePath) {
        VBox memberCard = new VBox(10);
        memberCard.setAlignment(javafx.geometry.Pos.CENTER);
        memberCard.setStyle(
            "-fx-background-color: rgba(100, 255, 218, 0.1); " +
            "-fx-background-radius: 15px; " +
            "-fx-padding: 20px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.5, 0, 2); " +
            "-fx-min-width: 250px; " +
            "-fx-max-width: 300px;"
        );
        
        // Create profile image
        javafx.scene.image.ImageView profileImage = new javafx.scene.image.ImageView();
        profileImage.setFitWidth(150);
        profileImage.setFitHeight(150);
        profileImage.setPreserveRatio(true);
        
        // Try to load the image
        try {
            java.io.InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                javafx.scene.image.Image image = new javafx.scene.image.Image(imageStream);
                if (!image.isError()) {
                    profileImage.setImage(image);
                } else {
                    setPlaceholderImageStyle(profileImage);
                }
            } else {
                setPlaceholderImageStyle(profileImage);
            }
        } catch (Exception e) {
            setPlaceholderImageStyle(profileImage);
        }
        
        // Create circular clip
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(75, 75, 75);
        profileImage.setClip(clip);
        
        // Create labels
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ccd6f6; -fx-text-alignment: center;");
        nameLabel.setWrapText(true);
        
        Label idLabel = new Label(id);
        idLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64FFDA; -fx-text-alignment: center;");
        
        // Create contact info container
        VBox contactInfo = new VBox(8);
        contactInfo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        contactInfo.setStyle("-fx-padding: 15 0 0 0;");
        
        // Phone
        Label phoneHeader = new Label("Phone:");
        phoneHeader.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892b0; -fx-font-weight: bold;");
        Label phoneLabel = new Label(phone);
        phoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ccd6f6;");
        phoneLabel.setWrapText(true);
        
        // Facebook
        Label facebookHeader = new Label("Facebook:");
        facebookHeader.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892b0; -fx-font-weight: bold; -fx-padding: 8 0 0 0;");
        Label facebookLabel = new Label(facebook);
        facebookLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ccd6f6;");
        facebookLabel.setWrapText(true);
        
        // GitHub
        Label githubHeader = new Label("GitHub:");
        githubHeader.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892b0; -fx-font-weight: bold; -fx-padding: 8 0 0 0;");
        Label githubLabel = new Label(github);
        githubLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ccd6f6;");
        githubLabel.setWrapText(true);
        
        // Add all contact info
        contactInfo.getChildren().addAll(
            phoneHeader, phoneLabel,
            facebookHeader, facebookLabel,
            githubHeader, githubLabel
        );
        
        // Add all elements to member card
        memberCard.getChildren().addAll(profileImage, nameLabel, idLabel, contactInfo);
        
        return memberCard;
    }

    /**
     * Set placeholder style for missing images
     */
    private void setPlaceholderImageStyle(javafx.scene.image.ImageView imageView) {
        imageView.setStyle(
            "-fx-background-color: #64FFDA; " +
            "-fx-background-radius: 75px; " +
            "-fx-border-color: #64FFDA; " +
            "-fx-border-radius: 75px; " +
            "-fx-border-width: 3px;"
        );
    }
 
}