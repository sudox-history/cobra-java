import ru.sudox.cobra.server.CobraServer;
import ru.sudox.cobra.server.CobraServerListener;
import ru.sudox.cobra.server.exceptions.CobraServerAlreadyListeningException;
import ru.sudox.cobra.server.exceptions.CobraServerUnhandledException;
import ru.sudox.cobra.socket.CobraSocket;
import ru.sudox.cobra.socket.exceptions.CobraSocketNotConnectedException;
import ru.sudox.cobra.socket.exceptions.CobraSocketQueueOverflowException;
import ru.sudox.cobra.socket.exceptions.CobraSocketUnhandledException;
import ru.sudox.cobra.socket.exceptions.CobraSocketWritingException;

import java.nio.ByteBuffer;

public class CobraMain implements CobraServerListener {

    public static void main(String[] args) {
        CobraServer server = new CobraServer(65535);

        try {
            server.setListener(new CobraMain());
            server.listen("127.0.0.1", "5555");
        } catch (CobraServerAlreadyListeningException | CobraServerUnhandledException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionOpen(CobraSocket socket) {
        System.out.println("Connection opened!");

        try {
            socket.send(ByteBuffer.allocateDirect(4).putInt(55));
        } catch (CobraSocketNotConnectedException | CobraSocketUnhandledException | CobraSocketQueueOverflowException | CobraSocketWritingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionDrain(CobraSocket socket) {

    }

    @Override
    public void onConnectionData(CobraSocket socket, ByteBuffer buffer) {
        System.out.println(buffer.capacity());
    }

    @Override
    public void onConnectionClose(CobraSocket socket, Exception exception) {
        System.out.println("Connection closed!");
    }

    @Override
    public void onServerClose(Exception exception) {
        exception.printStackTrace();
    }
}
