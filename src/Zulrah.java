import com.sun.xml.internal.ws.api.server.LazyMOMProvider;
import org.osbot.rs07.api.Prayer;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.PrayerButton;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import util.FormattingForPaint;

@ScriptManifest(name = "Zulrah", author = "Iownreality1", info = "Kills zulrah", version = 0.1, logo = "")
public class Zulrah extends Script
{

    Area Zulandre = new Area(3728, 5654, 3760, 5692);
    Area Dock = new Area(3728, 5654, 3760, 5692);
    boolean needsToBank = false;
    Position currentPosition;
    int rotationPosition = 0;
    int rotationNumber = 0;
    NPC zulrah;
    boolean startAgain = false;
    boolean t1Suspended = false;
    Position pos1;
    Position pos2;
    Position pos3;
    Position pos4;
    Position pos5;
    Position pos6;


    Thread thread1 = new Thread(() ->
    {
        while (getBot().getScriptExecutor().isRunning() && !getBot().getScriptExecutor().isPaused() && !getBot().getScriptExecutor().isSuspended())
        {
            try
            {
                if (npcs.closest("Zulrah") != null)
                {
                    NPC zulrah = npcs.closest("Zulrah");
                    if (zulrah.getAnimation() == 5072)
                    {
                        if (startAgain)
                        {
                            rotationPosition = 1;
                        }
                        else
                        {
                            rotationPosition++;
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
            npcs.closest("Zulrah").interact("Attack");
        }
        if (Zulandre.contains(myPosition()))
        {

        }
        else if (Dock.contains(myPosition()))
        {

        }
        else if (getWidgets().getWidgetContainingText("The priestess rows you to Zulrah") != null)
        {
            currentPosition = myPosition();
            pos1 = new Position(currentPosition.getX()+4, currentPosition.getY()+10, 0); //
            pos2 = new Position(currentPosition.getX()+6, currentPosition.getY()+9, 0); //
            pos3 = new Position(currentPosition.getX()+4, currentPosition.getY()+4, 0); //
            pos4 = new Position(currentPosition.getX()-4, currentPosition.getY()+4, 0); //
            pos5 = new Position(currentPosition.getX()-5, currentPosition.getY()+7, 0); //
            pos6 = new Position(currentPosition.getX()-4, currentPosition.getY()+10, 0); //

            myWalkingEvents(currentPosition.getX()+4, currentPosition.getY()+10);
            rotationPosition = 1;
            putMageGearOn();
        }
        else if (rotationPosition == 1)
        {
            myWalkingEvents(currentPosition.getX()+4, currentPosition.getY()+10);
            rotationPosition = 1;
            putMageGearOn();
        }
        else if (rotationPosition == 2 && npcs.closest("Zulrah").getId() == 2042 && rotationNumber == 0)
        {
            prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
            rotationNumber = 1;
        }
        else if (rotationPosition == 3 && rotationNumber == 1)
        {
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
        }

        else if (rotationPosition == 4 && rotationNumber == 1)
        {
            putRangeGearOn();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
        }
        else if (rotationPosition == 5 && rotationNumber == 1)
        {
            putMageGearOn();
            prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
        }
        else if (rotationNumber == 6 && rotationNumber == 1)
        {
            putRangeGearOn();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
        }

        else if (rotationNumber == 7 && rotationNumber == 1)
        {
            putMageGearOn();
            prayer.deactivateAll();
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
        }

        else if (rotationNumber == 8 && rotationNumber == 1)
        {
            putMageGearOn();
            prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
            if (!myPosition().equals(pos4))
            {
                myWalkingEvents(pos4.getX(),pos4.getY());
            }
        }

        else if (rotationPosition == 9 && rotationNumber == 1)
        {
            putRangeGearOn();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            if (!myPosition().equals(pos3))
            {
                myWalkingEvents(pos3.getX(),pos3.getY());
            }
        }

        else if (rotationPosition == 10 && rotationNumber == 1)
        {
            putMageGearOn();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            if (!myPosition().equals(pos1))
            {
                myWalkingEvents(pos1.getX(),pos1.getY());
            }
            if (projectiles.getAll().contains(1046) && !prayer.isActivated(PrayerButton.PROTECT_FROM_MISSILES))
            {
                prayer.set(PrayerButton.PROTECT_FROM_MISSILES, true);
            }
            if (projectiles.getAll().contains(1044) && !prayer.isActivated(PrayerButton.PROTECT_FROM_MAGIC))
            {
                prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            }

        }
        else if (rotationPosition == 11 && rotationNumber == 1)
        {
            startAgain = true;
            putRangeGearOn();
            prayer.set(PrayerButton.PROTECT_FROM_MAGIC, true);
            if (!myPosition().equals(pos1))
            {
                myWalkingEvents(pos1.getX(),pos1.getY());
            }
        }
        return random(200,500);
    }

    public void myWalkingEvents (int x, int y)
    {
        WalkingEvent myEvent = new WalkingEvent(new Position(x,y,0));
        myEvent.setMinDistanceThreshold(0);
        execute(myEvent);
    }

    public void putMageGearOn()
    {
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
    }

    public void putRangeGearOn()
    {
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
    }

}
