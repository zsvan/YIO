package net.zsvan.yio;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.zsvan.yio.init.YioModGameRules;

public class ArrowTracker {
    private static final String ARROWS_TAG = "StoredArrows";
    private static final String INFINITY_TAG = "InfinityArrows";
    private static final int MAX_ARROWS = 10;

    public static void storeArrow(Player player, Arrow arrow) {
        YioMod.debugLog("storeArrow called for player: {}, arrow: {}", player.getName().getString(), arrow.getClass().getSimpleName());
        YioMod.debugLog("Arrow owner: {}", arrow.getOwner() != null ? arrow.getOwner().getName().getString() : "null");

        CompoundTag persistentData = player.getPersistentData();

        // Check if the arrow came from an Infinity bow or creative player
        boolean isInfinityArrow = checkInfinityArrow(arrow);
        YioMod.debugLog("Is infinity arrow: {}", isInfinityArrow);

        if (isInfinityArrow) {
            // Store infinity arrow count separately
            int infinityCount = persistentData.getInt(INFINITY_TAG);
            persistentData.putInt(INFINITY_TAG, infinityCount + 1);
            YioMod.debugLog("Stored infinity arrow for player {} (Total infinity: {})",
                player.getName().getString(), infinityCount + 1);
        } else {
            // Store regular arrow data
            ListTag arrowsList;
            if (!persistentData.contains(ARROWS_TAG)) {
                arrowsList = new ListTag();
                persistentData.put(ARROWS_TAG, arrowsList);
            } else {
                arrowsList = persistentData.getList(ARROWS_TAG, 10);
            }

            // Save the arrow's data including all components
            CompoundTag arrowData = new CompoundTag();
            arrow.saveWithoutId(arrowData);

            if (arrowData.contains("item")) {
                CompoundTag itemData = arrowData.getCompound("item");

                // Add to the beginning for chronological order
                arrowsList.add(0, itemData);

                // Limit to MAX_ARROWS
                while (arrowsList.size() > MAX_ARROWS) {
                    arrowsList.remove(arrowsList.size() - 1);
                }

                YioMod.debugLog("Stored regular arrow for player {}: {} (Total: {})",
                    player.getName().getString(), itemData, arrowsList.size());
            }
        }
    }

    private static boolean checkInfinityArrow(Arrow arrow) {
        YioMod.debugLog("Checking infinity arrow...");

        // Check if shooter is in creative mode
        if (arrow.getOwner() instanceof Player shooterPlayer) {
            YioMod.debugLog("Shooter: {}, Creative mode: {}", shooterPlayer.getName().getString(), shooterPlayer.isCreative());
            if (shooterPlayer.isCreative()) {
                YioMod.debugLog("Arrow from creative player - treating as infinity");
                return true;
            }
        }

        // Check if arrow has infinity data by looking at its item components
        CompoundTag arrowNBT = new CompoundTag();
        arrow.saveWithoutId(arrowNBT);
        YioMod.debugLog("Arrow NBT: {}", arrowNBT);

        // Check if the arrow item has intangible_projectile component (infinity marker)
        if (arrowNBT.contains("item")) {
            CompoundTag itemData = arrowNBT.getCompound("item");
            if (itemData.contains("components")) {
                CompoundTag components = itemData.getCompound("components");
                if (components.contains("minecraft:intangible_projectile")) {
                    YioMod.debugLog("Found intangible_projectile component - infinity arrow!");
                    return true;
                }
            }
        }

        // Look for infinity marker in arrow data
        if (arrowNBT.contains("infinity")) {
            boolean isInfinity = arrowNBT.getByte("infinity") == 1;
            YioMod.debugLog("Found infinity tag: {}", isInfinity);
            return isInfinity;
        }

        // Check if arrow pickup is disallowed (another infinity indicator)
        if (arrow.pickup == Arrow.Pickup.DISALLOWED) {
            YioMod.debugLog("Arrow pickup disallowed - likely infinity");
            return true;
        }

        return false;
    }

    public static ItemStack retrieveArrow(Player player) {
        CompoundTag persistentData = player.getPersistentData();

        // Check yankingHurts gamerule and deal damage if enabled
        boolean shouldHurt = false;
        try {
            if (YioModGameRules.YANKING_HURTS != null &&
                player.level().getGameRules().getBoolean(YioModGameRules.YANKING_HURTS)) {
                shouldHurt = true;
            }
        } catch (Exception e) {
            YioMod.debugLog("Failed to check yankingHurts gamerule: {}", e.getMessage());
        }

        // Check if we have infinity arrows to handle first
        int infinityCount = persistentData.getInt(INFINITY_TAG);
        if (infinityCount > 0) {
            // Reduce infinity arrow count
            persistentData.putInt(INFINITY_TAG, infinityCount - 1);

            // Deal damage if gamerule is enabled
            if (shouldHurt && !player.isCreative()) {
                player.hurt(YioDamageTypes.arrowYanking(player), 2.0f); // 1 heart = 2.0f damage
            }

            // Send crumbling message to player
            Component message = Component.translatable("yio.arrow.crumbled")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
            player.displayClientMessage(message, true);

            // Add crumbling particles around the player
            if (player.level() instanceof ServerLevel serverLevel) {
                // Spawn smoke particles around the player (like failed taming)
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    10, 0.5, 0.5, 0.5, 0.1);

                // Add some puff particles for extra effect
                serverLevel.sendParticles(ParticleTypes.POOF,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    5, 0.3, 0.3, 0.3, 0.05);
            }

            YioMod.debugLog("Infinity arrow crumbled for player {} (Remaining infinity: {})",
                player.getName().getString(), infinityCount - 1);

            // Return empty stack since infinity arrow crumbled
            return ItemStack.EMPTY;
        }

        // Handle regular arrows
        if (!persistentData.contains(ARROWS_TAG)) {
            return new ItemStack(Items.ARROW);
        }

        ListTag arrowsList = persistentData.getList(ARROWS_TAG, 10);
        if (arrowsList.isEmpty()) {
            return new ItemStack(Items.ARROW);
        }

        // Deal damage if gamerule is enabled (for regular arrows too)
        if (shouldHurt && !player.isCreative()) {
            player.hurt(YioDamageTypes.arrowYanking(player), 2.0f); // 1 heart = 2.0f damage
        }

        // Get the last arrow (chronologically oldest) and remove it
        CompoundTag itemTag = arrowsList.getCompound(arrowsList.size() - 1);
        arrowsList.remove(arrowsList.size() - 1);

        try {
            // Try to reconstruct the ItemStack from the saved NBT data
            ItemStack stack = ItemStack.parseOptional(player.registryAccess(), itemTag);
            if (!stack.isEmpty()) {
                YioMod.debugLog("Retrieved arrow for player {}: {} (Remaining: {})",
                    player.getName().getString(), stack, arrowsList.size());
                return stack;
            }
        } catch (Exception e) {
            YioMod.debugLog("Failed to parse stored arrow data, falling back to basic arrow detection");
        }

        // Fallback: determine arrow type from components
        if (itemTag.contains("components")) {
            YioMod.debugLog("Retrieved tipped arrow for player {} (Remaining: {})",
                player.getName().getString(), arrowsList.size());
            return new ItemStack(Items.TIPPED_ARROW);
        }

        YioMod.debugLog("Retrieved regular arrow for player {} (Remaining: {})",
            player.getName().getString(), arrowsList.size());
        return new ItemStack(Items.ARROW);
    }

    public static int getArrowCount(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        int regularArrows = 0;
        int infinityArrows = persistentData.getInt(INFINITY_TAG);

        if (persistentData.contains(ARROWS_TAG)) {
            ListTag arrowsList = persistentData.getList(ARROWS_TAG, 10);
            regularArrows = arrowsList.size();
        }

        return regularArrows + infinityArrows;
    }

    public static void clearPlayerArrows(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        persistentData.remove(ARROWS_TAG);
        persistentData.remove(INFINITY_TAG);
        YioMod.debugLog("Cleared stored arrows for player {}", player.getName().getString());
    }
}
