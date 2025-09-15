package net.zsvan.yio.init;

import net.minecraft.world.level.GameRules;

public class YioModGameRules {
    public static GameRules.Key<GameRules.BooleanValue> KEEP_ARROWS;
    public static GameRules.Key<GameRules.BooleanValue> YANKING_HURTS;

    public static void init() {
        KEEP_ARROWS = GameRules.register("keepArrows", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
        YANKING_HURTS = GameRules.register("yankingHurts", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    }
}
