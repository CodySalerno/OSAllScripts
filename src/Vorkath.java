import javafx.geometry.Pos;
import org.osbot.P;
import org.osbot.Sk;
import org.osbot.rs07.api.Configs;
import org.osbot.rs07.api.Skills;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Projectile;
import org.osbot.rs07.api.ui.*;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.event.interaction.MouseMoveProfile;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.Condition;
import util.BetterWalk;
import util.FormattingForPaint;
import util.Sleep;
import util.constantVariables;

import java.awt.*;
import java.util.Dictionary;
import java.util.List;

@ScriptManifest(name = "Vorkath1.17", author = "Iownreality1", info = "Kills Vorkath", version = 0.1, logo = "")
public class Vorkath extends Script
{
    NPC vorkath;
    Position vorkPosition;
    Position safeSpotStart;
    Position nearSpotStart1;
    Position nearSpotStart2;
    Position nearSpotStart3;
    Position nearSpotStart4;
    Position safeSpotEnd;
    Area relleka = new Area(2626, 3668, 2645, 3705);
    Area vorkIsland = new Area(2265, 4034, 2282, 4052);
    Area falDeath = new Area(2962, 3333, 2979, 3346);
    Area falBank = new Area(2943, 3368, 2949, 3373);

    boolean poisonPhase;
    boolean icePhase;
    boolean attacking = false;
    boolean bombMoved = false;
    boolean needItems = false;
    BetterWalk betterWalk = new BetterWalk(this);
    List<Projectile> iceProjectiles;
    List<Projectile> bomb;
    List<Projectile> poison;
    double lastEat;
    double icephaseTimer;
    int kc = 0;
    String runningTime;
    boolean spawnSeen;
    long startTime;
    int deathCounter = 0;
    long lastKill;
    boolean lastBomb = false;
    Thread attackListener = new Thread(() ->
    {
        while (getBot().getScriptExecutor().isRunning() && !getBot().getScriptExecutor().isPaused() && !getBot().getScriptExecutor().isSuspended())
        {
            try
            {
                iceProjectiles = getProjectiles().filter(f -> f.getId() == 395);
                bomb = getProjectiles().filter(f -> f.getId() == 1481);
                poison = getProjectiles().filter(f -> f.getId() == 1483);
                if (bomb.size() > 0)
                {
                    icePhase = false;
                    poisonPhase = false;
                }
                if (objects.closest(32000) != null || poison.size() > 0)
                {
                    poisonPhase = true;
                }
                else
                {
                    poisonPhase = false;
                }
                if (vorkath != null && vorkath.getHealthPercent() == 0)
                {
                    poisonPhase = false;
                    icePhase = false;
                    kc++;
                    lastKill = System.currentTimeMillis();
                    sleep(4800);
                }
                if (falDeath.contains(myPosition()))
                {
                    poisonPhase = false;
                    icePhase = false;
                    deathCounter++;
                    sleep(10000);
                }
                if (npcs.closest("Zombified spawn") != null && !spawnSeen && icePhase)
                {
                    spawnSeen = true;
                }
                if (npcs.closest("Zombified spawn") == null && spawnSeen && icePhase)
                {
                    icePhase = false;
                }
                if (System.currentTimeMillis() > icephaseTimer+15000)
                {
                    icePhase = false;
                }
                if (bomb.size() > 0 && !lastBomb)
                {
                    lastBomb = true;
                    log("I see bomb now.");
                }
                if (bomb.size() == 0)
                {
                    lastBomb = false;
                }
            }
            catch (Exception e)
            {
                log(e.toString());
            }
        }
    });

    boolean stopAttacking = false;

    @Override
    public void onPaint(final Graphics2D g)
    {
        runningTime = FormattingForPaint.formatTime(System.currentTimeMillis()-startTime);
        g.setFont(constantVariables.font);
        g.setColor(Color.green);
        g.drawString("Running Time: " + runningTime,10,250);
        g.drawString("Vorkath kills: " + kc,10,270);
        g.drawString("Deaths: " + deathCounter,10,290);

    }


    @Override
    public void onStart()
    {
        lastKill = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
        iceProjectiles = getProjectiles().filter(f -> f.getId() == 395);
        bomb = getProjectiles().filter(f -> f.getId() == 1481);
        poison = getProjectiles().filter(f -> f.getId() == 1483);
        attackListener.start();
        lastEat = System.currentTimeMillis();
    }
    @Override
    public int onLoop() throws InterruptedException
    {
        if (attackListener.getState() == Thread.State.TERMINATED)
        {
            lastKill = System.currentTimeMillis();
            attackListener = new Thread(() ->
            {
                while (getBot().getScriptExecutor().isRunning() && !getBot().getScriptExecutor().isPaused() && !getBot().getScriptExecutor().isSuspended())
                {
                    try
                    {
                        iceProjectiles = getProjectiles().filter(f -> f.getId() == 395);
                        bomb = getProjectiles().filter(f -> f.getId() == 1481);
                        poison = getProjectiles().filter(f -> f.getId() == 1483);
                        if (bomb.size() > 0)
                        {
                            icePhase = false;
                            poisonPhase = false;
                        }
                        if (objects.closest(32000) != null || poison.size() > 0)
                        {
                            poisonPhase = true;
                        }
                        else
                        {
                            poisonPhase = false;
                        }
                        if (vorkath != null && vorkath.getHealthPercent() == 0)
                        {
                            poisonPhase = false;
                            icePhase = false;
                            kc++;
                            lastKill = System.currentTimeMillis();
                            sleep(4800);
                        }
                        if (falDeath.contains(myPosition()))
                        {
                            poisonPhase = false;
                            icePhase = false;
                            deathCounter++;
                            sleep(10000);
                        }
                        if (npcs.closest("Zombified spawn") != null && !spawnSeen && icePhase)
                        {
                            spawnSeen = true;
                        }
                        if (npcs.closest("Zombified spawn") == null && spawnSeen && icePhase)
                        {
                            icePhase = false;
                        }
                        if (System.currentTimeMillis() > icephaseTimer+15000)
                        {
                            icePhase = false;
                        }
                    }
                    catch (Exception e)
                    {
                        log(e.toString());
                    }
                }
            });
            attackListener.start();
        }
        if (lastKill < System.currentTimeMillis()- 1200000 || deathCounter > 9)
        {
            log("20 mninutes since last kill");
            log("kills: " + kc);
            log("Deaths: " + deathCounter);
            log("running time" + runningTime);
            stop();
        }
        if (!hasEquipment() || needItems)
        {
            log("Resupplying if");
            poisonPhase = false;
            icePhase = false;
            if (falDeath.contains(myPosition()))
            {
                log("fal death");
                walking.webWalk(falBank);
            }
            else if (inventory.contains("Dust rune", "Air rune", "Law Rune") && inventory.contains("Rune pouch"))
            {
                if (inventory.isItemSelected()) inventory.deselectItem();
                if (inventory.contains("Dust rune"))
                {
                    inventory.getItem("Dust rune").interact();
                    Sleep.sleepUntil(() -> inventory.isItemSelected(), 1000);
                    if (inventory.isItemSelected())
                    {
                        inventory.getItem("Rune pouch").interact();
                    }
                }
                if (inventory.isItemSelected()) inventory.deselectItem();
                if (inventory.contains("Law rune"))
                {
                    inventory.getItem("Law rune").interact();
                    Sleep.sleepUntil(() -> inventory.isItemSelected(), 1000);
                    if (inventory.isItemSelected())
                    {
                        inventory.getItem("Rune pouch").interact();
                    }
                }
                if (inventory.isItemSelected()) inventory.deselectItem();
                if (inventory.contains("Chaos rune"))
                {
                    inventory.getItem("Chaos rune").interact();
                    Sleep.sleepUntil(() -> inventory.isItemSelected(), 1000);
                    if (inventory.isItemSelected())
                    {
                        inventory.getItem("Rune pouch").interact();
                    }
                }
                equipItems();
                return 0;
            }
            else if (falBank.contains(myPosition()))
            {
                log("fal bank");
                if (!bank.isOpen() && (!inventory.contains("Earth rune") || !inventory.contains("Law rune") || !inventory.contains("Air rune")))
                {
                    log("open bank");
                    objects.closest("Bank booth").interact("Bank");
                    Sleep.sleepUntil(() -> bank.isOpen(), 3000);
                }
                else if (bank.isOpen() && (!inventory.contains("Earth rune") || !inventory.contains("Law rune") || !inventory.contains("Air rune")))
                {
                    log("need runes");
                    bank.withdraw("Earth rune", 1);
                    bank.withdraw("Air rune", 1);
                    bank.withdraw("Law rune", 1);
                }
                else if (inventory.contains("Earth rune") && inventory.contains("Law rune") && inventory.contains("Air rune"))
                {
                    if (bank.isOpen())
                    {
                        bank.close();
                        Sleep.sleepUntil(() -> !bank.isOpen(), 3000);
                    }
                    log("cast teleport");
                    magic.castSpell(Spells.NormalSpells.TELEPORT_TO_HOUSE);
                }
            }
            else if (objects.closest(4525) != null) //inside house portal
            {
                log("house portal");
                objects.closest(29339).interact("Enter");
            }
            else if (objects.closest(new Area(2098,3920,2098,3920),16700 ) != null)
            {
                log("lunar bank");
                if (camera.getYawAngle() != 0)
                {
                    camera.moveYaw(0);
                }
                if (objects.closest(new Area(2098,3920,2098,3920),16700 ).interact())
                {
                    Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 1600);
                    if (dialogues.isPendingContinuation())
                    {
                        dialogues.completeDialogue();
                        sleep(random(1800,2400));
                    }
                }
            }
            else if (relleka.contains(myPosition()) && !inventory.isFull() && widgets.get(602,7) == null)
            {
                log("Go to dock");
                walking.webWalk(new Position(2640,3695, 0));
                if (npcs.closest(7504) != null)
                {
                    npcs.closest(7504).interact("Collect");
                    sleep(3000);
                }
                if (dialogues.isPendingContinuation())
                {
                    log("pending continuation");
                    equipItems();
                    needItems = false;
                }
            }
            else if (widgets.get(602,7) != null)
            {
                log("widget is not null Madge");
                widgets.get(602,7).interact();
                sleep(1800);
                if (widgets.get(602,1,11)!= null)
                {
                    widgets.get(602,1,11).interact();
                }
            }
            else if (inventory.isFull())
            {
                if (widgets.get(602,1,11)!= null)
                {
                    widgets.get(602,1,11).interact();
                }
                else
                {
                    equipItems();
                }
                if (inventory.isFull())
                {
                    npcs.closest("Torfinn").interact("Collect");
                    sleep(5000);
                    if (dialogues.isPendingContinuation())
                    {
                        log("pending continuation");
                        equipItems();
                        needItems = false;
                    }
                }
            }
            if (objects.closest(27785) != null)
            {
                magic.castSpell(Spells.NormalSpells.TELEPORT_TO_HOUSE);
                sleep(5000);
            }
            return random(500,1000);
        }
        if (camera.getScaleZ() >200 && !poisonPhase && npcs.closest("Vorkath") != null)
        {
            log("Zooming out");
            myPlayer().hover();
            mouse.scrollDown();
            if (camera.getYawAngle() != 0)
            {
                camera.moveYaw(0);
            }
            return 0;
        }
        setVariables();
        if (!poisonPhase && !icePhase && bomb.size() == 0)
        {
            if (vorkath != null && !poisonPhase)
            {
                if (vorkath.getHealthPercent() != 100)
                {
                    if (vorkath.getHealthPercent() > 25)
                    {
                        if (inventory.contains("Ruby dragon bolts (e)"))
                        {
                            log("equipping ruby bolts");
                            inventory.getItem("Ruby dragon bolts (e)").interact();
                            attacking = false;
                        }
                    }
                    else
                    {
                        if (inventory.contains("Diamond dragon bolts (e)"))
                        {
                            log("equipping diamond bolts");
                            inventory.getItem("Diamond dragon bolts (e)").interact();
                            attacking = false;
                        }
                    }
                }
            }
            if (!groundItems.getAll().isEmpty() &&  npcs.closest(8059) != null && npcs.closest(8058) == null && !inventory.isFull())
            {
                log("looting");
                List<GroundItem> loot = groundItems.getAll();
                //while ((loot = groundItems.getAll()).size() > 0 && !inventory.isFull())
                while (!inventory.isFull() && loot.size() > 0)
                {
                    mouse.scrollUp();
                    groundItems.closest(loot.get(0).getName()).interact("Take");
                    long amountOnGround = inventory.getAmount(loot.get(0).getName());
                    String itemName = loot.get(0).getName();
                    Sleep.sleepUntil(() -> amountOnGround < inventory.getAmount(itemName), 3000);
                    loot = groundItems.getAll();
                }
                attacking = false;
            }
            else if (npcs.closest(8061) == null && bot.getTimeUntilNextBreak() < 360)
            {
                log("less than 6 mins till break and no active vork go to bank and afk.");
                if (npcs.closest(8059) != null && objects.closest(new Area(2099,3920,2099,3920),16700 ) == null && objects.closest(4525) == null)
                {
                    magic.castSpell(Spells.NormalSpells.TELEPORT_TO_HOUSE);
                    Sleep.sleepUntil(() -> objects.closest(4525) != null, 6000);
                }
                if  (objects.closest(4525) != null )
                {
                    if (objects.closest(29339) != null)
                    {
                        objects.closest(29339).interact("Enter");
                        Sleep.sleepUntil(() -> objects.closest(29339) == null, 3000);
                    }
                }
                if (objects.closest(new Area(2099,3920,2099,3920),16700 ) != null)
                {
                    if (!bank.isOpen())
                    {
                        objects.closest(new Area(2099,3920,2099,3920),16700 ).interact("Bank");
                    }
                }
                if (prayer.isQuickPrayerActive())
                {
                    prayer.setQuickPrayer(false);
                }
                return 5000;
            }
            else if (npcs.closest(8061) == null && prayer.isQuickPrayerActive())
            {
                log("turning off prayer");
                prayer.setQuickPrayer(false);
            }
            else if (!prayer.isQuickPrayerActive() && bomb.size() == 0 && npcs.closest(8061) != null)
            {
                log("turning on prayer");
                prayer.setQuickPrayer(true);
            }
            else if (skills.getDynamic(Skill.HITPOINTS) <= 39 && !poisonPhase && bomb.size() == 0)
            {
                if (System.currentTimeMillis()-lastEat > 1800)
                {
                    log("Eating");
                    setFastMouse();
                    if (inventory.contains(391) && bomb.size() == 0)
                    {
                        inventory.getItem(391).interact("Eat");
                    }
                    if (inventory.contains("Cooked karambwan") && bomb.size() == 0)
                    {
                        inventory.getItem("Cooked karambwan").interact("Eat");
                    }
                    setSlowMouse();
                    if (!inventory.contains(391, 3144) && npcs.closest("Vorkath") != null)
                    {
                        magic.castSpell(Spells.NormalSpells.TELEPORT_TO_HOUSE);
                    }
                    lastEat = System.currentTimeMillis();
                    attacking = false;
                }
            }
            else if (skills.getDynamic(Skill.PRAYER) <= 40 && !poisonPhase && bomb.size() == 0)
            {
                if (System.currentTimeMillis()-lastEat > 1800)
                {
                    log("Drinking prayer");
                    if (inventory.contains("Prayer potion(1)"))
                    {
                        inventory.getItem("Prayer potion(1)").interact("Drink");
                    }
                    else if (inventory.contains("Prayer potion(2)"))
                    {
                        inventory.getItem("Prayer potion(2)").interact("Drink");
                    }
                    else if (inventory.contains("Prayer potion(3)"))
                    {
                        inventory.getItem("Prayer potion(3)").interact("Drink");
                    }
                    else if (inventory.contains("Prayer potion(4)"))
                    {
                        inventory.getItem("Prayer potion(4)").interact("Drink");
                    }
                    lastEat = System.currentTimeMillis();
                    attacking = false;
                }
            }
            else if (skills.getDynamic(Skill.RANGED) <= skills.getStatic(Skill.RANGED) && !poisonPhase &&
                    bomb.size() == 0 && npcs.closest("Vorkath") != null)
            {
                if (System.currentTimeMillis()-lastEat > 1800)
                {
                    log("Drinking ranging");
                    if (inventory.contains("Divine ranging potion(1)"))
                    {
                        inventory.getItem("Divine ranging potion(1)").interact("Drink");
                    }
                    else if (inventory.contains("Divine ranging potion(2)"))
                    {
                        inventory.getItem("Divine ranging potion(2)").interact("Drink");
                    }
                    else if (inventory.contains("Divine ranging potion(3)"))
                    {
                        inventory.getItem("Divine ranging potion(3)").interact("Drink");
                    }
                    else if (inventory.contains("Divine ranging potion(4)"))
                    {
                        inventory.getItem("Divine ranging potion(4)").interact("Drink");
                    }
                    lastEat = System.currentTimeMillis();
                    attacking = false;
                }
            }
            else if (configs.get(102) >= -38 && !poisonPhase && bomb.size() == 0 && npcs.closest("Vorkath") != null)
            {
                if (System.currentTimeMillis()-lastEat > 1800)
                {
                    log("drinking anti-venom");
                    if (inventory.contains("Anti-venom+(1)"))
                    {
                        inventory.getItem("Anti-venom+(1)").interact("Drink");
                    }
                    else if (inventory.contains("Anti-venom+(2)"))
                    {
                        inventory.getItem("Anti-venom+(2)").interact("Drink");
                    }
                    else if (inventory.contains("Anti-venom+(3)"))
                    {
                        inventory.getItem("Anti-venom+(3)").interact("Drink");
                    }
                    else if (inventory.contains("Anti-venom+(4)"))
                    {
                        inventory.getItem("Anti-venom+(4)").interact("Drink");
                    }
                    attacking = false;
                    lastEat = System.currentTimeMillis();
                }
            }
            else if (configs.get(277) == 0 && !poisonPhase && bomb.size() == 0 && npcs.closest("Vorkath") != null)
            {
                if (System.currentTimeMillis()-lastEat > 1800)
                {
                    log("Drinking antifire");
                    if (inventory.contains("Extended super antifire(1)"))
                    {
                        inventory.getItem("Extended super antifire(1)").interact("Drink");
                    }
                    else if (inventory.contains("Extended super antifire(2)"))
                    {
                        inventory.getItem("Extended super antifire(2)").interact("Drink");
                    }
                    else if (inventory.contains("Extended super antifire(3)"))
                    {
                        inventory.getItem("Extended super antifire(3)").interact("Drink");
                    }
                    else if (inventory.contains("Extended super antifire(4)"))
                    {
                        inventory.getItem("Extended super antifire(4)").interact("Drink");
                    }
                    lastEat = System.currentTimeMillis();
                    attacking = false;
                }
            }
            else if (npcs.closest(8059) != null && bomb.size() == 0)
            {
                log("Poking the bear");
                if (inventory.getAmount(391) > 7)
                {
                    if (npcs.closest(8059).hasAction("Poke"))
                    {
                        if (npcs.closest(8059).interact("Poke"))
                        {
                            prayer.setQuickPrayer(true);
                            Sleep.sleepUntil(() -> npcs.closest(8059).isAnimating(), 3000);
                            log("running away");
                            myWalkingEvents3(new Position(myPosition().getX(), myPosition().getY()-6, 0));
                        }
                    }
                }
                else
                {
                    magic.castSpell(Spells.NormalSpells.TELEPORT_TO_HOUSE);
                    Sleep.sleepUntil(() -> objects.closest(4525) != null, 4000);
                }
                attacking = false;
            }
        }
        setVariables();
        if ((myPosition().equals(safeSpotStart) || myPosition().equals(nearSpotStart1) || myPosition().equals(nearSpotStart2) ||
                myPosition().equals(nearSpotStart3) || myPosition().equals(nearSpotStart4)) && poisonPhase && bomb.size() == 0 && vorkath != null)
        {
            log("moving to end posion phase");
            setFastMouse();
            safeSpotEnd.hover(getBot());
            mouse.click(true);
            if (menu.isOpen())
            {
                if (menu.selectAction("Walk here"))
                {
                    if (skills.getDynamic(Skill.HITPOINTS) <= 39 && System.currentTimeMillis()-lastEat > 1800)
                    {
                        if (inventory.contains(391))
                        {
                            inventory.getItem(391).interact("Eat");
                        }
                        if (inventory.contains("Cooked karambwan"))
                        {
                            inventory.getItem("Cooked karambwan").interact("Eat");
                        }
                        if (!inventory.contains(391, 3144))
                        {
                            magic.castSpell(Spells.NormalSpells.TELEPORT_TO_HOUSE);
                        }
                        lastEat = System.currentTimeMillis();
                        attacking = false;
                    }
                    else if (skills.getDynamic(Skill.HITPOINTS) <= skills.getStatic(Skill.HITPOINTS)-23 && System.currentTimeMillis()-lastEat > 1800)
                    {
                        if (inventory.contains(391))
                        {
                            inventory.getItem(391).interact("Eat");
                            lastEat = System.currentTimeMillis();
                            attacking = false;
                        }
                    }
                    Sleep.sleepUntil(() -> myPosition().getX()-1 <= safeSpotEnd.getX() || objects.closest(32000) == null || bomb.size() > 0, 2000);
                    attacking = false;
                }
                setSlowMouse();
            }
            attacking = false;
        }
        else if (!myPosition().equals(safeSpotStart) && poisonPhase && bomb.size() == 0 && vorkath != null && myPosition().getY() != safeSpotStart.getY())
        {
            log("moving to start posion phase");
            setFastMouse();
            if (settings.isRunning())
            {
                settings.setRunning(false);
            }
            safeSpotStart.hover(getBot());
            mouse.click(true);
            if (menu.isOpen())
            {
                if (menu.selectAction("Walk here"))
                {
                    if (skills.getDynamic(Skill.HITPOINTS) <= 39 && System.currentTimeMillis()-lastEat > 1800)
                    {
                        if (inventory.contains(391))
                        {
                            inventory.getItem(391).interact("Eat");
                        }
                        if (inventory.contains("Cooked karambwan"))
                        {
                            inventory.getItem("Cooked karambwan").interact("Eat");
                        }
                        if (!inventory.contains(391, 3144))
                        {
                            magic.castSpell(Spells.NormalSpells.TELEPORT_TO_HOUSE);
                        }
                        lastEat = System.currentTimeMillis();
                        attacking = false;
                    }
                    else if (skills.getDynamic(Skill.HITPOINTS) <= skills.getStatic(Skill.HITPOINTS)-23 && System.currentTimeMillis()-lastEat > 1800)
                    {
                        if (inventory.contains(391))
                        {
                            inventory.getItem(391).interact("Eat");
                            lastEat = System.currentTimeMillis();
                            attacking = false;
                        }
                    }
                    Sleep.sleepUntil(() -> myPosition().getX() >= safeSpotStart.getX()-1 && myPosition().getY() <= safeSpotStart.getY()+1 || objects.closest(32000) == null || bomb.size() > 0, 2500);
                    //Sleep.sleepUntil(() -> myPosition().getX() >= safeSpotStart.getX()-1  || objects.closest(32000) == null, 2500);
                    setSlowMouse();
                    attacking = false;
                }
            }
            attacking = false;
        }
        else if (!myPosition().equals(safeSpotStart) && poisonPhase && bomb.size() == 0 && vorkath != null)
        {
            log("moving to start posion phase");
            setFastMouse();
            if (settings.isRunning())
            {
                settings.setRunning(false);
            }
            safeSpotStart.hover(getBot());
            mouse.click(true);
            if (menu.isOpen())
            {
                if (menu.selectAction("Walk here"))
                {
                    if (skills.getDynamic(Skill.HITPOINTS) <= 39 && System.currentTimeMillis()-lastEat > 1800)
                    {
                        if (inventory.contains(391))
                        {
                            inventory.getItem(391).interact("Eat");
                        }
                        if (inventory.contains("Cooked karambwan"))
                        {
                            inventory.getItem("Cooked karambwan").interact("Eat");
                        }
                        if (!inventory.contains(391, 3144))
                        {
                            magic.castSpell(Spells.NormalSpells.TELEPORT_TO_HOUSE);
                        }
                        lastEat = System.currentTimeMillis();
                        attacking = false;
                    }
                    else if (skills.getDynamic(Skill.HITPOINTS) <= skills.getStatic(Skill.HITPOINTS)-23 && System.currentTimeMillis()-lastEat > 1800)
                    {
                        if (inventory.contains(391))
                        {
                            inventory.getItem(391).interact("Eat");
                            lastEat = System.currentTimeMillis();
                            attacking = false;
                        }
                    }
                    //Sleep.sleepUntil(() -> myPosition().getX() >= safeSpotStart.getX() && myPosition().getY() <= safeSpotStart.getY() || objects.closest(32000) == null, 2500);
                    Sleep.sleepUntil(() -> myPosition().getX() >= safeSpotStart.getX()-1  || objects.closest(32000) == null || bomb.size() > 0, 2500);
                    setSlowMouse();
                    attacking = false;
                }
            }
            attacking = false;
        }
        else if (icePhase && !stopAttacking && bomb.size() == 0 && vorkath != null)
        {
            log("ice phase stop attacking");
            setFastMouse();
            myPosition().hover(getBot());
            mouse.click(true);
            if (menu.isOpen())
            {
                menu.selectAction("Walk here");
            }
            else
            {
             return 0;
            }
            setSlowMouse();
            stopAttacking = true;
            attacking = false;
        }
        else if (icePhase && stopAttacking && bomb.size() == 0 && vorkath != null)
        {
            log("ice phase crumble undead on spawn.");
            setFastMouse();
            if (camera.getYawAngle() != 0)
            {
                camera.moveYaw(0);
            }
            if (camera.getPitchAngle() <= 65)
            {
                camera.movePitch(65);
            }
            if (npcs.closest("Zombified spawn") != null)
            {
                if (npcs.closest("Zombified spawn").hasAction("Attack"))
                {
                    if (npcs.closest("Zombified spawn").getPosition().getY() > myPosition().getY())
                    {

                    }
                    int magicxp = skills.getExperience(Skill.MAGIC);
                    if (magic.castSpellOnEntity(Spells.NormalSpells.CRUMBLE_UNDEAD, npcs.closest("Zombified spawn")))
                    {
                        log("casted crumble");
                        stopAttacking = false;
                        icePhase = false;
                        Sleep.sleepUntil(() -> skills.getExperience(Skill.MAGIC) > magicxp, 3000);
                        if (skills.getDynamic(Skill.HITPOINTS) <= 39 && System.currentTimeMillis()-lastEat > 1800)
                        {
                            if (inventory.contains(391))
                            {
                                inventory.getItem(391).interact("Eat");
                            }
                            if (inventory.contains("Cooked karambwan"))
                            {
                                inventory.getItem("Cooked karambwan").interact("Eat");
                            }
                            if (!inventory.contains(391, 3144))
                            {
                                magic.castSpell(Spells.NormalSpells.TELEPORT_TO_HOUSE);
                            }
                            lastEat = System.currentTimeMillis();
                            attacking = false;
                        }
                        else if (skills.getDynamic(Skill.HITPOINTS) <= skills.getStatic(Skill.HITPOINTS)-23 && System.currentTimeMillis()-lastEat > 1800)
                        {
                            if (inventory.contains(391))
                            {
                                inventory.getItem(391).interact("Eat");
                                lastEat = System.currentTimeMillis();
                                attacking = false;
                            }
                        }
                        tabs.open(Tab.INVENTORY);
                    }
                }
            }
            attacking = false;

        }
        else if (!poisonPhase && !icePhase && !attacking && bomb.size() == 0 && npcs.closest(8061) != null)
        {
            log("attack boss");
            if (vorkath != null)
            {
                if (vorkath.hasAction("Attack"))
                {
                    if (vorkath.interact("Attack"))
                    {
                        attacking = true;
                    }
                }
            }
            /* can readd this later if still moving too slow from bomb.
            if (bomb.size() > 0)
            {
                //moveFromBomb();
                Position moveFromBomb = new Position(myPosition().getX()+3, myPosition().getY(), 0);
                setFastMouse();
                moveFromBomb.hover(getBot());
                mouse.click(false);
                setSlowMouse();
            }

             */
        }
        //enter lunar portal at house.
        else if (objects.closest(4525) != null )
        {
            log("entering lunar portal.");
            if (objects.closest(29339) != null)
            {
                objects.closest(29339).interact("Enter");
                Sleep.sleepUntil(() -> objects.closest(29339) == null, 3000);
            }
        }
        else if (objects.closest(new Area(2099,3920,2099,3920),16700 ) != null && !inventoryReady())
        {
            log("resupplying");
            if (!bank.isOpen())
            {
                if (inventory.contains(21944))
                {
                   inventory.getItem(21944).interact();
                }
                objects.closest(new Area(2099,3920,2099,3920),16700 ).interact("Bank");
                Sleep.sleepUntil(() -> bank.isOpen(), 5000);
            }
            if (bank.isOpen() && !inventoryReady())
            {
                bank.depositAllExcept(23733,22209,12791,2434, 21946, 391, 3144, 21946, 21944);
                sleep(1200);
                long prayer = inventory.getAmount(2434);
                long manta = inventory.getAmount(391);
                long kara = inventory.getAmount(3144);
                long venom = inventory.getAmount(12913);
                long fire = inventory.getAmount(22209);
                long ranging = inventory.getAmount(23733);
                banking((int)prayer, 4, 2434);
                banking((int)venom, 1, 12913);
                banking((int)fire, 1, 22209);
                banking((int)ranging, 1, 23733);
                banking((int)manta, 11, 391);
                banking((int)kara, 8, 3144);
            }

        }
        else if (objects.closest(new Area(2098,3920,2098,3920),16700 ) != null && inventoryReady())
        {
            if (camera.getYawAngle() != 0)
            {
                camera.moveYaw(0);
            }
            log("inventory ready go back to vork");
            if (bank.isOpen())
            {
                bank.close();
                Sleep.sleepUntil(() -> !bank.isOpen(), 1000);
            }
            else
            {
                if (objects.closest(new Area(2098,3920,2097,3920),16700 ).interact())
                {
                    Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 3000);
                    if (dialogues.isPendingContinuation())
                    {
                        dialogues.completeDialogue();
                        sleep(random(1800,2400));
                    }
                }
            }

        }
        else if (relleka.contains(myPosition()))
        {
            log("Go to dock");
            walking.webWalk(new Position(2640,3695, 0));
            if (npcs.closest(7504) != null)
            {
                npcs.closest(7504).interact("Ungael");
                Sleep.sleepUntil(() -> vorkIsland.contains(myPosition()), 7000);
            }
        }
        else if (vorkIsland.contains(myPosition()))
        {
            log("go to ice to fight");
            walking.webWalk(new Position(2272,4052,0));
            Sleep.sleepUntil(() -> objects.closest(31990) != null, 5000);
            if (objects.closest(31990) != null)
            {
                objects.closest(31990).interact("Climb-over");
                sleep(3000);
            }
        }
        if (objects.closest(27785) != null)
        {
            magic.castSpell(Spells.NormalSpells.TELEPORT_TO_HOUSE);
            sleep(5000);
        }
        if (bomb.size() != 0)
        {
            return 0;
        }
        return 200;
    }


    private void moveFromBomb()
    {
        if (bomb.size() > 0 && !bombMoved && myPosition().getX()+2 <= vorkath.getPosition().getX()+6)
        {
            log("move from bomb.");
            myWalkingEvents(new Position(myPosition().getX()+2, myPosition().getY(),0));
            bombMoved = true;
            attacking = false;
        }
        else if (bomb.size() > 0 && !bombMoved && myPosition().getX()+2 > vorkath.getPosition().getX()+6)
        {
            log("move from bomb.");
            myWalkingEvents(new Position(myPosition().getX()-2, myPosition().getY(),0));
            bombMoved = true;
            attacking = false;
        }
        else if (bomb.size() == 0)
        {
            bombMoved = false;
        }
    }
    private boolean hasEquipment()
    {
        if (equipment.contains("Guthix coif") && equipment.contains("Ava's assembler") && equipment.contains("Salve amulet(ei)") &&
                equipment.contains("Guthix d'hide body") && equipment.contains("Guthix chaps") && equipment.contains("Dragonfire ward")
                && equipment.contains("Dragon hunter crossbow") && equipment.contains("Archers ring") && equipment.contains("Barrows gloves")
                && equipment.contains("Guthix d'hide boots"))
        {
            return true;
        }
        else
        {
            needItems = true;
            return false;
        }
    }
    private void equipItems()
    {
        if (inventory.contains("Guthix coif"))
        {
            inventory.getItem("Guthix coif").interact();
        }
        if (inventory.contains("Ava's assembler"))
        {
            inventory.getItem("Ava's assembler").interact();
        }
        if (inventory.contains("Salve amulet(ei)"))
        {
            inventory.getItem("Salve amulet(ei)").interact();
        }
        if (inventory.contains("Guthix d'hide body"))
        {
            inventory.getItem("Guthix d'hide body").interact();
        }
        if (inventory.contains("Guthix chaps"))
        {
            inventory.getItem("Guthix chaps").interact();
        }
        if (inventory.contains("Dragonfire ward"))
        {
            inventory.getItem("Dragonfire ward").interact();
        }
        if (inventory.contains("Dragon hunter crossbow"))
        {
            inventory.getItem("Dragon hunter crossbow").interact();
        }
        if (inventory.contains("Archers ring"))
        {
            inventory.getItem("Archers ring").interact();
        }
        if (inventory.contains("Barrows gloves"))
        {
            inventory.getItem("Barrows gloves").interact();
        }
        if (inventory.contains("Guthix d'hide boots"))
        {
            inventory.getItem("Guthix d'hide boots").interact();
        }
        if (inventory.contains(21944))
        {
            inventory.getItem(21944).interact();
        }
    }

    public void banking(int amount, int amountNeeded, int id)
    {
        if (amount < amountNeeded)
        {
            bank.withdraw(id, amountNeeded-amount);
        }
    }
    public boolean inventoryReady()
    {
        if (inventory.getAmount("Manta ray") == 11 && inventory.getAmount("Cooked karambwan") == 8 && inventory.getAmount("Prayer potion(4)") == 4
            && inventory.getAmount(23733) == 1 && inventory.getAmount(22209) == 1 && inventory.getAmount(12913) == 1 && inventory.contains(12791))
        {
            return true;
        }
        else return false;
    }
    public void setVariables()
    {
        if (npcs.closest("Vorkath") != null)
        {
            vorkath = npcs.closest("Vorkath");
        }
        if (vorkath != null)
        {
            vorkPosition = vorkath.getPosition();
            safeSpotStart = new Position(vorkPosition.getX()+6, vorkPosition.getY()-8,0);
            nearSpotStart1 = new Position(safeSpotStart.getX()+1, safeSpotStart.getY()+1,0);
            nearSpotStart2 = new Position(safeSpotStart.getX(), safeSpotStart.getY()+1,0);
            nearSpotStart3 = new Position(safeSpotStart.getX()-1, safeSpotStart.getY()+1,0);
            nearSpotStart4 = new Position(safeSpotStart.getX()-1, safeSpotStart.getY(),0);
            safeSpotEnd = new Position(vorkPosition.getX(), vorkPosition.getY()-8,0);
        }

        if (iceProjectiles.size() > 0)
        {
            icephaseTimer = System.currentTimeMillis();
            icePhase = true;
            spawnSeen = false;
        }
        if (bomb.size() > 0 && !bombMoved && myPosition().getX()+2 <= vorkath.getPosition().getX()+6)
        {
            log("move from bomb2.");
            Position moveFromBomb = new Position(myPosition().getX()+3, myPosition().getY(), 0);
            setFastMouse();
            moveFromBomb.hover(getBot());
            mouse.click(true);
            if (menu.isOpen())
            {
                if (menu.selectAction("Walk here"))
                {
                    setSlowMouse();
                    //myWalkingEvents(new Position(myPosition().getX()+2, myPosition().getY(),0));
                    bombMoved = true;
                    attacking = false;
                }
                else
                {
                    setVariables();
                }
            }
            /*
            setSlowMouse();
            //myWalkingEvents(new Position(myPosition().getX()+2, myPosition().getY(),0));
            bombMoved = true;
            attacking = false;
            */

        }
        else if (bomb.size() > 0 && !bombMoved && myPosition().getX()+2 > vorkath.getPosition().getX()+6)
        {
            log("move from bomb3.");
            //myWalkingEvents(new Position(myPosition().getX()-2, myPosition().getY(),0));
            Position moveFromBomb = new Position(myPosition().getX()-3, myPosition().getY(), 0);
            setFastMouse();
            moveFromBomb.hover(getBot());
            mouse.click(true);
            if (menu.isOpen())
            {
                if (menu.selectAction("Walk here"))
                {
                    setSlowMouse();
                    //myWalkingEvents(new Position(myPosition().getX()+2, myPosition().getY(),0));
                    bombMoved = true;
                    attacking = false;
                }
                else
                {
                    setVariables();
                }
            }
            /*
            setSlowMouse();
            //myWalkingEvents(new Positio
            bombMoved = true;
            attacking = false;

             */
        }
        else if (bomb.size() == 0)
        {
            bombMoved = false;
        }
        if (!poisonPhase)
        {
            if (!settings.isRunning())
            {
                settings.setRunning(true);
            }
        }
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

    public void myWalkingEvents (Position walkpos)
    {
        Position currentPos = myPosition();
        WalkingEvent myEvent = new WalkingEvent(walkpos);
        myEvent.setMinDistanceThreshold(0);
        myEvent.setHighBreakPriority(true);
        myEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate()
            {
                if (currentPos.getX()-2 == myPosition().getX() || currentPos.getX()+2 == myPosition().getX())
                {
                    return true;
                }
                if (bomb.size() == 0)
                {
                    return true;
                }
                else if (myPosition().equals(walkpos)) return true;
                else return false;
            }
        });
        execute(myEvent);
    }

    public void myWalkingEvents2 (Position walkpos)
    {
        WalkingEvent myEvent = new WalkingEvent(walkpos);
        myEvent.setMinDistanceThreshold(0);
        myEvent.setHighBreakPriority(true);
        myEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                if (myPosition().equals(walkpos)) return true;
                else return false;
            }
        });
        execute(myEvent);
    }

    public void myWalkingEvents3 (Position walkpos)
    {
        Position currentPos = myPosition();
        WalkingEvent myEvent = new WalkingEvent(walkpos);
        myEvent.setMinDistanceThreshold(0);
        myEvent.setHighBreakPriority(true);
        myEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate()
            {
                if (npcs.closest(8061) != null)
                {
                    if (npcs.closest(8061).isAnimating())
                    {
                        return true;
                    }
                    else
                        return false;
                }
                return false;
            }
        });
        execute(myEvent);
    }

}