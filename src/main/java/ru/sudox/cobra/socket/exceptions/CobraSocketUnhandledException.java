package ru.sudox.cobra.socket.exceptions;

public class CobraSocketUnhandledException extends Exception {

    public CobraSocketUnhandledException(int code) {
        super("Unhandled exception with code: " + code);
    }
}
