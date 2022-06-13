package util;

import org.osbot.rs07.script.MethodProvider;

public class EnergyCheck extends MethodProvider
{
    String[] Potions = {"Stamina potion(4)", "Stamina potion(3)", "Stamina potion(2)", "Stamina potion(1)"};
    public void Stamina()
    {
        for (int i = 0; i < Potions.length; i++)
        {
            if (inventory.contains(Potions[i]))
            {
                inventory.getItem(Potions[i]).interact();
            }
        }
    }
}


