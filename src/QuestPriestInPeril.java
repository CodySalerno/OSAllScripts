import javafx.geometry.Pos;
import org.osbot.P;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.event.WalkingEvent;
import util.constantVariables;
import util.Sleep;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;


/*
START IN VARROCK
ONLY START THIS SCRIPT WITH BRONZE DART (P+) 400+   (5628)
3K BRONZE DARTS (806)
BUCKET (1925)
50+ PURE ESS IN BANK (7936)
varrock tabs (8007)
400 mind runes, 1200 fire runes air of staff
lobster 20+
 */
@ScriptManifest(name = "PriestInPerild", author = "Iownreality1", info = "Smelts Cannon Balls", version = 0.1, logo = "")
public class QuestPriestInPeril extends Script
{
    boolean bronzeDartp = true;
    //Entity door = objects.closest(new Area(3217,3471,3219,3473),"Door");
    //Entity gate = objects.closest(new Area(3318,3466,3320,3469),"Gate");
    //Entity largeDoor = objects.closest(new Area(3407,3487,3410,3490),"Large door");
    Position guardianInstance;
    Area guardianArea;

    public int onLoop() throws InterruptedException
    {
        if (configs.get(302) == 0)
        {
            if (!inventory.equipment.contains(5628) && !inventory.contains(806) && !inventory.contains(1925) && !inventory.contains(8007))
            {
                if (!constantVariables.westBank.contains(myPosition()))
                {
                    log("Walk to bank booth");
                    walking.webWalk(constantVariables.westBank);
                    sleep(random(1800,2400));
                }
                if (constantVariables.westBank.contains(myPosition()))
                {
                    log("Opening bank");
                    objects.closest("Bank booth").interact("Bank");
                    sleep(random(1800,2400));
                }
                if (bank.isOpen())
                {
                    log("getting bronze dart, bronze dart (p+) and bucket from bank");
                    bank.withdrawAll(5628);
                    sleep(random(1800,2400));
                    bank.withdrawAll(806);
                    sleep(random(1800,2400));
                    bank.withdraw(1925,1);
                    sleep(random(1800,2400));
                    bank.withdrawAll(8007);
                    sleep(random(1800,2400));
                    bank.close();
                    sleep(random(1800,2400));
                }
            }
            else
            {
                if (!inventory.equipment.contains(5628))
                {
                    inventory.getItem(5628).interact("Wield");
                    sleep(random(1800,2400));
                }
                if (!constantVariables.nearRoald.contains(myPosition()))
                {
                    walking.webWalk(constantVariables.nearRoald);
                    sleep(random(1800,2400));
                }
                else
                {
                    Entity door = objects.closest(new Area(3217,3471,3219,3473),"Door");
                    if (door.hasAction("Open") && constantVariables.nearRoald.contains(myPosition()))
                    {
                        door.interact("Open");
                        sleep(random(1800,2400));
                    }
                    npcs.closest("King Roald").interact("Talk-to");
                    Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 6000);
                    dialogues.completeDialogueU("Yes.", "I'm looking for a quest!", "I'll get going");
                    sleep(random(1800,2400));
                }


            }
        }
        if (configs.get(302) == 1)
        {
            Entity door = objects.closest(new Area(3217,3471,3219,3473),"Door");
            if (door.hasAction("Open") && constantVariables.nearRoald.contains(myPosition()))
            {
                log("opening door");
                door.interact("Open");
                sleep(random(1800,2400));
            }
            if (constantVariables.nearRoald.contains(myPosition()))
            {
                log("Walk to temple");
                walking.webWalk(constantVariables.gateToTemple);
            }
            if (constantVariables.gateToTemple.contains(myPosition()))
            {
                Entity gate = objects.closest(new Area(3318,3466,3320,3469),"Gate");
                if (gate.hasAction("Open") && constantVariables.nearRoald.contains(myPosition()))
                {
                    log("open gate");
                    gate.interact("Open");
                    sleep(random(1800,2400));
                }
                else
                {
                    log("walk to temple");
                    walking.webWalk(constantVariables.outsideTemple);
                    sleep(random(1800,2400));
                }
            }
            if (constantVariables.outsideTemple.contains(myPosition()))
            {
                log("open large door");
                objects.closest("Large door").interact("Open");
                Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 6000);
                log("complete dialogue");
                dialogues.completeDialogueU("Roald sent me to check on Drezel.", "Sure. I'm a helpful person!");
                sleep(random(1800,2400));

            }
        }
        if (configs.get(302) == 2)
        {
            if (constantVariables.outsideTemple.contains(myPosition()))
            {
                WalkingEvent myEvent = new WalkingEvent(new Position(3405,3488,0));
                myEvent.setMinDistanceThreshold(0);
                log("move to walkable trapdoor spot");
                execute(myEvent);
                log("walk to trapdoor");
                walking.webWalk(constantVariables.byTrapDoor);
                sleep(random(1800,2400));
            }
            if (constantVariables.byTrapDoor.equals(myPosition()))
            {
                log("open trapdoor");
                objects.closest("Trapdoor").interact("Open");
                sleep(random(1800,2400));
                log("climb down trap door");
                objects.closest("Trapdoor").interact("Climb-down");
                sleep(random(1800,2400));
                if (dialogues.isPendingContinuation())
                {
                    log("complete dialogue");
                    dialogues.completeDialogue("Yes.");
                    sleep(random(1800,2400));
                    guardianInstance = myPosition();
                    guardianInstance = new Position((guardianInstance.getX()+1),guardianInstance.getY(),guardianInstance.getZ());
                    guardianArea = myPosition().getArea(20);
                }
            }
            if (guardianArea.contains(myPosition()))
            {
                log("attack dog");
                npcs.closest("Temple guardian").interact("Attack");
                sleep(random(1800,2400));
                WalkingEvent myEvent = new WalkingEvent(guardianInstance);
                myEvent.setMinDistanceThreshold(0);
                log("move to safespot");
                execute(myEvent);
                sleep(random(1800,2400));
                log("attack dog");
                npcs.closest("Temple guardian").interact("Attack");
                while(npcs.closest("Temple guardian").isHitBarVisible() && guardianInstance.equals(myPosition()))
                {
                    if (dialogues.isPendingContinuation())
                    {
                        log("complete dialogue");
                        dialogues.completeDialogueU();
                        sleep(random(1800,2400));
                    }
                    if (bronzeDartp)
                    {
                        if (inventory.equipment.getItem(5628).getAmount() < 50)
                        {
                            log("equip non poison dart");
                            inventory.getItem(806).interact("Wield");
                            sleep(random(1800,2400));
                            bronzeDartp = false;
                        }
                    }
                }
            }
        }
        if (configs.get(302) == 3)
        {

            if (guardianArea.contains(myPosition()))
            {
                log("Climb up ladder");
                objects.closest("Ladder").interact("Climb-up");
                sleep(2400);
                log("walk to temple");
                walking.webWalk(constantVariables.outsideTemple);
            }
            if (constantVariables.outsideTemple.contains(myPosition()))
            {
                log("open large door");
                objects.closest("Large door").interact("Open");
                sleep(random(1800,2400));
            }
            if (dialogues.isPendingContinuation())
            {
                log("Complete dialogue");
                dialogues.completeDialogueU();
                sleep(random(1800,2400));
                log("Complete teleport to var");
                inventory.getItem(8007).interact("Break");
            }
            if (constantVariables.varTeleport.contains(myPosition()))
            {
                log("walk to roald");
                walking.webWalk(constantVariables.nearRoald);
                sleep(random(1800,2400));
            }
            Entity door = objects.closest(new Area(3217,3471,3219,3473),"Door");
            if (door.hasAction("Open") && constantVariables.nearRoald.contains(myPosition()))
            {
                log("open door near roald");
                door.interact("Open");
                sleep(random(1800,2400));
            }
            else
            {
                log("speak to roald");
                npcs.closest("King Roald").interact("Talk-to");
                Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 6000);
                log("complete roald dialogue");
                dialogues.completeDialogueU();
                sleep(random(1800,2400));
            }

        }
        if (configs.get(302) == 4)
        {
            if (constantVariables.nearRoald.contains(myPosition()))
            {
                log("walking to eastbank");
                walking.webWalk(constantVariables.eastBank);
                sleep(random(1800,2400));
            }
            if (constantVariables.eastBank.contains(myPosition()) && !bank.isOpen() && !inventory.contains("lobster") && !inventory.contains("Mind rune")
                        && !inventory.contains("Fire rune") && !inventory.equipment.contains("Staff of air"))
            {
                log("opening bank");
                objects.closest("Bank booth").interact("Bank");
                sleep(random(1800,2400));
            }
            if (bank.isOpen())
            {
                bank.withdraw("Staff of air", 1);
                sleep(random(1800,2400));
                bank.withdrawAll("Fire rune");
                sleep(random(1800,2400));
                bank.withdrawAll("Mind rune");
                sleep(random(1800,2400));
                bank.withdraw("Lobster",20);
                sleep(random(1800,2400));
                bank.close();
                sleep(random(1800,2400));
                inventory.getItem("Staff of air").interact("Wield");
                sleep(random(1800,2400));
            }
            if (constantVariables.eastBank.contains(myPosition()) && inventory.equipment.contains("Staff of air"))
            {
                if (configs.get(43) != 4)
                {
                    widgets.get(548,69).interact();
                    sleep(3000);
                    widgets.get(593,26,4).interact();
                    sleep(3000);
                    widgets.get(201,1,4).interact();
                    sleep(3000);
                }

            }

            /*
            config 302 = 4
            head to temple
            check if gate is closed
            head to temple
            Open Large door
            Climb-up Staircase
            Attack   3846
            Sleep 2400
            move to 3413 3486 1 (ignore distance)
            sleep(3000
            move to 3418, 3486, 1
            eat lobster
            sleep 3000
            move 3417,3490, 1
            eat lobster
            sleep 3000
            if npc 3486.position = 3417 3488 1
            attack 3486
            complete dialogue (levling up)
            pick up Golden key
            Climb-up Ladder
            talk to drezel "So, what now?" "Yes, of course."
             */
            log("config is now 4 we don't have steps for this.");
        }
        return random(800,1200);
    }
}
