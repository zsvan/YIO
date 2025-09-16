package net.yio;

import net.yio.init.YioModGameRules;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;

@Mod.EventBusSubscriber
public class ArrowTimeLoop {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		Player player = (Player) event.player;
		Level world = event.player.level();
		if (world.getLevelData().getGameRules().getBoolean(YioModGameRules.KEEP_ARROWS)) {
			if (event.phase == TickEvent.Phase.END) {
				if (player == null) {
					return;
				}
				if (!world.isClientSide()) {
					LivingEntity liv_ = (LivingEntity) player;
					liv_.removeArrowTime = 100;
				}
			}
		}
	}
}
