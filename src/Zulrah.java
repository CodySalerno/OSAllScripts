import com.sun.xml.internal.ws.api.server.LazyMOMProvider;
import org.osbot.rs07.api.Prayer;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Projectile;
import org.osbot.rs07.api.ui.PrayerButton;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.event.interaction.MouseMoveProfile;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.Condition;
import util.FormattingForPaint;

import java.util.List;

@ScriptManifest(name = "Zulrah", author = "Iownreality1", info = "Kills zulrah", version = 0.1, logo = "")
public class Zulrah extends Script
{

    Area Zulandre = new Area(3728, 5654, 3760, 5692);
    Area Dock = new Area(3728, 5654, 3760, 5692);
    boolean needsToBank = false;
    Position currentPosition;
    int rotationPosition = 0;
    int rotationNumber = 0;
    double startTime;
    double endTime;
    NPC zulrah;
    boolean startAgain = false;
    boolean t1Suspended = false;
    boolean timeStarted = false;
    Position pos1;
    Position pos2;
    Position pos3;
    Position pos4;
    Position pos5;
    Position pos6;
    double LastEat;
    double walkTimer;
    boolean walkTooLong = false;
    /*
                        if (rotationPosition == 10 && rotationNumber == 1)
    {
        if (projectiles.getAll().contains(1046))
        {
            setFastMouse();
            getBot().getScriptExecutor().pause();
            prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
            setSlowMouse();
            attackZulrah();
            getBot().getScriptExecutor().resume();
        }
        if (projectiles.getAll().contains(1044))
        {
            getBot().getScriptExecutor().pause();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            setSlowMouse();
            attackZulrah();
            getBot().getScriptExecutor().resume();
        }
    }*/

    Thread attackListener = new Thread(() ->
    {
        while (getBot().getScriptExecutor().isRunning() && !getBot().getScriptExecutor().isPaused() && !getBot().getScriptExecutor().isSuspended())
        {
            try
            {
                //log(zulrah.getRotation()); 1281 @spot1 1537 @spot2
                if (System.currentTimeMillis() - walkTimer > 2400)
                {
                    walkTooLong = true;
                }
                else
                {
                    walkTooLong = false;
                }
                if ((rotationPosition == 10 && rotationNumber == 1) || (rotationPosition == 11 && rotationNumber == 2) || (rotationPosition == 9 && rotationNumber == 3) || (rotationPosition == 9 && rotationNumber == 4))
                {
                    if (getProjectiles().filter(f -> f.getId() == 1046).size() > 0)
                    {
                        if (!prayer.isActivated(PrayerButton.PROTECT_FROM_MISSILES))
                        {
                            setFastMouse();
                            prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
                            setSlowMouse();
                            attackZulrah();
                        }
                    }
                    if (getProjectiles().filter(f -> f.getId() == 1044).size() > 0)
                    {
                        if (!prayer.isActivated(PrayerButton.PROTECT_FROM_MAGIC))
                        {
                            setFastMouse();
                            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
                            setSlowMouse();
                            attackZulrah();
                        }
                    }
                }
                //log("mage proj: " + getProjectiles().filter(f -> f.getId() == 1046).size());
                //log("Range proj: " + getProjectiles().filter(f -> f.getId() == 1044).size());

            }
            catch (Exception e)
            {
                log(e.toString());
                //should say throw e
            }
        }
    });

    Thread thread1 = new Thread(() ->
    {
        while (getBot().getScriptExecutor().isRunning() && !getBot().getScriptExecutor().isPaused() && !getBot().getScriptExecutor().isSuspended())
        {
            try
            {
                if (npcs.closest("Zulrah") != null)
                {
                    zulrah = npcs.closest("Zulrah");
                    if (rotationPosition == 5 && zulrah.getId() == 2044 && rotationNumber == 1)
                    {
                        rotationNumber++;
                        Thread.sleep(4000);
                    }
                    if (zulrah.getAnimation() == 5072)
                    {
                        if ((rotationPosition == 12 && rotationNumber == 2) || (rotationPosition == 11 && rotationNumber == 1) || (rotationPosition == 10 && rotationNumber == 3) )
                        {
                            rotationPosition = 1;
                            rotationNumber = 0;
                            log("set position and number to 1 and 0");
                        }
                        else
                        {
                            if (rotationNumber == 0)
                            {
                                Thread.sleep(2000);
                            }
                            rotationPosition++;
                            log("Rotation Position changed to: " + rotationPosition);
                        }
                        Thread.sleep(4000);
                    }
                    else if (zulrah.getAnimation() == 5073)
                    {
                        if ((rotationPosition == 12 && rotationNumber == 2) || (rotationPosition == 11 && rotationNumber == 1) || (rotationPosition == 10 && rotationNumber == 3) )
                        {
                            rotationPosition = 1;
                            rotationNumber = 0;
                            log("set position and number to 1 and 0");
                        }
                        else
                        {
                            if (rotationNumber == 0)
                            {
                                Thread.sleep(2000);
                            }
                            rotationPosition++;
                            log("Rotation Position changed to: " + rotationPosition);
                        }
                        Thread.sleep(4000);
                    }
                }
            }
            catch (InterruptedException e)
            {
                log(e.toString());
                //should say throw e
            }
            catch (NullPointerException e)
            {

            }
        }
    });



    @Override
    public void onStart()
    {
        thread1.start();
        attackListener.start();
        LastEat = System.currentTimeMillis();
    }

    @Override
    public int onLoop() throws InterruptedException
    {

        if (npcs.closest("Zulrah") != null)
        {
            zulrah = npcs.closest("Zulrah");
        }
        if (!myPlayer().isInteracting(npcs.closest("Zulrah")) && npcs.closest("Zulrah") != null)
        {
            log("attack zulrah");
            npcs.closest("Zulrah").interact("Attack");
        }
        if (myPlayer().getHealthPercent() < 55)
        {
            log("need to eat");
            setFastMouse();
            if (inventory.getItem("Shark") != null && inventory.getItem("Cooked karambwan") != null && (System.currentTimeMillis()-LastEat > 1800))
            {
                inventory.getItem("Shark").interact("Eat");
                inventory.getItem("Cooked karambwan").interact("Eat");
                LastEat = System.currentTimeMillis();
            }
            else if (inventory.getItem("Shark") != null)
            {
                inventory.getItem("Shark").interact("Eat");
                LastEat = System.currentTimeMillis();
            }
            else if (inventory.getItem("Cooked karambwan") != null)
            {
                inventory.getItem("Cooked karambwan").interact("Eat");
                LastEat = System.currentTimeMillis();
            }
            else inventory.getItem("Teleport to house").interact("Break");
            setSlowMouse();
        }
        if (Zulandre.contains(myPosition()))
        {

        }
        if (Dock.contains(myPosition()))
        {

        }
        if (getWidgets().getWidgetContainingText("The priestess rows you to Zulrah") != null)
        {
            currentPosition = myPosition();
            pos1 = new Position(currentPosition.getX()+4, currentPosition.getY()+10, 0); //
            pos2 = new Position(currentPosition.getX()+6, currentPosition.getY()+9, 0); //
            pos3 = new Position(currentPosition.getX()+4, currentPosition.getY()+4, 0); //
            pos4 = new Position(currentPosition.getX()-4, currentPosition.getY()+4, 0); //
            pos5 = new Position(currentPosition.getX()-5, currentPosition.getY()+7, 0); //
            pos6 = new Position(currentPosition.getX()-4, currentPosition.getY()+10, 0); //

            myWalkingEvents(pos2.getX(), pos2.getY());
            rotationPosition = 1;
            putMageGearOn();
            attackZulrah();
        }
        if (rotationPosition == 1)
        {
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
            log("rotation position 1");
            putMageGearOn();
            myWalkingEvents(pos2.getX(), pos2.getY());
            rotationPosition = 1;
        }
        if (rotationPosition == 2 && rotationNumber == 0)
        {
            log("rotation position 2");
            if (rotationNumber == 0)
            {
                setFastMouse();
                prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
                setSlowMouse();
            }
            if (npcs.closest("Zulrah") != null)
            {
                zulrah = npcs.closest("Zulrah");
            }
            if (zulrah.getId() == 2042)
            {
                log("zulrah is green setting rotation to 1.");
                rotationNumber = 1;
            }
            if (zulrah.getId() == 2044)
            {
                log("zulrah is blue setting rotation to 2 and putting range gear on.");
                rotationNumber = 2;
                setFastMouse();
                prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
                setSlowMouse();
                putRangeGearOn();
            }
            if (zulrah.getId() == 2043)
            {
                log("zulrah is red setting rotation to 5");
                rotationNumber = 5;
            }
            attackZulrah();
        }

        if (rotationPosition == 2 && rotationNumber == 5)
        {
            log("rotation position 2");
            prayer.deactivateAll();
            if (myPosition().equals(pos2) && zulrah.getRotation() == 1537)
            {
                myWalkingEvents(pos3.getX(), pos3.getY());
            }
            if (myPosition().equals(pos3) && zulrah.getRotation() == 1281)
            {
                myWalkingEvents(pos2.getX(), pos2.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 3 && rotationNumber == 5)
        {
            log("rotation position 3");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            setSlowMouse();
            putRangeGearOn();
            attackZulrah();
            if (!myPosition().equals(pos2))
            {
                myWalkingEvents(pos2.getX(),pos2.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 4 && rotationNumber == 5)
        {
            log("rotation position 4");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
            setSlowMouse();
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 5 && rotationNumber == 5)
        {
            log("rotation position 5");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            setSlowMouse();
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
            attackZulrah();
            if (zulrah.getId() == 2044)
            {
                rotationNumber = 3;
            }
            else if (zulrah.getId() == 2043)
            {
                rotationNumber = 4;
            }
        }
        if (rotationPosition == 5 && rotationNumber == 3)
        {
            log("rotation position 5");
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 5 && rotationNumber == 4)
        {
            log("rotation position 5");
            putRangeGearOn();
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 6 && rotationNumber == 3)
        {
            log("rotation position 6");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            setSlowMouse();
            putRangeGearOn();
            attackZulrah();
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 7 && rotationNumber == 3)
        {
            log("rotation position 7");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
            setSlowMouse();
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 8 && rotationNumber == 3)
        {
            log("rotation position 8");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            setSlowMouse();
            putRangeGearOn();
            attackZulrah();
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 9 && rotationNumber == 3)
        {
            log("rotation position 9 jad");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
            setSlowMouse();
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 10 && rotationNumber == 3)
        {
            if (!myPosition().equals(pos2))
            {
                myWalkingEvents(pos2.getX(),pos2.getY());
            }
            attackZulrah();
            log("rotation position 10");
            putMageGearOn();
            attackZulrah();
            if (myPosition().equals(pos2) && zulrah.getRotation() == 1537)
            {
                myWalkingEvents(pos3.getX(), pos3.getY());
            }
            if (myPosition().equals(pos3) && zulrah.getRotation() == 1281)
            {
                myWalkingEvents(pos2.getX(), pos2.getY());
            }
        }

        if (rotationPosition == 6 && rotationNumber == 4)
        {
            log("rotation position 6");
            setFastMouse();
            prayer.deactivateAll();
            setSlowMouse();
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 7 && rotationNumber == 4)
        {
            log("rotation position 7");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
            setSlowMouse();
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(currentPosition))
            {
                myWalkingEvents(currentPosition.getX(),currentPosition.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 8 && rotationNumber == 4)
        {
            log("rotation position 8");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            setSlowMouse();
            putRangeGearOn();
            attackZulrah();
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 9 && rotationNumber == 4)
        {
            log("rotation position 9 jad");
            putMageGearOn();
            attackZulrah();
        }

        if (rotationPosition == 10 && rotationNumber == 4)
        {
            if (!myPosition().equals(pos2))
            {
                myWalkingEvents(pos2.getX(),pos2.getY());
            }
            attackZulrah();
            log("rotation position 10");
            putMageGearOn();
            attackZulrah();
            if (myPosition().equals(pos2) && zulrah.getRotation() == 1537)
            {
                myWalkingEvents(pos3.getX(), pos3.getY());
            }
            if (myPosition().equals(pos3) && zulrah.getRotation() == 1281)
            {
                myWalkingEvents(pos2.getX(), pos2.getY());
            }
        }

        if (rotationPosition == 3 && rotationNumber == 2)
        {
            log("rotation position 3");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
            setSlowMouse();
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 4 && rotationNumber == 2)
        {
            log("rotation position 4");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            setSlowMouse();
            putRangeGearOn();
            attackZulrah();
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 5 && rotationNumber == 2)
        {
            log("rotation position 5");
            setFastMouse();
            prayer.deactivateAll();
            setSlowMouse();
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 6 && rotationNumber == 2)
        {
            log("rotation position 6");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MISSILES,true);
            setSlowMouse();
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 7 && rotationNumber == 2)
        {
            log("rotation position 7");
            setFastMouse();
            prayer.deactivateAll();
            setSlowMouse();
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 8 && rotationNumber == 2)
        {
            log("rotation position 8");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC,true);
            setSlowMouse();
            putRangeGearOn();
            attackZulrah();
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 9 && rotationNumber == 2)
        {
            log("rotation position 9");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MISSILES,true);
            setSlowMouse();
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 10 && rotationNumber == 2)
        {
            log("rotation position 10");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC,true);
            setSlowMouse();
            putRangeGearOn();
            attackZulrah();
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 11 && rotationNumber == 2)
        {
            log("rotation position 11");
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 12 && rotationNumber == 2)
        {
            log("rotation position 12");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC,true);
            setSlowMouse();
            putRangeGearOn();
            attackZulrah();
            if (!myPosition().equals(pos1))
            {
                myWalkingEvents(pos1.getX(),pos1.getY());
            }
            attackZulrah();
        }



        if (rotationPosition == 3 && rotationNumber == 1)
        {
            log("rotation position 3");
            prayer.deactivateAll();
            if (!myPosition().equals(pos5) && zulrah.getRotation() != 511 && zulrah.getRotation() != 804)
            {
                myWalkingEvents(pos5.getX(), pos5.getY());
            }
            if (myPosition().equals(pos5) && zulrah.getRotation() == 511)
            {
                myWalkingEvents(pos6.getX(), pos6.getY());
            }
            if (myPosition().equals(pos6) && zulrah.getRotation() == 804)
            {
                myWalkingEvents(pos5.getX(), pos5.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 4 && rotationNumber == 1)
        {
            log("rotation position 4");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            setSlowMouse();
            putRangeGearOn();
            attackZulrah();
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
            attackZulrah();
        }
        if (rotationPosition == 5 && rotationNumber == 1)
        {
            log("rotation position 5");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
            setSlowMouse();
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
            attackZulrah();
        }
        if (rotationPosition == 6 && rotationNumber == 1)
        {
            log("rotation position 6");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            setSlowMouse();
            putRangeGearOn();
            attackZulrah();
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 7 && rotationNumber == 1)
        {
            log("rotation position 7");
            prayer.deactivateAll();
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 8 && rotationNumber == 1)
        {
            log("rotation position 8");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
            setSlowMouse();
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 9 && rotationNumber == 1)
        {
            log("rotation position 9");
            setFastMouse();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            setSlowMouse();
            putRangeGearOn();
            attackZulrah();
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
            attackZulrah();
        }

        if (rotationPosition == 10 && rotationNumber == 1)
        {
            putMageGearOn();
            attackZulrah();
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
            setSlowMouse();
            attackZulrah();
        }
        if (rotationPosition == 11 && rotationNumber == 1)
        {
            log("rotation position 11");
            startAgain = true;
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            putRangeGearOn();
            attackZulrah();
            if (!myPosition().equals(pos1))
            {
                myWalkingEvents(pos1.getX(),pos1.getY());
            }
            attackZulrah();
        }
        return random(200,400);
    }

    public void myWalkingEvents (int x, int y)
    {
        walkTimer = System.currentTimeMillis();
        WalkingEvent myEvent = new WalkingEvent(new Position(x,y,0));
        myEvent.setMinDistanceThreshold(0);
        myEvent.setHighBreakPriority(true);
        myEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                log("how long we been walking: " + (System.currentTimeMillis() -walkTimer));
                return walkTooLong;
            }
        });
        execute(myEvent);
        attackZulrah();
        log("leaving walking");
    }

    public void setFastMouse()
    {
        getBot().setMouseMoveProfile(new MouseMoveProfile()
                .setNoise(0)
                .setDeviation(0)
                .setOvershoots(0)
                .setSpeedBaseTime(0)
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
                .setFlowSpeedModifier(1.0)
                .setMinOvershootDistance(25)
                .setFlowVariety(MouseMoveProfile.FlowVariety.MEDIUM)
                .setOvershoots(2)
        );
    }

    public void attackZulrah()
    {
        if (!myPlayer().isInteracting(npcs.closest("Zulrah")) && npcs.closest("Zulrah") != null)
        {
            log("attack zulrah");
            npcs.closest("Zulrah").interact("Attack");
        }
    }
    public void putMageGearOn()
    {
        setFastMouse();
        if (equipment.contains("Amulet of fury"))
        {
            inventory.getItem("Occult necklace").interact("Wear");
        }
        if (equipment.contains("Guthix d'hide body"))
        {
            inventory.getItem("Dagon'hai robe top").interact("Wear");
        }
        if (equipment.contains("Guthix chaps"))
        {
            inventory.getItem("Dagon'hai robe bottom").interact("Wear");
        }
        if (equipment.contains("Toxic blowpipe"))
        {
            inventory.getItem("Trident of the swamp").interact("Wield");
        }
        setSlowMouse();
    }

    public void putRangeGearOn()
    {
        setFastMouse();
        if (equipment.contains("Occult necklace"))
        {
            inventory.getItem("Amulet of fury").interact("Wear");
        }
        if (equipment.contains("Dagon'hai robe top"))
        {
            inventory.getItem("Guthix d'hide body").interact("Wear");
        }
        if (equipment.contains("Dagon'hai robe bottom"))
        {
            inventory.getItem("Guthix chaps").interact("Wear");
        }
        if (equipment.contains("Trident of the swamp"))
        {
            inventory.getItem("Toxic blowpipe").interact("Wield");
        }
        setSlowMouse();
    }

}
