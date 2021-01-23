package ru.sudox.cobra.server;

import org.jetbrains.annotations.NotNull;
import ru.sudox.cobra.socket.CobraSocket;
import ru.sudox.cobra.socket.CobraSocketError;

import java.nio.ByteBuffer;

public interface CobraServerListener {
    void onConnectionOpen(@NotNull CobraServer server, @NotNull CobraSocket socket);

    void onConnectionDrain(@NotNull CobraServer server, @NotNull CobraSocket socket);

    void onConnectionData(@NotNull CobraServer server, @NotNull CobraSocket socket, @NotNull ByteBuffer buffer);

    void onConnectionClose(@NotNull CobraServer server, @NotNull CobraSocket socket, @NotNull CobraSocketError error);

    void onServerClose(@NotNull CobraServer server, @NotNull CobraServerError error);
}
