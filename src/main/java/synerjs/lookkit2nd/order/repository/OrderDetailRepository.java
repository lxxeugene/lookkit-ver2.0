package synerjs.lookkit2nd.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synerjs.lookkit2nd.order.entity.Order;
import synerjs.lookkit2nd.order.entity.OrderDetail;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrder(Order order);

    List<OrderDetail> findByUser_UserId(Long userId);
}
