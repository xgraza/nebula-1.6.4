package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableLevelWeather implements Callable
{
    final WorldInfo worldInfoInstance;

    CallableLevelWeather(WorldInfo par1WorldInfo)
    {
        this.worldInfoInstance = par1WorldInfo;
    }

    public String callLevelWeatherInfo()
    {
        return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", new Object[] {Integer.valueOf(WorldInfo.getRainTime(this.worldInfoInstance)), Boolean.valueOf(WorldInfo.getRaining(this.worldInfoInstance)), Integer.valueOf(WorldInfo.getThunderTime(this.worldInfoInstance)), Boolean.valueOf(WorldInfo.getThundering(this.worldInfoInstance))});
    }

    public Object call()
    {
        return this.callLevelWeatherInfo();
    }
}
