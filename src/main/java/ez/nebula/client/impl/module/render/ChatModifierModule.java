/*
 * Copyright (c) xgraza 2025
 */

package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 04/07/25
 */
@ModuleManifest(name = "ChatModifier",
        description = "Modifies how the chat looks",
        category = ModuleCategory.RENDER)
public final class ChatModifierModule extends Module
{
    @ModuleInstance
    public static ChatModifierModule INSTANCE;

    public final Setting<Boolean> transparentSetting = builder("Transparent", false)
            .setDescription("If to remove the rendered background from the chat line")
            .build();
    public final NumberSetting<Integer> animateSpeed = numberBuilder("Animation Time", 200)
            .setMin(0)
            .setMax(1000)
            .setScale(50)
            .setDescription("How long in seconds the chat line animation should be")
            .build();
    public final Setting<Boolean> timestampSetting = builder("Timestamp", false)
            .setDescription("If to show what time a message was sent")
            .build();
    public final Setting<Boolean> playerHeadsSetting = builder("Player Heads", false)
            .setDescription("If to render the player head along side with their message")
            .build();
    public final Setting<Boolean> highlightSelfSetting = builder("Highlight Self", true)
            .setDescription("If to highlight your name mentioned in chat")
            .build();
    public final Setting<Boolean> highlightFriendsSetting = builder("Highlight Friends", true)
            .setDescription("If to highlight your friends names mentioned in chat")
            .build();
    public final Setting<Boolean> infiniteChatSetting = builder("Infinite Chat", true)
            .setDescription("If to disable vanilla Minecraft chat clearing")
            .build();
}
