import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.List;
import java.util.*;

// Main class that represents the Refrigerator application
public class Refrigerator extends JFrame {
    private List<Product> products; // List to store products
    private BufferedImage leftDoorImage; // Image for the left door
    private BufferedImage rightDoorImage; // Image for the right door
    private JLabel dateLabel; // Label to display the date
    private JPanel mainPanel; // Main panel to display product information
    private JLabel nutritionalSummaryLabel; // Label to display nutritional summary
    private Date date = new Date(17, 10, 2024); // Set the date to 17/10/2024

    // Constructor to initialize the Refrigerator application
    public Refrigerator() {
        products = new ArrayList<>(); // Initialize the product list
        loadProductsFromCSV("refrigerator\\products.csv"); // Load products from CSV file
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
        discardExpiredButton.addActionListener(e -> discardExpired(date)); // Add action listener
        leftDoorPanel.add(discardExpiredButton); // Add button to the left door panel

        // Create and configure the "Sort by expiration date" button
        JButton sortByExpirationDateButton = createButton("Sort by expiration");
        sortByExpirationDateButton.setBounds(19, 400, 150, 30); // Set position and size of the button
        sortByExpirationDateButton.addActionListener(e -> sortByExpiration()); // Add action listener
        leftDoorPanel.add(sortByExpirationDateButton); // Add button to the left door panel

        // Create and configure the "menu" button
        JButton menuButton = createButton("menu");
        menuButton.setBounds(19, 450, 150, 30); // Set position and size of the button
        leftDoorPanel.add(menuButton); // Add button to the left door panel

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
        dateLabel = new JLabel("<html><b><font size='6' color='white'>" + this.date.getDay() + "/" + this.date.getMonth() + "/" + this.date.getYear() + "</font></b></html>"); // Example date with larger font and bold
        dateLabel.setBounds(53, 50, 150, 40); // Set position and size of the date label
        rightDoorPanel.add(dateLabel); // Add date label to the right door panel

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

        // Add panels to the application window
        add(leftDoorPanel, BorderLayout.WEST); // Add left door panel to the west
        add(new JScrollPane(mainPanel), BorderLayout.CENTER); // Add main panel to the center with scroll pane
        add(rightDoorPanel, BorderLayout.EAST); // Add right door panel to the east

        refreshMainPanel(); // Initial refresh to display products
    }

    // Method to discard expired products based on the current date
    private void discardExpired(Date currentDate) {
        Iterator<Product> iterator = products.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            if (product.isExpired(currentDate)) {
                iterator.remove(); // Remove expired product from the list
            }
        }
        refreshMainPanel(); // Refresh the main panel to update the display
    }

    // Method to sort products by expiration date
    private void sortByExpiration() {
        for (int i = 0; i < products.size(); i++) {
            for (int j = i + 1; j < products.size(); j++) {
                Product product1 = products.get(i);
                Product product2 = products.get(j);
                if (!product1.isAboutToExpired(product2.getExpiration(), 0)) {
                    swap(i, j); // Swap products if they are not in order
                }
            }
        }
        refreshMainPanel(); // Refresh the main panel to update the display
    }

    // Method to swap two products in the list
    private void swap(int i, int j) {
        Product temp = products.get(i);
        products.set(i, products.get(j));
        products.set(j, temp);
    }

    // Method to create a button with the specified text
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        return button;
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

    // Method to load products from a CSV file
    private void loadProductsFromCSV(String filePath) {
        Map<String, NutrientValues> nutrientValuesMap = Product.readNutrientValuesFromFile(); // Read nutrient values from file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // skip the header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String name = values[0];
                String[] dateParts = values[1].split("\\.");
                Integer day = Integer.parseInt(dateParts[0]);
                Integer month = Integer.parseInt(dateParts[1]);
                Integer year = Integer.parseInt(dateParts[2]);
                Date date = new Date(day, month, year);
                double price = Double.parseDouble(values[2]);
                Integer amount = Integer.parseInt(values[3]);

                Product product = new Product(name, nutrientValuesMap.get(name), date, price, amount); // Create a new product
                products.add(product); // Add the product to the list
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

        for (Product product : products) {
            if (product.departments().equals(category)) {
                JPanel productPanel = new JPanel();
                productPanel.setLayout(new BorderLayout());
                productPanel.setBackground(Color.WHITE); // Set background color to white

                // Add product image if available
                if (product.image() != null && !product.image().isEmpty()) {
                    try {
                        BufferedImage image = ImageIO.read(new File(product.image()));
                        Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        JLabel productImageLabel = new JLabel(new ImageIcon(scaledImage));
                        productPanel.add(productImageLabel, BorderLayout.CENTER);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Set product details with appropriate color based on expiration status
                String productDetails = "<html><span style='color:green;'>" + product.getDetails().replace("\n", "<br>") + "</span></html>";
                if (product.isAboutToExpired(date, 2)) {
                    productDetails = "<html><span style='color:orange;'>" + product.getDetails().replace("\n", "<br>") + "</span></html>";
                }
                if (product.isAboutToExpired(date, 0)) {
                    productDetails = "<html><span style='color:red;'>" + product.getDetails().replace("\n", "<br>") + "</span></html>";
                }

                JLabel productDetailsLabel = new JLabel(productDetails);
                productDetailsLabel.setVerticalTextPosition(SwingConstants.TOP);
                productDetailsLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                productPanel.add(productDetailsLabel, BorderLayout.SOUTH);

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

    // Method to calculate and display the nutritional summary of all products
    // Method to calculate and display the nutritional summary of all products
    private void displayNutritionalSummary() {
        double totalCalories = 0;
        double totalProtein = 0;
        double totalFat = 0;
        double totalCarbohydrates = 0;
    
        for (Product product : products) {
            totalCalories += product.getNutrientValues().getCalories();
            totalProtein += product.getNutrientValues().getProtein();
            totalFat += product.getNutrientValues().getFat();
            totalCarbohydrates += product.getNutrientValues().getCarbohydrates();
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

    // Main method to run the Refrigerator application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Refrigerator().setVisible(true); // Create and display the Refrigerator application
            }
        });
    }
}
