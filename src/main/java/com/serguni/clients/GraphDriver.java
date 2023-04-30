package com.serguni.clients;

import com.serguni.vars.JanusGraphProps;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.driver.ser.GraphBinaryMessageSerializerV1;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

@Singleton
public class GraphDriver {

    private Cluster cluster;

    private GraphTraversalSource g;

    @Inject
    JanusGraphProps janusGraphProps;

    private Configuration getConfiguration() {
        return new MapConfiguration(
                Map.of(
                        "hosts", janusGraphProps.clusterHosts(),
                        "port", janusGraphProps.port(),
                        "serializer", Map.of(
                                "className", GraphBinaryMessageSerializerV1.class.getName(),
                                "config", Map.of("ioRegistries", List.of(JanusGraphIoRegistry.class.getName()))
                        )
                )
        );
    }

    private Cluster getCluster() {
        if (cluster.isClosed()) {
            this.cluster = Cluster.open(getConfiguration());
        }
        return this.cluster;
    }

    public GraphTraversalSource g() {
        if (g == null) {
            this.g = traversal().withRemote(
                    DriverRemoteConnection.using(getCluster(), "g")
            );
        }
        return g;
    }
}
