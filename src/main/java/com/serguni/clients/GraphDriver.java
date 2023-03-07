package com.serguni.clients;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import javax.inject.Singleton;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

@Singleton
public class GraphDriver {

    public final GraphTraversalSource g;

    public GraphDriver() throws Exception {
        this.g = traversal().withRemote("conf/remote-graph.properties");
    }
}
