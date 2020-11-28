package ru.sudox.cobra.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.sudox.cobra.socket.CobraSocket;

import java.nio.ByteBuffer;

public interface CobraServerListener {
    void onConnectionOpen(@NotNull CobraServer server, @NotNull CobraSocket socket);

    void onConnectionDrain(@NotNull CobraServer server, @NotNull CobraSocket socket);

    void onConnectionData(@NotNull CobraServer server, @NotNull CobraSocket socket, @NotNull ByteBuffer buffer);

    void onConnectionClose(@NotNull CobraServer server, @NotNull CobraSocket socket, @Nullable Exception exception);

    void onServerClose(@NotNull CobraServer server, @Nullable Exception exception);
}
