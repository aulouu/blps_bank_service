package itmo.blps.bank_service.repository;

import itmo.blps.bank_service.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, Long> {
}
