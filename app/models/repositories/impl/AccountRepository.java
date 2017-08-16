package models.repositories.impl;

import models.Account;
import models.Customer;
import models.DatabaseExecutionContext;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class AccountRepository extends AbstractRepository<Account, Long> {

    @Inject
    public AccountRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    @Override
    public List<Account> list() {
        return getJpaApi().withTransaction(
                em -> em.createQuery("select a from Account a", Account.class).getResultList()
        );
    }

    @Override
    public Optional<Account> find(Long primaryKey) {
        final Account value = getJpaApi().withTransaction(em -> em.find(Account.class, primaryKey));
        if (value == null) return Optional.empty();
        return Optional.of(value);
    }
}
