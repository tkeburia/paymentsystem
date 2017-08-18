# paymentsystem
A sample system to transfer money between customer accounts

This application is built using the play framework and uses sbt as a build tool.
It comes with a bundled sbt runner so no need to have it pre-installed.

## Getting Started
To get started, clone the repository:
```bash
git clone https://github.com/tkeburia/paymentsystem.git
```

To build a runnable jar using the bundled sbt runner, in the root directory do:
```bash
./sbt assembly
```

this will build a file called `payment_system.jar` inside `target/scala-2.12` directory.


The assembly command will run the integration tests in `MoneyTransferIntegrationTest` to make sure all scenarios work
as expected.

The tests can be run separately as well using:

```bash
./sbt test
```

To run the jar file simply do:
```bash
java -jar PATH_TO_FILE/payment_system.jar
```

This will run the application on port 9000 on localhost.

## Usage

During startup, the application will load sample data from the `conf/data/customers.yml`.
This sets up several customer with different balances for different currencies, transfer limits and overdrafts.

To view all customers with the respective data hit
`http://localhost:9000/customers` or for a specific customer `http://localhost:9000/customer/{id}`

To make a transfer the following curl request (or equivalent) needs to be posted:
```bash
curl -X POST \
  http://localhost:9000/updateCustomerBalance \
  -H 'content-type: application/json' \
  -d '{
	"sourceCustomerId" : "100001",
	"targetCustomerId" : "100002",
	"amount" : "15",
	"currency" : "GBP"
}'
```

## Application logic

When a request to transfer money is received, the following checks will be carried out before proceeding:

- is source account locked?
- is target account locked?
- Is the currency we are trying to transfer absent from target account?
- does the source account have insufficient balance (logic on balance usage below)?
- has the daily transfer limit been reached by the source account?
- will the requested amount exceed the daily transfer limit?

If the answer to any of the above is yes, the transaction will not go through and the caller will receive a
json message saying the request was unsuccessful and outlining the reason(s).

#### Balance usage
The funds will come out of the account in the following order:
- take out as much as possible from requested currency
- use up overdraft
- go through other currencies and use up converted amounts one at a time until the requested amount is covered

#### Example:
```json
{
        "id": 100001,
        "firstName": "Lois",
        "lastName": "Griffin",
        "account": {
            "id": 1,
            "balances": [
                {
                    "currency": "GBP",
                    "amount": "100.00",
                    "id": 3
                },
                {
                    "currency": "USD",
                    "amount": "100.00",
                    "id": 4
                },
                {
                    "currency": "EUR",
                    "amount": "100.00",
                    "id": 5
                }
            ],
            "allowedOverdraft": "100.00",
            "usedOverdraft": "0.00",
            "locked": false,
            "accountLimit": {
                "transferLimit": "400.00",
                "transferredToday": "0.00",
                "id": 2,
                "limitReached": false
            },
            "remainingOverdraft": "100.00"
        }
    }
```

#####Scenario 1:
Sending 5 GBP - this will take 5 GBP from the GBP balance leaving 95 GBP and all other balances unchanged
#####Scenario 2:
Sending 105 GBP - this will take 100 GBP (max amount) from the GBP balance and then 5 from overdraft, leaving 0 GBP, and 95 available overdraft
#####Scenario 3:
Sending 205 GBP - this will take 100 GBP, use 100 from overdraft and take equivalent of 5 GBP from the USD balance, since this application is configured of 1 to 1 exchange rate for all currencies, this will leave 0 on GBP, 0 overdraft left and 95 USD

For simplicity, the overdraft is applied to the requested currency only.

##Future improvements
- make transfers and checks part of the same transaction, current flow checks the transfer is possible and then proceeds to complete it. If another operation altered the account in between these two operations, there will be problems.
- make the api responses contain relevant information if different currency was used and converted to complete the request
- implement configurable priority of currencies so that the order in which other currencies are used is dynamic
- when transferring to a target account, if the overdraft is used up, cover the overdraft first before increasing the currency balance
- make currency exchange rates configurable