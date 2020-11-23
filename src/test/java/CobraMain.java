import ru.sudox.cobra.socket.CobraSocket;
import ru.sudox.cobra.socket.CobraSocketListener;
import ru.sudox.cobra.socket.exceptions.CobraSocketAlreadyConnectedException;
import ru.sudox.cobra.socket.exceptions.CobraSocketUnhandledException;

import java.nio.ByteBuffer;

public class CobraMain {

    public static void main(String[] args) {
        CobraSocket socket = new CobraSocket(65535);

        socket.setListener(new CobraSocketListener() {
            private ByteBuffer buffer = ByteBuffer.allocateDirect(65533);

            @Override
            public void onConnect() {

            }

            @Override
            public void onData(ByteBuffer buffer) {
            }

            @Override
            public void onClose(Exception exception) {
                exception.printStackTrace();
            }
        });

        try {
            socket.connect("localhost", "5555");
        } catch (CobraSocketAlreadyConnectedException | CobraSocketUnhandledException e) {
            e.printStackTrace();
        }
    }
}
