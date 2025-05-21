package synerjs.lookkit2nd.payment.controller;

import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synerjs.lookkit2nd.order.dto.OrderDTO;
import synerjs.lookkit2nd.order.service.OrderService;
import synerjs.lookkit2nd.payment.dto.PaymentVerifyRequestDto;
import synerjs.lookkit2nd.payment.service.IamportService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final IamportService iamportService;
    private final OrderService orderService;

    @Autowired
    public PaymentController(IamportService iamportService, OrderService orderService) {
        this.iamportService = iamportService;
        this.orderService = orderService;
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyAndSaveOrder(@RequestBody PaymentVerifyRequestDto dto) {
        try {
            // 1. 포트원 서버에서 결제 정보 조회
            IamportResponse<Payment> response = iamportService.verifyPayment(dto.getImpUid());

            if (response == null || response.getResponse() == null) {
                return ResponseEntity.badRequest().body("결제 정보를 찾을 수 없습니다.");
            }

            // 2. 결제 금액 검증
            int paidAmount = response.getResponse().getAmount().intValue();
            if (paidAmount != dto.getTotalAmount()) {
                return ResponseEntity.badRequest().body("결제 금액 불일치");
            }

            // 3. 주문 DTO 구성
            OrderDTO orderDTO = OrderDTO.builder()
                    .userId(dto.getUserId())
                    .totalAmount(BigDecimal.valueOf(dto.getTotalAmount()))
                    .orderAddressee(dto.getOrderAddressee())
                    .orderAddress(dto.getOrderAddress())
                    .orderPhone(dto.getOrderPhone())
                    .orderComment(dto.getOrderComment())
                    .orderStatus("결제완료")
                    .orderDetails(dto.getOrderDetails())
                    .build();

            // 4. 주문 저장
            Long orderId = orderService.saveOrder(orderDTO);

            return ResponseEntity.ok().body(orderId);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
        }
    }
}
