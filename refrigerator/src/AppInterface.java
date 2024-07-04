public interface AppInterface {
    void loadAll();
    void showMain();
    RefrigeratorApp getRefrigeratorApp();

     // Define the GUI types as an enum
     enum GUIType {
        MAIN,
        REFRIGERATOR,
        SHOPPING,
        RECEPIES
    }
}
