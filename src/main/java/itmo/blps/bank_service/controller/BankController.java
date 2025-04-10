package itmo.blps.bank_service.controller;

import itmo.blps.bank_service.dto.request.BalanceRequest;
import itmo.blps.bank_service.model.Operation;
import itmo.blps.bank_service.service.BankService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bank")
public class BankController {
    private final BankService bankService;

    @PostMapping("/deposit")
    public ResponseEntity<Operation> deposit(
            @RequestParam String cardNumber,
            @RequestParam Double amount) {
        try {
            Operation operation = bankService.deposit(cardNumber, amount);
            return ResponseEntity.ok(operation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(
            @RequestBody @Valid BalanceRequest balanceRequest) {
        try {
            bankService.withdraw(balanceRequest.getNumber(), balanceRequest.getMoney());
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
