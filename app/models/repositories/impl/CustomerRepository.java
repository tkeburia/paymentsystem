package models.repositories.impl;

import models.Customer;
import models.DatabaseExecutionContext;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class CustomerRepository extends AbstractRepository<Customer, Long> {

    @Inject
    public CustomerRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    @Override
    public List<Customer> list() {
        return getJpaApi().withTransaction(
                em -> em.createQuery("select c from Customer c", Customer.class).getResultList()
        );
    }

    @Override
    public Optional<Customer> find(Long primaryKey) {
        final Customer value = getJpaApi().withTransaction(em -> em.find(Customer.class, primaryKey));
        if (value == null) return Optional.empty();
        return Optional.of(value);
    }
}
