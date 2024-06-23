import javax.swing.*; // Importing Swing components
import java.awt.*; // Importing AWT components
import java.awt.image.BufferedImage; // Importing BufferedImage class
import java.io.*; // Importing I/O classes
import javax.imageio.ImageIO; // Importing ImageIO class
import java.util.*; // Importing utility classes
import java.io.BufferedReader; // Importing BufferedReader class
import java.io.FileReader; // Importing FileReader class
import java.io.IOException; // Importing IOException class
import java.util.HashMap; // Importing HashMap class
import java.util.Map; // Importing Map class
import java.util.List; // Importing List class

public class MainMenu extends JFrame {
    private BufferedImage backgroundImage; // Variable to store background image
    private Map<String, BufferedImage> imageCache = new HashMap<>(); // Cache for product images
    private Map<String, ImageIcon> scaledImageCache = new HashMap<>(); // Cache for scaled product images

    public MainMenu() {
        setTitle("Main Menu"); // Setting the title of the window
        setSize(400, 700); // Setting the size of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Setting the default close operation

        loadProductsFromCSV(GlobalVariables.CSV_FILE_PATH, GlobalVariables.CSV_FILE_PATH2); // Load products from CSV files

        // Load images asynchronously
        new Thread(this::loadImagesToCache).start(); // Start a new thread to load images

        try {
            backgroundImage = ImageIO.read(new File("refrigerator\\src\\photos\\refr.png")); // Load the background image
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace if an error occurs
        }

        BackgroundPanel backgroundPanel = new BackgroundPanel(); // Create a new BackgroundPanel
        backgroundPanel.setLayout(new GridBagLayout()); // Set the layout to GridBagLayout
        setContentPane(backgroundPanel); // Set the content pane

        GridBagConstraints gbc = new GridBagConstraints(); // Create GridBagConstraints
        gbc.insets = new Insets(10, 10, 10, 10); // Set spacing between buttons

        JButton goToRefrigeratorButton = createButton("Go to Refrigerator"); // Create "Go to Refrigerator" button
        JButton goToShoppingListButton = createButton("Go to Shopping List"); // Create "Go to Shopping List" button
        JButton goToRecipesButton = createButton("Go to Recipes"); // Create "Go to Recipes" button

        goToRefrigeratorButton.addActionListener(e -> openRefrigeratorScreen()); // Add action listener to "Go to Refrigerator" button
        goToShoppingListButton.addActionListener(e -> openShoppingListScreen()); // Add action listener to "Go to Shopping List" button
        goToRecipesButton.addActionListener(e -> openRecipesScreen()); // Add action listener to "Go to Recipes" button

        gbc.gridx = 0; // Set grid x position
        gbc.gridy = 0; // Set grid y position
        backgroundPanel.add(goToRefrigeratorButton, gbc); // Add "Go to Refrigerator" button to the panel

        gbc.gridy = 1; // Set grid y position
        backgroundPanel.add(goToShoppingListButton, gbc); // Add "Go to Shopping List" button to the panel

        gbc.gridy = 2; // Set grid y position
        backgroundPanel.add(goToRecipesButton, gbc); // Add "Go to Recipes" button to the panel
    }

    public void updateDate(Date newDate) {
        GlobalVariables.date = newDate; // Update the global date variable
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text); // Create a new button with the specified text
        button.setPreferredSize(new Dimension(150, 30)); // Set the preferred size of the button
        return button; // Return the created button
    }

    private void openRefrigeratorScreen() {
        RefrigeratorApp refrigeratorApp = new RefrigeratorApp(this, imageCache, scaledImageCache, GlobalVariables.date); // Create a new RefrigeratorApp instance
        setVisible(false); // Set the main menu invisible
        refrigeratorApp.setVisible(true); // Set the refrigerator app visible
    }

    private void openShoppingListScreen() {
        JOptionPane.showMessageDialog(this, "Shopping List Screen (to be implemented)"); // Show a message dialog
    }

    private void openRecipesScreen() {
        JOptionPane.showMessageDialog(this, "Recipes Screen (to be implemented)"); // Show a message dialog
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Call the superclass method
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw the background image
            }
        }
    }

    // Method to load products from CSV files
    private void loadProductsFromCSV(String filePath, String filePath2) {
        Map<String, NutrientValues> nutrientValuesMap = Product.readNutrientValuesFromFile(); // Read nutrient values from file

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip the header line

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length < 4) { // Check if the line has the correct number of values
                    System.err.println("Skipping invalid line: " + line);
                    continue;
                }

                String name = values[0];
                String[] dateParts = values[1].split("\\.");
                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);
                Date date = new Date(day, month, year);
                double price = Double.parseDouble(values[2]);
                double amount = Double.parseDouble(values[3]);

                try (BufferedReader br2 = new BufferedReader(new FileReader(filePath2))) {
                    String line2;
                    br2.readLine(); // Skip the header line

                    while ((line2 = br2.readLine()) != null) {
                        String[] values2 = line2.split(",");
                        if (values2.length < 11) { // Check if the line has the correct number of values
                            System.err.println("Skipping invalid line: " + line2);
                            continue;
                        }

                        if (values2[0].equals(name)) {
                            String measurementUnit = values2[5];
                            boolean consumable = Boolean.parseBoolean(values2[6]);
                            String category = values2[7];
                            Integer Quantity_sold = Integer.parseInt(values2[8]);
                            Integer valid_Days = Integer.parseInt(values2[9]);
                            String imgPath = values2[10];

                            Product product = new Product(name, nutrientValuesMap.get(name), date, price, amount, measurementUnit, consumable, category, Quantity_sold, valid_Days, imgPath); // Create a new product
                            GlobalVariables.products.add(product); // Add the product to the global list
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // Print stack trace if an error occurs
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace if an error occurs
        }
    }

    // Method to load all product images into cache asynchronously
    private void loadImagesToCache() {
        for (Product product : GlobalVariables.products) {
            if (product.image() != null && !product.image().isEmpty()) {
                try {
                    File imageFile = new File(product.image());
                    if (!imageFile.exists()) {
                        System.err.println("Image file does not exist: " + product.image());
                        continue;
                    }
                    BufferedImage image = ImageIO.read(imageFile);
                    if (image == null) {
                        System.err.println("Failed to read image: " + product.image());
                        continue;
                    }
                    imageCache.put(product.image(), image); // Save the image in the cache

                    // Create and cache scaled image
                    Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    scaledImageCache.put(product.image(), new ImageIcon(scaledImage));
                } catch (IOException e) {
                    System.err.println("Error reading image file: " + product.image());
                    e.printStackTrace(); // Print stack trace if an error occurs
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true)); // Run the application
    }
}
