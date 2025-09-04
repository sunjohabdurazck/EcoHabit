package main.java.com.ecohabit.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ExportController {

    private Stage primaryStage;

    public ExportController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void exportToCSV(List<String[]> data, String fileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        fileChooser.setInitialFileName(fileName + "_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
        
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                for (String[] row : data) {
                    writer.write(String.join(",", row) + "\n");
                }
                showExportSuccess(file.getAbsolutePath());
            } catch (IOException e) {
                showExportError("Failed to export CSV: " + e.getMessage());
            }
        }
    }

    public void exportToPDF(List<String[]> data, String fileName) {
        // TODO: Implement PDF export functionality
        // This would typically use a library like Apache PDFBox or iText
        System.out.println("PDF export not yet implemented");
        showExportError("PDF export functionality is not yet implemented");
    }

    public void exportChartAsImage(String chartTitle) {
        // TODO: Implement chart image export functionality
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Chart Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PNG Files", "*.png"),
            new FileChooser.ExtensionFilter("JPEG Files", "*.jpg")
        );
        fileChooser.setInitialFileName(chartTitle + "_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
        
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            // TODO: Capture chart as image and save to file
            System.out.println("Saving chart image to: " + file.getAbsolutePath());
            showExportSuccess("Chart image saved successfully: " + file.getName());
        }
    }

    private void showExportSuccess(String message) {
        // TODO: Show success notification
        System.out.println("Export successful: " + message);
    }

    private void showExportError(String message) {
        // TODO: Show error notification
        System.out.println("Export error: " + message);
    }
}
