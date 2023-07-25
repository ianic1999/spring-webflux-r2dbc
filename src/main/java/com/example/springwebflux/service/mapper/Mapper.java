package com.example.springwebflux.service.mapper;

import java.util.List;
import java.util.stream.Collectors;

public interface Mapper<T, U> {
    U map(T entity);

    default List<U> mapList(List<T> entityList) {
        return entityList.stream()
                         .map(this::map)
                         .collect(Collectors.toList());
    }
}
