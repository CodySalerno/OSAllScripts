import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.event.interaction.MouseMoveProfile;
import org.osbot.rs07.script.MethodProvider;
import util.*;

import static java.lang.Thread.sleep;
import static org.osbot.rs07.script.MethodProvider.random;


public class FletchingTrainer
{

    boolean setFastMouse = false;
    boolean supplied = false;
    private final MethodProvider methods;
    Area camelot = new Area(2740,3420,2860,3482);
    Area catherbyBank = new Area(2806,3438, 2812,3441);
    Area catherbyRange = new Area(2815,3439, 2818,3443);


    public FletchingTrainer(final MethodProvider methods)
    {
        this.methods = methods;
    }

    public void TrainFletching(GEHelper geHelp, Supply supply, int goalLevel) throws InterruptedException
    {
        int currentLevel = methods.skills.getStatic(Skill.FLETCHING);
        if (!supplied)
        {
            supplied = dartSupplier(goalLevel, geHelp, supply);
            return;
        }
        if (!setFastMouse) setFastMouse();
        if (currentLevel < 22) //Bronze darts
        {
            methods.log("making bronze darts");
            makeDarts(819);
        }
        else if (currentLevel < 37) //iron darts
        {
            methods.log("making iron darts");
            makeDarts(820);
        }
        else if (currentLevel < 52) //steel darts
        {
            methods.log("making steel darts");
            makeDarts(821);
        }
        else if (currentLevel < 99) //mithril darts
        {
            methods.log("making mithril darts");
            makeDarts(822);
        }
    }
    public void setFastMouse()
    {
        methods.getBot().setMouseMoveProfile(new MouseMoveProfile()
                .setNoise(0)
                .setDeviation(0)
                .setOvershoots(0)
                .setSpeedBaseTime(0)
                .setFlowSpeedModifier(0)
                .setMinOvershootDistance(0)
                .setFlowVariety(MouseMoveProfile.FlowVariety.MEDIUM)
                .setOvershoots(0)
        );
        setFastMouse = true;
    }
    private void makeDarts(int dartTipId) throws InterruptedException {
        if (methods.bank.isOpen())
        {
            methods.bank.close();
        }
        if (methods.inventory.contains(dartTipId) && methods.inventory.contains(314) && !methods.inventory.isItemSelected())
        {
            methods.inventory.getItem(314).interact();
            sleep(random(75,150));
            methods.inventory.getItem(dartTipId).interact();
            sleep(random(75,150));
            methods.inventory.getItem(dartTipId).interact();
        }
        else if (methods.inventory.contains(dartTipId) && methods.inventory.contains(314) && methods.inventory.getSelectedItemId() == dartTipId )
        {
            methods.inventory.getItem(314).interact();
            sleep(random(75,150));
            methods.inventory.getItem(314).interact();
            sleep(random(75,150));
        }
        else if (methods.inventory.contains(dartTipId) && methods.inventory.contains(314) && methods.inventory.getSelectedItemId() == 314)
        {
            methods.inventory.getItem(dartTipId).interact();
            sleep(random(75,150));
            methods.inventory.getItem(dartTipId).interact();
            sleep(random(75,150));
        }
    }


    private boolean dartSupplier(int goalLevel, GEHelper geHelp, Supply supply) throws InterruptedException {
        int currentLevel = methods.skills.getStatic(Skill.FLETCHING);
        int[] levels = {10, 22,37,52,99};
        int[] dartsNeeded = new int[4];
        int[] xpPerTenDarts = {18, 38, 75, 112};
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
                    dartsNeeded[i-1] = ((methods.skills.getExperienceForLevel(levels[i])-methods.skills.getExperience(Skill.FLETCHING))/xpPerTenDarts[i-1])*10+10;
                }
                else
                {
                    dartsNeeded[i-1] = ((methods.skills.getExperienceForLevel(levels[i])-methods.skills.getExperienceForLevel(levels[i-1]))/xpPerTenDarts[i-1])*10+10;
                }
            }
            else
            {
                dartsNeeded[i-1] = 0;
            }
        }
        methods.log("Darts Needed: ");
        for (int i:dartsNeeded)
        {
            methods.log(i);
        }

        int[] supplyId = {819,820,821, 822,314};
        String[] supplyName = {"Bronze dart tip", "Iron dart tip", "Steel dart tip", "Mithril dart tip", "Feather"};
        int[] supplyPrice = {60, 150, 100, 100, 4};
        int[] supplyQuantity = {dartsNeeded[0], dartsNeeded[1], dartsNeeded[2], dartsNeeded[3], dartsNeeded[0]+dartsNeeded[1]+dartsNeeded[2]+dartsNeeded[3]};
        boolean withdraw = true;
        boolean[] withdraw_noted = {false, false, false, false, false};
        return supply.supply(supplyId, supplyName, supplyPrice, supplyQuantity, geHelp, withdraw, withdraw_noted);
    }


}
