package net.yio.init;

import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.level.GameRules;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class YioModGameRules {
	public static final GameRules.Key<GameRules.BooleanValue> KEEP_ARROWS = GameRules.register("keepArrows", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
}
