package checkers.impl;

import checkers.Checker;
import models.AccountLimit;
import rest.internal.request.InternalMoneyTransferRequest;

import java.util.List;

public class LimitChecker implements Checker {

    @Override
    public void check(InternalMoneyTransferRequest moneyTransferRequest, List<String> errors) {
        final AccountLimit accountLimit = moneyTransferRequest.getSourceCustomer().getAccount().getAccountLimit();

        if (accountLimit.isLimitReached()) {
            errors.add("Transfer limit reached for source customer");
            return;
        }
        if (accountLimit.getTransferLimit().subtract(accountLimit.getTransferredToday()).compareTo(moneyTransferRequest.getAmount()) < 0)
            errors.add("The requested amount exceeds limit for customer");
    }
}
