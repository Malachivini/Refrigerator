import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
    
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Shopping Cart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        
        // Main panel with scrolling
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // Sample product panel creation
        for (int i = 0; i < 15; i++) {
            mainPanel.add(createProductPanel("Product " + (i + 1), i + 1));
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Subpanel
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        subPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JButton buyAllButton = new JButton("Buy All");
        JButton cleanButton = new JButton("Clean");
        JLabel moneyLabel = new JLabel("$$$$$$");

        subPanel.add(buyAllButton);
        subPanel.add(cleanButton);
        subPanel.add(moneyLabel);

        // Add panels to frame
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(subPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static JPanel createProductPanel(String productName, int quantity) {
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        productPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        productPanel.setPreferredSize(new Dimension(350, 50));  // Fixed size for product panel

        JLabel imageLabel = new JLabel("Image");
        JLabel nameLabel = new JLabel(productName);
        JLabel quantityLabel = new JLabel(String.valueOf(quantity));
        JButton plusButton = new JButton("+");
        JButton minusButton = new JButton("-");

        // Plus button action listener
        plusButton.addActionListener(e -> {
            int qty = Integer.parseInt(quantityLabel.getText());
            quantityLabel.setText(String.valueOf(++qty));
        });

        // Minus button action listener
        minusButton.addActionListener(e -> {
            int qty = Integer.parseInt(quantityLabel.getText());
            if (qty > 0) {
                quantityLabel.setText(String.valueOf(--qty));
            }
        });

        productPanel.add(imageLabel);
        productPanel.add(nameLabel);
        productPanel.add(quantityLabel);
        productPanel.add(plusButton);
        productPanel.add(minusButton);

        return productPanel;
    }

}
