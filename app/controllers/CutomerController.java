package controllers;

import models.Customer;
import models.repositories.impl.CustomerRepository;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

import static java.lang.Long.valueOf;
import static java.lang.String.format;

public class CutomerController extends Controller {

    private final CustomerRepository customerRepository;

    @Inject
    public CutomerController(CustomerRepository customerRepository)
    {
        this.customerRepository = customerRepository;
    }

    public Result getCustomer(String id) {
        final Customer customer = customerRepository
                .find(valueOf(id))
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                format("Customer with id %s not found", id)
                        )
                );
        if (customer == null) return notFound();
        return ok(Json.toJson(customer));
    }

    public Result listCustomers() {
        return ok(Json.toJson(customerRepository.list()));
    }
}
