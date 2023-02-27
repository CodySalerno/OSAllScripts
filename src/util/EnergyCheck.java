package util;
import org.osbot.rs07.script.MethodProvider;

public class EnergyCheck //extends MethodProvider
{
    private final MethodProvider methods;

    public EnergyCheck(final MethodProvider methods)
    {
        this.methods = methods;
    }

    String[] Potions = {"Stamina potion(1)", "Stamina potion(2)", "Stamina potion(3)", "Stamina potion(4)"};

    public void Stamina()
    {
        if (methods.settings.getRunEnergy() <= 20)
        {
            methods.log("Replenishing stamina.");
            for (String potion : Potions) // uses the lowest dose potion first
            {
                if (methods.inventory.contains(potion))
                {
                    methods.log("Drinking stamina");
                    methods.inventory.getItem(potion).interact();
                    break;
                }
            }
        }
        if (!methods.settings.isRunning())
        {
            methods.settings.setRunning(true);
        }
    }
}