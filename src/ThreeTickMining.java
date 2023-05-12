import org.osbot.Sk;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.MagicSpell;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.event.interaction.MouseMoveProfile;
import org.osbot.rs07.listener.GameTickListener;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import util.BetterWalk;
import util.FormattingForPaint;
import util.Sleep;
import util.UsefulAreas;

import java.awt.*;

@ScriptManifest(name = "ThreeTickMining", author = "Iownreality1", info = "Trains mining at granite until 70", version = 0.1, logo = "")
public class ThreeTickMining extends Script
{
    Area graniteRock = new Area(3162,2904, 3169, 2913);
    Area rock1 = new Area(3165,2908, 3165, 2908);
    Area rock2 = new Area(3165,2909, 3165,2909);
    Area rock3 = new Area(3165,2910, 3165,2910);
    Area rock4 = new Area(3167,2911, 3167,2911);
    Position nearRock1 = new Position(3166,2908, 0);
    Position nearRock2 = new Position(3166,2909, 0);
    Position nearRock3 = new Position(3166,2910, 0);
    Position nearRock4 = new Position(3167,2910, 0);
    Area lastRock;
    BetterWalk betterWalk = new BetterWalk(this);
    int graniteRockId = 11387;

    int ticks = 0;
    int lastTick = 0;
    private String runningTime;
    Font font = new Font("Open Sans", Font.BOLD, 18);
    long startTime;
    int counter = 0;

    GameTickListener tickListener = new GameTickListener()
    {
        @Override
        public void onGameTick()
        {
            ticks++;
        }
    };

    public void onStart()
    {
        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.MINING);
        getBot().getGameTickListeners();
        ticks = client.getCurrentTick()/30;
        lastRock = rock1;
        setSlowMouse();
        if (graniteRock.contains(myPosition()))
        {
            betterWalk.MyWalkingEvent(nearRock1);
        }
        else stop();
    }

    @Override
    public void onPaint(final Graphics2D g)
    {

        Font font = new Font("Open Sans", Font.BOLD, 18);
        g.setFont(font);
        g.setColor(Color.green);
        g.drawString("Running Time: " + (FormattingForPaint.formatTime(System.currentTimeMillis() - startTime)), 10, 250);
        g.drawString("Mining xp Gained: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXP(Skill.MINING)), 10, 270);
        g.drawString("Mining xp/hr: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXPPerHour(Skill.MINING)), 10, 290);
        g.drawString("Current Level: " + skills.getStatic(Skill.MINING), 10, 310);
        int xpTillDone = (skills.getExperienceForLevel(70) - skills.getExperience(Skill.MINING));
        int xpPerSecond = (experienceTracker.getGainedXPPerHour(Skill.MINING)) / 3600;
        long timeTillDone = (xpTillDone / xpPerSecond);
        long timeTillLevel = (skills.getExperienceForLevel(skills.getStatic(Skill.MINING)+1)-skills.getExperience(Skill.MINING))/xpPerSecond;
        String S = FormattingForPaint.formatTimeSeconds(timeTillDone);
        String T = FormattingForPaint.formatTimeSeconds(timeTillLevel);
        g.drawString("Time until next level: " + T, 10, 330);
        g.drawString("Time until level 70: " + S, 10, 350);
    }

    @Override
    public int onLoop() throws InterruptedException
    {

        if (camera.getYawAngle() != 90)
        {
            camera.moveYaw(90);
        }
        if (camera.getPitchAngle() != 60)
        {
            camera.movePitch(60);
        }
        if (ticks < client.getCurrentTick()/30)
        {
            tickListener.onGameTick();
        }
        if (!inventory.contains("Guam leaf") && !inventory.contains("Grimy guam leaf"))
        {
            log("Ran out of guam logging out");
            stop();
        }
        else if (!graniteRock.contains(myPosition()))
        {
            log("Not at rocks logging out");
            stop();
        }
        else if (!inventory.contains("Waterskin(4)", "Waterskin(3)", "Waterskin(2)", "Waterskin(1)"))
        {
            log("cast humidify");
            magic.castSpell(Spells.LunarSpells.HUMIDIFY);
            sleep(2400);
            counter = 0;
        }
        else if (nearRock1.equals(myPosition()) && ticks >= lastTick+2 && lastRock == rock1)
        {
            log("mine rock 2");
            mineRock(rock2);
            counter = 0;
        }
        else if (nearRock2.equals(myPosition()) && ticks >= lastTick+2 && lastRock == rock2)
        {
            log("mine rock 3");
            mineRock(rock3);
            counter = 0;
        }
        else if (nearRock3.equals(myPosition()) && ticks >= lastTick+2 && lastRock == rock3)
        {
            log("mine rock 4");
            mineRock(rock4);
            counter = 0;
        }
        else if (nearRock4.equals(myPosition()) && ticks >= lastTick+2 && lastRock == rock4)
        {
            log("mine rock 1");
            mineRock(rock1);
            counter = 0;
        }
        else if (counter > 200)
        {
            lastRock = rock1;
            if (graniteRock.contains(myPosition()))
            {
                betterWalk.MyWalkingEvent(nearRock1);
                counter = 0;
            }
            else stop();
        }
        else
        {
            counter++;
        }


        return random(10,100);
    }

    public void mineRock(Area Rock2) throws InterruptedException {
        if (inventory.isItemSelected())
        {
            inventory.deselectItem();
        }
        log("clicking guam");
        inventory.getItem("Guam leaf").interact();
        Sleep.sleepUntil(() -> inventory.isItemSelected(), 1000);
        if (inventory.isItemSelected())
        {
            log("clicking swamp tar");
            if (inventory.getItem("Swamp tar").interact())
            {
                sleep(25);
                setFastMouse();
                objects.closest(Rock2, graniteRockId).interact("Mine");
                lastTick = ticks;
                setSlowMouse();
            }
            else
            {
                sleep(25);
                setFastMouse();
                objects.closest(Rock2, graniteRockId).interact("Mine");
                lastTick = ticks;
                setSlowMouse();
            }
            if (inventory.contains("Granite (500g)"))
            {
                setFastMouse();
                inventory.drop("Granite (500g)");
                sleep(25);
                objects.closest(Rock2, graniteRockId).interact("Mine");
                setSlowMouse();
            }
            else if (inventory.contains("Granite (2kg)"))
            {
                setFastMouse();
                inventory.drop("Granite (2kg)");
                sleep(25);
                objects.closest(Rock2, graniteRockId).interact("Mine");
                setSlowMouse();
            }
            else if (inventory.contains("Granite (5kg)"))
            {
                setFastMouse();
                inventory.drop("Granite (5kg)");
                sleep(25);
                objects.closest(Rock2, graniteRockId).interact("Mine");
                setSlowMouse();
            }
        }
        lastRock = Rock2;
    }

    public void setFastMouse()
    {
        getBot().setMouseMoveProfile(new MouseMoveProfile()
                .setNoise(0)
                .setDeviation(0)
                .setOvershoots(0)
                .setSpeedBaseTime(25)
                .setFlowSpeedModifier(0)
                .setMinOvershootDistance(0)
                .setFlowVariety(MouseMoveProfile.FlowVariety.MEDIUM)
                .setOvershoots(0)
        );
    }

    public void setSlowMouse()
    {
        getBot().setMouseMoveProfile(new MouseMoveProfile()
                .setNoise(2.15)
                .setDeviation(7)
                .setOvershoots(2)
                .setSpeedBaseTime(185)
                .setFlowSpeedModifier(0)
                .setMinOvershootDistance(25)
                .setFlowVariety(MouseMoveProfile.FlowVariety.MEDIUM)
                .setOvershoots(2)
        );
    }
}