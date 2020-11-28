package ru.sudox.cobra.discovery;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CobraDiscoveryListener {
    void onFound(@NotNull CobraDiscovery discovery, @NotNull String host);

    void onClose(@NotNull CobraDiscovery discovery, @Nullable Exception exception);
}
