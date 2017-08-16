package models.repositories;

import com.google.inject.ImplementedBy;
import models.repositories.impl.AbstractRepository;

import java.util.List;
import java.util.Optional;

@ImplementedBy(AbstractRepository.class)
public interface Repository<T, K> {

    T add(T toInsert);

    List<T> list();

    T update(T toUpdate);

    Optional<T> find(K primaryKey);

}
