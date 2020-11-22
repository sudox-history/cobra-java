package ru.sudox.cobra.socket.exceptions;

public final class CobraSocketWritingException extends Exception {

    public CobraSocketWritingException() {
        super("Failed to write data.");
    }
}
