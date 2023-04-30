package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoMultiServer
{
    public static boolean needsToTrade = false;
    private ServerSocket serverSocket;

    public void start(int port) throws IOException
    {
        serverSocket = new ServerSocket(port);
        while (true)
            new EchoClientHandler(serverSocket.accept()).start();
    }

    public void stop() throws IOException
    {
        serverSocket.close();
    }

    private static class EchoClientHandler extends Thread
    {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        public boolean needsTrade = false;

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try
            {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (".".equals(inputLine))
                    {
                        break;
                    }
                    if ("Trade".equals(inputLine));
                    {
                        needsToTrade = true;
                        ScriptServer.setTrade();
                    }
                }

                in.close();
                out.close();
                clientSocket.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public boolean returnTrade()
    {
        return needsToTrade;
    }
    public void setTrade()
    {
        needsToTrade = false;
    }
}
