
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.yio.init;

import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.level.GameRules;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class YioModGameRules {
	public static final GameRules.Key<GameRules.BooleanValue> KEEPARROWS = GameRules.register("keepArrows", GameRules.Category.MISC,
			GameRules.BooleanValue.create(true));
}
