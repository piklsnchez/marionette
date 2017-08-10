package com.swgas.rest;

import java.io.Serializable;

public interface Jsonable<T> extends Serializable{
    public String toJson();
    public T      fromJson(String json);
}
