package itmo.blps.bank_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import itmo.blps.bank_service.dto.request.BalanceRequest;
import itmo.blps.bank_service.dto.request.EventRequest;
import itmo.blps.bank_service.dto.response.EventResponse;
import jakarta.jms.Queue;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentListener {
    private final BankService bankService;
    private final JmsTemplate jmsTemplate;
    private final Queue orderPaymentQueue;

    @JmsListener(destination = "order.payment.queue")
    public void processPayment(@Payload Map<String, Object> payload,
                               @Header(name = "_type", required = false) String type) {

        EventResponse response = new EventResponse();

        try {
            // Извлекаем данные из Map
            String correlationId = (String) payload.get("correlationId");
            String number = (String) payload.get("number");
            Double money = (Double) payload.get("money");

            response.setCorrelationId(correlationId);

            bankService.withdraw(number, money);
            response.setSuccess(true);

        } catch (Exception e) {
            response.setSuccess(false);
            log.error("Payment processing failed", e);
        }

        jmsTemplate.convertAndSend(orderPaymentQueue, response);
    }
}
