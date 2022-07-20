import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import util.FormattingForPaint;
import util.Sleep;

import javax.swing.*;
import java.awt.*;

@ScriptManifest(name = "Herbing", author = "Iownreality1", info = "Mines at motherload mine", version = 0.1, logo = "")
public class Herbing extends Script
{

    String item;
    String item2;
    String item3;
    String runningTime;
    Font font = new Font("Open Sans", Font.BOLD, 18);
    long startTime;
    int startAmount;
    int currentAmount;
    long runningTimeInt;
    boolean startAmountFound = false;

    public void onStart() throws InterruptedException {
        JFrame jFrame = new JFrame();
        String getMessage = JOptionPane.showInputDialog(jFrame, "First Item?");
        item = getMessage;
        JFrame jFrame2 = new JFrame();
        String getMessage2 = JOptionPane.showInputDialog(jFrame2, "Second Item");
        item2 = getMessage2;
        JFrame jFrame3 = new JFrame();
        String getMessage3 = JOptionPane.showInputDialog(jFrame3, "What is being made?");
        item3 = getMessage3;
        startTime = System.currentTimeMillis();
        if (item == null || item2 == null)
        {
            stop();
        }
    }

    public void onPaint(final Graphics2D g)
    {
        runningTime = FormattingForPaint.formatTime(System.currentTimeMillis()-startTime);
        runningTimeInt = 3600000/(System.currentTimeMillis()-startTime);
        g.setFont(font);
        g.setColor(Color.green);
        g.drawString("Running Time: " + runningTime,10,250);
        g.drawString("Potions Made  " + (currentAmount-startAmount) , 10,270);
        g.drawString("Potions per Hour  " + ((currentAmount-startAmount)*runningTimeInt) , 10,290);
    }


    @Override
    public int onLoop() throws InterruptedException
    {
        if (!inventory.contains(item) || !inventory.contains(item2) || inventory.getAmount(item) <= 2 || inventory.getAmount(item2) <= 2)
        {
            objects.closest("Grand Exchange booth").interact("Bank");
            Sleep.sleepUntil(() -> (bank.isOpen()), 10000);
            if (bank.isOpen())
            {
                if (!startAmountFound)
                {
                    log("Changing start amount");
                    startAmount = (int)bank.getAmount(item3);
                    startAmountFound = true;
                }
                bank.depositAll();
                currentAmount = (int)bank.getAmount(item3);
                if (!inventory.contains(item) && !inventory.contains(item2))
                {
                    bank.withdraw(item,14);
                    bank.withdraw(item2, 14);
                    bank.close();
                }
            }
        }
        if (inventory.contains(item2) && inventory.contains(item))
        {
            inventory.getItem(item2).interact("Use");
            inventory.getItem(item).interact("Use");
            Sleep.sleepUntil(() -> (widgets.getWidgetContainingText("How many do you wish to make?") != null), 10000);
            widgets.get(270,14,38).interact();
            Sleep.sleepUntil(() -> (inventory.getAmount(item2) <= 2 || inventory.getAmount(item) <= 2 || dialogues.isPendingContinuation()), 20000);
        }
        if (dialogues.isPendingContinuation())
        {
            dialogues.completeDialogue();
        }

        return (random(400,600));
    }
}
