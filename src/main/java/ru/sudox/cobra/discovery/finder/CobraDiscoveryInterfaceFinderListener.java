package ru.sudox.cobra.discovery.finder;

import org.jetbrains.annotations.NotNull;

public interface CobraDiscoveryInterfaceFinderListener {
    void onFound(@NotNull CobraDiscoveryInterfaceFinder finder, @NotNull String host);
}
