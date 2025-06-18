package synerjs.lookkit2nd.payment.dto;

import lombok.*;
import synerjs.lookkit2nd.order.dto.OrderDetailDTO;
import jakarta.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerifyRequestDto {
    @NotBlank
    private String impUid;

    @NotBlank
    private String merchantUid;

    @NotNull
    private Long userId;

    private int totalAmount;
    private String orderAddress;
    private String orderAddressee;
    private String orderPhone;
    private String orderComment;
    private List<OrderDetailDTO> orderDetails;
}
