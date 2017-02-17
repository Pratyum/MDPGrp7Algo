package mdp.communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketCommunicator {
    
    private static final String _HOST_NAME = "192.168.7.1";
    private static final int _PORT = 13377;
    
    private static final int _MAX_MSG_LENGTH = 10;
    private static final String _ENCODING = "ASCII";
    private static final int _ENCODING_BYTE_SIZE = 1;
    
    private SocketChannel _socketChannel;
    ByteBuffer _inBuffer;
    ByteBuffer _outBuffer;
    
    public SocketCommunicator() throws IOException {
        _socketChannel = SocketChannel.open();
        _socketChannel.connect(new InetSocketAddress(_HOST_NAME, _PORT));
        _inBuffer = ByteBuffer.allocate(_ENCODING_BYTE_SIZE * _MAX_MSG_LENGTH);
        _outBuffer = ByteBuffer.allocate(_ENCODING_BYTE_SIZE * _MAX_MSG_LENGTH);
    }
    
    public String read() throws IOException {
        _inBuffer.clear();
        _socketChannel.read(_inBuffer);
        return new String(_inBuffer.array(), _ENCODING);
    }
    
    public void echo(String message) throws IOException {
        _outBuffer.clear();
        _outBuffer.asCharBuffer().put(message);
        _socketChannel.write(_outBuffer);
    }
    
    public void closeSocketChannel() throws IOException {
        _socketChannel.close();
    }
}
