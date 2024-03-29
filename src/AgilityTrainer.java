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
@ScriptManifest(name = "AgilityTrainer", author = "Iownreality1", info = "Train agility", version = 0.1, logo = "")
public class AgilityTrainer extends  Script {
    final Position GnomeStrongholdStart = new Position(2474, 3436, 0);
    final Area GnomeAreaGround = new Area(2467, 3413, 2492, 3441);
    final Area GnomeAreaMiddle = new Area(2467, 3413, 2492, 3441);
    final Area GnomeAreaTop = new Area(2467, 3413, 2492, 3441);
    final Area GnomeEnd = new Area(2471, 3435, 2489, 3440);
    final Area GnomeFirstObstacle = new Area(2470, 3430, 2477, 3425);
    final Area GnomeSecondObstacle = new Area(2471, 3422, 2476, 3424);
    final Area GnomeThirdObstacle = new Area(2472, 3421, 2477, 3418);
    final Area GnomeFourthObstacle = new Area(2483, 3421, 2488, 3418);
    final Area GnomeFifthObstacle = new Area(2480, 3425, 2489, 3417);
    final Area GnomeSixthObstacle = new Area(2480, 3426, 2490, 3432);
    final Position DraynorVillageStart = new Position(3103, 3279, 0);
    final Area allOfDraynorFloor = new Area(3066, 3238, 3111, 3290);
    final Area allOfDraynorRoof = new Area(3066,3238,3111,3290);
    final Area startDraynor = new Area(3103, 3274, 3111, 3284);
    final Area DraynorFirstObstacle = new Area(3097, 3277, 3102, 3281);
    final Area DraynorSecondObstacle = new Area(3088, 3273, 3091, 3276);
    final Area DraynorThirdObstacle = new Area(3089, 3265, 3094, 3267);
    final Area DraynorFourthObstacle = new Area(3088, 3257, 3088, 3261);
    final Area DraynorFifthObstacle = new Area(3088, 3255, 3094, 3255);
    final Area DraynorSixthObstacle = new Area(3096, 3256, 3101, 3261);
    final Area endOfDraynor = new Area(3102, 3259, 3105, 3263);
    final Area DraynorFall = new Area(3089, 3256, 3095, 3264);
    final Position VarrockStart = new Position(3221, 3414, 0);
    final Area AllVarrockGround = new Area(3190,3383,3240,3419);
    final Area AllVarrockMiddle = new Area(3190,3383,3240,3419);
    final Area AllVarrockRoof = new Area(3190,3383,3240,3419);
    final Area StartVarrock = new Area(3221,3413,3223,3415);
    final Area VarrockFirstObstacle = new Area(3214,3410,3219,3419);
    final Area VarrockSecondObstacle = new Area(3201,3413,3208,3417);
    final Area VarrockThirdObstacle = new Area(3193,3416,3197,3416);
    final Area VarrockFourthObstacle = new Area(3192,3402,3198,3406);
    final Area VarrockFifthObstacle = new Area(3183,3383,3202,3398);
    final Area VarrockFifthObstacle2 = new Area(3202,3395,3208,3403);
    final Area VarrockFifthHelper = new Area(3205,3398,3208,3403);
    final Area VarrockSixthObstacle = new Area(3218,3393,3232,3402);
    final Area VarrockSixthHelper = new Area(3227,3402,3232,3402);
    final Area VarrockSeventhObstacle = new Area(3236,3403,3240,3408);
    final Area VarrockEighthObstacle = new Area(3236,3410,3240,3415);
    final Position CanifisStart = new Position(3505, 3488, 0);
    final Area AllCanifisGround = new Area(3474,3476,3520,3513);
    final Area AllCanifisMiddle = new Area(3474,3476,3520,3513);
    final Area AllCanifisRoof = new Area(3474,3476,3520,3513);
    final Area StartCanifis = new Area(3503,3481,3511,3491);
    final Area CanifisFirstObstacle = new Area(3505,3492,3509,3498);
    final Area CanifisSecondObstacle = new Area(3496,3504,3503,3506);
    final Area CanifisThirdObstacle = new Area(3485,3498,3492,3504);
    final Area CanifisFourthObstacle = new Area(3475,3491,3479,3499);
    final Area CanifisFifthObstacle = new Area(3477, 3481,3484, 3487);
    final Area CanifisSixthObstacle = new Area(3489,3469,3503,3478);
    final Area CanifisSeventhObstacle = new Area(3508,3475,3516, 3483);
    final Position[] StartList = {GnomeStrongholdStart, DraynorVillageStart, VarrockStart, CanifisStart};
    boolean PriestInPerilDone = false;
    boolean nearCourse = false;
    int currentCourse;
    int nextLevel;
    private long startTime;
    GroundItem groundItem;
    Position itemPosition;
    final int[] courseReqs = {1, 10, 30, 40, 60};
    EnergyCheck stamina = new EnergyCheck(this);


    @Override
    public final void onStart() throws InterruptedException
    {
        log("New Start");
        GnomeAreaGround.setPlane(0);
        GnomeAreaMiddle.setPlane(1);
        GnomeAreaTop.setPlane(2);
        GnomeSecondObstacle.setPlane(1);
        GnomeThirdObstacle.setPlane(2);
        GnomeFourthObstacle.setPlane(2);
        allOfDraynorRoof.setPlane(3);
        DraynorFirstObstacle.setPlane(3);
        DraynorSecondObstacle.setPlane(3);
        DraynorThirdObstacle.setPlane(3);
        DraynorFourthObstacle.setPlane(3);
        DraynorFifthObstacle.setPlane(3);
        DraynorSixthObstacle.setPlane(3);
        AllVarrockMiddle.setPlane(1);
        AllVarrockRoof.setPlane(3);
        VarrockFirstObstacle.setPlane(3);
        VarrockSecondObstacle.setPlane(3);
        VarrockThirdObstacle.setPlane(1);
        VarrockFourthObstacle.setPlane(3);
        VarrockFifthObstacle.setPlane(3);
        VarrockFifthObstacle2.setPlane(3);
        VarrockFifthHelper.setPlane(3);
        VarrockSixthObstacle.setPlane(3);
        VarrockSixthHelper.setPlane(3);
        VarrockSeventhObstacle.setPlane(3);
        VarrockEighthObstacle.setPlane(3);
        AllCanifisGround.setPlane(0);
        AllCanifisMiddle.setPlane(2);
        AllCanifisRoof.setPlane(3);
        StartCanifis.setPlane(0);
        CanifisFirstObstacle.setPlane(2);
        CanifisSecondObstacle.setPlane(2);
        CanifisThirdObstacle.setPlane(2);
        CanifisFourthObstacle.setPlane(3);
        CanifisFifthObstacle.setPlane(2);
        CanifisSixthObstacle.setPlane(3);
        CanifisSeventhObstacle.setPlane(2);
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
        nextLevel = courseReqs[currentCourse+1];
        g.setFont(font);
        g.setColor(Color.green);
        g.drawString("Running Time: " + (FormattingForPaint.formatTime(System.currentTimeMillis() - startTime)), 10, 250);
        g.drawString("Agility xp Gained: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXP(Skill.AGILITY)), 10, 270);
        g.drawString("Agility xp/hr: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXPPerHour(Skill.AGILITY)), 10, 290);
        g.drawString("Current Level: " + skills.getStatic(Skill.AGILITY), 10, 310);
        int xpTillDone = (skills.getExperienceForLevel(nextLevel) - skills.getExperience(Skill.AGILITY));
        int xpPerSecond = (experienceTracker.getGainedXPPerHour(Skill.AGILITY)) / 3600;
        long timeTillDone = (xpTillDone / xpPerSecond);
        String S = FormattingForPaint.formatTimeSeconds(timeTillDone);
        g.drawString("Time until level " + nextLevel + ": " + S, 10, 330);
    }

    @Override
    public final int onLoop() throws InterruptedException
    {
        log("New Loop");
        stamina.Stamina();
        int level = skills.getDynamic(Skill.AGILITY);
        for (int i = 0; i < courseReqs.length; i++)
        {
            if (courseReqs[i] <= level)
            {
                currentCourse = i;
            }
        }
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
                nearCourse = (allOfDraynorFloor.contains(myPosition()) || allOfDraynorRoof.contains(myPosition()));
                if (nearCourse)
                {
                    Draynor();
                }
                else
                {
                    log("not in course");
                    walking.webWalk(StartList[currentCourse]);
                    nearCourse = true;
                }
                break;
            case 2:
                nearCourse = (AllVarrockGround.contains(myPosition()) || AllVarrockMiddle.contains(myPosition()) || AllVarrockRoof.contains(myPosition()));
                if (nearCourse)
                {
                    Varrock();
                }
                else
                {
                    log("Not in Varrock");
                    walking.webWalk(StartList[currentCourse]);
                    nearCourse = true;
                }
            case 3:
                nearCourse = (AllCanifisGround.contains(myPosition()) || AllCanifisMiddle.contains(myPosition()) || AllCanifisRoof.contains(myPosition()));
                if (nearCourse)
                {
                    Canifis();
                }
                else
                {
                    log("Not in Canifis");
                    walking.webWalk(StartList[currentCourse]);
                    nearCourse = true;
                }
        }
        return random(1200, 1800);
    }

    public void Gnome() throws InterruptedException
    {
        if (GnomeEnd.contains(myPosition()))
        {
            obstacleSolver("Log balance","Walk-across",GnomeEnd,GnomeFirstObstacle);
        }
        if (GnomeFirstObstacle.contains(myPosition()))
        {
            obstacleSolver("Obstacle net", "Climb-over", GnomeFirstObstacle, GnomeSecondObstacle);
        }
        if (GnomeSecondObstacle.contains(myPosition()))
        {
            obstacleSolver("Tree branch", "Climb", GnomeSecondObstacle, GnomeThirdObstacle);
        }
        if (GnomeThirdObstacle.contains(myPosition()))
        {
            obstacleSolver("Balancing rope", "Walk-on", GnomeThirdObstacle, GnomeFourthObstacle);
        }
        if (GnomeFourthObstacle.contains(myPosition()) && (myPosition().getZ() > 0))
        {
            obstacleSolver("Tree branch", "Climb-down", GnomeFourthObstacle, GnomeFifthObstacle);
        }
        if (GnomeFifthObstacle.contains(myPosition()) && (myPosition().getZ() == 0))
        {
            obstacleSolver("Obstacle net", "Climb-over", GnomeFifthObstacle, GnomeSixthObstacle);
        }
        else if (GnomeSixthObstacle.contains(myPosition()))
        {
            obstacleSolver("Obstacle pipe", "Squeeze-through", GnomeSixthObstacle, GnomeEnd);
            log("last pipe");
            walking.walk(objects.closest("Obstacle pipe"));
            sleep(random(200, 400));
            objects.closest("Obstacle pipe").interact("Squeeze-through");
            sleep(random(6000, 6600));
        }
        else
        {
            sleep(random(3000, 3600));
            if ((!(GnomeEnd.contains(myPosition()) || GnomeFirstObstacle.contains(myPosition()) || GnomeSecondObstacle.contains(myPosition())
                    || GnomeThirdObstacle.contains(myPosition()) || GnomeFourthObstacle.contains(myPosition()) || GnomeFifthObstacle.contains(myPosition())
                    || GnomeSixthObstacle.contains(myPosition()))) && myPosition().getZ() == 0)
            {
                log("fucked up");
                walking.walk(GnomeStrongholdStart);
            }
            else if ((!(GnomeEnd.contains(myPosition()) || GnomeFirstObstacle.contains(myPosition()) || GnomeSecondObstacle.contains(myPosition())
                    || GnomeThirdObstacle.contains(myPosition()) || GnomeFourthObstacle.contains(myPosition()) || GnomeFifthObstacle.contains(myPosition())
                    || GnomeSixthObstacle.contains(myPosition()))) && myPosition().getZ() == 1)
            {
                log("fucked up upstairs");

            }
            else if ((!(GnomeEnd.contains(myPosition()) || GnomeFirstObstacle.contains(myPosition()) || GnomeSecondObstacle.contains(myPosition())
                    || GnomeThirdObstacle.contains(myPosition()) || GnomeFourthObstacle.contains(myPosition()) || GnomeFifthObstacle.contains(myPosition())
                    || GnomeSixthObstacle.contains(myPosition()))) && myPosition().getZ() == 1)
            {
                log("fucked up upstairs x2");
            }
        }
    }

    public void Varrock() throws InterruptedException
    {
        if (AllVarrockGround.contains(myPosition()))
        {
            walking.walk(StartVarrock);
            sleep(random(600,1200));
        }
        if (StartVarrock.contains(myPosition()))
        {
            obstacleSolver("Rough wall","Climb",StartVarrock,VarrockFirstObstacle);
        }
        if (VarrockFirstObstacle.contains(myPosition()))
        {
            sleep(random(600,800));
            obstacleSolver("Clothes line","Cross",VarrockFirstObstacle,VarrockSecondObstacle);
        }
        if (VarrockSecondObstacle.contains(myPosition()))
        {
            sleep(random(1200,1500));
            obstacleSolver("Gap","Leap",VarrockSecondObstacle,VarrockThirdObstacle);
        }
        if (VarrockThirdObstacle.contains(myPosition()))
        {
            sleep(random(700,900));
            obstacleSolver("Wall","Balance",VarrockThirdObstacle,VarrockFourthObstacle);
        }
        if (VarrockFourthObstacle.contains(myPosition()))
        {
            obstacleSolver("Gap","Leap",VarrockFourthObstacle,VarrockFifthObstacle);
            sleep(random(1200,1800));
        }
        if (VarrockFifthObstacle.contains(myPosition()))
        {
            walking.walk(VarrockFifthObstacle2);
        }
        if (VarrockFifthObstacle2.contains(myPosition()))
        {
            obstacleSolver("Gap","Leap",VarrockFifthObstacle2,VarrockSixthObstacle);
        }
        if (VarrockSixthObstacle.contains(myPosition()))
        {
            obstacleSolver("Gap","Leap",VarrockSixthObstacle,VarrockSeventhObstacle,VarrockSixthHelper);
        }
        if (VarrockSeventhObstacle.contains(myPosition()))
        {
            log("in 7");
            obstacleSolver("Ledge","Hurdle",VarrockSeventhObstacle,VarrockEighthObstacle);
            sleep(random(400,600));//coordinate gets updated too early without
        }
        if (VarrockEighthObstacle.contains(myPosition()))
        {
            sleep(random(400,500));
            log("in 8");
            obstacleSolver("Edge","Jump-off",VarrockEighthObstacle,AllVarrockGround);
        }
    }

    public void Canifis() throws InterruptedException
    {
        if (AllCanifisGround.contains(myPosition()))
        {

            walking.walk(StartCanifis);
            sleep(random(600,1200));
        }
        if (StartCanifis.contains(myPosition()))
        {

            obstacleSolver("Tall tree","Climb",StartCanifis,CanifisFirstObstacle);
        }
        if (CanifisFirstObstacle.contains(myPosition()))
        {

            sleep(random(600,800));
            obstacleSolver("Gap","Jump",CanifisFirstObstacle,CanifisSecondObstacle);
        }
        if (CanifisSecondObstacle.contains(myPosition()))
        {

            sleep(random(1200,1500));
            obstacleSolver("Gap","Jump",CanifisSecondObstacle,CanifisThirdObstacle);
        }
        if (CanifisThirdObstacle.contains(myPosition()))
        {

            sleep(random(700,900));
            obstacleSolver("Gap","Jump",CanifisThirdObstacle,CanifisFourthObstacle);
        }
        if (CanifisFourthObstacle.contains(myPosition()))
        {

            obstacleSolver("Gap","Jump",CanifisFourthObstacle,CanifisFifthObstacle);
            sleep(random(1200,1800));
        }
        if (CanifisFifthObstacle.contains(myPosition()))
        {

            obstacleSolver("Pole-vault","Vault",CanifisFifthObstacle,CanifisSixthObstacle);
            sleep(random(1200,1800));
        }
        if (CanifisSixthObstacle.contains(myPosition()))
        {
            obstacleSolver("Gap","Jump",CanifisSixthObstacle,CanifisSeventhObstacle);
        }
        if (CanifisSeventhObstacle.contains(myPosition()))
        {
            obstacleSolver("Gap","Jump",CanifisSeventhObstacle,AllCanifisGround);
        }
    }


    public void Draynor() throws InterruptedException
    {
        log("In draynor method");
        if (allOfDraynorFloor.contains(myPosition()))
        {
            log("walking to draynor");
            walking.walk(startDraynor);
            sleep(random(600, 850));
        }
        if (startDraynor.contains(myPosition()))
        {
            obstacleSolver("Rough wall","Climb", startDraynor, DraynorFirstObstacle);
        }
        if (DraynorFirstObstacle.contains(myPosition()))
        {
            obstacleSolver("Tightrope","Cross", DraynorFirstObstacle, DraynorSecondObstacle);
        }
        if (DraynorSecondObstacle.contains(myPosition()))
        {
            obstacleSolver("Tightrope","Cross", DraynorSecondObstacle, DraynorThirdObstacle);
        }
        if (DraynorThirdObstacle.contains(myPosition()))
        {
            obstacleSolver("Narrow wall","Balance", DraynorThirdObstacle, DraynorFourthObstacle);
        }
        if (DraynorFourthObstacle.contains(myPosition()))
        {
            obstacleSolver("Wall","Jump-up", DraynorFourthObstacle, DraynorFifthObstacle);
        }
        if (DraynorFifthObstacle.contains(myPosition()))
        {
            obstacleSolver("Gap","Jump", DraynorFifthObstacle, DraynorSixthObstacle);
        }
        if (DraynorSixthObstacle.contains(myPosition()))
        {
            obstacleSolver("Crate","Climb-down", DraynorSixthObstacle, endOfDraynor);
        }
        if (endOfDraynor.contains(myPosition()))
        {
            walking.walk(startDraynor);
            Sleep.sleepUntil(() -> startDraynor.contains(myPosition()), 10000);
        }
        if (DraynorFall.contains(myPosition()))
        {
            walking.walk(startDraynor);
            Sleep.sleepUntil(() -> startDraynor.contains(myPosition()), 10000);
        }
    }



    public void obstacleSolver(String obstacle, String interaction, Area areaStart, Area areaEnd)
    {
        pickUpMark(areaStart);
        try
        {
            getObjects().closest(areaStart, obstacle).interact(interaction);
            objects.closest(areaStart, obstacle).interact(interaction);
        }
        catch (NullPointerException e)
        {
            log("Null exception caught again");
        }
        if (myPosition().getZ() == 0)
        {
            Sleep.sleepUntil(() -> areaEnd.contains(myPosition()), 10000);
        }
        // The above if condition is ran so that we don't check against ground positions but since we can't fail those.
        // If we don't do this the sleep will end immediately.
        else
        {
            Sleep.sleepUntil(() -> areaEnd.contains(myPosition()) ||
                    allOfDraynorFloor.contains(myPosition()) ||
                    AllVarrockGround.contains(myPosition()) ||
                    AllCanifisGround.contains(myPosition()), 10000);
            //TODO: add variable for what course ground you're on
        }
    }
    public void obstacleSolver(String obstacle, String interaction, Area areaStart, Area areaEnd, Area nearObstacle)
    {//use this version if you need to webwalk first
        pickUpMark(areaStart);
        try
        {
            walking.walk(nearObstacle);
            objects.closest(obstacle).interact(interaction);

        }
        catch (NullPointerException e)
        {
            log("Null exception caught");
        }
        Sleep.sleepUntil(() -> areaEnd.contains(myPosition()), 10000);
    }

    public void pickUpMark(Area currentArea)
    {
        try
        {
            groundItem = getGroundItems().closest(e -> e != null && e.getName().contains("Mark of grace"));

            if (groundItem != null)
            {
                itemPosition = groundItem.getPosition();
                if (currentArea.contains(itemPosition))
                {
                    groundItem.interact("Take");
                    Sleep.sleepUntil(() -> groundItems.closest("Mark of grace") == null, 10000);
                }
            }
        }
        catch (NullPointerException e)
        {
            log("Null pointer caught");
        }
    }

    public void PriestQuest() throws InterruptedException
    {
        Script PriestBot = new QuestPriestInPeril();
        PriestBot.exchangeContext(getBot());
        PriestBot.onStart();
    }
}
