package com.serguni.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SocketMessage implements Serializable {
    public enum Scope {
        NOTIFICATION,
        MESSAGE,
        CHAT_SESSIONS
    }

    private Scope scope;
    private Object body;
}
