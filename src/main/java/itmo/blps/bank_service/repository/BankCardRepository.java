package itmo.blps.bank_service.repository;

import itmo.blps.bank_service.model.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long>  {
    boolean existsByNumber(String number);
}
