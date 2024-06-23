import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.List;
import java.util.*;

public class RefrigeratorApp extends JFrame {
    private BufferedImage leftDoorImage; // Image for the left door
    private BufferedImage rightDoorImage; // Image for the right door
    private JLabel dateLabel; // Label to display the date
    private JPanel mainPanel; // Main panel to display product information
    private JLabel nutritionalSummaryLabel; // Label to display nutritional summary
    private Map<String, BufferedImage> imageCache; // Cache for product images
    private Map<String, ImageIcon> scaledImageCache; // Cache for scaled product images

    private JFrame mainMenuFrame; // Reference to the main menu frame

    // Constructor to initialize the Refrigerator application
    public RefrigeratorApp(JFrame mainMenuFrame, Map<String, BufferedImage> imageCache, Map<String, ImageIcon> scaledImageCache, Date date) {
        this.mainMenuFrame = mainMenuFrame; // Initialize the main menu frame reference
        this.imageCache = imageCache; // Initialize the image cache
        this.scaledImageCache = scaledImageCache; // Initialize the scaled image cache
        GlobalVariables.date = date; // Initialize the date

        loadDoorImages("refrigerator\\src\\photos\\refr3.png"); // Load door images

        setTitle("Refrigerator"); // Set the title of the application
        setSize(1100, 1000); // Set the size of the application window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the default close operation
        setLayout(new BorderLayout()); // Set the layout to BorderLayout

        // Create and configure the left door panel
        JPanel leftDoorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(leftDoorImage, 0, 0, getWidth(), getHeight(), this); // Draw the left door image
            }
        };
        leftDoorPanel.setLayout(null); // Use null layout for absolute positioning
        leftDoorPanel.setPreferredSize(new Dimension(200, 800)); // Set a fixed width for the door

        // Create and configure the "Discard Expired" button
        JButton discardExpiredButton = createButton("Discard Expired");
        discardExpiredButton.setBounds(19, 350, 150, 30); // Set position and size of the button
        discardExpiredButton.addActionListener(e -> {
            discardExpired(GlobalVariables.date); // Discard expired products
            saveProductsToCSV(GlobalVariables.CSV_FILE_PATH); // Save the updated product list to CSV
        });
        leftDoorPanel.add(discardExpiredButton); // Add button to the left door panel

        // Create and configure the "Sort by expiration date" button
        JButton sortByExpirationDateButton = createButton("Sort by expiration");
        sortByExpirationDateButton.setBounds(19, 400, 150, 30); // Set position and size of the button
        sortByExpirationDateButton.addActionListener(e -> {
            sortByExpiration(); // Sort products by expiration date
            saveProductsToCSV(GlobalVariables.CSV_FILE_PATH); // Save the updated product list to CSV
        });
        leftDoorPanel.add(sortByExpirationDateButton); // Add button to the left door panel

        // Create and configure the "Sort by name" button
        JButton sortByNameButton = createButton("Sort by products");
        sortByNameButton.setBounds(19, 450, 150, 30); // Set position and size of the button
        sortByNameButton.addActionListener(e -> {
            sortByName(); // Sort products by name
            saveProductsToCSV(GlobalVariables.CSV_FILE_PATH); // Save the updated product list to CSV
        });
        leftDoorPanel.add(sortByNameButton); // Add button to the left door panel

        // Create and configure the "menu" button
        JButton menuButton = createButton("menu");
        menuButton.setBounds(19, 500, 150, 30); // Set position and size of the button
        menuButton.addActionListener(e -> returnToMainMenu()); // Add action listener
        leftDoorPanel.add(menuButton); // Add button to the left door panel

        // Load the image for the search label
        try {
            BufferedImage searchImage = ImageIO.read(new File("refrigerator\\src\\photos\\search2.png"));
            ImageIcon searchIcon = new ImageIcon(searchImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH));

            // Create and configure the search label with the image
            JLabel searchLabel = new JLabel(searchIcon);
            searchLabel.setBounds(10, 10, 100, 100); // Set position and size of the label
            searchLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showSearchDialog(); // Show the search dialog on click
                }
            });

            leftDoorPanel.add(searchLabel); // Add label to the left door panel
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create and configure the right door panel
        JPanel rightDoorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(rightDoorImage, 0, 0, getWidth(), getHeight(), this); // Draw the right door image
            }
        };
        rightDoorPanel.setLayout(null); // Use null layout for absolute positioning
        rightDoorPanel.setPreferredSize(new Dimension(200, 800)); // Set a fixed width for the door

        // Create and configure the date label
        dateLabel = new JLabel("<html><b><font size='6' color='white'>" + GlobalVariables.date.getDay() + "/" + GlobalVariables.date.getMonth() + "/" + GlobalVariables.date.getYear() + "</font></b></html>"); // Example date with larger font and bold
        dateLabel.setBounds(53, 50, 150, 40); // Set position and size of the date label
        rightDoorPanel.add(dateLabel); // Add date label to the right door panel

        // Create and configure the "Next Day" button
        JButton nextDayButton = createButton("Next Day");
        nextDayButton.setBounds(19, 550, 150, 30); // Set position and size of the button
        nextDayButton.addActionListener(e -> {
            GlobalVariables.date = GlobalVariables.date.getNextDay(); // Move to the next day
            dateLabel.setText("<html><b><font size='6' color='white'>" + GlobalVariables.date.getDay() + "/" + GlobalVariables.date.getMonth() + "/" + GlobalVariables.date.getYear() + "</font></b></html>"); // Update the date label
            refreshMainPanel(); // Refresh the main panel to update the display
        });
        rightDoorPanel.add(nextDayButton); // Add button to the right door panel

        // Create and configure the main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Layout in vertical box
        mainPanel.setBackground(Color.WHITE); // Set background color to white

        // Create and configure the nutritional summary label
        nutritionalSummaryLabel = new JLabel();
        nutritionalSummaryLabel.setFont(new Font("Arial", Font.BOLD, 11)); // Set font for the summary
        nutritionalSummaryLabel.setForeground(Color.WHITE); // Set text color to white
        rightDoorPanel.add(nutritionalSummaryLabel);
        nutritionalSummaryLabel.setBounds(20, 100, 200, 200); // Set position and size of the label

        // Add panels to the refrigerator panel
        add(leftDoorPanel, BorderLayout.WEST); // Add left door panel to the west
        add(new JScrollPane(mainPanel), BorderLayout.CENTER); // Add main panel to the center with scroll pane
        add(rightDoorPanel, BorderLayout.EAST); // Add right door panel to the east

        refreshMainPanel(); // Initial refresh to display products
    }

    // Method to show the search dialog
    private void showSearchDialog() {
        String productName = JOptionPane.showInputDialog(this, "Enter product name:", "Search Product", JOptionPane.PLAIN_MESSAGE);
        if (productName != null && !productName.trim().isEmpty()) {
            Optional<Product> product = GlobalVariables.products.stream()
                    .filter(p -> p.getName().equalsIgnoreCase(productName.trim()))
                    .findFirst();

            if (product.isPresent()) {
                showProductSelectionDialog(product.get());
            } else {
                JOptionPane.showMessageDialog(this, "Product not found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Method to return to the main menu
    private void returnToMainMenu() {
        mainMenuFrame.setVisible(true); // Show the main menu frame
        if (mainMenuFrame instanceof MainMenu) {
            ((MainMenu) mainMenuFrame).updateDate(GlobalVariables.date); // Update the date in the main menu
        }
        setVisible(false); // Hide the refrigerator frame
    }

    // Method to discard expired products based on the current date
    private void discardExpired(Date currentDate) {
        Iterator<Product> iterator = GlobalVariables.products.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            if (product.isExpired(currentDate)) {
                iterator.remove(); // Remove expired product from the list
            }
        }
        refreshMainPanel(); // Refresh the main panel to update the display
    }

    // Method to sort products by name
    private void sortByName() {
        Collections.sort(GlobalVariables.products, Comparator.comparing(Product::getName));
        refreshMainPanel(); // Refresh the main panel to update the display
    }

    // Method to sort products by expiration date
    private void sortByExpiration() {
        for (int i = 0; i < GlobalVariables.products.size(); i++) {
            for (int j = i + 1; j < GlobalVariables.products.size(); j++) {
                Product product1 = GlobalVariables.products.get(i);
                Product product2 = GlobalVariables.products.get(j);
                if (!product1.isAboutToExpired(product2.getExpiration(), 0)) {
                    swap(i, j); // Swap products if they are not in order
                }
            }
        }
        refreshMainPanel(); // Refresh the main panel to update the display
    }

    // Method to swap two products in the list
    private void swap(int i, int j) {
        Product temp = GlobalVariables.products.get(i);
        GlobalVariables.products.set(i, GlobalVariables.products.get(j));
        GlobalVariables.products.set(j, temp);
    }

    // Method to create a button with the specified text
    private JButton createButton(String text) {
        return new JButton(text);
    }

    // Method to load door images from the specified file path
    private void loadDoorImages(String filePath) {
        try {
            BufferedImage image = ImageIO.read(new File(filePath));
            int width = image.getWidth();
            int height = image.getHeight();
            leftDoorImage = image.getSubimage(0, 0, width / 2, height); // Split the image into left door
            rightDoorImage = image.getSubimage(width / 2, 0, width / 2, height); // Split the image into right door
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to save products to a CSV file
    private void saveProductsToCSV(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Name,Expiration Date,Price,Amount"); // Header
            for (Product product : GlobalVariables.products) {
                writer.println(product.getName() + "," + product.getExpiration().getDay() + "." + product.getExpiration().getMonth() + "." + product.getExpiration().getYear() + "," + product.getPrice() + "," + product.getAmount());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to create a panel for a specific category of products
    private JScrollPane createCategoryPanel(String category) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(category)); // Set border with category title
        panel.setLayout(new FlowLayout(FlowLayout.LEFT)); // FlowLayout for horizontal alignment
        panel.setBackground(Color.WHITE); // Set background color to white

        for (Product product : GlobalVariables.products) {
            if (product.departments().equals(category)) {
                JPanel productPanel = createProductPanel(product);
                panel.add(productPanel); // Add product panel to the category panel
            }
        }

        // Create a scroll pane for the category panel
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(600, 200)); // Set a preferred size for each category panel

        return scrollPane;
    }

    // Method to create a panel for a product
    private JPanel createProductPanel(Product product) {
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BorderLayout());
        productPanel.setBackground(Color.WHITE); // Set background color to white

        // Add product image if available in cache
        if (product.image() != null && !product.image().isEmpty()) {
            ImageIcon scaledImageIcon = scaledImageCache.get(product.image());
            if (scaledImageIcon != null) {
                JLabel productImageLabel = new JLabel(scaledImageIcon);
                productPanel.add(productImageLabel, BorderLayout.CENTER);
            }
        }

        // Set product details with appropriate color based on expiration status
        String productDetails = "<html><span style='color:green;'>" + product.getDetails().replace("\n", "<br>") + "</span></html>";
        if (product.isAboutToExpired(GlobalVariables.date, 2)) {
            productDetails = "<html><span style='color:orange;'>" + product.getDetails().replace("\n", "<br>") + "</span></html>";
        }
        if (product.isAboutToExpired(GlobalVariables.date, 0)) {
            productDetails = "<html><span style='color:red;'>" + product.getDetails().replace("\n", "<br>") + "</span></html>";
        }

        JLabel productDetailsLabel = new JLabel(productDetails);
        productDetailsLabel.setVerticalTextPosition(SwingConstants.TOP);
        productDetailsLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        productPanel.add(productDetailsLabel, BorderLayout.SOUTH);

        productPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showProductSelectionDialog(product);
            }
        });

        return productPanel;
    }

    private void showProductSelectionDialog(Product product) {
        JDialog dialog = new JDialog(this, "Select Quantity", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.getContentPane().setBackground(Color.WHITE); // Set background color to white

        // Create a panel for the title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Set no gap between components
        titlePanel.setBackground(Color.WHITE); // Set background color to white

        JLabel titleLabel = new JLabel(product.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30)); // Set custom font size and style
        titleLabel.setBackground(Color.WHITE); // Set background color to white

        // Load product image
        ImageIcon productImageIcon = scaledImageCache.get(product.image());
        JLabel productImageLabel = new JLabel(productImageIcon);

        titlePanel.add(titleLabel);
        titlePanel.add(productImageLabel);
        dialog.add(titlePanel, BorderLayout.NORTH);

        // Create a panel for the lower part
        JPanel lowerPanel = new JPanel(new GridBagLayout());
        lowerPanel.setBackground(Color.WHITE); // Set background color to white

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel amountLabel = new JLabel("Available: ");
        gbc.gridx = 0;
        gbc.gridy = 0;
        lowerPanel.add(amountLabel, gbc);

        String unit = getUnit(product);
        JLabel amountValueLabel = new JLabel(String.valueOf(product.getAmount()) + " " + unit);
        gbc.gridx = 1;
        gbc.gridy = 0;
        lowerPanel.add(amountValueLabel, gbc);

        JLabel quantityLabel = new JLabel("Select Quantity: ");
        JTextField quantityField = new JTextField(10); // Set preferred size
        quantityField.setBackground(new Color(173, 216, 230)); // Set custom background color (light blue)
        gbc.gridx = 0;
        gbc.gridy = 1;
        lowerPanel.add(quantityLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        lowerPanel.add(quantityField, gbc);

        JLabel unitLabel = new JLabel(unit); // Add unit label next to the input field
        gbc.gridx = 2;
        gbc.gridy = 1;
        lowerPanel.add(unitLabel, gbc);

        // Load and resize the image
        JLabel bgImageLabel = null;
        try {
            BufferedImage bgImage = ImageIO.read(new File("refrigerator\\src\\photos\\open_mouth.png"));
            Image scaledImage = bgImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            bgImageLabel = new JLabel(new ImageIcon(scaledImage));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(Color.WHITE); // Set background color to white

        if (bgImageLabel != null) {
            containerPanel.add(bgImageLabel, BorderLayout.WEST); // Add image to the left
        }
        containerPanel.add(lowerPanel, BorderLayout.CENTER); // Add the lower panel to the center

        dialog.add(containerPanel, BorderLayout.CENTER);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> {
            String quantityText = quantityField.getText();
            try {
                double quantity = Double.parseDouble(quantityText);
                updateProductQuantity(product, quantity);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(confirmButton, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Method to get the unit of the product
    private String getUnit(Product product) {
        switch (product.getMeasurementUnit()) {
            case "USE_QUANTITY":
                return "units";
            case "USE_GRAMS":
                return "grams";
            case "USE_LITER":
                if (product.getAmount() >= 1000) {
                    product.setAmount(product.getAmount() / 1000); // Convert to liters
                    return "liters";
                } else {
                    return "ml";
                }
            default:
                return "";
        }
    }

    // Method to update the product quantity
    private void updateProductQuantity(Product product, double quantity) {
        if (quantity > 0 && quantity <= product.getAmount()) {
            product.setAmount(product.getAmount() - quantity);
            if (product.getAmount() == 0) {
                GlobalVariables.products.remove(product); // Remove the product from the list if amount is 0
            }
            saveProductsToCSV(GlobalVariables.CSV_FILE_PATH); // Save the updated product list to CSV
            refreshMainPanel(); // Refresh the main panel to update the display
        } else {
            JOptionPane.showMessageDialog(this, "Invalid quantity selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to calculate and display the nutritional summary of all products
    private void displayNutritionalSummary() {
        double totalCalories = 0;
        double totalProtein = 0;
        double totalFat = 0;
        double totalCarbohydrates = 0;

        for (Product product : GlobalVariables.products) {
            if(product.getMeasurementUnit().equals("USE_QUANTITY")){
                totalCalories += product.getNutrientValues().getCalories()*product.getAmount();
                totalProtein += product.getNutrientValues().getProtein()*product.getAmount();
                totalFat += product.getNutrientValues().getFat()*product.getAmount();
                totalCarbohydrates += product.getNutrientValues().getCarbohydrates()*product.getAmount();
            }
            else if(product.getMeasurementUnit().equals("USE_GRAMS")||product.getMeasurementUnit().equals("USE_LITER")){
                totalCalories += product.getNutrientValues().getCalories()*(product.getAmount()/100);
                totalProtein += product.getNutrientValues().getProtein()*(product.getAmount()/100);
                totalFat += product.getNutrientValues().getFat()*(product.getAmount()/100);
                totalCarbohydrates += product.getNutrientValues().getCarbohydrates()*(product.getAmount()/100);
            }
        }

        // Check for the conditions and set the color accordingly
        String proteinColor = totalProtein < 2000 ? "red" : "white";
        String fatColor = totalFat < 200 ? "red" : "white";
        String carbohydratesColor = totalCarbohydrates < 450 ? "red" : "white";

        // Use HTML to set the color of each row based on the conditions
        String proteinSummary = String.format("<font color='%s'>Total Protein: %.2f g</font>", proteinColor, totalProtein);
        String fatSummary = String.format("<font color='%s'>Total Fat: %.2f g</font>", fatColor, totalFat);
        String carbohydratesSummary = String.format("<font color='%s'>Total Carbohydrates: %.2f g</font>", carbohydratesColor, totalCarbohydrates);

        String summary = String.format(
                "<html>Total Calories: %.2f<br>%s<br>%s<br>%s</html>",
                totalCalories, proteinSummary, fatSummary, carbohydratesSummary
        );

        nutritionalSummaryLabel.setText(summary); // Set the text for the nutritional summary label
    }

    // Method to refresh the main panel with updated product information
    private void refreshMainPanel() {
        mainPanel.removeAll(); // Remove all existing components from the main panel

        // Add category panels to the main panel
        mainPanel.add(createCategoryPanel("Meat"));
        mainPanel.add(createCategoryPanel("Dairy"));
        mainPanel.add(createCategoryPanel("Dry Food"));
        mainPanel.add(createCategoryPanel("VegetableFruit"));

        displayNutritionalSummary(); // Display the nutritional summary

        mainPanel.revalidate(); // Revalidate the main panel
        mainPanel.repaint(); // Repaint the main panel to reflect changes
    }

    public static void main(String[] args) {
        // This main method is not needed because the RefrigeratorApp will be launched from MainMenu
    }
}