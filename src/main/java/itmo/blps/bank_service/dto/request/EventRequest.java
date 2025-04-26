package itmo.blps.bank_service.dto.request;

import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {
    private String transactionId;
    private String number;
    private Double money;
}