import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.input.mouse.BankSlotDestination;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import util.BetterWalk;
import util.Sleep;
import util.UsefulAreas;

@ScriptManifest(name = "Prayer Trainer", author = "Iownreality1", info = "Trains to 43 prayer", version = 0.1, logo = "")
public class Prayer extends Script
{
    //  500K COST TO BUY ALL SUPPLIES
    BetterWalk walker = new BetterWalk(this);
    int level;
    boolean has_bones = false;
    boolean has_teleport = true;
    Area geArea = UsefulAreas.GeArea;
    Area ChaosTemple = new Area(2948, 3819, 2951, 3823);
    Area Lumbridge = new Area(3200,3200,3300,3300);
    Area LumbridgeMid = new Area(3200,3200,3300,3300);
    Area LumbridgeTop = new Area(3200,3200,3300,3300);
    Area LumbridgeBank = new Area(3207, 3216,3210, 3220);
    Position wine = new Position(2950,3823, 0);
    public void onStart() throws InterruptedException
    {
        Lumbridge.setPlane(0);
        LumbridgeBank.setPlane(2);
        LumbridgeMid.setPlane(1);
        LumbridgeTop.setPlane(2);
    }
    @Override
    public int onLoop() throws InterruptedException
    {
        log("Loop");
        level = skills.getStatic(Skill.PRAYER);
        if (level >= 99)
        {
            if (geArea.contains(myPosition()))
            {
                stop();
            }
            else if (ChaosTemple.contains(myPosition()))
            {
                suicide();
            }
        }
        else if (inventory.getAmount("Dragon bones") == 27 &&
                 inventory.getAmount("Cemetery teleport") == 1)
        {
            teleport();
        }
        else if (geArea.contains(myPosition()))
        {
            log("In ge");
            npcs.closest("Banker").interact("Bank");
            Sleep.sleepUntil(() -> bank.isOpen(), 2400);
            if (bank.isOpen())
            {
                log("open bank in ge");
                bank.depositAll();
                sleep(random(1800, 2400));
                bank.depositWornItems();
                sleep(random(1800, 2400));
                if (bank.getAmount("Dragon bones") >= 27)
                {
                    log("have dragon bones");
                    bank.withdraw("Dragon bones", 27);
                    sleep(random(1800, 2400));
                    has_bones = true;
                }
                if (bank.contains("Cemetery teleport"))
                {
                    log("have teleport");
                    bank.withdraw("Cemetery teleport", 1);
                    sleep(random(1800, 2400));
                    has_teleport = true;
                }
                bank.close();
                sleep(random(1800, 2400));
            }
            if (has_bones && has_teleport)
            {
                log("Have both");
                teleport();
            }
            else
            {
                log("supplying");
                supply();
            }
        }
        else if (!inventory.contains("Dragon bones") && ChaosTemple.contains(myPosition()))
        {
            log("Die");
            suicide();
        }
        else if (myPosition().getY() >= 3525)
        {
            log("in wildy, go pray");
            prayer();
        }
        else if (Lumbridge.contains(myPosition()) ||
                 LumbridgeMid.contains(myPosition()) ||
                 LumbridgeTop.contains(myPosition()))
        {
            supply_from_lumbridge();
        }
        else
        {
            log("not in wildy go to ge");
            walking.webWalk(geArea);
        }
        return random(600, 800);
    }
    public void teleport() throws InterruptedException
    {
        inventory.getItem("Cemetery teleport").interact("Break");
        Sleep.sleepUntil(() -> dialogues.isPendingOption(), 2400);
        if (dialogues.isPendingOption())
        {
            log("teleport dialogue");
            dialogues.completeDialogue("Yes");
            sleep(random(3000, 3600));
        }
    }
    public void supply_from_lumbridge() throws InterruptedException
    {
        log("In supply from lumbridge");
        if (Lumbridge.contains(myPosition()))
        {
            log("Bottom lumby");
            walking.webWalk(LumbridgeBank);
            npcs.closest("Banker").interact("Bank");
            Sleep.sleepUntil(() -> bank.isOpen(), 4800);
            if (bank.isOpen())
            {
                sleep(random(1200, 1800));
                if (bank.getAmount("Dragon bones") >= 27 && bank.getAmount("Cemetery teleport") >= 1)
                {
                    bank.withdraw("Dragon bones", 27);
                    sleep(random(1200,1400));
                    bank.withdraw("Cemetery teleport", 1);
                    sleep(random(600, 800));
                    bank.close();
                    sleep(random(600, 800));
                    teleport();
                }
                else
                {
                    supply();
                }
            }
        }
    }
    public void supply() throws InterruptedException
    {
        log("Enter supply");
        if (geArea.contains(myPosition()))
        {
            log("in supply in ge");
            npcs.closest("Banker").interact("Bank");
            Sleep.sleepUntil(() -> bank.isOpen(), 2400);
            if (bank.isOpen())
            {
                log("in bank in supply");
                sleep(random(1800, 2400));
                bank.depositAll();
                sleep(random(1800, 2400));
                bank.depositWornItems();
                sleep(random(1800, 2400));
                bank.withdrawAll("Coins");
                sleep(random(1800, 2400));
                bank.close();
                sleep(random(1800, 2400));
            }
            log("opening ge");
            npcs.closest("Grand Exchange Clerk").interact("Exchange");
            Sleep.sleepUntil(() -> grandExchange.isOpen(), 2400);
            if (grandExchange.isOpen())
            {
                sleep(random(1800, 2400));
                grandExchange.buyItem(536, "Dragon bones", 4000, 108);
                sleep(random(1800, 2400));
                grandExchange.buyItem(19627, "Cemetery teleport", 2000, 5);
                sleep(random(1800, 2400));
                grandExchange.collect();
                sleep(random(1800, 2400));
                grandExchange.close();
                sleep(random(1800, 2400));
            }
        }
        else
        {
            walking.webWalk(geArea);
        }
    }
    public void prayer() throws InterruptedException
    {
        log("In prayer");
        if (ChaosTemple.contains(myPosition()) && inventory.contains("Dragon bones"))
        {
            log("In chaos temple with bones");
            //inventory.getItem("Dragon bones").interact("Use");
            inventory.interact(26, "Use");
            sleep(random(600, 800));
            objects.closest("Chaos altar").interact();
            sleep(random(300, 500));
        }
        else
        {
            log("in prayer not in temple");

            walking.webWalk(ChaosTemple);
        }
    }
    public void suicide()
    {
        log("in suicide");
        if (wine.equals(myPosition()))
        {
            log("next to wine");
            groundItems.closest("Wine of zamorak").interact("Take");
        }
        else if (!Lumbridge.contains(myPosition()))
        {
            log("not in lumby");
            walker.MyWalkingEvent(wine);
        }
    }
}
