package com.bestarmedia.migration.misc;

import java.util.List;

@FunctionalInterface
public interface PaginationFunction<T, R> {

    List<R> execute(List<T> t);
}
