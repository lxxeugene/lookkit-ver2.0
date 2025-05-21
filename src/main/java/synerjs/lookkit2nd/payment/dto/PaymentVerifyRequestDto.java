package synerjs.lookkit2nd.payment.dto;

import lombok.Getter;
import lombok.Setter;
import synerjs.lookkit2nd.order.dto.OrderDetailDTO;

import java.util.List;

@Getter
@Setter
public class PaymentVerifyRequestDto {
    private String impUid;
    private String merchantUid;
    private Long userId;
    private int totalAmount;
    private String orderAddress;
    private String orderAddressee;
    private String orderPhone;
    private String orderComment;
    private List<OrderDetailDTO> orderDetails;
}
