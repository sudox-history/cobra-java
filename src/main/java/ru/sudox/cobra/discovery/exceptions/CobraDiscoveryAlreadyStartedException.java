package ru.sudox.cobra.discovery.exceptions;

public final class CobraDiscoveryAlreadyStartedException extends Exception {

    public CobraDiscoveryAlreadyStartedException() {
        super("Discovery already started.");
    }
}
