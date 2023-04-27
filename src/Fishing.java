import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.input.mouse.InventorySlotDestination;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;
import util.FormattingForPaint;
import util.Sleep;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import java.awt.*;

//currently logs out if out of tar/guams, should fix to go to bank.
@ScriptManifest(name = "Fishing", author = "Iownreality1", info = "3t fishing barb.", version = 1.0, logo = "")
public final class Fishing extends Script
{

    private String runningTime;
    Font font = new Font("Open Sans", Font.BOLD, 18);
    long startTime;
    

    @Override
    public void onPaint(final Graphics2D g)
    {
        runningTime = FormattingForPaint.formatTime(System.currentTimeMillis()-startTime);
        g.setFont(font);
        g.setColor(Color.green);
        g.drawString("Running Time: " + runningTime,10,250);
        g.drawString("Fishing xp Gained: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXP(Skill.FISHING)),10,270);
        g.drawString("Fishing xp/hr: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXPPerHour(Skill.FISHING)),10,290);
        g.drawString("Current Fishing level: " + skills.getStatic(Skill.FISHING), 10,310);
    }
    public void onStart()
    {
        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.FISHING);
    }
    @Override
    public int onLoop() throws InterruptedException
    {
        NPC fishy = npcs.closest(new Area(myPosition().getX()+1, myPosition().getY(),myPosition().getX()+1, myPosition().getY()), "Fishing spot");
        if (inventory.isFull())
        {
            getKeyboard().pressKey(16);
            inventory.dropAll("Leaping trout", "Leaping salmon", "leaping sturgeon");
            getKeyboard().releaseKey(16);

        }

        else if (inventory.contains("Guam leaf") && inventory.getSlot("Guam leaf") != 12)
        {
            log("moving");
            moveInvItem(21,12);

        }
        else if (fishy != null && inventory.contains("Guam leaf") && inventory.contains("Swamp tar") && !(inventory.getSlot("Guam leaf") != 12))
        {
            int fish;
            if (!inventory.isItemSelected())
            {
                inventory.getItem("guam leaf").interact();
            }
            if (inventory.isItemSelected())
            {
                inventory.hover(8);
                inventory.getItem("Swamp tar").interact();
                if (inventory.contains("leaping trout") )
                {
                    log("dropping trout");
                    inventory.drop("leaping trout");
                }
                else if (inventory.contains("leaping salmon"))
                {
                    log("dropping salmon");
                    inventory.drop("leaping salmon");
                }
                else if (inventory.contains("leaping sturgeon"))
                {
                    log("dropping sturgeon");
                    inventory.drop("leaping sturgeon");
                }
                fishy.hover();
                //npcs.closest("Fishing spot").hover();
                log("sleeping until animation");
                Sleep.sleepUntil(() -> myPlayer().getAnimation() == 5249, 1800);
                log("animation has happened interact with fishing");
                fishy.interact("Use-rod");
                //npcs.closest("Fishing spot").interact();
                inventory.getItem("guam leaf").interact();
                fish = (int)inventory.getAmount("Leaping trout")+ (int)inventory.getAmount("Leaping salmon");
                log("sleep till fishy");
                Sleep.sleepUntil(() -> (fish < (int)inventory.getAmount("Leaping trout")+ (int)inventory.getAmount("Leaping salmon")), 600);
            }
        }
        else if (!inventory.contains("Guam leaf") && inventory.contains("Grimy Guam leaf"))
        {
            inventory.getItem("Grimy guam leaf").interact();
            sleep(random(200, 300));
            npcs.closest("Fishing spot").interact();
        }
        else if ((!inventory.contains("Guam leaf") || inventory.contains("Grimy Guam leaf")) && !inventory.contains("Swamp tar"))
        {
            stop();
        }
        else
        {
            npcs.closest("Fishing spot").interact();
            Sleep.sleepUntil(() -> myPlayer().getAnimation() == 9349, 1800);
        }

        //Sleep.sleepUntil(() -> inventory.contains("Leaping trout") || inventory.contains("leaping salmon"), 1800);

        return random(100,200);
    }

    public void moveInvItem(int from, int to) {
        if (from < 0) return;
        Rectangle toRec = getInventory().getSlotBoundingBox(to);
        RectangleDestination rectDest = new RectangleDestination(getBot(), getInventory().getSlotBoundingBox(getInventory().getSlot(from)));
        getMouse().continualClick(rectDest, new Condition() {
            public boolean evaluate() {
                if (toRec.contains(getMouse().getPosition())) return true;
                getMouse().move(new RectangleDestination(getBot(), toRec));
                return false;
            }
        });
        new ConditionalSleep(1200) {
            @Override
            public boolean condition() throws InterruptedException {
                return getInventory().getSlot(from) == to;
            }
        }.sleep();
    }

}