package main.java.com.ecohabit.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import main.java.com.ecohabit.model.User;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class GoogleAuthService {
    
    private static final String APPLICATION_NAME = "EcoHabit";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/userinfo.profile");
    
    private static GoogleAuthService instance;
    private GoogleAuthorizationCodeFlow flow;
    
    private GoogleAuthService() {
        try {
            initialize();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Google Auth Service", e);
        }
    }
    
    public static GoogleAuthService getInstance() {
        if (instance == null) {
            instance = new GoogleAuthService();
        }
        return instance;
    }
    
    private void initialize() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        
        // Load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
            new InputStreamReader(new FileInputStream("credentials.json")));
        
        flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(Paths.get(TOKENS_DIRECTORY_PATH).toFile()))
            .setAccessType("offline")
            .build();
    }
    
    public String getAuthorizationUrl() throws Exception {
        return flow.newAuthorizationUrl().setRedirectUri("http://localhost:8080/callback").build();
    }
    
    public GoogleTokenResponse exchangeCodeForTokens(String code) throws Exception {
        return flow.newTokenRequest(code).setRedirectUri("http://localhost:8080/callback").execute();
    }
    
    public User getUserInfo(GoogleTokenResponse credential) {
        // TODO: Implement fetching user info from Google API
        // This would typically call https://www.googleapis.com/oauth2/v1/userinfo
        User user = new User();
        user.setEmail("google.user@example.com"); // Replace with actual email from API
        user.setUsername("Google User"); // Replace with actual name from API
        user.setAuthProvider("google");
        return user;
    }
    
    public boolean hasStoredCredentials() {
        try {
            Credential credential = flow.loadCredential("user");
            return credential != null && credential.getAccessToken() != null;
        } catch (Exception e) {
            return false;
        }
    }
}