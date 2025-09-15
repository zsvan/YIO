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

@Mod.EventBusSubscriber
public class ArrowYank {
	@SubscribeEvent
	public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
		if (event.getHand() == InteractionHand.MAIN_HAND && event.getEntity().isShiftKeyDown()) {
			YioMod.PACKET_HANDLER.sendToServer(new RightClickEmptyPacket(event.getHand()));
		}
	}

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (event.getHand() == InteractionHand.MAIN_HAND && !event.getEntity().level().isClientSide() && event.getEntity().isShiftKeyDown()) {
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
		if (event.getHand() == InteractionHand.MAIN_HAND && !event.getEntity().level().isClientSide() && event.getEntity().isShiftKeyDown()) {
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
