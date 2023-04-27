import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.script.MethodProvider;
import util.GEHelper;
import util.Sleep;
import util.Supply;
import util.TalkCareful;

public class DaddysHome
{

    boolean supplied = false;
    Area marloHouse = new Area(3237,3469,3243,3478);
    Area yarloHouse = new Area(3236,3391,3248,3399);
    Area sawMill = new Area(3294,3479,3310,3492);
    private final MethodProvider methods;

    public DaddysHome(final MethodProvider methods)
    {
        this.methods = methods;
    }

    public void CompleteDaddy(GEHelper geHelp, Supply supply, TalkCareful talker) throws InterruptedException {
        methods.log("In construction");
        int daddysHomeProg = methods.configs.get(393);
        methods.log(daddysHomeProg);
        if (!supplied && daddysHomeProg == 0)
        {
            int[] supplyId = {960, 8790, 1539, 2347, 8794, 8007, 12625, 12642};
            String[] supplyName = {"Plank", "Bolt of cloth", "Steel nails", "Hammer", "Saw", "Varrock teleport", "Stamina potion", "Lumberyard teleport"};
            int[] supplyPrice = {500, 1500, 50, 1000, 1000, 700, 10000, 10000};
            int[] supplyQuantity = {10, 5, 100, 1, 1, 20, 2, 2};
            supplied = supply.supply(supplyId, supplyName, supplyPrice, supplyQuantity, geHelp);
        }
        if (supplied && daddysHomeProg == 0)
        {
            methods.log("walking to marllo");
            methods.walking.webWalk(new Position(3240,3475, 0));
            MethodProvider.sleep(MethodProvider.random(2000,3000));
            if(marloHouse.contains(methods.myPosition()))
            {
                methods.log("talking to marrlo in his home");
                talker.TalkandWait("Marlo", "What kind of favour", "Tell me more about", "Tell me where he" );
                methods.log("finished talking");
            }
        }
        if (daddysHomeProg == 524288)
        {
            methods.log("second step of quest");
            if(!yarloHouse.contains(methods.myPosition()))
            {
                methods.log("walking to yarlo");
                methods.walking.webWalk(yarloHouse);
            }
            else
            {
                methods.log("inside yarlo talking to yarlo");
                talker.TalkandWait("Old Man Yarlo");
            }
        }
        if (daddysHomeProg == 1223328)
        {
            demolishItems(40224, "Demolish");
        }
        if (daddysHomeProg == 1231520)
        {
            demolishItems(40223, "Demolish");

        }
        if (daddysHomeProg == 1233568)
        {
            demolishItems(40300, "Demolish");
        }
        if (daddysHomeProg == 1233600)
        {
            demolishItems(40301, "Demolish");
        }
        if (daddysHomeProg == 1233728)
        {
            demolishItems(40302, "Demolish");
        }
        if (daddysHomeProg == 1234240)
        {
            demolishItems(40304, "Remove");
        }
        if (daddysHomeProg == 1365312)
        {
            demolishItems(40303, "Remove");
        }
        if (daddysHomeProg == 1398080 || daddysHomeProg == 1922368 || daddysHomeProg == 2446656)
        {
            inArea(yarloHouse);
            talker.TalkandWait("Old Man Yarlo", "Skip Yarlo's");
        }
        if (daddysHomeProg == 2970944 && methods.inventory.getAmount(24938) <3)
        {
            inArea(yarloHouse);
            methods.objects.closest(40214).interact("Search");
        }
        if (daddysHomeProg == 2970944 && methods.inventory.getAmount(24938) == 3)
        {
            if (!sawMill.contains(methods.myPosition()))
            {
                methods.inventory.getItem(12642).interact();
                MethodProvider.sleep(MethodProvider.random(4600,5400));
            }
            else
            {
                talker.TalkandWait("Sawmill operator", "I need some wax");
            }
        }
        if (daddysHomeProg == 3495232)
        {
            if (sawMill.contains(methods.myPosition()))
            {
                methods.inventory.getItem(8007).interact("Varrock");
                MethodProvider.sleep(MethodProvider.random(4600,5400));
            }
            inArea(yarloHouse);
            demolishItems(40223, "Build");
            buildItems(3495232);
        }
        if (daddysHomeProg == 3497280)
        {
            inArea(yarloHouse);
            demolishItems(40224, "Build");
            buildItems(3497280);
        }
        if (daddysHomeProg == 3505472)
        {
            inArea(yarloHouse);
            demolishItems(40300, "Build");
            buildItems(3505472);
        }
        if (daddysHomeProg == 3505504)
        {
            inArea(yarloHouse);
            demolishItems(40301, "Build");
            buildItems(3505504);
        }
        if (daddysHomeProg == 3505632)
        {
            inArea(yarloHouse);
            demolishItems(40302, "Build");
            buildItems(3505632);
        }
        if (daddysHomeProg == 3505632)
        {
            inArea(yarloHouse);
            demolishItems(40302, "Build");
            buildItems(3505632);
        }
        if (daddysHomeProg == 3506144)
        {
            inArea(yarloHouse);
            demolishItems(40303, "Build");
            buildItems(3506144);
        }
        if (daddysHomeProg == 3538912)
        {
            inArea(yarloHouse);
            demolishItems(40304, "Build");
            buildItems(3538912);
        }
        if (daddysHomeProg == 3669984)
        {
            inArea(yarloHouse);
            talker.TalkandWait("Old Man Yarlo");
        }
        if (daddysHomeProg == 5767136 || daddysHomeProg == 6291424)
        {
            inArea(marloHouse);
            talker.TalkandWait("Marlo", "Yeah");
        }
        if (daddysHomeProg == 7340000 && methods.inventory.contains(24940))
        {
            methods.inventory.getItem(24940).interact();
        }
    }

    private void demolishItems(int id, String action) throws InterruptedException {
        if (!yarloHouse.contains(methods.myPosition()))
        {
            methods.log("walking to yarlo");
            methods.walking.webWalk(yarloHouse);
        }
        else
        {
            if (methods.objects.closest(id) != null) //table2
            {
                methods.objects.closest(id).interact(action);
                MethodProvider.sleep(MethodProvider.random(1800,2400));
            }
        }
    }
    private void buildItems(int oldConfig)
    {
        Sleep.sleepUntil(() -> methods.widgets.getWidgets(458,2) != null, 4000);
        methods.log("pressing key");
        methods.getKeyboard().typeString("1");
        Sleep.sleepUntil(() -> methods.configs.get(393) != oldConfig, 4000);
    }

    private void inArea(Area area)
    {
        if (!area.contains(methods.myPosition()))
        {
            methods.walking.webWalk(area);
        }
    }

}
