package ru.sudox.cobra.socket.exceptions;

public class CobraSocketResolvingException extends Exception {

    public CobraSocketResolvingException() {
        super("Failed to resolve host.");
    }
}
