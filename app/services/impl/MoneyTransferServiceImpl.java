package services.impl;

import checkers.Checker;
import enums.Currency;
import models.Account;
import models.Customer;
import models.repositories.impl.AccountRepository;
import models.repositories.impl.CustomerRepository;
import rest.external.request.MoneyTransferRequest;
import rest.internal.request.InternalMoneyTransferRequest;
import rest.internal.response.MoneyTransferResponse;
import services.CurrencyExchangeService;
import services.MoneyTransferService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

import static java.lang.Long.valueOf;
import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static utils.Utils.getAmountForCurrency;

public class MoneyTransferServiceImpl implements MoneyTransferService {
    private final Set<Checker> checkers;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CurrencyExchangeService currencyExchangeService;

    @Inject
    public MoneyTransferServiceImpl(
            Set<Checker> checkers,
            CustomerRepository customerRepository,
            AccountRepository accountRepository,
            CurrencyExchangeService currencyExchangeService
    )
    {
        this.checkers = checkers;
        this.customerRepository = customerRepository;
        this.currencyExchangeService = currencyExchangeService;
        this.accountRepository = accountRepository;
    }


    @Override
    public MoneyTransferResponse transferMoney(MoneyTransferRequest request) {
        List<String> errors = new ArrayList<>();
        InternalMoneyTransferRequest internalRequest = getInternalRequest(request);
        checkers.forEach(c -> c.check(internalRequest, errors));

        // at this point we know we can afford this transaction so we continue with this assumption
        // TODO this process needs to be transactional - some sort of locking mechanism needs to be in place
        // to avoid other transactions altering balances while this one hasn't finished
        if (errors.isEmpty()) return doTransfer(internalRequest);
        return new MoneyTransferResponse(false, errors);
    }

    private MoneyTransferResponse doTransfer(InternalMoneyTransferRequest request) {
        // here we keep track of all changes to different currency balances that will need to be applied as
        // result of this transaction
        Map<Currency, BigDecimal> balanceChanges = new HashMap<>();
        BigDecimal requestedAmount = request.getAmount();
        final Account srcAccount = request.getSourceCustomer().getAccount();

        // Try the requested currency: if (requestedAmount - existingAmountForCurrency) is negative we can
        // pay from the balance of the requested currency
        BigDecimal amountDifference = requestedAmount.subtract(srcAccount.getAmountFor(request.getCurrency()));
        if (amountDifference.compareTo(ZERO) <= 0) {
            balanceChanges.put(request.getCurrency(), requestedAmount);
            return commitBalanceChanges(request, balanceChanges, srcAccount);
        }
        // otherwise take out as much as we can from this currency and carry on
        balanceChanges.put(request.getCurrency(), srcAccount.getAmountFor(request.getCurrency()));

        // try overdraft: can we cover the requested amount by adding overdraft amount to the above difference?
        BigDecimal amountDifferenceIncludingOverdraft = amountDifference.subtract(srcAccount.getRemainingOverdraft());
        if (amountDifferenceIncludingOverdraft.compareTo(ZERO) <= 0) {
            srcAccount.incrementUsedOverdraft(amountDifference);
            return commitBalanceChanges(request, balanceChanges, srcAccount);
        }
        // if not, use up all allowed overdraft and carry on to other currencies
        srcAccount.incrementUsedOverdraft(srcAccount.getRemainingOverdraft());

        // try other currencies
        tryOtherCurrencies(request, balanceChanges, amountDifferenceIncludingOverdraft);

        return commitBalanceChanges(request, balanceChanges, srcAccount);
    }

    private void tryOtherCurrencies(
            InternalMoneyTransferRequest request,
            Map<Currency, BigDecimal> balanceChanges,
            BigDecimal amountDifferenceIncludingOverdraft
    )
    {
        // for each other currency decide what the exchanged amount is (here it will always be the same since we have
        // set up 1 to 1 rates for all currencies) and see if this amount brings the accumulated difference
        // (requested currency balance + overdraft) to zero or less
        for (Entry<Currency, BigDecimal> entry : currencyExchangeService.getExchangeRatesTable().get(request.getCurrency()).entrySet()) {
            final BigDecimal exchangedAmount = getAmountForCurrency(entry.getKey(), request).multiply(entry.getValue());
            if (amountDifferenceIncludingOverdraft.compareTo(exchangedAmount) <= 0) {
                // if it does we can finish here
                balanceChanges.put(entry.getKey(), amountDifferenceIncludingOverdraft);
                break;
            } else {
                // otherwise use up all exchanged balance, update the difference and carry on
                balanceChanges.put(entry.getKey(), exchangedAmount);
                amountDifferenceIncludingOverdraft = amountDifferenceIncludingOverdraft.subtract(exchangedAmount);
            }
        }
    }

    private MoneyTransferResponse commitBalanceChanges(
            InternalMoneyTransferRequest request,
            Map<Currency, BigDecimal> balanceChanges,
            Account srcAccount
    )
    {
        makeBalanceChanges(balanceChanges, srcAccount, request.getTargetCustomer().getAccount(), request);
        return new MoneyTransferResponse(true);
    }

    private void makeBalanceChanges(
            Map<Currency, BigDecimal> balanceChanges,
            Account sourceAccount,
            Account targetAccount,
            InternalMoneyTransferRequest request
    )
    {
        // update balances of the source account based on accumulated currency changes
        balanceChanges.entrySet().stream().forEach( entry ->
            sourceAccount.decreaseAmountFor(entry.getKey(), entry.getValue())
        );
        sourceAccount.getAccountLimit().increaseTransferredToday(request.getAmount());

        // update the target account balance
        targetAccount.increaseAmountFor(request.getCurrency(), request.getAmount());

        // persist changes
        accountRepository.update(sourceAccount);
        accountRepository.update(targetAccount);
    }

    private InternalMoneyTransferRequest getInternalRequest(MoneyTransferRequest request) {
        // look up the customers once and pass them downstream so we don't have to call the db
        // every time we want to access them
        Customer sourceCustomer = customerRepository
                .find(valueOf(request.getSourceCustomerId()))
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                format("Customer with id %s not found", request.getSourceCustomerId())
                        )
                );
        Customer targetCustomer = customerRepository
                .find(valueOf(request.getTargetCustomerId()))
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                format("Customer with id %s not found", request.getTargetCustomerId())
                        )
                );

        return new InternalMoneyTransferRequest(sourceCustomer, targetCustomer, request.getAmount(), request.getCurrency());
    }
}
