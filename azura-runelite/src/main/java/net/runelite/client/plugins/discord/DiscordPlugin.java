package net.runelite.client.plugins.discord;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

import java.awt.image.BufferedImage;

@PluginDescriptor(
        name = "Icons",
        description = "Shows the icons in the title bar",
        tags = {"action", "activity", "external", "integration", "status"}
)
@Slf4j
public class DiscordPlugin extends Plugin {

    @Inject
    private ClientToolbar clientToolbar;

    private NavigationButton discordButton;
    private NavigationButton storeButton;

    @Override
    protected void startUp() throws Exception {
        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "discord.png");

        discordButton = NavigationButton.builder()
            .tab(false)
            .tooltip("Join Discord")
            .icon(icon)
            .onClick(() -> LinkBrowser.browse(RuneLiteProperties.getDiscordInvite()))
            .build();
        
        clientToolbar.addNavigation(discordButton);
        
        final BufferedImage icon2 = ImageUtil.getResourceStreamFromClass(getClass(), "store.png");

        storeButton = NavigationButton.builder()
            .tab(false)
            .tooltip("Visit Store")
            .icon(icon2)
            .onClick(() -> LinkBrowser.browse("https://Solak.io/store"))
            .build();
        
        clientToolbar.addNavigation(storeButton);
    }

    @Override
    protected void shutDown() throws Exception {

        clientToolbar.removeNavigation(discordButton);
        clientToolbar.removeNavigation(storeButton);
    }
}
