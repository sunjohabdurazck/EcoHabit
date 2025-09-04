package main.java.com.ecohabit.util;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.prefs.Preferences;

/**
 * Manages application themes including light, dark, eco, and system themes
 */
public class ThemeManager {
    private static ThemeManager instance;
    private final Preferences preferences;
    private Theme currentTheme;
    
    public enum Theme {
        LIGHT("Light", "light-theme.css"),
        DARK("Dark", "dark-theme.css"),
        ECO("Eco", "styles.css"),
        SYSTEM("System", "");
        
        private final String displayName;
        private final String cssFile;
        
        Theme(String displayName, String cssFile) {
            this.displayName = displayName;
            this.cssFile = cssFile;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getCssFile() {
            return cssFile;
        }
        
        public static Theme fromString(String text) {
            for (Theme theme : Theme.values()) {
                if (theme.displayName.equalsIgnoreCase(text)) {
                    return theme;
                }
            }
            return ECO; // Default theme
        }
    }
    
    private ThemeManager() {
        preferences = Preferences.userNodeForPackage(ThemeManager.class);
        loadTheme();
    }
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public Theme getCurrentTheme() {
        return currentTheme;
    }
    
    public void setTheme(Theme theme) {
        this.currentTheme = theme;
        preferences.put("theme", theme.name());
        applyThemeToAllWindows();
    }
    
    private void loadTheme() {
        try {
            String themeName = preferences.get("theme", Theme.ECO.name());
            currentTheme = Theme.valueOf(themeName);
        } catch (Exception e) {
            currentTheme = Theme.ECO;
        }
    }
    
    public void applyTheme(Scene scene) {
        if (scene == null) return;
        
        // Clear existing theme stylesheets
        scene.getStylesheets().removeIf(url -> 
            url.contains("light-theme.css") || 
            url.contains("dark-theme.css") || 
            url.contains("styles.css")
        );
        
        // Apply appropriate theme
        if (currentTheme == Theme.SYSTEM) {
            applySystemTheme(scene);
        } else if (currentTheme != null && currentTheme.getCssFile() != null && !currentTheme.getCssFile().isEmpty()) {
            String cssPath = "/main/resources/css/" + currentTheme.getCssFile();
            try {
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            } catch (Exception e) {
                System.err.println("Failed to load theme: " + cssPath);
                // Fallback to eco theme
                scene.getStylesheets().add(getClass().getResource("/main/resources/css/styles.css").toExternalForm());
            }
        }
        
        // Apply dark mode pseudo-class for system theme detection
        boolean isDarkMode = isSystemDarkMode();
        scene.getRoot().pseudoClassStateChanged(PseudoClass.getPseudoClass("dark"), isDarkMode);
    }
    
    private void applySystemTheme(Scene scene) {
        boolean isDarkMode = isSystemDarkMode();
        String themeCss = isDarkMode ? "/main/resources/css/dark-theme.css" : "/main/resources/css/light-theme.css";
        
        try {
            scene.getStylesheets().add(getClass().getResource(themeCss).toExternalForm());
            scene.getRoot().pseudoClassStateChanged(PseudoClass.getPseudoClass("dark"), isDarkMode);
        } catch (Exception e) {
            System.err.println("Failed to load system theme: " + themeCss);
            // Fallback to eco theme
            scene.getStylesheets().add(getClass().getResource("/main/resources/css/styles.css").toExternalForm());
        }
    }
    
    private boolean isSystemDarkMode() {
        // This is a simplified approach - in a real application, you might use
        // System.getProperty("os.name") to determine the OS and use appropriate methods
        // For cross-platform compatibility, we'll use a simple approach
        
        // For macOS
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            try {
                Process process = Runtime.getRuntime().exec("defaults read -g AppleInterfaceStyle");
                process.waitFor();
                return process.exitValue() == 0;
            } catch (Exception e) {
                // If command fails, assume light mode
                return false;
            }
        }
        
        // For Windows
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            try {
                Process process = Runtime.getRuntime().exec("reg query HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize /v AppsUseLightTheme");
                process.waitFor();
                // If the value is 0, dark mode is enabled
                return process.exitValue() == 0;
            } catch (Exception e) {
                // If command fails, assume light mode
                return false;
            }
        } 
        
        // For Linux (this is a very basic check)
        if (System.getProperty("os.name").toLowerCase().contains("nix") || 
            System.getProperty("os.name").toLowerCase().contains("nux")) {
            // Try to detect dark theme from GTK settings
            try {
                Process process = Runtime.getRuntime().exec("gsettings get org.gnome.desktop.interface gtk-theme");
                process.waitFor();
                // If theme name contains "dark", assume dark mode
                java.io.InputStream inputStream = process.getInputStream();
                java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
                String result = scanner.hasNext() ? scanner.next() : "";
                return result.toLowerCase().contains("dark");
            } catch (Exception e) {
                // If command fails, assume light mode
                return false;
            }
        }
        
        // Default to light mode
        return false;
    }
    
    private void applyThemeToAllWindows() {
        // Apply theme to all open windows
        for (Window stage : Stage.getWindows()) {
            if (stage.getScene() != null) {
                applyTheme(stage.getScene());
            }
        }
        
        // Apply to future dialogs/alerts by modifying the default behavior
        // This is a bit hacky but works for most cases
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            applyTheme(alert.getDialogPane().getScene());
            alert.close();
        });
    }
    
    public void initializeStage(Stage stage) {
        // Listen for stage show event to apply theme
        stage.setOnShown(event -> applyTheme(stage.getScene()));
    }
}