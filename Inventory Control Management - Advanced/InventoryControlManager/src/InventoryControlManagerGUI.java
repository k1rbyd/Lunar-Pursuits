import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryControlManagerGUI extends JFrame {
    private JButton adminButton;
    private JButton userButton;
    private JButton customerButton;
    private JButton exitButton;

    private static final String ADMIN_PASSWORD = "Qwerty#80085";

    public InventoryControlManagerGUI() {
        setTitle("Inventory Control Manager");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1));

        adminButton = new JButton("ADMIN");
        userButton = new JButton("USER");
        customerButton = new JButton("CUSTOMER");
        exitButton = new JButton("Exit");

        mainPanel.add(adminButton);
        mainPanel.add(userButton);
        mainPanel.add(customerButton);
        mainPanel.add(exitButton);

        add(mainPanel);

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputPassword = JOptionPane.showInputDialog(
                        null,
                        "Enter the admin password:",
                        "Admin Authentication",
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (inputPassword != null && inputPassword.equals(ADMIN_PASSWORD)) {
                    showAdminMenu();
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Access denied. Incorrect password.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userID = JOptionPane.showInputDialog(
                        null,
                        "Enter your user ID:",
                        "User Login",
                        JOptionPane.INFORMATION_MESSAGE
                );
                String passcode = JOptionPane.showInputDialog(
                        null,
                        "Enter your passcode:",
                        "User Login",
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (authenticateUser(userID, passcode)) {
                    showUserMenu();
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Access denied. Invalid user ID or passcode.",
                            "User",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        customerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                while (true) {
                    Object[] options = {"View Products", "Place Order", "Exit"};

                    int choice = JOptionPane.showOptionDialog(
                            null,
                            "Customer Menu",
                            "Customer",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[2]
                    );

                    switch (choice) {
                        case 0:
                            showProductsForCustomer();
                            break;
                        case 1:
                            placeOrder();
                            break;
                        case 2:
                            return; 
                    }
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    public boolean authenticateUser(String userID, String passcode) {
        try {
            // Connect to the database (replace with your actual database connection details)
            String dbUrl = "jdbc:mysql://localhost:3306/PROJECT1";
            String dbUser = "root";
            String dbPassword = "Qwerty#80085";
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // Query to check if the user credentials are valid
            String query = "SELECT * FROM user WHERE userID = ? AND passcode = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, userID);
            statement.setString(2, passcode);
            ResultSet resultSet = statement.executeQuery();

            // Check if the query returned any results
            if (resultSet.next()) {
                // Authentication successful
                conn.close();
                return true;
            } else {
                // Authentication failed
                conn.close();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database connection errors or other exceptions here
            return false;
        }
    }

    private void showUserMenu() {
        // Create a menu with options for the user
        String[] options = {"Create New Product", "Edit Existing Product", "Delete Existing Product", "Display All Products", "Exit"};

        while (true) {
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "User Menu",
                    "User",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0:
                    createNewProduct();
                    break;
                case 1:
                    editExistingProduct();
                    break;
                case 2:
                    deleteExistingProduct();
                    break;
                case 3:
                    displayAllProducts();
                    break;
                case 4:
                    return;
            }
        }
    }

    private void showAdminMenu() {
        // Create a menu with options for the admin
        String[] options = {"Edit User Database", "Edit Product Database", "Exit"};

        while (true) {
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Admin Menu",
                    "Admin",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0:
                    showUserMenu1();
                    break;
                case 1:
                    showProductMenu();
                    break;
                case 2:
                    return;
            }
        }
    }

    private void showUserMenu1() {
        // Create a menu with options for user database editing
        String[] options = {"Create a new user", "Delete an existing user", "Display all users", "Exit"};

        while (true) {
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "User Database Editing",
                    "Admin",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0:
                    createUser();
                    break;
                case 1:
                    deleteUser();
                    break;
                case 2:
                    displayAllUsers();
                    break;
                case 3:
                    return;
            }
        }
    }

    private void createNewProduct() {
        String productidStr = JOptionPane.showInputDialog("Enter the ID of the new product:");
        String productName = JOptionPane.showInputDialog("Enter the name of the new product:");
        String quantityStr = JOptionPane.showInputDialog("Enter the quantity of the new product:");
        String priceStr = JOptionPane.showInputDialog("Enter the price of the new product:");
        String sellerName = JOptionPane.showInputDialog("Enter the seller's name:");

        try {
        	int productid = Integer.parseInt(productidStr);
            int quantity = Integer.parseInt(quantityStr);
            int price = Integer.parseInt(priceStr);

            // Implement product creation in the database
            String dbUrl = "jdbc:mysql://localhost:3306/PROJECT1";
            String dbUser = "root";
            String dbPassword = "Qwerty#80085";
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            String createProductSQL = "INSERT INTO products(productid,productname, quantity, price, seller_name) VALUES (?,?, ?, ?, ?)";
            PreparedStatement createProductStmt = conn.prepareStatement(createProductSQL);
            createProductStmt.setInt(1, productid);
            createProductStmt.setString(2, productName);
            createProductStmt.setInt(3, quantity);
            createProductStmt.setInt(4, price);
            createProductStmt.setString(5, sellerName);
            createProductStmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "New product added successfully.", "User", JOptionPane.INFORMATION_MESSAGE);

            conn.close();
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Invalid input or database error: " + ex.getMessage(), "User", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editExistingProduct() {
        String productIdStr = JOptionPane.showInputDialog("Enter the product ID of the product you want to edit:");

        try {
            int productId = Integer.parseInt(productIdStr);

            // Retrieve the product details based on the product ID from the database
            String dbUrl = "jdbc:mysql://localhost:3306/PROJECT1";
            String dbUser = "root";
            String dbPassword = "Qwerty#80085";
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            String selectProductSQL = "SELECT * FROM products WHERE productid = ?";
            PreparedStatement selectProductStmt = conn.prepareStatement(selectProductSQL);
            selectProductStmt.setInt(1, productId);
            ResultSet productResultSet = selectProductStmt.executeQuery();

            if (productResultSet.next()) {
                String currentProductName = productResultSet.getString("productname");
                int currentQuantity = productResultSet.getInt("quantity");
                int currentPrice = productResultSet.getInt("price");
                String currentSellerName = productResultSet.getString("seller_name");

                String newProductName = JOptionPane.showInputDialog("Enter the new product name:", currentProductName);
                String newQuantityStr = JOptionPane.showInputDialog("Enter the new quantity:", currentQuantity);
                String newPriceStr = JOptionPane.showInputDialog("Enter the new price:", currentPrice);
                String newSellerName = JOptionPane.showInputDialog("Enter the new seller name:", currentSellerName);

                try {
                    int newQuantity = Integer.parseInt(newQuantityStr);
                    int newPrice = Integer.parseInt(newPriceStr);

                    // Implement updating the product details in the database
                    String updateProductSQL = "UPDATE products SET productname = ?, quantity = ?, price = ?, seller_name = ? WHERE productid = ?";
                    PreparedStatement updateProductStmt = conn.prepareStatement(updateProductSQL);
                    updateProductStmt.setString(1, newProductName);
                    updateProductStmt.setInt(2, newQuantity);
                    updateProductStmt.setInt(3, newPrice);
                    updateProductStmt.setString(4, newSellerName);
                    updateProductStmt.setInt(5, productId);
                    updateProductStmt.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Product updated successfully.", "User", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException | SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input or database error: " + ex.getMessage(), "User", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Product not found.", "User", JOptionPane.ERROR_MESSAGE);
            }

            conn.close();
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Invalid input or database error: " + ex.getMessage(), "User", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteExistingProduct() {
        String productIdStr = JOptionPane.showInputDialog("Enter the product ID of the product you want to delete:");

        try {
            int productId = Integer.parseInt(productIdStr);

            // Implement product deletion in the database
            String dbUrl = "jdbc:mysql://localhost:3306/PROJECT1";
            String dbUser = "root";
            String dbPassword = "Qwerty#80085";
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            String deleteProductSQL = "DELETE FROM products WHERE productid = ?";
            PreparedStatement deleteProductStmt = conn.prepareStatement(deleteProductSQL);
            deleteProductStmt.setInt(1, productId);
            int rowsDeleted = deleteProductStmt.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "Product deleted successfully.", "User", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Product not found.", "User", JOptionPane.ERROR_MESSAGE);
            }

            conn.close();
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Invalid input or database error: " + ex.getMessage(), "User", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayAllProducts() {
        try {
            // Connect to the database
            String dbUrl = "jdbc:mysql://localhost:3306/PROJECT1";
            String dbUser = "root";
            String dbPassword = "Qwerty#80085";
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // Query to retrieve all products
            String query = "SELECT * FROM products";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder productsList = new StringBuilder("List of Products:\n");

            while (resultSet.next()) {
                int productId = resultSet.getInt("productid");
                String productName = resultSet.getString("productname");
                int productQuantity = resultSet.getInt("quantity");
                int productPrice = resultSet.getInt("price");
                String sellerName = resultSet.getString("seller_name");

                productsList.append("Product ID: ").append(productId).append("\n");
                productsList.append("Product Name: ").append(productName).append("\n");
                productsList.append("Quantity: ").append(productQuantity).append("\n");
                productsList.append("Price: ").append(productPrice).append("\n");
                productsList.append("Seller Name: ").append(sellerName).append("\n\n");
            }

            JOptionPane.showMessageDialog(null, productsList.toString(), "Products", JOptionPane.INFORMATION_MESSAGE);

            // Close the database connection
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createUser() {
        String username = JOptionPane.showInputDialog("Enter the username for the new user:");
        String password = JOptionPane.showInputDialog("Enter the password for the new user:");

        try {
            // Connect to the database
            String dbUrl = "jdbc:mysql://localhost:3306/PROJECT1";
            String dbUser = "root";
            String dbPassword = "Qwerty#80085";
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // Check if the username is already taken
            String checkUserSQL = "SELECT * FROM user WHERE userid = ?";
            PreparedStatement checkUserStmt = conn.prepareStatement(checkUserSQL);
            checkUserStmt.setString(1, username);
            ResultSet resultSet = checkUserStmt.executeQuery();

            if (!resultSet.next()) {
                // If the username is not taken, insert the new user into the database
                String insertUserSQL = "INSERT INTO user (userid, passcode) VALUES (?, ?)";
                PreparedStatement insertUserStmt = conn.prepareStatement(insertUserSQL);
                insertUserStmt.setString(1, username);
                insertUserStmt.setString(2, password);
                insertUserStmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "User created successfully.", "Admin", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Username already exists. Please choose another username.", "Admin", JOptionPane.ERROR_MESSAGE);
            }

            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database connection error or SQL error: " + ex.getMessage(), "Admin", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        String userIdToDelete = JOptionPane.showInputDialog("Enter the userid of the user to delete:");

        try {
            // Connect to the database
            String dbUrl = "jdbc:mysql://localhost:3306/PROJECT1";
            String dbUser = "root";
            String dbPassword = "Qwerty#80085";
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // Check if the userid exists
            String checkUserSQL = "SELECT * FROM user WHERE userid = ?";
            PreparedStatement checkUserStmt = conn.prepareStatement(checkUserSQL);
            checkUserStmt.setString(1, userIdToDelete);
            ResultSet resultSet = checkUserStmt.executeQuery();

            if (resultSet.next()) {
                // If the userid exists, delete the user from the database
                String deleteUserSQL = "DELETE FROM user WHERE userid = ?";
                PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserSQL);
                deleteUserStmt.setString(1, userIdToDelete);
                int rowsDeleted = deleteUserStmt.executeUpdate();

                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null, "User deleted successfully.", "Admin", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error deleting user.", "Admin", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "User not found.", "Admin", JOptionPane.ERROR_MESSAGE);
            }

            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database connection error or SQL error: " + ex.getMessage(), "Admin", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayAllUsers() {
        try {
            // Connect to the database
            String dbUrl = "jdbc:mysql://localhost:3306/PROJECT1";
            String dbUser = "root";
            String dbPassword = "Qwerty#80085";
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // Query to retrieve all users
            String query = "SELECT * FROM user";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder userList = new StringBuilder("List of Users:\n");

            while (resultSet.next()) {
                String username = resultSet.getString("userid");
                String passcode = resultSet.getString("passcode");

                // Display both username and passcode
                userList.append("Username: ").append(username).append("\n");
                userList.append("Passcode: ").append(passcode).append("\n");
                userList.append("\n");
            }

            JOptionPane.showMessageDialog(null, userList.toString(), "Users", JOptionPane.INFORMATION_MESSAGE);

            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database connection error or SQL error: " + ex.getMessage(), "Admin", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showProductMenu() {
        // Create a menu with options for product database editing
        String[] options = {"Create a new product", "Edit an existing product", "Delete an existing product", "Display all products", "Exit"};

        while (true) {
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Product Database Editing",
                    "Admin",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0:
                    createProduct();
                    break;
                case 1:
                    editProduct();
                    break;
                case 2:
                    deleteProduct();
                    break;
                case 3:
                    displayAllProducts();
                    break;
                case 4:
                    return;
            }
        }
    }

    private void createProduct() {
        String productidStr = JOptionPane.showInputDialog("Enter the ID of the new product:");
        String productName = JOptionPane.showInputDialog("Enter the name of the new product:");
        String quantityStr = JOptionPane.showInputDialog("Enter the quantity of the new product:");
        String priceStr = JOptionPane.showInputDialog("Enter the price of the new product:");
        String sellerName = JOptionPane.showInputDialog("Enter the seller's name:");

        try {
            int productid = Integer.parseInt(productidStr);
            int quantity = Integer.parseInt(quantityStr);
            int price = Integer.parseInt(priceStr);

            // Implement product creation in the database
            String dbUrl = "jdbc:mysql://localhost:3306/PROJECT1";
            String dbUser = "root";
            String dbPassword = "Qwerty#80085";
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            String createProductSQL = "INSERT INTO products(productid, productname, quantity, price, seller_name) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement createProductStmt = conn.prepareStatement(createProductSQL);
            createProductStmt.setInt(1, productid);
            createProductStmt.setString(2, productName);
            createProductStmt.setInt(3, quantity);
            createProductStmt.setInt(4, price);
            createProductStmt.setString(5, sellerName);
            createProductStmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "New product added successfully.", "Admin", JOptionPane.INFORMATION_MESSAGE);

            conn.close();
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Invalid input or database error: " + ex.getMessage(), "Admin", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editProduct() {
        String productIdStr = JOptionPane.showInputDialog("Enter the product ID of the product you want to edit:");

        try {
            int productId = Integer.parseInt(productIdStr);

            // Retrieve the product details based on the product ID from the database
            String dbUrl = "jdbc:mysql://localhost:3306/PROJECT1";
            String dbUser = "root";
            String dbPassword = "Qwerty#80085";
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            String selectProductSQL = "SELECT * FROM products WHERE productid = ?";
            PreparedStatement selectProductStmt = conn.prepareStatement(selectProductSQL);
            selectProductStmt.setInt(1, productId);
            ResultSet productResultSet = selectProductStmt.executeQuery();

            if (productResultSet.next()) {
                String currentProductName = productResultSet.getString("productname");
                int currentQuantity = productResultSet.getInt("quantity");
                int currentPrice = productResultSet.getInt("price");
                String currentSellerName = productResultSet.getString("seller_name");

                String newProductName = JOptionPane.showInputDialog("Enter the new product name:", currentProductName);
                String newQuantityStr = JOptionPane.showInputDialog("Enter the new quantity:", currentQuantity);
                String newPriceStr = JOptionPane.showInputDialog("Enter the new price:", currentPrice);
                String newSellerName = JOptionPane.showInputDialog("Enter the new seller name:", currentSellerName);

                try {
                    int newQuantity = Integer.parseInt(newQuantityStr);
                    int newPrice = Integer.parseInt(newPriceStr);

                    // Implement updating the product details in the database
                    String updateProductSQL = "UPDATE products SET productname = ?, quantity = ?, price = ?, seller_name = ? WHERE productid = ?";
                    PreparedStatement updateProductStmt = conn.prepareStatement(updateProductSQL);
                    updateProductStmt.setString(1, newProductName);
                    updateProductStmt.setInt(2, newQuantity);
                    updateProductStmt.setInt(3, newPrice);
                    updateProductStmt.setString(4, newSellerName);
                    updateProductStmt.setInt(5, productId);
                    updateProductStmt.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Product updated successfully.", "Admin", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException | SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input or database error: " + ex.getMessage(), "Admin", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Product not found.", "Admin", JOptionPane.ERROR_MESSAGE);
            }

            conn.close();
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Invalid input or database error: " + ex.getMessage(), "Admin", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        String productIdStr = JOptionPane.showInputDialog("Enter the product ID of the product you want to delete:");

        try {
            int productId = Integer.parseInt(productIdStr);

            // Implement product deletion in the database
            String dbUrl = "jdbc:mysql://localhost:3306/PROJECT1";
            String dbUser = "root";
            String dbPassword = "Qwerty#80085";
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            String deleteProductSQL = "DELETE FROM products WHERE productid = ?";
            PreparedStatement deleteProductStmt = conn.prepareStatement(deleteProductSQL);
            deleteProductStmt.setInt(1, productId);
            int rowsDeleted = deleteProductStmt.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "Product deleted successfully.", "Admin", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Product not found.", "Admin", JOptionPane.ERROR_MESSAGE);
            }

            conn.close();
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Invalid input or database error: " + ex.getMessage(), "Admin", JOptionPane.ERROR_MESSAGE);
        }
    } 

    private void showProductsForCustomer() {
        try {
            // Connect to the database
            String dbUrl = "jdbc:mysql://localhost:3306/PROJECT1";
            String dbUser = "root";
            String dbPassword = "Qwerty#80085";
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // Query to retrieve all products
            String query = "SELECT * FROM products";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder productsList = new StringBuilder("List of Products:\n");

            while (resultSet.next()) {
                int productId = resultSet.getInt("productid");
                String productName = resultSet.getString("productname");
                int productQuantity = resultSet.getInt("quantity");
                int productPrice = resultSet.getInt("price");
                String sellerName = resultSet.getString("seller_name");

                productsList.append("Product ID: ").append(productId).append("\n");
                productsList.append("Product Name: ").append(productName).append("\n");
                productsList.append("Quantity: ").append(productQuantity).append("\n");
                productsList.append("Price: ").append(productPrice).append("\n");
                productsList.append("Seller Name: ").append(sellerName).append("\n\n");
            }

            JOptionPane.showMessageDialog(null, productsList.toString(), "Products", JOptionPane.INFORMATION_MESSAGE);

            // Close the database connection
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void placeOrder() {
        String productIdStr = JOptionPane.showInputDialog("Enter the product ID of the product you want to order:");
        String quantityStr = JOptionPane.showInputDialog("Enter the quantity you want to order:");

        try {
            int productId = Integer.parseInt(productIdStr);
            int quantity = Integer.parseInt(quantityStr);

            // Implement order placement logic here
            String dbUrl = "jdbc:mysql://localhost:3306/PROJECT1";
            String dbUser = "root";
            String dbPassword = "Qwerty#80085";
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // Check if the product exists
            String selectProductSQL = "SELECT * FROM products WHERE productid = ?";
            PreparedStatement selectProductStmt = conn.prepareStatement(selectProductSQL);
            selectProductStmt.setInt(1, productId);
            ResultSet productResultSet = selectProductStmt.executeQuery();

            if (productResultSet.next()) {
                int availableQuantity = productResultSet.getInt("quantity");

                if (quantity <= availableQuantity) {
                    // Update the quantity in the database
                    int newQuantity = availableQuantity - quantity;
                    String updateQuantitySQL = "UPDATE products SET quantity = ? WHERE productid = ?";
                    PreparedStatement updateQuantityStmt = conn.prepareStatement(updateQuantitySQL);
                    updateQuantityStmt.setInt(1, newQuantity);
                    updateQuantityStmt.setInt(2, productId);
                    updateQuantityStmt.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Your transaction is successful. Please take your receipt and pay at the cashier.", "Customer", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "We apologize for any inconvenience, but we are currently out of stock of that item.", "Customer", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Product not found.", "Customer", JOptionPane.ERROR_MESSAGE);
            }

            conn.close();
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Invalid input or database error: " + ex.getMessage(), "Customer", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new InventoryControlManagerGUI().setVisible(true);
            }
        });
    }
}
