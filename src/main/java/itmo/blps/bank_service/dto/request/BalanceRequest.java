package itmo.blps.bank_service.dto.request;

import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceRequest {
    private String number;
    private Double money;
}
