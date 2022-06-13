import Util.Sleep;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
    public final void onStart() throws InterruptedException
    {
        //HashMap<String, String> usernames = new HashMap<String, String>();
        //usernames.put("upmysleeve12", "Fierceskunk+2@yahoo.com");

    }

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
                    sleep(random(1200, 1800));
                    if (getInventory().contains("Coins"))
                    {
                        cannonBallsSold = true;
                        int totalCoins = (int)getInventory().getAmount("Coins");
                        try {
                            String s = myPlayer().getName() + "\n" + String.valueOf(totalCoins) + "\n";
                            Files.write(Paths.get("C:\\Users\\zjmnk\\OSBot\\Data\\Bot1CurrentGold.txt"), s.getBytes(), StandardOpenOption.APPEND);
                            log("updating file");
                        }catch (IOException e) {}
                        if (totalCoins > 6000000) TradeMule(totalCoins);
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
                int steelBarAmount = ((int) getInventory().getAmount("Coins"))/475;
                getGrandExchange().buyItem(2353,"Steel Bar", 475, steelBarAmount);
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
    private void TradeMule (int totalCoins) throws InterruptedException {
        while (totalCoins > 6000000)
        {
            File file = new File("C:\\Users\\zjmnk\\OSBot\\Data\\NeedsTrade.txt");
            FileWriter myWriter = null;
            try {
                myWriter = new FileWriter(file);
                myWriter.write("T");
                myWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log("looking to trade mule.");
            String muleName = "aceuptheslee";
            boolean tradeComplete = false;
            if(getPlayers().closest(muleName) != null)
            {
                while (!tradeComplete)
                {
                    log("while trade is not complete");
                    getPlayers().closest(muleName).interact("Trade with");
                    log("Sending Trade");
                    sleep(random(8000,12000));
                    if (trade.isCurrentlyTrading())
                    {
                        if (trade.isFirstInterfaceOpen())
                        {
                            trade.offer("Coins", totalCoins-1000000);
                            log("Trading Gold First Screen");
                            sleep(random(8000,12000));
                            trade.acceptTrade();
                            sleep(random(8000,12000));
                        }
                        if (trade.isSecondInterfaceOpen())
                        {
                            if (inventory.getAmount("Coins") == 1000000 && trade.getOurOffers().contains("Coins") && trade.getOtherPlayer().equals(muleName))
                            {
                                log("Accept second trade");
                                trade.acceptTrade();
                                totalCoins = (int)getInventory().getAmount("Coins");
                                tradeComplete = true;
                                log("Trade complete = true break while loop");
                                sleep(random(8000,12000));

                            }
                            else
                            {
                                trade.declineTrade();
                            }
                        }
                    }
                }
            }
            else
            {
                sleep(8000);
            }
        }
        File file = new File("C:\\Users\\zjmnk\\OSBot\\Data\\NeedsTrade.txt");
        FileWriter myWriter = null;
        try {
            log("Gold is less then 6M changing file to F");
            myWriter = new FileWriter(file);
            myWriter.write("F");
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
