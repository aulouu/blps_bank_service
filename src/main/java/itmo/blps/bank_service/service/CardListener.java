package itmo.blps.bank_service.service;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardListener {
    private final BankService bankService;
    private final JmsTemplate jmsTemplate;
    private final Queue cardSubmitQueue;

    @JmsListener(destination = "card.create.queue")
    public void processCardCreate(Message message) throws JMSException {
        boolean success;
        String correlationId = message.getJMSCorrelationID();
        try {
            if (correlationId == null) {
                throw new IllegalArgumentException("Missing JMSCorrelationID");
            }

            String number = ((TextMessage) message).getText();
            number = number.replace("\"", "");
            log.info("Processing card creation for number: {}, correlationId: {}", number, correlationId);

            bankService.createNewCard(number);
            success = true;
            jmsTemplate.convertAndSend(cardSubmitQueue, success, m -> {
                m.setJMSCorrelationID(correlationId);
                return m;
            });
        } catch (Exception e) {
            log.error("Error processing card creation", e);
            success = false;
            jmsTemplate.convertAndSend(cardSubmitQueue, success, m -> {
                m.setJMSCorrelationID(correlationId);
                return m;
            });
        }
//        jmsTemplate.convertAndSend(cardSubmitQueue, success, m -> {
//            m.setJMSCorrelationID(correlationId);
//            return m;
//        });
    }
}
