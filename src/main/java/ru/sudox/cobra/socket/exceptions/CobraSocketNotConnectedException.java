package ru.sudox.cobra.socket.exceptions;

public final class CobraSocketNotConnectedException extends Exception {

    public CobraSocketNotConnectedException() {
        super("Socket not connected.");
    }
}
