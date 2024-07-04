import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class MainMenu extends GenericGUI {
    private BufferedImage backgroundImage;
    private Map<String, BufferedImage> imageCache = new HashMap<>();
    private Map<String, ImageIcon> scaledImageCache = new HashMap<>();
    private AppInterface app;

    public MainMenu(AppInterface app) {
        super("Main Menu", 400, 700);
        this.app = app;
    }

    @Override
    public void load() {
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
            backgroundImage = ImageIO.read(new File("refrigerator\\src\\photos\\refr.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        frame.setContentPane(backgroundPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton goToRefrigeratorButton = createButton("Go to Refrigerator");
        JButton goToShoppingListButton = createButton("Go to Shopping List");
        JButton goToRecipesButton = createButton("Go to Recipes");

        goToRefrigeratorButton.addActionListener(e -> openRefrigeratorScreen());
        goToShoppingListButton.addActionListener(e -> openShoppingListScreen());
        goToRecipesButton.addActionListener(e -> openRecipesScreen());

        gbc.gridx = 0;
        gbc.gridy = 0;
        backgroundPanel.add(goToRefrigeratorButton, gbc);

        gbc.gridy = 1;
        backgroundPanel.add(goToShoppingListButton, gbc);

        gbc.gridy = 2;
        backgroundPanel.add(goToRecipesButton, gbc);
    }

    public void updateDate(Date newDate) {
        GlobalVariables.date = newDate;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 30));
        return button;
    }

    private void openRefrigeratorScreen() {
        frame.setVisible(false);
        GlobalVariables.guis.get(AppInterface.GUIType.REFRIGERATOR.ordinal()).show();
    }

    private void openShoppingListScreen() {
        GlobalVariables.guis.get(AppInterface.GUIType.SHOPPING.ordinal()).show();
        frame.setVisible(false);
    }

    private void openRecipesScreen() {
        JOptionPane.showMessageDialog(frame, "Recipes Screen (to be implemented)");
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private void loadImagesToCache() {
        ExecutorService executor = Executors.newFixedThreadPool(4);

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
                    imageCache.put(product.image(), image);

                    Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    scaledImageCache.put(product.image(), new ImageIcon(scaledImage));
                }
            } catch (IOException e) {
                System.err.println("Error reading image file: " + product.image());
                e.printStackTrace();
            }
        }
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
    }

    public static void main(String[] args) {
        AppInterface app = new ApplicationImpl(); // Use ApplicationImpl instead of Application
        MainMenu mainMenu = new MainMenu(app);
        mainMenu.load();
        mainMenu.show();
    }
}
