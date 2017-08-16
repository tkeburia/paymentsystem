package checkers.impl;

import checkers.Checker;
import rest.internal.request.InternalMoneyTransferRequest;

import java.util.List;

public class AccountChecker implements Checker {

    @Override
    public void check(InternalMoneyTransferRequest moneyTransferRequest, List<String> errors) {

        if (moneyTransferRequest.getSourceCustomer().getAccount().isLocked()) errors.add("Source account locked");
        if (moneyTransferRequest.getTargetCustomer().getAccount().isLocked()) errors.add("Target account locked");
        if (!moneyTransferRequest
                .getTargetCustomer()
                .getAccount()
                .getBalances()
                .stream()
                .filter(b -> b.getCurrency().equals(moneyTransferRequest.getCurrency()))
                .findFirst()
                .isPresent()
        ) errors.add("Destination customer's account does not support requested currency");
    }
}
