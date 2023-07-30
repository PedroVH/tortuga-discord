package com.pedrovh.tortuga.discord.dao;

import com.pedrovh.tortuga.discord.config.Jsondb;
import io.jsondb.JsonDBTemplate;

import java.util.List;

public class DAO<E, T> {

    protected final Class<E> type;
    private final JsonDBTemplate jsondb;

    public DAO(Class<E> type) {
        this.type = type;
        this.jsondb = Jsondb.getJsonDB();
        if(!jsondb.collectionExists(type))
            jsondb.createCollection(type);
    }

    public boolean exists(T id) {
        return jsondb.findById(id, type) != null;
    }

    public List<E> findAll() {
        return jsondb.findAll(type);
    }

    public E findById(T id) {
        return jsondb.findById(id, type);
    }

    public E findOne(String query) {
        return jsondb.findOne(query, type);
    }

    public void insert(E pojo) {
        jsondb.insert(pojo);
    }

    public void save(E pojo) {
        jsondb.save(pojo, type);
    }

    public void remove(E pojo) {
        jsondb.remove(pojo, type);
    }

}
