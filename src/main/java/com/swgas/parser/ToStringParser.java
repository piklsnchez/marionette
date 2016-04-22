package com.swgas.parser;

/**
 *
 * @author ocstest
 */
@FunctionalInterface
public interface ToStringParser {
    public <T>String parseTo(T t);
}
