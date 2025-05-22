package synerjs.lookkit2nd.payment.controller;

import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import synerjs.lookkit2nd.order.dto.OrderDTO;
import synerjs.lookkit2nd.order.service.OrderService;
import synerjs.lookkit2nd.payment.dto.PaymentVerifyRequestDto;
import synerjs.lookkit2nd.payment.service.IamportService;

import java.math.BigDecimal;

@Slf4j
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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public ResponseEntity<?> verifyAndSaveOrder(@Valid @RequestBody PaymentVerifyRequestDto dto) {
        try {
            log.info("결제 검증 시작 - imp_uid: {}, merchant_uid: {}", dto.getImpUid(), dto.getMerchantUid());

            // 1. 결제 검증 요청
            IamportResponse<Payment> response = iamportService.verifyPayment(dto.getImpUid());
            if (response == null || response.getResponse() == null) {
                log.warn("결제 정보 없음 - imp_uid: {}", dto.getImpUid());
                return ResponseEntity.badRequest().body("결제 정보를 찾을 수 없습니다.");
            }

            // 2. 금액 검증
            int paidAmount = response.getResponse().getAmount().intValue();
            if (paidAmount != dto.getTotalAmount()) {
                log.warn("결제 금액 불일치 - 받은 금액: {}, 기대 금액: {}", paidAmount, dto.getTotalAmount());
                return ResponseEntity.badRequest().body("결제 금액 불일치");
            }

            // 3. 주문 DTO 매핑 및 저장
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

            Long orderId = orderService.saveOrder(orderDTO);
            log.info("주문 저장 완료 - orderId: {}", orderId);

            return ResponseEntity.ok().body(orderId);

        } catch (Exception e) {
            log.error("결제 검증/주문 저장 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
        }
    }
}
