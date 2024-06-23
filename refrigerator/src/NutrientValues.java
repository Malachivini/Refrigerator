public class NutrientValues {
    private double calories; // Calories content
    private double protein; // Protein content
    private double fat; // Fat content
    private double carbohydrates; // Carbohydrates content

    // Constructor to initialize a NutrientValues object with all fields
    public NutrientValues(double calories, double protein, double fat, double carbohydrates) {
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
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

    // Override toString method to provide a string representation of the object
    @Override
    public String toString() {
        return "NutrientValues{" +
                "calories=" + calories +
                ", protein=" + protein +
                ", fat=" + fat +
                ", carbohydrates=" + carbohydrates +
                '}';
    }
}
