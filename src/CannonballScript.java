import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;
import java.sql.*;

import java.util.function.BooleanSupplier;

@ScriptManifest(name = "CannonBalls", author = "Iownreality1", info = "Smelts Cannon Balls", version = 0.1, logo = "")
public final class CannonballScript extends Script
{
    /*TO DO LIST
    CREATE A SECOND BOT TO ACCEPT TRADES FROM MULE
    USE TELEPORTATION JEWELERY
    CATCH IF BANK WASN'T OPENED
    FIND OTHER BUGS
    IF ITEMS DON'T BUY OR SELL.
    NEXT BOT IDEA: QUESTS. FOR DS2/REGICIDE, NMZ BOT, CHINNING/BURSTING BOT
     */
    private final Area GeArea = new Area(3159,3482,3168,3492);
    private final Area BankArea = new Area(3095,3495,3097,3497);
    private final Position FurnacePosition = new Position(3109,3499,0);
    private final Position BankPosition = new Position(3096,3494,0);
    private int currentCoins;
    @Override
    public final int onLoop() throws InterruptedException
    {
        if (getSettings().getRunEnergy() > 20 && !getSettings().isRunning()) getSettings().setRunning(true);
        if (getInventory().contains("Steel Bar"))
        {
            smith();
        }
        else
        {
            bank("Steel Bar");
        }

        return random(1200, 1800);
    }

    public final void smith() throws InterruptedException {
        boolean isFinished = false;
        while (!isFinished)
        {
            getWalking().webWalk(FurnacePosition);
            log("Moving to Furnace");
            sleep(random(1200,1800));
            getObjects().closest("Furnace").interact("Smelt");
            log("Interact With Furnace");
            Sleep.sleepUntil(() -> getWidgets().getWidgetContainingText("How many bars would you like to smith?") != null, 10000);
            sleep(random(1200,1800));
            log("Sleep Until Widget");
            RS2Widget cannonBall = getWidgets().get(270, 14);
            cannonBall.interact("Make sets:");
            sleep(random(3000,3600));
            log("Interact with Widget to make cballs");
            log("Waiting for cballs to finish");
            while (getInventory().contains("Steel Bar") && !getDialogues().isPendingContinuation())
            {
                sleep(random(1200,1800));
            }
            if (!getInventory().contains("Steel Bar")) isFinished = true;
        }

    }

    public final void bank(String item) throws InterruptedException {
        getWalking().webWalk(BankPosition);
        log("Moving to Bank");
        sleep(random(1200,1800));
        getNpcs().closest("Banker").interact("Bank");
        log("Moving to Bank");
        Sleep.sleepUntil(() -> getBank().isOpen(), 30000);
        sleep(random(1200,1800));
        log("Waiting for bank to open");
        if (getBank().isOpen() && !getBank().contains(item) && item != "Coins")
        {
            bank.withdrawAll("Coins");
            sleep(random(1200,1800));
            AreaWalker(GeArea);

            Resupply();
            AreaWalker(BankArea);
        }
        else if (!getBank().isOpen())
        {
            log("Bank did not open");
        }
        else
        {
            //getBank().withdrawAll(item);
            getBank().withdrawAll("Steel Bar");
            sleep(random(300,800));
            log("Withdrawing "+ item);
            Sleep.sleepUntil(() -> (getInventory().contains(item)), 2000);
            log("Waiting for "+ item + " to be in inventory");
            if (getInventory().contains(item) && getBank().isOpen())
            {
                getBank().close();
                log("Closing Bank");
            }
        }

    }
    public final void AreaWalker(Area myArea) throws InterruptedException
    {
        if (myArea == BankArea) log("Moving to Edge");
        else log("Moving to GE");
        while(!myArea.contains(myPosition()))
        {
            getWalking().webWalk(myArea);

            sleep(random(1200,1800));
        }
    }

    private final void Resupply() throws InterruptedException {
        boolean cannonBallsSold = false;
        boolean steelBarsBought = false;
        while (!cannonBallsSold)
        {
            if (!getGrandExchange().isOpen())
            {
                getObjects().closest("Grand Exchange Booth").interact("Exchange");
                log("Opening GE");
                sleep(random(1200, 1800));
            }
            else
            {
                int cannonBallAmount = (int) getInventory().getAmount("Cannonball");
                getGrandExchange().sellItem(2, 200, cannonBallAmount);
                log("Selling Cannonballs");
                sleep(random(4000, 5000));
                if (widgets.get(465,6,0).isVisible())
                {
                    getGrandExchange().collect();
                    log("Collecting GE");
                    SqlUpdater((int)inventory.getAmount("Coins"));
                    sleep(random(1200, 1800));
                    if (getInventory().contains("Coins"))
                    {
                        cannonBallsSold = true;
                        int totalCoins = (int)getInventory().getAmount("Coins");
                        if (totalCoins > 6000000)
                        {
                            TradeMule(totalCoins);
                        }
                    }
                }

            }
        }
        while (!steelBarsBought)
        {
            if (!getGrandExchange().isOpen())
            {
                getObjects().closest("Grand Exchange Booth").interact("Exchange");
                log("Opening GE");
                sleep(random(1200, 1800));
            }
            else
            {
                int steelBarAmount = ((int) getInventory().getAmount("Coins"))/450;
                getGrandExchange().buyItem(2353,"Steel Bar", 450, steelBarAmount);
                log("Buying Steel Bars");
                sleep(random(1200, 1800));
                if (!getInventory().contains("Steel Bar"))
                {
                    getGrandExchange().collect();
                    log("Collecting GE");
                    sleep(random(1200, 1800));
                    if (getInventory().contains("Steel Bar"))
                    {
                        getGrandExchange().close();
                        log("Closing GE");
                        steelBarsBought = true;
                        sleep(random(1200, 1800));
                        getNpcs().closest("Banker").interact("Bank");
                        log("Opening Bank");
                        sleep(random(1200, 1800));
                        getBank().depositAll(2354);
                        log("Deposit Steel Bar");
                        sleep(random(1200, 1800));
                        getBank().depositAll(995);
                        log("Deposit Coins");
                        sleep(random(1200, 1800));
                        getBank().close();
                        log("Closing Bank");
                        sleep(random(1200, 1800));
                    }

                }
            }
        }
    }
    private void SqlUpdater(int coins)
    {
        Connection conn = null;
        try {

            String update = "UPDATE dbo.SmithingMules SET currentMoney = " + coins + " WHERE accountName = " + myPlayer().getName();
            String url = "jdbc:sqlserver://scriptingsqlautomation.database.windows.net;" +
                    "database=Scripting;" +
                    "user=AutomationScripters@scriptingsqlautomation;" +
                    "password=B4R86vsKM4hkdHw;" +
                    "encrypt=true;trustServerCertificate=false;" +
                    "hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            conn = DriverManager.getConnection(url); //connects to the database
            Statement statement = conn.createStatement(); //creates what database needs to do
            statement.executeUpdate(update); //actually does what database needs to do

        } catch (Exception e) {
            System.out.println("test-1" + e);
        }
    }
    private void TradeMule (int totalCoins) throws InterruptedException {
        String muleName = "aceuptheslee";
        boolean tradeComplete = false;
        if(getPlayers().closest(muleName) != null)
        {
            while (!tradeComplete)
            {
                getPlayers().closest(muleName).interact("Trade with");
                log("Sending Trade");
                sleep(random(2000, 2200));
                if (trade.isCurrentlyTrading())
                {
                    if (trade.isFirstInterfaceOpen())
                    {
                        trade.offer("Coins", totalCoins-1000000);
                        log("Trading Gold");
                        sleep(random(2000, 2200));
                        trade.acceptTrade();
                        sleep(random(2000, 2200));
                    }
                    if (trade.isSecondInterfaceOpen())
                    {
                        if (inventory.getAmount("Coins") == 1000000 && trade.getOurOffers().contains("Coins") && trade.getOtherPlayer().equals(muleName))
                        {
                            trade.acceptTrade();
                            sleep(random(2000, 2200));
                            tradeComplete = true;
                        }
                        else
                        {
                            trade.declineTrade();
                        }
                    }
                }
            }
        }
    }



}


class Sleep extends ConditionalSleep
{

    private final BooleanSupplier condition;

    public Sleep(final BooleanSupplier condition, final int timeout) {
        super(timeout);
        this.condition = condition;
    }

    @Override
    public final boolean condition() throws InterruptedException {
        return condition.getAsBoolean();
    }

    public static boolean sleepUntil(final BooleanSupplier condition, final int timeout) {
        return new Sleep(condition, timeout).sleep();
    }
}



