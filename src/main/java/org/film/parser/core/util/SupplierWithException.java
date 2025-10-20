package org.film.parser.core.util;

@FunctionalInterface
public interface SupplierWithException<T> {
    T get() throws Exception;
}