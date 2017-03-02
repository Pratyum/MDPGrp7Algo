package mdp.communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Timer;
import java.util.TimerTask;

public class SocketCommunicator {
    
    private static final String _HOST_NAME = "192.168.7.1";
    private static final int _PORT = 13377;
    
    private static final int _MAX_MSG_LENGTH = 10;
    private static final String _ENCODING = "ASCII";
    private static final int _ENCODING_BYTE_SIZE = 1;
    
    private SocketChannel _socketChannel;
    ByteBuffer _inBuffer;
    ByteBuffer _outBuffer;
    
    public SocketCommunicator(Runnable callback) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("Connecting...");
                    _socketChannel = SocketChannel.open();
                    _socketChannel.connect(new InetSocketAddress(_HOST_NAME, _PORT));
                    _inBuffer = ByteBuffer.allocate(8 * 30);
                    _outBuffer = ByteBuffer.allocate(1000);
                    timer.cancel();
                    System.out.println("Connected!");
                    callback.run();
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    System.out.println("Trying again in 5 second...");
                }
            }
        }, 0, 5000);
    }
    
    public String read() throws IOException {
        String str = "                    ";
        _inBuffer.clear();
        _inBuffer.asCharBuffer().put(str);
        _socketChannel.read(_inBuffer);
        String inputStr = new String(_inBuffer.array(), _ENCODING).trim();
        System.out.println("_inBuffer = " + inputStr + " (" + inputStr.length() + ")");
        return new String(_inBuffer.array(), _ENCODING).trim();
    }
    
    public void echo(String message) throws IOException {
        System.out.println("Sending out message: " + message);
        _outBuffer.clear();
        _outBuffer.asCharBuffer().put(message);
        _socketChannel.write(_outBuffer);
    }
    
    public void closeSocketChannel() throws IOException {
        _socketChannel.close();
    }
}
