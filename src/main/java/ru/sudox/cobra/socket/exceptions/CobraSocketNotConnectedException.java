package ru.sudox.cobra.socket.exceptions;

public class CobraSocketNotConnectedException extends Exception {

    public CobraSocketNotConnectedException() {
        super("Socket not connected.");
    }
}
