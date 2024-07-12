import com.sun.java.accessibility.util.GUIInitializedListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ShoppingCartGUI extends GenericGUI {
    private List<Product> selectedProducts;
    private List<Integer> quantities;
    private List<JPanel> productSubPanels;
    private JPanel mainPanel;
    private JTextField searchField;
    private JComboBox<String> searchBox;
    private JLabel moneyLabel;

    public ShoppingCartGUI(String frameName, int frameWidth, int frameHeight) {
        super(frameName, frameWidth, frameHeight);
        this.selectedProducts = new ArrayList<>();
        this.quantities = new ArrayList<>();
        this.productSubPanels = new ArrayList<>();

    }

    @Override
    public void load() {
        
        // Load initial products from the global list
        System.out.println("Loading initial products...");
        loadListFromCSV(GlobalVariables.SHOP_LIST);
        // for (int i = 0; i < 3; i++) {
        //     selectedProducts.add(GlobalVariables.allproducts.get(i));
        //     quantities.add(i + 1); // Quantities 1, 2, 3
        // }

        // Create main panel with fixed height for subpanels and scrolling
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // Create product panels for each selected product
        for (int i = 0; i < selectedProducts.size(); i++) {
            JPanel productPanel = createProductPanel(selectedProducts.get(i), quantities.get(i));
            productSubPanels.add(productPanel); // Store each product panel in the list
            mainPanel.add(productPanel);
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Create search field with dropdown
        searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateSearchBox(searchField.getText());
            }
        });

        searchBox = new JComboBox<>();
        updateSearchBox(""); // Initialize with all products
        System.out.println("Loading ...");

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProductToList();
            }
        });

       

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.add(searchField);
        searchPanel.add(searchBox);
        searchPanel.add(addButton);

        // Create subpanel
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        subPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JButton buyAllButton = new JButton("Buy All");
        JButton cleanButton = new JButton("Clean");
        moneyLabel = new JLabel("$$$$$$");

        // Add action listener to return to MainMenu
        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> returnToMainMenu());
        
         // Add action listener to clean button
        cleanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cleanShoppingCart();
            }
        });

         // Add action listener to buy all button
        buyAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buyAllProducts();
            }
        });

        subPanel.add(buyAllButton);
        subPanel.add(cleanButton);
        subPanel.add(moneyLabel);
        subPanel.add(backButton);

        // Add components to frame
        frame.getContentPane().add(searchPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(subPanel, BorderLayout.SOUTH);

        // Update total price initially
        updateTotalPrice();
    }

    private void cleanCSV(String csvFilePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath))) {
            bw.write("Product Name,Quantity");
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addProductToCSV(String csvFilePath, String productName, int quantity) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath, true))) {
            bw.write(productName + "," + quantity);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateCSV(String csvFilePath, String productName, int newQuantity) {
        List<String[]> csvData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                csvData.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath))) {
            for (String[] row : csvData) {
                if (row[0].trim().equalsIgnoreCase(productName)) {
                    if (newQuantity > 0) {
                        row[1] = String.valueOf(newQuantity);
                    } else {
                        continue; // Skip writing this row to effectively delete it
                    }
                }
                bw.write(String.join(",", row));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListFromCSV(String csvFilePath) {
        List<String[]> csvData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Skip the header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                csvData.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String[] values : csvData) {
            String productName = values[0].trim();
            int quantity = Integer.parseInt(values[1].trim());
            Optional<Product> productOpt = GlobalVariables.allproducts.stream()
                .filter(p -> p.getName().equalsIgnoreCase(productName))
                .findFirst();
            if (productOpt.isPresent()) {
                selectedProducts.add(productOpt.get());
                quantities.add(quantity);
            } else {
                System.err.println("Product not found in global list: " + productName);
            }
        }
    }
    
    @Override
    public void show() {
        System.out.println("Showing ShoppingCartGUI...");
        frame.setVisible(true);
    }

    @Override
    public void close() {
        frame.dispose();
    }

    @Override
    public void save() {
        // Save functionality to be implemented later
    }

    private JPanel createProductPanel(Product product, int quantity) {
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BorderLayout());
        productPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        productPanel.setPreferredSize(new Dimension(0, 50)); // Fixed height, variable width
        productPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        productPanel.setMinimumSize(new Dimension(0, 50));

        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel(product.getName());
        JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));
        JLabel quantityLabel = new JLabel(String.valueOf(quantity));

        // Load and resize product image
        ImageIcon productImage = new ImageIcon(product.getImgPath());
        Image image = productImage.getImage();
        Image resizedImage = image.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(resizedImage));

        detailsPanel.add(imageLabel);
        detailsPanel.add(nameLabel);
        detailsPanel.add(priceLabel);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton plusButton = new JButton("+");
        JButton minusButton = new JButton("-");

        // Plus button action listener
        plusButton.addActionListener(e -> {
            int qty = Integer.parseInt(quantityLabel.getText());
            quantityLabel.setText(String.valueOf(++qty));
            int index = selectedProducts.indexOf(product);
            quantities.set(index, qty);
            updateTotalPrice();
            updateCSV(GlobalVariables.SHOP_LIST, product.getName(), qty);
        });

        // Minus button action listener
        minusButton.addActionListener(e -> {
            int qty = Integer.parseInt(quantityLabel.getText());
            if (qty > 0) {
                quantityLabel.setText(String.valueOf(--qty));
                int index = selectedProducts.indexOf(product);
                quantities.set(index, qty);
                if (qty == 0) {
                    selectedProducts.remove(index);
                    quantities.remove(index);
                    productSubPanels.remove(index);
                    mainPanel.remove(productPanel);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                }
                updateCSV(GlobalVariables.SHOP_LIST, product.getName(), qty);
                updateTotalPrice();
            }
        });

        controlPanel.add(quantityLabel);
        controlPanel.add(plusButton);
        controlPanel.add(minusButton);

        productPanel.add(detailsPanel, BorderLayout.CENTER);
        productPanel.add(controlPanel, BorderLayout.EAST);

        return productPanel;
    }

    private void updateSearchBox(String text) {
        searchBox.removeAllItems();
        List<String> filteredProducts = GlobalVariables.allproducts.stream()
                .map(Product::getName)
                .filter(name -> name.toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
        for (String name : filteredProducts) {
            searchBox.addItem(name);
        }
    }

    private void addProductToList() {
        String selectedProduct = (String) searchBox.getSelectedItem();
        if (selectedProduct != null) {
            Product product = GlobalVariables.allproducts.stream()
                    .filter(p -> p.getName().equals(selectedProduct))
                    .findFirst()
                    .orElse(null);
            if (product != null) {
                int index = selectedProducts.indexOf(product);
                if (index >= 0) {
                    // Product already in the list, increase the quantity
                    int currentQuantity = quantities.get(index);
                    quantities.set(index, currentQuantity + 1);
                    JPanel productPanel = productSubPanels.get(index);
                    JLabel quantityLabel = (JLabel) ((JPanel) productPanel.getComponent(1)).getComponent(0);
                    quantityLabel.setText(String.valueOf(currentQuantity + 1));
                    updateCSV(GlobalVariables.SHOP_LIST, product.getName(), currentQuantity + 1);
                } else {
                    // Product not in the list, add new panel and update CSV
                    selectedProducts.add(product);
                    quantities.add(1);
                    JPanel productPanel = createProductPanel(product, 1);
                    productSubPanels.add(productPanel);
                    mainPanel.add(productPanel);
                    addProductToCSV(GlobalVariables.SHOP_LIST, product.getName(), 1);
                }
                mainPanel.revalidate();
                mainPanel.repaint();
                updateTotalPrice();
            }
        }
    }

    private void cleanShoppingCart() {
        // Clear arrays
        selectedProducts.clear();
        quantities.clear();
        productSubPanels.clear();
        mainPanel.removeAll();
        mainPanel.revalidate();
        mainPanel.repaint();

        // Clean the CSV file
        cleanCSV(GlobalVariables.SHOP_LIST);

        // Update total price
        updateTotalPrice();
    }

    private void buyAllProducts() {
        for (int i = 0; i < selectedProducts.size(); i++) {
            Product product = selectedProducts.get(i);
            int quantity = quantities.get(i);
            for (int j = 0; j < quantity; j++) {
                Product pr = new Product(product.getName(),product.getNutrientValues(), GlobalVariables.date.addDays(product.getvalid_Days()),product.getQuantity_sold(), product.getMeasurementUnit(), product.getImgPath(), product.getCategory());
                GlobalVariables.RefrigeratorProducts.add(pr);
            }
        }
        ((RefrigeratorApp) GlobalVariables.guis.get(AppInterface.GUIType.REFRIGERATOR.ordinal())).saveProductsToCSV(GlobalVariables.PRODUCT_IN_REFRIGERETOR);
        ((RefrigeratorApp) GlobalVariables.guis.get(AppInterface.GUIType.REFRIGERATOR.ordinal())).refreshMainPanel();
        
        // After buying all, clear the shopping cart
        cleanShoppingCart();
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (int i = 0; i < selectedProducts.size(); i++) {
            totalPrice += selectedProducts.get(i).getPrice() * quantities.get(i);
        }
        moneyLabel.setText(String.format("$%.2f", totalPrice));
    }

    // Method to return to the main menu
    private void returnToMainMenu() {
        System.out.println("Returning to MainMenu...");
        GlobalVariables.guis.get(AppInterface.GUIType.MAIN.ordinal()).show();
        frame.setVisible(false);
    }

    public static void main(String[] args) {
        ShoppingCartGUI gui = new ShoppingCartGUI("Shopping Cart", 400, 600); // Pass null for MainMenu in standalone mode
        gui.load();
        gui.show();

    }
}
