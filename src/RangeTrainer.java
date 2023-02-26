import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import util.BetterWalk;
import util.Sleep;
import util.UsefulAreas;


@ScriptManifest(name = "RangeTrainer", author = "Iownreality1", info = "Train ranged", version = 0.1, logo = "")
public class RangeTrainer extends Script {
    BetterWalk reaggro_walk = new BetterWalk(this);
    final Filter<Item> skillsNecklaceFilter = item -> item.getName().matches("Skills necklace\\(\\d\\)");
    final Filter<Item> GloryFilter = item -> item.getName().matches("Amulet of glory\\(\\d\\)");
    int iron_knives;
    int black_knives;
    boolean has_teleport;
    boolean supplied = false;
    int iron_needed = 0;
    int black_needed = 0;
    int lobsters = 0;
    int lobsters_needed = 0;
    Area draynor = new Area(3087, 3234, 3111, 3260);
    Area VeosKourend = new Area(1800, 3680, 1850, 3700);
    Area woodcutting = new Area(1650, 3500, 1700, 3550);
    Position reaggro = new Position(1742, 3509, 0);
    Position fightCrab = new Position(1698, 3486, 0);
    Area crabArea = new Area(1680, 3450, 1750, 3525);
    Area aroundTargetCrab = new Area (1696, 3485, 1698, 3487);

    @Override
    public int onLoop() throws InterruptedException
    {
        if (skills.getStatic(Skill.RANGED) >= 25)
        {
            walking.webWalk(reaggro); // move to safety
            magic.castSpell(Spells.NormalSpells.HOME_TELEPORT);
            stop();
        }
        if (crabArea.contains(myPosition()))
        {
            if (getSkills().getDynamic(Skill.HITPOINTS) < 10)
            {
                if (inventory.contains(379))
                {
                    inventory.getItem(379).interact();
                    sleep(random(600, 800));
                    if (!inventory.contains(379))
                    {
                        bank();
                        walking.webWalk(aroundTargetCrab);
                    }
                }
                else
                {
                    walking.webWalk(reaggro);
                    supplied = false;
                    return random(600, 800);
                }
            }
            if (getSkills().getStatic(Skill.RANGED) >= 10)
            {
                if (inventory.contains("Black knife"))
                {
                    inventory.getItem("Black knife").interact();
                }
                else if (!equipment.contains("Black knife"))
                {
                    supplied = false;
                    supply();
                }
            }
            if (!getCombat().isFighting())  //reaggro
            {
                reaggro_walk.MyWalkingEvent(fightCrab);
                sleep(random(600, 800));
                if (!(getCombat().isFighting()) && npcs.closest(aroundTargetCrab, 7207) != null)
                {
                    sleep(random(1200, 1800));
                    if (!(getCombat().isFighting()) && npcs.closest(aroundTargetCrab,  7207) != null)
                    // above makes sure the crab stays asleep for multiple ticks while near it
                    //TODO: Make this more elegant.
                    {
                        reaggro_walk.MyWalkingEvent(reaggro);
                        sleep(random(600, 800));
                        reaggro_walk.MyWalkingEvent(fightCrab);
                    }
                }
            }
        }
        else if (!supplied)
        {
            supplied = supply();
            return random(600, 800);
        }
        else if (UsefulAreas.GeArea.contains(myPosition()))
        {
            inventory.getItem(skillsNecklaceFilter).interact("Rub");
            Sleep.sleepUntil(() -> widgets.get(187, 3, 4) != null, 2400);
            if (widgets.get(187, 3, 4) != null)
            {
                widgets.interact(187, 3, 4, "Continue");
                keyboard.pressKey(53);
                sleep(random(4200, 4800));
                if (UsefulAreas.GeArea.contains(myPosition()))
                {
                    buy_glory();
                }
            }
        }

        else if (VeosKourend.contains(myPosition()))
        {
            inventory.getItem(skillsNecklaceFilter).interact("Rub");
            Sleep.sleepUntil(() -> widgets.get(187, 3, 4) != null, 2400);
            if (widgets.get(187, 3, 4) != null)
            {
                widgets.interact(187, 3, 4, "Continue");
                sleep(random(4200, 4800));
            }
        }
        else if (woodcutting.contains(myPosition()))
        {
            if (!equipment.contains("Iron knife") && skills.getStatic(Skill.RANGED) < 10)
            {
                inventory.getItem("Iron knife").interact();
            }
            else if (!equipment.contains("Black knife") && skills.getStatic(Skill.RANGED) >= 10)
            {
                inventory.getItem("Black knife").interact();
            }
            Position crabs = new Position(1685, 3478, 0);
            walking.webWalk(crabs);
        }
        else
        {
            supplied = false;
            magic.castSpell(Spells.NormalSpells.HOME_TELEPORT);
        }

        return random(600, 800);
    }
    public void bank() throws InterruptedException
    {
        reaggro_walk.MyWalkingEvent(1718,3466,0); //sand crabs bank location
        objects.closest("Bank chest").interact();
        Sleep.sleepUntil(() -> bank.isOpen(), 2400);
        if (bank.isOpen())
        {
            if (bank.contains(379))
            {
                bank.withdrawAll(379);
            }
            else
            {
                supplied = false;
                supply();

            }
        }
    }
    public void buy_glory() throws InterruptedException
    {
        boolean has_glory = false;
        npcs.closest("Banker").interact("Bank");
        Sleep.sleepUntil(() -> bank.isOpen(), 2400);
        if (bank.isOpen())
        {
            sleep(random(600, 800));
            bank.depositAll(380);
            if (bank.contains(GloryFilter))
            {
                bank.withdraw(GloryFilter, 1);
                has_glory = true;
            }
            sleep(random(600, 800));
            bank.close();
            sleep(random(600, 800));
        }
        if (!has_glory)
        {
            npcs.closest("Grand Exchange Clerk").interact("Exchange");
            Sleep.sleepUntil(() -> grandExchange.isOpen(), 2400);
            sleep(random(600, 800));
            if (grandExchange.isOpen()) {
                grandExchange.buyItem(11978, "Amulet of Glory", 20000, 1);
                sleep(random(600, 800));
                grandExchange.collect();
                sleep(random(600, 800));
                grandExchange.close();
                sleep(random(600, 800));
            }
        }
        inventory.getItem(GloryFilter).interact("Rub");
        Sleep.sleepUntil(() -> widgets.get(219, 1, 3) != null, 2400);
        if (widgets.get(219, 1, 3) != null)
        {
            widgets.interact(219, 1, 3, "Continue");
            Sleep.sleepUntil(() -> draynor.contains(myPosition()), 2400);
        }
        if (draynor.contains(myPosition()))
        {
            Position veos = new Position(3054, 3246, 0);
            walking.webWalk(veos);
            sleep(random(600, 800));
        }
        npcs.closest("Veos").interact("Talk-to");
        Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 2400);
        if (dialogues.isPendingContinuation())
        {
            dialogues.completeDialogue("That's great, can you take me there please?");
        }


    }
    public boolean supply() throws InterruptedException
    {
        if ((inventory.contains(863) || equipment.contains(863)) && //if inventory contains iron knives
                (inventory.contains(869) || equipment.contains(869)) &&  // and it contains black knives
                (inventory.contains(379)) && // and it contains lobsters
                (inventory.contains(skillsNecklaceFilter))) // and it contains a skills necklace
        {
            return true;
        }
        boolean have_food;
        if (skills.getStatic(Skill.RANGED) >= 5)
        {
            return true;
        }
        boolean checked_bank = false;
        walking.webWalk(UsefulAreas.GeArea);
        npcs.closest("Banker").interact("Bank");
        Sleep.sleepUntil(() -> bank.isOpen(), 2400);
        if (bank.isOpen())
        {
            bank.depositAll();
            sleep(random(600, 800));
            bank.depositWornItems();
            sleep(random(600,800));
            bank.withdrawAll("Coins");
            sleep(random(600,800));
            if (bank.contains(skillsNecklaceFilter))
            {
                has_teleport = true;
                bank.withdraw(skillsNecklaceFilter, 1);
                sleep(random(600,800));
            }
            if (bank.contains("Iron knife"))
            {
                iron_knives = (int) bank.getAmount("Iron knife");
                bank.withdrawAll("Iron knife");
                sleep(random(600,800));
            }
            if (bank.contains("Black knife"))
            {
                black_knives = (int) bank.getAmount("Black knife");
                bank.withdrawAll("Black knife");
                sleep(random(600,800));
            }
            if (bank.contains("Lobster"))
            {
                lobsters = (int) bank.getAmount("Lobster");
                bank.enableMode(Bank.BankMode.WITHDRAW_NOTE);
                sleep(random(600,800));
                bank.withdrawAll("Lobster");
                sleep(random(600,800));
            }
            bank.close();
            checked_bank = true;
            sleep(random(600, 800));
        }
        if (checked_bank)
        {
            if (iron_knives < 2000 && skills.getStatic(Skill.RANGED) < 10)
            {
                iron_needed = 2000 - iron_knives;
            }
            if (black_knives < 3000 && skills.getStatic(Skill.RANGED) < 25)
            {
                black_needed = 3000 - black_knives;
            }
            if (lobsters < 100)
            {
                lobsters_needed = 100 - lobsters;
            }
            if (iron_needed == 0 && black_needed == 0 && has_teleport && lobsters_needed == 0)
            {
                inventory.getItem("Lobster").interact();
                sleep(random(600, 800));
                npcs.closest("Banker").interact();
                Sleep.sleepUntil(() -> widgets.get(219, 1, 1) != null, 2400);
                if (widgets.get(219, 1, 1) != null)
                {
                    widgets.interact(219, 1, 1, "Continue");
                    sleep(random(600 ,800));
                }
                return true;
            }
        }
        else
        {
            return false;
        }
        npcs.closest("Grand Exchange Clerk").interact("Exchange");
        Sleep.sleepUntil(() ->grandExchange.isOpen(), 2400);
        sleep(random(600, 800));
        if (grandExchange.isOpen())
        {
            if (iron_needed > 0)
            {
                grandExchange.buyItem(863, "Iron knife", 100, iron_needed);
                sleep(random(600, 800));
            }
            if (black_needed > 0)
            {
                grandExchange.buyItem(869, "Black knife", 100, black_needed);
                sleep(random(600, 800));
            }
            if (!has_teleport)
            {
                grandExchange.buyItem(11968, "Skills necklace", 20000, 1);
                sleep(random(600, 800));
            }
            if (lobsters_needed > 0)
            {
                grandExchange.buyItem(379 , "Lobster", 200, lobsters_needed);
                sleep(random(600, 800));
            }
            sleep(random(600, 800));
            grandExchange.collect();
            sleep(random(1200, 1800));
            grandExchange.close();
            sleep(random(600, 800));
            inventory.getItem("Lobster").interact();
            sleep(random(600, 800));
            npcs.closest("Banker").interact();
            have_food = (inventory.getItem("Lobster").getAmount() >= 100);
            Sleep.sleepUntil(() -> widgets.get(219, 1, 1) != null, 2400);

            if (widgets.get(219, 1, 1) != null)
            {
                widgets.interact(219, 1, 1, "Continue");
                sleep(random(600 ,800));
            }
            return (inventory.getItem("Iron knife").getAmount() >= 2000 &&
                    inventory.getItem("Black knife").getAmount() >= 3000 &&
                    inventory.contains(skillsNecklaceFilter) &&
                    have_food);
        }
        return false;
    }

}