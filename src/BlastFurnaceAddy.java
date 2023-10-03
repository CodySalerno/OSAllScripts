import com.sun.deploy.security.SelectableSecurityManager;
import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.GrandExchange;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import util.*;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@ScriptManifest(name = "Addy Blast Furnace", author = "Iownreality1", info = "Creates bar", version = 0.1, logo = "")
public class BlastFurnaceAddy extends Script {
    EchoClient client1 = new EchoClient();

    Position bankPosition = new Position(1948,4957,0);
    Position conveyorPosition = new Position(1942,4967,0);
    Position barPosition = new Position(1941,4962,0);
    Position foreman = new Position(1943,4959,0);
    Position bottomConveyor = new Position(1937,4967,0);
    Area nearStairs = new Area(2931,10196,2932,10197);
    Area nearConveyor = new Area(1935,4969, 1944,4965);
    Area nearDispenser = new Area(1939,4965 ,1943, 4970);
    BetterWalk betterWalk = new BetterWalk(this);
    Area keldagrim = new Area(2924,10181,2938,10202);
    Area blastFurnace = new Area(1935,4956,1957,4974);
    Area allVarrock = new Area(3100,3250 ,3375 ,3514);
    long lastPaid;
    long startTime;
    boolean needStaminas = false;
    String s;
    boolean shouldPayForeman = false;
    boolean setOreOnBelt = false;
    boolean needBars;
    boolean checkedBank = false;
    String runningTime;
    Font font = new Font("Open Sans", Font.BOLD, 18);
    RS2Widget staminaChecker = null;
    Rectangle stamRect;
    Color stammed = new Color(213,88,28);
    Color notStammed = new Color(217,180,59);
    boolean coalBagEmptied;
    boolean needMaterials;
    int amountOfOre = 0;


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
        g.drawString("Bars Created  " + (int)(getExperienceTracker().getGainedXP(Skill.SMITHING)/37.5), 10,330);
        g.drawString("Bars per hour  " + (int)(getExperienceTracker().getGainedXPPerHour(Skill.SMITHING)/37.5), 10,350);
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
        if (needMaterials)
        {
            try
            {
                needMaterials = getMaterials();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            return random(500,800);
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
        if (configs.get(547) < 81 && blastFurnace.contains(myPosition()) && !objects.closest("Bar dispenser").hasAction("Take"))
        {
            useCoal();
            return (random(400,700));
        }
        if (blastFurnace.contains(myPosition()))
        {
            log("Blast furnace contains position");
            if (inventory.contains("Coal"))
            {
                log("inventory contains coal going to place in");
                if (!nearConveyor.contains(myPosition()))
                {
                    walking.webWalk(bottomConveyor);
                }
                objects.closest(9100).hover();
                mouse.click(true);
                if (menu.isOpen())
                {
                    menu.selectAction("Put-ore-on");
                    inventory.hover(1);
                    Sleep.sleepUntil(() -> !inventory.contains("Coal"), 1300);
                }
                if (!inventory.contains("Coal"))
                {
                    log("walking to bars");
                    walking.webWalk(barPosition);
                    sleep(300);
                }
                else log("inventory contains coal.");
            }
            if (objects.closest("Bar dispenser").hasAction("Take")) //bars are ready need to take
            {
                if (inventory.onlyContains("Coal bag"))
                {
                    log("going to get bars");
                    objects.closest("Bar dispenser").interact("Take");
                    Sleep.sleepUntil(() -> widgets.get(270,14,38) != null, 7000);
                    if (widgets.get(270,14,38) != null)
                    {
                        widgets.get(270,14,38).interact();
                        Sleep.sleepUntil(() -> inventory.contains("Adamantite bar"), 1000);
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
                        objects.closest("Bank chest").interact("Use");
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
                log("in else");
                if (inventory.contains("Adamantite bar"))
                {
                    log("Banking bars");
                    if (!bank.isOpen())
                    {
                        objects.closest("Bank chest").interact("Use");
                        Sleep.sleepUntil(() -> bank.isOpen(), 7000);
                        if (!bank.isOpen())
                        {
                            walking.webWalk(bankPosition);
                        }
                    }
                    if (bank.isOpen() && inventory.contains("Adamantite bar"))
                    {
                        if (!bank.contains("Adamantite ore") || !bank.contains("Coal"))
                        {
                            //stop();
                            if (bank.depositAllExcept("Coal bag"))
                            {
                                if (bank.depositAllExcept("Coal bag"))
                                {
                                    if (!bank.contains("Adamantite ore") || !bank.contains("Coal"))
                                    {
                                        needMaterials = true;
                                        checkedBank = false;
                                        return 600;
                                    }
                                }
                            }
                        }
                        bank.depositAll("Adamantite bar");
                        Sleep.sleepUntil(() -> !inventory.contains("Adamantite bar"), 3000);
                        staminaCheck();
                    }
                }
                if (!bank.isOpen() && inventory.onlyContains("Coal bag") && (!objects.closest("Bar dispenser").hasAction("Take")))
                {
                    log("opening bank");
                    objects.closest("Bank chest").interact("Use");
                    Sleep.sleepUntil(() -> bank.isOpen(), 7000);
                    staminaCheck();
                }
                if (bank.isOpen() && inventory.onlyContains("Coal bag"))
                {
                    if (!bank.contains("Adamantite ore") || !bank.contains("Coal"))
                    {
                        //stop();
                        if (bank.depositAllExcept("Coal bag"))
                        {
                            if (!bank.contains("Adamantite ore") || !bank.contains("Coal"))
                            {
                                needMaterials = true;
                                checkedBank = false;
                                return 600;
                            }
                        }
                    }
                    log("getting ore");
                    staminaCheck();
                    if (inventory.getItem("Coal bag").hasAction("Fill"))
                    {
                        inventory.getItem("Coal bag").interact("Fill");
                    }
                    bank.withdrawAll("Adamantite ore");
                    walking.webWalk(bottomConveyor);
                }
                if (inventory.contains("Adamantite ore") &&  (!nearConveyor.contains(myPosition())) && (!objects.closest("Bar dispenser").hasAction("Take")))
                {
                    log("walking with ore to conveyor");
                    walking.webWalk(nearConveyor);
                    //betterWalk.MyWalkingEvent(bottomConveyor);
                }
                if (nearConveyor.contains(myPosition()) && inventory.contains("Adamantite ore") && (!objects.closest("Bar dispenser").hasAction("Take")))
                {
                    log("depositing ore to conveyor");
                    //objects.closest(9100).interact("Put-ore-on"); //conveyor belt
                    objects.closest(9100).hover();
                    log("hovering");
                    mouse.click(true);
                    log("right clicked");
                    if (menu.isOpen())
                    {
                        log("menu is open");
                        if (menu.selectAction("Put-ore-on"))
                        {
                            coalBagEmptied = false;
                        }
                        else inventory.hover(1);
                    }
                    Sleep.sleepUntil(() -> !inventory.contains("Adamantite ore"), 5000);
                }
                if (!inventory.contains("Adamantite ore") && !coalBagEmptied && conveyorPosition.equals(myPosition()))
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
                            else
                            {
                                Sleep.sleepUntil(() -> !inventory.contains("Coal"), 1000);
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
                if (nearConveyor.contains(myPosition()) && !inventory.contains("Adamantite ore") && coalBagEmptied)
                {
                    log("no Adamantite ore going to bar position");
                    walking.webWalk(barPosition);
                    Sleep.sleepUntil(() -> objects.closest("Bar dispenser").hasAction("Take"), 5000);
                    if (objects.closest("Bar dispenser").hasAction("Take"))
                    {
                        objects.closest("Bar dispenser").interact("Take");
                        Sleep.sleepUntil(() -> widgets.get(270,14,38) != null, 7000);
                        if (widgets.get(270,14,38) != null)
                        {
                            widgets.get(270,14,38).interact();
                            Sleep.sleepUntil(() -> inventory.contains("Adamantite bar"), 1000);
                            if (dialogues.isPendingContinuation())
                            {
                                dialogues.completeDialogue();
                            }
                        }
                    }
                }
                if ((!bank.isOpen() && !inventory.isEmptyExcept("Adamantite ore", "Adamantite bar", "Coal", "Coal bag") ))
                {
                    log("weird item in inventory banking");
                    betterWalk.MyWalkingEvent(bankPosition);
                    objects.closest("Bank chest").interact("Use");
                    Sleep.sleepUntil(() -> bank.isOpen(), 3000);
                    if (bank.isOpen())
                    {
                        if (!bank.contains("Adamantite ore") || !bank.contains("Coal"))
                        {
                            //stop();
                            if (bank.depositAllExcept("Coal bag"))
                            {
                                if (!bank.contains("Adamantite ore") || !bank.contains("Coal"))
                                {
                                    needMaterials = true;
                                    checkedBank = false;
                                    return 600;
                                }
                            }
                        }
                        bank.depositAllExcept("Coal bag");
                    }
                }
            }
        }
        if (allVarrock.contains(myPosition()))
        {
            walking.webWalk(UsefulAreas.GeArea);
            if (UsefulAreas.GeArea.contains(myPosition()))
            {
                npcs.closest("Banker").interact("Bank");
                Sleep.sleepUntil(() -> bank.isOpen(), 8000);
                if (bank.isOpen())
                {
                    if (!bank.contains("Adamantite ore") || !bank.contains("Coal"))
                    {
                        //stop();
                        if (bank.depositAllExcept("Coal bag"))
                        {
                            if (!bank.contains("Adamantite ore") || !bank.contains("Coal"))
                            {
                                needMaterials = true;
                                checkedBank = false;
                                return 600;
                            }
                            else
                            {
                                log("go back to furnace");
                                getBackToFurnace();
                            }
                        }
                    }
                    else
                    {
                        getBackToFurnace();
                    }
                }
            }
        }
        if (keldagrim.contains(myPosition()))
        {
            getBackToFurnace();
        }

        if (configs.get(545) != 0)
        {
            needBars = true;
        }
        else needBars = false;
        return  random(400, 600);
    }



    private boolean getMaterials() throws InterruptedException, IOException {

        if (blastFurnace.contains(myPosition()))
        {
            if (bank.isOpen())
            {
                if (bank.contains("Varrock teleport"))
                {
                    log("Withdraw varrock teleport, Adamantite bars, and coins to resupply.");
                    bank.withdraw("Varrock teleport", 1);
                    if (bank.contains("Adamantite bar"))
                    {
                        bank.enableMode(Bank.BankMode.WITHDRAW_NOTE);
                        bank.withdrawAll("Adamantite bar");
                    }
                    if (bank.contains("Coins"))
                    {
                        bank.enableMode(Bank.BankMode.WITHDRAW_NOTE);
                        bank.withdrawAll("Coins");
                    }
                    if (inventory.contains("Varrock teleport"))
                    {
                        bank.close();
                        Sleep.sleepUntil(() -> !bank.isOpen(), 5000);
                        if (!bank.isOpen())
                        {
                            inventory.getItem("Varrock teleport").interact();
                            Sleep.sleepUntil(() -> !blastFurnace.contains(myPosition()), 8000);
                        }
                    }
                }
                else
                {
                    log("No varrock teleports to restock, ending script.");
                    stop();
                }
            }
            else
            {
                log("out of materials go to bank");
                walking.webWalk(bankPosition);
                objects.closest("Bank chest").interact("Use");
                Sleep.sleepUntil(() -> bank.isOpen(), 5000);
            }
        }
        else if (UsefulAreas.GeArea.contains(myPosition()))
        {
            if (!checkedBank)
            {
                npcs.closest("Banker").interact("Bank");
                Sleep.sleepUntil(() -> bank.isOpen(), 8000);
                if (bank.isOpen())
                {
                    if (bank.contains("Coins"))
                    {
                        bank.withdrawAll("Coins");
                    }
                    if (bank.contains("Adamantite bar"))
                    {
                        if (bank.enableMode(Bank.BankMode.WITHDRAW_NOTE))
                        {
                            bank.withdrawAll("Adamantite bar");
                        }
                    }
                    if (bank.contains("Coal"))
                    {
                        if (bank.enableMode(Bank.BankMode.WITHDRAW_NOTE))
                        {
                            bank.withdrawAll("Coal");
                        }
                    }
                    if (!bank.contains("Coins") && !bank.contains("Adamantite bar"))
                    {
                        checkedBank = true;
                        return true;
                    }
                }
            }
            else if (!grandExchange.isOpen())
            {
                log("GE not open, opening, then rsupplying.");
                npcs.closest("Grand Exchange clerk").interact("Exchange");
                Sleep.sleepUntil(() -> grandExchange.isOpen(), 7000);
            }
            else if (grandExchange.isOpen())
            {
                log("GE open resupplying.");
                if (inventory.contains("Adamantite bar"))
                {
                    log("selling Adamantite bars");
                    grandExchange.sellItem(2362,1910,(int)inventory.getAmount("Adamantite bar"));
                }
                else if (steelBarPending() || steelBarInitializing())
                {
                    log("waiting for Adamantite bars to sell.");
                    Sleep.sleepUntil(() -> grandExchange.getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.FINISHED_SALE, 50000);
                    if (grandExchange.getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.FINISHED_SALE)
                    {
                        grandExchange.collect();
                    }
                    else
                    {
                        return true;
                    }
                }
                else if (grandExchange.getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.FINISHED_SALE)
                {
                    log("if completed sale colect");
                    grandExchange.collect();
                    sleep(random(1800,2400));
                }
                else if (grandExchange.getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.EMPTY
                        && !inventory.contains("Adamantite bar") && inventory.contains("Coins") && !inventory.contains("Adamantite ore"))
                {
                    log("no Adamantite bars, and box 1 is empty. time to buy rune.");
                    int coins = (int)inventory.getAmount("Coins");
                    if (amountOfOre == 0)
                    {
                        amountOfOre = coins/1150;
                        if (amountOfOre > 1500)
                        {
                            amountOfOre = 1500;
                        }
                    }
                    if (amountOfOre != 0)
                    {
                        grandExchange.buyItem(449, "Adamantite ore", 1110,amountOfOre);
                        sleep(random(1200,1800));
                    }

                }
                else if ((ironOrePending() || ironOreInitializing()))
                {
                    log("sleep waiting until rune ore buys");
                    Sleep.sleepUntil(() -> (grandExchange.getStatus(GrandExchange.Box.BOX_1) != GrandExchange.Status.FINISHED_BUY), 50000);
                    return true;
                }
                else if (grandExchange.getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.FINISHED_BUY)
                {
                    grandExchange.collect();
                    Sleep.sleepUntil(() -> inventory.contains("Coal") && inventory.contains("Adamantite ore"), 5000);
                }
                else if (inventory.contains("Coal") && inventory.contains("Adamantite ore"))
                {
                    amountOfOre = 0;
                    return false;
                }
            }

        }
        else
        {
            walking.webWalk(UsefulAreas.GeArea);
        }
        return  true;
    }

    private void useCoal()
    {
        if (blastFurnace.contains(myPosition()))
        {
            log("in use coal function");
            if (!bank.isOpen() && !inventory.contains("Coal")) //goes to bank if not open and no coal
            {
                log("going to bank");
                betterWalk.MyWalkingEvent(bankPosition);
                objects.closest("Bank chest").interact("Use");
            }
            if (bank.isOpen() && !inventory.contains("Coal")) //gets coal out of bank
            {
                log("getting coal out of bank");
                bank.depositAllExcept("Coal bag");
                if (inventory.onlyContains("Coal bag"))
                {
                    inventory.getItem("Coal bag").interact("Fill");
                    bank.withdrawAll("Coal");
                    Sleep.sleepUntil(() -> inventory.contains("Coal"), 1000);
                }
            }
            if (inventory.contains("Coal"))
            {
                if (!nearConveyor.contains(myPosition()))
                {
                    walking.webWalk(nearConveyor);
                }
                objects.closest(9100).hover();
                mouse.click(true);
                if (menu.isOpen())
                {
                    if (menu.selectAction("Put-ore-on"))
                    {
                        Sleep.sleepUntil(() -> !inventory.contains("Coal"), 3000);
                        inventory.getItem("Coal bag").interact("Empty");
                        Sleep.sleepUntil(() -> inventory.contains("Coal"), 1000);
                    }
                    else
                    {
                        inventory.hover(1);
                    }
                }
            }
            if (inventory.contains("Coal"))
            {
                if (!nearConveyor.contains(myPosition()))
                {
                    walking.webWalk(nearConveyor);
                }
                objects.closest(9100).hover();
                mouse.click(true);
                if (menu.isOpen())
                {
                    if (menu.selectAction("Put-ore-on"))
                    {
                        Sleep.sleepUntil(() -> !inventory.contains("Coal"), 3000);
                    }
                    else
                    {
                        inventory.hover(1);
                    }
                }
            }
        }
    }
    private void staminaCheck()
    {
        if (bank.isOpen())
        {

            if ((configs.get(1575) == 0 || configs.get(1575) == 8388608) && settings.getRunEnergy() < 80) //matches current color to non stammed color.
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
            if (!settings.isRunning())
            {
                settings.setRunning(true);
            }
        }
        else return;
    }
    private void getBackToFurnace() throws InterruptedException {
        if (!keldagrim.contains(myPosition()) && !blastFurnace.contains(myPosition()) && !needMaterials)
        {
            log("in neither");
            if (bank.isOpen())
            {
                bank.close();
            }
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
        if (keldagrim.contains(myPosition()))
        {
            log("walking to stairs");
            walking.webWalk(nearStairs);
            if (nearStairs.contains(myPosition()))
            {
                objects.closest(9084).interact("Climb-down");
            }
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

    private void TradeMule (int totalCoins) throws InterruptedException, IOException {
        if (bank.isOpen())
        {
            bank.close();
        }
        log("Trade mule method.");
        if ((int)inventory.getAmount("Coins") > totalCoins)
        {
            log("writing to server");
            client1.startConnection("127.0.0.1", 6666);
            client1.sendMessage("Trade");
            log("looking to trade mule.");
            String muleName = "aceuptheslee";
            boolean tradeComplete = false;
            if(getPlayers().closest(muleName) != null)
            {
                while (!tradeComplete)
                {
                    log("while trade is not complete");
                    getPlayers().closest(muleName).interact("Trade with");
                    log("Sending Trade");
                    sleep(random(8000,12000));
                    if (trade.isCurrentlyTrading())
                    {
                        if (trade.isFirstInterfaceOpen())
                        {
                            trade.offer("Coins", totalCoins);
                            log("Trading Gold First Screen");
                            sleep(random(8000,12000));
                            trade.acceptTrade();
                            sleep(random(8000,12000));
                        }
                        if (trade.isSecondInterfaceOpen())
                        {
                            if (trade.getOurOffers().contains("Coins") && trade.getOtherPlayer().equals(muleName))
                            {
                                log("Accept second trade");
                                trade.acceptTrade();
                                totalCoins = 0;
                                tradeComplete = true;
                                log("Trade complete = true break while loop");
                                sleep(random(8000,12000));
                            }
                            else
                            {
                                trade.declineTrade();
                            }
                        }
                    }
                }
            }
            else
            {
                sleep(8000);
            }
        }
        if (totalCoins == 0)
        {
            log("changing file back to F");
            File file = new File("C:\\Users\\zjmnk\\OSBot\\Data\\NeedsTrade.txt");
            FileWriter myWriter;
            try
            {
                log("Gold is less then 6M changing file to F");
                myWriter = new FileWriter(file);
                myWriter.write("F");
                myWriter.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean steelBarInitializing()
    {
        if (grandExchange.getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.INITIALIZING_SALE)
        {
            return true;
        }
        else return false;

    }
    private boolean coalInitializing()
    {
        if (grandExchange.getStatus(GrandExchange.Box.BOX_2) == GrandExchange.Status.INITIALIZING_BUY)
        {
            return  true;
        }
        else return false;
    }
    private boolean coalPending()
    {
        if (grandExchange.getStatus(GrandExchange.Box.BOX_2) == GrandExchange.Status.PENDING_BUY)
        {
            return  true;
        }
        else return false;
    }
    private boolean coalEmpty()
    {
        if (grandExchange.getStatus(GrandExchange.Box.BOX_2) == GrandExchange.Status.EMPTY)
        {
            return  true;
        }
        else return false;
    }
    private boolean ironOreInitializing()
    {
        if (grandExchange.getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.INITIALIZING_BUY)
        {
            return  true;
        }
        else return false;
    }
    private boolean ironOrePending()
    {
        if (grandExchange.getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.PENDING_BUY)
        {
            return  true;
        }
        else return false;
    }
    private boolean ironOreEmpty()
    {
        if (grandExchange.getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.EMPTY)
        {
            return  true;
        }
        else return false;
    }
    private boolean steelBarPending()
    {
        if (grandExchange.getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.PENDING_SALE)
        {
            return  true;
        }
        else return false;
    }
    private boolean steelBarCompleted()
    {
        if (grandExchange.getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.COMPLETING_SALE)
        {
            return  true;
        }
        else return false;
    }

}

