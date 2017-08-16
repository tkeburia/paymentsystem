package utils;

import enums.Currency;
import rest.internal.request.InternalMoneyTransferRequest;

import java.math.BigDecimal;

public class Utils {

    public static BigDecimal getAmountForCurrency(Currency cur, InternalMoneyTransferRequest moneyTransferRequest) {
        return moneyTransferRequest.getSourceCustomer().getAccount().getAmountFor(cur);
    }
}
