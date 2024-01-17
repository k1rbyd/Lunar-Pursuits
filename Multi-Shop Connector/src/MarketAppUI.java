import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class MarketAppUI extends Application {

    private static final String HOST = "localhost";
    private static final String USER = "root";
    private static final String PASS = "Qwerty#80085";
    private static final String DATABASE_NAME = "PROJECT2";

    private Connection conn;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("MSC");

        // Initialize database connection
        Task<Connection> connectTask = new Task<>() {
            @Override
            protected Connection call() throws Exception {
                return getConnection();
            }
        };

        connectTask.setOnSucceeded(event -> {
            conn = connectTask.getValue();
            // Create the main UI and set it when the database connection is established
            createUI(primaryStage);
        });

        Thread connectThread = new Thread(connectTask);
        connectThread.start();

        // Show loading spinner
        StackPane loadingPane = new StackPane(new ProgressIndicator());
        Scene loadingScene = new Scene(loadingPane, 400, 200);
        primaryStage.setScene(loadingScene);
        primaryStage.show();
    }

    private void createUI(Stage primaryStage) {
        // Create the layout
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefSize(800, 600);

        // Create a menu
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Options");
        MenuItem viewToMenuItem = new MenuItem("View Transactions To Market");
        MenuItem viewFromMenuItem = new MenuItem("View Transactions From Market");
        MenuItem exitMenuItem = new MenuItem("Exit");
        menu.getItems().addAll(viewToMenuItem, viewFromMenuItem, new SeparatorMenuItem(), exitMenuItem);
        menuBar.getMenus().add(menu);

        // Create a TextArea for displaying information
        TextArea textArea = new TextArea();
        textArea.setEditable(false);

        // Create buttons for Buy and Sell
        Button buyButton = new Button("Buy Products");
        Button sellButton = new Button("Sell Products");

        // Create a VBox to hold the buttons
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(buyButton, sellButton);

        // Add components to the layout
        borderPane.setTop(menuBar);
        borderPane.setCenter(textArea);
        borderPane.setBottom(buttonBox);

        // Set actions for Buy and Sell buttons
        buyButton.setOnAction(e -> buyProducts(textArea));
        sellButton.setOnAction(e -> sellProducts(textArea));

        // Set actions for menu items
        viewToMenuItem.setOnAction(e -> viewTransactions(textArea, "transactions_to_market"));
        viewFromMenuItem.setOnAction(e -> viewTransactions(textArea, "transactions_from_market"));

        // Create a scene
        Scene scene = new Scene(borderPane);

        // Set the main UI scene
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private int queryProductAvailability(String market, String productName) throws SQLException {
        int availableQuantity = 0;

        String query = "SELECT quantity FROM " + market + " WHERE product_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, productName);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                availableQuantity = resultSet.getInt("quantity");
            }
        }

        return availableQuantity;
    }

    private void viewTransactions(TextArea textArea, String tableName) {
        // Implement the logic to view transactions to/from the market here
        try {
            String query = "SELECT * FROM " + tableName;
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Clear the text area
            textArea.clear();

            // Iterate through the results and append them to the text area
            while (resultSet.next()) {
                String productName = resultSet.getString("product_name");
                int quantity = resultSet.getInt("quantity");
                String marketName = resultSet.getString("market_name");

                textArea.appendText("Product Name: " + productName + "\n");
                textArea.appendText("Quantity: " + quantity + "\n");
                textArea.appendText("Market Name: " + marketName + "\n");
                textArea.appendText("\n"); // Add a separator
            }

            // Close resources
            preparedStatement.close();
            resultSet.close();
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void handleSQLException(SQLException e) {
        e.printStackTrace();
        System.err.println("Database error: " + e.getMessage());
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load the MySQL JDBC driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("JDBC Driver not found");
        }

        // Adjust the connection URL to match your database
        String url = "jdbc:mysql://" + HOST + ":3306/" + DATABASE_NAME
                + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

        return DriverManager.getConnection(url, USER, PASS);
    }

    private int orderProductFromMarket(Connection conn, String market, String productName, int quantity) throws SQLException {
        // Implement the logic to order a product from a market here
        int remainingQuantity = 0;
        try {
            // Check if the product is available in the selected market
            int availableQuantity = queryProductAvailability(market, productName);

            if (availableQuantity >= quantity) {
                // Update the remaining quantity in the market
                String updateQuery = "UPDATE " + market + " SET quantity = ? WHERE product_name = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    remainingQuantity = availableQuantity - quantity;
                    updateStmt.setInt(1, remainingQuantity);
                    updateStmt.setString(2, productName);
                    updateStmt.executeUpdate();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Not enough quantity available in the market.");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return remainingQuantity;
    }

    private int sellProductToMarket(Connection conn, String market, String productName, int quantity) throws SQLException {
        // Implement the logic to sell a product to a market here
        int remainingQuantity = 0;
        try {
            // Check if the product is available in the selected market
            int availableQuantity = queryProductAvailability(market, productName);

            // Update the remaining quantity in the market
            String updateQuery = "UPDATE " + market + " SET quantity = ? WHERE product_name = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                remainingQuantity = availableQuantity + quantity;
                updateStmt.setInt(1, remainingQuantity);
                updateStmt.setString(2, productName);
                updateStmt.executeUpdate();
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return remainingQuantity;
    }

    private void buyProducts(TextArea textArea) {
        // Create a dialog to select a market to buy from
        ChoiceDialog<String> marketDialog = new ChoiceDialog<>("SelectedMarket", "Market1", "Market2", "Market3");
        marketDialog.setTitle("Select Market");
        marketDialog.setHeaderText("Select a market to buy from:");
        marketDialog.setContentText("Market:");

        Optional<String> selectedMarket = marketDialog.showAndWait();
        if (selectedMarket.isPresent()) {
            String selectedMarketName = selectedMarket.get();

            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Buy Products");
            dialog.setHeaderText("Enter product details to buy from " + selectedMarketName);

            ButtonType buyButtonType = new ButtonType("Buy", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(buyButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField productNameField = new TextField();
            TextField quantityField = new TextField();

            grid.add(new Label("Product Name:"), 0, 0);
            grid.add(productNameField, 1, 0);
            grid.add(new Label("Quantity:"), 0, 1);
            grid.add(quantityField, 1, 1);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == buyButtonType) {
                    String productName = productNameField.getText();
                    int quantity = Integer.parseInt(quantityField.getText()); // Convert to int
                    try {
                        int remainingQuantity = orderProductFromMarket(conn, selectedMarketName, productName, quantity);
                        textArea.appendText("Ordered " + quantity + " " + productName + " from " + selectedMarketName +
                                ". Remaining Quantity: " + remainingQuantity + "\n");
                    } catch (SQLException e) {
                        handleSQLException(e);
                    }
                }
                return null;
            });

            dialog.showAndWait();
        }
    }

    private void sellProducts(TextArea textArea) {
        // Create a dialog to select a market to sell to
        ChoiceDialog<String> marketDialog = new ChoiceDialog<>("SelectedMarket", "Market1", "Market2", "Market3");
        marketDialog.setTitle("Select Market");
        marketDialog.setHeaderText("Select a market to sell to:");
        marketDialog.setContentText("Market:");

        Optional<String> selectedMarket = marketDialog.showAndWait();
        if (selectedMarket.isPresent()) {
            String selectedMarketName = selectedMarket.get();

            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Sell Products");
            dialog.setHeaderText("Enter product details to sell to " + selectedMarketName);

            ButtonType sellButtonType = new ButtonType("Sell", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(sellButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField productNameField = new TextField();
            TextField quantityField = new TextField();

            grid.add(new Label("Product Name:"), 0, 0);
            grid.add(productNameField, 1, 0);
            grid.add(new Label("Quantity:"), 0, 1);
            grid.add(quantityField, 1, 1);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == sellButtonType) {
                    String productName = productNameField.getText();
                    int quantity = Integer.parseInt(quantityField.getText()); // Convert to int
                    try {
                        int remainingQuantity = sellProductToMarket(conn, selectedMarketName, productName, quantity);
                        textArea.appendText("Sold " + quantity + " " + productName + " to " + selectedMarketName +
                                ". Remaining Quantity: " + remainingQuantity + "\n");
                    } catch (SQLException e) {
                        handleSQLException(e);
                    }
                }
                return null;
            });

            dialog.showAndWait();
        }
    }
}
