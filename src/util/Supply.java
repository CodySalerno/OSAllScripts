package util;

import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.script.MethodProvider;

import java.util.ArrayList;
import org.osbot.rs07.script.MethodProvider;


import static org.osbot.rs07.script.MethodProvider.*;
import static util.UsefulAreas.GeArea;

public class Supply
{

    private final MethodProvider methods;

    public Supply(final MethodProvider methods)
    {
        this.methods = methods;
    }

    /**
     *  supplyID = ID for supplies to buy<br>
     *  supplyName = Name for supplies to buy<br>
     *  supplyPrice = Price to buy each item at per item<br>
     *  supplyQuantity = Number of each item to buy.<br>
     */
    public boolean supply(int[] supplyID, String[] supplyName, int[] supplyPrice, int[] supplyQuantity, GEHelper geHelper) throws InterruptedException
    {

        if (!((supplyID.length == supplyName.length)  && (supplyName.length == supplyPrice.length) && (supplyPrice.length == supplyQuantity.length)))
        {
            methods.log("Not all same sizes");
            return false;
        }
        ArrayList<Integer> itemsNeededIndexes = new ArrayList<>();
        boolean need_to_buy = true;
        if (!GeArea.contains(methods.myPosition())) //Go to GE
        {
            methods.walking.webWalk(GeArea);
        }
        sleep(random(1800,2400));
        methods.npcs.closest("Banker").interact("Bank"); // open Bank
        Sleep.sleepUntil(() -> methods.bank.isOpen(), 2400);
        if (methods.bank.isOpen())
        {
            methods.bank.depositWornItems();
            sleep(random(600,800));
            methods.bank.depositAll();
            sleep(random(600,800));
            for (int i = 0; i < supplyID.length; i++)
            {

                if (methods.bank.getAmount(supplyID[i]) >= supplyQuantity[i]) // if already have enough of the item
                {
                    methods.log("Have enough");
                    methods.log(supplyID[i]);
                    methods.bank.withdraw(supplyID[i], supplyQuantity[i]); //withdraw the item
                    sleep(random(900,1300));
                }
                else if (methods.bank.contains(supplyID))  //if bot has some bot not enough
                {
                    methods.log("not enough");
                    methods.bank.withdrawAll(supplyID[i]); //withdraw available
                    sleep(random(900,1300));
                    itemsNeededIndexes.add(i);  //add index to ones to buy more of
                    methods.log(itemsNeededIndexes.size());
                }
                else
                {
                    itemsNeededIndexes.add(i);  //have 0 of the item
                    methods.log(itemsNeededIndexes.size());  //add index to ones to buy more of
                }
            }
            if (itemsNeededIndexes.size() == 0)  //if we have all items
            {
                need_to_buy = false;
            }
            else
            {
                methods.bank.withdrawAll("Coins");  //withdraw coins to buy items
            }
            sleep(random(600,800));
            methods.log("closing bank");
            methods.bank.close();
            sleep(random(900,1300));

        }
        else
        {
            return false;
        }
        if (need_to_buy)
        {
            methods.log("buying items");
            sleep(random(1200,1400));
            methods.npcs.closest("Grand Exchange clerk").interact("Exchange");  //open GE
            sleep(random(1200,1400));
            methods.log("opened exchange");
            int offerCount = 0;
            for (int i: itemsNeededIndexes)
            {
                if (offerCount == 8)
                {
                    methods.grandExchange.collect();
                    sleep(random(900,1300));
                    offerCount = geHelper.offersPending();
                }
                methods.log("Buying item");
                methods.grandExchange.buyItem(supplyID[i],supplyName[i],supplyPrice[i],
                                      supplyQuantity[i] -(int) methods.inventory.getAmount(supplyID[i]));
                sleep(random(1800,2400));
                offerCount += 1;
            }
            methods.grandExchange.collect();
            sleep(random(1800,2400));
        }
        for (int i = 0; i < supplyID.length; i++)
        {
            if (methods.inventory.getAmount(supplyID[i]) < supplyQuantity[i])
            {
                return false;
            }
        }
        return true;
    }
}
