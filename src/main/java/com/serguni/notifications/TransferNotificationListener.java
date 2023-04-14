package com.serguni.notifications;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serguni.models.SocketMessage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;

@ApplicationScoped
public class TransferNotificationListener {
    @Inject
    ObjectMapper om;

    @Inject
    Socket socket;


    /**
     * Отправка сообщения о переводе получателю
     */
    public void send(String userId, SocketMessage socketMessage) {
        Map<String, Session> sessions = Socket.SESSIONS.get(userId);

        socket.closeUnauthorizedSessions(userId);

        if (sessions == null) {
            return;
        }

        sessions.values().forEach(session -> {
            try {
                session
                        .getAsyncRemote()
                        .sendObject(
                                om.writeValueAsString(socketMessage),
                                result -> {
                                    if (result.getException() != null) {
                                        System.out.println("Unable to send message: " + result.getException());
                                    }
                                }
                        );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
