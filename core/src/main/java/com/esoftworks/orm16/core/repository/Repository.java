package com.esoftworks.orm16.core.repository;

import java.util.Optional;

public interface Repository<T, PK> {

    Optional<T> get(PK key);

    T add(T value);

    boolean remove(PK key);

    T update(T value);

}
