package com.serguni.config;


import com.serguni.exceptions.IndividualRegisteredAlready;
import com.serguni.exceptions.InvalidRegistrationForm;
import com.serguni.models.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.tinkerpop.gremlin.driver.Channelizer;
import org.apache.tinkerpop.gremlin.driver.ser.GraphBinaryMessageSerializerV1;
import org.apache.tinkerpop.gremlin.process.remote.RemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry;

import java.util.NoSuchElementException;


/**
 Нужен для native
 **/
@RegisterForReflection(targets = {
        Account.class,
        AccountRequisites.class,
        Individual.class,
        Product.class,
        ProductOrder.class,
        Profile.class,
        MyBank.class,
        Profile.class,
        DebitCard.class,
        Transfer.class,
        SocketMessage.class,
        TransferNotification.class,
        InvalidRegistrationForm.class,
        IndividualRegisteredAlready.class,
        NoSuchElementException.class,
        GraphBinaryMessageSerializerV1.class,
        JanusGraphIoRegistry.class,
        Channelizer.class,
        Channelizer.WebSocketChannelizer.class,
        Channelizer.AbstractChannelizer.class,
        RemoteConnection.class,
        GraphTraversalSource.class,
        EmptyGraph.class
})
public class ReflectionConfiguration {

}
