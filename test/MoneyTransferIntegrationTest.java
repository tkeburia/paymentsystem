import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Logger;
import rest.external.request.MoneyTransferRequest;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static enums.Currency.EUR;
import static enums.Currency.GBP;
import static java.math.BigDecimal.*;
import static org.hamcrest.Matchers.is;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

public class MoneyTransferIntegrationTest {
    private static final int PORT = 3333;

    @Before
    public void setUp() {
        Logger.info("Running Money Transfer Integration Tests");
        RestAssured.port = PORT;
    }

    @After
    public void tearDown() {
        RestAssured.reset();
    }

    @Test
    public void shouldSuccessfullyTransferFromRequestedCurrencyBalance() {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("100001", "100002", ONE, GBP);

        running(testServer(PORT), () -> {
            // make the transfer
            given()
                .contentType(ContentType.JSON)
                .body(moneyTransferRequest)
            .when()
                .post("/updateCustomerBalance")
            .then()
                .statusCode(200)
                .body("success", is(true));

            // check source account
            when()
                .get("/customer/100001")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("99.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("1.00"));

            // check target account
            when()
                .get("/customer/100002")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("101.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));
        });
    }

    @Test
    public void shouldSuccessfullyTransferFromRequestedCurrencyBalanceAndOverdraft() {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("100001", "100002", valueOf(101), GBP);

        running(testServer(PORT), () -> {
            // make the transfer
            given()
                .contentType(ContentType.JSON)
                .body(moneyTransferRequest)
            .when()
                .post("/updateCustomerBalance")
            .then()
                .statusCode(200)
                .body("success", is(true));

            // check source account
            when()
                .get("/customer/100001")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("0.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("1.00"))
                .body("account.accountLimit.transferredToday", is("101.00"));

            // check target account
            when()
                .get("/customer/100002")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("201.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));
        });
    }

    @Test
    public void shouldSuccessfullyTransferFromRequestedCurrencyBalanceAndOverdraftAndOneOtherCurrency() {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("100001", "100002", valueOf(201), GBP);

        running(testServer(PORT), () -> {
            // make the transfer
            given()
                .contentType(ContentType.JSON)
                .body(moneyTransferRequest)
            .when()
                .post("/updateCustomerBalance")
            .then()
                .statusCode(200)
                .body("success", is(true));

            // check source account
            when()
                .get("/customer/100001")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("0.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("99.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("100.00"))
                .body("account.accountLimit.transferredToday", is("201.00"));

            // check target account
            when()
                .get("/customer/100002")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("301.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));
        });
    }

    @Test
    public void shouldSuccessfullyTransferFromRequestedCurrencyBalanceAndOverdraftAndAllOtherCurrencies() {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("100001", "100002", valueOf(301), GBP);

        running(testServer(PORT), () -> {
            // make the transfer
            given()
                .contentType(ContentType.JSON)
                .body(moneyTransferRequest)
            .when()
                .post("/updateCustomerBalance")
            .then()
                .statusCode(200)
                .body("success", is(true));

            // check source account
            when()
                .get("/customer/100001")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("0.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("0.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("99.00"))
                .body("account.usedOverdraft", is("100.00"))
                .body("account.accountLimit.transferredToday", is("301.00"));

            // check target account
            when()
                .get("/customer/100002")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("401.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));
        });
    }

    @Test
    public void shouldSuccessfullyTransferMaxAvailableAmount() {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("100001", "100002", valueOf(400), GBP);

        running(testServer(PORT), () -> {
            // make the transfer
            given()
                .contentType(ContentType.JSON)
                .body(moneyTransferRequest)
            .when()
                .post("/updateCustomerBalance")
            .then()
                .statusCode(200)
                .body("success", is(true));

            // check source account
            when()
                .get("/customer/100001")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("0.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("0.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("0.00"))
                .body("account.usedOverdraft", is("100.00"))
                .body("account.accountLimit.transferredToday", is("400.00"));

            // check target account
            when()
                .get("/customer/100002")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("500.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));
        });
    }

    @Test
    public void shouldFailIfTransferWillExceedLimit() {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("100005", "100001", valueOf(200), GBP);

        running(testServer(PORT), () -> {
            // make the transfer
            given()
                .contentType(ContentType.JSON)
                .body(moneyTransferRequest)
            .when()
                .post("/updateCustomerBalance")
            .then()
                .statusCode(403)
                .body("success", is(false))
                .body("errorMessages.size()", is(1))
                .body("errorMessages[0]", is("The requested amount exceeds limit for customer"));

            // check source account hasn't changed
            when()
                .get("/customer/100005")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));

            // check target account hasn't changed
            when()
                .get("/customer/100001")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));
        });
    }

    @Test
    public void shouldFailIfTransferLimitAlreadyReached() {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("100003", "100001", TEN, EUR);

        running(testServer(PORT), () -> {
            // make the transfer
            given()
                .contentType(ContentType.JSON)
                .body(moneyTransferRequest)
            .when()
                .post("/updateCustomerBalance")
            .then()
                .statusCode(403)
                .body("success", is(false))
                .body("errorMessages.size()", is(1))
                .body("errorMessages[0]", is("Transfer limit reached for source customer"));

            // check source account hasn't changed
            when()
                .get("/customer/100003")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'USD' }.amount", is("0.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("100.00"));

            // check target account hasn't changed
            when()
                .get("/customer/100001")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));
        });
    }

    @Test
    public void shouldFailIfInsufficientFunds() {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("100002", "100001", valueOf(1500), GBP);

        running(testServer(PORT), () -> {
            // make the transfer
            given()
                .contentType(ContentType.JSON)
                .body(moneyTransferRequest)
            .when()
                .post("/updateCustomerBalance")
            .then()
                .statusCode(403)
                .body("success", is(false))
                .body("errorMessages.size()", is(1))
                .body("errorMessages[0]", is("Insufficient funds, for requested and other currencies"));

            // check source account hasn't changed
            when()
                .get("/customer/100002")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));

            // check target account hasn't changed
            when()
                .get("/customer/100001")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));
        });
    }

    @Test
    public void shouldFailWhenSourceAccountLocked() {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("100004", "100001", valueOf(15), GBP);

        running(testServer(PORT), () -> {
            // make the transfer
            given()
                .contentType(ContentType.JSON)
                .body(moneyTransferRequest)
            .when()
                .post("/updateCustomerBalance")
            .then()
                .statusCode(403)
                .body("success", is(false))
                .body("errorMessages.size()", is(1))
                .body("errorMessages[0]", is("Source account locked"));

            // check source account hasn't changed
            when()
                .get("/customer/100004")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));

            // check target account hasn't changed
            when()
                .get("/customer/100001")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));
        });
    }

    @Test
    public void shouldFailWhenTargetAccountLocked() {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("100001", "100004", valueOf(15), GBP);

        running(testServer(PORT), () -> {
            // make the transfer
            given()
                .contentType(ContentType.JSON)
                .body(moneyTransferRequest)
            .when()
                .post("/updateCustomerBalance")
            .then()
                .statusCode(403)
                .body("success", is(false))
                .body("errorMessages.size()", is(1))
                .body("errorMessages[0]", is("Target account locked"));

            // check source account hasn't changed
            when()
                .get("/customer/100001")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));

            // check target account hasn't changed
            when()
                .get("/customer/100004")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));
        });
    }


    @Test
    public void shouldFailWhenDestinationAccountDoesntSupportRequestedCurrency() {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("100001", "100003", valueOf(15), GBP);

        running(testServer(PORT), () -> {
            // make the transfer
            given()
                .contentType(ContentType.JSON)
                .body(moneyTransferRequest)
            .when()
                .post("/updateCustomerBalance")
            .then()
                .statusCode(403)
                .body("success", is(false))
                .body("errorMessages.size()", is(1))
                .body("errorMessages[0]", is("Destination customer's account does not support requested currency"));

            // check source account hasn't changed
            when()
                .get("/customer/100001")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'GBP' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'USD' }.amount", is("100.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("0.00"));

            // check target account hasn't changed
            when()
                .get("/customer/100003")
            .then()
                .statusCode(200)
                .body("account.balances.find { it.currency == 'USD' }.amount", is("0.00"))
                .body("account.balances.find { it.currency == 'EUR' }.amount", is("100.00"))
                .body("account.usedOverdraft", is("0.00"))
                .body("account.accountLimit.transferredToday", is("100.00"));
        });
    }

    @Test
    public void shouldFailWhenSourceUserNotFound() {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("100009", "100003", valueOf(15), GBP);

        running(testServer(PORT), () -> {
            given()
                .contentType(ContentType.JSON)
                .body(moneyTransferRequest)
            .when()
                .post("/updateCustomerBalance")
            .then()
                .statusCode(500)
                .body("Error", is("Customer with id 100009 not found"));

        });
    }

    @Test
    public void shouldFailWhenTargetUserNotFound() {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("100001", "100010", valueOf(15), GBP);

        running(testServer(PORT), () -> {
            given()
                .contentType(ContentType.JSON)
                .body(moneyTransferRequest)
            .when()
                .post("/updateCustomerBalance")
            .then()
                .statusCode(500)
                .body("Error", is("Customer with id 100010 not found"));

        });
    }

}
