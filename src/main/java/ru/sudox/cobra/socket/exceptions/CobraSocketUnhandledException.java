package ru.sudox.cobra.socket.exceptions;

public final class CobraSocketUnhandledException extends Exception {

    public CobraSocketUnhandledException(int code) {
        super("Unhandled exception with code: " + code);
    }
}
