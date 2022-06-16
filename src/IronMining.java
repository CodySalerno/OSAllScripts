import javafx.geometry.Pos;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import util.FormattingForPaint;

import java.awt.*;

@ScriptManifest(name = "Iron Miner", author = "Iownreality1", info = "Mine iron in rimmington", version = 0.1, logo = "")
public class IronMining extends Script
{
    Position nearRocks = new Position(2969,3239,0);
    private String runningTime;
    int oreMined;
    long startTime;
    Entity rock1;
    Entity rock2;
    Font font = new Font("Open Sans", Font.BOLD, 18);;

    @Override
    public void onPaint(final Graphics2D g)
    {

        runningTime = FormattingForPaint.formatTime(System.currentTimeMillis()-startTime);
        g.setFont(font);
        g.setColor(Color.green);
        g.drawString("Running Time: " + runningTime,10,250);
        g.drawString("Mining xp Gained: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXP(Skill.MINING)),10,270);
        g.drawString("Mining xp/hr: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXPPerHour(Skill.MINING)),10,290);
        g.drawString("Current Mining level: " + skills.getStatic(Skill.MINING), 10,310);
        g.drawString("Ore Mined:  " + oreMined, 10,330);


    }

    @Override
    public void onStart()
    {
        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.MINING);
    }

    @Override
    public int onLoop() throws InterruptedException
    {
        if (!nearRocks.equals(myPosition()))
        {
            stop();
        }
        rock1 = objects.closest(new Area(2969,3240,2969,3240),11364);
        if (rock1 != null)
        {
            if (rock1.hasAction("Mine") && !myPlayer().isAnimating() && rock1 != null && !inventory.isFull())
            {
                sleep(random(1000,1400));
                log("Mine north rock");
                rock1.interact("Mine");
                sleep(random(2400,3200));
                oreMined++;
            }
        }
        rock2 = objects.closest(new Area(2968,3239,2968,3239),11365);
        if (rock2 != null)
        {
            if (rock2.hasAction("Mine") && !myPlayer().isAnimating() && !inventory.isFull())
            {
                sleep(random(1000,1400));
                log("Mine west rock");
                rock2.interact("Mine");
                sleep(random(2400,3200));
                oreMined++;
            }
        }
        if (inventory.isFull())
        {
            inventory.dropAll("Iron ore");
            sleep(random(1800,3000));
        }

        if (skills.getStatic(Skill.MINING) >= 31)
        {
            stop();
        }


        return random(1500,2000);
    }

}
