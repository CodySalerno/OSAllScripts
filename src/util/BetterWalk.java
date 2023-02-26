package util;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.script.MethodProvider;

public class BetterWalk
{
    private final MethodProvider methods;

    public BetterWalk(final MethodProvider methods)
    {
        this.methods = methods;
    }

    public void MyWalkingEvent(int x, int y, int z)
    {
        WalkingEvent myEvent = new WalkingEvent(new Position(x,y,z));
        myEvent.setMinDistanceThreshold(0);
        methods.execute(myEvent);
    }
    public void MyWalkingEvent(Position pos)
    {
        WalkingEvent myEvent = new WalkingEvent(pos);
        myEvent.setMinDistanceThreshold(0);
        methods.execute(myEvent);
    }

}