import util.FormattingForPaint;
import util.Sleep;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;

@ScriptManifest(name = "AgilityTrainer", author = "Iownreality1", info = "Train agility", version = 0.1, logo = "")
public class AgilityTrainer extends  Script
{
    final Position GnomeStrongholdStart = new Position(2474,3436,0);
    final Area GnomeAreaGround = new Area(2467,3413,2492,3441);
    final Area GnomeAreaMiddle = new Area(2467,3413,2492,3441);
    final Area GnomeAreaTop = new Area(2467,3413,2492,3441);
    final Position DraynorVillageStart = new Position(3103,3279,0);
    final Position VarrockStart = new Position(3221,3414,0);
    final Position CanifisStart = new Position(3507,3488,0);
    final Position[] StartList = {GnomeStrongholdStart,DraynorVillageStart,VarrockStart,CanifisStart};
    boolean PriestInPerilDone = false;
    boolean nearCourse = false;
    int currentCourse;
    private long startTime;
    GroundItem groundItem;
    Position itemPosition;

    @Override
    public final void onStart()
    {
        GnomeAreaGround.setPlane(0);
        GnomeAreaMiddle.setPlane(1);
        GnomeAreaTop.setPlane(2);
        PriestInPerilDone = (configs.get(302) == 61);

        if (!PriestInPerilDone && skills.getDynamic(Skill.AGILITY) >= 40)
        {
            PriestQuest();
        }
        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.AGILITY);
    }

    @Override
    public void onPaint(final Graphics2D g)
    {
        Font font = new Font("Open Sans", Font.BOLD, 18);
        g.setFont(font);
        g.setColor(Color.green);
        g.drawString("Running Time: " + (FormattingForPaint.formatTime(System.currentTimeMillis()-startTime)),10,250);
        g.drawString("Agility xp Gained: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXP(Skill.AGILITY)),10,270);
        g.drawString("Agility xp/hr: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXPPerHour(Skill.AGILITY)),10,290);
        g.drawString("Current Level: " + skills.getStatic(Skill.AGILITY),10,310 );
        int xpTillDone = (skills.getExperienceForLevel(30)-skills.getExperience(Skill.AGILITY));
        int xpPerSecond = (experienceTracker.getGainedXPPerHour(Skill.AGILITY))/3600;
        long timeTillDone = (xpTillDone/xpPerSecond);
        String S = FormattingForPaint.formatTimeSeconds(timeTillDone);
        g.drawString("Time until level 30: " + S ,10,330 );
    }

    @Override
    public final int onLoop()  throws InterruptedException
    {
        //TODO:everything after gnome
        //TODO:fix gnome sleep timers
        int level = skills.getDynamic(Skill.AGILITY);
        String[] courses =  {"Gnome Stronghold", "Draynor Village", "Varrock","Canifis"};
        int[] courseReqs = {1,10,30,40};

        if ((settings.getRunEnergy() < 20) || (!settings.isRunning()))
        {

            inventory.getItem("Stamina potion(4)").interact();
        }
        for (int i= 0; i < courseReqs.length; i++)
        {
            if (courseReqs[i] <= level)
            {
                currentCourse = i;
            }
        }
        log("Current course is " + courses[currentCourse]);
        switch (currentCourse)
        {
            case 0:
                nearCourse = (GnomeAreaGround.contains(myPosition()) || GnomeAreaMiddle.contains(myPosition()) || GnomeAreaTop.contains(myPosition()));
                if (nearCourse)
                {
                    Gnome();
                }
                else if (!inventory.contains("Ardougne teleport"))
                {
                    log("not in course");
                    try
                    {
                        npcs.closest("Banker").interact("Bank");
                        log("Moving to Bank");
                        Sleep.sleepUntil(() -> getBank().isOpen(), 30000);
                        sleep(random(1200, 1800));
                        log("Waiting for bank to open");
                        getBank().withdraw("Ardougne teleport", 2);
                        sleep(random(1200, 1800));
                    }
                    catch (InterruptedException e)
                    {
                        log("HOLY SHIT IT THREW THE EXCEPTION ZACK LOOK AT THIS");
                        throw e;
                    }
                    catch (Exception e)
                    {
                        log(e.toString());
                    }
                }
                else
                {
                    log("not in course can tp");
                    inventory.getItem("Ardougne teleport").interact();
                    sleep(random(1200,1800));
                    walking.webWalk(StartList[currentCourse]);
                    nearCourse = true;
                }
                break;
            case 1:
                log("Go to draynor");
                Draynor();
                break;
        }
        return random(1200, 1800);
    }

    public void Gnome() throws InterruptedException
    {
        Area GnomeEnd = new Area(2471,3435,2489,3440);//0
        Area firstObstacle = new Area(2470,3430,2477,3425);//0
        Area secondObstacle = new Area(2471,3422,2476,3424);//1
        secondObstacle.setPlane(1);
        Area thirdObstacle = new Area(2472,3421,2477,3418);//2
        thirdObstacle.setPlane(2);
        Area fourthObstacle = new Area(2483,3421,2488,3418);//2
        fourthObstacle.setPlane(2);
        Area fifthObstacle = new Area(2480,3425,2489,3417);//0
        Area sixthObstacle = new Area(2480,3426,2490,3432);//0

        if (GnomeEnd.contains(myPosition()))
        {
            log("log balance");
            walking.walk(objects.closest("Log balance"));
            sleep(random(200,400));
            objects.closest("Log balance").interact("Walk-across");
            sleep(random(6000,6600));
        }
        else if (firstObstacle.contains(myPosition()))
        {
            log("obstacle net");
            walking.walk(objects.closest("Obstacle net"));
            sleep(random(200,400));
            objects.closest("Obstacle net").interact("Climb-over");
            sleep(random(6000,6600));
        }
        else if (secondObstacle.contains(myPosition()))
        {
            log("tree branch");
            walking.walk(objects.closest("Tree branch"));
            sleep(random(200,400));
            objects.closest("Tree branch").interact("Climb");
            sleep(random(6000,6600));
        }
        else if (thirdObstacle.contains(myPosition()))
        {
            log("balancing rope");
            walking.walk(objects.closest("Balancing rope"));
            sleep(random(200,400));
            objects.closest("Balancing rope").interact("Walk-on");
            sleep(random(6000,6600));
        }
        else if (fourthObstacle.contains(myPosition()) && (myPosition().getZ() > 0))
        {
            log("tree branch down");
            walking.walk(objects.closest("Tree branch"));
            sleep(random(200,400));
            objects.closest("Tree branch").interact("Climb-down");
            sleep(random(6000,6600));
        }
        else if (fifthObstacle.contains(myPosition()) && (myPosition().getZ() == 0))
        {
            log("obstacle net 2");
            walking.walk(objects.closest("Obstacle net"));
            sleep(random(200,400));
            objects.closest("Obstacle net").interact("Climb-over");
            sleep(random(6000,6600));
        }
        else if (sixthObstacle.contains(myPosition()))
        {
            log("last pipe");
            walking.walk(objects.closest("Obstacle pipe"));
            sleep(random(200,400));
            objects.closest("Obstacle pipe").interact("Squeeze-through");
            sleep(random(6000,6600));
        }
        else
        {
            sleep(random(3000,3600));
            if ((!(GnomeEnd.contains(myPosition()) || firstObstacle.contains(myPosition()) || secondObstacle.contains(myPosition())
                    || thirdObstacle.contains(myPosition()) || fourthObstacle.contains(myPosition()) || fifthObstacle.contains(myPosition())
                    || sixthObstacle.contains(myPosition()))) && myPosition().getZ() == 0)
            {
                log("fucked up");
                walking.webWalk(GnomeStrongholdStart);
            }
            else if ((!(GnomeEnd.contains(myPosition()) || firstObstacle.contains(myPosition()) || secondObstacle.contains(myPosition())
                    || thirdObstacle.contains(myPosition()) || fourthObstacle.contains(myPosition()) || fifthObstacle.contains(myPosition())
                    || sixthObstacle.contains(myPosition()))) && myPosition().getZ() == 1)
            {
                log("fucked up upstairs");

            }
            else if ((!(GnomeEnd.contains(myPosition()) || firstObstacle.contains(myPosition()) || secondObstacle.contains(myPosition())
                    || thirdObstacle.contains(myPosition()) || fourthObstacle.contains(myPosition()) || fifthObstacle.contains(myPosition())
                    || sixthObstacle.contains(myPosition()))) && myPosition().getZ() == 1)
            {
                log("fucked up upstairs x2");

            }
        }
    }

    public void Draynor() throws InterruptedException
    {
        log("In draynor method");
        Area allOfDraynor = new Area(3073,3239,3073,3290);
        Area startDraynor = new Area(3103,3274,3111,3284);
        Area firstObstacle = new Area(3097,3277,3102,3281);
        firstObstacle.setPlane(3);
        Area secondObstacle = new Area(3088,3273,3091,3276);
        secondObstacle.setPlane(3);
        Area thirdObstacle = new Area(3089,3265,3094,3267);
        thirdObstacle.setPlane(3);
        Area fourthObstacle = new Area(3088,3257,3088,3261);
        fourthObstacle.setPlane(3);
        Area fifthObstacle = new Area(3088,3255,3094,3255);
        fifthObstacle.setPlane(3);
        Area sixthObstacle = new Area(3096,3256,3101,3261);
        sixthObstacle.setPlane(3);
        Area endOfDraynor = new Area(3102,3259,3105,3263);
        Area draynorFall = new Area(3089, 3256, 3095,3264);

        if (allOfDraynor.contains(myPosition()))
        {
            log("walking to draynor");
            walking.webWalk(startDraynor);
            sleep(random(600,850));
        }
        if (startDraynor.contains(myPosition()))
        {
            log("walking to rough wall");
            objects.closest("Rough wall").interact("Climb");
            Sleep.sleepUntil(() -> firstObstacle.contains(myPosition()), 10000);
            sleep(random(1200,1800));
        }
        if (firstObstacle.contains(myPosition()))
        {
            pickUpMark(firstObstacle);
            log("walking to Tightrope");
            objects.closest("Tightrope").interact("Cross");
            Sleep.sleepUntil(() -> secondObstacle.contains(myPosition()), 10000);
            sleep(random(1200,1800));
        }
        if (secondObstacle.contains(myPosition()))
        {
            pickUpMark(secondObstacle);
            log("walking to Tightrope");
            objects.closest("Tightrope").interact("Cross");
            Sleep.sleepUntil(() -> thirdObstacle.contains(myPosition()), 10000);
            sleep(random(1200,1800));
        }
        if (thirdObstacle.contains(myPosition()))
        {
            pickUpMark(thirdObstacle);
            log("walking to Narrow wall");
            objects.closest("Narrow wall").interact("Balance");
            Sleep.sleepUntil(() -> fourthObstacle.contains(myPosition()), 10000);
            sleep(random(1200,1800));
        }
        if (fourthObstacle.contains(myPosition()))
        {
            pickUpMark(fourthObstacle);
            log("walking to Wall");
            objects.closest("Wall").interact("Jump-up");
            Sleep.sleepUntil(() -> fifthObstacle.contains(myPosition()), 10000);
            sleep(random(1200,1800));
        }
        if (fifthObstacle.contains(myPosition()))
        {
            pickUpMark(fifthObstacle);
            log("walking to Gap");
            objects.closest("Gap").interact("Jump");
            Sleep.sleepUntil(() -> sixthObstacle.contains(myPosition()), 10000);
            sleep(random(1200,1800));
        }
        if (sixthObstacle.contains(myPosition()))
        {
            pickUpMark(sixthObstacle);
            log("walking to Crate");
            objects.closest("Crate").interact("Climb-down");
            Sleep.sleepUntil(() -> endOfDraynor.contains(myPosition()), 10000);
            sleep(random(1800,2400));
        }
        if (endOfDraynor.contains(myPosition()))
        {
            walking.webWalk(startDraynor);
            Sleep.sleepUntil(() -> startDraynor.contains(myPosition()), 10000);
        }
        if (draynorFall.contains(myPosition()))
        {
            walking.webWalk(startDraynor);
            Sleep.sleepUntil(() -> startDraynor.contains(myPosition()), 10000);
        }
    }

    public void pickUpMark(Area currentArea) throws InterruptedException
    {
        groundItem = getGroundItems().closest(e -> e != null && e.getName().contains("Mark of grace"));

        if(groundItem != null)
        {
            itemPosition = groundItem.getPosition();
            if (currentArea.contains(itemPosition))
            {
                groundItem.interact("Take");
                sleep(random(2400,3000));
            }
        }
    }

    public void PriestQuest()
    {

    }
}
