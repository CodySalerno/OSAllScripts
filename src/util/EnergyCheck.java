package util;
import org.osbot.rs07.script.MethodProvider;

public class EnergyCheck //extends MethodProvider
{
    private final MethodProvider methods;

    public EnergyCheck(final MethodProvider methods)
    {
        this.methods = methods;
    }

    String[] Potions = {"Stamina potion(4)", "Stamina potion(3)", "Stamina potion(2)", "Stamina potion(1)"};

    public void Stamina()
    {
        methods.log("in stamina checker.");
        for (int i = 0; i < Potions.length; i++)
        {
            if (methods.inventory.contains("Stamina potion(1)"))
            {
                methods.log("this works");
                methods.inventory.getItem(Potions[i]).interact();
            }
            else if (methods.inventory.contains("Stamina potion(2)"))
            {
                methods.log("this works");
                methods.inventory.getItem(Potions[i]).interact();
            }
            else if (methods.inventory.contains("Stamina potion(3)"))
            {
                methods.log("this works");
                methods.inventory.getItem(Potions[i]).interact();
            }
            else if (methods.inventory.contains("Stamina potion(4)"))
            {
                methods.log("this works");
                methods.inventory.getItem(Potions[i]).interact();
            }
        }
    }
}