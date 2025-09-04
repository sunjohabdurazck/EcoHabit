package main.java.com.ecohabit.util;

import java.util.regex.Pattern;

public class EmailValidator {
    private static final String EMAIL_REGEX = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);
    
    public static boolean isValid(String email) {
        return pattern.matcher(email).matches();
    }
}