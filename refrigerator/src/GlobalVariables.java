import java.io.*; // Importing I/O classes
import java.util.*; // Importing utility classes

public class GlobalVariables {
    public static Date date = new Date(15, 10, 2024); // Global date variable
    public static final String PRODUCT_IN_REFRIGERETOR = "refrigerator\\productsInRefrigeretor.csv"; // CSV file path
    public static final String INGREDIENTS = "refrigerator\\ingredients.csv"; // CSV file path
    public static final String SHOP_LIST = "refrigerator\\ShoppingCart.csv";
    public static final String Recepies_LIST = "refrigerator\\myRecipes.csv";
    public static List<Product> allproducts = new ArrayList<>(); // Global products list
    public static List<Product> RefrigeratorProducts = new ArrayList<>(); // Global products in refrigerator
    public static List<GenericGUI> guis = new ArrayList<>();

    public static void loadAllProductsFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip the header line

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 12) { // Check if the line has the correct number of values
                    System.err.println("Skipping invalid line: " + line);
                    continue;
                }

                String name = values[0];
                double calories = Double.parseDouble(values[1]);
                double protein = Double.parseDouble(values[2]);
                double fat = Double.parseDouble(values[3]);
                double carbohydrates = Double.parseDouble(values[4]);
                String measurementUnit = values[5];
                boolean consumable = Boolean.parseBoolean(values[6]);
                String category = values[7];
                int quantitySold = Integer.parseInt(values[8]);
                int validityDaysFromPurchase = Integer.parseInt(values[9]);
                double price = Double.parseDouble(values[10]);
                String imagePath = values[11];

                NutrientValues nutrientValues = new NutrientValues(calories, protein, fat, carbohydrates);

                Product product = new Product(name, nutrientValues, price, measurementUnit, category, quantitySold, validityDaysFromPurchase, imagePath);
                allproducts.add(product); // Add the product to the global list
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace if an error occurs
        }
    }

    public static void loadRefrigeratorProductsFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip the header line

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 3) { // Check if the line has the correct number of values
                    System.err.println("Skipping invalid line: " + line);
                    continue;
                }

                String name = values[0];
                String[] dateParts = values[1].split("\\.");
                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);
                Date expiration = new Date(day, month, year);
                double amount = Double.parseDouble(values[2]);

                Product fullProduct = findProductByName(name);
                if (fullProduct != null) {
                    Product product = new Product(fullProduct.getName(), fullProduct.getNutrientValues(), expiration, amount, fullProduct.getMeasurementUnit(), fullProduct.getImgPath(), fullProduct.getCategory());
                    RefrigeratorProducts.add(product); // Add the product to the refrigerator list
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace if an error occurs
        }
    }

    private static Product findProductByName(String name) {
        for (Product product : allproducts) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }
}
