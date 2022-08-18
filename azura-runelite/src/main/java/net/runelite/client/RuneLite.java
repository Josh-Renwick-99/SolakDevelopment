package net.runelite.client;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.rs.ClientLoader;
import net.runelite.client.ui.ClientUI;
import org.necrotic.client.Client;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.swing.*;
import java.io.File;
import java.util.Locale;

@Singleton
@Slf4j
public final class RuneLite {

    public static final File RUNELITE_DIR = new File(System.getProperty("user.home"), ".azura");
    public static final File CACHE_DIR = new File(RUNELITE_DIR, "cache");
    public static final File PLUGINS_DIR = new File(RUNELITE_DIR, "plugins");
    public static final File PROFILES_DIR = new File(RUNELITE_DIR, "profiles");
    public static final File SCREENSHOT_DIR = new File(RUNELITE_DIR, "screenshots");
    public static final File LOGS_DIR = new File(RUNELITE_DIR, "logs");
    public static final File DEFAULT_SESSION_FILE = new File(RUNELITE_DIR, "session");
    public static final File DEFAULT_CONFIG_FILE = new File(RUNELITE_DIR, "settings.properties");

    @Getter
    private static Injector injector;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private EventBus eventBus;

    @Inject
    private ConfigManager configManager;

    @Inject
    private ClientUI clientUI;

    @Inject
    @Nullable
    private Client client;

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);

        boolean developerMode = false;

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
        {
            log.error("Uncaught exception:", throwable);
            if (throwable instanceof AbstractMethodError) {
                log.error("Classes are out of date; Build with maven again.");
            }
        });

        PROFILES_DIR.mkdirs();

        try {
            ClientLoader clientLoader = new ClientLoader();

            injector = Guice.createInjector(new RuneLiteModule(
                    clientLoader,
                    developerMode,
                    DEFAULT_CONFIG_FILE));
            injector.getInstance(RuneLite.class).start();
        } catch (Exception e) {
            log.error("Failure during startup", e);
            System.exit(1);
        }
    }

    public void start() throws Exception {
        injector.injectMembers(client);
        configManager.load();
        pluginManager.setOutdated(false);
        pluginManager.loadCorePlugins();
        pluginManager.loadDefaultPluginConfiguration(null);
        clientUI.init();		eventBus.register(clientUI);
        eventBus.register(pluginManager);
        eventBus.register(configManager);
        pluginManager.startPlugins();
        clientUI.show();
    }
}
