package models.repositories.impl;

import lombok.Getter;
import models.DatabaseExecutionContext;
import models.repositories.Repository;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import static lombok.AccessLevel.PROTECTED;

@Getter(PROTECTED)
public abstract class AbstractRepository<T, K> implements Repository<T, K> {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public AbstractRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public T add(T toInsert) {
        jpaApi.withTransaction(em -> insert(em, toInsert));
        return toInsert;
    }

    @Override
    public T update(T customer) {
        return jpaApi.withTransaction(em -> em.merge(customer));
    }

    private T insert(EntityManager em, T customer) {
        em.persist(customer);
        return customer;
    }
}
