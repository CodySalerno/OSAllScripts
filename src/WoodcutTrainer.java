import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;
import util.*;


public class WoodcutTrainer
{
    int[] axeLevels = {1,6,11,21,31,41,61};
    String[] Axes = {"Iron axe", "Steel axe", "Black axe", "Mithril axe", "Adamant axe", "Rune axe", "Dragon axe"};
    Area regularTree = new Area(1,2,3,4);
    Area oakTree = new Area(1,2,3,4);
    Area teakTree = new Area(1,2,3,4);
    Area[] trees = {};
    boolean supplied;

    private final MethodProvider methods;



    public WoodcutTrainer(final MethodProvider methods)
    {
        this.methods = methods;
    }

    public void TrainWoodcutting(GEHelper geHelp, Supply supplier, int goalLevel) throws InterruptedException {
        int currentLevel = methods.skills.getStatic(Skill.WOODCUTTING);
        String axe = Axes[0];
        for (int i=0; i < axeLevels.length; i++)
        {
            if (currentLevel >= axeLevels[i])
            {
                axe = Axes[i];
            }
        }

        supplied = methods.inventory.contains(axe);
        if (!supplied)
        {
            woodcutSupplier(axe, geHelp, supplier);
            return;
        }
        else
        {
            methods.log("Train in here");
        }
        methods.log("Done");
    }





    private void woodcutSupplier(String axe, GEHelper geHelp, Supply supplier) throws InterruptedException
    {
        if (methods.objects.closest("Bank booth") != null)
        {
            methods.objects.closest("Bank booth").interact("Bank");
            Sleep.sleepUntil(() -> methods.bank.isOpen(), 20000);
        }
        if (methods.bank.isOpen())
        {
            if (methods.bank.contains(axe))
            {
                methods.bank.withdraw(axe, 1);
                Sleep.sleepUntil(() -> methods.inventory.contains(axe), 1800);
            }
            else
            {
                int[] supplyID = {1349, 1353, 1361, 1355, 1357, 1359, 6739};
                String[] supplyName = Axes;
                int[] supplyPrice = {1000, 1000, 1000, 1000, 1000, 10000, 150000};
                int[] supplyQuantity = {0, 0, 0, 0, 0, 0, 0};
                int lower_count = -1;
                for (int i=0; i < axeLevels.length; i++)
                {
                    if (methods.skills.getStatic(Skill.WOODCUTTING) < axeLevels[i])
                    {
                        supplyQuantity[i] = 1;
                    }
                    else
                    {
                        lower_count++;
                    }
                }
                supplyQuantity[lower_count] = 1;

                boolean withdraw = false;
                boolean[] withdraw_noted = {};
                supplier.supply(supplyID, supplyName, supplyPrice, supplyQuantity,  geHelp, withdraw, withdraw_noted);
            }
        }
    }



}
