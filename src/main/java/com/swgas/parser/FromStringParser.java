package com.swgas.parser;

/**
 *
 * @author ocstest
 */
@FunctionalInterface
public interface FromStringParser {
    public <T> T parseFrom(String s);
    public static final FromStringParser DEFAULT = new FromStringParser() {
        @Override
        public <T> T parseFrom(String s) {
            return (T)s;
        }
    };
}
