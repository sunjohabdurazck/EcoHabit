// FileManager.java
package main.java.com.ecohabit.util;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Utility class for file operations and management
 */
public class FileManager {
    
    /**
     * Delete directory and all its contents recursively
     */
    public void deleteDirectoryRecursively(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    /**
     * Copy directory and all its contents recursively
     */
    public void copyDirectoryRecursively(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = target.resolve(source.relativize(dir));
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetFile = target.resolve(source.relativize(file));
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    /**
     * Calculate MD5 hash of a file
     */
    public String calculateFileHash(Path file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        
        try (InputStream fis = Files.newInputStream(file);
             DigestInputStream dis = new DigestInputStream(fis, md5)) {
            
            byte[] buffer = new byte[1024];
            while (dis.read(buffer) != -1) {
                // Reading file to update digest
            }
        }
        
        byte[] digest = md5.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        
        return sb.toString();
    }
    
    /**
     * Get file size in a human-readable format
     */
    public String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * Ensure directory exists, create if it doesn't
     */
    public void ensureDirectoryExists(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
    }
    
    /**
     * Get temporary file with specific extension
     */
    public Path getTempFile(String prefix, String suffix) throws IOException {
        return Files.createTempFile(prefix, suffix);
    }
    
    /**
     * Clean up temporary files older than specified days
     */
    public void cleanupTempFiles(Path tempDir, int olderThanDays) throws IOException {
        if (!Files.exists(tempDir)) return;
        
        long cutoffTime = System.currentTimeMillis() - (olderThanDays * 24L * 60L * 60L * 1000L);
        
        Files.walk(tempDir)
             .filter(Files::isRegularFile)
             .filter(path -> {
                 try {
                     return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                 } catch (IOException e) {
                     return false;
                 }
             })
             .forEach(path -> {
                 try {
                     Files.deleteIfExists(path);
                 } catch (IOException e) {
                     System.err.println("Failed to delete temp file: " + path);
                 }
             });
    }
}

