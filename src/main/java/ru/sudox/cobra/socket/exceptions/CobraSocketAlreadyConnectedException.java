package ru.sudox.cobra.socket.exceptions;

public class CobraSocketAlreadyConnectedException extends Exception {

    public CobraSocketAlreadyConnectedException() {
        super("Socket already connected.");
    }
}
