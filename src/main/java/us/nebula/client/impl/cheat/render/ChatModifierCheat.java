/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.client.impl.cheat.render;

import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatInstance;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;

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
    public final Setting<Integer> animateSpeed = new Setting<>(
            "Animation Time", 200, 0, 1000, 50);
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
