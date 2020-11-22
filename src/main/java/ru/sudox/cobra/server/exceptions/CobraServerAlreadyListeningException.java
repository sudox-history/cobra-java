package ru.sudox.cobra.server.exceptions;

public final class CobraServerAlreadyListeningException extends Exception {

    public CobraServerAlreadyListeningException() {
        super("Server already listening.");
    }
}
