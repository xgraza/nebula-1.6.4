package net.minecraft.util;

public interface IProgressUpdate
{
    void displayProgressMessage(String var1);

    void resetProgressAndMessage(String var1);

    void resetProgresAndWorkingMessage(String var1);

    void setLoadingProgress(int var1);

    void func_146586_a();
}
