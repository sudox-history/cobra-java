package ru.sudox.cobra.discovery.exceptions;

public final class CobraDiscoveryUnhandledException extends Exception {

    public CobraDiscoveryUnhandledException(int code) {
        super("Unhandled exception with code: " + code);
    }
}
