import java.util.ArrayList;
import java.util.List;

public class Application {

    // Define the GUI types as an enum
    public enum GUIType {
        MAIN,
        REFRIGERATOR,
        SHOPPING,
        RECEPIES
    }

    // List to hold the GUI instances
    private List<GenericGUI> guis;

    // Constructor to initialize the list with 4 ShoppingCartGUI instances
    public Application() {
        guis = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            guis.add(new ShoppingCartGUI("Shopping Cart " + i, 400, 600));
        }
    }

    // Method to load all GUI instances
    public void loadAll() {
        for (GenericGUI gui : guis) {
            gui.load();
        }
    }

    // Method to show the GUI at the MAIN location
    public void showMain() {
        guis.get(GUIType.MAIN.ordinal()).show();
    }

    // Main method to run the application
    public static void main(String[] args) {
        new MainMenu().setVisible(true); // for now
        GlobalVariables.LoadProducts();
        Application app = new Application();
        app.loadAll();
        app.showMain();
    }
}

