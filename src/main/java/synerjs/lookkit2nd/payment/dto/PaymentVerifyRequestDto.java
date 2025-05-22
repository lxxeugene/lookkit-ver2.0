package synerjs.lookkit2nd.payment.dto;

import lombok.Getter;
import lombok.Setter;
import synerjs.lookkit2nd.order.dto.OrderDetailDTO;
import jakarta.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class PaymentVerifyRequestDto {
    @NotBlank
    private String impUid;

    @NotBlank
    private String merchantUid;

    @NotNull
    private Long userId;

    @Min(1000)
    private int totalAmount;
    private String orderAddress;
    private String orderAddressee;
    private String orderPhone;
    private String orderComment;
    private List<OrderDetailDTO> orderDetails;
}
