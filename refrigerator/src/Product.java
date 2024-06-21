import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Class representing a Product with its details and nutrient values
public class Product {
    private String name; // Name of the product
    private NutrientValues nutrientValues; // Nutrient values of the product
    private Date expiration; // Expiration date of the product
    private double price; // Price of the product
    private Integer amount; // Amount of the product

    // Constructor to initialize a Product object with all fields
    public Product(String name, NutrientValues nutrientValues, Date expiration, double price, Integer amount) {
        this.name = name;
        this.nutrientValues = nutrientValues;
        this.price = price;
        this.expiration = expiration;
        this.amount = amount;
    }

    // Getters and setters for each field
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getExpiration() { return expiration; }
    public void setExpiration(Date expiration) { this.expiration = expiration; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public NutrientValues getNutrientValues() { return nutrientValues; }
    public void setNutrientValues(NutrientValues nutrientValues) { this.nutrientValues = nutrientValues; }
    public String departments() { return this.nutrientValues.getCategory(); }
    public String image() { return this.nutrientValues.getImage(); }
    public double calories() { return this.nutrientValues.getCalories(); }

    // Method to check if the product is expired based on the current date
    public boolean isExpired(Date currentDate) {
        return expiration.isBefore(currentDate, 0);
    }

    // Method to check if the product is about to expire within a specified number of days
    public boolean isAboutToExpired(Date currentDate, Integer daysBefore) {
        return expiration.isBefore(currentDate, daysBefore);
    }

    // New method to format the amount based on the measurement unit
    private String formatAmount() {
        String unit = nutrientValues.getMeasurementUnit();
        double formattedAmount = amount;
        String unitLabel = unit;

        switch (unit) {
            case "USE_GRAMS":
                if (amount >= 1000) {
                    formattedAmount = amount / 1000.0;
                    unitLabel = "kg";
                } else {
                    unitLabel = "grams";
                    return String.format("%d %s", (int) formattedAmount, unitLabel);
                }
                return String.format("%.1f %s", formattedAmount, unitLabel);

            case "USE_LITER":
                if (amount >= 1000) {
                    formattedAmount = amount / 1000.0;
                    unitLabel = "liter";
                } else {
                    unitLabel = "ml";
                    return String.format("%d %s", (int) formattedAmount, unitLabel);
                }
                return String.format("%.1f %s", formattedAmount, unitLabel);

            case "USE_QUANTITY":
                unitLabel = "Units";
                return String.format("%d %s", (int) formattedAmount, unitLabel);

            default:
                return String.format("%.1f %s", formattedAmount, unitLabel);
        }
    }

    // Method to get detailed information about the product
    public String getDetails() {
        return this.name + "\n" +
               "expiry date: " + getExpiration() + "\n" +
               formatAmount();
    }

    // Override toString method to provide a string representation of the object
    @Override
    public String toString() {
        return "Product{" +
               "name:'" + name + '\'' +
               ", nutrientValues:" + nutrientValues + " expiration date: " + expiration + " price: " + price +
               '}';
    }

    // Static method to read nutrient values from a default file "ingredients.csv"
    public static Map<String, NutrientValues> readNutrientValuesFromFile() {
        String filePath = "refrigerator\\\\ingredients.csv"; // Path to the ingredients file
        Map<String, NutrientValues> nutrientValuesMap = new HashMap<>(); // Map to store nutrient values
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 9) { // Ensure the line has the correct number of values
                    String productName = values[0];
                    double calories = Double.parseDouble(values[1]);
                    double protein = Double.parseDouble(values[2]);
                    double fat = Double.parseDouble(values[3]);
                    double carbohydrates = Double.parseDouble(values[4]);
                    String measurementUnit = values[5];
                    boolean consumable = Boolean.parseBoolean(values[6]);
                    String category = values[7];
                    String imgPath = "refrigerator\\" + values[8];

                    NutrientValues nutrientValues = new NutrientValues(calories, protein, fat, carbohydrates, measurementUnit, consumable, category, imgPath);
                    nutrientValuesMap.put(productName, nutrientValues); // Add the nutrient values to the map
                } else {
                    System.err.println("Skipping invalid line: " + line); // Print an error message for invalid lines
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace for IOException
        }
        return nutrientValuesMap; // Return the map of nutrient values
    }
}
