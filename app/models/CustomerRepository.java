package models;

import com.google.inject.ImplementedBy;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(JPACustomerRepository.class)
public interface CustomerRepository
{
    Customer add(Customer customer);
    List<Customer> list();
    Customer update(Customer customer);
}
