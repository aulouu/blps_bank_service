package itmo.blps.bank_service.service;

import itmo.blps.bank_service.model.BankCard;
import itmo.blps.bank_service.model.Operation;
import itmo.blps.bank_service.model.OperationType;
import itmo.blps.bank_service.repository.BankCardRepository;
import itmo.blps.bank_service.repository.OperationRepository;
import jakarta.transaction.UserTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.jta.JtaTransactionManager;

@Service
@RequiredArgsConstructor
public class BankService {
    private final BankCardRepository bankCardRepository;
    private final OperationRepository operationRepository;
    private final JtaTransactionManager transactionManager;

    public Operation deposit(String cardNumber, Double amount) {
        UserTransaction userTransaction;
        try {
            userTransaction = transactionManager.getUserTransaction();
            // Начинаем транзакцию
            userTransaction.begin();

            try {
                // 1. Находим карту
                BankCard card = bankCardRepository.findByNumber(cardNumber)
                        .orElseGet(() -> BankCard.builder().number(cardNumber).money(0.0).build());

                // 2. Проверяем сумму
                if (amount <= 0) {
                    throw new IllegalArgumentException("Amount must be positive");
                }

                // 3. Создаем запись операции
                Operation operation = Operation.builder()
                        .amount(amount)
                        .card(card)
                        .type(OperationType.DEPOSIT)
                        .build();

                // 4. Пополняем баланс
                card.setMoney(card.getMoney() + amount);

                // 5. Сохраняем изменения
                bankCardRepository.save(card);
                operationRepository.save(operation);

                // Фиксируем транзакцию
                userTransaction.commit();

                return operation;
            } catch (Exception e) {
                // Откатываем при ошибке
                userTransaction.rollback();
                throw new RuntimeException("Deposit failed", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Transaction error", e);
        }
    }

    public void withdraw(String cardNumber, Double amount) {
        UserTransaction userTransaction;
        try {
            userTransaction = transactionManager.getUserTransaction();
            // Начинаем транзакцию
            if (userTransaction != null) userTransaction.begin();
            try {
                // 1. Находим карту
                BankCard card = bankCardRepository.findByNumber(cardNumber)
                        .orElseGet(() -> BankCard.builder().number(cardNumber).money(0.0).build());

                // 2. Проверяем сумму и баланс
                if (amount <= 0) {
                    throw new IllegalArgumentException("Amount must be positive");
                }
                if (card.getMoney() < amount) {
                    throw new IllegalArgumentException("Insufficient funds");
                }

                // 3. Создаем запись операции
                Operation operation = Operation.builder()
                        .amount(amount)
                        .card(card)
                        .type(OperationType.WITHDRAWAL)
                        .build();

                // 4. Списываем средства
                card.setMoney(card.getMoney() - amount);

                // 5. Сохраняем изменения
                bankCardRepository.save(card);
                operationRepository.save(operation);

                // Фиксируем транзакцию
                userTransaction.commit();

//                return operation;
            } catch (Exception e) {
                // Откатываем при ошибке
                userTransaction.rollback();
                System.out.println(e.getMessage());
                throw new RuntimeException("Withdrawal failed", e);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Transaction error", e);
        }
    }

}
