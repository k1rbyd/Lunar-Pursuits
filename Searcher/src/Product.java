import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Product {
    private String title;
    private String price;
    private String description;
    private String imageUrl;
    private String brand;
    private String availability;
    private String seller;
}
