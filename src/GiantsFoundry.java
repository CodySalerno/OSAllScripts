import Util.FormattingForPaint;
import Util.Sleep;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import java.awt.*;
/* I NEED TO ADD THE ABILITY TO USE EXTRA MOLDS AS OF NOW
THIS SCRIPT WILL ONLY USE THE BASE MOULDS AND IS MOSTLY ONLY
USED FOR GETTING THE CANNONBALL MOULD ASAP.
 */
@ScriptManifest(name = "Giants Foundry New", author = "Iownreality1", info = "Does Giants Foundry", version = 1.0, logo = "")
public final class GiantsFoundry extends Script{
    RS2Widget heatArrow = null;
    RS2Widget heatProgressCold = null;
    RS2Widget heatProgressWarm = null;
    RS2Widget heatProgressHot = null;
    RS2Widget progressArrow = null;
    RS2Widget progressOne = null;
    RS2Widget progressTwo = null;
    RS2Widget progressThree = null;
    RS2Widget progressFour = null;
    boolean currentlMakingSword = false;
    int currentHeat;//1 = too cold, 2 = cold, 3 = between cold and warm, 4 = warm, 5 = between warm and hot, 6 = hot 7 = too hot
    int progress; //1 = first prog bar 2 = second prog bar 3 = third prog bar 4 = fourth prog bar
    Color progOneColor;
    Color progTwoColor;
    Color progThreeColor;
    Color progFourColor;
    Color red = new Color(184, 35, 30);
    Color yellow = new Color(238, 165, 11);
    Color green = new Color(64, 133, 70);
    boolean interactingFirst = false;
    boolean interactingSecond = false;
    Area lavaPool = new Area(3370, 11495, 3372, 11498);
    Position tripHammer = new Position(3367, 11497, 0);
    Area waterFall2 = new Area(3360, 11489, 3361, 11489);
    Position polishingWheel = new Position(3365, 11485, 0);
    int greenDescending = 204;
    int greenBot = 70;
    int greenTop = 157;
    int greenAscending = 68;
    int yellowDescending = 260;
    int yellowTop = 332;
    int yellowBot = 210;
    int redAscending = 450;
    int redBot = 362;
    int arrowPosition;
    boolean Open = false;
    boolean forteSet = false;
    boolean bladeSet = false;
    boolean tipSet = false;
    int bar = 0;
    String bar1 = "Steel bar";
    String bar2 = "Iron bar";
    private long startTime;
    private String runningTime;
    private int repGained;
    private int repStart;

    Thread thread1 = new Thread(() -> {
        while (getBot().getScriptExecutor().isRunning() && !getBot().getScriptExecutor().isPaused() && !getBot().getScriptExecutor().isSuspended()) {
            try
            {
                findHeat();
            }
            catch (InterruptedException e) {
            }
            try
            {
                findProgress();
            }
            catch (InterruptedException e)
            {
            }
            runningTime = FormattingForPaint.formatTime(System.currentTimeMillis()-startTime);
            repGained = configs.get(3436)-repStart;
            try {
                Thread.sleep(300);
            }
            catch (InterruptedException e) {

            }
        }
    });

    @Override
    public void onPaint(final Graphics2D g)
    {
        Graphics2D gr = g;
        Font font = new Font("Open Sans", Font.BOLD, 18);
        g.setFont(font);
        g.setColor(Color.green);
        g.drawString("Running Time: " + runningTime,10,250);
        g.drawString("Smithing xp Gained: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXP(Skill.SMITHING)),10,270);
        g.drawString("Smithing xp/hr: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXPPerHour(Skill.SMITHING)),10,290);
        g.drawString("Reputation Gained: " + FormattingForPaint.formatValue(repGained),10,310);
        g.drawString("Current reputation: " + (repGained+repStart),10,330 );
    }


    @Override
    public final void onStart() throws InterruptedException {
        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.SMITHING);
        repStart = configs.get(3436);
        thread1.start();
    }

    @Override
    public final int onLoop() throws InterruptedException {
        if (configs.get(3436) > 2000)//stops at 2k foundry rep
        {
            walking.walk(getNpcs().closest("Kovac"));
            sleep(random(800,1200));
            npcs.closest("Kovac").interact("Shop");
            Sleep.sleepUntil(() -> widgets.getWidgetContainingText("Giants' Foundry Reward Shop") != null, 10000);
            widgets.getWidgetContainingText("Double ammo mould").interact("Select");
            sleep(random(1400,1800));
            widgets.getWidgetContainingText("Buy").interact("Buy");
            sleep(random(1400,1800));
            stop();
        }
        setWidgets();
        findProgress();
        findHeat();
        if (currentlMakingSword) {
            log("WE ARE MAKING A SWORD");
            log("CURRENT PROGRESS IS: " + progress);
            log("CURRENT HEAT IS: " + currentHeat);
            log("CURRENT COLOR IS: " + progFourColor);
            if (progress == 1)
            {
                if (progOneColor.equals(red)) //seems to be working
                {
                    smithRed(progress);

                } else if (progOneColor.equals(yellow)) {
                    smithYellow(progress);
                } else if (progOneColor.equals(green)) {
                    smithGreen(progress);
                }
            }
            else if (progress == 2)
            {
                if (progTwoColor.equals(red)) {
                    smithRed(progress);
                } else if (progTwoColor.equals(yellow))//should be working
                {
                    smithYellow(progress);
                } else if (progTwoColor.equals(green)) {
                    smithGreen(progress);
                }
            }
            else if (progress == 3)
            {
                if (progThreeColor.equals(red)) //should be good
                {
                    smithRed(progress);
                } else if (progThreeColor.equals(yellow)) {
                    smithYellow(progress);
                } else if (progThreeColor.equals(green)) {
                    smithGreen(progress);
                }
            }
            else if (progress == 4)
            {

                if (progFourColor.equals(red)) {
                    smithRed(progress);
                } else if (progFourColor.equals(yellow)) {
                    smithYellow(progress);
                } else if (progFourColor.equals(green)) //should be working
                {
                    smithGreen(progress);
                }
            }
            else if (progress == 5)
            {
                log("Turn in sword");
                if(!polishingWheel.equals(myPosition()))
                {
                    walking.walk(npcs.closest("Kovac"));
                    sleep(random(700,900));
                }
                npcs.closest("Kovac").interact("Hand-in");
                while(!dialogues.isPendingContinuation())
                {

                }
                dialogues.completeDialogueU("Yes");
                sleep(random(700,900));

            }
        }
        else
        {
            while(configs.get(3430) == 0 && !Open)
            {
                objects.closest("Mould jig (Empty)").interact("Setup");
                sleep(random(1300,1900));
                Sleep.sleepUntil(() -> getWidgets().getWidgetContainingText("Giants' Foundry Mould Setup") != null, 10000);
                Open = true;
            }
            String reqOneString = "";
            String reqTwoString = "";
            while (Open == true)
            {
                if (reqOneString.equals(""))
                {
                    RS2Widget reqOne = getWidgets().get(718, 27);
                    reqOneString = reqOne.getMessage();
                    RS2Widget reqTwo = getWidgets().get(718, 29);
                    reqTwoString = reqTwo.getMessage();
                }
                if ((reqOneString.equals("Broad") && reqTwoString.equals("Heavy")) || (reqTwoString.equals("Broad") && reqOneString.equals("Heavy")))
                {
                    if (!forteSet)
                    {
                        widgets.get(718,12,1).interact("View");
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Medusa Ricasso").interact("Select");
                        log("Selecting Medusa Ricasso");
                        sleep(random(800,1200));
                        forteSet = true;
                    }
                    if (!bladeSet)
                    {
                        widgets.get(718,12,10).interact("View");
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Medusa Blade").interact("Select");
                        log("Selecting Medusa Blade");
                        sleep(random(800,1200));
                        bladeSet = true;
                    }
                    if (!tipSet)
                    {
                        widgets.get(718,12,19).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Saw Tip").interact("Select");
                        log("Selecting Saw Tip");
                        sleep(random(800,1200));
                        tipSet = true;
                    }
                }
                else if ((reqOneString.equals("Broad") && reqTwoString.equals("Light")) || (reqTwoString.equals("Broad") && reqOneString.equals("Light")))
                {
                    if (!forteSet)
                    {
                        widgets.get(718,12,1).interact("View");
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Chopper Forte").interact("Select");
                        log("Selecting Chopper Forte");
                        sleep(random(800,1200));
                        forteSet = true;
                    }
                    if (!bladeSet)
                    {
                        widgets.get(718,12,10).interact("View");
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Saw Blade").interact("Select");
                        log("Selecting Saw Blade");
                        sleep(random(800,1200));
                        bladeSet = true;
                    }
                    if (!tipSet)
                    {
                        widgets.get(718,12,19).interact("View");
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Chopper Tip").interact("Select");
                        log("Selecting Chopper Tip");
                        sleep(random(800,1200));
                        tipSet = true;
                    }
                }
                else if ((reqOneString.equals("Broad") && reqTwoString.equals("Spiked")) || (reqTwoString.equals("Broad") && reqOneString.equals("Spiked")))
                {
                    if (!forteSet)
                    {
                        widgets.get(718,12,1).interact("View");
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Medusa Ricasso").interact("Select");
                        log("Selecting Medusa Ricasso");
                        sleep(random(800,1200));
                        forteSet = true;
                    }
                    if (!bladeSet)
                    {
                        widgets.get(718,12,10).interact("View");
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Medusa Blade").interact("Select");
                        log("Selecting Medusa Blade");
                        sleep(random(800,1200));
                        bladeSet = true;
                    }
                    if (!tipSet)
                    {
                        widgets.get(718,12,19).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Saw Tip").interact("Select");
                        log("Selecting Saw Tip");
                        sleep(random(800,1200));
                        tipSet = true;
                    }
                }
                else if ((reqOneString.equals("Broad") && reqTwoString.equals("Flat")) || (reqTwoString.equals("Broad") && reqOneString.equals("Flat")))
                {
                    if (!forteSet)
                    {
                        widgets.get(718,12,1).interact("View");
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Medusa Ricasso").interact("Select");
                        log("Selecting Medusa Ricasso");
                        sleep(random(800,1200));
                        forteSet = true;
                    }
                    if (!bladeSet)
                    {
                        widgets.get(718,12,10).interact("View");
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Medusa Blade").interact("Select");
                        log("Selecting Medusa Blade");
                        sleep(random(800,1200));
                        bladeSet = true;
                    }
                    if (!tipSet)
                    {
                        widgets.get(718,12,19).interact("View");
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Serpent's Fang").interact("Select");
                        log("Selecting Serpent's Fang");
                        sleep(random(800,1200));
                        tipSet = true;
                    }
                }
                else if ((reqOneString.equals("Narrow") && reqTwoString.equals("Heavy")) || (reqTwoString.equals("Narrow") && reqOneString.equals("Heavy")))
                {
                    if (!forteSet)
                    {
                        widgets.get(718,12,1).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Serrated Forte").interact("Select");
                        log("Selecting Serrated Forte");
                        sleep(random(800,1200));
                        forteSet = true;
                    }
                    if (!bladeSet)
                    {
                        widgets.get(718,12,10).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Gladius Edge").interact("Select");
                        log("Selecting Gladius Edge");
                        sleep(random(800,1200));
                        bladeSet = true;
                    }
                    if (!tipSet)
                    {
                        widgets.get(718,12,19).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Gladius Point").interact("Select");
                        log("Selecting Gladius Point");
                        sleep(random(800,1200));
                        tipSet = true;
                    }
                }
                else if ((reqOneString.equals("Narrow") && reqTwoString.equals("Light")) || (reqTwoString.equals("Narrow") && reqOneString.equals("Light")))
                {
                    if (!forteSet)
                    {
                        widgets.get(718,12,1).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Serpent Ricasso").interact("Select");
                        log("Selecting Serpent Ricasso");
                        sleep(random(800,1200));
                        forteSet = true;
                    }
                    if (!bladeSet)
                    {
                        widgets.get(718,12,10).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Stiletto Blade").interact("Select");
                        log("Selecting Stiletto Blade");
                        sleep(random(800,1200));
                        bladeSet = true;
                    }
                    if (!tipSet)
                    {
                        widgets.get(718,12,19).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Serpent's Fang").interact("Select");
                        log("Selecting Serpent's Fang");
                        sleep(random(800,1200));
                        tipSet = true;
                    }
                }
                else if ((reqOneString.equals("Narrow") && reqTwoString.equals("Spiked")) || (reqTwoString.equals("Narrow") && reqOneString.equals("Spiked")))
                {
                    if (!forteSet)
                    {
                        widgets.get(718,12,1).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Serrated Forte").interact("Select");
                        log("Selecting Serrated Forte");
                        sleep(random(800,1200));
                        forteSet = true;
                    }
                    if (!bladeSet)
                    {
                        widgets.get(718,12,10).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Stiletto Blade").interact("Select");
                        log("Selecting Stiletto Blade");
                        sleep(random(800,1200));
                        bladeSet = true;
                    }
                    if (!tipSet)
                    {
                        widgets.get(718,12,19).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Serpent's Fang").interact("Select");
                        log("Selecting Serpent's Fang");
                        sleep(random(800,1200));
                        tipSet = true;
                    }
                }
                else if ((reqOneString.equals("Narrow") && reqTwoString.equals("Flat")) || (reqTwoString.equals("Narrow") && reqOneString.equals("Flat")))
                {
                    if (!forteSet)
                    {
                        widgets.get(718,12,1).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Serpent Ricasso").interact("Select");
                        log("Selecting Serpent Ricasso");
                        sleep(random(800,1200));
                        forteSet = true;
                    }
                    if (!bladeSet)
                    {
                        widgets.get(718,12,10).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Stiletto Blade").interact("Select");
                        log("Selecting Stiletto Blade");
                        sleep(random(800,1200));
                        bladeSet = true;
                    }
                    if (!tipSet)
                    {
                        widgets.get(718,12,19).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Gladius Point").interact("Select");
                        log("Selecting Gladius Point");
                        sleep(random(800,1200));
                        tipSet = true;
                    }
                }
                else if ((reqOneString.equals("Heavy") && reqTwoString.equals("Spiked")) || (reqTwoString.equals("Heavy") && reqOneString.equals("Spiked")))
                {
                    if (!forteSet)
                    {
                        widgets.get(718,12,1).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Serrated Forte").interact("Select");
                        log("Selecting Serrated Forte");
                        sleep(random(800,1200));
                        forteSet = true;
                    }
                    if (!bladeSet)
                    {
                        widgets.get(718,12,10).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Medusa Blade").interact("Select");
                        log("Selecting Medusa Blade");
                        sleep(random(800,1200));
                        bladeSet = true;
                    }
                    if (!tipSet)
                    {
                        widgets.get(718,12,19).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Saw Tip").interact("Select");
                        log("Selecting Saw Tip");
                        sleep(random(800,1200));
                        tipSet = true;
                    }
                }
                else if ((reqOneString.equals("Heavy") && reqTwoString.equals("Flat")) || (reqTwoString.equals("Heavy") && reqOneString.equals("Flat")))
                {
                    if (!forteSet)
                    {
                        widgets.get(718,12,1).interact("View");
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Medusa Ricasso").interact("Select");
                        log("Selecting Medusa Ricasso");
                        sleep(random(800,1200));
                        forteSet = true;
                    }
                    if (!bladeSet)
                    {
                        widgets.get(718,12,10).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Gladius Edge").interact("Select");
                        log("Selecting Gladius Edge");
                        sleep(random(800,1200));
                        bladeSet = true;
                    }
                    if (!tipSet)
                    {
                        widgets.get(718,12,19).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Gladius Point").interact("Select");
                        log("Selecting Gladius Point");
                        sleep(random(800,1200));
                        tipSet = true;
                    }
                }
                else if ((reqOneString.equals("Light") && reqTwoString.equals("Spiked")) || (reqTwoString.equals("Light") && reqOneString.equals("Spiked")))
                {
                    if (!forteSet)
                    {
                        widgets.get(718,12,1).interact("View");
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Disarming Forte").interact("Select");
                        log("Selecting Disarming Forte");
                        sleep(random(800,1200));
                        forteSet = true;
                    }
                    if (!bladeSet)
                    {
                        widgets.get(718,12,10).interact("View");
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Saw Blade").interact("Select");
                        log("Selecting Saw Blade");
                        sleep(random(800,1200));
                        bladeSet = true;
                    }
                    if (!tipSet)
                    {
                        widgets.get(718,12,19).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Serpent's Fang").interact("Select");
                        log("Selecting Serpent's Fang");
                        sleep(random(800,1200));
                        tipSet = true;
                    }
                }
                else if ((reqOneString.equals("Light") && reqTwoString.equals("Flat")) || (reqTwoString.equals("Light") && reqOneString.equals("Flat")))
                {
                    if (!forteSet)
                    {
                        widgets.get(718,12,1).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Serpent Ricasso").interact("Select");
                        log("Selecting Serpent Ricasso");
                        sleep(random(800,1200));
                        forteSet = true;
                    }
                    if (!bladeSet)
                    {
                        widgets.get(718,12,10).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Stiletto Blade").interact("Select");
                        log("Selecting Stiletto Blade");
                        sleep(random(800,1200));
                        bladeSet = true;
                    }
                    if (!tipSet)
                    {
                        widgets.get(718,12,19).interact("View");
                        log("scroll to bottom");
                        sleep(random(800,1200));
                        mouse.click(random(467,477),random(256,286),false);
                        sleep(random(800,1200));
                        widgets.getWidgetContainingText("Serpent's Fang").interact("Select");
                        log("Selecting Serpent's Fang");
                        sleep(random(800,1200));
                        tipSet = true;
                    }
                }
                if (tipSet && bladeSet && forteSet)
                {
                    widgets.get(718,6,0).interact("Set");
                    Open = false;
                }
            }
            tipSet = false;
            bladeSet = false;
            forteSet = false;
            Open = false;
            reqOneString = "";
            reqTwoString = "";
            while (!inventory.contains("Bucket of water"))
            {
                log("Walk to bank");
                walking.walk(objects.closest("Bank chest"));
                sleep(random(400,800));
                if (!bank.isOpen())
                {
                    log("Using Bank");
                    objects.closest("Bank chest").interact("Use");
                    sleep(random(800,1200));
                }
                if (bank.isOpen())
                {
                    log("Deposit all");
                    widgets.get(12,42).interact("Deposit inventory");
                    sleep(random(800,1200));
                    log("Withdraw 10 buckets of water");
                    bank.withdraw("Bucket of Water",10);
                    sleep(random(800,1200));
                }
            }
            while (bar < 2 && configs.get(3430) != 2)
            {
                if (!(new Area(3374,11493,3374,11494).contains(myPosition())))
                {
                    walking.walk(objects.closest("Bank chest"));
                    sleep(random(800,1200));
                }
                if (!bank.isOpen())
                {
                    log("Using Bank");
                    objects.closest("Bank chest").interact("Use");
                    sleep(random(800,1200));
                }
                if (bank.isOpen() && configs.get(2673) != 2)
                {
                    log("get 14 steel bars");
                    bank.withdraw(bar1, 14);
                    sleep(random(800,1200));
                    bank.close();
                    sleep(random(800,1200));
                }
                if (bank.isOpen() && configs.get(2673) == 2)
                {
                    log("get 14 iron bars");
                    bank.withdraw(bar2, 14);
                    sleep(random(800,1200));
                    bank.close();
                    sleep(random(800,1200));
                    log("finished banking iron bars bar = " + bar);
                }
                if (inventory.contains(bar1))
                {
                    log("use steel bars on crucible");
                    objects.closest("Crucible (empty)").interact("Fill");
                    Sleep.sleepUntil(() -> getWidgets().getWidgetContainingText("What metal would you like to add?") != null, 10000);
                    widgets.get(270,16,38).interact("Smelt");
                    sleep(random(5000,7000));
                    bar++;
                }
                if (inventory.contains(bar2))
                {
                    log("use iron bars on crucible");
                    objects.closest("Crucible (partially full)").interact("Fill");
                    Sleep.sleepUntil(() -> getWidgets().getWidgetContainingText("How metal would you like to add?") != null, 10000);
                    widgets.get(270,15,38).interact("Smelt");
                    sleep(random(5000,7000));
                    bar++;
                }
            }
            if (configs.get(2673) == 1 && bar == 2)
            {
                objects.closest("Crucible (full)").interact("Pour");
                sleep(random(7000,9000));
            }
            while (configs.get(3430) == 2)
            {
                objects.closest("Mould jig (Poured metal)").interact("Pick-up");
                sleep(random(2000,3000));
                bar=0;
            }
        }

        return random(600, 1200);
    }

    @Override
    public final void onExit() {
        thread1.stop();
    }

/*
    public final String formatValue(final long l) {
        return (l > 1_000_000) ? String.format("%.2fm", ((double) l / 1_000_000))
                : (l > 1000) ? String.format("%.1fk", ((double) l / 1000))
                : l + "";
    }
    public final String formatValue(final int l) {
        return (l > 1_000_000) ? String.format("%.2fm", ((double) l / 1_000_000))
                : (l > 1000) ? String.format("%.1fk", ((double) l / 1000))
                : l + "";
    }
    public final String formatTime(final long ms){
        long s = ms / 1000, m = s / 60, h = m / 60;
        s %= 60; m %= 60; h %= 24;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

 */

    public void setWidgets() {
        try {

            heatProgressCold = getWidgets().get(754, 19);
            heatProgressWarm = getWidgets().get(754, 20);
            heatProgressHot = getWidgets().get(754, 21);
            heatArrow = getWidgets().get(754, 74);
            progressArrow = getWidgets().get(754, 78);
            progressOne = getWidgets().get(754, 75, 0);
            progressTwo = getWidgets().get(754, 75, 11);
            progressThree = getWidgets().get(754, 75, 22);
            progressFour = getWidgets().get(754, 75, 33);

            //log("Finished Widgets");

            if (heatProgressWarm.isVisible()) {
                log("making it true");
                currentlMakingSword = true;
            } else
            {
                log("making it false");
                currentlMakingSword = false;
            }

        } catch (Exception e) {
            log("hit exception");
            currentlMakingSword = false;
        }
    }

    public void findProgress() throws InterruptedException {
        try
        {
            Rectangle rectProgress = new Rectangle();
            Rectangle rectOne = new Rectangle();
            Rectangle rectTwo = new Rectangle();
            Rectangle rectThree = new Rectangle();
            Rectangle rectFour = new Rectangle();
            rectProgress = progressArrow.getRectangleIgnoreIsHidden(false);
            rectOne = progressOne.getRectangleIgnoreIsHidden(false);
            rectTwo = progressTwo.getRectangleIgnoreIsHidden(false);
            rectThree = progressThree.getRectangleIgnoreIsHidden(false);
            rectFour = progressFour.getRectangleIgnoreIsHidden(false);
            progOneColor = colorPicker.colorAt((rectOne.x + 2), (rectOne.y + 2));
            progTwoColor = colorPicker.colorAt((rectTwo.x + 2), (rectTwo.y + 2));
            progThreeColor = colorPicker.colorAt((rectThree.x + 2), (rectThree.y + 2));
            progFourColor = colorPicker.colorAt((rectFour.x + 2), (rectFour.y + 2));

            if (rectProgress.x + (rectProgress.width / 2) >= rectOne.x && rectProgress.x + (rectProgress.width / 2) < rectOne.x + rectOne.width) {
                //log("Progress is within first box");
                progress = 1;
            } else if (rectProgress.x + (rectProgress.width / 2) >= rectTwo.x && rectProgress.x + (rectProgress.width / 2) < rectTwo.x + rectTwo.width) {
                //log("Progress is within second box");
                progress = 2;
            } else if (rectProgress.x + (rectProgress.width / 2) >= rectThree.x && rectProgress.x + (rectProgress.width / 2) < rectThree.x + rectThree.width) {
                //log("Progress is within third box");
                progress = 3;
            } else if (rectProgress.x + (rectProgress.width / 2) >= rectFour.x && rectProgress.x + (rectProgress.width / 2) < rectFour.x + rectFour.width) {
                //log("Progress is within fourth box");
                progress = 4;
            } else if (rectProgress.x + (rectProgress.width / 2) >= rectFour.x + rectFour.width) {
                log("Done with Weapon");
                progress = 5;
            } else {
                log("No Sword progress need to make sword");
                progress = 0;
            }
        }
        catch(Exception e)
        {

        }

    } //do not touch this code this is good

    public void findHeat() throws InterruptedException {
        //log("find progress: " + progress);
        try
        {
            Rectangle rectHeat = new Rectangle();
            Rectangle rectOne = new Rectangle();
            Rectangle rectTwo = new Rectangle();
            Rectangle rectThree = new Rectangle();
            rectHeat = heatArrow.getRectangleIgnoreIsHidden(false);
            rectOne = heatProgressCold.getRectangleIgnoreIsHidden(false);
            rectTwo = heatProgressWarm.getRectangleIgnoreIsHidden(false);
            rectThree = heatProgressHot.getRectangleIgnoreIsHidden(false);

            arrowPosition = rectHeat.x + (rectHeat.width / 2);

            if (rectHeat.x + (rectHeat.width / 2) > 65 && rectHeat.x + (rectHeat.width / 2) <= rectOne.x + rectOne.width) {
                //log("Progress is within COLD box");
                currentHeat = 2;
            } else if (rectHeat.x + (rectHeat.width / 2) > rectTwo.x && rectHeat.x + (rectHeat.width / 2) < (rectTwo.x + rectTwo.width)) {
                //log("Progress is within WARM box");
                currentHeat = 4;
            } else if (rectHeat.x + (rectHeat.width / 2) > rectThree.x && rectHeat.x + (rectHeat.width / 2) <= rectThree.x + rectThree.width) {
                //log("Progress is within HOT box");
                currentHeat = 6;
            } else if (rectHeat.x + (rectHeat.width / 2) > rectOne.x + rectOne.width && rectHeat.x + (rectHeat.width / 2) <= rectTwo.x) {
                //log("inbetween COLD and WARM");
                currentHeat = 3;
            } else if (rectHeat.x + (rectHeat.width / 2) > rectTwo.x + rectTwo.width && rectHeat.x + (rectHeat.width / 2) <= rectThree.x) {
                //log("inbetween WARM and HOT");
                currentHeat = 5;
            } else if (rectHeat.x + (rectHeat.width / 2) <= rectOne.x + 1) {
                //log("BELOW COLD");
                currentHeat = 1;
            } else if (rectHeat.x + (rectHeat.width / 2) >= rectThree.x) {
                //log("TOO HOT");
                currentHeat = 7;
            } else {
                log("SOMEHOW IS NO WHERE ON HEAT METER");
            }
        }
        catch (Exception e)
        {

        }
    }

    public void smithGreen(int currentProgress) throws InterruptedException {
        while (arrowPosition > greenDescending) // if heat is too high lower it
        {
            while (!waterFall2.contains(myPosition())) {
                log("Walking to waterfall");
                walking.walk(objects.closest("Waterfall"));
                sleep(random(400, 600));
            }
            if (interactingFirst == false) {
                log("Interacting with waterfall");
                objects.closest("Waterfall").interact("Cool-preform");
                interactingFirst = true;
            }
            sleep(random(100, 250));
        }
        log("End of arrowPosition > YellowDescending loop");
        interactingFirst = false;
        if (greenAscending > arrowPosition) {
            while (greenTop > arrowPosition) //heat back up until almost full of green
            {
                while (!lavaPool.contains(myPosition())) {
                    log("Walking to lava pool");
                    walking.walk(objects.closest("Lava pool"));
                    sleep(random(400, 600));
                }
                if (interactingFirst == false) {
                    log("Interacting with lava pool");
                    objects.closest("Lava pool").interact("Heat-preform");
                    interactingFirst = true;
                }
                sleep(random(100, 250));
            }
        }
        interactingFirst = false;
        while (greenBot < arrowPosition && progress == currentProgress) {
            if (!polishingWheel.equals(myPosition())) {
                log("Walking to Polishing wheel");
                walking.walk(objects.closest("Polishing wheel"));
            }
            //log("No longer waiting");
            if (interactingSecond == false) {
                if (!interactingSecond) {
                    log("interact with Polishing wheel");
                    objects.closest("Polishing wheel").interact("Use");
                    interactingSecond = true;
                }
            }
            sleep(random(100, 250));
        }
        walking.walk(lavaPool);
        interactingSecond = false;
    }

    public void smithRed(int currentProgress) throws InterruptedException {
        while (redAscending > arrowPosition) // if heat is low heat it up
        {
            while (!lavaPool.contains(myPosition())) {
                log("Walking to lavapool");
                walking.walk(objects.closest("Lava pool"));
                sleep(random(400, 600));
            }
            if (interactingFirst == false) {
                log("Interacting with lava pool");
                sleep(600);
                objects.closest("Lava pool").interact("Heat-preform");
                interactingFirst = true;
            }
            sleep(random(100, 250));
        }
        log("End of redAscending > arrowPosition loop");
        interactingFirst = false;
        if (!tripHammer.equals(myPosition())) {
            log("Walking to Trip hammer");
            walking.walk(objects.closest("Trip hammer"));
        }
        while (currentHeat == 7) {
            log("Waiting for heat to lower");
            sleep(random(100, 250));
        }
        while (arrowPosition > redBot && progress == currentProgress) {
            //log("No longer waiting");
            if (interactingSecond == false)
            {
                if (!interactingSecond)
                {
                    log("interact with hammer");
                    objects.closest("Trip hammer").interact("Use");
                    interactingSecond = true;
                }
            }
            sleep(random(100, 250));
        }
        interactingSecond = false;

    }

    public void smithYellow(int currentProgress) throws InterruptedException {
        while (arrowPosition > yellowDescending) //heat is too high so we lower it
        {
            while (!waterFall2.contains(myPosition())) {
                log("Walking to water fall");
                walking.walk(objects.closest("Waterfall"));
                sleep(random(400, 600));
            }
            if (interactingFirst == false) {
                log("Interacting with Waterfall");
                sleep(600);
                objects.closest("Waterfall").interact("Cool-preform");
                interactingFirst = true;
            }
            sleep(random(100, 250));
        }

        while(arrowPosition < yellowBot)
        {
            while (!lavaPool.contains(myPosition())) {
                log("Walking to lava pool");
                walking.walk(objects.closest("Lava pool"));
                sleep(random(400, 600));
            }
            if (interactingFirst == false) {
                log("Interacting with lava pool");
                sleep(600);
                objects.closest("Lava pool").interact("Heat-preform");
                interactingFirst = true;
            }
            sleep(random(100, 250));
        }
        log("Leaving arrowPosition < yellowBot");
        interactingFirst = false;
        log("Leaving arrowPosition > yellowDescending loop");
        if (!tripHammer.equals(myPosition()))
        {
            log("Walking to Grindstone");
            walking.walk(objects.closest("Grindstone"));
        }
        while (arrowPosition < yellowTop && progress == currentProgress) {
            if (interactingSecond == false)
            {
                if (!interactingSecond)
                {
                    log("interact with grindstone");
                    objects.closest("Grindstone").interact("Use");
                    interactingSecond = true;
                }
            }
            sleep(random(100, 250));
        }
        interactingSecond = false;
        walking.walk(waterFall2);

    }

}
