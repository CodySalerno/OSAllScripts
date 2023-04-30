package util;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class ScriptServer
{
    public static boolean needsTrade;
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


    public void startServer() throws IOException
    {
        server.start(6666);
    }

    public static boolean setTrade()
    {
        needsTrade = server.returnTrade();
        System.out.println(needsTrade);
        System.out.print(server.toString());
        return needsTrade;
    }

    public static String returnTrade()
    {
        System.out.println("FROM BOT: " + needsTrade);
        System.out.print("FROM BOT: " + server.toString());
        return "FROM BOT: " + server.toString();
    }

}
