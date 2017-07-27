package com.vseminar.data.model;

import java.util.List;

/**
 * Created by enosent on 2017. 7. 13..
 */
public interface VSeminarData<T> {

    T findOne(long id);
    List<T> findAll();
    int count();
    T save(T entity);
    void delete(long id);

}
