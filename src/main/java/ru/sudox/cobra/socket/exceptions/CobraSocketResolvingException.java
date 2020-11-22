package ru.sudox.cobra.socket.exceptions;

public final class CobraSocketResolvingException extends Exception {

    public CobraSocketResolvingException() {
        super("Failed to resolve host.");
    }
}
