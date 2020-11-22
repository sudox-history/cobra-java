package ru.sudox.cobra.socket.exceptions;

public final class CobraSocketConnectingException extends Exception {

    public CobraSocketConnectingException() {
        super("Failed to connect with host.");
    }
}
