package util;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class ScriptServer
{
    static EchoMultiServer server = new EchoMultiServer();

    public static void main(String[] args)
    {
        Thread thread1 = new Thread();
        if (thread1.getState() == Thread.State.TERMINATED || thread1.getState() == Thread.State.NEW)
        {
            thread1 = new Thread(() ->
            {
                try {
                    server.start(6666);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            );
            thread1.start();
        }
    }
}
