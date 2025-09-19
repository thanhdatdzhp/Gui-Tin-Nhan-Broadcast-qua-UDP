import java.net.InetAddress;
import java.net.UnknownHostException;

public final class Config {
    private Config(){}

    public static final String DEFAULT_BROADCAST_IP = "255.255.255.255";
    public static final int DEFAULT_PORT = 50000;
    public static final int DEFAULT_INTERVAL_MS = 1000;
    public static final int UDP_BUFFER_SIZE = 8192;
    public static final int SOCKET_TIMEOUT_MS = 1000;

    public static InetAddress toAddress(String ip){
        try { return InetAddress.getByName(ip); }
        catch (UnknownHostException e){ throw new RuntimeException("IP không hợp lệ: "+ip, e); }
    }
}
