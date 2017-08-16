package models;

import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class JPACustomerRepository implements CustomerRepository
{
    private JPAApi jpaApi;
    private DatabaseExecutionContext executionContext;

    @Inject
    public JPACustomerRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }


    @Override
    @Transactional
    public Customer add(Customer customer)
    {
        jpaApi.em().persist(customer);
        return customer;
    }

    @Override
    @Transactional
    public List<Customer> list()
    {
        return (List<Customer>) jpaApi.em().createQuery("SELECT c FROM Customer c").getResultList();
    }

    @Override
    public Customer update(Customer customer)
    {
        return jpaApi.em().merge(customer);
    }
}
