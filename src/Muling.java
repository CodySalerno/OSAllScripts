import org.osbot.rs07.api.Client;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import util.EchoClient;
import util.Sleep;
import util.UsefulAreas;

import java.io.IOException;

@ScriptManifest(name = "Muling", author = "Iownreality1", info = "Logs Mule as needed", version = 0.1, logo = "")
public final class Muling extends Script
{
    EchoClient client1 = new EchoClient();
    public boolean needToTrade = false;
    Thread thread1 = new Thread();

    @Override
    public void onStart()
    {

    }
    @Override
    public int onLoop() throws InterruptedException
    {
        log("Starting onLoop: Login State = " + client.getGameState());
        try
        {
            //log("Sending ?");
            client1.startConnection("127.0.0.1", 6666);
            String s = client1.sendMessageReturn("?");
            //log("s = " + s);
            if (s.equals("Yes"))
            {
                needToTrade = true;
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        if (client.getGameState() == Client.GameState.LOGGED_OUT && needToTrade)
        {
            areYouLoggedIn();
        }
        if (client.getGameState() == Client.GameState.LOGGED_IN && widgets.get(378,73) != null)
        {
            widgets.get(378,73).interact();
        }
        if (areYouLoggedIn() && needToTrade && widgets.get(378,73) == null)
        {
            try
            {
                log("trading main");
                TradeMain();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        if (!needToTrade && client.getGameState() == Client.GameState.LOGGED_IN)
        {
            logoutTab.open();
            sleep(random(10000,12000));
            logoutTab.logOut();
        }
        //log("sleeping 40-50");
        return(random(1000,1500));
    }

    private void loginToAccount(String username, String password)
    {

        LoginEvent loginEvent = new LoginEvent(username, password);
        getBot().addLoginListener(loginEvent);
        execute(loginEvent);
    }

    private boolean areYouLoggedIn() throws InterruptedException
    {
        //log("does banker have action " + client.getLoginState());
        if (needToTrade && client.getGameState() == Client.GameState.LOGGED_OUT)
        {
            log("going to log in now");
            loginToAccount("Fierceskunk+1@yahoo.com","applepieislife1600");
            log("sleeping");
            Sleep.sleepUntil(() -> client.getGameState() == Client.GameState.LOGGED_IN, 15000);
        }
        if (client.getGameState() == Client.GameState.LOGGED_IN)
        {
            return true;
        }
        else return false;
    }

    private void TradeMain() throws InterruptedException, IOException {
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
                        client1.sendMessage("Finished");
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
