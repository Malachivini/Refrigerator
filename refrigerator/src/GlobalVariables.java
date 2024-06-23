import java.util.List; // Importing List class
import java.util.ArrayList; // Importing List class

public class GlobalVariables {
    public static Date date = new Date(15, 10, 2024); // Global date variable
    public static final String CSV_FILE_PATH = "refrigerator\\productsInRefrigeretor.csv"; // CSV file path
    public static final String CSV_FILE_PATH2 = "refrigerator\\ingredients.csv"; // CSV file path
    public static List<Product> products = new ArrayList<>(); // Global products list
}
