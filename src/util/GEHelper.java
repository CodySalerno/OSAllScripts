package util;

import org.osbot.rs07.api.GrandExchange;
import org.osbot.rs07.script.MethodProvider;

public class GEHelper
{
    private final MethodProvider methods;

    public GEHelper(final MethodProvider methods)
    {
        this.methods = methods;
    }

    public int offersPending()
    {
        int offers = 0;
        GrandExchange.Status[] offerList = {null, null, null, null, null, null, null, null};
        offerList[0] = methods.getGrandExchange().getStatus(GrandExchange.Box.BOX_1);
        offerList[1] = methods.getGrandExchange().getStatus(GrandExchange.Box.BOX_2);
        offerList[2] = methods.getGrandExchange().getStatus(GrandExchange.Box.BOX_3);
        offerList[3] = methods.getGrandExchange().getStatus(GrandExchange.Box.BOX_4);
        offerList[4] = methods.getGrandExchange().getStatus(GrandExchange.Box.BOX_5);
        offerList[5] = methods.getGrandExchange().getStatus(GrandExchange.Box.BOX_6);
        offerList[6] = methods.getGrandExchange().getStatus(GrandExchange.Box.BOX_7);
        offerList[7] = methods.getGrandExchange().getStatus(GrandExchange.Box.BOX_8);
        for (GrandExchange.Status status : offerList)
        {
            if (status != GrandExchange.Status.EMPTY)
            {
                offers += 1;
            }
        }
        return offers;
    }
}
