import java.sql.*;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class InventoryControlManager {
    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Scanner scanner = new Scanner(System.in);

        final String HOST = "localhost";
        final String USER = "root";
        final String PASS = "Qwerty#80085";
        String query;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + HOST + ":3306/PROJECT1", USER, PASS);

            boolean programRunning = true;
            boolean access = false;
            boolean accessR = false;
            boolean accessC = false;

            while (programRunning) {
                System.out.println("Are you ADMIN, USER, or CUSTOMER?");
                System.out.println("[1] : ADMIN");
                System.out.println("[2] : USER");
                System.out.println("[3] : CUSTOMER");
                System.out.println("[4] : Exit");
                System.out.print("Your Response: ");
                int response0 = scanner.nextInt();
                scanner.nextLine();

                if (response0 == 1) {
                    System.out.print("Enter the root password sir: ");
                    String rootPassword = scanner.nextLine();

                    if (rootPassword.equals(PASS)) {
                        accessR = true;
                        System.out.println("Access granted as ROOT.\n");
                    } else {
                        System.out.println("Access denied. Incorrect root password.");
                        return;
                    }
                } else if (response0 == 2) {
                    System.out.print("Enter your user ID: ");
                    String username = scanner.nextLine();
                    
                    System.out.print("Enter your password: "); // Ask for the user's password
                    String password = scanner.nextLine();

                    query = "SELECT userid, passcode FROM user WHERE userid = ? AND passcode = ?";
                    stmt = conn.prepareStatement(query);
                    stmt.setString(1, username);
                    stmt.setString(2, password); // Check both username and password

                    rs = stmt.executeQuery();

                    if (rs.next()) {
                        access = true;
                        System.out.println("Access granted as USER.\n");
                    } else {
                        access = false;
                        System.out.println("Access denied.");
                        return;
                    }
                }
                 else if (response0 == 3) {
                    accessC = true;
                    System.out.println("Access granted as CUSTOMER.\n");
                } else if (response0 == 4) {
                    programRunning = false;
                }

                if (accessR) {
                    while (programRunning) {
                        System.out.println("Admin Menu:");
                        System.out.println("[1] : Edit User Database");
                        System.out.println("[2] : Edit Product Database");
                        System.out.println("[3] : Exit");
                        System.out.print("Your choice sir: ");
                        int choice1R = scanner.nextInt();
                        scanner.nextLine();

                        if (choice1R == 1) {
                            boolean runR = true;
                            while (runR) {
                                System.out.println("What do you want to do in the User Database Sir:");
                                System.out.println("[1] : Create a new user");
                                System.out.println("[2] : Delete an existing user");
                                System.out.println("[3] : Display all the users");
                                System.out.println("[4] : Exit");
                                System.out.print("Your Choice Sir: ");
                                int choice2R = scanner.nextInt();
                                scanner.nextLine();

                                if (choice2R == 1) {
                                    System.out.print("Enter the new user ID: ");
                                    String newUsername = scanner.nextLine();
                                    System.out.print("Enter the new password: ");
                                    String newPassword = scanner.nextLine();

                                    query = "INSERT INTO user(userid, passcode) VALUES (?, ?)";
                                    stmt = conn.prepareStatement(query);
                                    stmt.setString(1, newUsername);
                                    stmt.setString(2, newPassword);

                                    try {
                                        stmt.executeUpdate();
                                        System.out.println("New user added successfully.\n");
                                    } catch (SQLException e) {
                                        System.out.println("ERROR: " + e.getMessage());
                                    }
                                }

                                if (choice2R == 2) {
                                    System.out.print("Enter the user ID of the user you want to delete: ");
                                    String deleteUser = scanner.nextLine();

                                    query = "DELETE FROM user WHERE userid = ?";
                                    stmt = conn.prepareStatement(query);
                                    stmt.setString(1, deleteUser);

                                    try {
                                        stmt.executeUpdate();
                                        System.out.println("User deleted successfully.\n");
                                    } catch (SQLException e) {
                                        System.out.println("ERROR: " + e.getMessage());
                                    }
                                }

                                if (choice2R == 3) {
                                    System.out.println("List of all users:");
                                    query = "SELECT * FROM user";
                                    stmt = conn.prepareStatement(query);
                                    rs = stmt.executeQuery();

                                    while (rs.next()) {
                                        String userId = rs.getString("userid");
                                        String passcode = rs.getString("passcode");
                                        System.out.println("UserID: " + userId + " | Passcode: " + passcode);
                                    }
                                    System.out.println();
                                }

                                if (choice2R == 4) {
                                    runR = false;
                                }
                            }
                        }

                        if (choice1R == 2) {
                            boolean runR = true;
                            while (runR) {
                                System.out.println("What do you want to do in the Product Database Sir:");
                                System.out.println("[1] : Create a new product");
                                System.out.println("[2] : Edit an existing product");
                                System.out.println("[3] : Delete an existing product");
                                System.out.println("[4] : Display all the products");
                                System.out.println("[5] : Exit");
                                System.out.print("Your Choice Sir: ");
                                int choice3R = scanner.nextInt();
                                scanner.nextLine();

                                if (choice3R == 1) {
                                    System.out.print("Enter the product ID: ");
                                    int productId = scanner.nextInt();
                                    scanner.nextLine();
                                    System.out.print("Enter the product name: ");
                                    String productName = scanner.nextLine();
                                    System.out.print("Enter the quantity: ");
                                    int productQuantity = scanner.nextInt();
                                    scanner.nextLine();
                                    System.out.print("Enter the price: ");
                                    int productPrice = scanner.nextInt();
                                    scanner.nextLine();
                                    System.out.print("Enter the seller name: ");
                                    String sellerName = scanner.nextLine();

                                    productName = productName.toUpperCase();
                                    sellerName = sellerName.toUpperCase();

                                    query = "INSERT INTO products(productid, productname, quantity, price, seller_name) VALUES (?, ?, ?, ?, ?)";
                                    stmt = conn.prepareStatement(query);
                                    stmt.setInt(1, productId);
                                    stmt.setString(2, productName);
                                    stmt.setInt(3, productQuantity);
                                    stmt.setInt(4, productPrice);
                                    stmt.setString(5, sellerName);

                                    try {
                                        stmt.executeUpdate();
                                        System.out.println("New product added successfully.\n");
                                    } catch (SQLException e) {
                                        System.out.println("ERROR: " + e.getMessage());
                                    }
                                }

                                if (choice3R == 2) {
                                    System.out.print("Enter the product ID of the product you want to edit: ");
                                    int productId = scanner.nextInt();
                                    scanner.nextLine();
                                    System.out.print("Enter the new product name: ");
                                    String productName = scanner.nextLine();
                                    System.out.print("Enter the new quantity: ");
                                    int productQuantity = scanner.nextInt();
                                    scanner.nextLine();
                                    System.out.print("Enter the new price: ");
                                    int productPrice = scanner.nextInt();
                                    scanner.nextLine();
                                    System.out.print("Enter the new seller name: ");
                                    String sellerName = scanner.nextLine();

                                    productName = productName.toUpperCase();
                                    sellerName = sellerName.toUpperCase();

                                    query = "UPDATE products SET productname = ?, quantity = ?, price = ?, seller_name = ? WHERE productid = ?";
                                    stmt = conn.prepareStatement(query);
                                    stmt.setString(1, productName);
                                    stmt.setInt(2, productQuantity);
                                    stmt.setInt(3, productPrice);
                                    stmt.setString(4, sellerName);
                                    stmt.setInt(5, productId);

                                    try {
                                        stmt.executeUpdate();
                                        System.out.println("Product updated successfully.\n");
                                    } catch (SQLException e) {
                                        System.out.println("ERROR: " + e.getMessage());
                                    }
                                }

                                if (choice3R == 3) {
                                    System.out.print("Enter the product ID of the product you want to delete: ");
                                    int productId = scanner.nextInt();
                                    scanner.nextLine();

                                    query = "DELETE FROM products WHERE productid = ?";
                                    stmt = conn.prepareStatement(query);
                                    stmt.setInt(1, productId);

                                    try {
                                        stmt.executeUpdate();
                                        System.out.println("Product deleted successfully.\n");
                                    } catch (SQLException e) {
                                        System.out.println("ERROR: " + e.getMessage());
                                    }
                                }

                                if (choice3R == 4) {
                                    System.out.println("List of all products:");
                                    query = "SELECT * FROM products";
                                    stmt = conn.prepareStatement(query);
                                    rs = stmt.executeQuery();

                                    while (rs.next()) {
                                        int productId = rs.getInt("productid");
                                        String productName = rs.getString("productname");
                                        int productQuantity = rs.getInt("quantity");
                                        int productPrice = rs.getInt("price");
                                        String sellerName = rs.getString("seller_name");

                                        System.out.println("Product ID: " + productId);
                                        System.out.println("Product Name: " + productName);
                                        System.out.println("Quantity: " + productQuantity);
                                        System.out.println("Price: " + productPrice);
                                        System.out.println("Seller Name: " + sellerName);
                                        System.out.println();
                                    }
                                }

                                if (choice3R == 5) {
                                    runR = false;
                                }
                            }
                        }

                        if (choice1R == 3) {
                            programRunning = false;
                        }
                    }
                }


                if (access || accessR) {
                    while (programRunning) {
                        System.out.println("What do you want to do in the Product Database Sir:");
                        System.out.println("[1] : Create a new product");
                        System.out.println("[2] : Edit an existing product");
                        System.out.println("[3] : Delete an existing product");
                        System.out.println("[4] : Display all the products");
                        System.out.println("[5] : Exit");
                        System.out.print("Your Choice Sir: ");
                        int choice3R = scanner.nextInt();
                        scanner.nextLine();

                        if (choice3R == 1) {
                            System.out.print("Enter the product ID: ");
                            int productId = scanner.nextInt();
                            scanner.nextLine();
                            System.out.print("Enter the product name: ");
                            String productName = scanner.nextLine();
                            System.out.print("Enter the quantity: ");
                            int productQuantity = scanner.nextInt();
                            scanner.nextLine();
                            System.out.print("Enter the price: ");
                            int productPrice = scanner.nextInt();
                            scanner.nextLine();
                            System.out.print("Enter the seller name: ");
                            String sellerName = scanner.nextLine();

                            productName = productName.toUpperCase();
                            sellerName = sellerName.toUpperCase();

                            query = "INSERT INTO products(productid, productname, quantity, price, seller_name) VALUES (?, ?, ?, ?, ?)";
                            stmt = conn.prepareStatement(query);
                            stmt.setInt(1, productId);
                            stmt.setString(2, productName);
                            stmt.setInt(3, productQuantity);
                            stmt.setInt(4, productPrice);
                            stmt.setString(5, sellerName);

                            try {
                                stmt.executeUpdate();
                                System.out.println("New product added successfully.\n");
                            } catch (SQLException e) {
                                System.out.println("ERROR: " + e.getMessage());
                            }
                        }

                        if (choice3R == 2) {
                            System.out.print("Enter the product ID of the product you want to edit: ");
                            int productId = scanner.nextInt();
                            scanner.nextLine();
                            System.out.print("Enter the new product name: ");
                            String productName = scanner.nextLine();
                            System.out.print("Enter the new quantity: ");
                            int productQuantity = scanner.nextInt();
                            scanner.nextLine();
                            System.out.print("Enter the new price: ");
                            int productPrice = scanner.nextInt();
                            scanner.nextLine();
                            System.out.print("Enter the new seller name: ");
                            String sellerName = scanner.nextLine();

                            productName = productName.toUpperCase();
                            sellerName = sellerName.toUpperCase();

                            query = "UPDATE products SET productname = ?, quantity = ?, price = ?, seller_name = ? WHERE productid = ?";
                            stmt = conn.prepareStatement(query);
                            stmt.setString(1, productName);
                            stmt.setInt(2, productQuantity);
                            stmt.setInt(3, productPrice);
                            stmt.setString(4, sellerName);
                            stmt.setInt(5, productId);

                            try {
                                stmt.executeUpdate();
                                System.out.println("Product updated successfully.\n");
                            } catch (SQLException e) {
                                System.out.println("ERROR: " + e.getMessage());
                            }
                        }

                        if (choice3R == 3) {
                            System.out.print("Enter the product ID of the product you want to delete: ");
                            int productId = scanner.nextInt();
                            scanner.nextLine();

                            query = "DELETE FROM products WHERE productid = ?";
                            stmt = conn.prepareStatement(query);
                            stmt.setInt(1, productId);

                            try {
                                stmt.executeUpdate();
                                System.out.println("Product deleted successfully.\n");
                            } catch (SQLException e) {
                                System.out.println("ERROR: " + e.getMessage());
                            }
                        }

                        if (choice3R == 4) {
                            System.out.println("List of all products:");
                            query = "SELECT * FROM products";
                            stmt = conn.prepareStatement(query);
                            rs = stmt.executeQuery();

                            while (rs.next()) {
                                int productId = rs.getInt("productid");
                                String productName = rs.getString("productname");
                                int productQuantity = rs.getInt("quantity");
                                int productPrice = rs.getInt("price");
                                String sellerName = rs.getString("seller_name");

                                System.out.println("Product ID: " + productId);
                                System.out.println("Product Name: " + productName);
                                System.out.println("Quantity: " + productQuantity);
                                System.out.println("Price: " + productPrice);
                                System.out.println("Seller Name: " + sellerName);
                                System.out.println();
                            }
                        }

                        if (choice3R == 5) {
                        	programRunning = false;
                        }
                    }
                }


                if (accessC) {
                    while (programRunning) {
                        System.out.println("Customer Menu:");
                        System.out.println("[1] List all products");
                        System.out.println("[2] Search for a product");
                        System.out.println("[3] Buy a product");
                        System.out.println("[4] Exit");
                        System.out.print("Your choice: ");
                        int choice1 = scanner.nextInt();
                        scanner.nextLine();

                        if (choice1 == 1) {
                            // List all products
                            query = "SELECT * FROM products";
                            stmt = conn.prepareStatement(query);
                            rs = stmt.executeQuery();

                            while (rs.next()) {
                                int productId = rs.getInt("productid");
                                String productName = rs.getString("productname");
                                int productQuantity = rs.getInt("quantity");
                                int productPrice = rs.getInt("price");
                                String sellerName = rs.getString("seller_name");

                                System.out.println("Product ID: " + productId);
                                System.out.println("Product Name: " + productName);
                                System.out.println("Quantity: " + productQuantity);
                                System.out.println("Price: " + productPrice);
                                System.out.println("Seller Name: " + sellerName);
                                System.out.println();
                            }
                        } else if (choice1 == 2) {
                            System.out.print("Enter the product name to search for: ");
                            String searchName = scanner.nextLine();

                            query = "SELECT * FROM products WHERE productname = ?";
                            stmt = conn.prepareStatement(query);
                            stmt.setString(1, searchName);
                            rs = stmt.executeQuery();

                            while (rs.next()) {
                                int productId = rs.getInt("productid");
                                String productName = rs.getString("productname");
                                int productQuantity = rs.getInt("quantity");
                                int productPrice = rs.getInt("price");
                                String sellerName = rs.getString("seller_name");

                                System.out.println("Product ID: " + productId);
                                System.out.println("Product Name: " + productName);
                                System.out.println("Quantity: " + productQuantity);
                                System.out.println("Price: " + productPrice);
                                System.out.println("Seller Name: " + sellerName);
                                System.out.println();
                            }
                        } else if (choice1 == 3) if (choice1 == 3) {
                            System.out.print("Enter the product ID to buy: ");
                            int productId = scanner.nextInt();
                            scanner.nextLine();
                            System.out.print("Enter the quantity to buy: ");
                            int quantityToBuy = scanner.nextInt();
                            scanner.nextLine();

                            // Check if there's enough quantity to buy
                            query = "SELECT quantity FROM products WHERE productid = ?";
                            stmt = conn.prepareStatement(query);
                            stmt.setInt(1, productId);
                            rs = stmt.executeQuery();

                            int availableQuantity = 0;

                            if (rs.next()) {
                                availableQuantity = rs.getInt("quantity");
                            }

                            if (quantityToBuy <= availableQuantity) {
                                // Perform the purchase
                                int newQuantity = availableQuantity - quantityToBuy;

                                // Update the product quantity in the database
                                query = "UPDATE products SET quantity = ? WHERE productid = ?";
                                stmt = conn.prepareStatement(query);
                                stmt.setInt(1, newQuantity);
                                stmt.setInt(2, productId);
                                stmt.executeUpdate();

                                // You can implement other necessary actions here, such as updating user orders or calculating the total cost.

                                System.out.println("We appreciate your business. Please take your bill to the cashier to complete your purchase.");
                            } else {
                                System.out.println("We apologize for any inconvenience, but we are currently out of stock of that item.");
                            }
                        } else if (choice1 == 4) {
                            programRunning = false;
                        }
                    }
                }

            }
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                if (rs != null) rs.close();
                scanner.close();
            } catch (SQLException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }

        System.out.println("Goodbye!");
    }
}
