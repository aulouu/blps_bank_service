package itmo.blps.bank_service.controller;

import itmo.blps.bank_service.dto.request.BalanceRequest;
import itmo.blps.bank_service.dto.request.NewCardRequest;
import itmo.blps.bank_service.service.BankService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bank")
public class BankController {
    private final BankService bankService;

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody @Valid BalanceRequest balanceRequest) {
        bankService.withdraw(balanceRequest.getNumber(), balanceRequest.getMoney());
        return ResponseEntity.ok("success");
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody @Valid NewCardRequest newCardRequest) {
        bankService.createNewCard(newCardRequest.getNumber());
        return ResponseEntity.ok("success");
    }
}
