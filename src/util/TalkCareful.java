package util;

import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;

import java.lang.reflect.Array;

public class TalkCareful
{
    private final MethodProvider methods;
    public TalkCareful(final MethodProvider methods)
    {
        this.methods = methods;
    }

    public boolean TalkandWait(String npc, String ... option) throws InterruptedException
    {
        methods.log("trying to talk 0");
        methods.npcs.closest(npc).interact();
        Sleep.sleepUntil(() -> methods.dialogues.isPendingContinuation() || methods.dialogues.isPendingOption(), 6000);
        if (methods.dialogues.isPendingContinuation() || methods.dialogues.isPendingOption())
        {
            methods.dialogues.completeDialogue(option);
        }
        return methods.dialogues.isPendingContinuation();
    }

    public boolean TalkandWait(String npc) throws InterruptedException
    {
        methods.log("trying to talk 1");
        methods.npcs.closest(npc).interact("Talk-to");
        Sleep.sleepUntil(() -> methods.dialogues.isPendingContinuation(), 6000);
        if (methods.dialogues.isPendingContinuation())
        {
            methods.dialogues.completeDialogue();
        }
        return methods.dialogues.isPendingOption() || methods.dialogues.isPendingContinuation();
    }

    public boolean TalkandWait(int id) throws InterruptedException
    {
        methods.log("trying to talk 2 ");
        methods.npcs.closest(id).interact("Talk-to");
        Sleep.sleepUntil(() -> methods.dialogues.isPendingContinuation(), 6000);
        if (methods.dialogues.isPendingContinuation())
        {
            methods.dialogues.completeDialogue();
        }
        return methods.dialogues.isPendingOption() || methods.dialogues.isPendingContinuation();
    }

    public boolean TalkandWait(int id, String option) throws InterruptedException
    {
        methods.log("trying to talk 3");
        methods.npcs.closest(id).interact();
        Sleep.sleepUntil(() -> methods.dialogues.isPendingContinuation() || methods.dialogues.isPendingOption(), 6000);
        if (methods.dialogues.isPendingContinuation() || methods.dialogues.isPendingOption())
        {
            methods.dialogues.completeDialogue(option);
        }
        return methods.dialogues.isPendingContinuation();
    }
}
