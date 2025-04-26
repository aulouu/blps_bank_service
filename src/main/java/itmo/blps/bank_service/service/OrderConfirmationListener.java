package itmo.blps.bank_service.service;

import jakarta.jms.Queue;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConfirmationListener {
    private static final Logger logger = LoggerFactory.getLogger(OrderConfirmationListener.class);
    private final BankService bankService;
    private final JmsTemplate jmsTemplate;
    private final Queue cardSubmitQueue;


    @JmsListener(destination = "card.create.queue")
    public void processCardCreate(@Payload String number,
                                  @Header("JMSCorrelationID") String correlationId) {
        boolean success;
        try {
            bankService.createNewCard(number);
            success = true;
        } catch (Exception e) {
            logger.error("Unexpected error processing order: ", e);
            success = false;
        }
        jmsTemplate.convertAndSend(cardSubmitQueue, success, message -> {
            message.setJMSCorrelationID(correlationId);
            return message;
        });
    }
}
