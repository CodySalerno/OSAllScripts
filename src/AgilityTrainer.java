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
public class AgilityTrainer extends  Script {
    final Position GnomeStrongholdStart = new Position(2474, 3436, 0);
    final Area GnomeAreaGround = new Area(2467, 3413, 2492, 3441);
    final Area GnomeAreaMiddle = new Area(2467, 3413, 2492, 3441);
    final Area GnomeAreaTop = new Area(2467, 3413, 2492, 3441);
    final Position DraynorVillageStart = new Position(3103, 3279, 0);
    final Position VarrockStart = new Position(3221, 3414, 0);
    final Position CanifisStart = new Position(3507, 3488, 0);
    final Position[] StartList = {GnomeStrongholdStart, DraynorVillageStart, VarrockStart, CanifisStart};
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

        if (!PriestInPerilDone && skills.getDynamic(Skill.AGILITY) >= 40) {
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
        g.drawString("Running Time: " + (FormattingForPaint.formatTime(System.currentTimeMillis() - startTime)), 10, 250);
        g.drawString("Agility xp Gained: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXP(Skill.AGILITY)), 10, 270);
        g.drawString("Agility xp/hr: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXPPerHour(Skill.AGILITY)), 10, 290);
        g.drawString("Current Level: " + skills.getStatic(Skill.AGILITY), 10, 310);
        int xpTillDone = (skills.getExperienceForLevel(30) - skills.getExperience(Skill.AGILITY));
        int xpPerSecond = (experienceTracker.getGainedXPPerHour(Skill.AGILITY)) / 3600;
        long timeTillDone = (xpTillDone / xpPerSecond);
        String S = FormattingForPaint.formatTimeSeconds(timeTillDone);
        g.drawString("Time until level 30: " + S, 10, 330);
    }

    @Override
    public final int onLoop() throws InterruptedException
    {
        //TODO:everything after gnome
        //TODO:fix gnome sleep timers
        int level = skills.getDynamic(Skill.AGILITY);
        String[] courses = {"Gnome Stronghold", "Draynor Village", "Varrock", "Canifis"};
        int[] courseReqs = {1, 10, 30, 40};

        if ((settings.getRunEnergy() < 20) || (!settings.isRunning()))
        {

            inventory.getItem("Stamina potion(4)").interact();
        }
        for (int i = 0; i < courseReqs.length; i++)
        {
            if (courseReqs[i] <= level) {
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
                } else if (!inventory.contains("Ardougne teleport"))
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
                    sleep(random(1200, 1800));
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
        Area GnomeEnd = new Area(2471, 3435, 2489, 3440);//0
        Area firstObstacle = new Area(2470, 3430, 2477, 3425);//0
        Area secondObstacle = new Area(2471, 3422, 2476, 3424);//1
        secondObstacle.setPlane(1);
        Area thirdObstacle = new Area(2472, 3421, 2477, 3418);//2
        thirdObstacle.setPlane(2);
        Area fourthObstacle = new Area(2483, 3421, 2488, 3418);//2
        fourthObstacle.setPlane(2);
        Area fifthObstacle = new Area(2480, 3425, 2489, 3417);//0
        Area sixthObstacle = new Area(2480, 3426, 2490, 3432);//0

        if (GnomeEnd.contains(myPosition()))
        {
            obstacleSolver("Log balance","Walk-across",GnomeEnd,firstObstacle);
        }
        if (firstObstacle.contains(myPosition()))
        {
            obstacleSolver("Obstacle net", "Climb-over", firstObstacle, secondObstacle);
        }
        if (secondObstacle.contains(myPosition()))
        {
            obstacleSolver("Tree branch", "Climb", secondObstacle, thirdObstacle);
        }
        if (thirdObstacle.contains(myPosition()))
        {
            obstacleSolver("Balancing rope", "Walk-on", thirdObstacle, fourthObstacle);
        }
        if (fourthObstacle.contains(myPosition()) && (myPosition().getZ() > 0))
        {
            obstacleSolver("Tree branch", "Climb-down", fourthObstacle, fifthObstacle);
        }
        if (fifthObstacle.contains(myPosition()) && (myPosition().getZ() == 0))
        {
            obstacleSolver("Obstacle net", "Climb-over", fifthObstacle, sixthObstacle);
        }
        else if (sixthObstacle.contains(myPosition()))
        {
            obstacleSolver("Obstacle pipe", "Squeeze-through", sixthObstacle, GnomeEnd);
            log("last pipe");
            walking.walk(objects.closest("Obstacle pipe"));
            sleep(random(200, 400));
            objects.closest("Obstacle pipe").interact("Squeeze-through");
            sleep(random(6000, 6600));
        }
        else
        {
            sleep(random(3000, 3600));
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

    public void Varrock() throws InterruptedException
    {
        Area allofVarrock = new Area(3190,3383,3240,3419);
        Area startofVarrock = new Area(3221,3413,3223,3415);
        Area VarrockFall = new Area(3208,3410,3214,3421);
        Area firstObstacle = new Area(3214,3410,3219,3419);
        firstObstacle.setPlane(3);
        Area secondObstacle = new Area(3201,3413,3208,3417);
        secondObstacle.setPlane(3);
        Area thirdObstacle = new Area(3193,3416,3197,3416);
        thirdObstacle.setPlane(1);
        Area fourthObstacle = new Area(3192,3402,3198,3406);
        fourthObstacle.setPlane(3);
        Area fifthObstacle = new Area(3183,3383,3208,3403);
        fifthObstacle.setPlane(3);
        Area sixthObstacle = new Area(3218,3393,3232,3402);
        sixthObstacle.setPlane(3);
        Area seventhObstacle = new Area(3236,3403,3240,3408);
        seventhObstacle.setPlane(3);
        Area eighthOstable = new Area(3236,3410,3240,3415);
        eighthOstable.setPlane(3);

        if (allofVarrock.contains(myPosition()))
        {
            walking.webWalk(startofVarrock);
            sleep(random(600,1200));
        }
        if (startofVarrock.contains(myPosition()))
        {
            obstacleSolver("Rough wall","Climb",startofVarrock,firstObstacle);
        }
        if (firstObstacle.contains(myPosition()))
        {
            obstacleSolver("Clothes line","Cross",firstObstacle,secondObstacle);
        }
        if (secondObstacle.contains(myPosition()))
        {
            obstacleSolver("Gap","Leap",secondObstacle,thirdObstacle);
        }
        if (thirdObstacle.contains(myPosition()))
        {
            obstacleSolver("Wall","Balance",thirdObstacle,fourthObstacle);
        }
        if (fourthObstacle.contains(myPosition()))
        {
            obstacleSolver("Gap","Leap",fourthObstacle,fifthObstacle);
        }
        if (fifthObstacle.contains(myPosition()))
        {
            obstacleSolver("Gap","Leap",fifthObstacle,sixthObstacle);
        }
        if (sixthObstacle.contains(myPosition()))
        {
            obstacleSolver("Gap","Leap",sixthObstacle,seventhObstacle);
        }
        if (seventhObstacle.contains(myPosition()))
        {
            obstacleSolver("Ledge","Hurdle",seventhObstacle,eighthOstable);
        }
        if (eighthOstable.contains(myPosition()))
        {
            obstacleSolver("Edge","Jump-off",eighthOstable,startofVarrock);
        }

    }

    public void Draynor() throws InterruptedException
    {
        log("In draynor method");
        Area allOfDraynor = new Area(3066, 3238, 3111, 3290);
        Area startDraynor = new Area(3103, 3274, 3111, 3284);
        Area firstObstacle = new Area(3097, 3277, 3102, 3281);
        firstObstacle.setPlane(3);
        Area secondObstacle = new Area(3088, 3273, 3091, 3276);
        secondObstacle.setPlane(3);
        Area thirdObstacle = new Area(3089, 3265, 3094, 3267);
        thirdObstacle.setPlane(3);
        Area fourthObstacle = new Area(3088, 3257, 3088, 3261);
        fourthObstacle.setPlane(3);
        Area fifthObstacle = new Area(3088, 3255, 3094, 3255);
        fifthObstacle.setPlane(3);
        Area sixthObstacle = new Area(3096, 3256, 3101, 3261);
        sixthObstacle.setPlane(3);
        Area endOfDraynor = new Area(3102, 3259, 3105, 3263);
        Area draynorFall = new Area(3089, 3256, 3095, 3264);

        if (allOfDraynor.contains(myPosition()))
        {
            log("walking to draynor");
            walking.webWalk(startDraynor);
            sleep(random(600, 850));
        }
        if (startDraynor.contains(myPosition()))
        {
            obstacleSolver("Rough wall","Climb", startDraynor, firstObstacle);
        }
        if (firstObstacle.contains(myPosition()))
        {
            obstacleSolver("Tightrope","Cross", firstObstacle, secondObstacle);
        }
        if (secondObstacle.contains(myPosition()))
        {
            obstacleSolver("Tightrope","Cross", secondObstacle, thirdObstacle);
        }
        if (thirdObstacle.contains(myPosition()))
        {
            obstacleSolver("Narrow wall","Balance", thirdObstacle, fourthObstacle);
        }
        if (fourthObstacle.contains(myPosition()))
        {
            obstacleSolver("Wall","Jump-up", fourthObstacle, fifthObstacle);
        }
        if (fifthObstacle.contains(myPosition()))
        {
            obstacleSolver("Gap","Jump", fifthObstacle, sixthObstacle);
        }
        if (sixthObstacle.contains(myPosition()))
        {
            obstacleSolver("Crate","Climb-down", sixthObstacle, endOfDraynor);
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

    public void obstacleSolver(String obstacle, String interaction, Area areaStart, Area areaEnd) throws InterruptedException
    {
        pickUpMark(areaStart);
        objects.closest(obstacle).interact(interaction);
        Sleep.sleepUntil(() -> areaEnd.contains(myPosition()), 10000);
        sleep(random(1200,1800));
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
