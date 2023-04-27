import com.sun.org.apache.xpath.internal.objects.XNull;
import org.osbot.rs07.api.Dialogues;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import util.*;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


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
    Area marloHouse = new Area(3237,3469,3243,3478);
    Area yarloHouse = new Area(3236,3391,3248,3399);
    Area sawMill = new Area(3294,3479,3310,3492);
    boolean supplied = false;


    public void onStart() throws InterruptedException {
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
        log("We are in loop");
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
    }

    private void farm()
    {

    }


}