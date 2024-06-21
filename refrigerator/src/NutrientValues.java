// Class representing the nutritional values of a product
public class NutrientValues {
    private double calories; // Calories content
    private double protein; // Protein content
    private double fat; // Fat content
    private double carbohydrates; // Carbohydrates content
    private String measurementUnit; // Unit of measurement for the nutrients
    private boolean consumable; // Indicates if the product is consumable
    private String category; // Category of the product
    private String imgPath; // Path to the image of the product

    // Constructor to initialize a NutrientValues object with all fields
    public NutrientValues(double calories, double protein, double fat, double carbohydrates,
                          String measurementUnit, boolean consumable, String category, String imgPath) {
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.measurementUnit = measurementUnit;
        this.consumable = consumable;
        this.category = category;
        this.imgPath = imgPath;
    }

    // Getters and setters for each field
    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }

    public double getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(double carbohydrates) { this.carbohydrates = carbohydrates; }

    public String getMeasurementUnit() { return measurementUnit; }
    public void setMeasurementUnit(String measurementUnit) { this.measurementUnit = measurementUnit; }

    public boolean isConsumable() { return consumable; }
    public void setConsumable(boolean consumable) { this.consumable = consumable; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImage() { return imgPath; }

    // Override toString method to provide a string representation of the object
    @Override
    public String toString() {
        return "NutrientValues{" +
                "calories=" + calories +
                ", protein=" + protein +
                ", fat=" + fat +
                ", carbohydrates=" + carbohydrates +
                ", measurementUnit='" + measurementUnit + '\'' +
                ", consumable=" + consumable +
                ", category='" + category + '\'' +
                '}';
    }
}
