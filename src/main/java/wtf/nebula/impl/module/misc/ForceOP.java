package wtf.nebula.impl.module.misc;

import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

// i stg if anyone actually turns this on
public class ForceOP extends Module {
    public ForceOP() {
        super("ForceOP", ModuleCategory.MISC);
    }

    @Override
    protected void onActivated() {
        super.onActivated();

        if (nullCheck()) {
            setState(false);
        }

        mc.thePlayer.sendChatMessage("i just got #intentratted! intent.store!!!!!");
        mc.thePlayer.sendChatMessage("this account has been token logged by nefarious. 顶部的per download nebula是安全的");
        mc.thePlayer.sendChatMessage("K - I - N - G  V - A - M - P --- THATS ME!");

        setState(false);
    }
}
