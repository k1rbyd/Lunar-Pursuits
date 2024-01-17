import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MSC {
    private static final String HOST = "localhost";
    private static final String USER = "root";
    private static final String PASS = "Qwerty#80085";
    private static final String DATABASE_NAME = "PROJECT2";

    private static final String[] MARKETS = { "market1", "market2", "market3" };
    private static final int PRICE_PER_UNIT_BUY = 10;
    private static final int PRICE_PER_UNIT_SELL = 8;

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            Scanner scanner = new Scanner(System.in);
            int x = 1;
            while (x==1) {
                welcomeUser();

                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        buyProducts(conn, scanner);
                        break;
                    case 2:
                        sellProducts(conn, scanner);
                        break;
                    case 3:
                        viewTransactionsToMarket(conn);
                        break;
                    case 4:
                        viewTransactionsFromMarket(conn);
                        break;
                    case 5:
                        x=0;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + HOST + ":3306/" + DATABASE_NAME, USER, PASS);
    }

    public static void welcomeUser() {
        System.out.println("Welcome!");
        System.out.println("1. Buy Products");
        System.out.println("2. Sell Products");
        System.out.println("3. View Transactions From Market");
        System.out.println("4. View Transactions To Market");
        System.out.print("Select an option (1/2/3/4): ");
    }

    public static void buyProducts(Connection conn, Scanner scanner) {
        System.out.print("Enter the product to search: ");
        String productName = scanner.nextLine();

        Map<String, Integer> marketInfo = new HashMap<>();

        System.out.println("Markets where " + productName + " is available:");
        System.out.println("------------------------");

        for (String market : MARKETS) {
            int availableQuantity = 0;
            try {
                availableQuantity = displayMarketInfo(conn, market, productName);
            } catch (SQLException e) {
                handleSQLException(e);
            }

            if (availableQuantity > 0) {
                marketInfo.put(market, availableQuantity);
            }
        }

        if (marketInfo.isEmpty()) {
            System.out.println("The product is not available in any market.");
            return;
        }

        System.out.print("Enter the market from which you want to purchase " + productName + ": ");
        String selectedMarket = scanner.nextLine();

        if (!marketInfo.containsKey(selectedMarket)) {
            System.out.println("Invalid market selection.");
            return;
        }

        int availableQuantity = marketInfo.get(selectedMarket);

        System.out.print("Enter the quantity required (up to " + availableQuantity + "): ");
        int requiredQuantity = scanner.nextInt();
        scanner.nextLine();

        if (requiredQuantity <= 0 || requiredQuantity > availableQuantity) {
            System.out.println("Invalid quantity.");
            return;
        }

        try {
            int totalPrice = orderProductFromMarket(conn, selectedMarket, productName, requiredQuantity);
            System.out.println("Ordered from " + selectedMarket);
            System.out.println("Total Price: $" + totalPrice);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    public static void sellProducts(Connection conn, Scanner scanner) {
        System.out.print("Enter the product to sell: ");
        String productName = scanner.nextLine();

        Map<String, Integer> marketInfo = new HashMap<>();

        System.out.println("Markets where you can sell " + productName + ":");
        System.out.println("------------------------");

        for (String market : MARKETS) {
            int availableQuantity = 0;
            try {
                availableQuantity = displayMarketInfo(conn, market, productName);
            } catch (SQLException e) {
                handleSQLException(e);
            }

            if (availableQuantity > 0) {
                marketInfo.put(market, availableQuantity);
            }
        }

        if (marketInfo.isEmpty()) {
            System.out.println("You cannot sell the product to any market.");
            return;
        }

        System.out.print("Enter the market where you want to sell " + productName + ": ");
        String selectedMarket = scanner.nextLine();

        if (!marketInfo.containsKey(selectedMarket)) {
            System.out.println("Invalid market selection.");
            return;
        }

        int availableQuantity = marketInfo.get(selectedMarket);

        System.out.print("Enter the quantity to sell: ");
        int soldQuantity = scanner.nextInt();
        scanner.nextLine();

        if (soldQuantity <= 0) {
            System.out.println("Invalid quantity. Quantity must be greater than 0.");
            return;
        }

        try {
            int totalPrice = sellProductToMarket(conn, selectedMarket, productName, soldQuantity);
            System.out.println("Sold to " + selectedMarket);
            System.out.println("Total Income: $" + totalPrice);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private static void handleSQLException(SQLException e) {
        e.printStackTrace();
        System.err.println("Database error: " + e.getMessage());
    }

    private static void handleException(Exception e) {
        e.printStackTrace();
        System.err.println("An error occurred: " + e.getMessage());
    }

    public static int displayMarketInfo(Connection conn, String market, String productName) throws SQLException {
        String query = "SELECT * FROM " + market + " WHERE productname = ?";
        int availableQuantity = 0;
        int price = 0;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, productName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    availableQuantity = rs.getInt("quantity");
                    price = rs.getInt("price");
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }

        if (availableQuantity > 0) {
            System.out.println(market);
            System.out.println("Available Quantity: " + availableQuantity);
            System.out.println("Price: $" + price);
            System.out.println();
        }

        return availableQuantity;
    }

    public static int orderProductFromMarket(Connection conn, String market, String productName, int quantity) throws SQLException {
        int totalPrice = PRICE_PER_UNIT_BUY * quantity;

        String updateQuery = "UPDATE " + market + " SET quantity = quantity - ? WHERE productname = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
            ps.setInt(1, quantity);
            ps.setString(2, productName);
            ps.executeUpdate();
        }

        insertTransactionToMarket(conn, productName, market, quantity, totalPrice);

        return totalPrice;
    }

    public static int sellProductToMarket(Connection conn, String market, String productName, int quantity) throws SQLException {
        int totalPrice = PRICE_PER_UNIT_SELL * quantity;

        String updateQuery = "UPDATE " + market + " SET quantity = quantity + ? WHERE productname = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
            ps.setInt(1, quantity);
            ps.setString(2, productName);
            ps.executeUpdate();
        }

        insertTransactionFromMarket(conn, productName, market, quantity, totalPrice);

        return totalPrice;
    }

    public static void insertTransactionToMarket(Connection conn, String productName, String marketName, int quantity, int totalPrice) throws SQLException {
        String insertQuery = "INSERT INTO transactions_to_market (product_name, market_name, quantity, total_price) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            ps.setString(1, productName);
            ps.setString(2, marketName);
            ps.setInt(3, quantity);
            ps.setInt(4, totalPrice);
            ps.executeUpdate();
        }
    }

    public static void insertTransactionFromMarket(Connection conn, String productName, String marketName, int quantity, int totalPrice) throws SQLException {
        String insertQuery = "INSERT INTO transactions_from_market (product_name, market_name, quantity, total_price) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            ps.setString(1, productName);
            ps.setString(2, marketName);
            ps.setInt(3, quantity);
            ps.setInt(4, totalPrice);
            ps.executeUpdate();
        }
    }

    public static void viewTransactionsToMarket(Connection conn) {
        try {
            String query = "SELECT * FROM transactions_to_market";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("Transactions To Market:");
                    System.out.println("Transaction ID | Product Name | Market Name | Quantity | Total Price");
                    System.out.println("-------------------------------------------------------------");
                    while (rs.next()) {
                        int transactionId = rs.getInt("transaction_id");
                        String productName = rs.getString("product_name");
                        String marketName = rs.getString("market_name");
                        int quantity = rs.getInt("quantity");
                        int totalPrice = rs.getInt("total_price");
                        System.out.printf("%-15d | %-12s | %-11s | %-8d | $%-9d%n", transactionId, productName, marketName, quantity, totalPrice);
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    public static void viewTransactionsFromMarket(Connection conn) {
        try {
            String query = "SELECT * FROM transactions_from_market";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("Transactions From Market:");
                    System.out.println("Transaction ID | Product Name | Market Name | Quantity | Total Price");
                    System.out.println("-------------------------------------------------------------");
                    while (rs.next()) {
                        int transactionId = rs.getInt("transaction_id");
                        String productName = rs.getString("product_name");
                        String marketName = rs.getString("market_name");
                        int quantity = rs.getInt("quantity");
                        int totalPrice = rs.getInt("total_price");
                        System.out.printf("%-15d | %-12s | %-11s | %-8d | $%-9d%n", transactionId, productName, marketName, quantity, totalPrice);
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }
}
