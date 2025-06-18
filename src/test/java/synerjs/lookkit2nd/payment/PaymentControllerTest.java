package synerjs.lookkit2nd.payment;

import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import synerjs.lookkit2nd.order.dto.OrderDTO;
import synerjs.lookkit2nd.order.service.OrderService;
import synerjs.lookkit2nd.payment.controller.PaymentController;
import synerjs.lookkit2nd.payment.dto.PaymentVerifyRequestDto;
import synerjs.lookkit2nd.payment.service.IamportService;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class PaymentControllerTest {

    private PaymentController paymentController;
    private IamportService iamportService;
    private OrderService orderService;

    @BeforeEach
    void setup() {
        iamportService = Mockito.mock(IamportService.class);
        orderService = Mockito.mock(OrderService.class);
        paymentController = new PaymentController(iamportService, orderService);
    }

    @Test
    void 결제성공_주문저장() throws Exception {
        // given
        PaymentVerifyRequestDto dto = new PaymentVerifyRequestDto();
        dto.setImpUid("imp_123");
        dto.setMerchantUid("order_456");
        dto.setUserId(1L);
        dto.setTotalAmount(30000);
        dto.setOrderAddress("서울시 강남구");
        dto.setOrderAddressee("홍길동");
        dto.setOrderPhone("010-1234-5678");
        dto.setOrderComment("문 앞에 두세요");

        // Payment mock
        Payment payment = Mockito.mock(Payment.class);
        Mockito.when(payment.getAmount()).thenReturn(BigDecimal.valueOf(30000));

        // IamportResponse mock
        IamportResponse<Payment> response = Mockito.mock(IamportResponse.class);
        Mockito.when(response.getResponse()).thenReturn(payment);

        Mockito.when(iamportService.verifyPayment(dto.getImpUid())).thenReturn(response);
        Mockito.when(orderService.saveOrder(any(OrderDTO.class))).thenReturn(1L);

        // when
        var result = paymentController.verifyAndSaveOrder(dto);

        // then
        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(1L);
    }

    @Test
    void 결제금액_불일치() throws Exception {
        // given
        PaymentVerifyRequestDto dto = new PaymentVerifyRequestDto();
        dto.setImpUid("imp_789");
        dto.setMerchantUid("order_000");
        dto.setUserId(2L);
        dto.setTotalAmount(50000); // 기대값

        Payment payment = Mockito.mock(Payment.class);
        Mockito.when(payment.getAmount()).thenReturn(BigDecimal.valueOf(30000)); // 실제 결제 금액

        IamportResponse<Payment> response = Mockito.mock(IamportResponse.class);
        Mockito.when(response.getResponse()).thenReturn(payment);

        Mockito.when(iamportService.verifyPayment(dto.getImpUid())).thenReturn(response);

        // when
        var result = paymentController.verifyAndSaveOrder(dto);

        // then
        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("결제 금액 불일치");
    }

    @Test
    void 결제응답_NULL() throws Exception {
        // given
        PaymentVerifyRequestDto dto = PaymentVerifyRequestDto.builder()
                .impUid("imp_null")
                .merchantUid("order_null")
                .userId(3L)
                .totalAmount(10000)
                .build();

        Mockito.when(iamportService.verifyPayment(dto.getImpUid())).thenReturn(null);

        // when
        var result = paymentController.verifyAndSaveOrder(dto);

        // then
        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("결제 정보를 찾을 수 없습니다.");
    }

    @Test
    void 결제데이터_NULL() throws Exception {
        // given
        PaymentVerifyRequestDto dto = PaymentVerifyRequestDto.builder()
                .impUid("imp_null_data")
                .merchantUid("order_null_data")
                .userId(4L)
                .totalAmount(20000)
                .build();

        IamportResponse<Payment> response = Mockito.mock(IamportResponse.class);
        Mockito.when(response.getResponse()).thenReturn(null);

        Mockito.when(iamportService.verifyPayment(dto.getImpUid())).thenReturn(response);

        // when
        var result = paymentController.verifyAndSaveOrder(dto);

        // then
        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("결제 정보를 찾을 수 없습니다.");
    }

    @Test
    void 주문저장_중_예외발생() throws Exception {
        // given
        PaymentVerifyRequestDto dto = PaymentVerifyRequestDto.builder()
                .impUid("imp_fail")
                .merchantUid("order_fail")
                .userId(5L)
                .totalAmount(15000)
                .build();

        Payment payment = Mockito.mock(Payment.class);
        Mockito.when(payment.getAmount()).thenReturn(BigDecimal.valueOf(15000));

        IamportResponse<Payment> response = Mockito.mock(IamportResponse.class);
        Mockito.when(response.getResponse()).thenReturn(payment);

        Mockito.when(iamportService.verifyPayment(dto.getImpUid())).thenReturn(response);
        Mockito.when(orderService.saveOrder(any(OrderDTO.class))).thenThrow(new RuntimeException("DB 오류"));

        // when
        var result = paymentController.verifyAndSaveOrder(dto);

        // then
        assertThat(result.getStatusCode().value()).isEqualTo(500);
        assertThat(result.getBody().toString()).contains("서버 오류");
    }

}
