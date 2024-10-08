import java.util.HashMap;

public class ApplicationImpl implements AppInterface {


    // Constructor to initialize the list with GUI instances
    public ApplicationImpl() {
        MainMenu mainMenu = new MainMenu(this);
        GlobalVariables.guis.add(mainMenu); // Add MainMenu instance
        GlobalVariables.guis.add(new RefrigeratorApp(new HashMap<>(), new HashMap<>(), GlobalVariables.date)); // Add RefrigeratorApp instance with MainMenu as parameter
        GlobalVariables.guis.add(new ShoppingCartGUI("Shopping Cart", 500, 600)); // Pass mainMenu reference
        GlobalVariables.guis.add(new RecipesGUI("Recipes", 500, 600));
    }

    // Method to load all GUI instances
    @Override
    public void loadAll() {
        for (GenericGUI gui : GlobalVariables.guis) {
            gui.load();
        }
    }

    // Method to show the GUI at the MAIN location
    @Override
    public void showMain() {
        GlobalVariables.guis.get(GUIType.MAIN.ordinal()).show();
    }

    // Method to get the RefrigeratorApp instance
    @Override
    public RefrigeratorApp getRefrigeratorApp() {
        return (RefrigeratorApp) GlobalVariables.guis.get(GUIType.REFRIGERATOR.ordinal());
    }

    // Main method to run the application
    public static void main(String[] args) {
        GlobalVariables.loadAllProductsFromCSV(GlobalVariables.INGREDIENTS);
        GlobalVariables.loadRefrigeratorProductsFromCSV(GlobalVariables.PRODUCT_IN_REFRIGERETOR); // Load refrigerator products from CSV file
        ApplicationImpl app = new ApplicationImpl();
        app.loadAll();
        app.showMain();
    }
}
