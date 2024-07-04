import java.util.HashMap;

public class Application {

    // Define the GUI types as an enum
    public enum GUIType {
        MAIN,
        REFRIGERATOR,
        SHOPPING,
        RECEPIES
    }

    // Constructor to initialize the list with GUI instances
    public Application() {
        MainMenu mainMenu = new MainMenu(this);
        GlobalVariables.guis.add(mainMenu); // Add MainMenu instance
        GlobalVariables.guis.add(new RefrigeratorApp(mainMenu, new HashMap<>(), new HashMap<>(), GlobalVariables.date)); // Add RefrigeratorApp instance with MainMenu as parameter

        // Add ShoppingCartGUI instances with reference to MainMenu
        for (int i = 0; i < 2; i++) {
            GlobalVariables.guis.add(new ShoppingCartGUI("Shopping Cart " + i, 400, 600, mainMenu)); // Pass mainMenu reference
        }
    }

    // Method to load all GUI instances
    public void loadAll() {
        int x = 0;
        for (GenericGUI gui : GlobalVariables.guis) {
            gui.load();
            System.out.println(x++);
        }
    }

    // Method to show the GUI at the MAIN location
    public void showMain() {
        GlobalVariables.guis.get(GUIType.MAIN.ordinal()).show();
    }

    // Method to get the RefrigeratorApp instance
    public RefrigeratorApp getRefrigeratorApp() {
        return (RefrigeratorApp) GlobalVariables.guis.get(GUIType.REFRIGERATOR.ordinal());
    }

    // Main method to run the application
    public static void main(String[] args) {
        GlobalVariables.loadAllProductsFromCSV(GlobalVariables.INGREDIENTS);
        GlobalVariables.loadRefrigeratorProductsFromCSV(GlobalVariables.PRODUCT_IN_REFRIGERETOR); // Load refrigerator products from CSV file
        Application app = new Application();
        app.loadAll();
        app.showMain();
    }
}
