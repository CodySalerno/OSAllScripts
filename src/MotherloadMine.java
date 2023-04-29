import org.osbot.P;
import org.osbot.rs07.api.filter.AreaFilter;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Tab;
import util.FormattingForPaint;
import util.Sleep;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.util.Collection;

//config 375 = bag
@ScriptManifest(name = "Motherload Mine1.03", author = "Iownreality1", info = "Mines at motherload mine", version = 0.1, logo = "")
public class MotherloadMine extends Script
{
    Entity rock1;
    Entity rock2;
    Entity rock3;
    Entity returnRock1;
    Entity returnRock2;
    //Entity returnRock3;
    Entity miningRocks;
    Area MotherloadMine = new Area(3728, 5654, 3760, 5692);
    Area bankArea = new Area(3733,5654, 3760, 5679);
    Area hopperArea = new Area(3746, 5671, 3750, 5674);
    Area sackArea = new Area(3746, 5657, 3750,5662);
    Area nearRock1 = new Area(3733,5678,3736,5680);
    Area nearRock2 = new Area(3730, 5680,3734,5682);
    //Area nearRock3 = new Area(x, y, x, y);
    Area northMiningArea = new Area(3732, 5688, 3744,5692);
    Area northMiningRocks = new Area(3732, 5687, 3744,5692);
    Position bankPosition = new Position(3760,5666,0);
    Area returnNearRock1 = new Area(3730, 5680,3734,5682);
    Area returnNearRock2 = new Area(3728,5683, 3731, 5687);
    //Area returnNearRock3 = new Area(x, y, x, y);
    boolean isBanking = false;
    long lastAnimation;
    long timeSinceLastAnimation;
    Area rockArea;
    Position rockPosition;
    Font font = new Font("Open Sans", Font.BOLD, 18);
    private String runningTime;
    int oreMined;
    long startTime;
    int goldNuggets;
    int totalNuggets;

    Thread thread1 = new Thread(() ->
    {
        while (getBot().getScriptExecutor().isRunning() && !getBot().getScriptExecutor().isPaused() && !getBot().getScriptExecutor().isSuspended())
        {

            if (myPlayer().isAnimating())
            {
                lastAnimation = System.currentTimeMillis();
            }
            timeSinceLastAnimation = System.currentTimeMillis() - lastAnimation;

            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {

            }
        }
    });


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
        g.drawString("Gold Nuggets  " + goldNuggets, 10,330);
        g.drawString("Total Nuggets  " + totalNuggets, 10,350);


    }

    @Override
    public void onStart()
    {

        lastAnimation = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.MINING);
        thread1.start();
    }
    @Override
    public int onLoop() throws InterruptedException
    {
        if (!tabs.isOpen(Tab.INVENTORY))
        {
            tabs.open(Tab.INVENTORY);
        }
        log("Thread state: " + thread1.getState());
        if (thread1.getState() == Thread.State.TERMINATED)
        {
            lastAnimation = System.currentTimeMillis();
            thread1 = new Thread(() ->
            {
                while (getBot().getScriptExecutor().isRunning() && !getBot().getScriptExecutor().isPaused() && !getBot().getScriptExecutor().isSuspended())
                {

                    if (myPlayer().isAnimating())
                    {
                        lastAnimation = System.currentTimeMillis();
                    }
                    timeSinceLastAnimation = System.currentTimeMillis() - lastAnimation;

                    try
                    {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e)
                    {

                    }
                }
            });
            thread1.start();
        }
        log("Time since last animation: " + timeSinceLastAnimation);
        if (totalNuggets >= 100 || timeSinceLastAnimation > 900000)
        {
            stop();
        }

        if (settings.getRunEnergy() > 20 && settings.isRunning() == false)
        {
            settings.setRunning(true);
        }
		if (inventory.contains("Uncut sapphire") || inventory.contains("Uncut emerald") || inventory.contains("Uncut ruby") || inventory.contains("Uncut diamond"))
		{
            log("dropping gems");
			dropGems();
			sleep(random(1200,1600));
		}
        //Handles banking once inside banking area
        if (bankArea.contains(myPosition()) && inventory.isFull() && !inventory.contains("Coal"))
        {
            if (inventory.contains("Pay-dirt")) {
                log("walking to hopperArea area");
                walking.webWalk(hopperArea);
                if (hopperArea.contains(myPosition()) && inventory.contains("Pay-dirt"))
                {
                    log("deposit hopper");
                    objects.closest("Hopper").interact();
                    sleep(random(2400, 3000));
                }
            }
        }
        if (inventory.contains("Coal", "Mithril ore", "Iron ore", "Gold ore", "Adamantite ore", "Runite ore"))
        {
            log("walking to bank");
            walking.webWalk(bankPosition);
            sleep(random(1200,1800));
                if (!bank.isOpen())
                {
                    log("Open bank");
                    objects.closest("Bank chest").interact("Use");
                    sleep(random(1800,2400));
                }
                if (bank.isOpen())
                {
                    if (inventory.contains("Golden nugget"))
                    {
                        goldNuggets += (int) inventory.getAmount("Golden nugget");
                    }
                    if (bank.contains("Golden nugget"))
                    {
                        totalNuggets = (int)bank.getAmount("Golden nugget");
                    }
                    bank.depositAllExcept("Adamant pickaxe", "Rune pickaxe");
                    isBanking = false;

                }
        }
        if (bankArea.contains(myPosition()) && !widgets.get(382,3,2).getMessage().equals("0") && !nearRock1.contains(myPosition()))
        {
            if (!sackArea.contains(myPosition()))
            {
                log("walking to sack to retrieve ore");
                walking.webWalk(sackArea);
                sleep(random(1200,1800));
            }
            if (sackArea.contains(myPosition()))
            {
                log("get ore from sack");
                objects.closest("Sack").interact();
                sleep(random(1800,2400));
                if (inventory.contains("Golden Nuggets"))
                {
                    oreMined = (int)inventory.getAmount("Golden nugget");
                }
            }
        }
        if (bankArea.contains(myPosition()) && !nearRock1.contains(myPosition()) && widgets.get(382,3,2).getMessage().equals("0") && !inventory.contains("Coal"))
        {
                log("Walking to rock1");
				walking.webWalk(nearRock1);
        }
        if (nearRock1.contains(myPosition()) && !inventory.contains("Coal"))
        {
            walking.webWalk(northMiningArea);
        }
        if (northMiningArea.contains(myPosition()) && inventory.isFull())
        {
            log("Walking to rock2");
            walking.webWalk(bankArea);
        }
        if (northMiningArea.contains(myPosition()) && !inventory.isFull())
        {
            //logs out if players are in my mining area.
            if (getPlayers().closest(p -> p != null && !p.equals(myPlayer()) && northMiningArea.contains(p)) != null)
            {
                log("player in my area hopping worlds.");
                worlds.hopToP2PWorld();
                sleep(10000);

            }
            miningRocks = objects.closest(northMiningRocks, "Ore vein"); //this should be all the area I want to mine in.
            if (camera.getPitchAngle() != 67)
            {
                log("move pitch");
                camera.movePitch(67);
            }
            if (!(camera.getYawAngle() > 124 && camera.getYawAngle() < 235))
            {
                log("move camera");
                camera.moveYaw(random(124,235));
            }
            if (miningRocks != null)
            {
                rockPosition = miningRocks.getPosition();
                rockArea = new Area(rockPosition.getX(), rockPosition.getY(), rockPosition.getX(), rockPosition.getY());
            }
            if (miningRocks != null && !myPlayer().isAnimating() && timeSinceLastAnimation > 2400)
            {
                log("mining pay dirt");
                miningRocks.interact("Mine");
                sleep(5000);
                log("Waiting for rock to disappear");
                if (dialogues.isPendingContinuation())
                {
                    dialogues.completeDialogue();
                }
                log("rock disappeared.");
                sleep(random(1200,1800));
            }
        }
        if (!northMiningArea.contains(myPosition()) && !bankArea.contains(myPosition()) && !sackArea.contains(myPosition()) && !hopperArea.contains(myPosition()) && !nearRock1.contains(myPosition())
        && !nearRock2.contains(myPosition()) && !returnNearRock2.contains(myPosition()) && !returnNearRock1.contains(myPosition()))
        {
            log("out of all spots walk to known area (bank)");
            walking.webWalk(bankArea);
        }
        return random(800,1600);
    }
	
	public void dropGems()
	{
		if(inventory.contains("Uncut sapphire"))
		{
            inventory.drop("Uncut sapphire");
		}
		if(inventory.contains("Uncut emerald"))
		{
            inventory.drop("Uncut emerald");
		}
		if(inventory.contains("Uncut ruby"))
		{
            inventory.drop("Uncut ruby");
		}
		if(inventory.contains("Uncut diamond"))
		{
            inventory.drop("Uncut diamond");
		}
	}
}
