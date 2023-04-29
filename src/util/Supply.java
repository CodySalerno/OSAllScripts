package util;

import org.osbot.rs07.api.Bank;
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
    public boolean supply(int[] supplyID, String[] supplyName, int[] supplyPrice, int[] supplyQuantity, GEHelper geHelper, boolean withdrawItems, boolean[] withdrawNoted) throws InterruptedException
    {

        if (!((supplyID.length == supplyName.length)  && (supplyName.length == supplyPrice.length) && (supplyPrice.length == supplyQuantity.length) && (withdrawItems && supplyID.length == withdrawNoted.length)))
        {

            methods.log("Not all same sizes");
            return false;
        }
        int[] supplyQuantityNeeded = new int[supplyID.length];
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
            if(!methods.inventory.isEmpty() || !methods.equipment.isEmpty())
            {
                methods.bank.depositWornItems();
                sleep(random(600,800));
                methods.bank.depositAll();
                sleep(random(600,800));
            }
            for (int i = 0; i < supplyID.length; i++)
            {

                if (methods.bank.getAmount(supplyID[i]) >= supplyQuantity[i]) // if already have enough of the item
                {
                    methods.log("Have enough");
                    methods.log(supplyID[i]);
                    supplyQuantityNeeded[i] = 0;
                }
                else if (methods.bank.contains(supplyID))  //if bot has some bot not enough
                {
                    supplyQuantityNeeded[i] = supplyQuantity[i] - (int)methods.bank.getAmount(supplyID[i]);
                    methods.log("not enough");
                    itemsNeededIndexes.add(i);  //add index to ones to buy more of
                    methods.log(itemsNeededIndexes.size());
                }
                else
                {
                    supplyQuantityNeeded[i] = supplyQuantity[i];
                    itemsNeededIndexes.add(i);  //have 0 of the item
                    methods.log(itemsNeededIndexes.size());  //add index to ones to buy more of
                }
            }
            for (int i:supplyQuantityNeeded)
            {
                methods.log(i);
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
                        supplyQuantityNeeded[i]);
                sleep(random(1800,2400));
                offerCount += 1;
            }
            methods.grandExchange.collect();
            sleep(random(1800,2400));
            methods.grandExchange.close();
        }
        methods.log("checking if bank has everything");
        for (int i = 0; i < supplyID.length; i++)
        {
            if (!GeArea.contains(methods.myPosition())) //Go to GE
            {
                methods.walking.webWalk(GeArea);
            }
            if (!methods.bank.isOpen())
            {
                methods.npcs.closest("Banker").interact("Bank"); // open Bank
                Sleep.sleepUntil(() -> methods.bank.isOpen(), 2400);
                if (methods.bank.isOpen())
                {
                    if(!methods.inventory.isEmpty() || !methods.equipment.isEmpty())
                    {
                        methods.bank.depositAll();
                        sleep(random(900,1400));
                        methods.bank.depositWornItems();
                    }
                }
            }
            if (methods.bank.isOpen())
            {
                if (methods.bank.getAmount(supplyID[i]) < supplyQuantity[i])
                {
                    methods.log("bank didn't have everything returning false");
                    return false;
                }
                if (withdrawItems)
                {
                    if (withdrawNoted[i])
                    {
                        methods.bank.enableMode(Bank.BankMode.WITHDRAW_NOTE);
                    }
                    else
                    {
                        methods.bank.enableMode(Bank.BankMode.WITHDRAW_ITEM);
                    }
                    methods.bank.withdraw(supplyID[i], supplyQuantity[i]);
                }
            }
        }
        methods.log("bank has everything return true");
        return true;
    }
}
