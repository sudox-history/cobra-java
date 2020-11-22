import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CobraServer {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(5555);
        Socket socket = server.accept();

        while (socket.isConnected()) {
            int available = socket.getInputStream().available();
            byte[] bytes = new byte[available];
            int received = socket.getInputStream().read(bytes);

            if (received > 0) {
                System.out.println("[SERVER] Received: " + received);
            }
        }
    }
}
