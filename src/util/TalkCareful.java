package util;

import org.osbot.rs07.script.Script;


public class TalkCareful extends Script
{
    @Override
    public int onLoop()
    {
        return 0;
    }

    public boolean TalkandWait(String npc, String option)
    {
        npcs.closest(npc).interact(option);
        Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 6000);
        return dialogues.isPendingContinuation();
    }
    public  boolean TalkandWait(String npc)
    {

        npcs.closest(npc).interact("Talk-to");
        Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 6000);
        return dialogues.isPendingContinuation();
    }
    public boolean TalkandWait(int id)
    {
        npcs.closest(id).interact("Talk-to");
        Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 6000);
        return dialogues.isPendingContinuation();
    }
    public boolean TalkandWait(int id, String option)
    {
        npcs.closest(id).interact(option);
        Sleep.sleepUntil(() -> dialogues.isPendingContinuation(), 6000);
        return dialogues.isPendingContinuation();
    }
}
