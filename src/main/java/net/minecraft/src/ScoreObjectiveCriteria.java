package net.minecraft.src;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ScoreObjectiveCriteria
{
    Map field_96643_a = new HashMap();
    ScoreObjectiveCriteria field_96641_b = new ScoreDummyCriteria("dummy");
    ScoreObjectiveCriteria deathCount = new ScoreDummyCriteria("deathCount");
    ScoreObjectiveCriteria playerKillCount = new ScoreDummyCriteria("playerKillCount");
    ScoreObjectiveCriteria totalKillCount = new ScoreDummyCriteria("totalKillCount");
    ScoreObjectiveCriteria health = new ScoreHealthCriteria("health");

    String func_96636_a();

    int func_96635_a(List var1);

    boolean isReadOnly();
}
