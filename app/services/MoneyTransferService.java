package services;

import com.google.inject.ImplementedBy;
import rest.external.request.MoneyTransferRequest;
import rest.internal.response.MoneyTransferResponse;
import services.impl.MoneyTransferServiceImpl;

@ImplementedBy(MoneyTransferServiceImpl.class)
public interface MoneyTransferService {

    MoneyTransferResponse transferMoney(MoneyTransferRequest request);

}
