package ru.sudox.cobra.discovery;

import org.jetbrains.annotations.NotNull;

public interface CobraDiscoveryListener {
    void onFound(@NotNull CobraDiscovery discovery, @NotNull String host);

    void onClose(@NotNull CobraDiscovery discovery, @NotNull CobraDiscoveryError error);
}
