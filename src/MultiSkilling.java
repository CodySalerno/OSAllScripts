import javafx.geometry.Pos;
import org.osbot.P;
import org.osbot.Sk;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;


@ScriptManifest(name = "MultiSkilling", author = "Iownreality1", info = "Mines at motherload mine", version = 0.1, logo = "")
public class MultiSkilling extends Script
{

    String skill;
    Integer level;
    Supply supply = new Supply(this);
    GEHelper geHelp = new GEHelper(this);
    TalkCareful talker = new TalkCareful(this);
    DaddysHome daddy = new DaddysHome(this);
    EnergyCheck useStamina = new EnergyCheck(this);
    Area marloHouse = new Area(3238,3471,3242,3476);
    Area yarloHouse = new Area(3236,3391,3248,3399);
    Area sawMill = new Area(3294,3479,3310,3492);
    Area rimmingtonHouseArea = new Area(2953, 3221, 2957, 3226);
    Area phialsLocation = new Area(2946,3212, 2950, 3218);
    boolean rimmingtonHouse = false;
    boolean daddysHomeSupplied = false;
    boolean constructionSupplied = false;


    public void onStart() throws InterruptedException {
        log(inventory.getAmount("Oak plank"));
        String[] skills = {"Construction", "Farming"};
        JFrame frame = new JFrame("Select a skill");
        frame.setVisible(true);
        frame.setSize(250, 250);
        frame.setLocation(700, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // added code
        frame.add(panel);

        JLabel lbl = new JLabel("Select skill to train.");
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);

        final JComboBox<String> cb = new JComboBox<String>(skills);
        cb.setMaximumSize(cb.getPreferredSize()); // added code
        cb.setAlignmentX(Component.CENTER_ALIGNMENT);// added code
        panel.add(cb);

        JLabel lbl2 = new JLabel("Enter level to train to.");
        lbl2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl2);

        JTextField textBox = new JTextField("50");
        textBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        textBox.setEditable(true);
        textBox.setMaximumSize((textBox.getPreferredSize()));
        panel.add(textBox);

        JButton btn = new JButton("OK");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT); // added code
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    level = Integer.parseInt(textBox.getText());
                    if (level > 99 || level < 1)
                    {
                        throw new Exception("Level must be between 1-99");
                    }
                    skill = cb.getSelectedItem().toString();
                    frame.dispose();

                }
                catch (NumberFormatException ex)
                {
                    JOptionPane.showMessageDialog(frame, "Level must be a number", "Level Issue", JOptionPane.ERROR_MESSAGE);
                }
                catch (Exception exc)
                {
                    JOptionPane.showMessageDialog(frame, exc.getMessage(), "Level Issue", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(btn);

        frame.setVisible(true); // added code
    }

    @Override
    public int onLoop() throws InterruptedException {
        if (skill != null && level != null)
        {
            switch (skill)
            {
                case "Farming":
                    farm();
                    break;
                case "Construction":
                    Con();
                    break;

            }

        }
        return random(1800,2400);
    }

    private void Con() throws InterruptedException
    {
        int currentLevel = skills.getStatic(Skill.CONSTRUCTION);
        if (currentLevel >= level)
        {
            stop();
        }
        if ((settings.getRunEnergy() < 20) || (!settings.isRunning()))
        {
            useStamina.Stamina();
        }
        if (inventory.contains(24940))
        {
            inventory.getItem(24940).interact();
            sleep(random(800,1200));
        }
        if (configs.get(393) != 7864288 && (configs.get(393) != 7340000))
        {
            daddy.CompleteDaddy(geHelp,supply,talker);
        }
        else
        {
            if (!constructionSupplied)
            {
                constructionSupplied = constructionSupplier();
            }
            if ((configs.get(738) & 0x7) != 1)
            {
                inArea(marloHouse);
                npcs.closest("Estate agent").interact("Relocate");
                Sleep.sleepUntil(() -> (getWidgets().get(187,3,0) != null),8000);
                widgets.get(187,3,0).interact();
                sleep(random(800,1200));
            }
            else
            {
                if (!inventory.contains("Plank") && !inventory.contains("Oak plank") || !inventory.contains(2347) || !inventory.contains(8794)) //unnotes hammer and saw.
                {
                    if (UsefulAreas.GeArea.contains(myPosition()))
                    {
                        if (inventory.contains(2348)) //noted hammer
                        {
                            if (grandExchange.isOpen())
                            {
                                grandExchange.close();
                                Sleep.sleepUntil(() -> !grandExchange.isOpen(), 3000);
                                sleep(random(1200,1800));
                            }
                            inventory.getItem(2348).interact();
                            Sleep.sleepUntil(() -> inventory.isItemSelected(), 3000);
                            npcs.closest("Banker").interact();
                            Sleep.sleepUntil(() -> dialogues.inDialogue(), 3000);
                            if (dialogues.inDialogue())
                            {
                                dialogues.completeDialogue("Yes");
                                Sleep.sleepUntil(() -> !dialogues.inDialogue(), 3000);
                            }
                        }
                        if (inventory.contains(8795)) //noted saw
                        {
                            inventory.getItem(8795).interact();
                            Sleep.sleepUntil(() -> inventory.isItemSelected(), 3000);
                            npcs.closest("Banker").interact();
                            Sleep.sleepUntil(() -> dialogues.inDialogue(), 3000);
                            if (dialogues.inDialogue())
                            {
                                dialogues.completeDialogue("Yes");
                                Sleep.sleepUntil(() -> !dialogues.inDialogue(), 3000);
                            }
                        }
                        if (inventory.contains(8795) || inventory.contains(2348))
                        {
                            return;
                        }
                    }
                    else
                    {
                        walking.webWalk(UsefulAreas.GeArea);
                    }
                }
                if (objects.closest(4525) == null && objects.closest(15478) == null)
                {
                    if (inventory.contains("Teleport to house"))
                    {
                        inventory.getItem("Teleport to house").interact("Outside");
                        Sleep.sleepUntil(()-> objects.closest(15478) != null, 10000);
                    }
                }
                if (currentLevel < 9)
                {
                    if (objects.closest(4525) != null) //inside house
                    {
                        getFifteenCon(960, 2, true, 6752, 4517, 458, 4, 4);
                    }
                    if (objects.closest(15478) != null) //outside house
                    {
                        unnote(961, 2);
                    }
                }
                else if (currentLevel < 19)
                {
                    if (objects.closest(4525) != null) //inside house
                    {
                        if (objects.closest(15404) == null) //no sink space therefore no kitchen
                        {
                            buildKitchen();
                        }
                        else
                        {
                            getFifteenCon(960,8, true, 13565,15403, 458,4,4 );
                        }
                    }
                    if (objects.closest(15478) != null) //outside house
                    {
                        unnote(961, 8);
                    }
                }
                else if (currentLevel < 26)
                {
                    if (objects.closest(4525) != null) //inside house
                    {
                        getFifteenCon(8778,2, false, 6755,4517, 458,7,4 );
                    }
                    if (objects.closest(15478) != null) //outside house
                    {
                        unnote(8779, 2);
                    }
                }
                else if (currentLevel < 33)
                {
                    if (objects.closest(4525) != null) //inside house
                    {
                        getFifteenCon(8778,3, false, 6756,4517, 458,8,4 );
                    }
                    if (objects.closest(15478) != null) //outside house
                    {
                        unnote(8779, 3);
                    }
                }
                else
                { //oak larders for anything over 33 con.
                    if (objects.closest(4525) != null) //inside house
                    {
                        if (objects.closest(15404) == null) //no sink space therefore no kitchen
                        {
                            buildKitchen();
                        }
                        else
                        {
                            if (objects.closest(13565) != null)
                            {
                                getFifteenCon(8778,8, false, 13565,15403, 458,5,4 );
                            }
                            getFifteenCon(8778,8, false, 13566,15403, 458,5,4 );
                        }
                    }
                    if (objects.closest(15478) != null) //outside house
                    {
                        unnote(8779, 8);
                    }
                }
            }
        }
    }

    private void unnote(int plank, int plankAmount) throws InterruptedException {
        if (inventory.getAmount(plank-1) <= plankAmount)
        {
            if (!phialsLocation.contains(myPosition()))
            {
                walking.webWalk(phialsLocation);
            }
            if (phialsLocation.contains(myPosition()))
            {
                inventory.getItem(plank).interact();
                Sleep.sleepUntil(() -> inventory.isItemSelected(), 1500);
                if (inventory.isItemSelected())
                {
                    npcs.closest("Phials").interact();
                    Sleep.sleepUntil(() -> dialogues.inDialogue(), 8000);
                    if (dialogues.inDialogue())
                    {
                        dialogues.completeDialogue("Exchange All");
                    }
                }
            }
        }
        if ((inventory.getAmount(plank-1) >= plankAmount))
        {
            if (!rimmingtonHouseArea.contains(myPosition()))
            {
                walking.webWalk(rimmingtonHouseArea);
            }
            if (rimmingtonHouseArea.contains(myPosition()))
            {
                objects.closest(15478).interact("Build mode");
                Sleep.sleepUntil(() -> objects.closest(4525) != null, 8000);
            }
        }

    }

    private void bank()
    {

    }

    private void buildKitchen() throws InterruptedException {
        log("under 15 gl build me a ktichen pls.");
        int x = objects.closest(4525).getX();//9939   9936
        int y = objects.closest(4525).getY();//3723   3723
        Area door = new Area(x-4, y-1, x-2,y+1);
        if ( objects.closest(door,"Door hotspot") != null)
        {
            objects.closest(door,"Door hotspot").interact("Build");
            Sleep.sleepUntil(() -> widgets.getWidgetContainingText("Room Creation Menu") != null, 5000);
            if (widgets.getWidgetContainingText("Room Creation Menu") != null)
            {
                widgets.get(212,9).interact();
                Sleep.sleepUntil(() -> dialogues.inDialogue(), 5000);
                if (dialogues.inDialogue())
                {
                    dialogues.completeDialogue("Build");
                    Sleep.sleepUntil(() -> !dialogues.inDialogue() , 5000);
                }
            }
        }
    }

    private void getFifteenCon(int plank, int plankAmount, boolean nailsNeeded, int builtId, int emptyId, int widget1, int widget2, int widget3) throws InterruptedException {
        if (inventory.getAmount(plank) >= plankAmount  && ((inventory.getAmount("Steel nails") >= 10) || !nailsNeeded) && inventory.contains(2347) && inventory.contains(8794))
        {
            if (objects.closest(builtId) != null) //built chair
            {
                objects.closest(builtId).interact("Remove");
                Sleep.sleepUntil(() -> dialogues.inDialogue(), 6000);
                if (dialogues.inDialogue())
                {
                    dialogues.completeDialogue("Yes");
                    Sleep.sleepUntil(() -> objects.closest(builtId ) == null, 2400);
                }
            }
            if (objects.closest(emptyId) != null) //not built chair
            {
                objects.closest(emptyId).interact("Build");
                Sleep.sleepUntil(() -> widgets.get(widget1,widget2,widget3) != null, 3000);
                if (widgets.get(widget1,widget2,widget3) != null)
                {
                    widgets.get(widget1,widget2,widget3).interact();
                    Sleep.sleepUntil(() -> objects.closest(emptyId ) == null, 10000);
                }
            }
        }
        else
        {
            objects.closest(4525).interact(); //portal
            Sleep.sleepUntil(() -> objects.closest(4525) == null, 6000);

        }
    }

    private void inArea(Area area)
    {
        if (!area.contains(myPosition()))
        {
            walking.webWalk(area);
        }
    }

    private boolean constructionSupplier() throws InterruptedException {
        int plankXP;
        int oakPlankXp;
        if (level < 19)
        {
            plankXP = skills.getExperienceForLevel(level)-skills.getExperience(Skill.CONSTRUCTION);
            oakPlankXp = 0;
        }
        else if (skills.getStatic(Skill.CONSTRUCTION) >= 19)
        {
            plankXP = 0;
            oakPlankXp = skills.getExperienceForLevel(level)-skills.getExperience(Skill.CONSTRUCTION);
        }
        else
        {
            plankXP = skills.getExperienceForLevel(19)-skills.getExperience(Skill.CONSTRUCTION);
            oakPlankXp = skills.getExperienceForLevel(level)-skills.getExperienceForLevel(19);
        }
        int plankAmount = (plankXP/29)+8;
        int steelNailAmount = plankAmount*15;
        if (plankAmount == 8)
        {
            plankAmount = 0;
            steelNailAmount = 0;
        }
        int oakPlankAmount = (oakPlankXp/60)+8; //add 8 because floor division, 8 is cost for one item
        log("Plank needed: " + plankAmount + "Oak needed" + oakPlankAmount + "Nails: " + steelNailAmount);
        if (inventory.getAmount("Plank") >= plankAmount && inventory.getAmount("Oak plank") >= oakPlankAmount
                && inventory.getAmount("Steel nails") >= steelNailAmount && inventory.getAmount("Coins") >= ((plankAmount+oakPlankAmount)*5)+20000
                && inventory.contains("Hammer") && inventory.contains("Saw"))
        {
            return true;
        }
        int[] supplyId = {960,1539,8778, 8013,2347, 8794, 995};
        String[] supplyName = {"Plank", "Steel nails", "Oak plank", "Teleport to house", "Hammer", "Saw", "Coins"};
        int[] supplyPrice = {300, 50, 500, 1000, 1000, 1000, 1};
        int[] supplyQuantity = {plankAmount, steelNailAmount, oakPlankAmount, 1, 1, 1, ((plankAmount+oakPlankAmount)*5)+20000};
        return supply.supply(supplyId, supplyName, supplyPrice, supplyQuantity, geHelp);
    }

    private void farm()
    {

    }


}