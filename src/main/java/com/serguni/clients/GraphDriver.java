package com.serguni.clients;

//import com.serguni.vars.ConfigVars;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.driver.ser.GraphBinaryMessageSerializerV1;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

@Singleton
public class GraphDriver {

    Cluster cluster;

    private GraphTraversalSource g;

    public GraphTraversalSource g() {

        List<String> clusterHosts = List.of(System.getenv().get("CLUSTER_HOSTS").split(","));

        if (g == null || cluster.isClosed()) {
            try {
                Configuration config = new MapConfiguration(
                        Map.of(
                                "hosts", clusterHosts,
                                "port", 8182,
                                "serializer", Map.of(
                                        "className", GraphBinaryMessageSerializerV1.class.getName(),
                                        "config", Map.of("ioRegistries", List.of(JanusGraphIoRegistry.class.getName()))
                                )
                        )
                );
                this.cluster = Cluster.open(config);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            this.g = traversal().withRemote(
                    DriverRemoteConnection.using(cluster, "g")
            );
        }
        return g;
    }
}
