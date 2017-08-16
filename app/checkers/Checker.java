package checkers;

import rest.internal.request.InternalMoneyTransferRequest;

import java.util.List;

public interface Checker {

    void check(InternalMoneyTransferRequest moneyTransferRequest, List<String> errors);

}
