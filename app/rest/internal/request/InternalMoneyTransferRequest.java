package rest.internal.request;

import enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import models.Customer;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class InternalMoneyTransferRequest {
    Customer sourceCustomer;
    Customer targetCustomer;
    BigDecimal amount;
    Currency currency;
}
