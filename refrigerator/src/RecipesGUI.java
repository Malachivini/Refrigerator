import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipesGUI extends GenericGUI {
    private List<Recipe> recipes; //All recipes in the app
    private JPanel cardPanel; // Main panel with CardLayout
    private CardLayout cardLayout; // Layout manager for cardPanel
    private JPanel recipeListPanel; // Panel showing the list of recipes
    private JPanel ingredientsPanel; // Panel showing the ingredients of a recipe
    private JPanel inputPanel; // Panel for adding new recipes
    private JButton availableRecipesButton;

    public RecipesGUI(String frameName, int frameWidth, int frameHeight) {
        super(frameName, frameWidth, frameHeight);
        this.recipes = new ArrayList<>();
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);
    }

    // A method loading all recipes part
    @Override
    public void load() {
        // Load existing recipes from the CSV file
        loadRecipesFromCSV(GlobalVariables.Recepies_LIST);
    
        // Create the panel to show the opening window - all of the recipes
        recipeListPanel = new JPanel();
        recipeListPanel.setLayout(new BoxLayout(recipeListPanel, BoxLayout.Y_AXIS));
        recipeListPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65), 1));
        recipeListPanel.setBackground(Color.WHITE);
    
        for (Recipe recipe : recipes) {
            JPanel recipePanel = createRecPanel(recipe);
            recipeListPanel.add(recipePanel);
        }
    
        // Create a scroll pane for the recipe list
        JScrollPane recipeListScrollPane = new JScrollPane(recipeListPanel);
        recipeListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        recipeListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
        // Add the recipe list scroll pane to the card panel
        cardPanel.add(recipeListScrollPane, "RecipeList");
    
        // Create the bottom panel for adding new recipes and available recipes
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65), 1));
        buttonPanel.setBackground(Color.WHITE);
    
        inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        inputPanel.setBackground(Color.WHITE);
    
        JTextField recipeInputField = new JTextField(); //place to write new recipe
        recipeInputField.setPreferredSize(new Dimension(200, 30));
        JButton addRecipeButton = createStyledButton("Add Recipe");
        addRecipeButton.addActionListener(e -> {
            String recipeText = recipeInputField.getText();
            if (!recipeText.isEmpty()) {
                addNewRecipe(recipeText);
                recipeInputField.setText(""); // Clear the input field after adding the recipe
            }
        });
        
        // Add the field to the screen
        inputPanel.add(recipeInputField);
        inputPanel.add(addRecipeButton);
    
        // Create a panel for the available recipes button and the back button
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        navigationPanel.setBackground(Color.WHITE);
    
        availableRecipesButton = createStyledButton("Available Recipes");
        availableRecipesButton.addActionListener(e -> showAvailableRecipes());
    
        JButton backButton = createStyledButton("Back to Main Menu");
        backButton.addActionListener(e -> returnToMainMenu());
    
        // Add the buttons to the screen
        navigationPanel.add(backButton);
        navigationPanel.add(availableRecipesButton);
    
        // Add all panels to the button panel
        buttonPanel.add(inputPanel); // Add inputPanel only in the main screen
        buttonPanel.add(navigationPanel);
    
        // A window listener to ensure save when the window is closed
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                close(); // Call the close method when the window is closing
            }
        });
    
        // Add components to the frame
        frame.getContentPane().add(cardPanel, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }
            
    // A method creates the button
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(60, 63, 65));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setUI(new BasicButtonUI());
        return button;
    }

    @Override
    public void show() {
        frame.setVisible(true); // set the window of 'Recipes' to become visible
    }

    @Override
    public void close() {
        save(); // Save the recipes before closing the application
        frame.dispose();
    }
    
    // A method saving all changes one have made in the recipes list to the data base
    public void save() {
        String filePath = GlobalVariables.Recepies_LIST;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Recipe recipe : recipes) {
                StringBuilder sb = new StringBuilder();
                sb.append(recipe.getName()).append(",");
                sb.append(String.join(",", recipe.getIngredients()));
                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
  

    // Method loading all recipes from the data base
    private void loadRecipesFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", 2);
                if (values.length > 1) {
                    String recipeName = values[0];
                    String[] ingredientsArray = values[1].split(",");
                    List<String> ingredients = new ArrayList<>(Arrays.asList(ingredientsArray));
                    recipes.add(new Recipe(recipeName, ingredients));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to add a new recipe
    private void addNewRecipe(String recipeText) {
        // Split the input text into the correct format
        String[] values = recipeText.split(",", 2);

        if(values.length <=1){
            // No products added to the recipe
            JOptionPane.showMessageDialog(frame, "No ingredients added OR Ingredients should be separated by a comma. try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        else{
            String recipeName = values[0].trim();
            String[] ingredientsArray = values[1].split(",");
            List<String> ingredients = new ArrayList<>(Arrays.asList(ingredientsArray));

            // Check the format: Product name Quantity
        for (String ingredient : ingredientsArray) {
            String[] parts = ingredient.trim().split("\\s+");
            String quantity = parts[parts.length -1];
            String productName = String.join(" ", Arrays.copyOfRange(parts, 0, parts.length - 1));
            
            // Check quantity is a number
            boolean isNum = true;
            for (char c : quantity.toCharArray()) {
                if (!Character.isDigit(c)) {
                    isNum = false;
                }
            }
            // Check the product name is valid
            boolean exsist = false;
            for (Product product : GlobalVariables.allproducts) {
                String pName = product.getName().trim();
                
                if (pName.equalsIgnoreCase(productName)) {
                    exsist = true;
                    break; // Found the ingredient
                }
            }

            if(!isNum || !exsist){
                JOptionPane.showMessageDialog(frame, "Invalid quantity OR Invalid ingredient. try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

            Recipe newRecipe = new Recipe(recipeName, ingredients);
            recipes.add(newRecipe);

            // Add the new recipe panel to the GUI
            JPanel newRecipePanel = createRecPanel(newRecipe);
            recipeListPanel.add(newRecipePanel);

            // Refresh the GUI
            recipeListPanel.revalidate();
            recipeListPanel.repaint();
        }
    }

    // Method creating the panel of each recipe
    private JPanel createRecPanel(Recipe recipe) {
        JPanel RecPanel = new JPanel();
        RecPanel.setLayout(new BorderLayout());
        RecPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65), 1));
        RecPanel.setPreferredSize(new Dimension(0, 50)); // Fixed height, variable width
        RecPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        RecPanel.setMinimumSize(new Dimension(0, 50));
        RecPanel.setBackground(Color.WHITE);

        // Adding the recipe name to the panel
        JLabel recipeLabel = new JLabel(recipe.getName());
        recipeLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        RecPanel.add(recipeLabel, BorderLayout.CENTER);

        // Adding the arrow button to see all ingredients
        JButton arrowButton = createStyledButton("→");
        arrowButton.addActionListener(e -> showIngredients(recipe));
        RecPanel.add(arrowButton, BorderLayout.EAST);

        return RecPanel;
    }

    // Method showig a recipe's ingredients
    private void showIngredients(Recipe recipe) {
        // Hide the input panel and available recipes button when showing ingredients
        inputPanel.setVisible(false);
        availableRecipesButton.setVisible(false);
    
        // Create a panel to show the recipe's ingredients
        ingredientsPanel = new JPanel();
        ingredientsPanel.setLayout(new BorderLayout());
        ingredientsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ingredientsPanel.setBackground(Color.WHITE);
    
        // Create the ingredients list panel
        JPanel ingredientsListPanel = new JPanel();
        ingredientsListPanel.setLayout(new BoxLayout(ingredientsListPanel, BoxLayout.Y_AXIS));
        ingredientsListPanel.setBackground(Color.WHITE);
    
        // the recipe name as a title
        JLabel recipeTitle = new JLabel(recipe.getName() + ":");
        recipeTitle.setFont(new Font("Arial", Font.BOLD, 16));
        recipeTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        ingredientsListPanel.add(recipeTitle);
    
    
        // List to hold missing ingredients
        List<String> missingIngredients = new ArrayList<>();
    
        for (String ingredient : recipe.getIngredients()) {
            String[] parts = ingredient.split("\\s+");
            String ingredientName = String.join(" ", Arrays.copyOf(parts, parts.length - 1)).trim();
            String quantity = parts[parts.length - 1].trim();

            // Find the product in order to write the correct unit
            String unit ="";
            for (Product product : GlobalVariables.allproducts) {
                String productName = product.getName().trim();
                
                if (productName.equalsIgnoreCase(ingredientName)) {
                     unit = product.getMeasurementUnit().trim();
                    break; // Found the ingredient
                }
            }

            String useUnit = "";
            if(unit.equalsIgnoreCase("USE_GRAMS")){
                useUnit = "[gr]";
            }
            else if(unit.equalsIgnoreCase("USE_LITER")){
                useUnit = "[ml]";
            }
            else{
                useUnit = "[units]";
            }
    
            String displayText = ingredientName + " - " + quantity + " " + useUnit;
            JLabel ingredientLabel = new JLabel(displayText);
            ingredientLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
            ingredientsListPanel.add(ingredientLabel);

            // Check if the ingredient is available, ignoring the quantity (might want couple of recipes)
            boolean found = false;
            for (Product product : GlobalVariables.RefrigeratorProducts) {
                if (ingredientName.equalsIgnoreCase(product.getName())) {
                    found = true;
                    break;
                }
            }
            
            // If we didnt find the ingredient- add to missing ingridients in order to show the user
            if (!found) {
                missingIngredients.add(ingredient);
            }
        }
    
        // Create a scroll pane for the ingredients list
        JScrollPane ingredientsScrollPane = new JScrollPane(ingredientsListPanel);
        ingredientsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        ingredientsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
        // Add the ingredients list to the ingredients panel
        ingredientsPanel.add(ingredientsScrollPane, BorderLayout.CENTER);
    
        // Create the status panel at the bottom with space for the status label and button
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Color.WHITE);
    
        JTextArea statusTextArea = new JTextArea();
        statusTextArea.setWrapStyleWord(true);
        statusTextArea.setLineWrap(true);
        statusTextArea.setEditable(false);
        statusTextArea.setBackground(Color.WHITE);
        JScrollPane statusScrollPane = new JScrollPane(statusTextArea);
        statusScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
        // Tell the user if there are missing ingredients
        if (missingIngredients.isEmpty()) {
            statusTextArea.setText("All ingredients available");
            statusTextArea.setForeground(Color.GREEN);
        }
        
        else {
            String missingText = "Missing ingredients: " + String.join(", ", missingIngredients);
            statusTextArea.setText(missingText);
            statusTextArea.setForeground(Color.RED);
        }
    
        // Create a panel for the status label and the button to ensure they don't overlap
        JPanel labelAndButtonPanel = new JPanel(new BorderLayout());
        labelAndButtonPanel.setBackground(Color.WHITE);
        labelAndButtonPanel.add(statusScrollPane, BorderLayout.CENTER);
    
        // Add the "Add to Shopping List" button if there are missing ingredients
        if (!missingIngredients.isEmpty()) {
            JButton addToShoppingListButton = createStyledButton("Add to Shopping List");
            addToShoppingListButton.setPreferredSize(new Dimension(200, 30));
            addToShoppingListButton.addActionListener(e -> addMissingToCart(missingIngredients));
            labelAndButtonPanel.add(addToShoppingListButton, BorderLayout.EAST);
        }
    
        statusPanel.add(labelAndButtonPanel, BorderLayout.CENTER);
    
        // Add the status panel to the ingredients panel
        ingredientsPanel.add(statusPanel, BorderLayout.SOUTH);
    
        // Create the back button and add it to the bottom right
        JButton backButton = createStyledButton("← Back");
        backButton.addActionListener(e -> returnToRecipeList());
    
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
        backPanel.add(backButton);
        backPanel.setBackground(Color.WHITE);
        ingredientsPanel.add(backPanel, BorderLayout.NORTH);
    
        // Add the ingredients panel to the card panel and show it
        cardPanel.add(ingredientsPanel, "Ingredients");
        cardLayout.show(cardPanel, "Ingredients");
    }
         
    // Methos to add missing ingredients of a recipe to the shopping cart
    private void addMissingToCart(List<String> missingIngredients) {
        // Access to the shopping cart from global variables
        ShoppingCartGUI shoppingCartGUI = (ShoppingCartGUI) GlobalVariables.guis.get(AppInterface.GUIType.SHOPPING.ordinal());
        
        // Add what's missing to the shopping cart (called only when the button is pressed)
        for (String missingIngredient : missingIngredients) {
            String ingredientName = missingIngredient.trim().replaceAll("\\s+\\S+$", "");
            String[] parts = missingIngredient.trim().split("\\s+");
            String quantityStr = parts[parts.length - 1]; // the quantity is the last part
    
            try {
                int quantityNeeded = Integer.parseInt(quantityStr);
                // Find the product in order to know in what quantity it is sold
                for (Product product : GlobalVariables.allproducts) {
                    String productName = product.getName().trim();
    
                    if (productName.equalsIgnoreCase(ingredientName)) {
                        int quantitySold = product.getQuantity_sold();
                        int quantityToAdd = (int) Math.ceil((double) quantityNeeded / quantitySold); // calculate how much to buy
                        shoppingCartGUI.addOrUpdateProduct(ingredientName, quantityToAdd);
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid quantity for ingredient: " + missingIngredient);
            }
        }
    
        JOptionPane.showMessageDialog(frame, "Missing ingredients added to shopping list.");
    }
    
    private void showAvailableRecipes() {
        // Hide the input panel and available recipes button when showing available recipes
        inputPanel.setVisible(false);
        availableRecipesButton.setVisible(false);
    
        JPanel availableRecipesPanel = new JPanel();
        availableRecipesPanel.setLayout(new BorderLayout());
        availableRecipesPanel.setBackground(Color.WHITE);
    
        // Create the list panel for available recipes
        JPanel availableRecipesListPanel = new JPanel();
        availableRecipesListPanel.setLayout(new BoxLayout(availableRecipesListPanel, BoxLayout.Y_AXIS));
        availableRecipesListPanel.setBackground(Color.WHITE);
    
        for (Recipe recipe : recipes) {
            if (areIngredientsAvailable(recipe, GlobalVariables.RefrigeratorProducts)) {
                JPanel recipePanel = createRecPanel(recipe);
                availableRecipesListPanel.add(recipePanel);
            }
        }
    
        // Create a scroll pane for the available recipes list
        JScrollPane availableRecipesScrollPane = new JScrollPane(availableRecipesListPanel);
        availableRecipesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        availableRecipesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
        // Add the available recipes list to the main panel
        availableRecipesPanel.add(availableRecipesScrollPane, BorderLayout.CENTER);
    
        // Create the back button and add it to the bottom right
        JButton backButton = createStyledButton("← Back");
        backButton.addActionListener(e -> returnToRecipeList());
    
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // align to the right
        backPanel.add(backButton);
        backPanel.setBackground(Color.WHITE);
        availableRecipesPanel.add(backPanel, BorderLayout.NORTH);
    
        // Add the available recipes panel to the card panel and show it
        cardPanel.add(availableRecipesPanel, "AvailableRecipes");
        cardLayout.show(cardPanel, "AvailableRecipes");
    }
    
    private void returnToRecipeList() {
        // Show the input panel and available recipes button when returning to the recipe list
        inputPanel.setVisible(true);
        availableRecipesButton.setVisible(true);
        cardLayout.show(cardPanel, "RecipeList");
    }
    
    // Method checking if all ingredients ofa recipe are available
    private boolean areIngredientsAvailable(Recipe recipe, List<Product> availableProducts) {
        for (String ingredient : recipe.getIngredients()) {
            // Remove the last word (quantity):
            String ingredientName = ingredient.trim().replaceAll("\\s+\\S+$", "");
    
            boolean found = false;
            // Check if this ingredient exist's in the refrigerator
            for (Product product : availableProducts) {
                if (ingredientName.equalsIgnoreCase(product.getName())) {
                    found = true;
                    break;
                }
            }
    
            if (!found) {
                // The product is missing
                return false;
            }
        }
        return true;
    }
            
    // Method to return to the main menu
    private void returnToMainMenu() {
        //save all changes before going out
        save();
        GlobalVariables.guis.get(AppInterface.GUIType.MAIN.ordinal()).show();
        frame.setVisible(false);
    }
}
        