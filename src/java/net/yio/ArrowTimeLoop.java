package net.yio;

import net.yio.world.KeepArrowsGameRule;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;

public class ArrowTimeLoop {
	@Mod.EventBusSubscriber
	public static class Trigger {
		@SubscribeEvent
		public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
			PlayerEntity player = (PlayerEntity) event.player;
			World world = player.world;
			if (world.getWorldInfo().getGameRulesInstance().getBoolean(KeepArrowsGameRule.gamerule)) {
				if (event.phase == TickEvent.Phase.END) {
					if (player == null) {
						return;
					}
					if (!world.isRemote()) {
						LivingEntity liv_ = (LivingEntity) player;
						liv_.arrowHitTimer = 100;
					}
				}
			}
		}
	}
}
