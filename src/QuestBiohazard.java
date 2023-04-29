import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import util.*;

import java.util.ArrayList;

@ScriptManifest(name = "Biohazard", author = "Iownreality1", info = "Biohazard", version = 0.1, logo = "")
public class QuestBiohazard extends Script
{
    BetterWalk MyWeb = new BetterWalk(this);
    TalkCareful talker = new TalkCareful(this);
    boolean supplied = false;
    boolean has_gas = false;
    GEHelper geHelper = new GEHelper(this);
    Supply supply = new Supply(this);
    final int[] supplyID = {1387, 558, 556, 8011, 8007, 12625};
    final String[] supplyName = {"Staff of fire", "Mind rune", "Air rune", "Ardougne teleport",  "Varrock teleport", "Stamina potion(4)"};
    final int[] supplyPrice = {3000, 10, 10, 500, 700, 10000};
    final int[] supplyQuantity = {1, 100, 200, 9, 10, 5};
    boolean withdraw = true;
    boolean[] withdraw_noted = {false, false, false, false, false, false};
    private final Area GeArea = new Area(3159,3482,3168,3492);
    private final Area ArdougneArea = new Area(2600,3300,2700,3500);
    private final Area ElenaArea = new Area(2590,3334,2592,3338);
    private final Area JericoArea = new Area(2609, 3321,2622, 3328);
    private final Position watchtower = new Position(2563, 3300, 0);
    private final Area nearVat = new Area(2542, 3328, 2555, 3333);
    private final Area SaraHouse = new Area(2515, 3270, 2517, 3275);
    private final Position outsideHQ = new Position(2551, 3320, 0);
    private final Area insideHQ = new Area(2542, 3321, 2555, 3327);
    private final Area nearStairsHq = new Area(2542, 3324, 2546, 3326);
    private final Position safespot = new Position(2547, 3322, 1);
    private final Area crateRoom = new Area(2552, 3325, 2554, 3326);
    private final Area southBank = new Area(2649, 3280,2655, 3287);
    private final Area shipArea = new Area(2672, 3274, 2682, 3276);
    private final Area rimmingtonBoat = new Area(2910, 3220, 2919, 3222);
    private final Area rimmingtonDock = new Area(2908, 3225, 2922, 3226);
    private final Area chemistArea = new Area(2929, 3207, 2935,3213);
    private final Area varrockSquare = new Area(3200,3400,3225,3450);
    private final Area varrockGate = new Area(3260, 3404,3263, 3407);
    private final Area insideGate = new Area(3264, 3381, 3286, 3407);
    private final Area varrockInn = new Area(3266,3388,3274,3391);
    private final Area dressShop = new Area(3278, 3395,3283, 3400);
    private final Position Guidor = new Position(3283, 3382, 0);
    private final Area kingRoom = new Area(2575,3292,2580,3295);

    EnergyCheck useStamina = new EnergyCheck(this);

    public void onStart()
    {
        rimmingtonBoat.setPlane(1);
        crateRoom.setPlane(1);
        insideHQ.setPlane(0);
        nearStairsHq.setPlane(0);
        kingRoom.setPlane(1);
    }

    public int onLoop() throws InterruptedException
    {

        if ((settings.getRunEnergy() < 20) || (!settings.isRunning()))
        {

            useStamina.Stamina();
        }
        int BiohazardProg =configs.get(68);
        log(BiohazardProg);
        switch (BiohazardProg)
        {
        case 0:
            if (!supplied)
            {
                supply.supply(supplyID, supplyName, supplyPrice, supplyQuantity, geHelper, withdraw, withdraw_noted);
            }
            if (supplied)
            {
                log("Case 0");
                if (!ElenaArea.contains(myPosition()))
                {
                    inventory.getItem(8011).interact();
                    sleep(random(4200, 4800));
                }
                walking.webWalk(ElenaArea);
                sleep(random(1800, 2400));
                log(configs.get(68));
                talker.TalkandWait("Elena", "Yes");
                log(configs.get(68));
                sleep(random(1800, 2400));
                break;
            }
        case 1:
            log("Case 1");
            if (!JericoArea.contains(myPosition()))
            {
                walking.webWalk(JericoArea);
                log(configs.get(68));
                talker.TalkandWait("Jerico");
                log(configs.get(68));
            }
            sleep(random(600, 800));
        case 2:
            if (!inventory.contains("Bird feed"))
            {
                MyWeb.MyWebWalkingEvent(2611, 3325, 0);
                if (objects.closest("Cupboard").getDefinition().getActions()[0].equals("Open")) //opens if closed
                {
                    objects.closest("Cupboard").interact();
                    sleep(random(1800, 2400));
                }
                objects.closest("Cupboard").interact(); //searches
                Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 2400);
                if (dialogues.isPendingContinuation())
                {
                    dialogues.completeDialogue();
                }
            }
            else if (!inventory.contains("Pigeon cage"))
            {
                MyWeb.MyWebWalkingEvent(2619, 3324, 0);
                groundItems.closest("Pigeon cage").interact();
            }
            else
            {
                MyWeb.MyWebWalkingEvent(watchtower);
                sleep(random(600, 800));
                if (myPosition().equals(watchtower))
                {
                    log(configs.get(68));
                    inventory.getItem("Bird feed").interact();
                    sleep(random(1800, 2400));
                    objects.closest("Watchtower").interact();
                    Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 2400);
                    if (dialogues.isPendingContinuation())
                    {
                        dialogues.completeDialogue();
                    }
                    log(configs.get(68));
                }
            }
        case 3:
            if (watchtower.equals(myPosition()))
            {
                log(configs.get(68));
                inventory.getItem("Pigeon cage").interact();
                Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 2400);
                if (dialogues.isPendingContinuation())
                {
                    dialogues.completeDialogue();
                }
                log(configs.get(68));
            }
            else
            {
                MyWeb.MyWebWalkingEvent(watchtower);
                sleep(random(600, 800));
            }
        case 4:
            log("Case 4");
            if (equipment.contains("Gas mask"))
            {
                walking.webWalk(new Area(2559, 3265, 2560, 3270));
                log(configs.get(68));
                talker.TalkandWait("Omart", "Okay, lets do it.");
                log(configs.get(68));
            }
            else
            {
                if (inventory.contains("Gas mask"))
                {
                    log("inventory contains");
                    inventory.getItem("Gas mask").interact();
                    sleep(random(1800, 2400));
                }
                else if (!inventory.contains("Gas mask") && !equipment.contains("Gas mask") && !has_gas)
                {
                    MyWeb.MyWebWalkingEvent(2575, 3334, 0);
                    if (objects.closest("Cupboard").getDefinition().getActions()[0].equals("Open")) //opens if closed
                    {
                        log("opening");
                        objects.closest("Cupboard").interact();
                        sleep(random(1800, 2400));
                    }
                    log("searching");
                    objects.closest("Cupboard").interact();
                    sleep(random(1800, 2400));
                    log("recheck");
                    if (inventory.contains("Gas mask"))
                    {
                        log("yes");
                        has_gas = true;
                    }
                    else
                    {
                        log("must be in bank");
                        MyWeb.MyWebWalkingEvent(2615, 3332, 0);
                        npcs.closest("Banker").interact("Bank");
                        Sleep.sleepUntil(() -> bank.isOpen(), 2400);
                        sleep(random(1800, 2400));
                        if (bank.isOpen() && bank.contains("Gas mask"))
                        {
                            bank.withdraw("Gas mask", 1);
                            sleep(random(1800, 2400));
                            bank.close();
                        }
                        if (inventory.contains("Gas mask"))
                        {
                            log("after banking");
                            has_gas = true;
                        }
                    }
                }
            }
        case 5:
            log("case 5");
            if (nearVat.contains(myPosition()) && inventory.contains("Rotten apple"))
            {
                log("have apple");
                log(configs.get(68));
                inventory.getItem("Rotten apple").interact("Use");
                sleep(random(600, 800));
                objects.closest(37327).interact();
                log(configs.get(68));
                sleep(random(2400, 3000));
            }
            else if (nearVat.contains(myPosition()))
            {
                log("no apple");
                log(configs.get(68));
                groundItems.closest("Rotten apple").interact("Take");
                Sleep.sleepUntil(() -> inventory.contains("Rotten apple"), 2400);
                log(configs.get(68));
            }
            else
            {
                log("outside");
                log(configs.get(68));
                walking.webWalk(new Position(2538, 3331, 0));
                log(configs.get(68));
                objects.closest("Fence").interact("Squeeze-through");
                sleep(random(2400, 3000));
                log(configs.get(68));
            }
        case 6:
            log("case 6");
            if (myPosition().getZ() == 1 && inventory.contains("Key"))
            {
                log(configs.get(68));
                walking.webWalk(crateRoom);
                sleep(random(600, 800));
                log(configs.get(68));
                objects.closest(2064).interact("Search");
                sleep(random(600, 800));
                log(configs.get(68));
            }
            else if (myPosition().getZ() == 1 && (!myPosition().equals(safespot)))
            {
                log("upstairs");
                log(configs.get(68));
                MyWeb.MyWebWalkingEvent(safespot);
            }
            else if (myPosition().getZ() == 1 && (!getCombat().isFighting()))
            {
                log("upstairs in safespot");
                if (inventory.contains("Staff of fire"))
                {
                    log("equipping");
                    inventory.getItem("Staff of fire").interact();
                }
                else if (configs.get(108) != 9)
                {
                    log("setting autocast");
                    tabs.open(Tab.ATTACK);
                    sleep(random(600, 800));
                    widgets.get(593, 26, 4).interact();
                    sleep(random(600, 800));
                    widgets.get(201, 1, 4).interact();
                    sleep(random(600, 800));
                }
                else if (!inventory.contains("Key") && npcs.closest("Mourner") != null)
                {
                    log("about to fight");
                    npcs.closest("Mourner").interact("Attack");
                }
                else
                {
                    log("done");
                    log(configs.get(68));
                }
            }
            else if (myPosition().getZ() == 1 && (getCombat().isFighting()))
            {
                return random(600, 800);
            }
            else if (insideHQ.contains(myPosition()))
            {
                log("in hq");
                if (nearStairsHq.contains(myPosition()))
                {
                    log("Near stairs");
                    objects.closest(16671).interact("Climb-up");
                    sleep(random(1800, 2400));
                }
                else
                {
                    log("In but not near");
                    walking.webWalk(nearStairsHq);
                    sleep(random(1200, 1800));
                }
            }
            else if (nearVat.contains(myPosition()))
            {
                log("near vat");
                objects.closest("Fence").interact("Squeeze-through");
                sleep(random(2400, 3000));
                log(configs.get(68));
            }
            else if (equipment.contains("Medical gown") && (!insideHQ.contains(myPosition())))
            {
                log("wearing gown outside");
                walking.webWalk(outsideHQ);
                objects.closest("Door").interact("Open");
                Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 2400);
                if (dialogues.isPendingContinuation())
                {
                    dialogues.completeDialogue();
                }
                log(configs.get(68));
            }
            else if (inventory.contains("Medical gown"))
            {
                log(configs.get(68));
                inventory.getItem("Medical gown").interact();
                log(configs.get(68));
            }
            else if (SaraHouse.contains(myPosition()) &&
                     (!inventory.contains("Medical gown") && (!equipment.contains("Medical gown"))))
            {
                log("in house without gown");
                if (objects.closest("Cupboard").getDefinition().getActions()[0].equals("Open")) //opens if closed
                {
                    log(configs.get(68));
                    objects.closest("Cupboard").interact();
                    sleep(random(1800, 2400));
                }
                log(configs.get(68));
                objects.closest("Cupboard").interact(); //searches
                Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 2400);
                if (dialogues.isPendingContinuation())
                {
                    dialogues.completeDialogue();
                    log(configs.get(68));
                }
            }
            else if (!inventory.contains("Medical gown") && (!equipment.contains("Medical gown")))
            {
                log("no gown");
                walking.webWalk(SaraHouse);
            }
        case 7:
            if (inventory.contains("Key") && inventory.contains(420))
            {
                inventory.getItem("Key").interact("Drop");
            }
            if (inventory.contains("Key") && (!inventory.contains(420)))
            {
                walking.webWalk(crateRoom);
                sleep(random(600, 800));
                log(configs.get(68));
                objects.closest(2064).interact("Search");
                sleep(random(600, 800));
                log(configs.get(68));
            }
            log("Case 7");
            if (inventory.contains(420) && myPosition().getZ() == 1)
            {
                log(configs.get(68));
                inventory.getItem(8011).interact();
                log(configs.get(68));
                sleep(random(3600, 4200));
            }
            else if (inventory.contains(420))
            {
                log(configs.get(68));
                walking.webWalk(ElenaArea);
                log(configs.get(68));
                sleep(random(600, 800));
                talker.TalkandWait("Elena");
                log(configs.get(68));
            }
        case 10:

            log("Case 10");
            if (!southBank.contains(myPosition()) && (!inventory.contains("Coins")))
            {
                log("Going to get coins");
                walking.webWalk(southBank);
                sleep(random(1200, 1800));
            }
            else if (!inventory.contains("Coins") && southBank.contains(myPosition())) {
                log("grabbing coins");
                npcs.closest("Banker").interact("Bank");
                Sleep.sleepUntil(() -> bank.isOpen(), 2400);
                if (bank.isOpen())
                {
                    bank.withdrawAll("Coins");
                    sleep(random(600, 800));
                    bank.close();
                }
            }
            else if (southBank.contains(myPosition()))
            {
                log("going to boat");
                walking.webWalk(shipArea);
                sleep(random(2400, 3000));
            }
            else if (shipArea.contains(myPosition()))
            {
                log("getting on boat");
                npcs.closest("Captain Barnaby").interact("Rimmington");
                Sleep.sleepUntil(() -> rimmingtonBoat.contains(myPosition()), 6000);
            }
            else if (rimmingtonBoat.contains(myPosition()))
            {
                log("crossing the gangplank");
                objects.closest("Gangplank").interact("Cross");
                sleep(random(2400, 3000));
            }
            else if (rimmingtonDock.contains(myPosition()))
            {
                log("walking to chemist");
                walking.webWalk(chemistArea);
                sleep(random(2400, 3000));
            }
            else if (chemistArea.contains(myPosition()))
            {
                log("talking to chemist");
                talker.TalkandWait("Chemist", "Your quest.");
                sleep(random(2400, 3000));
            }
            else
            {
                log("Going to rimmington");
                walking.webWalk(chemistArea);
            }
        case 12:
            log("Case 12");
            if (Guidor.equals(myPosition()))
            {
                talker.TalkandWait("Guidor", "come to ask you", "I've been sent", "That's why Elena");
            }
            else if (insideGate.contains(myPosition()) &&
                     inventory.contains(415) &&
                     inventory.contains(416) &&
                     inventory.contains(417))
            {
                walking.webWalk(Guidor);
            }
            else if (inventory.contains(415) && (!varrockInn.contains(myPosition())))
            {
                log("First item deposit");
                talker.TalkandWait(1103, "ethenea");
                log(configs.get(68));
                sleep(random(1800, 2400));
            }
            else if (inventory.contains(416) && (!varrockInn.contains(myPosition())))
            {
                log("second item deposit");
                talker.TalkandWait(1105, "liquid honey");
                log(configs.get(68));
                sleep(random(1800, 2400));
            }
            else if (inventory.contains(417) && (!varrockInn.contains(myPosition())))
            {
                log("third item deposit");
                talker.TalkandWait(1107, "broline");
            }
            else if (varrockSquare.contains(myPosition()))
            {
                log("In square");
                walking.webWalk(varrockGate);
            }
            else if (varrockGate.contains(myPosition()))
            {
                log("near gate");
                objects.closest("Gate").interact("Open");
                Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 2400);
                if (dialogues.isPendingContinuation())
                {
                    dialogues.completeDialogue();
                }
            }
            else if (insideGate.contains(myPosition()) &&
                     (!inventory.contains("Priest gown")) && (!equipment.contains("Priest gown")))
            {
                log("In gate");
                walking.webWalk(dressShop);
            }
            else if (dressShop.contains(myPosition()) &&
                     (!inventory.contains("Priest gown")) && (!equipment.contains("Priest gown")))
            {
                log("In shop");
                talker.TalkandWait("Asyff", "Priest Gown");
            }
            else if (inventory.contains("Priest gown"))
            {
                log("putting on gown");
                inventory.getItem("Priest gown").interact();
            }
            else if (equipment.contains("Priest gown") && (!varrockInn.contains(myPosition())))
            {
                log("going to inn");
                walking.webWalk(varrockInn);
            }
            else if (varrockInn.contains(myPosition()))
            {
                log("getting back items");
                if (!inventory.contains(415))
                {
                    log("first");
                    talker.TalkandWait(1104);
                    log(configs.get(68));
                    sleep(random(1800, 2400));
                }
                else if (!inventory.contains(416))
                {
                    log("second");
                    talker.TalkandWait(1106);
                    log(configs.get(68));
                    sleep(random(1800, 2400));
                }
                else if (!inventory.contains(417))
                {
                    log("third");
                    talker.TalkandWait(1108);
                    log(configs.get(68));
                    sleep(random(1800, 2400));
                }
            }
            case 14:
                log("14");
                if (insideGate.contains(myPosition()))
                {
                    inventory.getItem(8011).interact();
                    sleep(random(4200,4800));
                }
                else if (ElenaArea.contains(myPosition()))
                {
                    talker.TalkandWait("Elena");
                }
                else if (ArdougneArea.contains(myPosition()))
                {
                    walking.webWalk(ElenaArea);
                }
            case 15:
                if (kingRoom.contains(myPosition()))
                {
                    talker.TalkandWait("King Lathas", "Understand");
                }
                else
                {
                    walking.webWalk(kingRoom);
                }
            case 16:
                stop(); // quest completed.

        }
        return random(1200, 1800);
    }


}
