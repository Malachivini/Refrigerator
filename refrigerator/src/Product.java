import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Product {
    private String name; // Name of the product
    private NutrientValues nutrientValues; // Nutrient values of the product
    private Date expiration; // Expiration date of the product
    private double price; // Price of the product
    private double amount; // Amount of the product

    private String measurementUnit; // Unit of measurement for the product
    private boolean consumable; // Indicates if the product is consumable
    private String category; // Category of the product
    private String imgPath; // Path to the image of the product
    private Integer quantity_sold;
    private Integer valid_Days;

    public Product(String name,NutrientValues nutrientValues, Date expiration,double amount, String measurementUnit,String imgPath, String category){ //refrigeretor
        this.name = name;
        this.expiration=expiration;
        this.amount = amount;
        this.nutrientValues=nutrientValues;
        this.measurementUnit =measurementUnit;
        this.imgPath =imgPath;
        this.category=category;
    }

    public Product(String name, NutrientValues nutrientValues, double price, 
    String measurementUnit, String category,Integer quantity_sold,Integer valid_Days, String imgPath){ //shopping
        this.name = name;
        this.nutrientValues = nutrientValues;
        this.price = price;
        this.measurementUnit = measurementUnit;
        this.category = category;
        this.quantity_sold = quantity_sold;
        this.imgPath = imgPath;
        this.valid_Days = valid_Days;
    }

    // Constructor to initialize a Product object with all fields
    public Product(String name, NutrientValues nutrientValues, Date expiration, double price, double amount,
                   String measurementUnit, boolean consumable, String category,Integer quantity_sold,Integer valid_Days, String imgPath) {
        this.name = name;
        this.nutrientValues = nutrientValues;
        this.expiration = expiration;
        this.price = price;
        this.amount = amount;
        this.measurementUnit = measurementUnit;
        this.consumable = consumable;
        this.category = category;
        this.quantity_sold = quantity_sold;
        this.imgPath = imgPath;
        this.valid_Days = valid_Days;
        
    }

    // Getters and setters for each field
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getExpiration() { return expiration; }
    public void setExpiration(Date expiration) { this.expiration = expiration; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public NutrientValues getNutrientValues() { return nutrientValues; }
    public void setNutrientValues(NutrientValues nutrientValues) { this.nutrientValues = nutrientValues; }
    public String getMeasurementUnit() { return measurementUnit; }
    public void setMeasurementUnit(String measurementUnit) { this.measurementUnit = measurementUnit; }
    public boolean isConsumable() { return consumable; }
    public void setConsumable(boolean consumable) { this.consumable = consumable; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getQuantity_sold() { return quantity_sold; }
    public void setQuantity_sold(Integer quantity_sold) { this.quantity_sold = quantity_sold; }
    public Integer getvalid_Days() { return valid_Days; }
    public void setvalid_Days(Integer valid_Days) { this.valid_Days = valid_Days; }
    public String getImgPath() { return imgPath; }
    public void setImgPath(String imgPath) { this.imgPath = imgPath; }

    public String departments() { return this.category; }
    public String image() { return this.imgPath; }
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
        String unit = measurementUnit;
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
               ", measurementUnit:'" + measurementUnit + '\'' +
               ", consumable:" + consumable +
               ", category:'" + category + '\'' +
               ", imgPath:'" + imgPath + '\'' +
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
                if (values.length == 12) { // Ensure the line has the correct number of values
                    String productName = values[0];
                    double calories = Double.parseDouble(values[1]);
                    double protein = Double.parseDouble(values[2]);
                    double fat = Double.parseDouble(values[3]);
                    double carbohydrates = Double.parseDouble(values[4]);

                    NutrientValues nutrientValues = new NutrientValues(calories, protein, fat, carbohydrates);
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
