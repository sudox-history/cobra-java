package ru.sudox.cobra.socket.exceptions;

public final class CobraSocketQueueOverflowException extends Exception {

    public CobraSocketQueueOverflowException() {
        super("Queue overflowed.");
    }
}
