package net.zsvan.yio;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;

@EventBusSubscriber(value = {Dist.CLIENT})
public class ArrowYank {
	@SubscribeEvent
	public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
		if (event.getHand() != InteractionHand.MAIN_HAND)
			return;
		if (!event.getEntity().isShiftKeyDown())
			return;

		Player player = event.getEntity();
		int vanillaArrowCount = player.getArrowCount();
		if (vanillaArrowCount > 0) {
			PacketDistributor.sendToServer(new RightClickEmptyPacket(event.getHand()));
		}
	}

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (event.getHand() == InteractionHand.MAIN_HAND && !event.getEntity().level().isClientSide() && event.getEntity().isShiftKeyDown()) {
			Player player = event.getEntity();
			int trackedArrowCount = ArrowTracker.getArrowCount(player);
			ItemStack mainHandItem = player.getMainHandItem();
			if ((mainHandItem.getItem() == Items.ARROW) && trackedArrowCount > 0) {
				player.swing(InteractionHand.MAIN_HAND, true);

				int vanillaArrowCount = player.getArrowCount();
				if (vanillaArrowCount > 0) {
					player.setArrowCount(vanillaArrowCount - 1);
				}

				ItemStack retrievedArrow = ArrowTracker.retrieveArrow(player);
				if (!retrievedArrow.isEmpty()) {
					ItemHandlerHelper.giveItemToPlayer(player, retrievedArrow);
				}
				// If empty, it was an infinity arrow that crumbled (message already shown)
			}
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getHand() == InteractionHand.MAIN_HAND && !event.getEntity().level().isClientSide() && event.getEntity().isShiftKeyDown()) {
			Player player = event.getEntity();
			int trackedArrowCount = ArrowTracker.getArrowCount(player);
			ItemStack mainHandItem = player.getMainHandItem();
			if (mainHandItem.isEmpty() && trackedArrowCount > 0) {
				player.swing(InteractionHand.MAIN_HAND, true);

				int vanillaArrowCount = player.getArrowCount();
				if (vanillaArrowCount > 0) {
					player.setArrowCount(vanillaArrowCount - 1);
				}

				ItemStack retrievedArrow = ArrowTracker.retrieveArrow(player);
				if (!retrievedArrow.isEmpty()) {
					ItemHandlerHelper.giveItemToPlayer(player, retrievedArrow);
				}
				// If empty, it was an infinity arrow that crumbled (message already shown)
			}
		}
	}
}
