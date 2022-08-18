package net.runelite.client.plugins.legacy;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.necrotic.Configuration;
import org.necrotic.client.Client;
import org.necrotic.client.constants.GameFrameConstants;

import javax.inject.Inject;

@PluginDescriptor(name = "Client Settings", loadWhenOutdated = true)
public class LegacyPlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private LegacyConfig config;

    @Override
    protected void startUp() throws Exception {
        syncConfigs();
    }

    @Override
    protected void shutDown() throws Exception {
        super.shutDown();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if ("legacy".contentEquals(configChanged.getGroup())) {
            syncConfigs();
        }
    }

    @Provides
    LegacyConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(LegacyConfig.class);
    }

    private void syncConfigs() {
        Configuration.NEW_FUNCTION_KEYS = config.useNewFunctionKeys();
        Configuration.NEW_HEALTH_BARS = config.useNewHealthBars();
        Configuration.NEW_HITMARKS = config.useNewHitsplats();
        Configuration.CONSTITUTION_ENABLED = config.useNewDamage();
        Configuration.NEW_CURSORS = config.useNewCursors();
        Configuration.DISPLAY_USERNAMES_ABOVE_HEAD = config.displayNameAboveHead();
        Configuration.DISPLAY_HP_ABOVE_HEAD = config.displayHitpointsAboveHead();
        Configuration.HIGHLIGHT_USERNAME = config.highlightNameInChat();
        GameFrameConstants.gameframeType = config.useNewGameframe() ? GameFrameConstants.GameFrameType.FRAME_554 : GameFrameConstants.GameFrameType.FRAME_525;
        Configuration.FOG_ENABLED = config.enableFog();
        Configuration.PARTICLES_ENABLED = config.enableParticles();
        Configuration.GROUND_TEXT = config.displayNameAboveGroundItems();
    }
}
