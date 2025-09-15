/**
 * The code of this mod element is always locked.
 *
 * You can register new events in this class too.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser -> New... and make sure to make the class
 * outside net.yio as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 *
 * This class will be added in the mod root package.
*/
package net.yio;

import net.yio.init.YioModGameRules;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;

public class ArrowTimeLoop {
	@Mod.EventBusSubscriber
	public static class Trigger {
		@SubscribeEvent
		public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
			Player player = (Player) event.player;
			Level world = event.player.level;
			if (world.getLevelData().getGameRules().getBoolean(YioModGameRules.KEEPARROWS)) {
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
}
