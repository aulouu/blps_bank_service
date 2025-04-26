package itmo.blps.bank_service.dto.response;

import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private String transactionId;
    private boolean success;
}
