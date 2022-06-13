import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import java.io.File;
import java.io.IOException;
import java.io.*;

@ScriptManifest(name = "Muling", author = "Iownreality1", info = "Logs Mule as needed", version = 0.1, logo = "")
public final class Muling extends Script
{
    private LoginEvent loginEvent;
    boolean loggingIn = false;
    static boolean needToTrade = false;

    @Override
    public final int onLoop() throws InterruptedException
    {
        log("Starting onLoop: Login State = " + client.getLoginUIState());
        if (!loggingIn && client.getLoginUIState() == 0)
        {
            CheckGoldAmounts();
        }
        if (client.getLoginUIState() == 1)
        {

        }
        if (client.getLoginUIState() == 2)
        {
            CheckGoldAmounts();
            TradeMain();
        }
        if (needToTrade = false && client.getLoginUIState() == 2);
        {
            logoutTab.open();
            sleep(random(10000,12000));
            logoutTab.logOut();
        }
        log("sleeping 40-50");
        return(random(20000,30000));
    }

    private void loginToAccount(String username, String password) {

        loginEvent = new LoginEvent(username, password);
        getBot().addLoginListener(loginEvent);
        execute(loginEvent);
    }

    private void CheckGoldAmounts() throws InterruptedException {
        loggingIn = true;
        log("still working?");
        File file = new File("C:\\Users\\zjmnk\\OSBot\\Data\\NeedsTrade.txt");
        try (FileReader fr = new FileReader(file))
        {
            char[] chars = new char[(int) file.length()];
            fr.read(chars);
            String fileContent = new String(chars);
            needToTrade = fileContent.equals("T");
            log("need to trade" + needToTrade);
        }
        catch (IOException e) {
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