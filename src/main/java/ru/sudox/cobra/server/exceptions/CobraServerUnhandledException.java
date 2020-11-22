package ru.sudox.cobra.server.exceptions;

public final class CobraServerUnhandledException extends Exception {

    public CobraServerUnhandledException(int code) {
        super("Unhandled exception with code: " + code);
    }
}
