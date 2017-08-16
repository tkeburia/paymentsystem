package rest.external.request;

import enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoneyTransferRequest {
    String sourceCustomerId;
    String targetCustomerId;
    BigDecimal amount;
    Currency currency;
}
