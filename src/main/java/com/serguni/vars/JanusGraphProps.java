package com.serguni.vars;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;

import java.util.List;

@StaticInitSafe
@ConfigMapping(prefix = "janusgraph-db")
public interface JanusGraphProps {
    List<String> clusterHosts();
    int port();

}
