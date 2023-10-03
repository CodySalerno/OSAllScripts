import util.EnergyCheck;
import util.FormattingForPaint;
import util.Sleep;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;


import java.awt.*;
//TODO: QA old courses to see if new changes broke anything.
@ScriptManifest(name = "BlastFurnaceGold", author = "Iownreality1", info = "Train agility", version = 0.1, logo = "")
public class BlastFurnaceGold extends  Script
{

    String runningTime;
    long startTime;
    Font font = new Font("Open Sans", Font.BOLD, 18);
    @Override
    public void onPaint(final Graphics2D g)
    {
        runningTime = FormattingForPaint.formatTime(System.currentTimeMillis()-startTime);
        g.setFont(font);
        g.setColor(Color.green);
        g.drawString("Running Time: " + runningTime,10,250);
        g.drawString("Smithing xp Gained: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXP(Skill.SMITHING)),10,270);
        g.drawString("Smithing xp/hr: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXPPerHour(Skill.SMITHING)),10,290);
        g.drawString("Current Smithing level: " + skills.getStatic(Skill.SMITHING), 10,310);
        g.drawString("Bars Created  " + (int)(getExperienceTracker().getGainedXP(Skill.SMITHING)/56.2), 10,330);
        g.drawString("Bars per hour  " + (int)(getExperienceTracker().getGainedXPPerHour(Skill.SMITHING)/56.2), 10,350);
    }

    public void onStart()
    {
        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.SMITHING);
    }

    @Override
    public int onLoop() throws InterruptedException
    {
        if (inventory.contains("Gold ore"))
        {
            if (objects.closest(9100).interact("Put-ore-on"))
            {
                Sleep.sleepUntil(() -> !inventory.contains("Gold ore"), 10000);
            }
            if (!inventory.contains("Gold ore"))
            {
                walking.webWalk(new Position(1941,4962,0));
            }
        }
        else if (objects.closest("Bar dispenser").hasAction("Take") && inventory.contains("Ice gloves") && !dialogues.isPendingContinuation())
        {
            if (inventory.contains("Ice gloves"))
            {
                inventory.getItem("Ice gloves").interact("Wear");
                Sleep.sleepUntil(() -> !inventory.contains("Ice gloves"), 2000);
            }
        }
        else if (objects.closest("Bar dispenser").hasAction("Take") && !inventory.contains("Ice gloves") && widgets.get(270,14,38) == null && !dialogues.isPendingContinuation())
        {
            if (objects.closest("Bar dispenser").interact("Take"))
            {
                Sleep.sleepUntil(() -> widgets.get(270,14,38) != null, 7000);

            }
        }
        else if (widgets.get(270,14,38) != null)
        {
            if (widgets.get(270,14,38).interact())
            {
                Sleep.sleepUntil(() -> inventory.contains("Gold bar"), 5000);
            }
        }

        else if (dialogues.isPendingContinuation())
        {
            log("pending continuation");
            if(inventory.contains(776))
            {
               inventory.getItem(776).interact();
               Sleep.sleepUntil(() -> equipment.contains(776), 3000);
            }
        }
        else if (!dialogues.isPendingContinuation() && inventory.contains("Gold bar") && !bank.isOpen())
        {
            objects.closest("Bank chest").interact("Use");
            Sleep.sleepUntil(() -> bank.isOpen(), 5000);
        }
        else if (bank.isOpen() && inventory.contains("Gold bar"))
        {
            bank.depositAll("Gold bar");
            Sleep.sleepUntil(() -> !inventory.contains("Gold bar"), 3000);
        }
        else if (bank.isOpen() && !inventory.contains("Gold bar") && !inventory.contains("Gold ore"))
        {
            energy();
            bank.withdrawAll("Gold ore");
            Sleep.sleepUntil(() -> inventory.contains("Gold ore"), 3000);
        }
        return random(300,500);
    }
    private void energy()
    {
        if ((configs.get(1575) == 0 || configs.get(1575) == 8388608) && settings.getRunEnergy() < 60)
        {
            if (bank.withdraw("Stamina potion(1)", 1))
            {
                Sleep.sleepUntil(() -> inventory.contains("Stamina potion(1)"), 2000);
                if  (inventory.contains("Stamina potion(1)"))
                {
                    inventory.getItem("Stamina potion(1)").interact("Drink");
                    Sleep.sleepUntil(() -> !inventory.contains("Stamina potion(1)"), 2000);
                    if (inventory.contains("Vial"))
                    {
                        bank.depositAll("Vial");
                        Sleep.sleepUntil(() -> !inventory.contains("Vial"), 3000);
                    }
                }
            }
        }
    }
}