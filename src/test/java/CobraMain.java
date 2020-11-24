import ru.sudox.cobra.discovery.CobraDiscovery;
import ru.sudox.cobra.discovery.CobraDiscoveryListener;
import ru.sudox.cobra.discovery.exceptions.*;

public class CobraMain {

    public static void main(String[] args) throws CobraDiscoveryUnhandledException, CobraDiscoveryAlreadyStartedException, CobraDiscoveryJoiningGroupException, CobraDiscoveryBindingException, CobraDiscoverySendingException {
        CobraDiscovery discovery = new CobraDiscovery();

        discovery.setListener(new CobraDiscoveryListener() {
            @Override
            public void onFound(CobraDiscovery discovery, String host) {
                System.out.println("Found device: " + host);
            }

            @Override
            public void onClose(CobraDiscovery discovery, Exception exception) {
                System.out.println("Discovery closed");
            }
        });

        discovery.listen();
    }
}
