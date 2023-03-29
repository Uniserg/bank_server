package com.serguni.notifications;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.impl.jose.JWT;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint(value = "/notifications/{access_token}")
public class Socket {

//    @Inject
//    ChatOnlineListener chatOnlineListener;


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

//
//            // TODO: ВЫПИЛИТЬ
//            SocketMessage socketMessage = new SocketMessage();
//            socketMessage.setBody(SESSIONS.keySet());
//            socketMessage.setScope(SocketMessage.Scope.CHAT_SESSIONS);
//            chatOnlineListener.broadcast(socketMessage);

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

        // TODO: ВЫПИЛИТЬ
//        SocketMessage socketMessage = new SocketMessage();
//        socketMessage.setBody(SESSIONS.keySet());
//        socketMessage.setScope(SocketMessage.Scope.CHAT_SESSIONS);
//        chatOnlineListener.broadcast(socketMessage);
    }

    @OnError
    @ActivateRequestContext
    public void onError(Session session, Throwable throwable) {
//        if (SESSIONS.containsKey())
//        SESSIONS.remove((String) session.getUserProperties().get("userId"));
        System.out.println(throwable);
        LOG.error("onError", throwable);
    }

}