import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import util.FormattingForPaint;
import util.Sleep;

import java.awt.*;

@ScriptManifest(name = "Blast Furnace1.02", author = "Iownreality1", info = "Mines at motherload mine", version = 0.1, logo = "")
public class BlastFurnace extends Script {

    Position bankPosition = new Position(1948,4957,0);
    Position conveyorPosition = new Position(1942,4967,0);
    Position barPosition = new Position(1941,4962,0);
    Position foreman = new Position(1943,4959,0);
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

        if (!settings.isRunning() && settings.getRunEnergy() > 15)
        {
            settings.setRunning(true);
        }
        if (bankPosition.equals(myPosition())&& inventory.contains("Steel bar") && !bank.isOpen())
        {
            log("Using bank");
            objects.closest("Bank chest").interact("Use");
            Sleep.sleepUntil(() -> (bank.isOpen()), 10000);

            if (players.settings.getRunEnergy() < 20)
            {
                if (inventory.isFull())
                {
                    bank.depositAll("Steel bar");
                    Sleep.sleepUntil(() -> (!inventory.contains("Steel bar")), 10000);
                }
                bank.withdraw(12631, 1);
                Sleep.sleepUntil(() -> (inventory.contains(12631)), 10000);
                bank.withdraw(3022, 1); //super energy 1
                Sleep.sleepUntil(() -> (inventory.contains(3022)), 10000);
                inventory.getItem(12631).interact("Drink");
                sleep(random(2100,2400));
                inventory.getItem(3022).interact("Drink");
                Sleep.sleepUntil(() -> !inventory.contains(3022), 2000);
                bank.depositAll("Vial");
            }
            if (!inventory.isFull())
            {
                staminaChecker = widgets.get(160,29);
                stamRect = staminaChecker.getRectangleIgnoreIsHidden(false);
                Color staminaColor = colorPicker.colorAt((stamRect.x + stamRect.width/2), (stamRect.y + stamRect.height/2));
                if (configs.get(1575) == 0)
                {
                    bank.withdraw("Stamina potion(1)", 1);
                    Sleep.sleepUntil(() -> (inventory.contains("Stamina potion(1)")), 10000);
                    inventory.getItem("Stamina potion(1)").interact("Drink");
                    Sleep.sleepUntil(() -> (inventory.contains("Vial")), 10000);
                    bank.depositAll("Vial");
                }
            }
        }
        else if (shouldPayForeman)
        {
            if (!inventory.contains("Coins"))
            {
                WalkingEvent myEvent = new WalkingEvent(bankPosition);
                myEvent.setMinDistanceThreshold(0);
                execute(myEvent);
                objects.closest("Bank chest").interact("Use");
                Sleep.sleepUntil(() -> (bank.isOpen()), 10000);
                bankItem("Coal");
                bankItem("Iron ore");
                bankItem("Steel Bar");
                bank.withdraw("Coins", 2500);
                Sleep.sleepUntil(() -> (inventory.contains("Coins")), 10000);
            }
            else
            {
                WalkingEvent myEvent = new WalkingEvent(foreman);
                myEvent.setMinDistanceThreshold(0);
                execute(myEvent);
                if (foreman.equals(myPosition()))
                {
                    npcs.closest("Blast Furnace Foreman").interact("Pay");
                    Sleep.sleepUntil(() -> (widgets.getWidgetContainingText("Pay 2,500 coins to use the Blast Furnace?") != null), 10000);
                    widgets.get(219,1,1).interact();
                    Sleep.sleepUntil(() -> widgets.get(219,1,1) == null, 2000);
                    shouldPayForeman = false;
                }
            }
        }

        else if (bankPosition.equals(myPosition()) && !inventory.contains("Iron ore") && !bank.isOpen())
        {
            log("Using bank");
            objects.closest("Bank chest").interact("Use");
            Sleep.sleepUntil(() -> (bank.isOpen()), 10000);


            if (players.settings.getRunEnergy() < 20)
            {
                if (inventory.isFull())
                {
                    bank.depositAll(inventory.getItemInSlot(2).toString());
                }
                bank.withdraw("Super energy(4)", 1);
                Sleep.sleepUntil(() -> (inventory.contains("Super energy(4)")), 10000);
                inventory.getItem("Super energy(4)").interact("Drink");
                sleep(random(2100,2400));
                inventory.getItem("Super energy(3)").interact("Drink");
                sleep(random(2100,2400));
                inventory.getItem("Super energy(2)").interact("Drink");
                sleep(random(2100,2400));
                inventory.getItem("Super energy(1)").interact("Drink");
                Sleep.sleepUntil(() -> (inventory.contains("Vial")), 10000);
                bank.depositAll("Vial");
            }
            if (!inventory.isFull())
            {
                if (configs.get(1575) == 0)
                {
                    bank.withdraw("Stamina potion(1)", 1);
                    Sleep.sleepUntil(() -> (inventory.contains("Stamina potion(1)")), 10000);
                    inventory.getItem("Stamina potion(1)").interact("Drink");
                    Sleep.sleepUntil(() -> (inventory.contains("Vial")), 10000);
                    bank.depositAll("Vial");
                }
            }
        }

        else if (!equipment.contains("Ice gloves")) stop();

        else if (bankPosition.equals(myPosition()) && inventory.contains("Steel bar") && bank.isOpen())
        {

            if (!bank.contains("Iron ore") || !bank.contains("Coal"))
            {
                stop();
            }

            log("Bank all steel bars");
            bank.depositAll("Steel bar");
            Sleep.sleepUntil(() -> (!inventory.contains("Steel bar")), 10000);
            if (inventory.contains("Coins"))
            {
                bank.depositAll("Coins");
            }
            if (players.settings.getRunEnergy() < 20)
            {
                if (inventory.isFull())
                {
                    bank.depositAll(inventory.getItemInSlot(2).toString());
                }
                bank.withdraw("Super energy(4)", 1);
                Sleep.sleepUntil(() -> (inventory.contains("Super energy(4)")), 10000);
                inventory.getItem("Super energy(4)").interact("Drink");
                sleep(random(2100,2400));
                inventory.getItem("Super energy(3)").interact("Drink");
                sleep(random(2100,2400));
                inventory.getItem("Super energy(2)").interact("Drink");
                sleep(random(2100,2400));
                inventory.getItem("Super energy(1)").interact("Drink");
                Sleep.sleepUntil(() -> (inventory.contains("Vial")), 10000);
                bank.depositAll("Vial");
            }
            if (configs.get(1575) == 0)
            {
                bank.withdraw("Stamina potion(1)", 1);
                Sleep.sleepUntil(() -> (inventory.contains("Stamina potion(1)")), 10000);
                inventory.getItem("Stamina potion(1)").interact("Drink");
                Sleep.sleepUntil(() -> (inventory.contains("Vial")), 10000);
                bank.depositAll("Vial");
            }
        }

        else if (bankPosition.equals(myPosition()) && !inventory.contains("Iron ore") && bank.isOpen() && !needBars)
        {
            if (!bank.contains("Iron ore") || !bank.contains("Coal"))
            {
                stop();
            }
            log("Get ore from bank");
            if (players.settings.getRunEnergy() < 20)
            {
                if (inventory.isFull())
                {
                    bank.depositAll(inventory.getItemInSlot(2).toString());
                }
                bank.withdraw("Super energy(4)", 1);
                Sleep.sleepUntil(() -> (inventory.contains("Super energy(4)")), 10000);
                inventory.getItem("Super energy(4)").interact("Drink");
                sleep(random(2100,2400));
                inventory.getItem("Super energy(3)").interact("Drink");
                sleep(random(2100,2400));
                inventory.getItem("Super energy(2)").interact("Drink");
                sleep(random(2100,2400));
                inventory.getItem("Super energy(1)").interact("Drink");
                Sleep.sleepUntil(() -> (inventory.contains("Vial")), 10000);
                bank.depositAll("Vial");
            }
            if (configs.get(1575) == 0)
            {
                bank.withdraw("Stamina potion(1)", 1);
                Sleep.sleepUntil(() -> (inventory.contains("Stamina potion(1)")), 10000);
                inventory.getItem("Stamina potion(1)").interact("Drink");
                Sleep.sleepUntil(() -> (inventory.contains("Vial")), 10000);
                bank.depositAll("Vial");
            }
            retrieveOreFromBank();
        }

        else if (bankPosition.equals(myPosition()) && inventory.contains("Iron ore"))
        {
            WalkingEvent myEvent = new WalkingEvent(conveyorPosition);
            myEvent.setMinDistanceThreshold(0);
            execute(myEvent);
        }

        else if (conveyorPosition.equals(myPosition()) && (inventory.contains("Iron ore")))
            {
                if (inventory.contains("Iron ore"))
                {
                    RS2Object belt = getBelt();
                    if (belt != null)
                    {
                        log("if belt.interact(put-ore-on)");
                        if (belt.interact("Put-ore-on"))
                        {
                            log("Sleeping on 1");
                            Sleep.sleepUntil(() -> (!inventory.getInventory().contains("Iron ore") || mustPayForeman()), 10000);
                            if (mustPayForeman())
                            {
                                log(("inside should pay foreman"));
                                shouldPayForeman = true;
                            }
                            else
                            {
                                log(("inside else"));
                                Sleep.sleepUntil(() -> !inventory.getInventory().contains("Iron ore"), 10000);
                            }
                        }

                    }

                    if (!inventory.contains("Coal") && !shouldPayForeman)
                    {
                        inventory.getItem("Coal bag").interact("Empty");
                        belt = getBelt();
                        if (belt != null)
                        {
                            if (belt.interact("Put-ore-on"))
                            {
                                log("Sleeping on 3");
                                Sleep.sleepUntil(() -> !inventory.getInventory().contains("Coal") || mustPayForeman(), 10000);
                                if (mustPayForeman())
                                {
                                    shouldPayForeman = true;
                                }
                                else
                                {
                                    Sleep.sleepUntil(() -> !inventory.getInventory().contains("Coal"), 10000);
                                    if (!inventory.getInventory().contains("Coal"))
                                    {
                                        setOreOnBelt = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        else if (conveyorPosition.equals(myPosition()) && !inventory.contains("Iron ore") && !inventory.contains("Coal"))
            {
                WalkingEvent myEvent = new WalkingEvent(barPosition);
                myEvent.setMinDistanceThreshold(0);
                execute(myEvent);
            }

        else if (barPosition.equals(myPosition()) && !inventory.contains("Steel bar") && needBars)
        {
            objects.closest("Bar dispenser").interact("Take");
            Sleep.sleepUntil(() -> getWidgets().getWidgetContainingText("How many would you like to take?") != null, 10000);
            widgets.get(270,14,38).interact();
            Sleep.sleepUntil(() -> inventory.contains("Steel bar"), 10000);
        }

        else if (conveyorPosition.equals(myPosition()) && inventory.contains("Steel bar"))
        {
            WalkingEvent myEvent = new WalkingEvent(bankPosition);
            myEvent.setMinDistanceThreshold(0);
            execute(myEvent);
        }

        else
        {
            WalkingEvent myEvent = new WalkingEvent(bankPosition);
            myEvent.setMinDistanceThreshold(0);
            execute(myEvent);
        }
        if (configs.get(545) != 0)
        {
            needBars = true;
        }
        else needBars = false;


        return random(500,700);
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

