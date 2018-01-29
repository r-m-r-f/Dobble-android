package dobbleproject.dobble;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketWrapper {
    Socket socket;
    BufferedReader reader = null;
    BufferedWriter writer = null;

    public SocketWrapper(Socket socket) {
        this.socket = socket;
    }

    public synchronized BufferedReader getReader() {
        if(reader == null) {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return reader;
    }

    public synchronized BufferedWriter getWriter() {
        if(writer == null) {
            try {
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return writer;
    }

    public synchronized int getLocalPort() {
        return socket.getLocalPort();
    }

    public synchronized void connect(InetSocketAddress address) throws IOException {
        socket.connect(address);
    }

    public synchronized void connect(InetSocketAddress address, int timeout) throws IOException {
        socket.connect(address, timeout);
    }

    public synchronized boolean isClosed() {
        return socket.isClosed();
    }

    public synchronized InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    public synchronized void close() throws IOException {
        if(!socket.isClosed()) {
            socket.close();
        }
    }

}
