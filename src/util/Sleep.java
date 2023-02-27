package util;

import org.osbot.rs07.utility.ConditionalSleep;

import java.util.function.BooleanSupplier;

public class Sleep extends ConditionalSleep
{
    private final BooleanSupplier condition;

    public Sleep(final BooleanSupplier condition, final int timeout)
    {
        super(timeout);
        this.condition = condition;
    }

    @Override
    public final boolean condition()
    {
        return condition.getAsBoolean();
    }

    public static void sleepUntil(final BooleanSupplier condition, final int timeout)
    {
        new Sleep(condition, timeout).sleep();
    }
    public static void sleepUntil(final boolean condition, final int timeout)
    {
        sleepUntil(() -> condition, timeout);
    }
}
