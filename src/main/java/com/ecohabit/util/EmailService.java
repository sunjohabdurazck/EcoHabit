package main.java.com.ecohabit.util;

public class EmailService {
    
    public boolean sendEmail(String to, String subject, String body) {
        // In a real implementation, this would integrate with an email service
        // like SendGrid, Amazon SES, or a local SMTP server
        
        System.out.println("Sending email to: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        
        // Simulate email sending
        try {
            Thread.sleep(1000); // Simulate network delay
            return true; // Assume success for demo purposes
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}