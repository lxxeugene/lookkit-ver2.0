package synerjs.lookkit2nd.payment.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.IamportResponse;
import org.springframework.stereotype.Service;

@Service
public class IamportService {
    private final IamportClient iamportClient;

    public IamportService() {
        this.iamportClient = new IamportClient("imp40354073", "SECRET"); // 보안 유의
    }

    public IamportResponse<Payment> verifyPayment(String impUid) throws Exception {
        return iamportClient.paymentByImpUid(impUid);
    }
}
