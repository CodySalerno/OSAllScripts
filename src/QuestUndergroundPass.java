import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import util.BetterWalk;
import util.EnergyCheck;
import util.Sleep;
import util.TalkCareful;

import java.util.ArrayList;

import static util.UsefulAreas.GeArea;

@ScriptManifest(name = "Underground Pass", author = "Iownreality1", info = "Underground Pass quest", version = 0.1, logo = "")
public class QuestUndergroundPass extends Script
{
    BetterWalk MyWeb = new BetterWalk(this);
    TalkCareful talker = new TalkCareful(this);
    EnergyCheck useStamina = new EnergyCheck(this);
    boolean supplied = false;
    final int[] supplyID = {};
    final String[] supplyName = {};
    final int[] supplyPrice = {};
    final int[] supplyQuantity = {};

    public void onStart()
    {
        //set planes
    }

    public int onLoop() throws InterruptedException
    {
        log("Please");

        if ((settings.getRunEnergy() < 20) || (!settings.isRunning()))
        {

            useStamina.Stamina();
        }
        int UndergroundPassProg =configs.get(161);
        log(UndergroundPassProg);
        switch (UndergroundPassProg)
        {
            case 0:
                for (int i = 0; i < supplyID.length; i++)
                {
                    if (!(inventory.getAmount(supplyID[i]) >= supplyQuantity[i]) && !supplied)
                    {
                        supplied = Supply();
                    }
                    else
                    {
                        supplied = true;
                    }
                }
                if (supplied)
                {
                    log("Case 0");

                    break;
                }
            case 1:
                log("Case 1");

            case 2:
                log("Case 2");

            case 3:
                log("Case 3");

            case 4:
                log("Case 4");

            case 5:
                log("case 5");
            case 6:
                log("case 6");
            case 7:
                log("case 7");
            case 10:
                log("Case 10");
            case 12:
                log("Case 12");
            case 14:
                log("Case 14");
            case 15:
                log("Case 15");
            case 16:
                log("Case 16");
                stop(); // quest completed.

        }
        return random(1200, 1800);
    }

    public boolean Supply() throws InterruptedException
    {
        ArrayList<Integer> itemsNeededIndexes = new ArrayList<>();
        boolean need_to_buy = true;
        walking.webWalk(GeArea);
        sleep(random(1800,2400));
        npcs.closest("Banker").interact("Bank");
        Sleep.sleepUntil(() -> bank.isOpen(), 2400);
        if (bank.isOpen())
        {
            bank.depositWornItems();
            sleep(random(600,800));
            bank.depositAll();
            sleep(random(600,800));

            for (int i = 0; i < supplyID.length; i++)
            {

                if (bank.getAmount(supplyID[i]) >= supplyQuantity[i])
                {
                    log("Have enough");
                    log(supplyID[i]);
                    bank.withdraw(supplyID[i], supplyQuantity[i]);
                }
                else if (bank.contains(supplyID))
                {
                    log("not enough");
                    bank.withdrawAll(supplyID[i]);
                    itemsNeededIndexes.add(i);
                    log(itemsNeededIndexes.size());
                }
                else
                {
                    itemsNeededIndexes.add(i);
                    log(itemsNeededIndexes.size());
                }
            }
            if (itemsNeededIndexes.size() == 0)
            {
                need_to_buy = false;
            }
            else
            {
                bank.withdrawAll("Coins");
            }
            sleep(random(600,800));
            log("closing bank");
            bank.close();
        }
        else
        {
            return false;
        }
        if (need_to_buy)
        {
            log("buying items");
            sleep(random(1200,1400));
            npcs.closest("Grand Exchange clerk").interact("Exchange");
            sleep(random(1200,1400));
            log("opened exchange");
            for (int i: itemsNeededIndexes)
            {
                log("Buying item");
                grandExchange.buyItem(supplyID[i],supplyName[i],supplyPrice[i],
                                      supplyQuantity[i] -(int) inventory.getAmount(supplyID[i]));
                sleep(random(1800,2400));
            }
            grandExchange.collect();
            sleep(random(1800,2400));
        }
        for (int i = 0; i < supplyID.length; i++)
        {
            if (inventory.getAmount(supplyID[i]) < supplyQuantity[i])
            {
                return false;
            }
        }
        return true;
    }
}
