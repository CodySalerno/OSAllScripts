import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;
import util.*;

import static java.lang.Thread.sleep;
import static org.osbot.rs07.script.MethodProvider.random;


public class HerbloreTrainer
{

    boolean setFastMouse = false;
    boolean supplied = false;
    private final MethodProvider methods;
    Area camelot = new Area(2740,3420,2860,3482);
    Area catherbyBank = new Area(2806,3438, 2812,3441);
    Area catherbyRange = new Area(2815,3439, 2818,3443);


    public HerbloreTrainer(final MethodProvider methods)
    {
        this.methods = methods;
    }

    public void TrainHerblore(GEHelper geHelp, Supply supply, int goalLevel) throws InterruptedException
    {
        int currentLevel = methods.skills.getStatic(Skill.HERBLORE);
        if (!supplied)
        {
            supplied = herbSupplier(goalLevel, geHelp, supply);
            return;
        }
        if (currentLevel < 12) //attack potions
        {
            methods.log("making attack potions");
            makePotions(91, 221);
        }
        else if (currentLevel < 22) //str potions
        {
            methods.log("making str potions");
            makePotions(95, 225);
        }
        else if (currentLevel < 26) //restore potions
        {
            methods.log("making restore potions");
            makePotions(97, 223);
        }
        else if (currentLevel < 38) //energy potions
        {
            methods.log("making energy potions");
            makePotions(97, 1975);
        }
        else if (currentLevel < 45) //prayer potions
        {
            methods.log("making prayer potions");
            makePotions( 99, 231);
        }
        else if (currentLevel < 52) //super attack potions
        {
            methods.log("making super attack potions");
            makePotions(101, 221);
        }
        else if (currentLevel < 55) //super energy potions
        {
            methods.log("making super energy potions");
            makePotions(103, 2970);
        }
        else if (currentLevel < 66) //super str potions
        {
            methods.log("making super str potions");
            makePotions(105, 225);
        }
        else if (currentLevel < 72) //super def potions
        {
            methods.log("making super def potions");
            makePotions(107, 239);
        }
        else if (currentLevel < 81) //ranging potions
        {
            methods.log("making ranging potions");
            makePotions(109, 245);
        }
        else if (currentLevel < 99) //sara brew potions
        {
            methods.log("making sara bew potions");
            makePotions(3002, 6693);
        }
    }
    private void makePotions(int id1, int id2) throws InterruptedException
    {
        if (methods.inventory.contains(id1) && methods.inventory.contains(id2))
        {
            if (methods.bank.isOpen())
            {
                methods.bank.close();
                Sleep.sleepUntil(() -> !methods.bank.isOpen(), 3000);
            }
            if (!methods.bank.isOpen())
            {

                if (methods.inventory.isItemSelected())
                {
                    if (methods.inventory.getSelectedItemId() == id1)
                    {
                        if (methods.inventory.getItem(id2).interact())
                        {
                            Sleep.sleepUntil(() -> methods.widgets.get(270,14,38)!= null, 3000);
                            if (methods.widgets.get(270,14,38)!= null)
                            {
                                if (methods.widgets.get(270,14,38).interact())
                                {
                                    Sleep.sleepUntil(() -> !methods.inventory.contains(id1), 20000);
                                }
                            }
                        }
                    }
                    else if (methods.inventory.getSelectedItemId() == id2)
                    {
                        if (methods.inventory.getItem(id1).interact())
                        {
                            Sleep.sleepUntil(() -> methods.widgets.get(270,14,38)!= null, 3000);
                            if (methods.widgets.get(270,14,38)!= null)
                            {
                                if (methods.widgets.get(270,14,38).interact())
                                {
                                    Sleep.sleepUntil(() -> !methods.inventory.contains(id1), 20000);
                                }
                            }
                        }
                    }
                }
                else
                {
                    if (methods.inventory.getItem(id1).interact())
                    {
                        if (methods.inventory.getItem(id2).interact())
                        {
                            Sleep.sleepUntil(() -> methods.widgets.get(270,14,38)!= null, 3000);
                            if (methods.widgets.get(270,14,38)!= null)
                            {
                                if (methods.widgets.get(270,14,38).interact())
                                {
                                    Sleep.sleepUntil(() -> !methods.inventory.contains(id1), 20000);
                                }
                            }
                        }
                    }
                }
            }
        }
        else
        {
            if (methods.bank.isOpen())
            {
                if (!methods.inventory.isEmpty())
                {
                    methods.bank.depositAll();
                }
                else
                {
                    methods.bank.withdraw(id1, 14);
                    methods.bank.withdraw(id2, 14);
                    Sleep.sleepUntil(() -> methods.inventory.contains(id1) && methods.inventory.contains(id2), 3000);
                }
                if (methods.inventory.contains(id1) && methods.inventory.contains(id2))
                {
                    methods.bank.close();
                }
            }
            else
            {
                methods.npcs.closest("Banker").interact("Bank");
                Sleep.sleepUntil(() -> methods.bank.isOpen(), 3000);
            }
        }

    }


    private boolean herbSupplier(int goalLevel, GEHelper geHelp, Supply supply) throws InterruptedException {
        int currentLevel = methods.skills.getStatic(Skill.HERBLORE);
        int[] levels = {3, 12,22,26,38, 45, 52, 55, 66, 72, 81, 99};
        int[] suppliesNeeded = new int[11];
        int[] xpPerPotion = {25,50,62,67,87,100,117,125,150,162,180};
        int counter = 0;
        for (int i = 0; i < levels.length; i++)
        {
            if (levels[i] > goalLevel && counter == 0)
            {
                levels[i] = goalLevel;
                counter++;
            }
            else if (levels[i] > goalLevel && counter != 0)
            {
                levels[i] = 0;
            }
        }
        for (int i = 1; i < levels.length; i++)
        {
            if (currentLevel < levels[i])
            {
                if (currentLevel >= levels[i-1])
                {
                    suppliesNeeded[i-1] = ((methods.skills.getExperienceForLevel(levels[i])-methods.skills.getExperience(Skill.HERBLORE))/xpPerPotion[i-1])+1;
                }
                else
                {
                    suppliesNeeded[i-1] = ((methods.skills.getExperienceForLevel(levels[i])-methods.skills.getExperienceForLevel(levels[i-1]))/xpPerPotion[i-1])+1;
                }
            }
            else
            {
                suppliesNeeded[i-1] = 0;
            }
        }
        methods.log("Pots Needed: ");
        for (int i:suppliesNeeded)
        {
            methods.log(i);
        }

        int[] supplyId = {91, 221, 95, 225, 97, 223, 97, 1975, 99, 231, 101, 221, 103, 2970, 105, 225, 107, 239, 109, 245, 3002, 6693, 227};
        String[] supplyName = {"Guam pot", "Eye of newt", "Tarromin pot", "Limp", "Harralander pot", "Red spider", "Harralander pot", "Chocolate dust", "Ranarr pot", "Snape",
                    "irit pot", "Eye of newt", "avantoe pot", "mort myre", "Kwuarm pot", "limp", "cadantine pot", "White be", "Dwarf weed pot",
                "Wine of za", "Toadflax pot", "Crushed nest", "Vial of water"};
        int[] supplyPrice = {150,5,250,250, 525, 500, 525, 50, 6200, 400, 1300, 5, 3500, 300, 2800, 300, 2900, 600, 1500, 1000, 3200,5500, 5};
        int total = 0;
        for (int i: suppliesNeeded)
        {
            total += i;
        }
        int[] supplyQuantity = {suppliesNeeded[0], suppliesNeeded[0], suppliesNeeded[1], suppliesNeeded[1], suppliesNeeded[2], suppliesNeeded[2], suppliesNeeded[3], suppliesNeeded[3],
                suppliesNeeded[4], suppliesNeeded[4], suppliesNeeded[5],suppliesNeeded[5], suppliesNeeded[6], suppliesNeeded[6], suppliesNeeded[7], suppliesNeeded[7],
                suppliesNeeded[8], suppliesNeeded[8], suppliesNeeded[9], suppliesNeeded[9], suppliesNeeded[10], suppliesNeeded[10], total};
        boolean withdraw = false;
        boolean[] withdraw_noted = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        return supply.supply(supplyId, supplyName, supplyPrice, supplyQuantity, geHelp, withdraw, withdraw_noted);
    }


}
