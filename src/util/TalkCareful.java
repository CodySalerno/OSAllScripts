package util;

import org.osbot.rs07.script.MethodProvider;


public class TalkCareful
{
    private final MethodProvider methods;
    public TalkCareful(final MethodProvider methods)
    {
        this.methods = methods;
    }

    public void TalkandWait(String npc, String ... option) throws InterruptedException
    {
        methods.log("Talking npc with options");
        methods.npcs.closest(npc).interact();
        Sleep.sleepUntil(() -> methods.dialogues.isPendingContinuation() || methods.dialogues.isPendingOption(), 6000);
        if (methods.dialogues.isPendingContinuation() || methods.dialogues.isPendingOption())
        {
            methods.dialogues.completeDialogue(option);
        }
    }

    public void TalkandWait(String npc) throws InterruptedException
    {
        methods.log("talking npc no options");
        methods.npcs.closest(npc).interact("Talk-to");
        Sleep.sleepUntil(() -> methods.dialogues.isPendingContinuation(), 6000);
        if (methods.dialogues.isPendingContinuation())
        {
            methods.dialogues.completeDialogue();
        }
    }

    public void TalkandWait(int id) throws InterruptedException
    {
        methods.log("talking id no options ");
        methods.npcs.closest(id).interact("Talk-to");
        Sleep.sleepUntil(() -> methods.dialogues.isPendingContinuation(), 6000);
        if (methods.dialogues.isPendingContinuation())
        {
            methods.dialogues.completeDialogue();
        }
    }

    public void TalkandWait(int id, String option) throws InterruptedException
    {
        methods.log("talking id with options");
        methods.npcs.closest(id).interact();
        Sleep.sleepUntil(() -> methods.dialogues.isPendingContinuation() || methods.dialogues.isPendingOption(), 6000);
        if (methods.dialogues.isPendingContinuation() || methods.dialogues.isPendingOption())
        {
            methods.dialogues.completeDialogue(option);
        }
    }
}
