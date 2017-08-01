package com.swgas.rest;

public interface Jsonable<T> {
    public String toJson();
    public T      fromJson(String json);
}
