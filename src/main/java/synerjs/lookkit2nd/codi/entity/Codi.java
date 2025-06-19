package synerjs.lookkit2nd.codi.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import synerjs.lookkit2nd.product.entity.Product;
import synerjs.lookkit2nd.review.entity.Review;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "codi")
public class Codi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CODI_ID")
    private Long codiId;

    private String codiName;
    private String codiDescription;
    private String codiThumbnail;
    private Integer codiPrice;

    @Column(nullable = false)
    private Integer quantity = 1; // 기본값으로 1 설정

    @OneToMany(mappedBy = "codi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Product> products = new ArrayList<>();


    @OneToMany(mappedBy = "codi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public Codi(String codiName, String codiDescription, String codiThumbnail, Integer codiPrice, Integer quantity) {
        this.codiName = codiName;
        this.codiDescription = codiDescription;
        this.codiThumbnail = codiThumbnail;
        this.codiPrice = codiPrice;
        this.quantity = (quantity != null) ? quantity : 1;
    }

}
