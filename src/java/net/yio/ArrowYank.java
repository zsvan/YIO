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

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;

public class ArrowYank {
	@Mod.EventBusSubscriber
	public static class Trigger {
		@SubscribeEvent
		public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
			if (event.getHand() == InteractionHand.MAIN_HAND && event.getEntity().isShiftKeyDown()) {
				YioMod.PACKET_HANDLER.sendToServer(new RightClickEmptyPacket(event.getHand()));
			}
		}

		@SubscribeEvent
		public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
			if (event.getHand() == InteractionHand.MAIN_HAND && !event.getEntity().level.isClientSide() && event.getEntity().isShiftKeyDown()) {
				Player player = (Player) event.getEntity();
				int arrowCount = ((LivingEntity) player).getArrowCount();
				ItemStack mainHandItem = player.getMainHandItem();
				if ((mainHandItem.getItem() == Items.ARROW) && arrowCount > 0) {
					player.swing(InteractionHand.MAIN_HAND, true);
					((LivingEntity) player).setArrowCount(arrowCount - 1);
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.ARROW, 1));
				}
			}
		}

		@SubscribeEvent
		public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
			if (event.getHand() == InteractionHand.MAIN_HAND && !event.getEntity().level.isClientSide() && event.getEntity().isShiftKeyDown()) {
				Player player = (Player) event.getEntity();
				int arrowCount = ((LivingEntity) player).getArrowCount();
				ItemStack mainHandItem = player.getMainHandItem();
				if (mainHandItem.isEmpty() && arrowCount > 0) {
					player.swing(InteractionHand.MAIN_HAND, true);
					((LivingEntity) player).setArrowCount(arrowCount - 1);
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.ARROW, 1));
				}
			}
		}
	}
}
