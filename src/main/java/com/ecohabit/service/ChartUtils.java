
// ChartUtils.java
package main.java.com.ecohabit.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import java.util.Random;

public class ChartUtils {
    private final Random random = new Random();
    
    public XYChart.Series<String, Number> generateWeeklyTrendData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("CO₂ Saved");
        
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : days) {
            double value = 5 + random.nextDouble() * 10; // Random values between 5-15
            series.getData().add(new XYChart.Data<>(day, value));
        }
        
        return series;
    }
    
    public ObservableList<PieChart.Data> generateActivityBreakdownData() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        
        pieData.add(new PieChart.Data("Transportation", 35));
        pieData.add(new PieChart.Data("Energy", 25));
        pieData.add(new PieChart.Data("Food", 20));
        pieData.add(new PieChart.Data("Recycling", 15));
        pieData.add(new PieChart.Data("Other", 5));
        
        return pieData;
    }
}

