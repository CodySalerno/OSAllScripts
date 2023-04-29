import org.osbot.Be;
import org.osbot.P;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import util.BetterWalk;
import util.FormattingForPaint;
import util.Sleep;

import java.awt.*;

@ScriptManifest(name = "Improved Blast Furnace1.01", author = "Iownreality1", info = "Creates bar", version = 0.1, logo = "")
public class ImprovedBlastFurnace extends Script {

    Position bankPosition = new Position(1948,4957,0);
    Position conveyorPosition = new Position(1942,4967,0);
    Position barPosition = new Position(1941,4962,0);
    Position foreman = new Position(1943,4959,0);
    Position bottomConveyor = new Position(1937,4967,0);
    Area nearStairs = new Area(2931,10196,2932,10197);
    Area nearConveyor = new Area(1936,4969, 1944,4965);
    BetterWalk betterWalk = new BetterWalk(this);
    Area keldagrim = new Area(2924,10181,2938,10202);
    Area blastFurnace = new Area(1935,4956,1957,4974);
    long lastPaid;
    long startTime;
    String s;
    boolean shouldPayForeman = false;
    boolean setOreOnBelt = false;
    boolean needBars;
    String runningTime;
    Font font = new Font("Open Sans", Font.BOLD, 18);
    RS2Widget staminaChecker = null;
    Rectangle stamRect;
    Color stammed = new Color(213,88,28);
    Color notStammed = new Color(217,180,59);
    boolean coalBagEmptied;


    @Override
    public void onPaint(final Graphics2D g)
    {
        runningTime = FormattingForPaint.formatTime(System.currentTimeMillis()-startTime);
        g.setFont(font);
        g.setColor(Color.green);
        g.drawString("Running Time: " + runningTime,10,250);
        g.drawString("Smithing xp Gained: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXP(Skill.SMITHING)),10,270);
        g.drawString("Smithing xp/hr: " + FormattingForPaint.formatValue(getExperienceTracker().getGainedXPPerHour(Skill.SMITHING)),10,290);
        g.drawString("Current Smithing level: " + skills.getStatic(Skill.SMITHING), 10,310);
        g.drawString("Bars Created  " + (int)(getExperienceTracker().getGainedXP(Skill.SMITHING)/17.5), 10,330);
        g.drawString("Bars per hour  " + (int)(getExperienceTracker().getGainedXPPerHour(Skill.SMITHING)/17.5), 10,350);
    }

    public void onStart()
    {
        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.SMITHING);
        if (configs.get(545) != 0)
        {
            needBars = true;
        }
        else
        {
            needBars = false;
        }
    }

    @Override
    public int onLoop() throws InterruptedException
    {
        getBackToFurnace();
        if (configs.get(547) == 0)
        {
            useCoal();
        }
        if (mustPayForeman())
        {
            log("Must pay foreman");
            if (!inventory.contains("Coins"))
            {
                betterWalk.MyWalkingEvent(bankPosition);
                objects.closest("Bank chest").interact("Use");
                Sleep.sleepUntil(() -> (bank.isOpen()), 10000);
                bank.depositAllExcept("Coal bag");
                bank.withdraw("Coins", 2500);
                Sleep.sleepUntil(() -> (inventory.contains("Coins")), 10000);
            }
            if (inventory.contains("Coins"))
            {
                betterWalk.MyWalkingEvent(foreman);
                if (foreman.equals(myPosition()))
                {
                    npcs.closest("Blast Furnace Foreman").interact("Pay");
                    Sleep.sleepUntil(() -> (widgets.getWidgetContainingText("Pay 2,500 coins to use the Blast Furnace?") != null), 10000);
                    widgets.get(219,1,1).interact();
                    Sleep.sleepUntil(() -> widgets.get(219,1,1) == null, 2000);
                    shouldPayForeman = false;
                }
            }
            return random(400,700);
        }
        if (inventory.contains("Coal"))
        {
            log("inventory contains coal going to place in");
            if (!nearConveyor.equals(myPosition()))
            {
                betterWalk.MyWalkingEvent(bottomConveyor);
            }
            objects.closest(9100).hover();
            mouse.click(true);
            if (menu.isOpen())
            {
                menu.selectAction("Put-ore-on");
                Sleep.sleepUntil(() -> !inventory.contains("Coal"), 3000);
            }
            return (random(400,700));
        }
        if (blastFurnace.contains(myPosition()))
        {
            if (objects.closest("Bar dispenser").hasAction("Take")) //bars are ready need to take
            {
                if (inventory.onlyContains("Coal bag"))
                {
                    log("going to get bars");
                    objects.closest("Bar dispenser").interact("Take");
                    Sleep.sleepUntil(() -> widgets.getWidgetContainingText("How many would") != null, 7000);
                    if (widgets.getWidgetContainingText("How many would") != null)
                    {
                        widgets.get(270,14,38).interact();
                        Sleep.sleepUntil(() -> inventory.contains("Steel bar"), 5000);
                        if (dialogues.isPendingContinuation())
                        {
                            dialogues.completeDialogue();
                        }
                        return (random(400,700));
                    }
                }
                else
                {
                    log("going to bank to clear inventory for bars");
                    if (!bank.isOpen())
                    {
                        objects.closest("Bank chest").interact();
                        Sleep.sleepUntil(() -> bank.isOpen(), 7000);
                    }
                    if (bank.isOpen())
                    {
                        bank.depositAllExcept("Coal bag");
                        Sleep.sleepUntil(() -> inventory.onlyContains("Coal bag"), 3000);
                        return (random(400,700));
                    }
                }
            }
            else //everything else happens here.
            {
                if (inventory.contains("Steel bar"))
                {
                    log("Banking bars");
                    if (!bank.isOpen())
                    {
                        objects.closest("Bank chest").interact();
                        Sleep.sleepUntil(() -> bank.isOpen(), 7000);
                    }
                    if (bank.isOpen() && inventory.contains("Steel bar"))
                    {
                        bank.depositAll("Steel bar");
                        Sleep.sleepUntil(() -> !inventory.contains("Steel bar"), 3000);
                        staminaCheck();
                    }
                }
                if (!bank.isOpen() && inventory.onlyContains("Coal bag"))
                {
                    log("opening bank");
                    objects.closest("Bank chest").interact();
                    Sleep.sleepUntil(() -> bank.isOpen(), 7000);
                    staminaCheck();
                }
                if (bank.isOpen() && inventory.onlyContains("Coal bag"))
                {
                    log("getting ore");
                    staminaCheck();
                    if (inventory.getItem("Coal bag").hasAction("Fill"))
                    {
                        inventory.getItem("Coal bag").interact("Fill");
                    }
                    bank.withdrawAll("Iron ore");
                    betterWalk.MyWalkingEvent(bottomConveyor);
                }
                if (inventory.contains("Iron ore") &&  (!nearConveyor.contains(myPosition())))
                {
                    log("walking with ore to conveyor");
                    betterWalk.MyWalkingEvent(bottomConveyor);
                    //betterWalk.MyWalkingEvent(conveyorPosition);
                }
                if (nearConveyor.contains(myPosition()) && inventory.contains("Iron ore") )
                {
                    log("depositing ore to conveyor");
                    //objects.closest(9100).interact("Put-ore-on"); //conveyor belt
                    objects.closest(9100).hover();
                    log("hovering");
                    mouse.click(true);
                    log("right clicked");
                    if (menu.isOpen())
                    {
                        if (menu.selectAction("Put-ore-on"))
                        {
                            coalBagEmptied = false;
                        }
                        else inventory.hover(1);
                    }
                    Sleep.sleepUntil(() -> !inventory.contains("Iron ore"), 5000);
                }
                if (!inventory.contains("Iron ore") && !coalBagEmptied && conveyorPosition.equals(myPosition()))
                {
                    log("Emptying coal bag");
                    inventory.getItem("Coal bag").interact("Empty");
                    //objects.closest(9100).interact("Put-ore-on");
                    objects.closest(9100).hover();
                    mouse.click(true);
                    if (menu.isOpen())
                    {
                        if (menu.selectAction("Put-ore-on"))
                        {
                            if (mustPayForeman())
                            {
                                return (random(400,700));
                            }
                            log("selecting put ore on");
                            coalBagEmptied = true;
                        }
                        else
                        {
                            inventory.hover(1);
                        }
                        if (!coalBagEmptied)
                        {
                            return (random(400,700));
                        }
                    }

                    Sleep.sleepUntil(() -> !inventory.contains("Coal"), 3000);
                }
                else
                {
                    log("Sending through loop again");
                    return (random(400,700));
                }
                if (nearConveyor.contains(myPosition()) && !inventory.contains("Iron ore") && coalBagEmptied)
                {
                    log("no iron ore going to bar position");
                    betterWalk.MyWalkingEvent(barPosition);
                    Sleep.sleepUntil(() -> configs.get(545) != 0, 5000);
                    if (configs.get(545) != 0)
                    {
                        objects.closest("Bar dispenser").interact("Take");
                        Sleep.sleepUntil(() -> widgets.getWidgetContainingText("How many would") != null, 7000);
                        if (widgets.getWidgetContainingText("How many would") != null)
                        {
                            widgets.get(270,14,38).interact();
                            Sleep.sleepUntil(() -> inventory.contains("Steel bar"), 5000);
                            if (dialogues.isPendingContinuation())
                            {
                                dialogues.completeDialogue();
                            }
                        }
                    }
                }
                if ((!bank.isOpen() && !inventory.isEmptyExcept("Iron ore", "Steel bar", "Coal", "Coal bag") ))
                {
                    log("weird item in inventory banking");
                    betterWalk.MyWalkingEvent(bankPosition);
                    objects.closest("Bank chest").interact();
                    Sleep.sleepUntil(() -> bank.isOpen(), 3000);
                    if (bank.isOpen())
                    {
                        bank.depositAllExcept("Coal bag");
                    }
                }
            }
        }

        if (configs.get(545) != 0)
        {
            needBars = true;
        }
        else needBars = false;
        return  random(400, 600);
    }


    private void useCoal()
    {
        if (blastFurnace.contains(myPosition()))
        {
            if (bank.isOpen())
            {
                bank.depositAllExcept("Coal bag");
                if (inventory.onlyContains("Coal bag"))
                {

                }
            }
        }
    }
    private void staminaCheck()
    {
        if (bank.isOpen())
        {

            if (configs.get(1575) == 0 && settings.getRunEnergy() < 80) //matches current color to non stammed color.
            {
                if (bank.contains("Stamina potion(1)"))
                {
                    bank.withdraw("Stamina potion(1)", 1);
                    Sleep.sleepUntil(() -> inventory.contains("Stamina potion(1)"),3000);
                    if (inventory.contains("Stamina potion(1)"))
                    {
                        inventory.getItem("Stamina potion(1)").interact("Drink");
                        Sleep.sleepUntil(() -> inventory.contains("Vial"), 3000);
                        if (inventory.contains("Vial"))
                        {
                            bank.depositAll("Vial");
                            Sleep.sleepUntil(() -> !inventory.contains("Vial"), 3000);
                        }
                    }
                }
            }
            if (bank.contains("Super energy(1)") && settings.getRunEnergy() < 60)
            {
                bank.withdraw("Super energy(1)", 1);
                Sleep.sleepUntil(() -> inventory.contains("Super energy(1)"),3000);
                if (inventory.contains("Super energy(1)"))
                {
                    inventory.getItem("Super energy(1)").interact("Drink");
                    Sleep.sleepUntil(() -> inventory.contains("Vial"), 3000);
                    if (inventory.contains("Vial"))
                    {
                        bank.depositAll("Vial");
                        Sleep.sleepUntil(() -> !inventory.contains("Vial"), 3000);
                    }
                }
            }
            settings.setRunning(true);
        }
        else return;
    }
    private void getBackToFurnace() throws InterruptedException {
        if (keldagrim.contains(myPosition()))
        {
            log("walking to stairs");
            walking.webWalk(nearStairs);
            if (nearStairs.contains(myPosition()))
            {
                objects.closest(9084).interact();
            }
        }
        else if (!keldagrim.contains(myPosition()) && !blastFurnace.contains(myPosition()))
        {
            log("in neither");
            widgets.get(548,53).interact();
            Sleep.sleepUntil(() -> widgets.get(707,6,3) != null, 3000);
            if (widgets.get(707,6,3) != null)
            {
                widgets.get(707,6,3).interact();
                Sleep.sleepUntil(() -> widgets.get(76,11) != null, 3000);
                {
                    if (widgets.get(76,11) != null)
                    {
                        widgets.get(76,11).interact();
                        Sleep.sleepUntil(() -> widgets.get(76,22,2) != null, 3000);
                        if (widgets.get(76,22,2)!= null)
                        {
                            widgets.get(76,22,2).interact();
                            sleep(random(1200,1800));
                            if (widgets.getWidgetContainingText("Blast Furnace") != null)
                            {
                                widgets.get(76,30).interact();
                                Sleep.sleepUntil(()-> keldagrim.contains(myPosition()), 20000);
                            }

                        }
                    }
                }
            }
        }
    }

    private void retrieveOreFromBank() throws InterruptedException {
        inventory.interact("Fill", "Coal bag");
        if (!inventory.contains("Iron ore"))
        {
            bank.withdrawAll("Iron ore");
        }
    }

    private RS2Object getBelt()
    {
        return inventory.getObjects().closest(b -> b.getName().equals("Conveyor belt") && b.hasAction("Put-ore-on"));
    }

    private boolean mustPayForeman()
    {
        return inventory.getWidgets().singleFilter(inventory.getWidgets().getAll(),
                widget -> widget.isVisible() && (widget.getMessage().contains("You must ask the foreman's permission before using the blast<br>furnace"))) != null;
    }

    private void bankItem(String item)
    {
        if (inventory.contains(item) && bank.isOpen())
        {
            bank.depositAll(item);
            Sleep.sleepUntil(() -> (!inventory.contains(item)), 10000);
        }
    }

}

