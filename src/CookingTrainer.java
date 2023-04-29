import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;
import util.*;


public class CookingTrainer
{

    boolean supplied = false;
    private final MethodProvider methods;
    Area camelot = new Area(2740,3420,2860,3482);
    Area catherybyBank = new Area(2806,3438, 2812,3441);
    Area catherybyRange = new Area(2815,3439, 2818,3443);


    public CookingTrainer(final MethodProvider methods)
    {
        this.methods = methods;
    }

    public void TrainCooking(GEHelper geHelp, Supply supply, int goalLevel) throws InterruptedException
    {
        int currentLevel = methods.skills.getStatic(Skill.COOKING);
        if (!supplied)
        {
            supplied = cookingSupplier(goalLevel, geHelp, supply);
            return;
        }
        if (goToCatherby())
        {
            methods.log("Not in catherby");
            return;
        }
        if (currentLevel < 5) //shrimp
        {
            cookFood(317);
        }
        else if (currentLevel < 15) //herring
        {
            cookFood(345);
        }
        else if (currentLevel < 25) //trout
        {
            cookFood(335);
        }
        else if (currentLevel < 35) //salmon
        {
            cookFood(331);
        }
        else //wines
        {
            methods.log("starting wines");
            makeWines();
        }
    }

    private void makeWines()
    {
        if(catherybyBank.contains(methods.myPosition()))
        {
            if (methods.inventory.contains(1937) && methods.inventory.contains(1987))
            {
                if (methods.inventory.isItemSelected())
                {
                    methods.inventory.deselectItem();
                    Sleep.sleepUntil(() -> !methods.inventory.isItemSelected(),3000);
                }
                else
                {
                    methods.inventory.getItem(1937).interact();
                    Sleep.sleepUntil(() -> methods.inventory.isItemSelected(), 3000);
                    if (methods.inventory.isItemSelected())
                    {
                        methods.inventory.getItem(1987).interact();
                        Sleep.sleepUntil(() -> methods.widgets.get(270,14,38) != null, 3000);
                        if (methods.widgets.get(270,14,38) != null)
                        {
                            int amount = (int)methods.inventory.getAmount("Grapes");
                            methods.widgets.get(270,14,38).interact();
                            Sleep.sleepUntil(() -> amount != (int)methods.inventory.getAmount("Grapes"), 3000);
                            if (amount != (int)methods.inventory.getAmount("Grapes"))
                            {
                                Sleep.sleepUntil(() -> !methods.inventory.contains(1937) || !methods.inventory.contains(1987) || methods.dialogues.isPendingContinuation(), 50000);
                                methods.log("done sleeping after last wine");
                            }
                        }
                    }
                }
            }
            else
            {
                methods.npcs.closest("Banker").interact("Bank");
                Sleep.sleepUntil(() -> methods.bank.isOpen(), 5000);
                if (methods.bank.isOpen())
                {
                    if (!methods.inventory.isEmpty())
                    {
                        methods.bank.depositAll();
                        Sleep.sleepUntil(() -> methods.inventory.isEmpty(), 3000);
                    }
                    if (methods.inventory.isEmpty())
                    {
                        methods.bank.withdraw(1937, 14);
                        Sleep.sleepUntil(() -> methods.inventory.contains(1937), 5000);
                        if (methods.inventory.contains(1937))
                        {
                            methods.bank.withdraw(1987, 14);
                            Sleep.sleepUntil(() -> methods.inventory.contains(1987), 5000);
                        }
                        if (methods.skills.getStatic(Skill.COOKING) < 68  //this will wait until fermenting jugs are done fermenting
                                && methods.bank.getAmount(1995) > 130) //higher level means less fails good to do every 10 invens or so.
                        {
                            int xp = methods.skills.getExperience(Skill.COOKING);
                            methods.bank.close();
                            Sleep.sleepUntil(() -> xp != methods.skills.getExperience(Skill.COOKING), 50000);
                        }
                        if (methods.inventory.contains(1937) && methods.inventory.contains(1987) && methods.bank.isOpen())
                        {
                            methods.bank.close();
                            Sleep.sleepUntil(() -> !methods.bank.isOpen(), 5000);
                        }
                    }
                }
            }
        }
    }

    private void cookFood(int food)
    {
        methods.log("in shrim");
        if (methods.inventory.contains(food))
        {
            methods.log("Have shrimp in inventory");
            if (catherybyRange.contains(methods.myPosition()))
            {
                methods.log("start cooking");
                methods.objects.closest("Range").interact();
                Sleep.sleepUntil(() -> methods.widgets.getWidgetContainingText("How many would you like to cook?") != null, 5000);
                if (methods.widgets.get(270,14,38) != null)
                {
                    int amountOfFood = (int)methods.inventory.getAmount(food);
                    methods.widgets.get(270,14,38).interact();
                    Sleep.sleepUntil(() -> amountOfFood !=(int)methods.inventory.getAmount(food), 7000 );
                    if (amountOfFood == (int)methods.inventory.getAmount(food)) return; //starting loop over b/c food is not cooking.
                    else Sleep.sleepUntil(() -> methods.dialogues.isPendingContinuation() || !methods.inventory.contains(food), 80000); //long sleep waiting for inven to finish
                }
            }
            else
            {
                methods.log("walk to range");
                methods.walking.webWalk(catherybyRange);
            }
        }
        else
        {
            methods.log("No shrimp in inventory");
            if (catherybyRange.contains(methods.myPosition()))
            {
                methods.log("walking to bank");
                methods.walking.webWalk(catherybyBank);
            }
            else if (catherybyBank.contains(methods.myPosition()))
            {
                methods.log("in bank getting shrimp");
                methods.npcs.closest("Banker").interact("Bank");
                Sleep.sleepUntil(() -> methods.bank.isOpen(), 6000);
                if (methods.bank.isOpen())
                {
                    methods.bank.depositAll();
                    Sleep.sleepUntil(() -> methods.inventory.isEmpty(), 5000);
                    if (methods.inventory.isEmpty())
                    {
                        methods.bank.withdrawAll(food);
                        Sleep.sleepUntil(() -> methods.inventory.contains(food), 5000);
                        methods.bank.close();
                        Sleep.sleepUntil(() -> !methods.bank.isOpen(), 5000);
                    }
                }
            }
            else
            {
                methods.log("im lost go back to bank");
                methods.walking.webWalk(catherybyBank);
            }
        }
    }
    private boolean goToCatherby()
    {
        if (camelot.contains(methods.myPosition()))
        {
            return false;
        }
        if (UsefulAreas.GeArea.contains(methods.myPosition()))
        {
            if (methods.inventory.contains(8010))
            {
                methods.inventory.getItem(8010).interact();
                Sleep.sleepUntil(() -> camelot.contains(methods.myPosition()), 5000 );
            }
            else
            {
                methods.npcs.closest("Banker").interact("Bank"); // open Bank
                Sleep.sleepUntil(()-> methods.bank.isOpen(), 5000);
                if (methods.bank.isOpen())
                {
                    if (methods.bank.contains(8010))
                    {
                        methods.bank.withdraw(8010,1);
                        Sleep.sleepUntil(() -> methods.inventory.contains(8010), 4000);
                        methods.bank.close();
                        Sleep.sleepUntil(() -> !methods.bank.isOpen(), 4000);
                        if (!methods.bank.isOpen() && methods.inventory.contains(8010))
                        {
                            methods.inventory.getItem(8010).interact();
                            Sleep.sleepUntil(() -> camelot.contains(methods.myPosition()), 5000);
                        }
                    }
                    else
                    {
                        supplied = false;
                        return true;
                    }
                }
            }
        }
        return !camelot.contains(methods.myPosition());
    }
    private boolean cookingSupplier(int goalLevel, GEHelper geHelp, Supply supply) throws InterruptedException {
        int currentLevel = methods.skills.getStatic(Skill.COOKING);
        int[] levels = {1, 5,15,25,35,68,99};
        int[] fishNeeded = new int[6];
        int[] approxXpPerFish = {15, 15, 25, 30, 100,200};
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
                    fishNeeded[i-1] = (methods.skills.getExperienceForLevel(levels[i])-methods.skills.getExperience(Skill.COOKING))/approxXpPerFish[i-1];
                }
                else
                {
                    fishNeeded[i-1] = (methods.skills.getExperienceForLevel(levels[i])-methods.skills.getExperienceForLevel(levels[i-1]))/approxXpPerFish[i-1];
                }
            }
            else
            {
                fishNeeded[i-1] = 0;
            }
        }
        methods.log("Fish Needed: ");
        for (int i:fishNeeded)
        {
            methods.log(i);
        }

        int[] supplyId = {317,345,335, 331,1937, 1987, 8010};
        String[] supplyName = {"Raw shrimps", "Raw herring", "Raw trout", "Raw salmon", "Jug of water", "Grapes", "Camelot teleport"};
        int[] supplyPrice = {200, 200, 100, 100, 50, 50, 2000};
        int[] supplyQuantity = {fishNeeded[0], fishNeeded[1], fishNeeded[2], fishNeeded[3], fishNeeded[4]+fishNeeded[5], fishNeeded[4]+fishNeeded[5], 1};
        boolean withdraw = false;
        boolean[] withdraw_noted = {};

        if (camelot.contains(methods.myPosition()))
        {
            methods.walking.webWalk(catherybyBank);
            if (methods.npcs.closest("Banker") != null)
            {
                methods.npcs.closest("Banker").interact("Bank");
                Sleep.sleepUntil(() -> methods.bank.isOpen(), 7000);
                if (methods.bank.isOpen())
                {
                    methods.bank.depositAll();
                    Sleep.sleepUntil(() -> methods.inventory.isEmpty(), 5000);
                    boolean haveSups = true;
                    for (int i = 0; i <supplyId.length-1; i++)
                    {
                        int amount = (int)methods.bank.getAmount(supplyId[i]);
                        if (amount < supplyQuantity[i])
                        {
                            methods.log("Don't have sups" + supplyId[i]);
                            haveSups = false;
                        }
                    }
                    if (haveSups)
                    {
                        return true;
                    }
                }

            }
        }
        return supply.supply(supplyId, supplyName, supplyPrice, supplyQuantity, geHelp, withdraw, withdraw_noted);
    }


}
