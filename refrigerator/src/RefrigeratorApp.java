import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;


public class RefrigeratorApp extends GenericGUI {
    private BufferedImage leftDoorImage; // Image for the left door
    private BufferedImage rightDoorImage; // Image for the right door
    private JLabel dateLabel; // Label to display the date
    private JPanel mainPanel; // Main panel to display product information
    private JLabel nutritionalSummaryLabel; // Label to display nutritional summary
    private Map<String, BufferedImage> imageCache; // Cache for product images
    private Map<String, ImageIcon> scaledImageCache; // Cache for scaled product images

    private int refrigeratorTemp; // Temperature of the refrigerator
    private int freezerTemp; // Temperature of the freezer
    private RoundLabel refrigeratorTempLabel; // Label to display refrigerator temperature
    private RoundLabel freezerTempLabel; // Label to display freezer temperature

    private static final String REFRIGETETOR_CSV = "refrigerator\\Refrigeretor.csv";
    private MainMenu mainMenu; // Reference to MainMenu

    // Constructor to initialize the Refrigerator application
    public RefrigeratorApp(MainMenu mainMenu, Map<String, BufferedImage> imageCache, Map<String, ImageIcon> scaledImageCache, Date date) {
        super("Refrigerator", 1100, 1000);
        this.mainMenu = mainMenu; // Initialize the MainMenu reference
        this.imageCache = imageCache; // Initialize the image cache
        this.scaledImageCache = scaledImageCache; // Initialize the scaled image cache
        GlobalVariables.date = date; // Initialize the date

        loadTemperaturesFromCSV(REFRIGETETOR_CSV); // Load temperatures from CSV file

        loadDoorImages("refrigerator\\src\\photos\\refr3.png"); // Load door images

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the default close operation
        frame.setLayout(new BorderLayout()); // Set the layout to BorderLayout

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
            saveProductsToCSV(GlobalVariables.PRODUCT_IN_REFRIGERETOR); // Save the updated product list to CSV
        });
        leftDoorPanel.add(discardExpiredButton); // Add button to the left door panel

        // Create and configure the "Sort by expiration date" button
        JButton sortByExpirationDateButton = createButton("Sort by expiration");
        sortByExpirationDateButton.setBounds(19, 400, 150, 30); // Set position and size of the button
        sortByExpirationDateButton.addActionListener(e -> {
            sortByExpiration(); // Sort products by expiration date
            saveProductsToCSV(GlobalVariables.PRODUCT_IN_REFRIGERETOR); // Save the updated product list to CSV
        });
        leftDoorPanel.add(sortByExpirationDateButton); // Add button to the left door panel

        // Create and configure the "Sort by name" button
        JButton sortByNameButton = createButton("Sort by products");
        sortByNameButton.setBounds(19, 450, 150, 30); // Set position and size of the button
        sortByNameButton.addActionListener(e -> {
            sortByName(); // Sort products by name
            saveProductsToCSV(GlobalVariables.PRODUCT_IN_REFRIGERETOR); // Save the updated product list to CSV
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

        // Add temperature controls for the refrigerator
        JLabel refrigeratorLabel = new JLabel("<html><b><font size='4' color='white'>Refrigerator</font></b></html>");
        refrigeratorLabel.setBounds(19, 650, 150, 30); // Set position and size of the label
        rightDoorPanel.add(refrigeratorLabel); // Add label to the right door panel

        RoundButton fridgeMinusButton = new RoundButton("-");
        fridgeMinusButton.setBounds(19, 680, 50, 50); // Set position and size of the button
        fridgeMinusButton.setFont(new Font("Arial", Font.BOLD, 20));
        fridgeMinusButton.setBackground(Color.GRAY);
        fridgeMinusButton.addActionListener(e -> updateRefrigeratorTemp(-1));
        rightDoorPanel.add(fridgeMinusButton); // Add button to the right door panel

        refrigeratorTempLabel = new RoundLabel(String.valueOf(refrigeratorTemp) + "°");
        refrigeratorTempLabel.setBounds(80, 680, 50, 50); // Set position and size of the label
        refrigeratorTempLabel.setForeground(Color.BLUE);
        refrigeratorTempLabel.setBackground(Color.WHITE); // Set background color to white
        refrigeratorTempLabel.setFont(new Font("Arial", Font.BOLD, 20));
        rightDoorPanel.add(refrigeratorTempLabel); // Add label to the right door panel

        RoundButton fridgePlusButton = new RoundButton("+");
        fridgePlusButton.setBounds(140, 680, 50, 50); // Set position and size of the button
        fridgePlusButton.setFont(new Font("Arial", Font.BOLD, 20));
        fridgePlusButton.setBackground(Color.GRAY);
        fridgePlusButton.addActionListener(e -> updateRefrigeratorTemp(1));
        rightDoorPanel.add(fridgePlusButton); // Add button to the right door panel

        // Add temperature controls for the freezer
        JLabel freezerLabel = new JLabel("<html><b><font size='4' color='white'>Freezer</font></b></html>");
        freezerLabel.setBounds(19, 720, 150, 30); // Set position and size of the label
        rightDoorPanel.add(freezerLabel); // Add label to the right door panel

        RoundButton freezerMinusButton = new RoundButton("-");
        freezerMinusButton.setBounds(19, 750, 50, 50); // Set position and size of the button
        freezerMinusButton.setFont(new Font("Arial", Font.BOLD, 20));
        freezerMinusButton.setBackground(Color.GRAY);
        freezerMinusButton.addActionListener(e -> updateFreezerTemp(-1));
        rightDoorPanel.add(freezerMinusButton); // Add button to the right door panel

        freezerTempLabel = new RoundLabel(String.valueOf(freezerTemp) + "°");
        freezerTempLabel.setBounds(80, 750, 50, 50); // Set position and size of the label
        freezerTempLabel.setForeground(Color.BLUE);
        freezerTempLabel.setBackground(Color.WHITE); // Set background color to white
        freezerTempLabel.setFont(new Font("Arial", Font.BOLD, 20));
        rightDoorPanel.add(freezerTempLabel); // Add label to the right door panel

        RoundButton freezerPlusButton = new RoundButton("+");
        freezerPlusButton.setBounds(140, 750, 50, 50); // Set position and size of the button
        freezerPlusButton.setFont(new Font("Arial", Font.BOLD, 20));
        freezerPlusButton.setBackground(Color.GRAY);
        freezerPlusButton.addActionListener(e -> updateFreezerTemp(1));
        rightDoorPanel.add(freezerPlusButton); // Add button to the right door panel

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
        frame.add(leftDoorPanel, BorderLayout.WEST); // Add left door panel to the west
        frame.add(new JScrollPane(mainPanel), BorderLayout.CENTER); // Add main panel to the center with scroll pane
        frame.add(rightDoorPanel, BorderLayout.EAST); // Add right door panel to the east

        loadImagesToCache(); // Load images into cache

        refreshMainPanel(); // Initial refresh to display products
    }

    @Override
    public void load() {
        // Implement the load method
    }

    @Override
    public void show() {
        refreshMainPanel(); // Refresh the main panel to update the display
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

    // Method to load temperatures from CSV file
    private void loadTemperaturesFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            if ((line = br.readLine()) != null) {
                String[] headers = line.split(",");
                if ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    refrigeratorTemp = Integer.parseInt(values[0]);
                    freezerTemp = Integer.parseInt(values[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to save temperatures to CSV file
    private void saveTemperaturesToCSV(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("refrigeratorTemp,freezerTemp"); // Header
            writer.println(refrigeratorTemp + "," + freezerTemp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to show the search dialog
    private void showSearchDialog() {
        String productName = JOptionPane.showInputDialog(frame, "Enter product name:", "Search Product", JOptionPane.PLAIN_MESSAGE);
        if (productName != null && !productName.trim().isEmpty()) {
            Optional<Product> product = GlobalVariables.RefrigeratorProducts.stream()
                    .filter(p -> p.getName().equalsIgnoreCase(productName.trim()))
                    .findFirst();

            if (product.isPresent()) {
                showProductSelectionDialog(product.get());
            } else {
                JOptionPane.showMessageDialog(frame, "Product not found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Method to return to the main menu
    private void returnToMainMenu() {
        GlobalVariables.guis.get(Application.GUIType.MAIN.ordinal()).show(); // Show the main menu
        frame.setVisible(false); // Hide the refrigerator frame
    }

    // Method to discard expired products based on the current date
    private void discardExpired(Date currentDate) {
        Iterator<Product> iterator = GlobalVariables.RefrigeratorProducts.iterator();
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
        Collections.sort(GlobalVariables.RefrigeratorProducts, Comparator.comparing(Product::getName));
        refreshMainPanel(); // Refresh the main panel to update the display
    }

    // Method to sort products by expiration date
    private void sortByExpiration() {
        for (int i = 0; i < GlobalVariables.RefrigeratorProducts.size(); i++) {
            for (int j = i + 1; j < GlobalVariables.RefrigeratorProducts.size(); j++) {
                Product product1 = GlobalVariables.RefrigeratorProducts.get(i);
                Product product2 = GlobalVariables.RefrigeratorProducts.get(j);
                if (!product1.isAboutToExpired(product2.getExpiration(), 0)) {
                    swap(i, j); // Swap products if they are not in order
                }
            }
        }
        refreshMainPanel(); // Refresh the main panel to update the display
    }

    // Method to swap two products in the list
    private void swap(int i, int j) {
        Product temp = GlobalVariables.RefrigeratorProducts.get(i);
        GlobalVariables.RefrigeratorProducts.set(i, GlobalVariables.RefrigeratorProducts.get(j));
        GlobalVariables.RefrigeratorProducts.set(j, temp);
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
            for (Product product : GlobalVariables.RefrigeratorProducts) {
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

        for (Product product : GlobalVariables.RefrigeratorProducts) {
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
            System.out.println("Checking for product image in cache: " + product.image());
            ImageIcon scaledImageIcon = scaledImageCache.get(product.image());

            if (scaledImageIcon != null) {
                JLabel productImageLabel = new JLabel(scaledImageIcon);
                productPanel.add(productImageLabel, BorderLayout.CENTER);
            } else {
                System.err.println("Scaled image not found in cache for: " + product.image());
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

        // Use the outer class reference to call the method
        productPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showProductSelectionDialog(product);
            }
        });

        return productPanel;
    }

    public void showProductSelectionDialog(Product product) {
        JDialog dialog = new JDialog(frame, "Select Quantity", true);
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
        dialog.setLocationRelativeTo(frame);
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
                GlobalVariables.RefrigeratorProducts.remove(product); // Remove the product from the list if amount is 0
            }
            saveProductsToCSV(GlobalVariables.PRODUCT_IN_REFRIGERETOR); // Save the updated product list to CSV
            refreshMainPanel(); // Refresh the main panel to update the display
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid quantity selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to calculate and display the nutritional summary of all products
    private void displayNutritionalSummary() {
        double totalCalories = 0;
        double totalProtein = 0;
        double totalFat = 0;
        double totalCarbohydrates = 0;

        for (Product product : GlobalVariables.RefrigeratorProducts) {
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

        System.out.println("Main panel refreshed with " + GlobalVariables.RefrigeratorProducts.size() + " products.");
        for (Product product : GlobalVariables.RefrigeratorProducts) {
            System.out.println("Product: " + product.getName() + ", amount: " + product.getAmount() + ", Image: " + product.image());
        }
    }

    // Method to update the refrigerator temperature
    private void updateRefrigeratorTemp(int change) {
        if(refrigeratorTemp==12){
            if(change==1){
                return;
            }
        }if(refrigeratorTemp==1){
            if(change==-1){
                return;
            }
        }
        refrigeratorTemp += change;
        refrigeratorTempLabel.setText(refrigeratorTemp + "°");
        saveTemperaturesToCSV(REFRIGETETOR_CSV); // Save the updated temperatures to CSV
    }

    // Method to update the freezer temperature
    private void updateFreezerTemp(int change) {
        if(freezerTemp==-13){
            if(change==1){
                return;
            }
        }if(freezerTemp==-23){
            if(change==-1){
                return;
            }
        }
        freezerTemp += change;
        freezerTempLabel.setText(freezerTemp + "°");
        saveTemperaturesToCSV(REFRIGETETOR_CSV); // Save the updated temperatures to CSV
    }

    // Method to load images into cache
    private void loadImagesToCache() {
        ExecutorService executor = Executors.newFixedThreadPool(4); // Create a thread pool with 4 threads

        List<Product> productsBatch = new ArrayList<>();
        for (Product product : GlobalVariables.RefrigeratorProducts) {
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

    // Method to add a new product to the CSV file and the main panel
    public void addProduct(Product newProduct) {
    // Add the new product to the global product list
    GlobalVariables.RefrigeratorProducts.add(newProduct);

    // Save the updated product list to CSV
    saveProductsToCSV(GlobalVariables.PRODUCT_IN_REFRIGERETOR);

    // Refresh the main panel to update the display
    refreshMainPanel();
    }


    public static void main(String[] args) {
        // This main method is not needed because the RefrigeratorApp will be launched from MainMenu
    }
}

class RoundButton extends JButton {
    public RoundButton(String label) {
        super(label);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillOval(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getForeground());
        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(50, 50);
    }

    @Override
    public boolean contains(int x, int y) {
        int radius = getWidth() / 2;
        return (x - radius) * (x - radius) + (y - radius) * (y - radius) <= radius * radius;
    }
}

class RoundLabel extends JLabel {
    public RoundLabel(String text) {
        super(text);
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillOval(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getForeground());
        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(50, 50);
    }

    @Override
    public boolean contains(int x, int y) {
        int radius = getWidth() / 2;
        return (x - radius) * (x - radius) + (y - radius) * (y - radius) <= radius * radius;
    }
}
