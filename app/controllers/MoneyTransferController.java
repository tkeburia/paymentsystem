package controllers;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import rest.external.request.MoneyTransferRequest;
import rest.internal.response.MoneyTransferResponse;
import services.MoneyTransferService;

import javax.inject.Inject;

import static play.libs.Json.toJson;

public class MoneyTransferController extends Controller {

    private final MoneyTransferService moneyTransferService;

    @Inject
    public MoneyTransferController(MoneyTransferService moneyTransferService) {
        this.moneyTransferService = moneyTransferService;
    }

    public Result transferMoney() {
        final MoneyTransferResponse moneyTransferResponse = delegateRequest();

        if (moneyTransferResponse.isSuccess()) return ok(toJson(moneyTransferResponse));
        return forbidden(toJson(moneyTransferResponse));
    }

    private MoneyTransferResponse delegateRequest() {
        final MoneyTransferRequest moneyTransferRequest = Json
                .fromJson(request().body().asJson(), MoneyTransferRequest.class);

        return moneyTransferService.transferMoney(moneyTransferRequest);
    }
}
