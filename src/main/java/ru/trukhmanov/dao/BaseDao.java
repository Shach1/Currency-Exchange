package ru.trukhmanov.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDao<T>{
    List<T> getAll();

    Optional<T> insert(T t);
}
