import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the product model: ");
        String productModel = scanner.nextLine().trim();

        try {
            String productUrl = getProductUrlByModel(productModel);

            if (productUrl == null) {
                System.out.println("No product found with the specified model: " + productModel);
                return;
            }

            Product productDetails = getProductDetails(productUrl);

            if (productDetails == null) {
                System.out.println("Error: Failed to extract product details.");
                return;
            }

            printProductDetails(productDetails, productUrl);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String getProductUrlByModel(String productModel) throws IOException {
        String searchUrl = "https://www.bestbuy.com/site/searchpage.jsp?st=" + productModel;

        try {
            Connection.Response response = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .execute();

            if (response.statusCode() == 200) {
                Document document = response.parse();
                Elements productTitles = document.select(".sku-header .heading-5 a");

                for (Element title : productTitles) {
                    String titleText = title.text().trim().toLowerCase();

                    if (titleText.contains(productModel.toLowerCase())) {
                        return title.attr("href");
                    }
                }

                productTitles = document.select(".sku-header .product-title a");

                for (Element title : productTitles) {
                    String titleText = title.text().trim().toLowerCase();

                    if (titleText.contains(productModel.toLowerCase()) || productModel.toLowerCase().contains(titleText)) {
                        return title.attr("href");
                    }
                }
            } else {
                System.err.println("HTTP error fetching search URL. Status=" + response.statusCode() + ", URL=[" + searchUrl + "]");
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }

        return null;
    }

    private static Product getProductDetails(String productUrl) throws IOException {
        try {
            Connection.Response response = Jsoup.connect(productUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .execute();

            if (response.statusCode() == 200) {
                Document document = response.parse();
                String title = extractText(document, "h1.sku-title");
                String price = extractText(document, "div.priceView-hero-price span.sr-only");
                String description = extractText(document, "div.overview-section p");
                String imageUrl = extractAttribute(document, "img.primary-image", "src");
                String brand = extractText(document, "div.sku-title-container span.sku-title-brand");
                String availability = extractText(document, "div.priceView-stock-price span.availability-message");
                String seller = "Best Buy";

                return new Product(title, price, description, imageUrl, brand, availability, seller);
            } else {
                System.err.println("HTTP error fetching product URL. Status=" + response.statusCode() + ", URL=[" + productUrl + "]");
            }
        } catch (IOException e) {
            System.err.println("Error fetching product details: " + e.getMessage());
            throw e;
        }

        return null;
    }

    private static void printProductDetails(Product product, String productUrl) {
        System.out.println("Product Title: " + product.getTitle());
        System.out.println("Price: " + product.getPrice());
        System.out.println("Description: " + product.getDescription());
        System.out.println("Image URL: " + product.getImageUrl());
        System.out.println("Brand: " + product.getBrand());
        System.out.println("Availability: " + product.getAvailability());
        System.out.println("Seller: " + product.getSeller());
        System.out.println("Product URL: " + productUrl);
    }

    private static String extractText(Document document, String selector) {
        Element element = document.selectFirst(selector);
        return (element != null) ? element.text().trim() : null;
    }

    private static String extractAttribute(Document document, String selector, String attribute) {
        Element element = document.selectFirst(selector);
        return (element != null) ? element.attr(attribute).trim() : null;
    }
}
