package main.java.com.ecohabit.util;

import com.sun.net.httpserver.HttpServer;
import javafx.application.Platform;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class OAuthCallbackServer {
    
    private HttpServer server;
    private CompletableFuture<String> codeFuture;
    
    public OAuthCallbackServer() {
        this.codeFuture = new CompletableFuture<>();
    }
    
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/callback", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String code = extractCodeFromQuery(query);
            
            String response = "<html><body><h1>Authentication successful! You can close this window.</h1></body></html>";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
            
            codeFuture.complete(code);
            stop();
        });
        
        server.start();
    }
    
    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
    
    private String extractCodeFromQuery(String query) {
        if (query != null && query.contains("code=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("code=")) {
                    return param.substring(5);
                }
            }
        }
        return null;
    }
    
    public CompletableFuture<String> getCodeFuture() {
        return codeFuture;
    }
}