package main.java.com.ecohabit.model;

/**
 * Model class representing an activity category with name and color properties
 */
public class ActivityCategory {
    private String name;
    private String color;
    
    /**
     * Constructor for ActivityCategory
     * @param name The name of the category
     * @param color The hex color code for the category
     */
    public ActivityCategory(String name, String color) {
        this.name = name;
        this.color = color;
    }
    
    /**
     * Gets the category name
     * @return The name of the category
     */
    public String getName() { 
        return name; 
    }
    
    /**
     * Gets the category color
     * @return The hex color code for the category
     */
    public String getColor() { 
        return color; 
    }
    
    /**
     * Sets the category name
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets the category color
     * @param color The hex color code to set
     */
    public void setColor(String color) {
        this.color = color;
    }
    
    @Override
    public String toString() {
        return "ActivityCategory{name='" + name + "', color='" + color + "'}";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ActivityCategory that = (ActivityCategory) obj;
        return java.util.Objects.equals(name, that.name) && 
               java.util.Objects.equals(color, that.color);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, color);
    }
}