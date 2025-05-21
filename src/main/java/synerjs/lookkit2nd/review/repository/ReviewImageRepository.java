package synerjs.lookkit2nd.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synerjs.lookkit2nd.review.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
}
