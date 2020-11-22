package ru.sudox.cobra.socket.exceptions;

public final class CobraSocketAlreadyConnectedException extends Exception {

    public CobraSocketAlreadyConnectedException() {
        super("Socket already connected.");
    }
}
