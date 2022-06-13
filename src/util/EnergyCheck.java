package util;

import org.osbot.rs07.script.Script;

public class EnergyCheck extends Script
{
    String[] Potions = {"Stamina potion(4)", "Stamina potion(3)", "Stamina potion(2)", "Stamina potion(1)"};
    @Override
    public int onLoop() throws InterruptedException
    {
        return 0;
    }
    public void Stamina()
    {
        log("Can get here");
    }
}
