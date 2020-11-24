package ru.sudox.cobra.discovery;

public interface CobraDiscoveryListener {
    void onFound(CobraDiscovery discovery, String host);

    void onClose(CobraDiscovery discovery, Exception exception);
}
