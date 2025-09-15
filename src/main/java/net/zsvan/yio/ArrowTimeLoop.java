package net.zsvan.yio;

import net.zsvan.yio.init.YioModGameRules;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;

public class ArrowTimeLoop {
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		Level world = player.level();

		// Safety check to ensure the game rule exists before accessing it
		GameRules.Key<GameRules.BooleanValue> keepArrowsRule = YioModGameRules.KEEP_ARROWS;
		if (keepArrowsRule == null || world.getLevelData().getGameRules().getRule(keepArrowsRule) == null) {
			return;
		}

		if (world.getLevelData().getGameRules().getBoolean(YioModGameRules.KEEP_ARROWS)) {
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
