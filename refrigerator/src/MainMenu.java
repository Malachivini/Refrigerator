import javax.swing.*; // Importing Swing components
import java.awt.*; // Importing AWT components
import java.awt.image.BufferedImage; // Importing BufferedImage class
import java.io.*; // Importing I/O classes
import javax.imageio.ImageIO; // Importing ImageIO class
import java.util.*; // Importing utility classes
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class MainMenu extends GenericGUI {
    private BufferedImage backgroundImage; // Variable to store background image
    private Map<String, BufferedImage> imageCache = new HashMap<>(); // Cache for product images
    private Map<String, ImageIcon> scaledImageCache = new HashMap<>(); // Cache for scaled product images
    private Application app;

    public MainMenu(Application app) {
        super("Main Menu", 400, 700); // Call the super constructor
        this.app = app;

        

        // Show a loading indicator while images are being loaded
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Loading application...");
        frame.setContentPane(progressBar);
        frame.setVisible(true);

        // Load images asynchronously and wait for completion
        new Thread(() -> {
            loadImagesToCache();
            // Update UI on the Event Dispatch Thread after loading is complete
            SwingUtilities.invokeLater(() -> {
                frame.remove(progressBar);
                initializeUI();
            });
        }).start();
    }

    private void initializeUI() {
        try {
            backgroundImage = ImageIO.read(new File("refrigerator\\src\\photos\\refr.png")); // Load the background image
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace if an error occurs
        }

        BackgroundPanel backgroundPanel = new BackgroundPanel(); // Create a new BackgroundPanel
        backgroundPanel.setLayout(new GridBagLayout()); // Set the layout to GridBagLayout
        frame.setContentPane(backgroundPanel); // Set the content pane

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

        frame.setVisible(true);
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
        frame.setVisible(false); // Set the main menu invisible
        GlobalVariables.guis.get(Application.GUIType.REFRIGERATOR.ordinal()).show(); // Show the refrigerator app
    }

    private void openShoppingListScreen() {
        GlobalVariables.guis.get(Application.GUIType.SHOPPING.ordinal()).show(); // Show the existing ShoppingCartGUI instance
        frame.setVisible(false); // Set the main menu invisible
    }

    private void openRecipesScreen() {
        JOptionPane.showMessageDialog(frame, "Recipes Screen (to be implemented)"); // Show a message dialog
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

    // Method to load all product images into cache asynchronously
    private void loadImagesToCache() {
        ExecutorService executor = Executors.newFixedThreadPool(4); // Create a thread pool with 4 threads

        List<Product> productsBatch = new ArrayList<>();
        for (Product product : GlobalVariables.allproducts) {
            if (product.image() != null && !product.image().isEmpty()) {
                productsBatch.add(product);
                if (productsBatch.size() == 10) {
                    List<Product> batch = new ArrayList<>(productsBatch);
                    productsBatch.clear();
                    executor.submit(() -> loadImageBatch(batch));
                }
            }
        }

        if (!productsBatch.isEmpty()) {
            executor.submit(() -> loadImageBatch(productsBatch));
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    private void loadImageBatch(List<Product> batch) {
        for (Product product : batch) {
            try {
                File imageFile = new File(product.image());
                System.out.println("Loading image from: " + imageFile.getAbsolutePath());
                if (!imageFile.exists()) {
                    System.err.println("Image file does not exist: " + product.image());
                    continue;
                }
                BufferedImage image = ImageIO.read(imageFile);
                if (image == null) {
                    System.err.println("Failed to read image: " + product.image());
                    continue;
                }
                synchronized (imageCache) {
                    imageCache.put(product.image(), image); // Save the image in the cache

                    // Create and cache scaled image
                    Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    scaledImageCache.put(product.image(), new ImageIcon(scaledImage));
                }
            } catch (IOException e) {
                System.err.println("Error reading image file: " + product.image());
                e.printStackTrace(); // Print stack trace if an error occurs
            }
        }
    }

    @Override
    public void load() {
        // Implement the load method
    }

    @Override
    public void show() {
        frame.setVisible(true);
    }

    @Override
    public void close() {
        frame.dispose();
    }

    @Override
    public void save() {
        // Implement the save method
    }
}
