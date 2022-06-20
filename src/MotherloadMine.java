import org.osbot.rs07.api.filter.AreaFilter;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.RS2Object;
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
@ScriptManifest(name = "Motherload Mine", author = "Iownreality1", info = "Mines at motherload mine", version = 0.1, logo = "")
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
    Area bankArea = new Area(3739,5654, 3760, 5675);
    Area hopperArea = new Area(3746, 5671, 3750, 5674);
    Area sackArea = new Area(3746, 5657, 3750,5662);
    Area nearRock1 = new Area(3733,5678,3736,5680);
    Area nearRock2 = new Area(3730, 5680,3734,5682);
    //Area nearRock3 = new Area(x, y, x, y);
    Area northMiningArea = new Area(3732, 5687, 3744,5692);
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


    }

    @Override
    public void onStart()
    {
        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.MINING);
        thread1.start();
    }
    @Override
    public int onLoop() throws InterruptedException
    {
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
                log("walkinging to hopperArea area");
                walking.webWalk(hopperArea);
                if (hopperArea.contains(myPosition()) && inventory.contains("Pay-dirt")) {
                    log("deposit hopper");
                    objects.closest("Hopper").interact();
                    sleep(random(2400, 3000));
                }
            }
        }
        if (inventory.contains("Coal"))
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
                    log("deposit all");
                    if (inventory.contains("Golden nugget"))
                    {
                        goldNuggets += (int) inventory.getAmount("Golden nugget");
                    }
                    bank.depositAll();
                    sleep(random(1800,2400));
                    isBanking = false;
                }
        }

        if (bankArea.contains(myPosition()) && configs.get(375) != 0)
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


        if (bankArea.contains(myPosition()) && !nearRock1.contains(myPosition()) && configs.get(375) ==0 && !inventory.contains("Coal"))
        {
                log("Walking to rock1");
				walking.webWalk(nearRock1);
				sleep(random(800,1200));
        }

        if (nearRock1.contains(myPosition()) && !inventory.isFull())
        {
            rock1 = objects.closest(new Area(3733,5680,3733,5680), 26679);
            if (nearRock1.contains(myPosition()) && rock1 != null && !myPlayer().isAnimating())
            {
                log("Mining rock1");
                rock1.interact();
				Sleep.sleepUntil(() -> (objects.closest(new Area(3733,5680,3733,5680), 26679) == null), 6000); //not sure if this will work looking for rock1 area if this works.
                sleep(random(600,900));
            }
			if (rock1 == null)
            {
                log("running past rock 1");
                walking.webWalk(nearRock2);
                sleep(random(1200,1600));
            }
        }

        if (nearRock2.contains(myPosition()) && !inventory.isFull())
        {

            rock2 = objects.closest(new Area(3731,5683,3731,5683),26680);
            if (nearRock2.contains(myPosition()) && rock2 != null && !myPlayer().isAnimating())
            {
                log("Mining rock2");
                rock2.interact();
                sleep(random(1200,1600));
            }
			if (rock2 == null)
            {
                log("running past rock 2");
                walking.webWalk(northMiningArea);
                sleep(random(1200,1600));
            }
        }
		/*
        if (nearRock3.contains(myPosition()))
        {
            rock3 = objects.closest(new Area(x, y, x, y),id);
            if (rock3 == null)
            {
                log("running past rock 3");
                walking.webWalk(northMiningArea);
                sleep(random(1200,1600);
            }
            if (nearRock3.contains(myPosition()) && rock3 != null && !myPlayer().isAnimating())
            {
                log("Mining rock3");
                objects.closest(rock3).interact();
                sleep(random(1200,1600);
            }
        }*/

        if (northMiningArea.contains(myPosition()) && inventory.isFull())
        {

            isBanking = true;
            log("is banking: " + isBanking);
            log("Walking to rock2");
            walking.webWalk(returnNearRock2);
            sleep(random(1200,1600));
        }
		/*
        if (returnNearRock3.contains(myPosition()) && isBanking)
        {
            returnRock3 = objects.closest(new Area(x, y, x, y),id);
            if (returnRock3 == null)
            {
                log("running past rock 3");
                walking.webWalk(returnNearRock2);
                sleep(random(1200,1600);
            }
            if (nearRock3.contains(myPosition()) && returnRock3 != null && !myPlayer().isAnimating())
            {
                log("Mining returnRock3");
                objects.closest(returnRock3).interact();
                sleep(random(1200,1600);
        }*/

        if (returnNearRock2.contains(myPosition()) && inventory.isFull())
        {
            returnRock2 = objects.closest(new Area(3731,5683,3731,5683),26680);
            if (returnNearRock2.contains(myPosition()) && (returnRock2 != null) && (!myPlayer().isAnimating()))
            {
                log("Mining returnRock2");
                returnRock2.interact();
                sleep(random(1200,1600));
			}
			if (returnRock2 == null)
            {
                log("running past rock 2");
                walking.webWalk(returnNearRock1);
                sleep(random(1200,1600));
            }
        }

        if (returnNearRock1.contains(myPosition()) && inventory.isFull())
        {
            returnRock1 = objects.closest(new Area(3733,5680,3733,5680), 26679);
            if (returnNearRock1.contains(myPosition()) && returnRock1 != null && !myPlayer().isAnimating())
            {
                log("Mining returnRock1");
               returnRock1.interact();
                sleep(random(1200,1600));
			}
			if (returnRock1 == null)
            {
                log("running past rock 1");
                walking.webWalk(bankArea);
                sleep(random(1200,1600));
            }
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

            miningRocks = objects.closest(northMiningArea, "Ore vein"); //this should be all the area i want to mine in.
            if (miningRocks != null)
            {
                rockPosition = miningRocks.getPosition();
                rockArea = new Area(rockPosition.getX(), rockPosition.getY(), rockPosition.getX(), rockPosition.getY());
            }
            if (miningRocks != null && !myPlayer().isAnimating() && timeSinceLastAnimation > 1500)
            {
                log("mining pay dirt");
                miningRocks.interact();
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
