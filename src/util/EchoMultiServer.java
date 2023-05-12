package util;

import javax.management.remote.rmi._RMIConnection_Stub;
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
        System.out.println(needsToTrade);
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
                    System.out.println("input line: " + inputLine);
                    if (".".equals(inputLine))
                    {
                        break;
                    }
                    else if ("Trade".equals(inputLine))
                    {
                        System.out.println("Setting needsToTrade to true:");
                        needsToTrade = true;
                    }
                    else if ("?".equals(inputLine) && needsToTrade)
                    {
                        System.out.println("Sent ? returning Yes");
                        System.out.println(needsToTrade);
                        out.println("Yes");
                    }
                    else if ("?".equals(inputLine) && !needsToTrade)
                    {
                        System.out.println("Sent ? returning No");
                        out.println("No");
                    }
                    else if ("Finished".equals(inputLine))
                    {
                        needsToTrade = false;
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

}
