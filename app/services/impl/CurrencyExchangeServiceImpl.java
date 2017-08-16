package services.impl;

import enums.Currency;
import services.CurrencyExchangeService;

import java.math.BigDecimal;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static enums.Currency.*;
import static java.math.BigDecimal.ONE;

public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {
    // for the purposes of this exercise we will define exchange rates between all currencies as 1 to 1
    // TODO externalise this and make it configurable
    @Override
    public Map<Currency, Map<Currency, BigDecimal>> getExchangeRatesTable() {
        return of(
                GBP, of(USD, ONE, EUR, ONE),
                USD, of(EUR, ONE, GBP, ONE),
                EUR, of(USD, ONE, GBP, ONE)
        );
    }
}
