package net.zsvan.yio;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.player.Player;

@EventBusSubscriber(modid = YioMod.MODID)
public class ArrowTrackingEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (!(event.getSource().getDirectEntity() instanceof Arrow arrow))
            return;
        ArrowTracker.storeArrow(player, arrow);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ArrowTracker.clearPlayerArrows(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        ArrowTracker.clearPlayerArrows(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        ArrowTracker.clearPlayerArrows(event.getEntity());
    }
}
