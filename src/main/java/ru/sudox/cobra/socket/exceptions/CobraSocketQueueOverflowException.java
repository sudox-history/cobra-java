package ru.sudox.cobra.socket.exceptions;

public class CobraSocketQueueOverflowException extends Exception {

    public CobraSocketQueueOverflowException() {
        super("Queue overflowed.");
    }
}
