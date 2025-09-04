package main.java.com.ecohabit.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {
    private static SceneManager instance;
    private static Stage primaryStage;
    
    public SceneManager() {}
    
    public static SceneManager getInstance() {
        if (instance == null) { 
            instance = new SceneManager();
        }
        return instance;
    }
    
    public static void setPrimaryStage(Stage stage) {
        try {
			primaryStage = stage;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void switchToLogin() {
        loadScene("/main/resources/fxml/login.fxml", "EcoHabit - Login");
    }
     
    public void switchToSignup() { 
        loadScene("/main/resources//fxml/signup.fxml", "EcoHabit - Sign Up");
    }
    
    public void switchToDashboard() { 
        loadScene("/main/resources/fxml/dashboard.fxml", "EcoHabit - Dashboard");
    }
    
    public void switchToMain() {
        loadScene("/main/resources/fxml/main.fxml", "EcoHabit - Main");
    }
    
    public void switchToSettings() {
        loadScene("/main/resources/fxml/settings.fxml", "EcoHabit - Settings");
    }
    
    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/main/resources/css/styles.css").toExternalForm());
            
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load scene: " + fxmlPath);
        }
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}