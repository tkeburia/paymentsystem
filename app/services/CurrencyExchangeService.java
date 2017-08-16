package services;

import com.google.inject.ImplementedBy;
import enums.Currency;
import services.impl.CurrencyExchangeServiceImpl;

import java.math.BigDecimal;
import java.util.Map;

@ImplementedBy(CurrencyExchangeServiceImpl.class)
public interface CurrencyExchangeService {

    Map<Currency, Map<Currency, BigDecimal>> getExchangeRatesTable();

}
