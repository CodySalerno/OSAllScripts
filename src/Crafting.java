import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import util.Sleep;

@ScriptManifest(name = "Crafting", author = "Iownreality1", info = "Get 10 crafting", version = 0.1, logo = "")
public class Crafting extends Script
{
    private final Area GeArea = new Area(3159,3482,3168,3492);
    long leather_count;
    boolean need_needle;
    boolean need_thread;
    boolean need_leather;
    int level;
    boolean supplied = false;

    @Override
    public int onLoop() throws InterruptedException
    {
            level = skills.getStatic(Skill.CRAFTING);
            if (level >= 10)
            {
                stop();
            }
            if (!supplied)
            {
                buySupplies();
            }
            else if (inventory.contains("Leather") && inventory.contains("Needle") && inventory.contains("Thread"))
            {
                inventory.getItem("Leather").interact();
                sleep(random(600, 1200));
                inventory.getItem("Needle").interact();
                Sleep.sleepUntil(() ->
                        widgets.getWidgetContainingText("How many") != null, 2400);
                RS2Widget root = widgets.get(270, 14, 38);
                root.interact("Make");
                Sleep.sleepUntil(() ->
                                (!inventory.contains("Leather") ||
                                getWidgets().getWidgetContainingText("Congratulations") != null),
                         48000);
            }
            else if (!inventory.contains("Leather"))
            {
                bank();
            }
        return random(600, 8001);
    }
    public void bank() throws  InterruptedException
    {
        npcs.closest("Banker").interact("Bank");
        Sleep.sleepUntil(() -> bank.isOpen(), 2400);
        if (bank.isOpen())
        {
            bank.depositAll("Leather gloves");
            sleep(random(600, 800));
            bank.withdrawAll("Leather");
            sleep(random(600, 800));
            bank.close();
            sleep(random(600, 800));
        }
    }
    public void buySupplies() throws InterruptedException {
        int more_leather_needed = 0;
        int total_leather_needed = (int) ((1154 - skills.getExperience(Skill.CRAFTING)) / 13.8) + 1;
        if (!GeArea.contains(myPosition()))
        {
            walking.webWalk(GeArea);
        }
        npcs.closest("Banker").interact("Bank");
        Sleep.sleepUntil(() -> bank.isOpen(), 2400);
        if (bank.isOpen())
        {
            bank.depositAll();
            need_needle = !bank.contains("Needle");
            log("missing needle " + need_needle);
            need_thread = !bank.contains("Thread");
            
            log("missing thread " + need_thread);
            if (!need_thread)
            {
                bank.withdrawAll("Thread");
                sleep(random(600, 1200));
            }
            if (!need_needle)
            {
                bank.withdrawAll("Needle");
                sleep(random(600, 1200));
            }
            leather_count = bank.getAmount("Leather");
            bank.withdrawAll("Coins");
            sleep(random(600, 1200));
            bank.close();
            need_leather = leather_count < total_leather_needed;
            more_leather_needed = total_leather_needed - (int) leather_count;
            sleep(random(600, 1200));
        }
        if (need_thread || need_needle || need_leather)
        {
            npcs.closest("Grand Exchange Clerk").interact("Exchange");
            Sleep.sleepUntil(() -> grandExchange.isOpen(), 2400);
            if (grandExchange.isOpen())
            {
                sleep(random(600, 800));
                if (need_leather)
                {
                    grandExchange.buyItem(1741, "Leather",  500, more_leather_needed);
                    sleep(random(600, 1200));
                }
                if (need_needle)
                {
                    grandExchange.buyItem(1733, "Needle", 500, 1);
                    sleep(random(600, 1200));
                }
                if (need_thread)
                {
                    grandExchange.buyItem(1734, "Thread", 50, 20);
                    sleep(random(600, 1200));
                }
                grandExchange.collect();
                sleep(random(600, 1200));
                grandExchange.close();
                sleep(random(600, 1200));
            }
        }
        npcs.closest("Banker").interact("Bank");
        Sleep.sleepUntil(() -> bank.isOpen(), 2400);
        if (bank.isOpen())
        {
            supplied = inventory.contains("Needle") && inventory.contains("Thread") &&
                    ((inventory.getAmount("Leather") + bank.getAmount("Leather")) >= 84);
        }
    }
}