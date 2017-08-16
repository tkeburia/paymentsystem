package controllers;

import models.Customer;
import models.CustomerRepository;
import play.mvc.*;

import javax.inject.Inject;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private final CustomerRepository customerRepository;

    @Inject
    public HomeController(CustomerRepository customerRepository)
    {
        this.customerRepository = customerRepository;
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        customerRepository.add(new Customer(1l, "aa", "bb"));
        return ok(customerRepository.list().toString());
    }

}
