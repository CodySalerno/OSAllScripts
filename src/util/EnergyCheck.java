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
        methods.log("in stamina checker.");
        for (String potion : Potions) {
            if (methods.inventory.contains(potion)) {
                methods.log("this works");
                methods.inventory.getItem(potion).interact();
                break;
            }
        }
    }
}