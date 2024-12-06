import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// Main Class
public class AuctionManagementSystem {
    private JFrame mainFrame;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Bid> bids = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AuctionManagementSystem::new);
    }

    public AuctionManagementSystem() {
        mainFrame = new JFrame("Auction Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createMainMenuPanel(), "MainMenu");
        mainPanel.add(createAdminPanel(), "AdminPanel");
        mainPanel.add(createBidderPanel(), "BidderPanel");

        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
    }

    // Main Menu Panel
    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Auction Management System", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton adminButton = new JButton("Admin Login");
        JButton bidderButton = new JButton("Bidder Login");

        adminButton.addActionListener(e -> cardLayout.show(mainPanel, "AdminPanel"));
        bidderButton.addActionListener(e -> cardLayout.show(mainPanel, "BidderPanel"));

        buttonPanel.add(adminButton);
        buttonPanel.add(bidderButton);

        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    // Admin Panel
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, BorderLayout.NORTH);

        JTextArea itemListArea = new JTextArea(15, 50);
        itemListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(itemListArea);

        JButton addItemButton = new JButton("Add Item");
        JButton backButton = new JButton("Back to Main Menu");

        addItemButton.addActionListener(e -> {
            String itemName = JOptionPane.showInputDialog(panel, "Enter Item Name:");
            if (itemName != null && !itemName.trim().isEmpty()) {
                String startPriceStr = JOptionPane.showInputDialog(panel, "Enter Starting Price:");
                try {
                    double startPrice = Double.parseDouble(startPriceStr);
                    items.add(new Item(itemName, startPrice));
                    JOptionPane.showMessageDialog(panel, "Item added successfully!");
                    updateItemList(itemListArea);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Invalid price format!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addItemButton);
        buttonPanel.add(backButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Bidder Panel
    private JPanel createBidderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Bidder Dashboard", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, BorderLayout.NORTH);

        JTextArea auctionArea = new JTextArea(15, 50);
        auctionArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(auctionArea);

        JButton placeBidButton = new JButton("Place Bid");
        JButton backButton = new JButton("Back to Main Menu");

        placeBidButton.addActionListener(e -> {
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "No items available for bidding!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            StringBuilder itemList = new StringBuilder("Available Items:\n");
            for (int i = 0; i < items.size(); i++) {
                itemList.append(i + 1).append(". ").append(items.get(i)).append("\n");
            }

            String choice = JOptionPane.showInputDialog(panel, itemList + "\nEnter item number to bid on:");
            try {
                int itemIndex = Integer.parseInt(choice) - 1;
                if (itemIndex < 0 || itemIndex >= items.size()) {
                    throw new IndexOutOfBoundsException();
                }

                String bidAmountStr = JOptionPane.showInputDialog(panel, "Enter your bid amount:");
                double bidAmount = Double.parseDouble(bidAmountStr);

                Item selectedItem = items.get(itemIndex);
                if (bidAmount > selectedItem.getHighestBid()) {
                    selectedItem.setHighestBid(bidAmount);
                    bids.add(new Bid(selectedItem.getName(), bidAmount));
                    JOptionPane.showMessageDialog(panel, "Bid placed successfully!");
                    updateAuctionList(auctionArea);
                } else {
                    JOptionPane.showMessageDialog(panel, "Bid must be higher than the current highest bid!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(placeBidButton);
        buttonPanel.add(backButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Helper Methods
    private void updateItemList(JTextArea area) {
        StringBuilder content = new StringBuilder("Items for Auction:\n");
        for (Item item : items) {
            content.append(item).append("\n");
        }
        area.setText(content.toString());
    }

    private void updateAuctionList(JTextArea area) {
        StringBuilder content = new StringBuilder("Current Auctions:\n");
        for (Item item : items) {
            content.append(item).append("\n");
        }
        area.setText(content.toString());
    }

    // Item Class
    static class Item {
        private String name;
        private double startingPrice;
        private double highestBid;

        public Item(String name, double startingPrice) {
            this.name = name;
            this.startingPrice = startingPrice;
            this.highestBid = startingPrice;
        }

        public String getName() {
            return name;
        }

        public double getHighestBid() {
            return highestBid;
        }

        public void setHighestBid(double highestBid) {
            this.highestBid = highestBid;
        }

        @Override
        public String toString() {
            return name + " (Starting Price: $" + startingPrice + ", Current Highest Bid: $" + highestBid + ")";
        }
    }

    // Bid Class
    static class Bid {
        private String itemName;
        private double bidAmount;

        public Bid(String itemName, double bidAmount) {
            this.itemName = itemName;
            this.bidAmount = bidAmount;
        }

        @Override
        public String toString() {
            return "Item: " + itemName + ", Bid: $" + bidAmount;
        }
    }
}