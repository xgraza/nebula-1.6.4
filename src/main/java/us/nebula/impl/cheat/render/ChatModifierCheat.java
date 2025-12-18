/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.impl.cheat.render;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;

/**
 * @author xgraza
 * @since 04/07/25
 */
@CheatManifest(name = "ChatModifier",
        description = "Modifies how the chat looks",
        category = CheatCategory.RENDER)
public final class ChatModifierCheat extends Cheat
{
    @CheatInstance
    public static ChatModifierCheat INSTANCE;

    public final Setting<Boolean> transparentSetting = new Setting<>(
            "Transparent", false);
    public final Setting<Double> animateSpeed = new Setting<>(
            "Animation Speed", 0.8, 0.0, 1.0, 0.05);
    public final Setting<Boolean> timestampSetting = new Setting<>(
            "Timestamp", false);
    public final Setting<Boolean> playerHeadsSetting = new Setting<>(
            "Player Heads", false);
    public final Setting<Boolean> highlightSelfSetting = new Setting<>(
            "Highlight Self", true);
    public final Setting<Boolean> highlightFriendsSetting = new Setting<>(
            "Highlight Friends", true);
    public final Setting<Boolean> infiniteChatSetting = new Setting<>(
            "Infinite Chat", true);
}
