package com.serguni.repositories;

import com.serguni.clients.GraphDriver;

import javax.inject.Inject;

public abstract class AbstractRepository {
    @Inject
    GraphDriver gd;
}
