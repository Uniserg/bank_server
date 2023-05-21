package com.serguni.notifications;

import com.serguni.clients.KeycloakAdminClient;
import com.serguni.vars.KeycloakProps;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.impl.jose.JWT;
import org.jboss.logging.Logger;
import org.keycloak.representations.idm.UserSessionRepresentation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ApplicationScoped
@ServerEndpoint(value = "/notifications/{access_token}")
public class Socket {

    @Inject
    KeycloakAdminClient adminClient;
    @Inject
    KeycloakProps keycloakProps;

    public static final Map<String, Map<String, Session>> SESSIONS = new ConcurrentHashMap<>();
    Logger LOG = Logger.getLogger(Socket.class);

    @OnOpen
    @ActivateRequestContext
    public void onOpen(Session session, @PathParam("access_token") String accessToken) {
        JsonObject payload = JWT.parse(accessToken).getJsonObject("payload");

        String userId = payload.getString("sub");
        String sessionId = payload.getString("sid");

        session.getUserProperties().put("userId", userId);

        if (SESSIONS.containsKey(userId)) {
            SESSIONS
                    .get(userId)
                    .put(sessionId, session);
        } else {
            Map<String, Session> sessions = new HashMap<>();
            sessions.put(sessionId, session);
            SESSIONS.put(userId, sessions);
        }
    }

    @OnClose
    @ActivateRequestContext
    public void onClose(Session session) {
        String userId = (String) session.getUserProperties().get("userId");
        SESSIONS.get(userId).remove(session);

        // TODO: ВЫНЕСТИ В ОТДЕЛЬНУЮ ФУНКЦИЯ ПРОВЕРКУ НА ОТКЛЮЧЕНИЕ ВСЕХ СЕССИЙ И ВЫХОД И СЕТИ
        if (SESSIONS.get(userId).isEmpty()) {
            SESSIONS.remove(userId);
        }
    }

    @OnError
    @ActivateRequestContext
    public void onError(Session session, Throwable throwable) {
        System.out.println(throwable);
        LOG.error("onError", throwable);
    }

    public void closeUnauthorizedSessions(String userId) {

        Set<String> activeSessions = adminClient
                .getKeycloak()
                .realm(keycloakProps.realm())
                .users()
                .get(userId)
                .getUserSessions()
                .stream()
                .map(UserSessionRepresentation::getId).collect(Collectors.toSet());

        Map<String, Session> sessions = SESSIONS.get(userId);
        for (String sessionId : sessions.keySet()) {

            if (!activeSessions.contains(sessionId)) {

                try {
                    sessions.get(sessionId).close();
                    sessions.remove(sessionId);
                } catch (IOException ignored) {
                }
            }
        }
    }

}