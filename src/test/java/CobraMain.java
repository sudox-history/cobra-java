import ru.sudox.cobra.socket.CobraSocket;
import ru.sudox.cobra.socket.CobraSocketListener;
import ru.sudox.cobra.socket.exceptions.CobraSocketAlreadyConnectedException;
import ru.sudox.cobra.socket.exceptions.CobraSocketNotConnectedException;
import ru.sudox.cobra.socket.exceptions.CobraSocketQueueOverflowException;
import ru.sudox.cobra.socket.exceptions.CobraSocketUnhandledException;

import java.nio.ByteBuffer;

public class CobraMain {

    public static void main(String[] args) {
        CobraSocket socket = new CobraSocket(65535);

        socket.setListener(new CobraSocketListener() {
            @Override
            public void onConnect() {
                ByteBuffer buffer = ByteBuffer.allocateDirect(65533);

                for (;;) {
                    try {
                        socket.send(buffer);
                    } catch (CobraSocketNotConnectedException | CobraSocketUnhandledException e) {
                        e.printStackTrace();
                    }
                }
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
