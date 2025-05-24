package itmo.blps.bank_service.service;

import itmo.blps.bank_service.exception.*;
import itmo.blps.bank_service.model.BankCard;
import itmo.blps.bank_service.model.Operation;
import itmo.blps.bank_service.model.OperationType;
import itmo.blps.bank_service.repository.BankCardRepository;
import itmo.blps.bank_service.repository.OperationRepository;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class BankService {
    private final BankCardRepository bankCardRepository;
    private final OperationRepository operationRepository;
    private final TransactionManager transactionManager;

    public void withdraw(String cardNumber, Double amount) {
        try {
//            transactionManager.begin();

            BankCard card = bankCardRepository.findByNumber(cardNumber)
                    .orElseThrow(() -> new CardNotFoundException(String.format("No such card with number: %s", cardNumber)));

            if (amount <= 0) {
                throw new NotValidInputException("Amount must be positive");
            }
            if (card.getMoney() < amount) {
                throw new NotEnoughMoneyException("Not enough money at bank card");
            }

            Operation operation = Operation.builder()
                    .amount(amount)
                    .card(card)
                    .type(OperationType.WITHDRAWAL)
                    .timestamp(LocalDateTime.now())
                    .build();

            card.setMoney(card.getMoney() - amount);
            bankCardRepository.save(card);
            operationRepository.save(operation);

//            transactionManager.commit();
        } catch (Exception e) {
//            try {
//                transactionManager.rollback();
//            } catch (SystemException ex) {
//                throw new FailTransactionException("Failed to rollback transaction");
//            }
            throw new FailTransactionException("Withdraw failed with error: " + e.getMessage());
        }
    }

    public void createNewCard(String cardNumber) {
        Optional<BankCard> tryCard = bankCardRepository.findByNumber(cardNumber);
        if (tryCard.isPresent()) {
            throw new CardAlreadyExistsException(String.format("Card with number: %s already exists", cardNumber));
        }
        BankCard card = bankCardRepository.findByNumber(cardNumber)
                .orElseGet(() -> BankCard.builder()
                        .number(cardNumber)
                        .money(ThreadLocalRandom.current().nextDouble(1000, 50000))
                        .build());
        bankCardRepository.save(card);
    }

}
