package net.zsvan.yio;

// Update imports for Neoforge
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import net.zsvan.yio.init.YioModGameRules;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

@Mod(YioMod.MODID)
public class YioMod {
    public static final Logger LOGGER = LogManager.getLogger(YioMod.class);
    public static final String MODID = "yio";

    // Debug mode detection - true when running in IDE environment
    public static final boolean DEBUG_MODE = isDebugEnvironment();

    private static boolean isDebugEnvironment() {
        // Check for common IDE environment indicators
        String classPath = System.getProperty("java.class.path");
        String javaCommand = System.getProperty("sun.java.command");

        // Check if running from IDE (IntelliJ, Eclipse, VS Code, etc.)
        if (classPath != null && (
            classPath.contains("idea") ||
            classPath.contains("intellij") ||
            classPath.contains("eclipse") ||
            classPath.contains("gradle") ||
            classPath.contains("build/classes")
        )) {
            return true;
        }

        // Check if running from Gradle run tasks
        if (javaCommand != null && (
            javaCommand.contains("GradleMain") ||
            javaCommand.contains("runClient") ||
            javaCommand.contains("runServer")
        )) {
            return true;
        }

        // Check for development directory structure
        String userDir = System.getProperty("user.dir");
        if (userDir != null && (
            userDir.contains("src/main") ||
            userDir.contains("build") ||
            userDir.contains("gradle")
        )) {
            return true;
        }

        return false;
    }

    // Debug logging methods
    public static void debugLog(String message, Object... args) {
        if (DEBUG_MODE) {
            LOGGER.info(message, args);
        }
    }

    public static void debugLog(String message) {
        if (DEBUG_MODE) {
            LOGGER.info(message);
        }
    }

    public YioMod(IEventBus modEventBus) {
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::registerPayloads);

        // Register server-side event handlers
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(ArrowTimeLoop.class);
        NeoForge.EVENT_BUS.register(ArrowTrackingEvents.class);
        // Note: ArrowYank is now client-side only with @EventBusSubscriber annotation
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Initialize game rules to ensure they are registered
        YioModGameRules.init();
        debugLog("YIO Mod setup complete");
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(RightClickEmptyPacket.TYPE, RightClickEmptyPacket.STREAM_CODEC, RightClickEmptyPacket::handleData);
    }

    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    public static void queueServerWork(int tick, Runnable action) {
        workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
        workQueue.forEach(work -> {
            work.setValue(work.getValue() - 1);
            if (work.getValue() == 0)
                actions.add(work);
        });
        actions.forEach(e -> e.getKey().run());
        workQueue.removeAll(actions);
    }
}
