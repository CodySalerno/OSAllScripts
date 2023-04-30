import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import util.EchoClient;
import util.EchoMultiServer;
import util.ScriptServer;

import java.io.File;
import java.io.IOException;
import java.io.*;
import java.util.Locale;

@ScriptManifest(name = "Muling", author = "Iownreality1", info = "Logs Mule as needed", version = 0.1, logo = "")
public final class Muling extends Script
{
    EchoClient client1 = new EchoClient();
    boolean loggingIn = false;
    public boolean needToTrade = false;
    Thread thread1 = new Thread();

    @Override
    public void onStart()
    {

    }
    @Override
    public int onLoop() throws InterruptedException
    {
        createThread();
        log("Starting onLoop: Login State = " + client.getLoginUIState());

        try
        {
            log("Sending ?");
            client1.startConnection("127.0.0.1", 6666);
            String s = client1.sendMessageReturn("?");
            log("s = " + s);
            if (s.equals("Yes"))
            {
                log("Returned yes time to trade.");
                needToTrade = true;
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        if (client.getLoginUIState() == 2 && needToTrade)
        {
            TradeMain();
        }
        if (!needToTrade && client.getLoginUIState() == 2)
        {
            logoutTab.open();
            sleep(random(10000,12000));
            logoutTab.logOut();
        }
        log("sleeping 40-50");
        return(random(2000,5000));
    }

    private void createThread()
    {
        thread1 = new Thread(() ->
        {
            while (getBot().getScriptExecutor().isRunning() && !getBot().getScriptExecutor().isPaused() && !getBot().getScriptExecutor().isSuspended())
            {
                /*
                if (ScriptServer.returnTrade())
                {
                    log("sever says to trade");
                }
                */

            }
        });
        thread1.start();
    }


    private void loginToAccount(String username, String password)
    {

        LoginEvent loginEvent = new LoginEvent(username, password);
        getBot().addLoginListener(loginEvent);
        execute(loginEvent);
    }

    private void CheckGoldAmounts() throws InterruptedException
    {
        loggingIn = true;
        log("still working?");
        File file = new File("C:\\Users\\zjmnk\\OSBot\\Data\\NeedsTrade.txt");
        try (FileReader fr = new FileReader(file))//TODO: Convince zack to remove
        {
            char[] chars = new char[(int) file.length()];
            fr.read(chars); //TODO: Convince zack to remove
            String fileContent = new String(chars);
            needToTrade = fileContent.equals("T");
            log("need to trade " + needToTrade);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        while (needToTrade && client.getLoginUIState() ==0)
        {
            log("going to log in now");
            loginToAccount("Fierceskunk+1@yahoo.com","applepieislife1600");
            log("sleeping");
            sleep(25000);
        }
        loggingIn = false;
    }

    private void TradeMain() throws InterruptedException
    {
        log("in trading main method");
        boolean tradeComplete = false;
        boolean tradeCancelled = false;
        log("entering not currently trading while loop");
        if (trade.getLastRequestingPlayer() != null)
        {
            String trader = trade.getLastRequestingPlayer().getName();
            log("Looking for trader: " + trader);
            getPlayers().closest(trader).interact("Trade with");
            log("Sleeping 20-25");
            sleep(random(8000,12000));
        }
        while (!tradeComplete)
        {
            log("im stuck in this loop");
            if (trade.isCurrentlyTrading())
            {
                if (trade.isFirstInterfaceOpen())
                {
                    log("If first trade is open, accept");
                    trade.acceptTrade();
                    sleep(random(8000,12000));
                }
                if (trade.isSecondInterfaceOpen())
                {
                    while(!trade.didOtherAcceptTrade())
                    {
                        sleep(random(1000,2000));
                        if(!trade.isCurrentlyTrading())
                        {
                            log("Trade was cancelled");
                            tradeCancelled = true;
                            break;
                        }
                    }
                    if (!tradeCancelled)
                    {
                        log("Other Played accepted im accepting.");
                        trade.acceptTrade();
                        tradeComplete = true;
                        needToTrade = false;
                        sleep(random(8000,12000));
                    }
                }
            }
            else
            {
                break;
            }
        }
    }

}
