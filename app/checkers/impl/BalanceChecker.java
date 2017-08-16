package checkers.impl;

import checkers.Checker;
import enums.Currency;
import rest.internal.request.InternalMoneyTransferRequest;
import services.CurrencyExchangeService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map.Entry;

import static java.math.BigDecimal.ZERO;
import static utils.Utils.getAmountForCurrency;

public class BalanceChecker implements Checker {

    private final CurrencyExchangeService currencyExchangeService;

    @Inject
    public BalanceChecker(CurrencyExchangeService currencyExchangeService) {
        this.currencyExchangeService = currencyExchangeService;
    }

    // TODO make API response state if the remainder in being filled from other currencies
    // TODO implement a priority of currencies to decide where to take money from first
    @Override
    public void check(InternalMoneyTransferRequest request, List<String> errors) {
        // for the purpose of this exercise we assume overdraft only applies to the requested currency, i.e.
        // the application will use up the overdraft for requested currency before tapping into other currencies
        // and starting conversion
        BigDecimal differenceForRequestedCurrency = request.getAmount()
                   .subtract(getAmountForCurrency(request.getCurrency(), request))
                   .subtract(request.getSourceCustomer().getAccount().getRemainingOverdraft());
        if (differenceForRequestedCurrency.compareTo(ZERO) <= 0) return;

        for (Entry<Currency, BigDecimal> entry : currencyExchangeService
                .getExchangeRatesTable().get(request.getCurrency()).entrySet())
        {
            differenceForRequestedCurrency = differenceForRequestedCurrency
                    .subtract(getAmountForCurrency(entry.getKey(), request).multiply(entry.getValue()));
            if (differenceForRequestedCurrency.compareTo(ZERO) <= 0) return;
        }

        errors.add("Insufficient funds, for requested and other currencies");
    }

}
