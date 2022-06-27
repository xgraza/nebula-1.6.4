package net.minecraft.src;

import paulscode.sound.SoundSystem;

class SoundManagerINNER1 implements Runnable
{
    final SoundManager theSoundManager;

    SoundManagerINNER1(SoundManager par1SoundManager)
    {
        this.theSoundManager = par1SoundManager;
    }

    public void run()
    {
        SoundManager.func_130080_a(this.theSoundManager, new SoundSystem());
        SoundManager.func_130082_a(this.theSoundManager, true);
    }
}
