// ActivityDAO.java
package main.java.com.ecohabit.dao;

import main.java.com.ecohabit.model.Activity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityDAO {
    private List<Activity> activities;
    
    public ActivityDAO() {
        this.activities = new ArrayList<>();
        // Add some sample data
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        activities.add(new Activity("Biked to work", LocalDate.now(), 2.5));
        activities.add(new Activity("Used reusable water bottle", LocalDate.now(), 0.3));
        activities.add(new Activity("Recycled plastic bottles", LocalDate.now().minusDays(1), 0.5));
        activities.add(new Activity("Plant-based lunch", LocalDate.now().minusDays(1), 1.8));
    }
    
    public void addActivity(Activity activity) {
        activities.add(activity);
    }
    
    public List<Activity> getActivitiesForDate(LocalDate date) {
        return activities.stream()
            .filter(activity -> activity.getDate().equals(date))
            .collect(Collectors.toList());
    }
    
    public List<Activity> getActivitiesForWeek(LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        return activities.stream()
            .filter(activity -> 
                !activity.getDate().isBefore(weekStart) && 
                !activity.getDate().isAfter(weekEnd))
            .collect(Collectors.toList());
    }
    
    public List<Activity> getAllActivities() {
        return new ArrayList<>(activities);
    }
}
