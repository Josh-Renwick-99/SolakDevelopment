package net.runelite.client.plugins.legacy;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("legacy")
public interface LegacyConfig extends Config {

    @ConfigItem(
            keyName = "useNewFunctionKeys",
            name = "RS2 Function Keys",
            description = "Switches the function keybinds to RS2 function keybinds when enabled.",
            position = 0
    )
    default boolean useNewFunctionKeys() {
        return true;
    }

    @ConfigItem(
            keyName = "useNewHealthBars",
            name = "RS2 Health Bars",
            description = "Switches the health bar style to RS2 style when enabled.",
            position = 1
    )
    default boolean useNewHealthBars() {
        return true;
    }

    @ConfigItem(
            keyName = "useNewHitsplats",
            name = "RS2 Hitsplats",
            description = "Switches the hitsplats style to RS2 style when enabled.",
            position = 2
    )
    default boolean useNewHitsplats() {
        return true;
    }

    @ConfigItem(
            keyName = "useNewDamage",
            name = "RS2 (x10) Damage",
            description = "Switches the damage style to RS2 style when enabled.",
            position = 3
    )
    default boolean useNewDamage() {
        return true;
    }

    @ConfigItem(
            keyName = "useNewCursors",
            name = "RS2 Cursors",
            description = "Switches the cursors style to RS2 style when enabled.",
            position = 4
    )
    default boolean useNewCursors() {
        return true;
    }

    @ConfigItem(
            keyName = "useNewGameframe",
            name = "RS2 Gameframe",
            description = "Switches the gameframe style to RS2 style when enabled.",
            position = 5
    )
    default boolean useNewGameframe() {
        return true;
    }

    @ConfigItem(
            keyName = "displayNameAboveHead",
            name = "Name Above Head",
            description = "Displays the character display name above their head when enabled.",
            position = 6
    )
    default boolean displayNameAboveHead() {
        return true;
    }

    @ConfigItem(
            keyName = "displayHitpointsAboveHead",
            name = "Hitpoints Above Head",
            description = "Displays the character hitpoints above their head when enabled.",
            position = 7
    )
    default boolean displayHitpointsAboveHead() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightNameInChat",
            name = "Highlight Name In Chat",
            description = "Highlight the character name when it is mentioned in the chat box.",
            position = 8
    )
    default boolean highlightNameInChat() {
        return true;
    }

    @ConfigItem(
            keyName = "enableFog",
            name = "Enable Fog",
            description = "Enable the FOG graphical feature.",
            position = 9
    )
    default boolean enableFog() {
        return true;
    }

    @ConfigItem(
            keyName = "enableParticles",
            name = "Enable Particles",
            description = "Enable the particles graphical feature.",
            position = 10
    )
    default boolean enableParticles() {
        return true;
    }

    @ConfigItem(
            keyName = "displayNameAboveGroundItems",
            name = "Name Above Ground Items",
            description = "Displays the ground item name above the ground item when enabled.",
            position = 11
    )
    default boolean displayNameAboveGroundItems() {
        return true;
    }
}
