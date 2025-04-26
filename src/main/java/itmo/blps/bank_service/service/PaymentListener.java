package itmo.blps.bank_service.service;

import itmo.blps.bank_service.dto.request.BalanceRequest;
import itmo.blps.bank_service.dto.request.EventRequest;
import itmo.blps.bank_service.dto.response.EventResponse;
import jakarta.jms.Queue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentListener {
    private final BankService bankService;
    private final JmsTemplate jmsTemplate;
    private final Queue orderPaymentQueue;

    @JmsListener(destination = "order.payment.queue")
    public void processPayment(EventRequest eventRequest) {
        EventResponse response = new EventResponse();
        response.setTransactionId(eventRequest.getTransactionId());
        try {
            bankService.withdraw(eventRequest.getNumber(), eventRequest.getMoney());
            response.setSuccess(true);
        } catch (Exception e) {
            response.setSuccess(false);
            log.error("Payment processing failed for transaction: {}", eventRequest.getTransactionId(), e);
            log.error("Unexpected error processing event: ", e);
        }
        jmsTemplate.convertAndSend(orderPaymentQueue, response);
    }
}
